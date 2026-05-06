package library.ui;

import library.enums.ReturnCondition;
import library.service.BorrowReturnService;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class BorrowReturnFrame extends JFrame {
    private final BorrowReturnService service;

    private JTextField txtReaderCode;
    private JTextField txtLibrarianId;
    private JTextField txtBarcodes;
    private JTextField txtReturnBarcode;
    private JComboBox<ReturnCondition> cbReturnCondition;
    private JTextArea txtOutput;

    public BorrowReturnFrame(BorrowReturnService service) {
        this.service = service;

        setTitle("Quản lý mượn / trả sách");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        formPanel.add(new JLabel("Mã độc giả:"));
        txtReaderCode = new JTextField();
        formPanel.add(txtReaderCode);

        formPanel.add(new JLabel("Mã thủ thư:"));
        txtLibrarianId = new JTextField("LIB001");
        formPanel.add(txtLibrarianId);

        formPanel.add(new JLabel("Barcode mượn (cách nhau dấu phẩy):"));
        txtBarcodes = new JTextField();
        formPanel.add(txtBarcodes);

        formPanel.add(new JLabel("Barcode trả:"));
        txtReturnBarcode = new JTextField();
        formPanel.add(txtReturnBarcode);

        formPanel.add(new JLabel("Tình trạng trả:"));
        cbReturnCondition = new JComboBox<>(ReturnCondition.values());
        formPanel.add(cbReturnCondition);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton btnCheck = new JButton("Kiểm tra tình trạng mượn");
        JButton btnBorrow = new JButton("Lập phiếu mượn");
        JButton btnReturn = new JButton("Trả sách");
        JButton btnBooks = new JButton("Xem danh sách sách");
        JButton btnLoans = new JButton("Xem phiếu mượn");
        JButton btnFines = new JButton("Xem phí phạt");
        JButton btnLogs = new JButton("Xem nhật ký");

        buttonPanel.add(btnCheck);
        buttonPanel.add(btnBorrow);
        buttonPanel.add(btnReturn);
        buttonPanel.add(btnBooks);
        buttonPanel.add(btnLoans);
        buttonPanel.add(btnFines);
        buttonPanel.add(btnLogs);

        txtOutput = new JTextArea();
        txtOutput.setEditable(false);
        txtOutput.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(txtOutput);
        scrollPane.setPreferredSize(new Dimension(850, 400));

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);

        btnCheck.addActionListener(e -> handleCheck());
        btnBorrow.addActionListener(e -> handleBorrow());
        btnReturn.addActionListener(e -> handleReturn());
        btnBooks.addActionListener(e -> txtOutput.setText(service.getAllBooksInfo()));
        btnLoans.addActionListener(e -> txtOutput.setText(service.getAllLoansInfo()));
        btnFines.addActionListener(e -> txtOutput.setText(service.getAllFinesInfo()));
        btnLogs.addActionListener(e -> txtOutput.setText(service.getAllLogsInfo()));
    }

    private void handleCheck() {
        String result = service.checkBorrowStatus(txtReaderCode.getText().trim());
        txtOutput.setText(result);
    }

    private void handleBorrow() {
        List<String> barcodes = Arrays.asList(txtBarcodes.getText().split(","));
        String result = service.createLoan(
                txtReaderCode.getText().trim(),
                txtLibrarianId.getText().trim(),
                barcodes
        );
        txtOutput.setText(result);
    }

    private void handleReturn() {
        String result = service.returnBook(
                txtReturnBarcode.getText().trim(),
                (ReturnCondition) cbReturnCondition.getSelectedItem()
        );
        txtOutput.setText(result);
    }
}