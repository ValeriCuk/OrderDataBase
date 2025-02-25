package org.example.products;

import java.util.List;

public interface ProductDAO {
    void addProduct();
    List<Product> showAll();
    Product getProductById();
    void updateProductQuantity(int productId, double newQuantity);
}
