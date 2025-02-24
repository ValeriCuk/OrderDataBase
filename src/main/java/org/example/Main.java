package org.example;

import org.example.clients.ClientDAOImpl;
import org.example.clients.ClientsService;
import org.example.products.ProductDAOImpl;
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
                        new ClientsService(new ClientDAOImpl(connection)).runDB();
                        break;
                    case "2":
                        new ProductService(new ProductDAOImpl(connection)).start();
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
            st.execute("DROP TABLE IF EXISTS Orders");
            st.execute("CREATE TABLE Orders (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "client_id INT NOT NULL, " +
                    "date DATE NOT NULL, " +
                    "amount DECIMAL(10, 2) NOT NULL, " +
                    "FOREIGN KEY (client_id) REFERENCES Clients(id)"
                    + ")");
            //clients table
            st.execute("DROP TABLE IF EXISTS Clients");
            st.execute("CREATE TABLE Clients (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "firstName VARCHAR(20) NOT NULL, " +
                    "lastName VARCHAR(30) NOT NULL, " +
                    "phone VARCHAR(20) NOT NULL, " +
                    "email VARCHAR(50) NOT NULL"
                    + ")");
            //products table
            st.execute("DROP TABLE IF EXISTS Products");
            st.execute("CREATE TABLE Products (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "article VARCHAR(10) NOT NULL, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "price DECIMAL(10, 2) NOT NULL, " +
                    "quantity DECIMAL(20, 5) NOT NULL, " +
                    "unit VARCHAR(10) NOT NULL "
                    + ")");
            //orders_items table
            st.execute("DROP TABLE IF EXISTS Orders_items");
            st.execute("CREATE TABLE Orders_items (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "order_id INT NOT NULL, " +
                    "product_id INT NOT NULL, " +
                    "price DECIMAL(10, 2) NOT NULL, " +
                    "quantity DECIMAL(20, 5) NOT NULL, " +
                    "unit VARCHAR(10) NOT NULL " +
                    "FOREIGN KEY (product_id) REFERENCES Products(id) " +
                    "FOREIGN KEY (order_id) REFERENCES Orders(id) "
                    + ")");
        }
    }
}