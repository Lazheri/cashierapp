package com.cashier.controller;

import com.cashier.dao.PromotionDAO;
import com.cashier.model.Promotion;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class PromotionManagementController implements Initializable {

    @FXML private TableView<Promotion> promotionTable;
    @FXML private TableColumn<Promotion, String> codeColumn;
    @FXML private TableColumn<Promotion, String> descriptionColumn;
    @FXML private TableColumn<Promotion, String> typeColumn;
    @FXML private TableColumn<Promotion, String> valueColumn;
    @FXML private TableColumn<Promotion, String> validFromColumn;
    @FXML private TableColumn<Promotion, String> validToColumn;
    @FXML private TableColumn<Promotion, String> usageColumn;
    @FXML private TableColumn<Promotion, String> activeColumn;

    @FXML private TextField codeField;
    @FXML private TextField descriptionField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField valueField;
    @FXML private TextField minimumBasketField;
    @FXML private DatePicker validFromDatePicker;
    @FXML private DatePicker validToDatePicker;
    @FXML private TextField usageCapField;
    @FXML private CheckBox activeCheckBox;
    @FXML private Label messageLabel;

    private PromotionDAO promotionDAO;
    private ObservableList<Promotion> promotions;
    private Promotion selectedPromotion = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        promotionDAO = new PromotionDAO();
        promotions = FXCollections.observableArrayList();

        setupTableColumns();
        loadPromotions();

        promotionTable.setOnMouseClicked(e -> selectPromotion());
        
        clearMessage();
    }

    private void setupTableColumns() {
        codeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCode()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        valueColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("%.3f", cellData.getValue().getValue())));
        validFromColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getValidFrom() != null ? cellData.getValue().getValidFrom().toString() : "N/A"
        ));
        validToColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getValidTo() != null ? cellData.getValue().getValidTo().toString() : "N/A"
        ));
        usageColumn.setCellValueFactory(cellData -> {
            Promotion p = cellData.getValue();
            String usage = p.getCurrentUsage() + "";
            if (p.getUsageCap() != null) {
                usage += " / " + p.getUsageCap();
            } else {
                usage += " / ∞";
            }
            return new SimpleStringProperty(usage);
        });
        activeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isActive() ? "Oui" : "Non"));

        promotionTable.setItems(promotions);
    }

    private void loadPromotions() {
        promotions.clear();
        promotions.addAll(promotionDAO.getAllPromotions());
    }

    private void selectPromotion() {
        selectedPromotion = promotionTable.getSelectionModel().getSelectedItem();
        if (selectedPromotion != null) {
            populateForm(selectedPromotion);
        }
    }

    private void populateForm(Promotion promotion) {
        codeField.setText(promotion.getCode());
        descriptionField.setText(promotion.getDescription());
        typeCombo.setValue(promotion.getType());
        valueField.setText(String.valueOf(promotion.getValue()));
        minimumBasketField.setText(String.valueOf(promotion.getMinimumBasket()));
        
        if (promotion.getValidFrom() != null) {
            validFromDatePicker.setValue(promotion.getValidFrom().toLocalDate());
        } else {
            validFromDatePicker.setValue(null);
        }
        
        if (promotion.getValidTo() != null) {
            validToDatePicker.setValue(promotion.getValidTo().toLocalDate());
        } else {
            validToDatePicker.setValue(null);
        }
        
        if (promotion.getUsageCap() != null) {
            usageCapField.setText(String.valueOf(promotion.getUsageCap()));
        } else {
            usageCapField.setText("");
        }
        
        activeCheckBox.setSelected(promotion.isActive());
    }

    private void clearFormFields() {
        codeField.clear();
        descriptionField.clear();
        typeCombo.setValue("PERCENT");
        valueField.clear();
        minimumBasketField.clear();
        validFromDatePicker.setValue(null);
        validToDatePicker.setValue(null);
        usageCapField.clear();
        activeCheckBox.setSelected(true);
        selectedPromotion = null;
    }

    @FXML
    private void addPromotion() {
        if (!validateForm()) {
            return;
        }

        try {
            String code = codeField.getText().trim();
            String description = descriptionField.getText().trim();
            String type = typeCombo.getValue();
            double value = Double.parseDouble(valueField.getText().trim());
            double minimumBasket = Double.parseDouble(minimumBasketField.getText().trim());
            
            LocalDateTime validFrom = null;
            if (validFromDatePicker.getValue() != null) {
                validFrom = validFromDatePicker.getValue().atStartOfDay();
            }
            
            LocalDateTime validTo = null;
            if (validToDatePicker.getValue() != null) {
                validTo = validToDatePicker.getValue().atTime(23, 59, 59);
            }
            
            Integer usageCap = null;
            if (!usageCapField.getText().trim().isEmpty()) {
                usageCap = Integer.parseInt(usageCapField.getText().trim());
            }
            
            boolean active = activeCheckBox.isSelected();

            Promotion promotion = new Promotion(code, description, type, value, validFrom, validTo, usageCap, minimumBasket, active);
            double promotionId = promotionDAO.addPromotion(promotion);

            if (promotionId > 0) {
                showMessage("Promotion ajoutée avec succès");
                clearFormFields();
                loadPromotions();
            } else {
                showMessage("Erreur lors de l'ajout de la promotion");
            }
        } catch (NumberFormatException e) {
            showMessage("Erreur: Valeur invalide - " + e.getMessage());
        }
    }

    @FXML
    private void updatePromotion() {
        if (selectedPromotion == null) {
            showMessage("Veuillez sélectionner une promotion");
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            selectedPromotion.setCode(codeField.getText().trim());
            selectedPromotion.setDescription(descriptionField.getText().trim());
            selectedPromotion.setType(typeCombo.getValue());
            selectedPromotion.setValue(Double.parseDouble(valueField.getText().trim()));
            selectedPromotion.setMinimumBasket(Double.parseDouble(minimumBasketField.getText().trim()));
            
            LocalDateTime validFrom = null;
            if (validFromDatePicker.getValue() != null) {
                validFrom = validFromDatePicker.getValue().atStartOfDay();
            }
            selectedPromotion.setValidFrom(validFrom);
            
            LocalDateTime validTo = null;
            if (validToDatePicker.getValue() != null) {
                validTo = validToDatePicker.getValue().atTime(23, 59, 59);
            }
            selectedPromotion.setValidTo(validTo);
            
            Integer usageCap = null;
            if (!usageCapField.getText().trim().isEmpty()) {
                usageCap = Integer.parseInt(usageCapField.getText().trim());
            }
            selectedPromotion.setUsageCap(usageCap);
            
            selectedPromotion.setActive(activeCheckBox.isSelected());

            promotionDAO.updatePromotion(selectedPromotion);
            showMessage("Promotion mise à jour avec succès");
            clearFormFields();
            loadPromotions();
        } catch (NumberFormatException e) {
            showMessage("Erreur: Valeur invalide - " + e.getMessage());
        }
    }

    @FXML
    private void deletePromotion() {
        if (selectedPromotion == null) {
            showMessage("Veuillez sélectionner une promotion");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la promotion");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette promotion?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            promotionDAO.deletePromotion(selectedPromotion.getId());
            showMessage("Promotion supprimée avec succès");
            clearFormFields();
            loadPromotions();
        }
    }

    @FXML
    private void clearForm() {
        clearFormFields();
        clearMessage();
    }

    private boolean validateForm() {
        if (codeField.getText().trim().isEmpty()) {
            showMessage("Erreur: Le code est obligatoire");
            return false;
        }

        if (typeCombo.getValue() == null) {
            showMessage("Erreur: Le type est obligatoire");
            return false;
        }

        try {
            Double.parseDouble(valueField.getText().trim());
        } catch (NumberFormatException e) {
            showMessage("Erreur: La valeur doit être un nombre");
            return false;
        }

        try {
            Double.parseDouble(minimumBasketField.getText().trim());
        } catch (NumberFormatException e) {
            showMessage("Erreur: Le minimum du panier doit être un nombre");
            return false;
        }

        return true;
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> messageLabel.setText(""));
        delay.play();
    }

    private void clearMessage() {
        messageLabel.setText("");
    }
}
