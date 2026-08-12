package com.mycompany.sistema_de_ventas;



/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import com.mycompany.sistema_de_ventas.controller.InvoiceController;
import com.mycompany.sistema_de_ventas.model.dao.InvoiceDao;
import com.mycompany.sistema_de_ventas.model.dao.impl.InvoiceDaoImpl;
import com.mycompany.sistema_de_ventas.view.InvoiceForm;

public class Main {

    public static void main(String[] args) {
        // Asegura que la interfaz gráfica se ejecute dentro del hilo de eventos de Swing
        java.awt.EventQueue.invokeLater(() -> {
            InvoiceForm view = new InvoiceForm();
            
            // CORREGIDO: Se instancian la implementación concreta (InvoiceDaoImpl)
            // pero se asigna a la variable de tipo interfaz (InvoiceDao)
            InvoiceDao dao = new InvoiceDaoImpl(); 
            
            InvoiceController controller = new InvoiceController(view, dao);
            view.setVisible(true);
        });
    }
}