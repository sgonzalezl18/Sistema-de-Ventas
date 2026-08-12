/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema_de_ventas.model.entity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa el encabezado principal de la factura.
 */
public class Invoice {

    private int invoiceId;
    private String invoiceNumber;
    private String customerName;
    private String customerTaxId; // NIT o Identificación Fiscal
    private LocalDate issueDate;
    private double totalAmount;
    
    // Lista que guarda los productos/ítems agregados
    private List<InvoiceDetail> details;

    // Constructor
    public Invoice() {
        this.details = new ArrayList<>();
        this.issueDate = LocalDate.now(); // Asigna la fecha actual automáticamente
        this.totalAmount = 0.0;
    }

    // Método para agregar un detalle a la lista y actualizar el total general
    public void addDetail(InvoiceDetail detail) {
        this.details.add(detail);
        calculateTotal();
    }

    // Método para remover un detalle por su índice y re-calcular el total
    public void removeDetail(int index) {
        if (index >= 0 && index < details.size()) {
            this.details.remove(index);
            calculateTotal();
        }
    }

    // Recorre todos los ítems y suma sus subtotales
    public void calculateTotal() {
        this.totalAmount = 0.0;
        for (InvoiceDetail detail : details) {
            this.totalAmount += detail.getSubtotal();
        }
    }

    // --- GETTERS Y SETTERS ---

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerTaxId() {
        return customerTaxId;
    }

    public void setCustomerTaxId(String customerTaxId) {
        this.customerTaxId = customerTaxId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public List<InvoiceDetail> getDetails() {
        return details;
    }
    
}
