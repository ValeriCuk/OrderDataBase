package org.example.orders;

import org.example.products.Product;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class OrderService {

    private final OrderDAO orderDAO;
    private final Scanner scanner = new Scanner(System.in);

    public OrderService(Connection connection) {
        orderDAO = new OrderDAOImpl(connection);
    }

    public void start() {
        while (true) {
            System.out.println("Orders DB");
            System.out.println("\t1: create order");
            System.out.println("\t2: view orders");
            System.out.println("\t3: order details by ID");
            System.out.print("\t-> ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    orderDAO.createOrder();
                    break;
                case "2":
                    List<Order> orders = orderDAO.getOrders();
                    for (Order order : orders) {
                        System.out.println(order);
                    }
                    break;
                case "3":
                    orderDAO.printOrderById();
                default:
                    return;
            }
        }
    }
}
