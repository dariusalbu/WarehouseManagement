package org.example;
import org.example.gui.MainFrame;
import org.example.db.WarehouseDatabase;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        WarehouseDatabase db = new WarehouseDatabase();
        db.initializeDatabase();
        db.createTables();
        db.seedData();

        MainFrame frame = new MainFrame();

        frame.setVisible(true);
    }
}