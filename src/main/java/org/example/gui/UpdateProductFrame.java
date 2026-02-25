package org.example.gui;

import org.example.db.WarehouseDatabase;
import org.example.model.Product;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class UpdateProductFrame extends JFrame {
    private JPanel update_products;
    private JComboBox category;
    private JButton executeButton;
    private JTextField product_name;
    private JTextField stock;
    private JTextField ID;

    public UpdateProductFrame(MainFrame mainFrame) {
        setTitle("Update product");
        setSize(700, 150);
        setContentPane(update_products);
        setAlwaysOnTop(true);
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Product product = new Product();
                product.setId(Integer.parseInt(ID.getText()));
                product.setProduct_name(product_name.getText());
                product.setCurrent_stock(Integer.parseInt(stock.getText()));
                String categorie_produs = Objects.requireNonNull(category.getSelectedItem()).toString();
                switch (categorie_produs) {
                    case "Electronics":
                        product.setCategory_id(1);
                        break;
                    case "Furniture":
                        product.setCategory_id(2);
                        break;
                    case "Consumables":
                        product.setCategory_id(3);
                        break;
                }

                WarehouseDatabase db = new WarehouseDatabase();
                try {
                    db.updateProduct(product);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                List<Product> produse;
                try {
                    produse = db.getProducts();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    mainFrame.loadTabel(produse, false);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}
