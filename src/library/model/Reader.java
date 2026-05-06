package library.model;

public class Reader {
    private String readerId;
    private String readerCode;
    private String fullName;
    private String email;
    private String phone;
    private String citizenId;
    private String address;
    private int currentBorrowedCount;

    public Reader(String readerId, String readerCode, String fullName, String email, String phone, String citizenId, String address) {
        this.readerId = readerId;
        this.readerCode = readerCode;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.citizenId = citizenId;
        this.address = address;
        this.currentBorrowedCount = 0;
    }

    public String getReaderId() {
        return readerId;
    }

    public String getReaderCode() {
        return readerCode;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public String getAddress() {
        return address;
    }

    public int getCurrentBorrowedCount() {
        return currentBorrowedCount;
    }

    public void increaseBorrowedCount(int amount) {
        this.currentBorrowedCount += amount;
    }

    public void decreaseBorrowedCount(int amount){
        this.currentBorrowedCount -= amount;
        if(this.currentBorrowedCount < 0) {
            this.currentBorrowedCount = 0;
        }
    }

    @Override
    public String toString() {
        return "Reader{" +
                "readerId=" + readerId +
                ", readerCode='" + readerCode + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", citizenId='" + citizenId + '\'' +
                ", address='" + address + '\'' +
                ", currentBorrowedCount=" + currentBorrowedCount +
                '}';
    }
}
