package org.example;

import org.example.clients.ClientsService;
import org.example.orders.OrderService;
import org.example.products.ProductService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {

    private static final DBProperties DB_PROPERTIES = new DBProperties();
    private static Connection connection;

    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        try (Connection connection = DriverManager.getConnection(DB_PROPERTIES.getUrl(), DB_PROPERTIES.getUsername(), DB_PROPERTIES.getPassword())){
            Main.connection = connection;
            initDB();
            while (true) {
                System.out.println("1: Clients DB");
                System.out.println("2: Products DB");
                System.out.println("3: Orders DB");
                System.out.print("-> ");

                String s = scanner.nextLine();
                switch (s) {
                    case "1":
                        new ClientsService(connection).start();
                        break;
                    case "2":
                        new ProductService(connection).start();
                        break;
                    case "3":
                        new OrderService(connection).start();
                        break;
                    default:
                        return;
                }
            }
        }

    }

    private static void initDB() throws SQLException {
        try (Statement st = connection.createStatement()){
            dropTables(st);
            createTables(st);
            createTrigger();
        }
    }

    private static void dropTables(Statement st) throws SQLException {
        st.execute("DROP TABLE IF EXISTS Orders_items");
        st.execute("DROP TABLE IF EXISTS Orders");
        st.execute("DROP TABLE IF EXISTS Products");
        st.execute("DROP TABLE IF EXISTS Clients");
    }

    private static void createTables(Statement st) throws SQLException {
        createClients(st);
        createProducts(st);
        createOrders(st);
        createOrders_items(st);
    }

    private static void createClients(Statement st) throws SQLException {
        st.execute("CREATE TABLE Clients (" +
                "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "firstName VARCHAR(20) NOT NULL, " +
                "lastName VARCHAR(30) NOT NULL, " +
                "phone VARCHAR(20) NOT NULL, " +
                "email VARCHAR(50) NOT NULL"
                + ")");
    }

    private static void createProducts(Statement st) throws SQLException {
        st.execute("CREATE TABLE Products (" +
                "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "article VARCHAR(10) NOT NULL, " +
                "name VARCHAR(50) NOT NULL, " +
                "price DECIMAL(10, 2) NOT NULL, " +
                "quantity DECIMAL(20, 5) NOT NULL, " +
                "unit VARCHAR(10) NOT NULL "
                + ")");
    }

    private static void createOrders(Statement st) throws SQLException {
        st.execute("CREATE TABLE Orders (" +
                "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "client_id INT NOT NULL, " +
                "date DATE NOT NULL, " +
                "amount DECIMAL(10, 2) NOT NULL, " +
                "FOREIGN KEY (client_id) REFERENCES Clients(id)"
                + ")");
    }

    private static void createOrders_items(Statement st) throws SQLException {
        st.execute("CREATE TABLE Orders_items (" +
                "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "order_id INT NOT NULL, " +
                "product_id INT NOT NULL, " +
                "price DECIMAL(10, 2) NOT NULL, " +
                "quantity DECIMAL(20, 5) NOT NULL, " +
                "unit VARCHAR(10) NOT NULL, " +
                "FOREIGN KEY (product_id) REFERENCES Products(id), " +
                "FOREIGN KEY (order_id) REFERENCES Orders(id) "
                + ")");
    }

    public static void createTrigger() {
        String sql = """
        CREATE TRIGGER update_order_amount
        AFTER INSERT ON Orders_items
        FOR EACH ROW
        BEGIN
            DECLARE total_amount DECIMAL(10,2);

            SELECT SUM(quantity * price) INTO total_amount
            FROM Orders_items
            WHERE order_id = NEW.order_id;

            UPDATE Orders
            SET amount = total_amount
            WHERE id = NEW.order_id;
        END;
        """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            System.out.println("The trigger has been successfully created.");
        } catch (SQLException e) {
            System.err.println("Error creating trigger: " + e.getMessage());
        }
    }

}