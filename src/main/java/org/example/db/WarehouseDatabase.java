package org.example.db;

import org.example.model.*;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseDatabase {
    private static final String URL = "jdbc:postgresql://localhost:5432/warehouse";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public void initializeDatabase() {
        String adminUrl = "jdbc:postgresql://localhost:5432/postgres";
        try (Connection conn = DriverManager.getConnection(adminUrl, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT 1 FROM pg_database WHERE datname = 'warehouse'");
            if (!rs.next()) {
                stmt.executeUpdate("CREATE DATABASE warehouse");
            }
        } catch (SQLException e) {
            System.err.println("Error during database initialization: " + e.getMessage());
        }
    }

    public void createTables() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS public.categories (" +
                    "id_category SERIAL PRIMARY KEY, " +
                    "category_name VARCHAR(100) NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS public.suppliers (" +
                    "id_supplier SERIAL PRIMARY KEY, " +
                    "supplier_name VARCHAR(100) NOT NULL, " +
                    "supplier_email VARCHAR(100) NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS public.products (" +
                    "id_product SERIAL PRIMARY KEY, " +
                    "product_name VARCHAR(100), " +
                    "current_stock INTEGER DEFAULT 1, " +
                    "id_category INTEGER REFERENCES public.categories(id_category))");

            stmt.execute("CREATE TABLE IF NOT EXISTS public.product_locations (" +
                    "id_product INTEGER PRIMARY KEY REFERENCES public.products(id_product), " +
                    "shelf_code VARCHAR(10))");

            stmt.execute("CREATE TABLE IF NOT EXISTS public.product_suppliers (" +
                    "id_product INTEGER REFERENCES public.products(id_product), " +
                    "id_supplier INTEGER REFERENCES public.suppliers(id_supplier), " +
                    "PRIMARY KEY (id_product, id_supplier))");

            stmt.execute("CREATE TABLE IF NOT EXISTS public.transactions (" +
                    "id_transaction SERIAL PRIMARY KEY, " +
                    "transaction_type VARCHAR(10), " +
                    "quantity INTEGER, " +
                    "id_product INTEGER REFERENCES public.products(id_product), " +
                    "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void seedData() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM public.categories");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO public.categories (id_category, category_name) VALUES " +
                        "(1, 'Electronics'), " +
                        "(2, 'Furniture'), " +
                        "(3, 'Consumables')");
                stmt.execute("SELECT pg_catalog.setval('public.categories_id_category_seq', 3, true)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearDatabase() throws SQLException {
        String[] queries = {
                "DELETE FROM public.transactions",
                "DELETE FROM public.product_suppliers",
                "DELETE FROM public.product_locations",
                "DELETE FROM public.products",
                "DELETE FROM public.suppliers",
                "DELETE FROM public.categories"
        };

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            for (String q : queries) {
                statement.executeUpdate(q);
            }

            statement.execute("ALTER SEQUENCE public.categories_id_category_seq RESTART WITH 1");
            statement.execute("ALTER SEQUENCE public.products_id_product_seq RESTART WITH 1");
            statement.execute("ALTER SEQUENCE public.suppliers_id_supplier_seq RESTART WITH 1");
            statement.execute("ALTER SEQUENCE public.transactions_id_transaction_seq RESTART WITH 1");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Postgres Error: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Product> getProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products ORDER BY product_name";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                products.add(getInfoProducts(resultSet, false));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Postgres Error: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        return products;
    }

    public List<Product> getPopularProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.id_product, p.product_name, SUM(t.quantity) as out_quantity, p.id_category FROM products as p " +
                "JOIN transactions as t ON p.id_product = t.id_product WHERE t.transaction_type = 'OUT' GROUP BY p.id_product, p.product_name, p.id_category ORDER BY out_quantity DESC";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                products.add(getInfoProducts(resultSet, true));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Postgres Error: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        return products;
    }

    public List<Product> searchProducts(String name) throws SQLException {
        List<Product> result = new ArrayList<>();
        String query = "SELECT * FROM products WHERE product_name ILIKE ? ORDER BY product_name";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + name + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(getInfoProducts(resultSet, false));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Postgres Error: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }

    public void addProduct(Product product) throws SQLException {
        String query = "INSERT INTO products (product_name, current_stock, id_category) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, product.getProduct_name());
            preparedStatement.setInt(2, product.getCurrent_stock());
            preparedStatement.setInt(3, product.getCategory_id());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Postgres Error: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateProduct(Product product) throws SQLException {
        String query = "UPDATE products SET product_name = ?, current_stock = ? , id_category = ? WHERE id_product = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, product.getProduct_name());
            preparedStatement.setInt(2, product.getCurrent_stock());
            preparedStatement.setInt(3, product.getCategory_id());
            preparedStatement.setInt(4, product.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Postgres Error: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteProduct(int id_product) throws SQLException {
        String[] dependentTables = {"product_locations", "product_suppliers", "transactions"};
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            for (String table : dependentTables) {
                String delete = "DELETE FROM " + table + " WHERE id_product = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(delete)) {
                    preparedStatement.setInt(1, id_product);
                    preparedStatement.executeUpdate();
                }
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM products WHERE id_product = ?")) {
                preparedStatement.setInt(1, id_product);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Postgres Error: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void StockManagement(int id_product, int stock, String transaction_type) throws SQLException {
        String query = "UPDATE products SET current_stock = current_stock + ? WHERE id_product = ?";
        String query_log = "INSERT INTO transactions (transaction_type, quantity, id_product) VALUES (?, ?, ?)";
        if (transaction_type.equals("OUT")) {
            query = "UPDATE products SET current_stock = current_stock - ? WHERE id_product = ? AND current_stock >= ?";
        }

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             PreparedStatement preparedStatement1 = connection.prepareStatement(query_log)) {

            preparedStatement.setInt(1, stock);
            preparedStatement.setInt(2, id_product);
            if (transaction_type.equals("OUT")) preparedStatement.setInt(3, stock);

            preparedStatement1.setString(1, transaction_type);
            preparedStatement1.setInt(2, stock);
            preparedStatement1.setInt(3, id_product);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0 && transaction_type.equals("OUT")) {
                throw new SQLException("Error! Insufficient stock!");
            } else {
                preparedStatement1.executeUpdate();
            }
        } catch (SQLException e) {
            java.awt.Window activeWindow = javax.swing.FocusManager.getCurrentManager().getActiveWindow();
            JOptionPane.showMessageDialog(activeWindow, e.getMessage(), "Alert",
                    e.getMessage().contains("Insufficient") ? JOptionPane.WARNING_MESSAGE : JOptionPane.ERROR_MESSAGE);
        }
    }

    public Product getInfoProducts(ResultSet resultSet, boolean popular) throws SQLException {
        int id_product = resultSet.getInt("id_product");
        String product_name = resultSet.getString("product_name");
        int current_stock = resultSet.getInt(!popular ? "current_stock" : "out_quantity");
        int id_category = resultSet.getInt("id_category");
        return new Product(id_product, product_name, current_stock, id_category);
    }
}