package com.ccms.transactionreport.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "MERCHANT")
public class Merchant {

    @Id
    @Column(name = "MERCHANT_ID")
    private Long merchantId;

    @Column(name = "MERCHANT_NAME", nullable = false, length = 100)
    private String merchantName;

    @Column(name = "CATEGORY", nullable = false, length = 50)
    private String category;

    @Column(name = "LOCATION", nullable = false, length = 100)
    private String location;

    public Merchant() {
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}