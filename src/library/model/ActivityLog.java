package library.model;

import java.time.LocalDateTime;

public class ActivityLog {
    private String logId;
    private String action;
    private LocalDateTime actionTime;
    private String description;

    public ActivityLog(String logId, String action, LocalDateTime actionTime, String description) {
        this.logId = logId;
        this.action = action;
        this.actionTime = actionTime;
        this.description = description;
    }

    public String getLogId() {
        return logId;
    }

    public String getAction() {
        return action;
    }

    public LocalDateTime getActionTime() {
        return actionTime;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "ActivityLog{" +
                "logId='" + logId + '\'' +
                ", action='" + action + '\'' +
                ", actionTime=" + actionTime +
                ", description='" + description + '\'' +
                '}';
    }
}