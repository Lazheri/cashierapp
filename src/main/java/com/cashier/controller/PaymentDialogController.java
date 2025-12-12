package com.cashier.controller;

import com.cashier.model.PaymentResult;
import com.cashier.service.PaymentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PaymentDialogController implements Initializable {

    @FXML private Label totalLabel;
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private VBox cashPaymentBox;
    @FXML private TextField cashAmountField;
    @FXML private Label changeLabel;
    @FXML private VBox cardPaymentBox;
    @FXML private TextField cardNumberField;
    @FXML private ComboBox<String> cardBrandCombo;
    @FXML private VBox digitalPaymentBox;
    @FXML private TextField transactionReferenceField;
    @FXML private Label errorLabel;

    private double total;
    private PaymentResult result;
    private PaymentService paymentService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paymentService = new PaymentService();
        
        paymentMethodCombo.setItems(FXCollections.observableArrayList("Espèces", "Carte Bancaire", "Paiement Digital"));
        paymentMethodCombo.valueProperty().addListener((obs, oldVal, newVal) -> handlePaymentMethodChange(newVal));
        
        cardBrandCombo.setItems(FXCollections.observableArrayList("Visa", "MasterCard", "American Express", "Autre"));
        
        cashAmountField.textProperty().addListener((obs, oldVal, newVal) -> updateChange());
        
        errorLabel.setText("");
    }

    public void setTotal(double total) {
        this.total = total;
        totalLabel.setText(String.format("%.3f TND", total));
    }

    private void handlePaymentMethodChange(String method) {
        cashPaymentBox.setVisible(false);
        cashPaymentBox.setManaged(false);
        cardPaymentBox.setVisible(false);
        cardPaymentBox.setManaged(false);
        digitalPaymentBox.setVisible(false);
        digitalPaymentBox.setManaged(false);
        errorLabel.setText("");

        if ("Espèces".equals(method)) {
            cashPaymentBox.setVisible(true);
            cashPaymentBox.setManaged(true);
            cashAmountField.requestFocus();
        } else if ("Carte Bancaire".equals(method)) {
            cardPaymentBox.setVisible(true);
            cardPaymentBox.setManaged(true);
            cardNumberField.requestFocus();
        } else if ("Paiement Digital".equals(method)) {
            digitalPaymentBox.setVisible(true);
            digitalPaymentBox.setManaged(true);
            transactionReferenceField.requestFocus();
        }
    }

    private void updateChange() {
        try {
            double amountPaid = Double.parseDouble(cashAmountField.getText());
            double change = paymentService.calculateChange(total, amountPaid);
            changeLabel.setText(String.format("%.3f TND", change));
            if (change < 0) {
                changeLabel.setStyle("-fx-text-fill: red;");
            } else {
                changeLabel.setStyle("-fx-text-fill: green;");
            }
        } catch (NumberFormatException e) {
            changeLabel.setText("0.000 TND");
            changeLabel.setStyle("");
        }
    }

    @FXML
    private void confirmPayment() {
        errorLabel.setText("");
        String method = paymentMethodCombo.getValue();
        
        if (method == null) {
            errorLabel.setText("Veuillez sélectionner un mode de paiement.");
            return;
        }

        if ("Espèces".equals(method)) {
            handleCashPayment();
        } else if ("Carte Bancaire".equals(method)) {
            handleCardPayment();
        } else if ("Paiement Digital".equals(method)) {
            handleDigitalPayment();
        }
    }

    private void handleCashPayment() {
        try {
            double amountPaid = Double.parseDouble(cashAmountField.getText());
            String error = paymentService.validateCashPayment(total, amountPaid);
            if (error != null) {
                errorLabel.setText(error);
                return;
            }
            double change = paymentService.calculateChange(total, amountPaid);
            result = new PaymentResult(true, "Espèces", amountPaid, change, null);
            closeDialog();
        } catch (NumberFormatException e) {
            errorLabel.setText("Montant invalide.");
        }
    }

    private void handleCardPayment() {
        String cardNumber = cardNumberField.getText();
        String cardBrand = cardBrandCombo.getValue();
        
        String error = paymentService.validateCardPayment(cardNumber, cardBrand);
        if (error != null) {
            errorLabel.setText(error);
            return;
        }
        
        String maskedNumber = paymentService.maskCardNumber(cardNumber);
        String reference = cardBrand + " - " + maskedNumber;
        
        result = new PaymentResult(true, "Carte Bancaire", total, 0.0, reference);
        closeDialog();
    }

    private void handleDigitalPayment() {
        String transactionRef = transactionReferenceField.getText();
        
        String error = paymentService.validateDigitalPayment(transactionRef);
        if (error != null) {
            errorLabel.setText(error);
            return;
        }
        
        result = new PaymentResult(true, "Paiement Digital", total, 0.0, transactionRef);
        closeDialog();
    }

    @FXML
    private void cancelPayment() {
        result = new PaymentResult(false, "Paiement annulé");
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) totalLabel.getScene().getWindow();
        stage.close();
    }

    public PaymentResult getResult() {
        return result;
    }
}
