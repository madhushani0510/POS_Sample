package lk.ijse.pos.dao.impl;

import lk.ijse.pos.model.Item;

import java.util.ArrayList;

/**
 * @auther : Madhushani Gamage
 * @Data : / / 2021
 **/
public interface ItemDAO {


        public boolean addItem(Item item) throws Exception;
        public boolean updateItem(Item item) throws Exception;
        public boolean deleteItem(String id) throws Exception;
        public Item searchItem(String id) throws Exception;
        public ArrayList<Item> getAllItem() throws Exception;
        public boolean updateItemQtyOnHand(String code,int qtyOnHand) throws Exception;
    }
