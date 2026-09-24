package vsms;

import java.sql.*;
import java.util.Scanner;

public class VehicleServiceManagement {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        int choice;

        do {

            System.out.println();
            System.out.println("==============================================");
            System.out.println("       VEHICLE SERVICE MANAGEMENT SYSTEM");
            System.out.println("==============================================");
            System.out.println("1.  Register Customer");
            System.out.println("2.  Add Vehicle");
            System.out.println("3.  View All Customers");
            System.out.println("4.  View All Vehicles");
            System.out.println("5.  Search Vehicle");
            System.out.println("6.  Create Service Request");
            System.out.println("7.  View Service Requests");
            System.out.println("8.  Update Service Status");
            System.out.println("9.  Update Service Cost");
            System.out.println("10. View Service History");
            System.out.println("11. Delete Vehicle");
            System.out.println("12. Delete Customer");
            System.out.println("13. Exit");
            System.out.println("==============================================");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    registerCustomer();
                    break;

                case 2:
                    addVehicle();
                    break;

                case 3:
                    viewCustomers();
                    break;

                case 4:
                    viewVehicles();
                    break;

                case 5:
                    searchVehicle();
                    break;

                case 6:
                    createServiceRequest();
                    break;

                case 7:
                    viewServiceRequests();
                    break;

                case 8:
                    updateServiceStatus();
                    break;

                case 9:
                    updateServiceCost();
                    break;

                case 10:
                    serviceHistory();
                    break;

                case 11:
                    deleteVehicle();
                    break;

                case 12:
                    deleteCustomer();
                    break;

                case 13:
                    System.out.println();
                    System.out.println("Thank you for using the system!");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 13);

        sc.close();
    }


    // =========================================================
    // 1. REGISTER CUSTOMER
    // =========================================================

    static void registerCustomer() {

        String sql =
                "INSERT INTO customers " +
                "(name, phone, email, address) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            sc.nextLine();

            System.out.print("Enter Customer Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Phone: ");
            String phone = sc.nextLine();

            System.out.print("Enter Email: ");
            String email = sc.nextLine();

            System.out.print("Enter Address: ");
            String address = sc.nextLine();

            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, email);
            ps.setString(4, address);

            int rows = ps.executeUpdate();

            if (rows > 0) {

                ResultSet rs = ps.getGeneratedKeys();

                if (rs.next()) {

                    int customerId =
                            rs.getInt(1);

                    System.out.println();
                    System.out.println(
                            "Customer Registered Successfully!"
                    );

                    System.out.println(
                            "Customer ID: " + customerId
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Database Error: " + e.getMessage()
            );
        }
    }


    // =========================================================
    // 2. ADD VEHICLE
    // =========================================================

    static void addVehicle() {

        try (Connection con = DBConnection.getConnection()) {

            System.out.print("Enter Customer ID: ");
            int customerId = sc.nextInt();

            // Check customer exists
            String checkCustomer =
                    "SELECT customer_id FROM customers " +
                    "WHERE customer_id = ?";

            try (PreparedStatement ps =
                         con.prepareStatement(checkCustomer)) {

                ps.setInt(1, customerId);

                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {

                    System.out.println(
                            "Customer ID does not exist!"
                    );

                    return;
                }
            }

            sc.nextLine();

            System.out.print("Enter Vehicle Number: ");
            String vehicleNumber = sc.nextLine();

            System.out.print("Enter Vehicle Model: ");
            String vehicleModel = sc.nextLine();

            System.out.print("Enter Vehicle Type: ");
            String vehicleType = sc.nextLine();

            String sql =
                    "INSERT INTO vehicles " +
                    "(customer_id, vehicle_number, " +
                    "vehicle_model, vehicle_type) " +
                    "VALUES (?, ?, ?, ?)";

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setInt(1, customerId);
                ps.setString(2, vehicleNumber);
                ps.setString(3, vehicleModel);
                ps.setString(4, vehicleType);

                ps.executeUpdate();

                System.out.println(
                        "Vehicle Added Successfully!"
                );
            }

        } catch (SQLIntegrityConstraintViolationException e) {

            System.out.println(
                    "Vehicle number already exists!"
            );

        } catch (SQLException e) {

            System.out.println(
                    "Database Error: " + e.getMessage()
            );
        }
    }


    // =========================================================
    // 3. VIEW ALL CUSTOMERS
    // =========================================================

    static void viewCustomers() {

        String sql =
                "SELECT * FROM customers";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println();
            System.out.println("============== CUSTOMERS ==============");

            boolean found = false;

            while (rs.next()) {

                found = true;

                System.out.println(
                        "Customer ID : " +
                        rs.getInt("customer_id")
                );

                System.out.println(
                        "Name        : " +
                        rs.getString("name")
                );

                System.out.println(
                        "Phone       : " +
                        rs.getString("phone")
                );

                System.out.println(
                        "Email       : " +
                        rs.getString("email")
                );

                System.out.println(
                        "Address     : " +
                        rs.getString("address")
                );

                System.out.println(
                        "----------------------------------------"
                );
            }

            if (!found) {
                System.out.println(
                        "No customers found."
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Database Error: " + e.getMessage()
            );
        }
    }


    // =========================================================
    // 4. VIEW ALL VEHICLES
    // =========================================================

    static void viewVehicles() {

        String sql =
                "SELECT v.vehicle_id, v.vehicle_number, " +
                "v.vehicle_model, v.vehicle_type, " +
                "c.customer_id, c.name " +
                "FROM vehicles v " +
                "JOIN customers c " +
                "ON v.customer_id = c.customer_id";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println();
            System.out.println("=============== VEHICLES ===============");

            boolean found = false;

            while (rs.next()) {

                found = true;

                System.out.println(
                        "Vehicle ID     : " +
                        rs.getInt("vehicle_id")
                );

                System.out.println(
                        "Vehicle Number : " +
                        rs.getString("vehicle_number")
                );

                System.out.println(
                        "Model          : " +
                        rs.getString("vehicle_model")
                );

                System.out.println(
                        "Type           : " +
                        rs.getString("vehicle_type")
                );

                System.out.println(
                        "Customer ID    : " +
                        rs.getInt("customer_id")
                );

                System.out.println(
                        "Customer Name  : " +
                        rs.getString("name")
                );

                System.out.println(
                        "----------------------------------------"
                );
            }

            if (!found) {
                System.out.println(
                        "No vehicles found."
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Database Error: " + e.getMessage()
            );
        }
    }


    // =========================================================
    // 5. SEARCH VEHICLE
    // =========================================================

    static void searchVehicle() {

        try (Connection con = DBConnection.getConnection()) {

            sc.nextLine();

            System.out.print(
                    "Enter Vehicle Number: "
            );

            String vehicleNumber =
                    sc.nextLine();

            String sql =
                    "SELECT v.vehicle_id, " +
                    "v.vehicle_number, " +
                    "v.vehicle_model, " +
                    "v.vehicle_type, " +
                    "c.customer_id, " +
                    "c.name, c.phone " +
                    "FROM vehicles v " +
                    "JOIN customers c " +
                    "ON v.customer_id = c.customer_id " +
                    "WHERE v.vehicle_number = ?";

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setString(1, vehicleNumber);

                ResultSet rs =
                        ps.executeQuery();

                if (rs.next()) {

                    System.out.println();
                    System.out.println(
                            "========== VEHICLE DETAILS =========="
                    );

                    System.out.println(
                            "Vehicle ID    : " +
                            rs.getInt("vehicle_id")
                    );

                    System.out.println(
                            "Vehicle Number: " +
                            rs.getString("vehicle_number")
                    );

                    System.out.println(
                            "Vehicle Model : " +
                            rs.getString("vehicle_model")
                    );

                    System.out.println(
                            "Vehicle Type  : " +
                            rs.getString("vehicle_type")
                    );

                    System.out.println(
                            "Customer ID   : " +
                            rs.getInt("customer_id")
                    );

                    System.out.println(
                            "Customer Name : " +
                            rs.getString("name")
                    );

                    System.out.println(
                            "Customer Phone: " +
                            rs.getString("phone")
                    );

                } else {

                    System.out.println(
                            "Vehicle Not Found!"
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Database Error: " + e.getMessage()
            );
        }
    }


    // =========================================================
    // 6. CREATE SERVICE REQUEST
    // =========================================================

    static void createServiceRequest() {

        try (Connection con = DBConnection.getConnection()) {

            System.out.print("Enter Vehicle ID: ");
            int vehicleId = sc.nextInt();

            // Check vehicle exists
            String checkVehicle =
                    "SELECT vehicle_id FROM vehicles " +
                    "WHERE vehicle_id = ?";

            try (PreparedStatement ps =
                         con.prepareStatement(checkVehicle)) {

                ps.setInt(1, vehicleId);

                ResultSet rs =
                        ps.executeQuery();

                if (!rs.next()) {

                    System.out.println(
                            "Vehicle ID does not exist!"
                    );

                    return;
                }
            }

            sc.nextLine();

            System.out.print(
                    "Enter Service Date (YYYY-MM-DD): "
            );

            String date = sc.nextLine();

            System.out.println();
            System.out.println("Service Types:");
            System.out.println("1. General Service");
            System.out.println("2. Oil Change");
            System.out.println("3. Engine Service");
            System.out.println("4. Brake Service");
            System.out.println("5. AC Service");
            System.out.println("6. Full Service");

            System.out.print(
                    "Enter Service Type: "
            );

            int serviceChoice =
                    sc.nextInt();

            String serviceType;

            switch (serviceChoice) {

                case 1:
                    serviceType = "General Service";
                    break;

                case 2:
                    serviceType = "Oil Change";
                    break;

                case 3:
                    serviceType = "Engine Service";
                    break;

                case 4:
                    serviceType = "Brake Service";
                    break;

                case 5:
                    serviceType = "AC Service";
                    break;

                case 6:
                    serviceType = "Full Service";
                    break;

                default:
                    System.out.println(
                            "Invalid service type!"
                    );
                    return;
            }

            System.out.print(
                    "Enter Service Cost: "
            );

            double cost =
                    sc.nextDouble();

            String status = "Pending";

            String sql =
                    "INSERT INTO service_records " +
                    "(vehicle_id, service_date, " +
                    "service_type, service_cost, " +
                    "service_status) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setInt(1, vehicleId);
                ps.setDate(
                        2,
                        Date.valueOf(date)
                );
                ps.setString(3, serviceType);
                ps.setDouble(4, cost);
                ps.setString(5, status);

                ps.executeUpdate();

                System.out.println();
                System.out.println(
                        "Service Request Created Successfully!"
                );
            }

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Invalid date format!"
            );

            System.out.println(
                    "Use YYYY-MM-DD."
            );

        } catch (SQLException e) {

            System.out.println(
                    "Database Error: " + e.getMessage()
            );
        }
    }


    // =========================================================
    // 7. VIEW SERVICE REQUESTS
    // =========================================================

    static void viewServiceRequests() {

        String sql =
                "SELECT s.service_id, " +
                "v.vehicle_number, " +
                "s.service_date, " +
                "s.service_type, " +
                "s.service_cost, " +
                "s.service_status " +
                "FROM service_records s " +
                "JOIN vehicles v " +
                "ON s.vehicle_id = v.vehicle_id";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println();
            System.out.println(
                    "=========== SERVICE REQUESTS ==========="
            );

            boolean found = false;

            while (rs.next()) {

                found = true;

                System.out.println(
                        "Service ID    : " +
                        rs.getInt("service_id")
                );

                System.out.println(
                        "Vehicle Number: " +
                        rs.getString("vehicle_number")
                );

                System.out.println(
                        "Service Date  : " +
                        rs.getDate("service_date")
                );

                System.out.println(
                        "Service Type  : " +
                        rs.getString("service_type")
                );

                System.out.println(
                        "Service Cost  : " +
                        rs.getDouble("service_cost")
                );

                System.out.println(
                        "Status        : " +
                        rs.getString("service_status")
                );

                System.out.println(
                        "----------------------------------------"
                );
            }

            if (!found) {

                System.out.println(
                        "No service requests found."
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Database Error: " + e.getMessage()
            );
        }
    }


    // =========================================================
    // 8. UPDATE SERVICE STATUS
    // =========================================================

    static void updateServiceStatus() {

        try (Connection con = DBConnection.getConnection()) {

            System.out.print(
                    "Enter Service ID: "
            );

            int serviceId =
                    sc.nextInt();

            System.out.println();
            System.out.println("Status Values:");
            System.out.println("1. Pending");
            System.out.println("2. In Progress");
            System.out.println("3. Completed");
            System.out.println("4. Delivered");

            System.out.print(
                    "Select New Status: "
            );

            int statusChoice =
                    sc.nextInt();

            String status;

            switch (statusChoice) {

                case 1:
                    status = "Pending";
                    break;

                case 2:
                    status = "In Progress";
                    break;

                case 3:
                    status = "Completed";
                    break;

                case 4:
                    status = "Delivered";
                    break;

                default:
                    System.out.println(
                            "Invalid status!"
                    );
                    return;
            }

            String sql =
                    "UPDATE service_records " +
                    "SET service_status = ? " +
                    "WHERE service_id = ?";

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setString(1, status);
                ps.setInt(2, serviceId);

                int rows =
                        ps.executeUpdate();

                if (rows > 0) {

                    System.out.println(
                            "Service Status Updated Successfully!"
                    );

                } else {

                    System.out.println(
                            "Service ID not found!"
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Database Error: " + e.getMessage()
            );
        }
    }


    // =========================================================
    // 9. UPDATE SERVICE COST
    // =========================================================

    static void updateServiceCost() {

        try (Connection con = DBConnection.getConnection()) {

            System.out.print(
                    "Enter Service ID: "
            );

            int serviceId =
                    sc.nextInt();

            System.out.print(
                    "Enter New Service Cost: "
            );

            double cost =
                    sc.nextDouble();

            if (cost < 0) {

                System.out.println(
                        "Cost cannot be negative!"
                );

                return;
            }

            String sql =
                    "UPDATE service_records " +
                    "SET service_cost = ? " +
                    "WHERE service_id = ?";

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setDouble(1, cost);
                ps.setInt(2, serviceId);

                int rows =
                        ps.executeUpdate();

                if (rows > 0) {

                    System.out.println(
                            "Service Cost Updated Successfully!"
                    );

                } else {

                    System.out.println(
                            "Service ID not found!"
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Database Error: " + e.getMessage()
            );
        }
    }


    // =========================================================
    // 10. SERVICE HISTORY
    // =========================================================

    static void serviceHistory() {

        try (Connection con = DBConnection.getConnection()) {

            System.out.print(
                    "Enter Vehicle ID: "
            );

            int vehicleId =
                    sc.nextInt();

            String sql =
                    "SELECT s.service_id, " +
                    "v.vehicle_number, " +
                    "s.service_date, " +
                    "s.service_type, " +
                    "s.service_cost, " +
                    "s.service_status " +
                    "FROM service_records s " +
                    "JOIN vehicles v " +
                    "ON s.vehicle_id = v.vehicle_id " +
                    "WHERE s.vehicle_id = ? " +
                    "ORDER BY s.service_date DESC";

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setInt(1, vehicleId);

                ResultSet rs =
                        ps.executeQuery();

                System.out.println();
                System.out.println(
                        "============ SERVICE HISTORY ============"
                );

                boolean found = false;

                while (rs.next()) {

                    found = true;

                    System.out.println(
                            "Service ID    : " +
                            rs.getInt("service_id")
                    );

                    System.out.println(
                            "Vehicle Number: " +
                            rs.getString("vehicle_number")
                    );

                    System.out.println(
                            "Date          : " +
                            rs.getDate("service_date")
                    );

                    System.out.println(
                            "Service Type  : " +
                            rs.getString("service_type")
                    );

                    System.out.println(
                            "Cost          : " +
                            rs.getDouble("service_cost")
                    );

                    System.out.println(
                            "Status        : " +
                            rs.getString("service_status")
                    );

                    System.out.println(
                            "------------------------------------------"
                    );
                }

                if (!found) {

                    System.out.println(
                            "No service history found."
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Database Error: " + e.getMessage()
            );
        }
    }


    // =========================================================
    // 11. DELETE VEHICLE
    // =========================================================

    static void deleteVehicle() {

        try (Connection con = DBConnection.getConnection()) {

            System.out.print(
                    "Enter Vehicle ID: "
            );

            int vehicleId =
                    sc.nextInt();

            String sql =
                    "DELETE FROM vehicles " +
                    "WHERE vehicle_id = ?";

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setInt(1, vehicleId);

                int rows =
                        ps.executeUpdate();

                if (rows > 0) {

                    System.out.println(
                            "Vehicle Deleted Successfully!"
                    );

                } else {

                    System.out.println(
                            "Vehicle ID not found!"
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Database Error: " + e.getMessage()
            );
        }
    }


    // =========================================================
    // 12. DELETE CUSTOMER
    // =========================================================

    static void deleteCustomer() {

        try (Connection con = DBConnection.getConnection()) {

            System.out.print(
                    "Enter Customer ID: "
            );

            int customerId =
                    sc.nextInt();

            String sql =
                    "DELETE FROM customers " +
                    "WHERE customer_id = ?";

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setInt(1, customerId);

                int rows =
                        ps.executeUpdate();

                if (rows > 0) {

                    System.out.println(
                            "Customer Deleted Successfully!"
                    );

                } else {

                    System.out.println(
                            "Customer ID not found!"
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Database Error: " + e.getMessage()
            );
        }
    }
}
