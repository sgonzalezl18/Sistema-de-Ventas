package com.mycompany.sistema_de_ventas.model.entity;

public class InvoiceDetail {

    private int productId;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double subtotal;

    public InvoiceDetail(int productId, String productName, int quantity, double unitPrice) {
        setProductId(productId);
        setProductName(productName);
        setQuantity(quantity);
        setUnitPrice(unitPrice);
    }

    private double calculateSubtotal() {
        return this.quantity * this.unitPrice;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        if (productId > 0) {
            this.productId = productId;
        }
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        if (productName != null && !productName.trim().isEmpty()) {
            this.productName = productName.trim().toUpperCase();
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
            this.subtotal = calculateSubtotal();
        }
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        if (unitPrice > 0) {
            this.unitPrice = unitPrice;
            this.subtotal = calculateSubtotal();
        }
    }

    public double getSubtotal() {
        return subtotal;
    }
}