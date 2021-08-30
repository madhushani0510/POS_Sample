package lk.ijse.pos.dao.impl;

import lk.ijse.pos.model.Customer;

import java.util.ArrayList;

/**
 * @auther : Madhushani Gamage
 * @Data : / / 2021
 **/
public interface CustomerDAO {

    public boolean addCustomer(Customer customer) throws Exception;
    public boolean updateCustomer(Customer customer) throws Exception;
    public boolean deleteCustomer(String id) throws Exception;
    public Customer searchCustomer(String id) throws Exception;
    public ArrayList<Customer> getAllCustomer() throws Exception;

}
