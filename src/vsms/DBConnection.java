package vsms;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/vehicle_service";

    private static final String USER =
            "root";

    private static final String PASSWORD =
            "root";   // Change this to your MySQL password

    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}


