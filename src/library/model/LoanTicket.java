package library.model;

import library.enums.LoanStatus;

import java.time.LocalDate;

public class LoanTicket {
    private String loanId;
    private String readerId;
    private String librarianId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LoanStatus status;

    public LoanTicket(String loanId, String readerId, String librarianId,
                      LocalDate borrowDate, LocalDate dueDate, LoanStatus status) {
        this.loanId = loanId;
        this.readerId = readerId;
        this.librarianId = librarianId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    public String getLoanId() {
        return loanId;
    }

    public String getReaderId() {
        return readerId;
    }

    public String getLibrarianId() {
        return librarianId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LoanTicket{" +
                "loanId='" + loanId + '\'' +
                ", readerId='" + readerId + '\'' +
                ", librarianId='" + librarianId + '\'' +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", status=" + status +
                '}';
    }
}
