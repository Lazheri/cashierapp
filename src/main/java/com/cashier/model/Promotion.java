package com.cashier.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Promotion {
    private int id;
    private String code;
    private String description;
    private String type; // "PERCENT" or "FIXED"
    private double value;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Integer usageCap;
    private int currentUsage;
    private double minimumBasket;
    private boolean active;

    public Promotion(int id, String code, String description, String type, double value,
                     LocalDateTime validFrom, LocalDateTime validTo, Integer usageCap,
                     int currentUsage, double minimumBasket, boolean active) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.type = type;
        this.value = value;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.usageCap = usageCap;
        this.currentUsage = currentUsage;
        this.minimumBasket = minimumBasket;
        this.active = active;
    }

    public Promotion(String code, String description, String type, double value,
                     LocalDateTime validFrom, LocalDateTime validTo, Integer usageCap,
                     double minimumBasket, boolean active) {
        this.code = code;
        this.description = description;
        this.type = type;
        this.value = value;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.usageCap = usageCap;
        this.currentUsage = 0;
        this.minimumBasket = minimumBasket;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public Integer getUsageCap() {
        return usageCap;
    }

    public void setUsageCap(Integer usageCap) {
        this.usageCap = usageCap;
    }

    public int getCurrentUsage() {
        return currentUsage;
    }

    public void setCurrentUsage(int currentUsage) {
        this.currentUsage = currentUsage;
    }

    public double getMinimumBasket() {
        return minimumBasket;
    }

    public void setMinimumBasket(double minimumBasket) {
        this.minimumBasket = minimumBasket;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Promotion{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", value=" + value +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                ", usageCap=" + usageCap +
                ", currentUsage=" + currentUsage +
                ", minimumBasket=" + minimumBasket +
                ", active=" + active +
                '}';
    }
}
