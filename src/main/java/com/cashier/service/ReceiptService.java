package com.cashier.service;

import com.cashier.controller.MainController;
import com.cashier.model.Vente;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReceiptService {

    public String formatReceipt(int saleId, Vente vente, List<MainController.CartItem> cartItems) {
        StringBuilder receipt = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        receipt.append("========================================\n");
        receipt.append("           REÇU DE VENTE\n");
        receipt.append("========================================\n\n");
        receipt.append("ID de Vente: ").append(saleId).append("\n");
        receipt.append("Date: ").append(vente.getDateVente().format(formatter)).append("\n\n");
        receipt.append("----------------------------------------\n");
        receipt.append("ARTICLES\n");
        receipt.append("----------------------------------------\n");
        
        for (MainController.CartItem item : cartItems) {
            receipt.append(String.format("%-20s x %.2f\n", item.getProductName(), item.getQuantity()));
            receipt.append(String.format("  %.3f TND x %.3f = %.3f TND\n", 
                item.getUnitPrice(), item.getQuantity(), item.getTotal()));
        }
        
        receipt.append("----------------------------------------\n");
        receipt.append(String.format("TOTAL: %.3f TND\n", vente.getTotal()));
        receipt.append("========================================\n\n");
        
        receipt.append("MODE DE PAIEMENT: ").append(vente.getPaymentMethod() != null ? vente.getPaymentMethod() : "N/A").append("\n");
        
        if ("Espèces".equals(vente.getPaymentMethod())) {
            receipt.append(String.format("Montant reçu: %.3f TND\n", vente.getAmountPaid()));
            receipt.append(String.format("Monnaie: %.3f TND\n", vente.getChangeDue()));
        } else if (vente.getPaymentReference() != null && !vente.getPaymentReference().isEmpty()) {
            receipt.append("Référence: ").append(vente.getPaymentReference()).append("\n");
        }
        
        receipt.append("\n========================================\n");
        receipt.append("      Merci de votre visite!\n");
        receipt.append("========================================\n");
        
        return receipt.toString();
    }

    public boolean printReceipt(String receiptContent) {
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        
        if (printerJob == null) {
            showError("Aucune imprimante disponible", "Impossible de détecter une imprimante.");
            return false;
        }

        Text text = new Text(receiptContent);
        text.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10pt;");
        
        TextFlow textFlow = new TextFlow(text);
        textFlow.setPrefWidth(400);

        boolean success = printerJob.printPage(textFlow);
        
        if (success) {
            printerJob.endJob();
            return true;
        } else {
            showError("Erreur d'impression", "Échec de l'impression du reçu.");
            return false;
        }
    }

    public boolean exportToPdf(String receiptContent, int saleId) {
        try {
            String fileName = "Recu_Vente_" + saleId + ".txt";
            File file = new File(fileName);
            
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(receiptContent);
            }
            
            showInfo("Export réussi", "Le reçu a été exporté vers: " + file.getAbsolutePath());
            return true;
        } catch (IOException e) {
            showError("Erreur d'export", "Impossible d'exporter le reçu: " + e.getMessage());
            return false;
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
