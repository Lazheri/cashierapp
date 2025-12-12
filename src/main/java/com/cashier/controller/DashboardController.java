package com.cashier.controller;

import com.cashier.dao.ProduitDAO;
import com.cashier.dao.VenteDAO;
import com.cashier.dao.LigneVenteDAO;
import com.cashier.dao.CustomerDAO;
import com.cashier.model.Vente;
import com.cashier.model.Produit;
import com.cashier.model.Customer;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private Label dailySalesLabel;
    @FXML private Label weeklySalesLabel;
    @FXML private Label activeCustomersLabel;
    @FXML private Label lowStockLabel;

    @FXML private TableView<TopProductItem> topProductsTable;
    @FXML private TableColumn<TopProductItem, String> productNameColumn;
    @FXML private TableColumn<TopProductItem, Double> quantitySoldColumn;
    @FXML private TableColumn<TopProductItem, Double> revenueColumn;

    @FXML private TableView<TopCustomerItem> topCustomersTable;
    @FXML private TableColumn<TopCustomerItem, String> customerNameColumn;
    @FXML private TableColumn<TopCustomerItem, Double> totalSpentColumn;
    @FXML private TableColumn<TopCustomerItem, Integer> loyaltyPointsColumn;
    @FXML private TableColumn<TopCustomerItem, Integer> purchaseCountColumn;

    @FXML private TableView<LowStockItem> lowStockTable;
    @FXML private TableColumn<LowStockItem, String> lowStockProductColumn;
    @FXML private TableColumn<LowStockItem, Double> stockQuantityColumn;

    private VenteDAO venteDAO;
    private ProduitDAO produitDAO;
    private LigneVenteDAO ligneVenteDAO;
    private CustomerDAO customerDAO;

    private ObservableList<TopProductItem> topProducts;
    private ObservableList<TopCustomerItem> topCustomers;
    private ObservableList<LowStockItem> lowStockItems;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        venteDAO = new VenteDAO();
        produitDAO = new ProduitDAO();
        ligneVenteDAO = new LigneVenteDAO();
        customerDAO = new CustomerDAO();

        topProducts = FXCollections.observableArrayList();
        topCustomers = FXCollections.observableArrayList();
        lowStockItems = FXCollections.observableArrayList();

        setupTopProductsTable();
        setupTopCustomersTable();
        setupLowStockTable();

        startDatePicker.setValue(LocalDate.now().minusDays(30));
        endDatePicker.setValue(LocalDate.now());

        refreshDashboard();
    }

    private void setupTopProductsTable() {
        productNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));
        quantitySoldColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getQuantitySold()).asObject());
        revenueColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getRevenue()).asObject());
        topProductsTable.setItems(topProducts);
    }

    private void setupTopCustomersTable() {
        customerNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerName()));
        totalSpentColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotalSpent()).asObject());
        loyaltyPointsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getLoyaltyPoints()).asObject());
        purchaseCountColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPurchaseCount()).asObject());
        topCustomersTable.setItems(topCustomers);
    }

    private void setupLowStockTable() {
        lowStockProductColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProductName()));
        stockQuantityColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getQuantity()).asObject());
        lowStockTable.setItems(lowStockItems);
    }

    @FXML
    private void handleApplyFilter() {
        refreshDashboard();
    }

    @FXML
    private void handleResetFilter() {
        startDatePicker.setValue(LocalDate.now().minusDays(30));
        endDatePicker.setValue(LocalDate.now());
        refreshDashboard();
    }

    private void refreshDashboard() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        updateDailySales();
        updateWeeklySales();
        updateActiveCustomers(startDate, endDate);
        updateLowStockCount();
        updateTopProducts(startDate, endDate);
        updateTopCustomers(startDate, endDate);
    }

    private void updateDailySales() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        List<Vente> ventes = venteDAO.getAllVentes();
        double dailySales = ventes.stream()
            .filter(v -> !v.getDateVente().isBefore(startOfDay) && !v.getDateVente().isAfter(endOfDay))
            .mapToDouble(Vente::getTotal)
            .sum();

        dailySalesLabel.setText(String.format("%.3f TND", dailySales));
    }

    private void updateWeeklySales() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDateTime startOfWeek = weekStart.atStartOfDay();
        LocalDateTime endOfWeek = today.atTime(23, 59, 59);

        List<Vente> ventes = venteDAO.getAllVentes();
        double weeklySales = ventes.stream()
            .filter(v -> !v.getDateVente().isBefore(startOfWeek) && !v.getDateVente().isAfter(endOfWeek))
            .mapToDouble(Vente::getTotal)
            .sum();

        weeklySalesLabel.setText(String.format("%.3f TND", weeklySales));
    }

    private void updateActiveCustomers(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Vente> ventes = venteDAO.getAllVentes();
        long activeCustomers = ventes.stream()
            .filter(v -> !v.getDateVente().isBefore(startDateTime) && !v.getDateVente().isAfter(endDateTime))
            .filter(v -> v.getCustomerId() != null)
            .map(Vente::getCustomerId)
            .distinct()
            .count();

        activeCustomersLabel.setText(String.valueOf(activeCustomers));
    }

    private void updateLowStockCount() {
        List<Produit> produits = produitDAO.getAllProduits();
        long lowStock = produits.stream()
            .filter(p -> p.getQuantite() < 10)
            .count();
        lowStockLabel.setText(String.valueOf(lowStock));
    }

    private void updateTopProducts(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Vente> allVentes = venteDAO.getAllVentes();
        List<Vente> filteredVentes = allVentes.stream()
            .filter(v -> !v.getDateVente().isBefore(startDateTime) && !v.getDateVente().isAfter(endDateTime))
            .collect(Collectors.toList());

        Map<Integer, TopProductData> productData = new HashMap<>();

        for (Vente vente : filteredVentes) {
            List<com.cashier.model.LigneVente> lignes = ligneVenteDAO.getLignesVenteByVenteId(vente.getId());
            for (com.cashier.model.LigneVente ligne : lignes) {
                Produit produit = produitDAO.getProduitById(ligne.getProduitId());
                if (produit != null) {
                    productData.computeIfAbsent(ligne.getProduitId(),
                        k -> new TopProductData(produit.getNom())).add(ligne.getQuantite(), ligne.getPrixUnitaire() * ligne.getQuantite());
                }
            }
        }

        topProducts.clear();
        productData.values().stream()
            .sorted((a, b) -> Double.compare(b.getRevenue(), a.getRevenue()))
            .limit(10)
            .map(data -> new TopProductItem(data.getProductName(), data.getQuantitySold(), data.getRevenue()))
            .forEach(topProducts::add);
    }

    private void updateTopCustomers(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Vente> allVentes = venteDAO.getAllVentes();
        List<Vente> filteredVentes = allVentes.stream()
            .filter(v -> !v.getDateVente().isBefore(startDateTime) && !v.getDateVente().isAfter(endDateTime))
            .filter(v -> v.getCustomerId() != null)
            .collect(Collectors.toList());

        Map<Integer, TopCustomerData> customerData = new HashMap<>();

        for (Vente vente : filteredVentes) {
            Integer customerId = vente.getCustomerId();
            Customer customer = customerDAO.getCustomerById(customerId);
            if (customer != null) {
                customerData.computeIfAbsent(customerId,
                    k -> new TopCustomerData(customer.getName(), customer.getLoyaltyPoints()))
                    .addPurchase(vente.getTotal());
            }
        }

        topCustomers.clear();
        customerDAO.getTopCustomers(10).forEach(customer -> {
            if (customerData.containsKey(customer.getId())) {
                TopCustomerData data = customerData.get(customer.getId());
                topCustomers.add(new TopCustomerItem(
                    customer.getName(),
                    data.getTotalSpent(),
                    customer.getLoyaltyPoints(),
                    data.getPurchaseCount()
                ));
            }
        });
        
        updateLowStockItems();
    }

    private void updateLowStockItems() {
        lowStockItems.clear();
        List<Produit> produits = produitDAO.getAllProduits();
        produits.stream()
            .filter(p -> p.getQuantite() < 10)
            .map(p -> new LowStockItem(p.getNom(), p.getQuantite()))
            .forEach(lowStockItems::add);
    }

    public static class TopProductItem {
        private String productName;
        private double quantitySold;
        private double revenue;

        public TopProductItem(String productName, double quantitySold, double revenue) {
            this.productName = productName;
            this.quantitySold = quantitySold;
            this.revenue = revenue;
        }

        public String getProductName() { return productName; }
        public double getQuantitySold() { return quantitySold; }
        public double getRevenue() { return revenue; }
    }

    public static class TopCustomerItem {
        private String customerName;
        private double totalSpent;
        private int loyaltyPoints;
        private int purchaseCount;

        public TopCustomerItem(String customerName, double totalSpent, int loyaltyPoints, int purchaseCount) {
            this.customerName = customerName;
            this.totalSpent = totalSpent;
            this.loyaltyPoints = loyaltyPoints;
            this.purchaseCount = purchaseCount;
        }

        public String getCustomerName() { return customerName; }
        public double getTotalSpent() { return totalSpent; }
        public int getLoyaltyPoints() { return loyaltyPoints; }
        public int getPurchaseCount() { return purchaseCount; }
    }

    public static class LowStockItem {
        private String productName;
        private double quantity;

        public LowStockItem(String productName, double quantity) {
            this.productName = productName;
            this.quantity = quantity;
        }

        public String getProductName() { return productName; }
        public double getQuantity() { return quantity; }
    }

    private static class TopProductData {
        private String productName;
        private double quantitySold;
        private double revenue;

        public TopProductData(String productName) {
            this.productName = productName;
            this.quantitySold = 0;
            this.revenue = 0;
        }

        public void add(double quantity, double price) {
            this.quantitySold += quantity;
            this.revenue += price;
        }

        public String getProductName() { return productName; }
        public double getQuantitySold() { return quantitySold; }
        public double getRevenue() { return revenue; }
    }

    private static class TopCustomerData {
        private String customerName;
        private int loyaltyPoints;
        private double totalSpent;
        private int purchaseCount;

        public TopCustomerData(String customerName, int loyaltyPoints) {
            this.customerName = customerName;
            this.loyaltyPoints = loyaltyPoints;
            this.totalSpent = 0;
            this.purchaseCount = 0;
        }

        public void addPurchase(double amount) {
            this.totalSpent += amount;
            this.purchaseCount++;
        }

        public String getCustomerName() { return customerName; }
        public int getLoyaltyPoints() { return loyaltyPoints; }
        public double getTotalSpent() { return totalSpent; }
        public int getPurchaseCount() { return purchaseCount; }
    }
}
