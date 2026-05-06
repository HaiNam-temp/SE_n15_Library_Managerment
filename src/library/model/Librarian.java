package library.model;

public class Librarian {
    private String librarianId;
    private String fullName;
    private String contactInfo;

    public Librarian(String librarianId, String fullName, String contactInfo) {
        this.librarianId = librarianId;
        this.fullName = fullName;
        this.contactInfo = contactInfo;
    }

    public String getLibrarianId() {
        return librarianId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    @Override
    public String toString() {
        return "Librarian{" +
                "librarianId='" + librarianId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                '}';
    }
}
