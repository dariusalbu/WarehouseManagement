package org.example.gui;

import org.example.db.WarehouseDatabase;
import org.example.model.Product;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class StockManagement extends JFrame {
    private JTextField stock;
    private JButton executeButton;
    private JTextField id;
    private JPanel stockManagement;

    public StockManagement(MainFrame mainFrame, String option) {
        setTitle(option);
        setSize(500, 150);
        setContentPane(stockManagement);
        setAlwaysOnTop(true);
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ID = Integer.parseInt(id.getText());
                int stoc_curent = Integer.parseInt(stock.getText());

                WarehouseDatabase db = new WarehouseDatabase();
                try {
                    db.StockManagement(ID, stoc_curent, option);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(StockManagement.this, ex.getMessage(),"Insuficient stock!", JOptionPane.WARNING_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(StockManagement.this, ex.getMessage(), "Error!",  JOptionPane.WARNING_MESSAGE);
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
