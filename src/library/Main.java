package library;

import library.enums.BookStatus;
import library.model.BookItem;
import library.model.BorrowingRule;
import library.model.Librarian;
import library.model.Reader;
import library.repository.LibraryRepository;
import library.service.BorrowReturnService;
import library.ui.BorrowReturnFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        LibraryRepository repository = new LibraryRepository();

        repository.setActiveRule(new BorrowingRule("RULE001", 5, 7, 5000));

        Reader reader1 = new Reader(
                "R001",
                "DG001",
                "Nguyễn Văn A",
                "a@gmail.com",
                "0123456789",
                "079999999999",
                "Cà Mau"
        );

        Reader reader2 = new Reader(
                "R002",
                "DG002",
                "Trần Thị B",
                "b@gmail.com",
                "0988888888",
                "080000000000",
                "Bạc Liêu"
        );

        repository.getReaders().put(reader1.getReaderId(), reader1);
        repository.getReaders().put(reader2.getReaderId(), reader2);

        Librarian librarian = new Librarian(
                "LIB001",
                "Lê Thị Thủ Thư",
                "librarian@library.com"
        );

        repository.getLibrarians().put(librarian.getLibrarianId(), librarian);

        BookItem book1 = new BookItem("BI001", "BC001", "Lập trình Java", BookStatus.AVAILABLE, "Kệ A1");
        BookItem book2 = new BookItem("BI002", "BC002", "Cơ sở dữ liệu", BookStatus.AVAILABLE, "Kệ A2");
        BookItem book3 = new BookItem("BI003", "BC003", "Mạng máy tính", BookStatus.AVAILABLE, "Kệ A3");
        BookItem book4 = new BookItem("BI004", "BC004", "Hệ điều hành", BookStatus.AVAILABLE, "Kệ B1");
        BookItem book5 = new BookItem("BI005", "BC005", "Cấu trúc dữ liệu", BookStatus.AVAILABLE, "Kệ B2");

        repository.getBookItems().put(book1.getItemId(), book1);
        repository.getBookItems().put(book2.getItemId(), book2);
        repository.getBookItems().put(book3.getItemId(), book3);
        repository.getBookItems().put(book4.getItemId(), book4);
        repository.getBookItems().put(book5.getItemId(), book5);

        BorrowReturnService service = new BorrowReturnService(repository);

        SwingUtilities.invokeLater(() -> {
            BorrowReturnFrame frame = new BorrowReturnFrame(service);
            frame.setVisible(true);
        });
    }
}