package com.cartrawler.assessment.car;

public record CarResult(String description, String supplierName, String sippCode, double rentalCost,
                        FuelPolicy fuelPolicy) {
    public enum FuelPolicy {
        FULLFULL,
        FULLEMPTY
    }

    public String toString() {
        return this.supplierName + " : " +
                this.description + " : " +
                this.sippCode + " : " +
                this.rentalCost + " : " +
                this.fuelPolicy;
    }
}
