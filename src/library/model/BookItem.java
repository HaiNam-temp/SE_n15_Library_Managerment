package library.model;

import library.enums.BookStatus;

public class BookItem {
    private String itemId;
    private String barcode;
    private String title;
    private BookStatus status;
    private String location;

    public BookItem(String itemId, String barcode, String title, BookStatus status, String location) {
        this.itemId = itemId;
        this.barcode = barcode;
        this.title = title;
        this.status = status;
        this.location = location;
    }

    public String getItemId() {
        return itemId;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getTitle() {
        return title;
    }

    public BookStatus getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public boolean isAvailable(){
        return this.status == BookStatus.AVAILABLE;
    }

    @Deprecated
    public String ToString() {
        return "BookItem{" +
                "itemId='" + itemId + '\'' +
                ", barcode='" + barcode + '\'' +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
