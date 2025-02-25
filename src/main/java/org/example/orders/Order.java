package org.example.orders;

import lombok.Data;

import java.util.Date;

@Data
public class Order {

    private int id;
    private int clientId;
    private Date date;
    private double amount;

    public Order(){}

    public Order(int id, int clientId, Date date, double amount) {
        this.id = id;
        this.clientId = clientId;
        this.date = date;
        this.amount = amount;
    }
}
