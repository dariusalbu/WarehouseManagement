package org.example.model;

public class Product {
    protected int id;
    protected String product_name;
    protected int current_stock;
    protected int category_id;

    public Product(int id, String product_name, int current_stock, int category_id) {
        this.id = id;
        this.product_name = product_name;
        this.current_stock = current_stock;
        this.category_id = category_id;
    }

    public Product() {
        this.id = 0;
        this.product_name = "";
        this.current_stock = 0;
        this.category_id = 0;
    }

    public void intrareStoc(int quantity) {
        this.current_stock += quantity;
    }

    public void iesireStoc(int quantity) throws InsufficientStockException {
        if (quantity > this.current_stock) {
            throw new InsufficientStockException();
        }
        this.current_stock -= quantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setCurrent_stock(int current_stock) {
        this.current_stock = current_stock;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getId() {
        return id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public int getCurrent_stock() {
        return current_stock;
    }

    public int getCategory_id() {
        return category_id;
    }

    public String get_category_name() {
        return switch (category_id) {
            case 1 -> "Electronics";
            case 2 -> "Furniture";
            case 3 -> "Consumables";
            default -> null;
        };
    }
}
