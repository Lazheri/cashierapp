package com.cashier.controller;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import com.cashier.model.Vente;
import com.cashier.service.ReceiptService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ReceiptController implements Initializable {

    @FXML private Label saleIdLabel;
    @FXML private Label saleDateLabel;
    @FXML private TableView<ReceiptItem> receiptItemsTable;
    @FXML private TableColumn<ReceiptItem, String> receiptProductColumn;
    @FXML private TableColumn<ReceiptItem, Double> receiptQuantityColumn;
    @FXML private TableColumn<ReceiptItem, Double> receiptPriceColumn;
    @FXML private Label receiptTotalLabel;
    @FXML private Label paymentMethodLabel;
    @FXML private HBox amountPaidBox;
    @FXML private Label amountPaidLabel;
    @FXML private HBox changeBox;
    @FXML private Label changeLabel;
    @FXML private HBox referenceBox;
    @FXML private Label paymentReferenceLabel;

    private ObservableList<ReceiptItem> receiptItems;
    private ReceiptService receiptService;
    private int saleId;
    private Vente vente;
    private List<MainController.CartItem> cartItems;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        receiptItems = FXCollections.observableArrayList();
        receiptService = new ReceiptService();
        receiptProductColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        receiptQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        receiptPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        receiptItemsTable.setItems(receiptItems);
    }

    public void setReceiptDetails(int saleId, Vente vente, List<MainController.CartItem> cartItems) {
        this.saleId = saleId;
        this.vente = vente;
        this.cartItems = cartItems;
        
        saleIdLabel.setText("ID de Vente: " + saleId);
        saleDateLabel.setText("Date: " + vente.getDateVente().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        
        for (MainController.CartItem item : cartItems) {
            receiptItems.add(new ReceiptItem(item.getProductName(), item.getQuantity(), item.getUnitPrice()));
        }
        receiptTotalLabel.setText(String.format("%.3f TND", vente.getTotal()));
        
        String paymentMethod = vente.getPaymentMethod();
        paymentMethodLabel.setText(paymentMethod != null ? paymentMethod : "N/A");
        
        if ("Espèces".equals(paymentMethod)) {
            amountPaidBox.setVisible(true);
            amountPaidBox.setManaged(true);
            amountPaidLabel.setText(String.format("%.3f TND", vente.getAmountPaid()));
            
            changeBox.setVisible(true);
            changeBox.setManaged(true);
            changeLabel.setText(String.format("%.3f TND", vente.getChangeDue()));
        } else if (vente.getPaymentReference() != null && !vente.getPaymentReference().isEmpty()) {
            referenceBox.setVisible(true);
            referenceBox.setManaged(true);
            paymentReferenceLabel.setText(vente.getPaymentReference());
        }
    }

    @FXML
    private void printReceipt() {
        String receiptContent = receiptService.formatReceipt(saleId, vente, cartItems);
        receiptService.printReceipt(receiptContent);
    }

    @FXML
    private void exportReceipt() {
        String receiptContent = receiptService.formatReceipt(saleId, vente, cartItems);
        receiptService.exportToPdf(receiptContent, saleId);
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


