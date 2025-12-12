package com.cashier.service;

import com.cashier.dao.PromotionDAO;
import com.cashier.model.Promotion;
import java.time.LocalDateTime;

public class PromotionService {
    private PromotionDAO promotionDAO;

    public PromotionService() {
        this.promotionDAO = new PromotionDAO();
    }

    public class ValidationResult {
        public boolean isValid;
        public String errorMessage;
        public Promotion promotion;

        public ValidationResult(boolean isValid, String errorMessage, Promotion promotion) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
            this.promotion = promotion;
        }
    }

    public ValidationResult validatePromoCode(String code, double basketTotal) {
        if (code == null || code.trim().isEmpty()) {
            return new ValidationResult(false, "Code de promotion vide", null);
        }

        Promotion promotion = promotionDAO.getPromotionByCode(code.trim());
        if (promotion == null) {
            return new ValidationResult(false, "Code de promotion invalide", null);
        }

        if (!promotion.isActive()) {
            return new ValidationResult(false, "Ce code de promotion est désactivé", null);
        }

        LocalDateTime now = LocalDateTime.now();
        if (promotion.getValidFrom() != null && now.isBefore(promotion.getValidFrom())) {
            return new ValidationResult(false, "Ce code n'est pas encore valide", null);
        }

        if (promotion.getValidTo() != null && now.isAfter(promotion.getValidTo())) {
            return new ValidationResult(false, "Ce code de promotion a expiré", null);
        }

        if (promotion.getUsageCap() != null && promotion.getCurrentUsage() >= promotion.getUsageCap()) {
            return new ValidationResult(false, "Ce code de promotion a atteint sa limite d'utilisation", null);
        }

        if (basketTotal < promotion.getMinimumBasket()) {
            return new ValidationResult(false, "Panier insuffisant. Minimum: " + promotion.getMinimumBasket() + " TND", null);
        }

        return new ValidationResult(true, "", promotion);
    }

    public double calculateDiscount(Promotion promotion, double amount) {
        if (promotion == null) {
            return 0;
        }

        if ("PERCENT".equalsIgnoreCase(promotion.getType())) {
            return amount * (promotion.getValue() / 100.0);
        } else if ("FIXED".equalsIgnoreCase(promotion.getType())) {
            return promotion.getValue();
        }

        return 0;
    }

    public void recordPromotionUsage(int promotionId) {
        promotionDAO.incrementUsage(promotionId);
    }
}
