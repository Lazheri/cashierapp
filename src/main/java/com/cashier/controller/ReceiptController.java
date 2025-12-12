package com.cashier.controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import com.cashier.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ReceiptController implements Initializable {

    @FXML private Label saleIdLabel;
    @FXML private Label saleDateLabel;
    @FXML private Label customerLabel;
    @FXML private Label loyaltyPointsLabel;
    @FXML private TableView<ReceiptItem> receiptItemsTable;
    @FXML private TableColumn<ReceiptItem, String> receiptProductColumn;
    @FXML private TableColumn<ReceiptItem, Double> receiptQuantityColumn;
    @FXML private TableColumn<ReceiptItem, Double> receiptPriceColumn;
    @FXML private Label receiptTotalLabel;

    private ObservableList<ReceiptItem> receiptItems;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        receiptItems = FXCollections.observableArrayList();
        receiptProductColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        receiptQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        receiptPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        receiptItemsTable.setItems(receiptItems);
    }

    public void setReceiptDetails(int saleId, LocalDateTime saleDate, List<MainController.CartItem> cartItems, double total) {
        setReceiptDetails(saleId, saleDate, cartItems, total, null);
    }

    public void setReceiptDetails(int saleId, LocalDateTime saleDate, List<MainController.CartItem> cartItems, double total, Customer customer) {
        saleIdLabel.setText("ID de Vente: " + saleId);
        saleDateLabel.setText("Date: " + saleDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        
        if (customer != null) {
            customerLabel.setText("Client: " + customer.getName());
            loyaltyPointsLabel.setText("Points loyauté: " + customer.getLoyaltyPoints());
        } else {
            customerLabel.setText("Client: Non enregistré");
            loyaltyPointsLabel.setText("Points loyauté: 0");
        }
        
        for (MainController.CartItem item : cartItems) {
            receiptItems.add(new ReceiptItem(item.getProductName(), item.getQuantity(), item.getUnitPrice()));
        }
        receiptTotalLabel.setText(String.format("%.3f TND", total));
    }

    @FXML
    @SuppressWarnings("unused")
    private void closeReceipt() {
        Stage stage = (Stage) receiptItemsTable.getScene().getWindow();
        stage.close();
    }

    public static class ReceiptItem {
        private final String productName;
        private final double quantity;
        private final double unitPrice;

        public ReceiptItem(String productName, double quantity, double unitPrice) {
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public String getProductName() { return productName; }
        public double getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
    }
}


