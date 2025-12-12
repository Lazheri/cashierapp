package com.cashier.controller;

import com.cashier.dao.CustomerDAO;
import com.cashier.dao.VenteDAO;
import com.cashier.model.Customer;
import com.cashier.model.Vente;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerManagementController implements Initializable {

    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, Integer> idColumn;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, Integer> pointsColumn;

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField pointsField;
    @FXML private TextArea notesField;
    @FXML private TextField searchField;

    @FXML private TableView<PurchaseHistoryItem> purchaseHistoryTable;
    @FXML private TableColumn<PurchaseHistoryItem, Integer> saleIdColumn;
    @FXML private TableColumn<PurchaseHistoryItem, String> saleDateColumn;
    @FXML private TableColumn<PurchaseHistoryItem, Double> saleTotalColumn;
    @FXML private TableColumn<PurchaseHistoryItem, Integer> pointsUsedColumn;

    private CustomerDAO customerDAO;
    private VenteDAO venteDAO;
    private ObservableList<Customer> customers;
    private ObservableList<PurchaseHistoryItem> purchaseHistory;
    private Customer selectedCustomer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customerDAO = new CustomerDAO();
        venteDAO = new VenteDAO();
        customers = FXCollections.observableArrayList();
        purchaseHistory = FXCollections.observableArrayList();

        setupCustomersTable();
        setupPurchaseHistoryTable();
        loadAllCustomers();

        customersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedCustomer = newSelection;
                displayCustomerDetails(newSelection);
                loadPurchaseHistory(newSelection.getId());
            }
        });
    }

    private void setupCustomersTable() {
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        pointsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getLoyaltyPoints()).asObject());
        customersTable.setItems(customers);
    }

    private void setupPurchaseHistoryTable() {
        saleIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getSaleId()).asObject());
        saleDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSaleDate()));
        saleTotalColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotal()).asObject());
        pointsUsedColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPointsUsed()).asObject());
        purchaseHistoryTable.setItems(purchaseHistory);
    }

    private void loadAllCustomers() {
        customers.clear();
        customers.addAll(customerDAO.getAllCustomers());
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadAllCustomers();
        } else {
            customers.clear();
            customers.addAll(customerDAO.searchCustomers(query));
        }
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        loadAllCustomers();
    }

    @FXML
    private void handleAddCustomer() {
        clearFields();
        selectedCustomer = null;
    }

    @FXML
    private void handleSaveCustomer() {
        if (nameField.getText().trim().isEmpty()) {
            showMessage("Le nom du client est requis.");
            return;
        }

        try {
            int points = pointsField.getText().isEmpty() ? 0 : Integer.parseInt(pointsField.getText());
            Customer customer = new Customer(
                nameField.getText(),
                phoneField.getText(),
                emailField.getText(),
                points,
                notesField.getText()
            );

            if (selectedCustomer == null) {
                customerDAO.addCustomer(customer);
                showMessage("Client ajouté avec succès.");
            } else {
                customer.setId(selectedCustomer.getId());
                customerDAO.updateCustomer(customer);
                showMessage("Client mis à jour avec succès.");
            }

            loadAllCustomers();
            clearFields();
        } catch (NumberFormatException e) {
            showMessage("Format de points invalide.");
        }
    }

    @FXML
    private void handleDeleteCustomer() {
        if (selectedCustomer == null) {
            showMessage("Veuillez sélectionner un client à supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le client?");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer " + selectedCustomer.getName() + "?");

        if (confirmation.showAndWait().isPresent() && confirmation.getResult() == ButtonType.OK) {
            customerDAO.deleteCustomer(selectedCustomer.getId());
            loadAllCustomers();
            clearFields();
            showMessage("Client supprimé.");
        }
    }

    private void displayCustomerDetails(Customer customer) {
        idField.setText(String.valueOf(customer.getId()));
        nameField.setText(customer.getName());
        phoneField.setText(customer.getPhone());
        emailField.setText(customer.getEmail());
        pointsField.setText(String.valueOf(customer.getLoyaltyPoints()));
        notesField.setText(customer.getNotes());
    }

    private void loadPurchaseHistory(int customerId) {
        purchaseHistory.clear();
        List<Integer> saleIds = customerDAO.getPurchaseHistoryIds(customerId);
        for (Integer saleId : saleIds) {
            Vente vente = venteDAO.getVenteById(saleId);
            if (vente != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                purchaseHistory.add(new PurchaseHistoryItem(
                    vente.getId(),
                    vente.getDateVente().format(formatter),
                    vente.getTotal(),
                    vente.getLoyaltyPointsUsed()
                ));
            }
        }
    }

    private void clearFields() {
        idField.clear();
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        pointsField.clear();
        notesField.clear();
        purchaseHistory.clear();
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class PurchaseHistoryItem {
        private int saleId;
        private String saleDate;
        private double total;
        private int pointsUsed;

        public PurchaseHistoryItem(int saleId, String saleDate, double total, int pointsUsed) {
            this.saleId = saleId;
            this.saleDate = saleDate;
            this.total = total;
            this.pointsUsed = pointsUsed;
        }

        public int getSaleId() { return saleId; }
        public String getSaleDate() { return saleDate; }
        public double getTotal() { return total; }
        public int getPointsUsed() { return pointsUsed; }
    }
}
