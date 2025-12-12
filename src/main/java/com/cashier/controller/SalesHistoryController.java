package com.cashier.controller;

import com.cashier.SessionContext;
import com.cashier.dao.VenteDAO;
import com.cashier.dao.LigneVenteDAO;
import com.cashier.dao.ProduitDAO;
import com.cashier.model.Vente;
import com.cashier.model.LigneVente;
import com.cashier.model.Produit;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class SalesHistoryController implements Initializable {

    @FXML private TableView<Vente> salesTable;
    @FXML private TableColumn<Vente, Integer> saleIdColumn;
    @FXML private TableColumn<Vente, String> saleDateColumn;
    @FXML private TableColumn<Vente, Double> saleTotalColumn;

    @FXML private VBox saleDetailsBox;
    @FXML private Label selectedSaleIdLabel;
    @FXML private Label selectedSaleDateLabel;
    @FXML private Label selectedSaleTotalLabel;

    @FXML private TableView<SaleItemDetail> saleItemsTable;
    @FXML private TableColumn<SaleItemDetail, String> itemProductColumn;
    @FXML private TableColumn<SaleItemDetail, Double> itemQuantityColumn;
    @FXML private TableColumn<SaleItemDetail, Double> itemPriceColumn;
    @FXML private TableColumn<SaleItemDetail, Double> itemTotalColumn;

    @FXML private Label messageLabel;

    private VenteDAO venteDAO;
    private LigneVenteDAO ligneVenteDAO;
    private ProduitDAO produitDAO;
    private ObservableList<Vente> sales;
    private ObservableList<SaleItemDetail> saleItems;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!SessionContext.isAdmin()) {
            denyAccessAndClose();
            return;
        }

        venteDAO = new VenteDAO();
        ligneVenteDAO = new LigneVenteDAO();
        produitDAO = new ProduitDAO();
        sales = FXCollections.observableArrayList();
        saleItems = FXCollections.observableArrayList();

        setupSalesTable();
        setupSaleItemsTable();
        loadSales();
        clearMessage();
    }

    private void setupSalesTable() {
        saleIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        saleDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateVente().format(FORMATTER)));
        saleTotalColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotal()).asObject());

        salesTable.setItems(sales);

        salesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showSaleDetails(newSelection);
            } else {
                hideSaleDetails();
            }
        });
    }

    private void setupSaleItemsTable() {
        itemProductColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));
        itemQuantityColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getQuantity()).asObject());
        itemPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getUnitPrice()).asObject());
        itemTotalColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotal()).asObject());

        saleItemsTable.setItems(saleItems);
    }

    private void loadSales() {
        sales.clear();
        sales.addAll(venteDAO.getAllVentes());
    }

    private void showSaleDetails(Vente vente) {
        selectedSaleIdLabel.setText(String.valueOf(vente.getId()));
        selectedSaleDateLabel.setText(vente.getDateVente().format(FORMATTER));
        selectedSaleTotalLabel.setText(String.format("%.3f TND", vente.getTotal()));

        saleItems.clear();
        List<LigneVente> lignesVente = ligneVenteDAO.getLignesVenteByVenteId(vente.getId());

        for (LigneVente ligne : lignesVente) {
            Produit produit = produitDAO.getProduitById(ligne.getProduitId());
            String productName = (produit != null) ? produit.getNom() : "Produit supprimé (ID: " + ligne.getProduitId() + ")";

            SaleItemDetail item = new SaleItemDetail(
                productName,
                ligne.getQuantite(),
                ligne.getPrixUnitaire()
            );
            saleItems.add(item);
        }

        saleDetailsBox.setVisible(true);
    }

    private void hideSaleDetails() {
        saleDetailsBox.setVisible(false);
        saleItems.clear();
    }

    @FXML
    private void refreshSales() {
        loadSales();
        hideSaleDetails();
        clearMessage();
    }

    @FXML
    private void deleteSale() {
        Vente selected = salesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("Veuillez sélectionner une vente à supprimer.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la vente");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer la vente ID " + selected.getId() + " ?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            ligneVenteDAO.deleteLignesVenteByVenteId(selected.getId());
            venteDAO.deleteVente(selected.getId());

            showMessage("Vente supprimée avec succès.");
            refreshSales();
        }
    }

    private void denyAccessAndClose() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Accès refusé");
            alert.setHeaderText("Accès administrateur requis");
            alert.setContentText("Vous n'avez pas les droits pour accéder à cette fonctionnalité.");
            alert.showAndWait();

            Stage stage = (Stage) salesTable.getScene().getWindow();
            stage.close();
        });
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
    }

    private void clearMessage() {
        messageLabel.setText("");
    }

    public static class SaleItemDetail {
        private String productName;
        private double quantity;
        private double unitPrice;

        public SaleItemDetail(String productName, double quantity, double unitPrice) {
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public String getProductName() {
            return productName;
        }

        public double getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public double getTotal() {
            return quantity * unitPrice;
        }
    }
}
