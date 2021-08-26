package lk.ijse.pos.dao;

import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.model.Customer;

import java.lang.reflect.Array;
import java.sql.*;
import java.util.ArrayList;

/**
 * @auther : Madhushani Gamage
 * @Data : / / 2021
 **/
public class CustomerDAOImpl {

    public boolean addCustomer(Customer customer) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement("INSERT INTO Customer VALUES (?,?,?,?)");
        pstm.setObject(1, customer.getcID());
        pstm.setObject(2, customer.getName());
        pstm.setObject(3, customer.getAddress());
        pstm.setObject(4, 0);
        return (pstm.executeUpdate() > 0);
    }

    public boolean updateCustomer(Customer customer) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement("UPDATE Customer SET name=?, address=? WHERE id=?");
        pstm.setObject(1, customer.getName());
        pstm.setObject(2, customer.getAddress());
        pstm.setObject(3, customer.getcID());
        return (pstm.executeUpdate() > 0);
    }

    public boolean deleteCustomer(String id) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement("DELETE FROM Customer WHERE id=?");
        pstm.setObject(1, id);
        return (pstm.executeUpdate() > 0);
    }

    public Customer searchCustomer(String id) throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        Statement stm = connection.createStatement();
        ResultSet rst = stm.executeQuery("SELECT * FROM Customer where id=?");
        if (rst.next()) {
            return new Customer(rst.getString("id"), rst.getString("name"), rst.getString("address"));
        }
        return null;
    }
    public ArrayList<Customer> getAllCustomers() throws Exception {
        Connection connection = DBConnection.getInstance().getConnection();
        Statement stm = connection.createStatement();
        ResultSet rst = stm.executeQuery("SELECT * FROM Customer");
        ArrayList<Customer> alCustomers = new ArrayList<>();
        while (rst.next()) {
            Customer customer = new Customer(rst.getString(1), rst.getString(2), rst.getString(3));
            alCustomers.add(customer);
        }
        return alCustomers;
     }
    }

