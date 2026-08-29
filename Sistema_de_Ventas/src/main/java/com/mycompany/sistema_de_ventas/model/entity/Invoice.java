package com.mycompany.sistema_de_ventas.model.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Invoice {

    private int invoiceId;
    private String invoiceNumber;
    private String customerName;
    private String customerTaxId;
    private LocalDate issueDate;
    private double totalAmount;
    private List<InvoiceDetail> details;

    public Invoice() {
        this.details = new ArrayList<>();
        this.issueDate = LocalDate.now();
        this.totalAmount = 0.0;
    }

    public void addDetail(InvoiceDetail detail) {
        if (detail != null) {
            this.details.add(detail);
            calculateTotal();
        }
    }

    public void removeDetail(int index) {
        if (index >= 0 && index < details.size()) {
            this.details.remove(index);
            calculateTotal();
        }
    }

    public void calculateTotal() {
        this.totalAmount = 0.0;
        for (InvoiceDetail detail : details) {
            this.totalAmount += detail.getSubtotal();
        }
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        if (invoiceId > 0) {
            this.invoiceId = invoiceId;
        }
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        if (invoiceNumber != null && !invoiceNumber.trim().isEmpty()) {
            this.invoiceNumber = invoiceNumber.trim().toUpperCase();
        }
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        if (customerName != null && customerName.trim().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            this.customerName = customerName.trim().toUpperCase();
        }
    }

    public String getCustomerTaxId() {
        return customerTaxId;
    }

    public void setCustomerTaxId(String customerTaxId) {
        if (customerTaxId != null && customerTaxId.trim().matches("^[a-zA-Z0-9-]+$")) {
            this.customerTaxId = customerTaxId.trim().toUpperCase();
        }
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        if (issueDate != null && !issueDate.isAfter(LocalDate.now())) {
            this.issueDate = issueDate;
        }
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        if (totalAmount >= 0) {
            this.totalAmount = totalAmount;
        }
    }

    public List<InvoiceDetail> getDetails() {
        return details;
    }
}