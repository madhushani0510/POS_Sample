package lk.ijse.pos.dao;

import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.model.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * @auther : Madhushani Gamage
 * @Data : / / 2021
 **/
public class ItemDAOImpl {


        public boolean addItem(Item item) throws Exception {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("INSERT INTO Item VALUES (?,?,?,?)");
            pstm.setObject(1, item.getCode());
            pstm.setObject(2, item.getDescription());
            pstm.setObject(3, item.getUnitPrice());
            pstm.setObject(4, item.getQtyOnHand());
            return (pstm.executeUpdate() > 0);
        }

        public boolean deleteItem(String code) throws Exception {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("DELETE FROM Item WHERE code=?");
            pstm.setObject(1, code);
            return (pstm.executeUpdate() > 0);
        }


        public boolean updateItem(Item item) throws Exception {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement("UPDATE Item SET description=?, unitPrice=?, qtyOnHand=? WHERE code=?");
            pstm.setObject(1, item.getDescription());
            pstm.setObject(2, item.getUnitPrice());
            pstm.setObject(3, item.getQtyOnHand());
            pstm.setObject(4, item.getCode());
            return (pstm.executeUpdate() > 0);
        }


        public Item searchItem(String code) throws Exception {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Item where code=?");
            stm.setObject(1, code);
            ResultSet rst = stm.executeQuery();
            if (rst.next()) {
                return new Item(rst.getString(1),
                        rst.getString(2),
                        rst.getBigDecimal(3),
                        rst.getInt(4));
            }
            return null;
        }

        public ArrayList<Item> getAllItems() throws Exception {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Item");
            ArrayList<Item> allItems = new ArrayList<>();
            while (rst.next()) {
                Item item = new Item(rst.getString(1),
                        rst.getString(2),
                        rst.getBigDecimal(3),
                        rst.getInt(4));
                allItems.add(item);
            }
            return allItems;
        }

    public boolean updateItemQtyOnHand(String itemCode, int qty) {
            return false;
    }
}
