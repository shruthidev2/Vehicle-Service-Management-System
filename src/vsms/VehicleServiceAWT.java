package vsms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class VehicleServiceAWT extends JFrame {

    // =========================================================
    // COLOR PALETTE (Professional Automobile Theme)
    // =========================================================
    private final Color DARK_BLUE = new Color(15, 23, 42);       // Slate 900
    private final Color ROYAL_BLUE = new Color(37, 99, 235);     // Blue 600
    private final Color LIGHT_BLUE = new Color(224, 242, 254);   // Blue 100
    private final Color ACCENT_BLUE = new Color(96, 165, 250);   // Blue 400
    
    private final Color BACKGROUND = new Color(248, 250, 252);   // Slate 50
    private final Color SIDEBAR_BG = new Color(30, 41, 59);      // Slate 800
    private final Color CARD_BG = Color.WHITE;
    
    private final Color TEXT_DARK = new Color(15, 23, 42);
    private final Color TEXT_MUTED = new Color(100, 116, 139);   // Slate 500
    private final Color TEXT_LIGHT = new Color(241, 245, 249);   // Slate 100
    
    private final Color SUCCESS = new Color(22, 163, 74);        // Green 600
    private final Color WARNING = new Color(217, 119, 6);        // Amber 600
    private final Color DANGER = new Color(220, 38, 38);         // Red 600

    // =========================================================
    // UI COMPONENTS
    // =========================================================
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    
    // Status & Feedback
    private JLabel statusLabel;
    private String selectedView = "dashboard";

    // Dashboard Cards (Metrics)
    private JLabel lblTotalCustomers;
    private JLabel lblTotalVehicles;
    private JLabel lblTotalServices;
    private JLabel lblCompletedServices;

    // Tables for data views
    private JTable customerTable;
    private JTable vehicleTable;
    private JTable serviceTable;
    private JTable historyTable;

    // Sidebar buttons for active state tracking
    private JButton btnDashboard, btnViewCust, btnAddCust, btnViewVeh, btnAddVeh, 
                    btnViewServ, btnAddServ, btnHistory, btnRefresh, btnReset, btnExit;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    public VehicleServiceAWT() {
        setTitle("Vehicle Service Management System");
        setSize(1250, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Initialize UI Sections
        initHeader();
        initSidebar();
        initMainContentArea();

        // Initial Load
        showDashboard();
        setVisible(true);
    }

    // =========================================================
    // HEADER
    // =========================================================
    private void initHeader() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, DARK_BLUE, getWidth(), 0, ROYAL_BLUE);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(1250, 80));
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(0, 25, 0, 25));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("🚗 VEHICLE SERVICE MANAGEMENT SYSTEM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Manage Customers • Vehicles • Services • Records");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_LIGHT);

        titlePanel.add(title);
        titlePanel.add(subtitle);
        header.add(titlePanel, BorderLayout.WEST);

        statusLabel = new JLabel("System Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        statusLabel.setForeground(ACCENT_BLUE);
        header.add(statusLabel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    // =========================================================
    // SIDEBAR MENU
    // =========================================================
    private void initSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(240, 700));
        sidebar.setLayout(new BorderLayout());

        JPanel menuList = new JPanel();
        menuList.setBackground(SIDEBAR_BG);
        menuList.setLayout(new GridLayout(11, 1, 0, 4));
        menuList.setBorder(new EmptyBorder(15, 10, 15, 10));

        btnDashboard = createSidebarButton("🏠  Dashboard");
        btnViewCust  = createSidebarButton("👤  View Customers");
        btnAddCust   = createSidebarButton("➕  Add Customer");
        btnViewVeh   = createSidebarButton("🚗  View Vehicles");
        btnAddVeh    = createSidebarButton("➕  Add Vehicle");
        btnViewServ  = createSidebarButton("🔧  Service Requests");
        btnAddServ   = createSidebarButton("➕  Add Service Req");
        btnHistory   = createSidebarButton("📋  Service History");
        btnRefresh   = createSidebarButton("🔄  Refresh Data");
        btnReset     = createSidebarButton("↩  Reset View");
        btnExit      = createSidebarButton("🚪  Exit System");

        // Action Listeners
        btnDashboard.addActionListener(e -> { showDashboard(); setActiveButton(btnDashboard); });
        btnViewCust.addActionListener(e -> { loadCustomers(); showCard("CustomersView"); setActiveButton(btnViewCust); });
        btnAddCust.addActionListener(e -> { openAddCustomerDialog(); });
        btnViewVeh.addActionListener(e -> { loadVehicles(); showCard("VehiclesView"); setActiveButton(btnViewVeh); });
        btnAddVeh.addActionListener(e -> { openAddVehicleDialog(); });
        btnViewServ.addActionListener(e -> { loadServiceRequests(); showCard("ServicesView"); setActiveButton(btnViewServ); });
        btnAddServ.addActionListener(e -> { openAddServiceDialog(); });
        btnHistory.addActionListener(e -> { loadServiceHistory(); showCard("HistoryView"); setActiveButton(btnHistory); });
        btnRefresh.addActionListener(e -> { refreshCurrentView(); });
        btnReset.addActionListener(e -> { showDashboard(); setActiveButton(btnDashboard); });
        btnExit.addActionListener(e -> dispose());

        menuList.add(btnDashboard);
        menuList.add(btnViewCust);
        menuList.add(btnAddCust);
        menuList.add(btnViewVeh);
        menuList.add(btnAddVeh);
        menuList.add(btnViewServ);
        menuList.add(btnAddServ);
        menuList.add(btnHistory);
        menuList.add(btnRefresh);
        menuList.add(btnReset);
        menuList.add(btnExit);

        sidebar.add(menuList, BorderLayout.CENTER);
        add(sidebar, BorderLayout.WEST);
        setActiveButton(btnDashboard);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(TEXT_LIGHT);
        btn.setBackground(SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn.getBackground() != ROYAL_BLUE) btn.setBackground(new Color(51, 65, 85));
            }
            public void mouseExited(MouseEvent e) {
                if (btn.getBackground() != ROYAL_BLUE) btn.setBackground(SIDEBAR_BG);
            }
        });
        return btn;
    }

    private void setActiveButton(JButton activeBtn) {
        Component[] comps = ((JPanel)((JPanel)btnDashboard.getParent())).getComponents();
        for (Component c : comps) {
            if (c instanceof JButton && c != btnRefresh && c != btnReset && c != btnExit && c != btnAddCust && c != btnAddVeh && c != btnAddServ) {
                c.setBackground(SIDEBAR_BG);
            }
        }
        activeBtn.setBackground(ROYAL_BLUE);
    }

    // =========================================================
    // MAIN CONTENT PANELS (CardLayout)
    // =========================================================
    private void initMainContentArea() {
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(BACKGROUND);

        mainContentPanel.add(createDashboardPanel(), "DashboardView");
        mainContentPanel.add(createTablePanel("Customer Management", customerTable = createStyledTable()), "CustomersView");
        mainContentPanel.add(createTablePanel("Vehicle Directory", vehicleTable = createStyledTable()), "VehiclesView");
        mainContentPanel.add(createTablePanel("Active Service Requests", serviceTable = createStyledTable()), "ServicesView");
        mainContentPanel.add(createTablePanel("Comprehensive Service History", historyTable = createStyledTable()), "HistoryView");

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void showCard(String cardName) {
        cardLayout.show(mainContentPanel, cardName);
        selectedView = cardName;
    }

    // =========================================================
    // DASHBOARD PANEL & METRICS
    // =========================================================
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel welcomeTitle = new JLabel("Welcome to VSMS Dashboard");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeTitle.setForeground(TEXT_DARK);
        panel.add(welcomeTitle, BorderLayout.NORTH);

        JPanel metricsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        metricsGrid.setOpaque(false);

        lblTotalCustomers = new JLabel("0", JLabel.CENTER);
        lblTotalVehicles = new JLabel("0", JLabel.CENTER);
        lblTotalServices = new JLabel("0", JLabel.CENTER);
        lblCompletedServices = new JLabel("0", JLabel.CENTER);

        metricsGrid.add(createMetricCard("Total Customers", lblTotalCustomers, ROYAL_BLUE));
        metricsGrid.add(createMetricCard("Total Vehicles", lblTotalVehicles, new Color(13, 148, 136))); // Teal
        metricsGrid.add(createMetricCard("Active Service Requests", lblTotalServices, WARNING));
        metricsGrid.add(createMetricCard("Completed Services", lblCompletedServices, SUCCESS));

        panel.add(metricsGrid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 6, 0, 0, accentColor),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(TEXT_DARK);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void showDashboard() {
        showCard("DashboardView");
        selectedView = "dashboard";
        updateDashboardMetrics();
        statusLabel.setText("Dashboard loaded successfully");
    }

    private void updateDashboardMetrics() {
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement()) {
            ResultSet rs1 = st.executeQuery("SELECT COUNT(*) FROM customers");
            if (rs1.next()) lblTotalCustomers.setText(String.valueOf(rs1.getInt(1)));

            ResultSet rs2 = st.executeQuery("SELECT COUNT(*) FROM vehicles");
            if (rs2.next()) lblTotalVehicles.setText(String.valueOf(rs2.getInt(1)));

            ResultSet rs3 = st.executeQuery("SELECT COUNT(*) FROM service_records");
            if (rs3.next()) lblTotalServices.setText(String.valueOf(rs3.getInt(1)));

            ResultSet rs4 = st.executeQuery("SELECT COUNT(*) FROM service_records WHERE service_status = 'Completed'");
            if (rs4.next()) lblCompletedServices.setText(String.valueOf(rs4.getInt(1)));
        } catch (SQLException e) {
            statusLabel.setText("Error loading metrics: " + e.getMessage());
        }
    }

    // =========================================================
    // TABLE WRAPPER PANEL
    // =========================================================
    private JPanel createTablePanel(String titleText, JTable table) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_DARK);
        panel.add(title, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JTable createStyledTable() {
        JTable table = new JTable();
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(TEXT_DARK);
        table.setSelectionBackground(new Color(191, 219, 254));
        return table;
    }

    // =========================================================
    // DATA LOADING METHODS (JDBC Integration)
    // =========================================================
    private void loadCustomers() {
        String[] columns = {"Customer ID", "Name", "Phone", "Email", "Address"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        String sql = "SELECT * FROM customers ORDER BY customer_id";

        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
            }
            customerTable.setModel(model);
            statusLabel.setText("Customer data loaded");
        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    private void loadVehicles() {
        String[] columns = {"Vehicle ID", "Vehicle Number", "Model", "Type", "Cust ID", "Owner Name"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        String sql = "SELECT v.vehicle_id, v.vehicle_number, v.vehicle_model, v.vehicle_type, c.customer_id, c.name FROM vehicles v JOIN customers c ON v.customer_id = c.customer_id ORDER BY v.vehicle_id";

        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5), rs.getString(6)});
            }
            vehicleTable.setModel(model);
            statusLabel.setText("Vehicle data loaded");
        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    private void loadServiceRequests() {
        String[] columns = {"Service ID", "Vehicle No", "Service Date", "Service Type", "Cost (Rs.)", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        String sql = "SELECT s.service_id, v.vehicle_number, s.service_date, s.service_type, s.service_cost, s.service_status FROM service_records s JOIN vehicles v ON s.vehicle_id = v.vehicle_id ORDER BY s.service_id";

        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getString(4), rs.getDouble(5), rs.getString(6)});
            }
            serviceTable.setModel(model);
            statusLabel.setText("Service requests loaded");
        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    private void loadServiceHistory() {
        String[] columns = {"Vehicle No", "Service ID", "Date", "Service Type", "Cost (Rs.)", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        String sql = "SELECT v.vehicle_number, s.service_id, s.service_date, s.service_type, s.service_cost, s.service_status FROM service_records s JOIN vehicles v ON s.vehicle_id = v.vehicle_id ORDER BY v.vehicle_number, s.service_date DESC";

        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getInt(2), rs.getDate(3), rs.getString(4), rs.getDouble(5), rs.getString(6)});
            }
            historyTable.setModel(model);
            statusLabel.setText("Service history loaded");
        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    // =========================================================
    // INPUT DIALOGS FOR ADDING DATA
    // =========================================================
    private void openAddCustomerDialog() {
        JDialog dialog = new JDialog(this, "Add New Customer", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.getRootPane().setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField addressField = new JTextField();

        dialog.add(new JLabel("Full Name:")); dialog.add(nameField);
        dialog.add(new JLabel("Phone:")); dialog.add(phoneField);
        dialog.add(new JLabel("Email:")); dialog.add(emailField);
        dialog.add(new JLabel("Address:")); dialog.add(addressField);

        JButton saveBtn = new JButton("Save Customer");
        saveBtn.setBackground(ROYAL_BLUE);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            String sql = "INSERT INTO customers (name, phone, email, address) VALUES (?, ?, ?, ?)";
            try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nameField.getText());
                ps.setString(2, phoneField.getText());
                ps.setString(3, emailField.getText());
                ps.setString(4, addressField.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Customer added successfully!");
                dialog.dispose();
                if (selectedView.equals("CustomersView")) loadCustomers();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(new JLabel()); dialog.add(saveBtn);
        dialog.setVisible(true);
    }

    private void openAddVehicleDialog() {
        JDialog dialog = new JDialog(this, "Add New Vehicle", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.getRootPane().setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField numField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField custIdField = new JTextField();

        dialog.add(new JLabel("Vehicle Number:")); dialog.add(numField);
        dialog.add(new JLabel("Vehicle Model:")); dialog.add(modelField);
        dialog.add(new JLabel("Vehicle Type:")); dialog.add(typeField);
        dialog.add(new JLabel("Customer ID:")); dialog.add(custIdField);

        JButton saveBtn = new JButton("Save Vehicle");
        saveBtn.setBackground(ROYAL_BLUE);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            String sql = "INSERT INTO vehicles (vehicle_number, vehicle_model, vehicle_type, customer_id) VALUES (?, ?, ?, ?)";
            try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, numField.getText());
                ps.setString(2, modelField.getText());
                ps.setString(3, typeField.getText());
                ps.setInt(4, Integer.parseInt(custIdField.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Vehicle added successfully!");
                dialog.dispose();
                if (selectedView.equals("VehiclesView")) loadVehicles();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Input/Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(new JLabel()); dialog.add(saveBtn);
        dialog.setVisible(true);
    }

    private void openAddServiceDialog() {
        JDialog dialog = new JDialog(this, "Add Service Request", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.getRootPane().setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextField vehIdField = new JTextField();
        JTextField dateField = new JTextField("YYYY-MM-DD");
        JTextField typeField = new JTextField();
        JTextField costField = new JTextField();
        JTextField statusField = new JTextField("Pending");

        dialog.add(new JLabel("Vehicle ID:")); dialog.add(vehIdField);
        dialog.add(new JLabel("Service Date:")); dialog.add(dateField);
        dialog.add(new JLabel("Service Type:")); dialog.add(typeField);
        dialog.add(new JLabel("Cost (Rs.):")); dialog.add(costField);
        dialog.add(new JLabel("Status:")); dialog.add(statusField);

        JButton saveBtn = new JButton("Save Service");
        saveBtn.setBackground(ROYAL_BLUE);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            String sql = "INSERT INTO service_records (vehicle_id, service_date, service_type, service_cost, service_status) VALUES (?, ?, ?, ?, ?)";
            try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, Integer.parseInt(vehIdField.getText()));
                ps.setDate(2, Date.valueOf(dateField.getText()));
                ps.setString(3, typeField.getText());
                ps.setDouble(4, Double.parseDouble(costField.getText()));
                ps.setString(5, statusField.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Service record added successfully!");
                dialog.dispose();
                if (selectedView.equals("ServicesView")) loadServiceRequests();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Input/Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(new JLabel()); dialog.add(saveBtn);
        dialog.setVisible(true);
    }

    // =========================================================
    // REFRESH & ERROR HANDLING
    // =========================================================
    private void refreshCurrentView() {
        switch (selectedView) {
            case "dashboard": updateDashboardMetrics(); break;
            case "CustomersView": loadCustomers(); break;
            case "VehiclesView": loadVehicles(); break;
            case "ServicesView": loadServiceRequests(); break;
            case "HistoryView": loadServiceHistory(); break;
        }
        statusLabel.setText("Data refreshed successfully");
    }

    private void showDatabaseError(SQLException e) {
        statusLabel.setText("Database error occurred");
        JOptionPane.showMessageDialog(this, "Unable to load information.\nDetails: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    // =========================================================
    // MAIN METHOD
    // =========================================================
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new VehicleServiceAWT());
    }
}