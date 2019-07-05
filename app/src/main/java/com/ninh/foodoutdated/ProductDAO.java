package com.ninh.foodoutdated;

import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private List<Product> products;

    ProductDAO() {
        initEmptyList();

        boolean fileExist = false;
        if (fileExist) {
            loadFromFile();
        }
    }

    private void initEmptyList() {
        products = new ArrayList<>();
    }

    private void loadFromFile() {

    }

    public List<Product> loadAll() {
        return products;
    }

    public Product loadById(long id) {
        Product product = null;
        for (Product p : products) {
            if (p.getId() == id) {
                product = p;
            }
        }
        return product;
    }

    public int findIndex(long id) {
        int index = -1;
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == id) {
                index = i;
            }
        }
        return index;
    }

    public int deleteById(long id) {
        int index = findIndex(id);
        if (index != -1) {
            products.remove(index);
        }
        return index;
    }

    public void add(Product product) {
        if (product != null) {
            products.add(product);
        }
    }

    public int size() {
        return products.size();
    }
}
