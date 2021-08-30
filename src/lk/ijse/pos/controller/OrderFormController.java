package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import lk.ijse.pos.dao.CustomerDAOImpl;
import lk.ijse.pos.dao.ItemDAOImpl;
import lk.ijse.pos.dao.OrderDAOImpl;
import lk.ijse.pos.dao.OrderDetailsDAO;
import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.model.Customer;
import lk.ijse.pos.model.Item;
import lk.ijse.pos.model.Orders;
import lk.ijse.pos.view.tblmodel.OrderDetails;


import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author : Sanu Vithanage
 * @since : 0.1.0
 **/

public class OrderFormController implements Initializable {

    @FXML
    private JFXComboBox<String> cmbCustomerID;
    @FXML
    private JFXComboBox<String> cmbItemCode;


    @FXML
    private JFXTextField txtCustomerName;
    @FXML
    private JFXTextField txtDescription;
    @FXML
    private JFXTextField txtQtyOnHand;
    @FXML
    private JFXTextField txtUnitPrice;
    @FXML
    private JFXTextField txtQty;
    @FXML
    private TableView<OrderDetails> tblOrderDetails;

    private ObservableList<OrderDetails> olOrderDetails;

    private boolean update = false;
    @FXML
    private JFXButton btnRemove;
    @FXML
    private Label lblTotal;
    @FXML
    private JFXTextField txtOrderID;
    @FXML
    private JFXDatePicker txtOrderDate;

    private Connection connection;

    public OrderFormController() throws Exception {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
             connection = DBConnection.getInstance().getConnection();

            // Create a day cell factory
            Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
                public DateCell call(final DatePicker datePicker) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate item, boolean empty) {
                            // Must call super
                            super.updateItem(item, empty);
                            LocalDate today = LocalDate.now();
                            setDisable(empty || item.compareTo(today) < 0);
                        }
                    };
                }
            };

            txtOrderDate.setDayCellFactory(dayCellFactory);
            loadAllData();
        } catch (SQLException ex) {
            Logger.getLogger(OrderFormController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            e.printStackTrace();
        }


        cmbCustomerID.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                String customerID = observable.getValue();
                if (customerID == null) {
                    txtCustomerName.setText("");
                    return;
                }

                try {

                    CustomerDAOImpl dao = new CustomerDAOImpl();
                    System.out.println("Customer ID : "+customerID);
                    Customer customer = dao.searchCustomer(customerID);
                    System.out.println("Customer Details :"+ customer.toString());
                    if (customer!=null) {
                        txtCustomerName.setText(customer.getName());
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(OrderFormController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                String itemCode = observable.getValue();

                if (itemCode == null) {
                    txtDescription.setText("");
                    txtQtyOnHand.setText("");
                    txtUnitPrice.setText("");
                    txtQty.setText("");
                    return;
                }

                try {
                   ItemDAOImpl itemDAO = new ItemDAOImpl();
                    Item item = itemDAO.searchItem(itemCode);

                    if (item!=null) {
                        String description = item.getDescription();
                        double unitPrice = item.getUnitPrice().doubleValue();
                        int qtyOnHand = item.getQtyOnHand();

                        txtDescription.setText(description);
                        txtUnitPrice.setText(unitPrice + "");
                        txtQtyOnHand.setText(qtyOnHand + "");
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(OrderFormController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        tblOrderDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        tblOrderDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblOrderDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblOrderDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrderDetails.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));

        olOrderDetails = FXCollections.observableArrayList();
        tblOrderDetails.setItems(olOrderDetails);

        tblOrderDetails.getItems().addListener(new ListChangeListener<OrderDetails>() {
            @Override
            public void onChanged(Change<? extends OrderDetails> c) {

                double total = 0.0;

                for (OrderDetails orderDetail : olOrderDetails) {
                    total += orderDetail.getTotal();
                }

                lblTotal.setText("Total : " + total);

            }
        });

        tblOrderDetails.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<OrderDetails>() {
            @Override
            public void changed(ObservableValue<? extends OrderDetails> observable, OrderDetails oldValue, OrderDetails newValue) {

                OrderDetails currentRow = observable.getValue();

                if (currentRow == null) {
                    cmbItemCode.getSelectionModel().clearSelection();
                    update = false;
                    btnRemove.setDisable(true);
                    return;
                }

                update = true;
                String itemCode = currentRow.getItemCode();
                btnRemove.setDisable(false);

                cmbItemCode.getSelectionModel().select(itemCode);
                txtQty.setText(currentRow.getQty() + "");

            }
        });

        btnRemove.setDisable(true);

    }

    private void loadAllData() throws Exception {



            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM Customer");
            cmbCustomerID.getItems().removeAll(cmbCustomerID.getItems());
            while (rst.next()) {
                String id = rst.getString(1);
                cmbCustomerID.getItems().add(id);
            }
            rst = stm.executeQuery("SELECT * FROM Item");
            cmbItemCode.getItems().removeAll(cmbItemCode.getItems());
            while (rst.next()) {
                String itemCode = rst.getString(1);
                cmbItemCode.getItems().add(itemCode);
                CustomerDAOImpl dao = new CustomerDAOImpl();

                ArrayList<Customer> allCustomers = dao.getAllCustomers();

                cmbCustomerID.getItems().removeAll(cmbCustomerID.getItems());

                for (Customer customer : allCustomers) {
                    String id = customer.getcID();
                    cmbCustomerID.getItems().add(id);
                }

                ItemDAOImpl itemDAO = new ItemDAOImpl();
                ArrayList<Item> allItems = itemDAO.getAllItems();

                cmbItemCode.getItems().removeAll(cmbItemCode.getItems());
                for (Item item : allItems) {
                    itemCode = item.getCode();
                    cmbItemCode.getItems().add(itemCode);
                }

            }
        }


        @FXML
    private void navigateToMain(MouseEvent event) throws IOException {
        Label lblMainNav = (Label) event.getSource();
        Stage primaryStage = (Stage) lblMainNav.getScene().getWindow();

        Parent root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/pos/view/MainForm.fxml"));
        Scene mainScene = new Scene(root);
        primaryStage.setScene(mainScene);
        primaryStage.centerOnScreen();
    }

    @FXML
    private void btnSaveOnAction(ActionEvent event) {

        String itemCode = cmbItemCode.getSelectionModel().getSelectedItem();
        int qty = Integer.parseInt(txtQty.getText());
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        if (!update) {
            for (OrderDetails orderDetail : olOrderDetails) {
                if (orderDetail.getItemCode().equals(itemCode)) {
                    Alert error = new Alert(Alert.AlertType.ERROR, "Please update the item instead of adding", ButtonType.OK);
                    error.setHeaderText("Duplicate Entry Found");
                    error.setTitle("Duplicate Error");
                    error.show();
                    return;
                }
            }
        }
        OrderDetails orderDetail = new OrderDetails(
                itemCode,
                txtDescription.getText(),
                qty,
                unitPrice,
                qty * unitPrice);
        if (!update) {
            olOrderDetails.add(orderDetail);
            tblOrderDetails.setItems(olOrderDetails);
        } else {
            OrderDetails selectedRow = tblOrderDetails.getSelectionModel().getSelectedItem();
            int index = olOrderDetails.indexOf(selectedRow);
            olOrderDetails.set(index, orderDetail);
        }
        cmbItemCode.getSelectionModel().clearSelection();
        cmbItemCode.requestFocus();


    }

    @FXML
    private void btnRemoveOnAction(ActionEvent event) {
        OrderDetails selectedRow = tblOrderDetails.getSelectionModel().getSelectedItem();
        olOrderDetails.remove(selectedRow);

    }

    @FXML
    private void btnPlaceOrderOnAction(ActionEvent event) throws Exception {

            try{
            connection.setAutoCommit(false);

                    //Add order record
                    OrderDAOImpl orderDAO = new OrderDAOImpl();
                    Orders orders = new Orders(txtOrderID.getText(),parseDate(txtOrderDate.getEditor().getText()),cmbCustomerID.getSelectionModel().getSelectedItem());
                System.out.println(orders.getId()+"   "+orders.getDate()+"   "+orders.getCustomerId());
                    boolean b1 = orderDAO.addOrder(orders);

                    if (!b1) {
                        System.out.println("Order record addition failed");
                        connection.rollback();
                        return;
                    }

                    //Add Order details to table
                    OrderDetailsDAO orderDetailsDAO = new OrderDetailsDAO();
                    for(OrderDetails orderDetail:olOrderDetails) {
                        lk.ijse.pos.model.OrderDetails orderDetails = new lk.ijse.pos.model.OrderDetails(
                        txtOrderID.getText(),
                        orderDetail.getItemCode(),
                        orderDetail.getQty(),
                        new BigDecimal(orderDetail.getUnitPrice()));

        boolean b2 = orderDetailsDAO.addOrderDetails(orderDetails);
                        System.out.println("Order Details State :"+b2);
        if (!b2) {
            System.out.println("Order details addition failed");
            connection.rollback();
            return;
        }
        int qtyOnHand = 0;

        ItemDAOImpl itemDAO = new ItemDAOImpl();
        Item item = itemDAO.searchItem(orderDetail.getItemCode());

        if (item!=null) {
            qtyOnHand = item.getQtyOnHand();
        }
        ItemDAOImpl itemDAO1 = new ItemDAOImpl();
        boolean b = itemDAO1.updateItemQtyOnHand(orderDetail.getItemCode(), orderDetail.getQty());

        if (!b) {
            System.out.println("update Item Qty on Hand failed");
            connection.rollback();
            return;
        }

    }

                    connection.commit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Order Placed", ButtonType.OK);
            alert.show();

        } catch (SQLException ex) {
        try {
        connection.rollback();
        } catch (SQLException ex1) {
        Logger.getLogger(OrderFormController.class.getName()).log(Level.SEVERE, null, ex1);
        }
        Logger.getLogger(OrderFormController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
        e.printStackTrace();
        } finally {
        try {
        connection.setAutoCommit(true);
        } catch (SQLException ex) {
        Logger.getLogger(OrderFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
        }

    }

    private Date parseDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        try {
            return sdf.parse(date);
        } catch (ParseException ex) {

            Logger.getLogger(OrderFormController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
