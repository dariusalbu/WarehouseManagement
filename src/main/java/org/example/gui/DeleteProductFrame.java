package org.example.gui;

import org.example.db.WarehouseDatabase;
import org.example.model.Product;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class DeleteProductFrame extends JFrame {
    private JPanel delete_product;
    private JTextField id;
    private JButton executeButton;

    public DeleteProductFrame(MainFrame mainFrame) {
        setTitle("Delete product");
        setSize(500, 150);
        setContentPane(delete_product);
        setAlwaysOnTop(true);
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ID = Integer.parseInt(id.getText());

                WarehouseDatabase db = new WarehouseDatabase();
                try {
                    db.deleteProduct(ID);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                List<Product> products;
                try {
                    products = db.getProducts();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    mainFrame.loadTabel(products, false);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}
