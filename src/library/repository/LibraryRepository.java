package library.repository;

import library.model.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LibraryRepository {
    private final Map<String, Reader> readers = new HashMap<>();
    private final Map<String, Librarian> librarians = new HashMap<>();
    private final Map<String, BookItem> bookItems = new HashMap<>();
    private final Map<String, LoanTicket> loanTickets = new HashMap<>();
    private final Map<String, LoanDetail> loanDetails = new HashMap<>();
    private final Map<String, Fine> fines = new HashMap<>();
    private final List<ActivityLog> logs = new ArrayList<>();

    private BorrowingRule activeRule;

    private final AtomicInteger loanCounter = new AtomicInteger(1);
    private final AtomicInteger loanDetailCounter = new AtomicInteger(1);
    private final AtomicInteger fineCounter = new AtomicInteger(1);
    private final AtomicInteger logCounter = new AtomicInteger(1);

    public Map<String, Reader> getReaders() {
        return readers;
    }

    public Map<String, Librarian> getLibrarians() {
        return librarians;
    }

    public Map<String, BookItem> getBookItems() {
        return bookItems;
    }

    public Map<String, LoanTicket> getLoanTickets() {
        return loanTickets;
    }

    public Map<String, LoanDetail> getLoanDetails() {
        return loanDetails;
    }

    public Map<String, Fine> getFines() {
        return fines;
    }

    public List<ActivityLog> getLogs() {
        return logs;
    }

    public BorrowingRule getActiveRule() {
        return activeRule;
    }

    public void setActiveRule(BorrowingRule activeRule) {
        this.activeRule = activeRule;
    }

    public String nextLoanId() {
        return String.format("LOAN%03d", loanCounter.getAndIncrement());
    }

    public String nextLoanDetailId() {
        return String.format("LD%03d", loanDetailCounter.getAndIncrement());
    }

    public String nextFineId() {
        return String.format("FINE%03d", fineCounter.getAndIncrement());
    }

    public String nextLogId() {
        return String.format("LOG%03d", logCounter.getAndIncrement());
    }

    public Reader findReaderByReaderCode(String readerCode) {
        return readers.values()
                .stream()
                .filter(r -> r.getReaderCode().equalsIgnoreCase(readerCode))
                .findFirst()
                .orElse(null);
    }

    public Librarian findLibrarianById(String librarianId) {
        return librarians.get(librarianId);
    }

    public BookItem findBookByBarcode(String barcode) {
        return bookItems.values()
                .stream()
                .filter(b -> b.getBarcode().equalsIgnoreCase(barcode))
                .findFirst()
                .orElse(null);
    }

    public LoanDetail findBorrowingDetailByItemId(String itemId) {
        for (LoanDetail detail : loanDetails.values()) {
            if (detail.getItemId().equals(itemId) && !detail.isReturned()) {
                return detail;
            }
        }
        return null;
    }

    public List<LoanDetail> findLoanDetailsByLoanId(String loanId) {
        List<LoanDetail> result = new ArrayList<>();
        for (LoanDetail detail : loanDetails.values()) {
            if (detail.getLoanId().equals(loanId)) {
                result.add(detail);
            }
        }
        return result;
    }
}