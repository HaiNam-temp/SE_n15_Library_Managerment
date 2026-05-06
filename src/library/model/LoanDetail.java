package library.model;

import library.enums.ReturnCondition;

import java.time.LocalDate;

public class LoanDetail {
    private String loanDetailId;
    private String loanId;
    private String itemId;
    private LocalDate actualReturnDate;
    private ReturnCondition returnCondition;

    public LoanDetail(String loanDetailId, String loanId, String itemId) {
        this.loanDetailId = loanDetailId;
        this.loanId = loanId;
        this.itemId = itemId;
    }

    public String getLoanDetailId() {
        return loanDetailId;
    }

    public String getLoanId() {
        return loanId;
    }

    public String getItemId() {
        return itemId;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public ReturnCondition getReturnCondition() {
        return returnCondition;
    }

    public boolean isReturned() {
        return actualReturnDate != null;
    }

    public void markReturned(LocalDate actualReturnDate, ReturnCondition returnCondition) {
        this.actualReturnDate = actualReturnDate;
        this.returnCondition = returnCondition;
    }

    @Override
    public String toString() {
        return "LoanDetail{" +
                "loanDetailId='" + loanDetailId + '\'' +
                ", loanId='" + loanId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", actualReturnDate=" + actualReturnDate +
                ", returnCondition=" + returnCondition +
                '}';
    }
}