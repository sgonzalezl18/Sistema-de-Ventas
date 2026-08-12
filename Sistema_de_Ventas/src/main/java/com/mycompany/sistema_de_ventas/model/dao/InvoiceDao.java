/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema_de_ventas.model.dao;
import com.mycompany.sistema_de_ventas.model.entity.Invoice;
import com.mycompany.sistema_de_ventas.model.entity.Invoice;
import java.util.List;

/**
 * Interfaz que define las operaciones del CRUD para las facturas.
 */
public interface InvoiceDao {
    
    // Guarda una factura completa (Encabezado + Detalles) en la base de datos
    boolean save(Invoice invoice);
    
    // Obtiene el siguiente número de factura correlativo
    String getNextInvoiceNumber();
    
    // Busca una factura por su ID
    Invoice findById(int id);
    
    // Obtiene la lista de todas las facturas registradas
    List<Invoice> findAll();
}