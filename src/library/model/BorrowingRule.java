package library.model;

public class BorrowingRule {
    private String ruleId;
    private int maxBooks;
    private int maxBorrowDays;
    private double finePerDay;

    public BorrowingRule(String ruleId, int maxBooks, int maxBorrowDays, double finePerDay) {
        this.ruleId = ruleId;
        this.maxBooks = maxBooks;
        this.maxBorrowDays = maxBorrowDays;
        this.finePerDay = finePerDay;
    }

    public String getRuleId() {
        return ruleId;
    }

    public int getMaxBooks() {
        return maxBooks;
    }

    public int getMaxBorrowDays() {
        return maxBorrowDays;
    }

    public double getFinePerDay() {
        return finePerDay;
    }
}