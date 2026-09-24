package vsms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class VehicleServiceSystem extends JFrame {

    // ================= COLORS =================
    private final Color NAVY = new Color(25, 35, 55);
    private final Color BLUE = new Color(0, 123, 255);
    private final Color SKY = new Color(235, 245, 255);
    private final Color WHITE = Color.WHITE;
    private final Color TEXT = new Color(40, 40, 40);
    private final Color GREEN = new Color(40, 167, 69);
    private final Color ORANGE = new Color(255, 153, 51);

    // ================= MAIN COMPONENTS =================
    private JPanel contentPanel;
    private CardLayout cardLayout;

    private JLabel customerCount;
    private JLabel vehicleCount;
    private JLabel serviceCount;
    private JLabel completedCount;

    private JTable customerTable;
    private JTable vehicleTable;
    private JTable serviceTable;
    private JTable historyTable;

    // ================= CONSTRUCTOR =================
    public VehicleServiceSystem() {

        setTitle("Vehicle Service System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        createTopPanel();
        createMenuPanel();
        createContentPanel();

        showDashboard();

        setVisible(true);
    }

    // ================= TOP PANEL =================
    private void createTopPanel() {

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(NAVY);
        top.setPreferredSize(new Dimension(1200, 75));
        top.setBorder(new EmptyBorder(10, 25, 10, 25));

        JLabel title = new JLabel("VEHICLE SERVICE CENTER");
        title.setForeground(WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 25));

        JLabel subtitle = new JLabel("Vehicle Service Management System");
        subtitle.setForeground(new Color(200, 220, 240));
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);

        textPanel.add(title);
        textPanel.add(subtitle);

        top.add(textPanel, BorderLayout.WEST);

        JLabel admin = new JLabel("ADMIN PANEL");
        admin.setForeground(WHITE);
        admin.setFont(new Font("Arial", Font.BOLD, 14));

        top.add(admin, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
    }

    // ================= LEFT MENU =================
    private void createMenuPanel() {

        JPanel menu = new JPanel();
        menu.setBackground(new Color(245, 247, 250));
        menu.setPreferredSize(new Dimension(210, 600));
        menu.setLayout(new GridLayout(9, 1, 5, 5));
        menu.setBorder(new EmptyBorder(20, 12, 20, 12));

        JButton dashboard = createButton("Dashboard");
        JButton customers = createButton("Customers");
        JButton vehicles = createButton("Vehicles");
        JButton services = createButton("Service Requests");
        JButton history = createButton("Service History");
        JButton addCustomer = createButton("Add Customer");
        JButton addVehicle = createButton("Add Vehicle");
        JButton addService = createButton("Add Service");
        JButton exit = createButton("Exit");

        dashboard.addActionListener(e -> showDashboard());

        customers.addActionListener(e -> {
            loadCustomers();
            cardLayout.show(contentPanel, "customers");
        });

        vehicles.addActionListener(e -> {
            loadVehicles();
            cardLayout.show(contentPanel, "vehicles");
        });

        services.addActionListener(e -> {
            loadServices();
            cardLayout.show(contentPanel, "services");
        });

        history.addActionListener(e -> {
            loadHistory();
            cardLayout.show(contentPanel, "history");
        });

        addCustomer.addActionListener(e -> addCustomer());

        addVehicle.addActionListener(e -> addVehicle());

        addService.addActionListener(e -> addService());

        exit.addActionListener(e -> System.exit(0));

        menu.add(dashboard);
        menu.add(customers);
        menu.add(vehicles);
        menu.add(services);
        menu.add(history);
        menu.add(addCustomer);
        menu.add(addVehicle);
        menu.add(addService);
        menu.add(exit);

        add(menu, BorderLayout.WEST);
    }

    // ================= MENU BUTTON =================
    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(WHITE);
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(
                new Color(220, 225, 230)
        ));

        return button;
    }

    // ================= CONTENT PANEL =================
    private void createContentPanel() {

        cardLayout = new CardLayout();

        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(SKY);

        contentPanel.add(createDashboard(), "dashboard");

        customerTable = createTable();
        contentPanel.add(createTablePanel(
                "Customer Details", customerTable), "customers");

        vehicleTable = createTable();
        contentPanel.add(createTablePanel(
                "Vehicle Details", vehicleTable), "vehicles");

        serviceTable = createTable();
        contentPanel.add(createTablePanel(
                "Service Requests", serviceTable), "services");

        historyTable = createTable();
        contentPanel.add(createTablePanel(
                "Service History", historyTable), "history");

        add(contentPanel, BorderLayout.CENTER);
    }

    // ================= DASHBOARD =================
    private JPanel createDashboard() {

        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(SKY);
        main.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel heading = new JLabel("Dashboard Overview");
        heading.setFont(new Font("Arial", Font.BOLD, 28));
        heading.setForeground(NAVY);

        main.add(heading, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 4, 20, 20));
        cards.setOpaque(false);

        customerCount = new JLabel("0", SwingConstants.CENTER);
        vehicleCount = new JLabel("0", SwingConstants.CENTER);
        serviceCount = new JLabel("0", SwingConstants.CENTER);
        completedCount = new JLabel("0", SwingConstants.CENTER);

        cards.add(createCard(
                "CUSTOMERS",
                customerCount,
                BLUE));

        cards.add(createCard(
                "VEHICLES",
                vehicleCount,
                new Color(0, 150, 136)));

        cards.add(createCard(
                "SERVICES",
                serviceCount,
                ORANGE));

        cards.add(createCard(
                "COMPLETED",
                completedCount,
                GREEN));

        main.add(cards, BorderLayout.CENTER);

        return main;
    }

    // ================= DASHBOARD CARD =================
    private JPanel createCard(
            String title,
            JLabel value,
            Color color) {

        JPanel panel = new JPanel(new BorderLayout());

        panel.setBackground(WHITE);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        5, 0, 0, 0, color),
                BorderFactory.createEmptyBorder(
                        20, 15, 20, 15)
        ));

        JLabel titleLabel = new JLabel(
                title,
                SwingConstants.CENTER);

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 14));

        titleLabel.setForeground(
                new Color(100, 100, 100));

        value.setFont(
                new Font("Arial", Font.BOLD, 38));

        value.setForeground(color);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(value, BorderLayout.CENTER);

        return panel;
    }

    // ================= TABLE =================
    private JTable createTable() {

        JTable table = new JTable();

        table.setRowHeight(30);

        table.setFont(
                new Font("Arial", Font.PLAIN, 13));

        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13));

        table.getTableHeader().setBackground(NAVY);
        table.getTableHeader().setForeground(WHITE);

        return table;
    }

    // ================= TABLE PANEL =================
    private JPanel createTablePanel(
            String title,
            JTable table) {

        JPanel panel = new JPanel(
                new BorderLayout(15, 15));

        panel.setBackground(SKY);

        panel.setBorder(
                new EmptyBorder(25, 25, 25, 25));

        JLabel heading = new JLabel(title);

        heading.setFont(
                new Font("Arial", Font.BOLD, 24));

        heading.setForeground(NAVY);

        panel.add(heading, BorderLayout.NORTH);

        JScrollPane scrollPane =
                new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ================= DASHBOARD DATA =================
    private void showDashboard() {

        cardLayout.show(contentPanel, "dashboard");

        try {

            Connection con =
                    DBConnection.getConnection();

            Statement st =
                    con.createStatement();

            ResultSet rs;

            rs = st.executeQuery(
                    "SELECT COUNT(*) FROM customers");

            if (rs.next())
                customerCount.setText(
                        rs.getString(1));

            rs = st.executeQuery(
                    "SELECT COUNT(*) FROM vehicles");

            if (rs.next())
                vehicleCount.setText(
                        rs.getString(1));

            rs = st.executeQuery(
                    "SELECT COUNT(*) FROM service_records");

            if (rs.next())
                serviceCount.setText(
                        rs.getString(1));

            rs = st.executeQuery(
                    "SELECT COUNT(*) FROM service_records " +
                    "WHERE service_status='Completed'");

            if (rs.next())
                completedCount.setText(
                        rs.getString(1));

            con.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database connection error:\n"
                            + e.getMessage());
        }
    }

    // ================= LOAD CUSTOMERS =================
    private void loadCustomers() {

        DefaultTableModel model =
                new DefaultTableModel();

        model.setColumnIdentifiers(new String[]{
                "ID",
                "Name",
                "Phone",
                "Email",
                "Address"
        });

        try {

            Connection con =
                    DBConnection.getConnection();

            Statement st =
                    con.createStatement();

            ResultSet rs = st.executeQuery(
                    "SELECT * FROM customers");

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5)
                });
            }

            customerTable.setModel(model);

            con.close();

        } catch (Exception e) {

            showError(e);
        }
    }

    // ================= LOAD VEHICLES =================
    private void loadVehicles() {

        DefaultTableModel model =
                new DefaultTableModel();

        model.setColumnIdentifiers(new String[]{
                "Vehicle ID",
                "Vehicle Number",
                "Model",
                "Type",
                "Customer ID",
                "Owner"
        });

        try {

            Connection con =
                    DBConnection.getConnection();

            Statement st =
                    con.createStatement();

            String sql =
                    "SELECT v.vehicle_id," +
                    "v.vehicle_number," +
                    "v.vehicle_model," +
                    "v.vehicle_type," +
                    "c.customer_id," +
                    "c.name " +
                    "FROM vehicles v " +
                    "JOIN customers c " +
                    "ON v.customer_id=c.customer_id";

            ResultSet rs =
                    st.executeQuery(sql);

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6)
                });
            }

            vehicleTable.setModel(model);

            con.close();

        } catch (Exception e) {

            showError(e);
        }
    }

    // ================= LOAD SERVICES =================
    private void loadServices() {

        DefaultTableModel model =
                new DefaultTableModel();

        model.setColumnIdentifiers(new String[]{
                "Service ID",
                "Vehicle No",
                "Date",
                "Service Type",
                "Cost",
                "Status"
        });

        try {

            Connection con =
                    DBConnection.getConnection();

            Statement st =
                    con.createStatement();

            String sql =
                    "SELECT s.service_id," +
                    "v.vehicle_number," +
                    "s.service_date," +
                    "s.service_type," +
                    "s.service_cost," +
                    "s.service_status " +
                    "FROM service_records s " +
                    "JOIN vehicles v " +
                    "ON s.vehicle_id=v.vehicle_id";

            ResultSet rs =
                    st.executeQuery(sql);

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getDate(3),
                        rs.getString(4),
                        rs.getDouble(5),
                        rs.getString(6)
                });
            }

            serviceTable.setModel(model);

            con.close();

        } catch (Exception e) {

            showError(e);
        }
    }

    // ================= LOAD HISTORY =================
    private void loadHistory() {

        loadServices();

        historyTable.setModel(
                serviceTable.getModel());
    }

    // ================= ADD CUSTOMER =================
    private void addCustomer() {

        JTextField name =
                new JTextField();

        JTextField phone =
                new JTextField();

        JTextField email =
                new JTextField();

        JTextField address =
                new JTextField();

        JPanel panel = new JPanel(
                new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Name:"));
        panel.add(name);

        panel.add(new JLabel("Phone:"));
        panel.add(phone);

        panel.add(new JLabel("Email:"));
        panel.add(email);

        panel.add(new JLabel("Address:"));
        panel.add(address);

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Add Customer",
                        JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {

            try {

                Connection con =
                        DBConnection.getConnection();

                String sql =
                        "INSERT INTO customers " +
                        "(name,phone,email,address) " +
                        "VALUES (?,?,?,?)";

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ps.setString(1, name.getText());
                ps.setString(2, phone.getText());
                ps.setString(3, email.getText());
                ps.setString(4, address.getText());

                ps.executeUpdate();

                JOptionPane.showMessageDialog(
                        this,
                        "Customer added successfully!");

                con.close();

                showDashboard();

            } catch (Exception e) {

                showError(e);
            }
        }
    }

    // ================= ADD VEHICLE =================
    private void addVehicle() {

        JOptionPane.showMessageDialog(
                this,
                "Vehicle entry form can be connected " +
                "to your vehicles table here.");
    }

    // ================= ADD SERVICE =================
    private void addService() {

        JOptionPane.showMessageDialog(
                this,
                "Service entry form can be connected " +
                "to your service_records table here.");
    }

    // ================= ERROR =================
    private void showError(Exception e) {

        JOptionPane.showMessageDialog(
                this,
                "Error:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // ================= MAIN =================
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new VehicleServiceSystem();

        });
    }
}
