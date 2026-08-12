/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema_de_ventas.model.entity;

/**
 *
 * @author saimo
 */
public class InvoiceDetail {
    /**
    * Representa una fila individual dentro de la factura (un ítem comprado).
    */
    // Atributos privados (encapsulamiento)
    private int productId;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double subtotal;
    
    // Constructor para inicializar una fila
    public InvoiceDetail(int productId, String productName, int quantity, double unitPrice){
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = calculateSubtotal();
    }
    
    // Método interno para calcular el subtotal (Cantidad x Precio)
    private double calculateSubtotal() {
        return this.quantity * this.unitPrice;
    }
    
    // --- GETTERS Y SETTERS ---
    // Sirven para consultar o modificar los valores de forma segura

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    // Si cambia la cantidad, debemos recalcular el subtotal
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = calculateSubtotal();
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    // Si cambia el precio, debemos recalcular el subtotal
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.subtotal = calculateSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }
}
