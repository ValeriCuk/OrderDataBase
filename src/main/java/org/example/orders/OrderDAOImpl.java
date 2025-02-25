package org.example.orders;

import org.example.clients.ClientDAO;
import org.example.clients.ClientDAOImpl;
import org.example.products.Product;
import org.example.products.ProductDAO;
import org.example.products.ProductDAOImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrderDAOImpl implements OrderDAO {

    private final Connection connection;
    private final Scanner scanner = new Scanner(System.in);
    private final ClientDAO clientDAO;
    private final ProductDAO productDAO;

    public OrderDAOImpl(Connection connection) {
        this.connection = connection;
        this.clientDAO = new ClientDAOImpl(connection);
        this.productDAO = new ProductDAOImpl(connection);
    }

    @Override
    public void createOrder() {
        int orderId;
        int clientID = clientDAO.getClientByID();

        try {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO Orders (client_id, date, amount) VALUES(?, CURRENT_DATE, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, clientID);
                ps.setDouble(2, 0.0);
                ps.executeUpdate();
                System.out.println("Order created successfully");

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    } else {
                        throw new SQLException("Order creation failed, no ID obtained.");
                    }
                }
                addProductsToOrder(orderId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addProductsToOrder(int orderId) {
        while (true) {
            System.out.println("Do you want to add a product? (yes/no)");
            String answer = scanner.nextLine().trim().toLowerCase();
            if (!answer.equals("yes")) {
                break;
            }
            Product product = productDAO.getProductById();
            System.out.println(product);
            double quantity = setQuantity(product.getId(), product.getQuantity());

            String insertItemSQL = "INSERT INTO Orders_items (order_id, product_id, price, quantity, unit) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psItem = connection.prepareStatement(insertItemSQL)) {
                psItem.setInt(1, orderId);
                psItem.setInt(2, product.getId());
                psItem.setDouble(3, product.getPrice());
                psItem.setDouble(4, quantity);
                psItem.setString(5, product.getUnit().toString());
                psItem.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private double setQuantity(int productId, double currentQuantity) {
        double quantity = 0.0;
        while (true) {
            try {
                System.out.println("Enter quantity -> ");
                quantity = Double.parseDouble(scanner.nextLine());
                if (quantity > currentQuantity) {
                    System.out.println("Insufficient quantity. Available: " + currentQuantity + ". Try again.");
                    continue;
                }
                double newQuantity = currentQuantity - quantity;
                productDAO.updateProductQuantity(productId, newQuantity);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid quantity.");
            }
        }
        return quantity;
    }

    @Override
    public List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM Orders";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int clientId = rs.getInt("client_id");
                Date orderDate = rs.getDate("date");
                double amount = rs.getDouble("amount");

                Order order = new Order(id, clientId, orderDate, amount);
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public void printOrderById() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Order ID: ");
        int orderId = scanner.nextInt();

        String sql = """
        SELECT o.id AS order_id, o.date, o.amount, 
               c.firstName, c.lastName, c.phone, c.email, 
               p.name AS product_name, oi.quantity, oi.price, oi.unit
        FROM Orders o
        JOIN Clients c ON o.client_id = c.id
        LEFT JOIN Orders_items oi ON o.id = oi.order_id
        LEFT JOIN Products p ON oi.product_id = p.id
        WHERE o.id = ?
        ORDER BY p.name
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                boolean orderFound = false;

                while (rs.next()) {
                    if (!orderFound) {
                        orderFound = true;
                        System.out.println("\n===============================");
                        System.out.println("Order ID: " + rs.getInt("order_id"));
                        System.out.println("Date: " + rs.getDate("date"));
                        System.out.println("Total Amount: " + rs.getDouble("amount"));
                        System.out.println("Client: " + rs.getString("firstName") + " " + rs.getString("lastName"));
                        System.out.println("Phone: " + rs.getString("phone"));
                        System.out.println("Email: " + rs.getString("email"));
                        System.out.println("Products:");
                    }

                    String productName = rs.getString("product_name");
                    if (productName != null) {
                        System.out.printf("  - %s (%.2f %s) - %.2f UAH%n",
                                productName,
                                rs.getDouble("quantity"),
                                rs.getString("unit"),
                                rs.getDouble("price"));
                    }
                }

                if (!orderFound) {
                    System.out.println("Order with ID " + orderId + " not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
