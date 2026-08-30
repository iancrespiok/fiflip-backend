package com.fiflip.backend.budget;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pricing_items")
public class PricingItem {

    @Id
    @Column(name = "item_key")
    private String key;

    @Column(nullable = false)
    private String label;

    @Column(name = "item_group", nullable = false)
    private String group;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PricingUnit unit;

    @Column(nullable = false)
    private double price;

    protected PricingItem() {
    }

    public PricingItem(String key, String label, String group, PricingUnit unit, double price) {
        this.key = key;
        this.label = label;
        this.group = group;
        this.unit = unit;
        this.price = price;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public String getGroup() {
        return group;
    }

    public PricingUnit getUnit() {
        return unit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
