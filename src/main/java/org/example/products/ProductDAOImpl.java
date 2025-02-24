package org.example.products;

import org.example.clients.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProductDAOImpl implements ProductDAO {

    private final Connection connection;
    private final Scanner scanner = new Scanner(System.in);

    public ProductDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addProduct() {
        System.out.println("Article: ");
        String article = scanner.nextLine();
        System.out.println("Name: ");
        String name = scanner.nextLine();
        double price = getPrice();
        double quantity = getQuantity();
        String unit = chooseUnit();

        try {
            try (PreparedStatement st = connection.prepareStatement("INSERT INTO Products (article, name, price, quantity, unit) VALUES(?, ?, ?, ?, ?)")) {
                st.setString(1, article);
                st.setString(2, name);
                st.setDouble(3, price);
                st.setDouble(4, quantity);
                st.setString(5, unit);
                st.executeUpdate();
                System.out.println("Product added successfully");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private double getPrice() {
        double price;
        while (true) {
            System.out.print("Price: ");
            String input = scanner.nextLine();

            if (input == null || input.trim().isEmpty()) {
                System.out.println("-> Invalid input!\n");
                continue;
            }

            String strPrice = input.trim().replace(',', '.');

            try {
                price = Double.parseDouble(strPrice);
                return price;
            } catch (NumberFormatException e) {
                System.out.println("-> Invalid input! " + strPrice + "\n");
            }
        }
    }

    private double getQuantity() {
        double quantity;
        while (true) {
            System.out.println("Quantity: ");

            String input = scanner.nextLine();

            if (input == null || input.trim().isEmpty()) {
                System.out.println("-> Invalid input!\n");
                continue;
            }

            String strQuantity = input.trim().replace(',', '.');

            try {
                quantity = Double.parseDouble(strQuantity);
                return quantity;
            } catch (NumberFormatException e) {
                System.out.println("-> Invalid input! " + strQuantity + "\n");
            }
        }
    }

    private String chooseUnit() {
        Scanner scanner = new Scanner(System.in);
        Units[] units = Units.values();

        while (true) {
            System.out.println("Units:");
            for (int i = 0; i < units.length; i++) {
                System.out.println((i + 1) + ". " + units[i]);
            }
            System.out.print("input number -> ");

            String input = scanner.nextLine();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= units.length) {
                    return units[choice - 1].name();
                } else {
                    System.out.println("Invalid input!\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input!\n");
            }
        }
    }

    @Override
    public List<Product> showAll() {
        List<Product> products = new ArrayList<>();
        try{
            try (Statement st = connection.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT * FROM Products")) {
                    while (rs.next()) {
                        Product product = new Product();

                        product.setId(rs.getInt(1));
                        product.setArticle(rs.getString(2));
                        product.setName(rs.getString(3));
                        product.setPrice(rs.getDouble(4));
                        product.setQuantity(rs.getDouble(5));
                        Units unit = Units.fromString(rs.getString(6));
                        product.setUnit(unit);
                        products.add(product);
                    }
                }
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
