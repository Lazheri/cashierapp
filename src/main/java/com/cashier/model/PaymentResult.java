package com.cashier.model;

public class PaymentResult {
    private boolean success;
    private String paymentMethod;
    private double amountPaid;
    private double changeDue;
    private String paymentReference;
    private String errorMessage;

    public PaymentResult(boolean success, String paymentMethod, double amountPaid, double changeDue, String paymentReference) {
        this.success = success;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.changeDue = changeDue;
        this.paymentReference = paymentReference;
    }

    public PaymentResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public double getChangeDue() {
        return changeDue;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
