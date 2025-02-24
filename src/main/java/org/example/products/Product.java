package org.example.products;

import lombok.Data;

@Data
public class Product {
    private int id;
    private String article;
    private String name;
    private double price;
    private double quantity;
    private Units unit;

    public Product(){}

    public Product(int id, String article, String name, double price, int quantity, Units unit) {
        this.id = id;
        this.article = article;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.unit = unit;
    }
}
