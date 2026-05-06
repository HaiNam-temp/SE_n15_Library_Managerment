package library.model;

import library.enums.FineStatus;

public class Fine {
    private String fineId;
    private String loanDetailId;
    private double amount;
    private String reason;
    private FineStatus status;

    public Fine(String fineId, String loanDetailId, double amount, String reason, FineStatus status) {
        this.fineId = fineId;
        this.loanDetailId = loanDetailId;
        this.amount = amount;
        this.reason = reason;
        this.status = status;
    }

    public String getFineId() {
        return fineId;
    }

    public String getLoanDetailId() {
        return loanDetailId;
    }

    public double getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public FineStatus getStatus() {
        return status;
    }

    public void setStatus(FineStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Fine{" +
                "fineId='" + fineId + '\'' +
                ", loanDetailId='" + loanDetailId + '\'' +
                ", amount=" + amount +
                ", reason='" + reason + '\'' +
                ", status=" + status +
                '}';
    }
}