package library.service;

import library.enums.BookStatus;
import library.enums.FineStatus;
import library.enums.LoanStatus;
import library.enums.ReturnCondition;
import library.model.*;
import library.repository.LibraryRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BorrowReturnService {
    private final LibraryRepository repository;

    public BorrowReturnService(LibraryRepository repository) {
        this.repository = repository;
    }

    public String checkBorrowStatus(String readerCode) {
        Reader reader = repository.findReaderByReaderCode(readerCode);
        if (reader == null) {
            return "Độc giả không tồn tại.";
        }

        BorrowingRule rule = repository.getActiveRule();
        int remaining = rule.getMaxBooks() - reader.getCurrentBorrowedCount();

        StringBuilder sb = new StringBuilder();
        sb.append("===== TÌNH TRẠNG MƯỢN =====\n");
        sb.append("Độc giả: ").append(reader.getFullName()).append("\n");
        sb.append("Mã độc giả: ").append(reader.getReaderCode()).append("\n");
        sb.append("Đang mượn: ").append(reader.getCurrentBorrowedCount()).append(" cuốn\n");
        sb.append("Còn được mượn thêm: ").append(Math.max(remaining, 0)).append(" cuốn\n");
        sb.append("Số ngày mượn tối đa: ").append(rule.getMaxBorrowDays()).append("\n\n");

        sb.append("Danh sách sách đang mượn:\n");
        boolean found = false;

        for (LoanTicket loanTicket : repository.getLoanTickets().values()) {
            if (loanTicket.getReaderId().equals(reader.getReaderId())
                    && loanTicket.getStatus() == LoanStatus.BORROWING) {

                for (LoanDetail detail : repository.findLoanDetailsByLoanId(loanTicket.getLoanId())) {
                    if (!detail.isReturned()) {
                        BookItem book = repository.getBookItems().get(detail.getItemId());
                        if (book != null) {
                            sb.append("- ")
                              .append(book.getBarcode())
                              .append(" - ")
                              .append(book.getTitle())
                              .append(" - Hạn trả: ")
                              .append(loanTicket.getDueDate())
                              .append("\n");
                            found = true;
                        }
                    }
                }
            }
        }

        if (!found) {
            sb.append("- Không có\n");
        }

        return sb.toString();
    }

    public String createLoan(String readerCode, String librarianId, List<String> barcodes) {
        Reader reader = repository.findReaderByReaderCode(readerCode);
        if (reader == null) {
            return "Độc giả không tồn tại.";
        }

        Librarian librarian = repository.findLibrarianById(librarianId);
        if (librarian == null) {
            return "Thủ thư không tồn tại.";
        }

        List<String> cleanedBarcodes = new ArrayList<>();
        for (String barcode : barcodes) {
            if (barcode != null && !barcode.trim().isEmpty()) {
                cleanedBarcodes.add(barcode.trim());
            }
        }

        if (cleanedBarcodes.isEmpty()) {
            return "Bạn chưa nhập barcode sách.";
        }

        BorrowingRule rule = repository.getActiveRule();
        if (reader.getCurrentBorrowedCount() + cleanedBarcodes.size() > rule.getMaxBooks()) {
            return "Vượt hạn mức mượn sách.";
        }

        List<BookItem> booksToBorrow = new ArrayList<>();
        for (String barcode : cleanedBarcodes) {
            BookItem bookItem = repository.findBookByBarcode(barcode);
            if (bookItem == null) {
                return "Không tìm thấy sách với barcode: " + barcode;
            }
            if (bookItem.getStatus() != BookStatus.AVAILABLE) {
                return "Sách không sẵn sàng để mượn: " + bookItem.getTitle();
            }
            booksToBorrow.add(bookItem);
        }

        String loanId = repository.nextLoanId();
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(rule.getMaxBorrowDays());

        LoanTicket loanTicket = new LoanTicket(
                loanId,
                reader.getReaderId(),
                librarianId,
                borrowDate,
                dueDate,
                LoanStatus.BORROWING
        );

        repository.getLoanTickets().put(loanId, loanTicket);

        for (BookItem bookItem : booksToBorrow) {
            String loanDetailId = repository.nextLoanDetailId();
            LoanDetail detail = new LoanDetail(loanDetailId, loanId, bookItem.getItemId());
            repository.getLoanDetails().put(loanDetailId, detail);
            bookItem.setStatus(BookStatus.BORROWED);
        }

        reader.increaseBorrowedCount(booksToBorrow.size());

        saveLog(
                "CREATE_LOAN",
                "Thủ thư " + librarian.getFullName() +
                        " lập phiếu mượn " + loanId +
                        " cho độc giả " + reader.getFullName()
        );

        return "Lập phiếu mượn thành công.\nMã phiếu: " + loanId + "\nHạn trả: " + dueDate;
    }

    public String returnBook(String barcode, ReturnCondition condition) {
        BookItem bookItem = repository.findBookByBarcode(barcode);
        if (bookItem == null) {
            return "Không tìm thấy sách.";
        }

        LoanDetail loanDetail = repository.findBorrowingDetailByItemId(bookItem.getItemId());
        if (loanDetail == null) {
            return "Sách không có thông tin mượn hợp lệ.";
        }

        LoanTicket loanTicket = repository.getLoanTickets().get(loanDetail.getLoanId());
        if (loanTicket == null) {
            return "Không tìm thấy phiếu mượn.";
        }

        loanDetail.markReturned(LocalDate.now(), condition);

        double fineAmount = 0;
        String fineReason = null;

        long lateDays = ChronoUnit.DAYS.between(loanTicket.getDueDate(), LocalDate.now());
        if (lateDays > 0) {
            fineAmount = lateDays * repository.getActiveRule().getFinePerDay();
            fineReason = "Trả sách quá hạn " + lateDays + " ngày";
        }

        if (condition == ReturnCondition.NORMAL) {
            bookItem.setStatus(BookStatus.AVAILABLE);
        } else if (condition == ReturnCondition.DAMAGED) {
            bookItem.setStatus(BookStatus.DAMAGED);
            fineAmount = 50000;
            fineReason = "Sách bị hư hỏng";
        } else if (condition == ReturnCondition.LOST) {
            bookItem.setStatus(BookStatus.LOST);
            fineAmount = 200000;
            fineReason = "Làm mất sách";
        }

        Reader reader = repository.getReaders().get(loanTicket.getReaderId());
        if (reader != null) {
            reader.decreaseBorrowedCount(1);
        }

        if (allBooksReturned(loanTicket.getLoanId())) {
            loanTicket.setStatus(LoanStatus.COMPLETED);
        }

        if (fineAmount > 0) {
            Fine fine = new Fine(
                    repository.nextFineId(),
                    loanDetail.getLoanDetailId(),
                    fineAmount,
                    fineReason,
                    FineStatus.UNPAID
            );
            repository.getFines().put(fine.getFineId(), fine);
        }

        saveLog("RETURN_BOOK", "Trả sách barcode=" + barcode + ", condition=" + condition);

        if (fineAmount > 0) {
            return "Trả sách thành công.\nPhí phạt: " + fineAmount + " VNĐ\nLý do: " + fineReason;
        }
        return "Trả sách thành công. Không có phí phạt.";
    }

    public String getAllBooksInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== DANH SÁCH SÁCH =====\n");
        for (BookItem bookItem : repository.getBookItems().values()) {
            sb.append(bookItem.getBarcode())
              .append(" - ")
              .append(bookItem.getTitle())
              .append(" - ")
              .append(bookItem.getStatus())
              .append(" - ")
              .append(bookItem.getLocation())
              .append("\n");
        }
        return sb.toString();
    }

    public String getAllLoansInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== DANH SÁCH PHIẾU MƯỢN =====\n");
        for (LoanTicket loan : repository.getLoanTickets().values()) {
            sb.append("Phiếu: ").append(loan.getLoanId())
              .append(" | ReaderId: ").append(loan.getReaderId())
              .append(" | Borrow: ").append(loan.getBorrowDate())
              .append(" | Due: ").append(loan.getDueDate())
              .append(" | Status: ").append(loan.getStatus())
              .append("\n");
        }
        return sb.toString();
    }

    public String getAllFinesInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== DANH SÁCH PHÍ PHẠT =====\n");
        if (repository.getFines().isEmpty()) {
            sb.append("Không có phí phạt.\n");
            return sb.toString();
        }

        for (Fine fine : repository.getFines().values()) {
            sb.append(fine.getFineId())
              .append(" | ")
              .append(fine.getAmount())
              .append(" VNĐ | ")
              .append(fine.getReason())
              .append(" | ")
              .append(fine.getStatus())
              .append("\n");
        }
        return sb.toString();
    }

    public String getAllLogsInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== NHẬT KÝ HỆ THỐNG =====\n");
        for (ActivityLog log : repository.getLogs()) {
            sb.append(log.getLogId())
              .append(" | ")
              .append(log.getActionTime())
              .append(" | ")
              .append(log.getAction())
              .append(" | ")
              .append(log.getDescription())
              .append("\n");
        }
        return sb.toString();
    }

    private boolean allBooksReturned(String loanId) {
        for (LoanDetail detail : repository.findLoanDetailsByLoanId(loanId)) {
            if (!detail.isReturned()) {
                return false;
            }
        }
        return true;
    }

    private void saveLog(String action, String description) {
        ActivityLog log = new ActivityLog(
                repository.nextLogId(),
                action,
                LocalDateTime.now(),
                description
        );
        repository.getLogs().add(log);
    }
}