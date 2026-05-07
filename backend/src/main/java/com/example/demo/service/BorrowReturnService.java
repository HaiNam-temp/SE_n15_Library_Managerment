package com.example.demo.service;

import com.example.demo.domain.ActivityLog;
import com.example.demo.domain.BookItem;
import com.example.demo.domain.BorrowingRule;
import com.example.demo.domain.Fine;
import com.example.demo.domain.Librarian;
import com.example.demo.domain.LoanDetail;
import com.example.demo.domain.LoanTicket;
import com.example.demo.domain.Reader;
import com.example.demo.dto.BorrowStatusDto;
import com.example.demo.dto.BorrowedBookDto;
import com.example.demo.dto.CreateLoanRequest;
import com.example.demo.dto.LoanResponse;
import com.example.demo.dto.ReturnBookRequest;
import com.example.demo.dto.ReturnBookResponse;
import com.example.demo.enums.BookItemStatus;
import com.example.demo.enums.FineStatus;
import com.example.demo.enums.LoanStatus;
import com.example.demo.enums.ReturnCondition;
import com.example.demo.repository.ActivityLogRepository;
import com.example.demo.repository.BookItemRepository;
import com.example.demo.repository.BorrowingRuleRepository;
import com.example.demo.repository.FineRepository;
import com.example.demo.repository.LibrarianRepository;
import com.example.demo.repository.LoanDetailRepository;
import com.example.demo.repository.LoanTicketRepository;
import com.example.demo.repository.ReaderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class BorrowReturnService extends BaseService {

    private static final BigDecimal DAMAGED_FINE_AMOUNT = BigDecimal.valueOf(50000);
    private static final BigDecimal LOST_FINE_AMOUNT = BigDecimal.valueOf(200000);

    private final ReaderRepository readerRepository;
    private final LibrarianRepository librarianRepository;
    private final BookItemRepository bookItemRepository;
    private final BorrowingRuleRepository borrowingRuleRepository;
    private final LoanTicketRepository loanTicketRepository;
    private final LoanDetailRepository loanDetailRepository;
    private final FineRepository fineRepository;
    private final ActivityLogRepository activityLogRepository;

    public BorrowReturnService(
            ReaderRepository readerRepository,
            LibrarianRepository librarianRepository,
            BookItemRepository bookItemRepository,
            BorrowingRuleRepository borrowingRuleRepository,
            LoanTicketRepository loanTicketRepository,
            LoanDetailRepository loanDetailRepository,
            FineRepository fineRepository,
            ActivityLogRepository activityLogRepository
    ) {
        this.readerRepository = readerRepository;
        this.librarianRepository = librarianRepository;
        this.bookItemRepository = bookItemRepository;
        this.borrowingRuleRepository = borrowingRuleRepository;
        this.loanTicketRepository = loanTicketRepository;
        this.loanDetailRepository = loanDetailRepository;
        this.fineRepository = fineRepository;
        this.activityLogRepository = activityLogRepository;
    }

    public BorrowStatusDto checkBorrowStatus(Long readerId) {
        Reader reader = findReader(readerId);
        BorrowingRule rule = findActiveRule();
        int borrowedCount = getBorrowedCount(reader);
        int remaining = rule.getMaxBooks() - borrowedCount;

        BorrowStatusDto borrowStatusDto = new BorrowStatusDto();
        borrowStatusDto.setReaderId(reader.getId());
        borrowStatusDto.setFullName(reader.getFullName());
        borrowStatusDto.setCurrentBorrowedCount(borrowedCount);
        borrowStatusDto.setRemainingBorrowCount(Math.max(remaining, 0));
        borrowStatusDto.setMaxBorrowDays(rule.getMaxBorrowDays());

        for (LoanTicket loanTicket : loanTicketRepository.findByReaderAndStatus(reader, LoanStatus.BORROWED)) {
            for (LoanDetail loanDetail : loanDetailRepository.findByLoanTicket(loanTicket)) {
                if (loanDetail.getActualReturnDate() == null && loanDetail.getBookItem() != null) {
                    borrowStatusDto.getBorrowedBooks().add(toBorrowedBookDto(loanDetail, loanTicket));
                }
            }
        }

        return borrowStatusDto;
    }

    @Transactional
    public LoanResponse createLoan(CreateLoanRequest request) {
        Reader reader = findReader(request.getReaderId());
        Librarian librarian = findLibrarian(request.getLibrarianId());
        BorrowingRule rule = findActiveRule();
        List<String> barcodes = cleanBarcodes(request.getBarcodes());

        if (barcodes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Barcode list is required");
        }

        int borrowedCount = getBorrowedCount(reader);
        if (borrowedCount + barcodes.size() > rule.getMaxBooks()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reader exceeds borrowing limit");
        }

        List<BookItem> bookItems = findAvailableBookItems(barcodes);

        LoanTicket loanTicket = new LoanTicket();
        loanTicket.setReader(reader);
        loanTicket.setBorrowDate(LocalDate.now());
        loanTicket.setDueDate(LocalDate.now().plusDays(rule.getMaxBorrowDays()));
        loanTicket.setStatus(LoanStatus.BORROWED);
        loanTicket = loanTicketRepository.save(loanTicket);

        for (BookItem bookItem : bookItems) {
            LoanDetail loanDetail = new LoanDetail();
            loanDetail.setLoanTicket(loanTicket);
            loanDetail.setBookItem(bookItem);
            loanDetailRepository.save(loanDetail);

            bookItem.setStatus(BookItemStatus.BORROWED);
            bookItemRepository.save(bookItem);
        }

        reader.setCurrentBorrowedCount(borrowedCount + bookItems.size());
        readerRepository.save(reader);

        saveLog(
                "CREATE_LOAN",
                "Librarian " + librarian.getFullName() + " created loan " + loanTicket.getLoanId()
                        + " for reader " + reader.getFullName(),
                librarian
        );

        return toLoanResponse(loanTicket, bookItems);
    }

    @Transactional
    public ReturnBookResponse returnBook(ReturnBookRequest request) {
        if (request.getBarcode() == null || request.getBarcode().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Barcode is required");
        }

        ReturnCondition condition = request.getCondition() == null ? ReturnCondition.NORMAL : request.getCondition();
        BookItem bookItem = bookItemRepository.findByBarcodeIgnoreCase(request.getBarcode().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book item not found"));
        LoanDetail loanDetail = loanDetailRepository.findByBookItemAndActualReturnDateIsNull(bookItem)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Borrowing detail not found"));
        LoanTicket loanTicket = loanDetail.getLoanTicket();
        Reader reader = loanTicket.getReader();

        loanDetail.setActualReturnDate(LocalDate.now());
        loanDetail.setReturnCondition(condition.name());
        loanDetailRepository.save(loanDetail);

        BigDecimal fineAmount = BigDecimal.ZERO;
        String fineReason = null;

        long lateDays = ChronoUnit.DAYS.between(loanTicket.getDueDate(), LocalDate.now());
        if (lateDays > 0) {
            fineAmount = findActiveRule().getFinePerDay().multiply(BigDecimal.valueOf(lateDays));
            fineReason = "Return overdue by " + lateDays + " days";
        }

        if (condition == ReturnCondition.NORMAL) {
            bookItem.setStatus(BookItemStatus.AVAILABLE);
        } else if (condition == ReturnCondition.DAMAGED) {
            bookItem.setStatus(BookItemStatus.MAINTENANCE);
            fineAmount = DAMAGED_FINE_AMOUNT;
            fineReason = "Book is damaged";
        } else if (condition == ReturnCondition.LOST) {
            bookItem.setStatus(BookItemStatus.LOST);
            fineAmount = LOST_FINE_AMOUNT;
            fineReason = "Book is lost";
        }
        bookItemRepository.save(bookItem);

        if (reader != null) {
            reader.setCurrentBorrowedCount(Math.max(getBorrowedCount(reader) - 1, 0));
            readerRepository.save(reader);
        }

        if (allBooksReturned(loanTicket)) {
            loanTicket.setStatus(LoanStatus.RETURNED);
            loanTicketRepository.save(loanTicket);
        }

        if (fineAmount.compareTo(BigDecimal.ZERO) > 0) {
            Fine fine = new Fine();
            fine.setLoanDetail(loanDetail);
            fine.setAmount(fineAmount);
            fine.setReason(fineReason);
            fine.setStatus(FineStatus.UNPAID);
            fineRepository.save(fine);
        }

        saveLog("RETURN_BOOK", "Returned book barcode=" + bookItem.getBarcode() + ", condition=" + condition, null);

        ReturnBookResponse response = new ReturnBookResponse();
        response.setMessage("Return book successfully");
        response.setFineAmount(fineAmount);
        response.setFineReason(fineReason);
        return response;
    }

    public List<LoanResponse> findAllLoans() {
        return loanTicketRepository.findAll()
                .stream()
                .map(loanTicket -> toLoanResponse(loanTicket, extractBookItems(loanTicket)))
                .toList();
    }

    private Reader findReader(Long readerId) {
        if (readerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reader id is required");
        }

        return readerRepository.findById(readerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reader not found"));
    }

    private Librarian findLibrarian(Long librarianId) {
        if (librarianId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Librarian id is required");
        }

        return librarianRepository.findById(librarianId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Librarian not found"));
    }

    private BorrowingRule findActiveRule() {
        return borrowingRuleRepository.findFirstByOrderByRuleIdAsc()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Borrowing rule not found"));
    }

    private List<String> cleanBarcodes(List<String> barcodes) {
        if (barcodes == null) {
            return List.of();
        }

        List<String> cleanedBarcodes = new ArrayList<>();
        for (String barcode : barcodes) {
            if (barcode != null && !barcode.trim().isEmpty()) {
                cleanedBarcodes.add(barcode.trim());
            }
        }
        return cleanedBarcodes;
    }

    private List<BookItem> findAvailableBookItems(List<String> barcodes) {
        List<BookItem> bookItems = new ArrayList<>();
        for (String barcode : barcodes) {
            BookItem bookItem = bookItemRepository.findByBarcodeIgnoreCase(barcode)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book item not found: " + barcode));
            if (bookItem.getStatus() != BookItemStatus.AVAILABLE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book item is not available: " + barcode);
            }
            bookItems.add(bookItem);
        }
        return bookItems;
    }

    private int getBorrowedCount(Reader reader) {
        if (reader.getCurrentBorrowedCount() == null) {
            return 0;
        }
        return reader.getCurrentBorrowedCount();
    }

    private boolean allBooksReturned(LoanTicket loanTicket) {
        return loanDetailRepository.findByLoanTicket(loanTicket)
                .stream()
                .allMatch(loanDetail -> loanDetail.getActualReturnDate() != null);
    }

    private BorrowedBookDto toBorrowedBookDto(LoanDetail loanDetail, LoanTicket loanTicket) {
        BorrowedBookDto borrowedBookDto = new BorrowedBookDto();
        BookItem bookItem = loanDetail.getBookItem();
        borrowedBookDto.setBarcode(bookItem.getBarcode());
        if (bookItem.getBook() != null) {
            borrowedBookDto.setTitle(bookItem.getBook().getTitle());
        }
        borrowedBookDto.setDueDate(loanTicket.getDueDate());
        return borrowedBookDto;
    }

    private LoanResponse toLoanResponse(LoanTicket loanTicket, List<BookItem> bookItems) {
        LoanResponse loanResponse = new LoanResponse();
        loanResponse.setLoanId(loanTicket.getLoanId());
        if (loanTicket.getReader() != null) {
            loanResponse.setReaderId(loanTicket.getReader().getId());
        }
        loanResponse.setBorrowDate(loanTicket.getBorrowDate());
        loanResponse.setDueDate(loanTicket.getDueDate());
        loanResponse.setStatus(loanTicket.getStatus());
        loanResponse.setBarcodes(
                bookItems.stream()
                        .map(BookItem::getBarcode)
                        .toList()
        );
        return loanResponse;
    }

    private List<BookItem> extractBookItems(LoanTicket loanTicket) {
        return loanDetailRepository.findByLoanTicket(loanTicket)
                .stream()
                .map(LoanDetail::getBookItem)
                .toList();
    }

    private void saveLog(String action, String description, Librarian librarian) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setAction(action);
        activityLog.setActionTime(LocalDateTime.now());
        activityLog.setDescription(description);
        if (librarian != null) {
            activityLog.setAccount(librarian.getAccount());
        }
        activityLogRepository.save(activityLog);
    }
}
