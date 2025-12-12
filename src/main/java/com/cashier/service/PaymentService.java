package com.cashier.service;

public class PaymentService {

    public double calculateChange(double total, double amountPaid) {
        return amountPaid - total;
    }

    public String validateCashPayment(double total, double amountPaid) {
        if (amountPaid < total) {
            return "Le montant payé est insuffisant.";
        }
        return null;
    }

    public String validateCardPayment(String cardNumber, String cardBrand) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return "Le numéro de carte est requis.";
        }
        if (cardBrand == null || cardBrand.trim().isEmpty()) {
            return "La marque de la carte est requise.";
        }
        if (cardNumber.length() < 4) {
            return "Numéro de carte invalide.";
        }
        return null;
    }

    public String validateDigitalPayment(String transactionReference) {
        if (transactionReference == null || transactionReference.trim().isEmpty()) {
            return "La référence de transaction est requise.";
        }
        if (transactionReference.length() < 4) {
            return "Référence de transaction invalide.";
        }
        return null;
    }

    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "****-****-****-" + last4;
    }

    public String formatPaymentReference(String method, String reference) {
        if (reference == null || reference.trim().isEmpty()) {
            return null;
        }
        return method + ": " + reference;
    }
}
