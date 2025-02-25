package org.example.orders;

import java.util.List;

public interface OrderDAO {
    void createOrder();
    List<Order> getOrders();
    void printOrderById();
}
