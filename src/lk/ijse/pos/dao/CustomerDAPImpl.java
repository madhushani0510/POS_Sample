package lk.ijse.pos.dao;

import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.model.Customer;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 * @auther : Madhushani Gamage
 * @Data : / / 2021
 **/
public class CustomerDAPImpl {

    public boolean addCustomer{
        Connection connection = DBConnection.getInstance().getConnection();

        PreparedStatement pstm = connection.prepareStatement("INSERT INTO Customer VALUES (?,?,?,?)");

        pstm.setObject(1, txtCustomerId.getText());
        pstm.setObject(2, txtCustomerName.getText());
        pstm.setObject(3, txtCustomerAddress.getText());
        pstm.setObject(4, 0);

    }
    public boolean updateCustomer{
        Connection connection = DBConnection.getInstance().getConnection();

        PreparedStatement pstm = connection.prepareStatement("UPDATE Customer SET name=?, address=? WHERE id=?");
        pstm.setObject(1, txtCustomerName.getText());
        pstm.setObject(2, txtCustomerAddress.getText());
        pstm.setObject(3, txtCustomerId.getText());

    }
    public boolean deleteCustomer{
        Connection connection = DBConnection.getInstance().getConnection();

        PreparedStatement pstm = connection.prepareStatement("DELETE FROM Customer WHERE id=?");
        pstm.setObject(1, customerID);

    }
    public Customer searchCustomer{
        Connection connection = DBConnection.getInstance().getConnection();
        Statement stm = connection.createStatement();
        ResultSet rst = stm.executeQuery("SELECT * FROM Customer where id=?");

    }
    public ArrayList<Customer> getAllCustomer{
        Connection connection = DBConnection.getInstance().getConnection();
        Statement stm = connection.createStatement();
        ResultSet rst = stm.executeQuery("SELECT * FROM Customer");
    }
}
