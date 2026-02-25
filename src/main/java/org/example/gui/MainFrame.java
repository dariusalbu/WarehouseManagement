package org.example.gui;

import org.example.db.WarehouseDatabase;
import org.example.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class MainFrame extends JFrame {
    private JPanel main_pannel;
    private JTable table1;
    private JButton searchButton;
    private JTextField searchBartextArea;
    private JButton optionsButton;
    private JCheckBox checkPopularityCheckBox;
    private JButton Delete;

    public MainFrame() throws SQLException {
        setTitle("Warehouse Manager");
        setSize(800, 600);
        setContentPane(main_pannel);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        WarehouseDatabase db = new WarehouseDatabase();
        List<Product> products = db.getProducts();
        loadTabel(products, false);
        actionButtonsSearch(db);

        JPopupMenu popupMenu = new JPopupMenu();
        createActionsMenu(popupMenu);
        checkPopularityCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkPopularityCheckBox.isSelected()) {
                    try {
                        List<Product> produse_populare = db.getPopularProducts();
                        loadTabel(produse_populare, true);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else {
                    try {
                        List<Product> updated_products = db.getProducts();
                        loadTabel(products, false);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
    }

    public void loadTabel(List<Product> list, boolean popularity) throws SQLException {
        String[] columns = {"ID", "product_name", "current_stock", "category_id", "category_name"};
        if (popularity) {
            columns[2] = "output_quantity";
        }
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table1.setModel(model);

        for (int i = 0; i < list.size(); i++) {
            Object[] row = {list.get(i).getId(),  list.get(i).getProduct_name(), list.get(i).getCurrent_stock(), list.get(i).getCategory_id(), list.get(i).get_category_name()};
            model.addRow(row);
        }
    }

    private void searchResultProdus(WarehouseDatabase db) throws SQLException {
        String searchTxt = searchBartextArea.getText();
        List<Product> searchResult = null;
        try {
            searchResult = db.searchProducts(searchTxt);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        try {
            loadTabel(searchResult, false);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void actionButtonsSearch(WarehouseDatabase db) throws SQLException {
        searchBartextArea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    searchResultProdus(db);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    searchResultProdus(db);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        Delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    db.clearDatabase();
                    loadTabel(db.getPopularProducts(), false);
                    db.createTables();
                    db.seedData();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void createActionsMenu(JPopupMenu popupMenu) {
        JMenuItem addProdus = new JMenuItem("add");
        JMenuItem updateProdus = new JMenuItem("update");
        JMenuItem deleteProdus = new JMenuItem("delete");
        JMenu stockProdus = new JMenu("in/out");
        JMenuItem inStock = new JMenuItem("stock in");
        JMenuItem outStock = new JMenuItem("stock out");
        stockProdus.add(inStock);
        stockProdus.add(outStock);
        popupMenu.add(addProdus);
        popupMenu.add(updateProdus);
        popupMenu.add(deleteProdus);
        popupMenu.add(stockProdus);
        optionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popupMenu.show(optionsButton, 0, optionsButton.getHeight());
            }
        });

        addProdus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                callAdaugareProduse();
            }
        });

        updateProdus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                callActualizareProduse();
            }
        });

        deleteProdus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                callStergereProduse();
            }
        });

        inStock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                callGestionareStoc("IN");
            }
        });

        outStock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                callGestionareStoc("OUT");
            }
        });
    }

    private void callAdaugareProduse() {
        AddProductFrame frame = new AddProductFrame(MainFrame.this);
        frame.setLocationRelativeTo(MainFrame.this);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void callStergereProduse() {
        DeleteProductFrame dialog = new DeleteProductFrame(MainFrame.this);
        dialog.setLocationRelativeTo(MainFrame.this);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    private void callActualizareProduse() {
        UpdateProductFrame dialog = new UpdateProductFrame(MainFrame.this);
        dialog.setLocationRelativeTo(MainFrame.this);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }

    private void callGestionareStoc(String optiune) {
        StockManagement dialog = new StockManagement(MainFrame.this, optiune);
        dialog.setLocationRelativeTo(MainFrame.this);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
}
