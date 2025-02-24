package org.example;

import org.example.clients.ClientDAOImpl;
import org.example.clients.ClientsService;

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
                        new ClientsService(new ClientDAOImpl(connection)).runDB();
                        break;
                    case "2":
                        //Products
                        break;
                    case "3":
                        //Orders
                        break;
                    default:
                        return;
                }
            }
        }

    }

    private static void initDB() throws SQLException {

        try (Statement st = connection.createStatement()){
            //orders table
            st.execute("DROP TABLE IF EXISTS orders");
            st.execute("CREATE TABLE orders (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "client_id INT NOT NULL, " +
                    "date DATE NOT NULL, " +
                    "amount DECIMAL(10, 2) NOT NULL, " +
                    "FOREIGN KEY (client_id) REFERENCES Clients(id)"
                    + ")");
            //clients table
            st.execute("DROP TABLE IF EXISTS clients");
            st.execute("CREATE TABLE clients (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "firstName VARCHAR(20) NOT NULL, " +
                    "lastName VARCHAR(30) NOT NULL, " +
                    "phone VARCHAR(20) NOT NULL, " +
                    "email VARCHAR(50) NOT NULL"
                    + ")");
            //products table
            st.execute("DROP TABLE IF EXISTS products");
            st.execute("CREATE TABLE products (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "article VARCHAR(10) NOT NULL, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "price DECIMAL(10, 2) NOT NULL, " +
                    "count INT NOT NULL, " +
                    "unit VARCHAR(10) NOT NULL "
                    + ")");
            //orders_items table
            st.execute("DROP TABLE IF EXISTS orders_items");
            st.execute("CREATE TABLE orders_items (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "order_id INT NOT NULL, " +
                    "product_id INT NOT NULL, " +
                    "price DECIMAL(10, 2) NOT NULL, " +
                    "count INT NOT NULL, " +
                    "unit VARCHAR(10) NOT NULL " +
                    "FOREIGN KEY (product_id) REFERENCES Products(id) " +
                    "FOREIGN KEY (order_id) REFERENCES Orders(id) "
                    + ")");
        }
    }
}