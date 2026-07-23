import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Aplikasi Kasir POS Sederhana, Modern, Responsif & Minimalis menggunakan Java Swing
 */
public class Kasir_GUI extends JFrame {

    // Data Produk (Kategori, Nama Barang, Harga)
    private static class Produk {
        String kategori;
        String nama;
        double harga;

        public Produk(String kategori, String nama, double harga) {
            this.kategori = kategori;
            this.nama = nama;
            this.harga = harga;
        }

        @Override
        public String toString() {
            return "[" + kategori + "] " + nama;
        }
    }

    // Koleksi Produk Lengkap
    private final Produk[] daftarProduk = {
        // Makanan
        new Produk("Makanan", "Nasi Goreng Special", 18000),
        new Produk("Makanan", "Mie Ayam Bakso", 15000),
        new Produk("Makanan", "Ayam Geprek Sambal Matah", 20000),
        new Produk("Makanan", "Soto Ayam Kampung", 16000),
        new Produk("Makanan", "Sate Ayam (10 Tusuk)", 25000),
        new Produk("Makanan", "Nasi Uduk Komplit", 22000),
        new Produk("Makanan", "Kwetiau Goreng Sapi", 24000),
        new Produk("Makanan", "Bebek Goreng Kremes", 28000),
        new Produk("Makanan", "Nasi Rendang Padang", 25000),
        new Produk("Makanan", "Bakso Urat Jumbo", 18000),

        // Minuman
        new Produk("Minuman", "Es Teh Manis", 5000),
        new Produk("Minuman", "Es Jeruk Peras", 7000),
        new Produk("Minuman", "Kopi Susu Gula Aren", 12000),
        new Produk("Minuman", "Jus Alpukat", 15000),
        new Produk("Minuman", "Air Mineral 600ml", 4000),
        new Produk("Minuman", "Teh Tarik Boba", 14000),
        new Produk("Minuman", "Matcha Latte Ice", 16000),
        new Produk("Minuman", "Kopi Hitam Tubruk", 8000),
        new Produk("Minuman", "Es Cappuccino", 13000),

        // Camilan & Snack
        new Produk("Camilan", "Kentang Goreng (French Fries)", 12000),
        new Produk("Camilan", "Roti Bakar Cokelat Keju", 15000),
        new Produk("Camilan", "Cireng Rujak Crisp", 10000),
        new Produk("Camilan", "Pisang Goreng Keju", 12000),
        new Produk("Camilan", "Dimsum Ayam (4 Pcs)", 15000)
    };

    // Komponen GUI
    private JComboBox<Produk> cbProduk;
    private JTextField txtHarga;
    private JTextField txtJumlah;
    private JTable tableKeranjang;
    private DefaultTableModel tableModel;

    private JLabel lblTotalHarga;
    private JTextField txtUangBayar;
    private JLabel lblUangKembali;
    private JButton btnProsesBayar;
    private JButton btnHapusItem;

    private double totalBelanja = 0;
    private final NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    // Skema Warna UI Modern
    private final Color COLOR_PRIMARY = new Color(15, 23, 42);      // Slate 900
    private final Color COLOR_ACCENT = new Color(79, 70, 229);      // Indigo 600
    private final Color COLOR_SUCCESS = new Color(16, 185, 129);    // Emerald 500
    private final Color COLOR_DANGER = new Color(239, 68, 68);      // Red 500
    private final Color COLOR_BG = new Color(241, 245, 249);         // Slate 100
    private final Color COLOR_CARD = Color.WHITE;

    public Kasir_GUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        initUI();
    }

    private void initUI() {
        setTitle("Aplikasi Kasir POS - Toko Sederhana");
        setSize(980, 680);
        setMinimumSize(new Dimension(880, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(COLOR_BG);

        // --- HEADER ---
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(COLOR_PRIMARY);
        panelHeader.setBorder(new EmptyBorder(12, 25, 12, 25));

        JLabel lblTitle = new JLabel("APLIKASI KASIR POS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Sistem Penjualan & Kasir Minimalis");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(148, 163, 184));

        JPanel panelTitleGroup = new JPanel(new GridLayout(2, 1));
        panelTitleGroup.setOpaque(false);
        panelTitleGroup.add(lblTitle);
        panelTitleGroup.add(lblSubtitle);

        panelHeader.add(panelTitleGroup, BorderLayout.WEST);
        add(panelHeader, BorderLayout.NORTH);

        // --- BODY (MAIN CONTAINER) ---
        JPanel panelMain = new JPanel(new BorderLayout(15, 15));
        panelMain.setBackground(COLOR_BG);
        panelMain.setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. PANEL KIRI: FORM INPUT ITEM (RAPI & LEGA)
        panelMain.add(createFormPanel(), BorderLayout.WEST);

        // 2. PANEL TENGAH: TABEL KERANJANG BELANJA (STYLING MODERN)
        panelMain.add(createTablePanel(), BorderLayout.CENTER);

        add(panelMain, BorderLayout.CENTER);

        // --- PANEL SELATAN: SUMMARY & PEMBAYARAN RESPONSIF FIXED ---
        add(createPaymentPanel(), BorderLayout.SOUTH);

        // Inisialisasi awal harga
        updateHargaSelected();
    }

    private JPanel createFormPanel() {
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(COLOR_CARD);
        panelForm.setPreferredSize(new Dimension(350, 0)); // Diperluas agar tidak mepet
        panelForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(20, 22, 20, 22) // Padding dalam lega
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        Font fontHeader = new Font("Segoe UI", Font.BOLD, 16);
        Font fontLabel = new Font("Segoe UI", Font.BOLD, 12);
        Font fontInput = new Font("Segoe UI", Font.PLAIN, 13);

        // Judul Card
        JLabel lblCardTitle = new JLabel("Pilih Produk");
        lblCardTitle.setFont(fontHeader);
        lblCardTitle.setForeground(COLOR_PRIMARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 4, 12, 4);
        panelForm.add(lblCardTitle, gbc);

        gbc.insets = new Insets(4, 4, 4, 4);

        // Label Produk
        gbc.gridy = 1;
        JLabel lblProduk = new JLabel("Nama Produk:");
        lblProduk.setFont(fontLabel);
        panelForm.add(lblProduk, gbc);

        // Combo Box Produk
        cbProduk = new JComboBox<>(daftarProduk);
        cbProduk.setFont(fontInput);
        cbProduk.setBackground(Color.WHITE);
        cbProduk.setPreferredSize(new Dimension(0, 36));
        cbProduk.addActionListener(e -> updateHargaSelected());
        gbc.gridy = 2;
        gbc.insets = new Insets(2, 4, 10, 4);
        panelForm.add(cbProduk, gbc);

        // Label Harga
        gbc.gridy = 3;
        gbc.insets = new Insets(4, 4, 4, 4);
        JLabel lblHarga = new JLabel("Harga Satuan:");
        lblHarga.setFont(fontLabel);
        panelForm.add(lblHarga, gbc);

        // Input Harga (Disabled/Read-only)
        txtHarga = new JTextField();
        txtHarga.setFont(fontInput);
        txtHarga.setEditable(false);
        txtHarga.setPreferredSize(new Dimension(0, 36));
        txtHarga.setBackground(new Color(248, 250, 252));
        txtHarga.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(4, 10, 4, 10)
        ));
        gbc.gridy = 4;
        gbc.insets = new Insets(2, 4, 10, 4);
        panelForm.add(txtHarga, gbc);

        // Label Jumlah
        gbc.gridy = 5;
        gbc.insets = new Insets(4, 4, 4, 4);
        JLabel lblJumlah = new JLabel("Jumlah Beli:");
        lblJumlah.setFont(fontLabel);
        panelForm.add(lblJumlah, gbc);

        // TextField Jumlah (Angka Saja / INT)
        txtJumlah = new JTextField();
        txtJumlah.setFont(fontInput);
        txtJumlah.setPreferredSize(new Dimension(0, 36));
        txtJumlah.setToolTipText("Ketik jumlah beli (kosongkan untuk default 1)");
        txtJumlah.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(4, 10, 4, 10)
        ));
        txtJumlah.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtJumlah.selectAll();
            }
        });
        txtJumlah.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume(); // Hanya terima angka (int)
                }
            }
        });
        gbc.gridy = 6;
        gbc.insets = new Insets(2, 4, 15, 4);
        panelForm.add(txtJumlah, gbc);

        // Tombol Tambah ke Keranjang (Font Hitam)
        JButton btnTambah = new JButton("+ Tambah ke Keranjang");
        btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTambah.setBackground(new Color(165, 180, 252)); // Light Indigo
        btnTambah.setForeground(Color.BLACK);
        btnTambah.setFocusPainted(false);
        btnTambah.setPreferredSize(new Dimension(0, 40));
        btnTambah.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTambah.addActionListener(e -> tambahKeKeranjang());

        gbc.gridy = 7;
        gbc.insets = new Insets(10, 4, 5, 4);
        panelForm.add(btnTambah, gbc);

        return panelForm;
    }

    private JPanel createTablePanel() {
        JPanel panelTable = new JPanel(new BorderLayout(0, 10));
        panelTable.setBackground(COLOR_CARD);
        panelTable.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(18, 18, 18, 18)
        ));

        // Judul Card Tabel
        JLabel lblTableTitle = new JLabel("Daftar Keranjang Belanja");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTableTitle.setForeground(COLOR_PRIMARY);
        lblTableTitle.setBorder(new EmptyBorder(0, 2, 8, 0));
        panelTable.add(lblTableTitle, BorderLayout.NORTH);

        // Model Tabel
        String[] columnNames = {"No", "Nama Produk", "Harga Satuan", "Qty", "Subtotal"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableKeranjang = new JTable(tableModel);
        tableKeranjang.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableKeranjang.setRowHeight(32); // Baris lebih tinggi & lega
        tableKeranjang.setShowGrid(true);
        tableKeranjang.setGridColor(new Color(241, 245, 249));

        // Styling Header Tabel (Background Light Slate & Teks Gelap/Hitam)
        tableKeranjang.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableKeranjang.getTableHeader().setBackground(new Color(226, 232, 240)); // Background Abu-abu Slate Terang
        tableKeranjang.getTableHeader().setForeground(COLOR_PRIMARY); // Warna Font Gelap (Dark Slate)
        tableKeranjang.getTableHeader().setPreferredSize(new Dimension(0, 36));
        tableKeranjang.getTableHeader().setReorderingAllowed(false);

        tableKeranjang.setSelectionBackground(new Color(224, 231, 255));
        tableKeranjang.setSelectionForeground(COLOR_PRIMARY);

        // Atur Lebar Kolom
        tableKeranjang.getColumnModel().getColumn(0).setPreferredWidth(40);  // No
        tableKeranjang.getColumnModel().getColumn(1).setPreferredWidth(210); // Nama Produk
        tableKeranjang.getColumnModel().getColumn(2).setPreferredWidth(110); // Harga Satuan
        tableKeranjang.getColumnModel().getColumn(3).setPreferredWidth(50);  // Qty
        tableKeranjang.getColumnModel().getColumn(4).setPreferredWidth(125); // Subtotal

        // Custom Cell Renderer dengan Zebra Striping (Berselang-seling) & Alignment Presisi
        // Align Kiri (Nama Produk)
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.LEFT);
                if (!isSelected) setZebraBackground(this, row);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 8));
                return this;
            }
        };

        // Align Tengah (No & Qty)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) setZebraBackground(this, row);
                return this;
            }
        };

        // Align Kanan (Harga & Subtotal)
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                if (!isSelected) setZebraBackground(this, row);
                if (column == 4) setFont(new Font("Segoe UI", Font.BOLD, 13)); // Tebalkan Subtotal
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 10));
                return this;
            }
        };

        tableKeranjang.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableKeranjang.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        tableKeranjang.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tableKeranjang.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tableKeranjang.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(tableKeranjang);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel panelCenterContainer = new JPanel(new BorderLayout());
        panelCenterContainer.setOpaque(false);
        panelCenterContainer.add(scrollPane, BorderLayout.CENTER);

        // Toolbar aksi tabel (Hapus Item - Font Hitam)
        JPanel panelTableAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        panelTableAction.setOpaque(false);

        btnHapusItem = new JButton("Hapus Item Terpilih");
        btnHapusItem.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHapusItem.setBackground(new Color(252, 165, 165)); // Light Red
        btnHapusItem.setForeground(Color.BLACK);
        btnHapusItem.setFocusPainted(false);
        btnHapusItem.setPreferredSize(new Dimension(160, 32));
        btnHapusItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHapusItem.addActionListener(e -> hapusItemTerpilih());

        panelTableAction.add(btnHapusItem);
        panelCenterContainer.add(panelTableAction, BorderLayout.SOUTH);

        panelTable.add(panelCenterContainer, BorderLayout.CENTER);

        return panelTable;
    }

    private void setZebraBackground(JComponent comp, int row) {
        comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
        comp.setForeground(new Color(30, 41, 59));
    }

    private JPanel createPaymentPanel() {
        JPanel panelBottom = new JPanel(new GridBagLayout());
        panelBottom.setBackground(COLOR_CARD);
        panelBottom.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
                new EmptyBorder(12, 20, 12, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- BARIS 1: TOTAL BELANJA (BANER BESAR & RAPI) ---
        JPanel panelTotalBox = new JPanel(new BorderLayout());
        panelTotalBox.setBackground(new Color(238, 242, 255)); // Soft Indigo Tint
        panelTotalBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(199, 210, 254), 1),
                new EmptyBorder(8, 15, 8, 15)
        ));

        JLabel lblTotalTitle = new JLabel("TOTAL BELANJA :");
        lblTotalTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalTitle.setForeground(COLOR_PRIMARY);

        lblTotalHarga = new JLabel("Rp 0");
        lblTotalHarga.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotalHarga.setForeground(COLOR_ACCENT);

        panelTotalBox.add(lblTotalTitle, BorderLayout.WEST);
        panelTotalBox.add(lblTotalHarga, BorderLayout.EAST);

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        panelBottom.add(panelTotalBox, gbc);

        // --- BARIS 2: PEMBAYARAN & ACTION BUTTONS (BENTUK FIXED / TIDAK BERGESER) ---
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.weightx = 0;

        // Label & Textfield Uang Bayar (Ukuran Fixed & Angka Saja)
        JLabel lblUang = new JLabel("Uang Bayar (Rp):");
        lblUang.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 0;
        panelBottom.add(lblUang, gbc);

        txtUangBayar = new JTextField(12);
        txtUangBayar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtUangBayar.setPreferredSize(new Dimension(130, 32));
        txtUangBayar.setMinimumSize(new Dimension(130, 32));
        txtUangBayar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { hitungKembalianLive(); }
            public void removeUpdate(DocumentEvent e) { hitungKembalianLive(); }
            public void changedUpdate(DocumentEvent e) { hitungKembalianLive(); }
        });
        txtUangBayar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume(); // Hanya terima angka (int)
                }
            }
        });
        gbc.gridx = 1;
        panelBottom.add(txtUangBayar, gbc);

        // Box & Label Kembalian (Ukuran Fixed 240px)
        JPanel panelKembaliBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        panelKembaliBox.setOpaque(false);
        panelKembaliBox.setPreferredSize(new Dimension(240, 32));
        panelKembaliBox.setMinimumSize(new Dimension(240, 32));

        lblUangKembali = new JLabel("Kembalian: Rp 0");
        lblUangKembali.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUangKembali.setForeground(COLOR_PRIMARY);
        panelKembaliBox.add(lblUangKembali);

        gbc.gridx = 2;
        gbc.weightx = 1.0;
        panelBottom.add(panelKembaliBox, gbc);

        // Panel Tombol Kanan (Font Hitam - Ukuran Fixed)
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelButtons.setOpaque(false);

        btnProsesBayar = new JButton("Bayar & Cetak Struk");
        btnProsesBayar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnProsesBayar.setBackground(new Color(110, 231, 183)); // Light Emerald
        btnProsesBayar.setForeground(Color.BLACK);
        btnProsesBayar.setFocusPainted(false);
        btnProsesBayar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnProsesBayar.addActionListener(e -> prosesBayar());

        JButton btnReset = new JButton("Transaksi Baru");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReset.setBackground(new Color(203, 213, 225)); // Light Slate
        btnReset.setForeground(Color.BLACK);
        btnReset.setFocusPainted(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> resetTransaksi());

        panelButtons.add(btnProsesBayar);
        panelButtons.add(btnReset);

        gbc.gridx = 3;
        gbc.weightx = 0;
        panelBottom.add(panelButtons, gbc);

        return panelBottom;
    }

    private void updateHargaSelected() {
        Produk selected = (Produk) cbProduk.getSelectedItem();
        if (selected != null) {
            txtHarga.setText(formatRupiah.format(selected.harga));
        }
    }

    private void tambahKeKeranjang() {
        Produk produk = (Produk) cbProduk.getSelectedItem();
        if (produk == null) return;

        String strJumlah = txtJumlah.getText().trim();
        int jumlahBaru = 1;
        if (!strJumlah.isEmpty()) {
            try {
                jumlahBaru = Integer.parseInt(strJumlah);
                if (jumlahBaru <= 0) {
                    JOptionPane.showMessageDialog(this, "Jumlah beli minimal 1!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Masukkan angka jumlah beli yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        boolean barangSudahAda = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String namaDiTabel = (String) tableModel.getValueAt(i, 1);
            if (namaDiTabel.equals(produk.nama)) {
                int jumlahLama = (int) tableModel.getValueAt(i, 3);
                int jumlahTotal = jumlahLama + jumlahBaru;
                double subtotalBaru = produk.harga * jumlahTotal;

                tableModel.setValueAt(jumlahTotal, i, 3);
                tableModel.setValueAt(formatRupiah.format(subtotalBaru), i, 4);
                barangSudahAda = true;
                break;
            }
        }

        if (!barangSudahAda) {
            double subtotal = produk.harga * jumlahBaru;
            int noUrut = tableModel.getRowCount() + 1;
            tableModel.addRow(new Object[]{
                noUrut,
                produk.nama,
                formatRupiah.format(produk.harga),
                jumlahBaru,
                formatRupiah.format(subtotal)
            });
        }

        txtJumlah.setText("");
        hitungTotalBelanja();
    }

    private void hapusItemTerpilih() {
        int selectedRow = tableKeranjang.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item pada tabel keranjang yang ingin dihapus!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        tableModel.removeRow(selectedRow);

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(i + 1, i, 0);
        }

        hitungTotalBelanja();
    }

    private void hitungTotalBelanja() {
        totalBelanja = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String subtotalStr = (String) tableModel.getValueAt(i, 4);
            try {
                double subtotal = formatRupiah.parse(subtotalStr).doubleValue();
                totalBelanja += subtotal;
            } catch (Exception ignored) {}
        }

        lblTotalHarga.setText(formatRupiah.format(totalBelanja));
        hitungKembalianLive();
    }

    private void hitungKembalianLive() {
        String inputBayar = txtUangBayar.getText().trim();
        if (inputBayar.isEmpty()) {
            lblUangKembali.setText("Kembalian: Rp 0");
            lblUangKembali.setForeground(COLOR_PRIMARY);
            return;
        }

        try {
            String cleanInput = inputBayar.replaceAll("[^0-9]", "");
            if (cleanInput.isEmpty()) {
                lblUangKembali.setText("Kembalian: Rp 0");
                return;
            }

            double uangBayar = Double.parseDouble(cleanInput);
            double kembalian = uangBayar - totalBelanja;

            if (kembalian < 0) {
                lblUangKembali.setText("Uang Kurang: " + formatRupiah.format(Math.abs(kembalian)));
                lblUangKembali.setForeground(COLOR_DANGER);
            } else {
                lblUangKembali.setText("Kembalian: " + formatRupiah.format(kembalian));
                lblUangKembali.setForeground(COLOR_SUCCESS);
            }
        } catch (NumberFormatException e) {
            lblUangKembali.setText("Input Tidak Valid");
            lblUangKembali.setForeground(COLOR_DANGER);
        }
    }

    private void prosesBayar() {
        if (totalBelanja <= 0) {
            JOptionPane.showMessageDialog(this, "Keranjang belanja masih kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String inputBayar = txtUangBayar.getText().trim().replaceAll("[^0-9]", "");
        if (inputBayar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan nominal uang pembayaran!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double uangBayar = Double.parseDouble(inputBayar);
        if (uangBayar < totalBelanja) {
            JOptionPane.showMessageDialog(this, "Uang pembayaran masih kurang!", "Pembayaran Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double kembalian = uangBayar - totalBelanja;

        tampilkanStruk(uangBayar, kembalian);
        resetTransaksi();
    }

    private void tampilkanStruk(double uangBayar, double kembalian) {
        StringBuilder struk = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String tanggal = dtf.format(LocalDateTime.now());

        struk.append("==========================================\n");
        struk.append("              TOKO POS SEDERHANA          \n");
        struk.append("          Struk Pembayaran Penjualan      \n");
        struk.append("==========================================\n");
        struk.append("Tanggal : ").append(tanggal).append("\n");
        struk.append("------------------------------------------\n");
        struk.append(String.format("%-18s %-4s %-12s\n", "Item", "Qty", "Subtotal"));
        struk.append("------------------------------------------\n");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String nama = (String) tableModel.getValueAt(i, 1);
            int qty = (int) tableModel.getValueAt(i, 3);
            String subtotal = (String) tableModel.getValueAt(i, 4);

            if (nama.length() > 18) {
                nama = nama.substring(0, 15) + "...";
            }
            struk.append(String.format("%-18s %-4d %-12s\n", nama, qty, subtotal));
        }

        struk.append("------------------------------------------\n");
        struk.append(String.format("%-23s : %s\n", "TOTAL BELANJA", formatRupiah.format(totalBelanja)));
        struk.append(String.format("%-23s : %s\n", "UANG BAYAR", formatRupiah.format(uangBayar)));
        struk.append(String.format("%-23s : %s\n", "KEMBALIAN", formatRupiah.format(kembalian)));
        struk.append("==========================================\n");
        struk.append("      Terima Kasih Atas Kunjungan Anda!   \n");
        struk.append("==========================================\n");

        JTextArea textArea = new JTextArea(struk.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(360, 380));

        JOptionPane.showMessageDialog(this, scrollPane, "Struk Transaksi Selesai", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetTransaksi() {
        tableModel.setRowCount(0);
        totalBelanja = 0;
        lblTotalHarga.setText("Rp 0");
        lblUangKembali.setText("Kembalian: Rp 0");
        lblUangKembali.setForeground(COLOR_PRIMARY);
        txtUangBayar.setText("");
        txtJumlah.setText("");
        cbProduk.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Kasir_GUI().setVisible(true);
        });
    }
}
