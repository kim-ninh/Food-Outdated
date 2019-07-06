package com.ninh.foodoutdated;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private List<Product> products;
    private final String FILE_NAME = "data.json";
    private Context context;
    private Gson gson;

    ProductDAO(Context context) {
        this.context = context;
        gson = new Gson();
        initEmptyList();

        if (isFileExists()) {
            products = loadFromFile();
        }
    }

    private boolean isFileExists() {
        File dataFile = new File(context.getFilesDir(), FILE_NAME);
        return dataFile.exists();
    }

    private void initEmptyList() {
        products = new ArrayList<>();
    }

    private List<Product> loadFromFile() {
        List<Product> productList = null;
        String data = "";
        FileInputStream inputStream;
        BufferedReader reader;
        try {
            inputStream = context.openFileInput(FILE_NAME);
            reader = new BufferedReader(new FileReader(inputStream.getFD()));
            data = reader.readLine();

            Type productType = new TypeToken<List<Product>>() {
            }.getType();
            productList = gson.fromJson(data, productType);

            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productList;
    }

    private void saveToFile() {

        String data = gson.toJson(products);
        File file = new File(context.getFilesDir(), FILE_NAME);
        Logger.i(file.getAbsolutePath());
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            saveToFile();
        }
    }

    public int size() {
        return products.size();
    }
}
