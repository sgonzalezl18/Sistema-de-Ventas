/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema_de_ventas.model.dao.impl;
import com.mycompany.sistema_de_ventas.model.dao.InvoiceDao;
import com.mycompany.sistema_de_ventas.model.entity.Invoice;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la interfaz InvoiceDao.
 * Cumple el contrato firmado en InvoiceDao.
 */
public class InvoiceDaoImpl implements InvoiceDao {

    @Override
    public boolean save(Invoice invoice) {
        // Aquí se escribirán las consultas SQL (INSERT INTO invoice...) en el futuro.
        // Por ahora simulamos que el guardado fue exitoso para probar la interfaz gráfica.
        System.out.println("=== FACTURA GUARDADA EN BASE DE DATOS ===");
        System.out.println("Cliente: " + invoice.getCustomerName());
        System.out.println("Fecha: " + invoice.getIssueDate());
        System.out.println("Total: Q." + invoice.getTotalAmount());
        System.out.println("Cantidad de ítems: " + invoice.getDetails().size());
        return true;
    }

    @Override
    public String getNextInvoiceNumber() {
        // Genera un correlativo automático simulado para la pantalla
        return "INV-001";
    }

    @Override
    public Invoice findById(int id) {
        return null;
    }

    @Override
    public List<Invoice> findAll() {
        return new ArrayList<>();
    }
}