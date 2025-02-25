package org.example.products;

import org.example.clients.Client;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class ProductService {

    private final ProductDAO productDAO;
    private final Scanner scanner = new Scanner(System.in);

    public ProductService(Connection connection) {
        this.productDAO = new ProductDAOImpl(connection);
    }

    public void start() {
        while (true) {
            System.out.println("Product DB");
            System.out.println("\t1: add product");
            System.out.println("\t2: view products");
            System.out.print("\t-> ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    productDAO.addProduct();
                    break;
                case "2":
                    List<Product> products = productDAO.showAll();
                    for (Product product : products) {
                        System.out.println(product);
                    }
                    break;
                default:
                    return;
            }
        }
    }
}
