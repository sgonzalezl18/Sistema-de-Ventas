/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema_de_ventas.controller;
import com.mycompany.sistema_de_ventas.model.dao.InvoiceDao;
import com.mycompany.sistema_de_ventas.model.entity.Invoice;
import com.mycompany.sistema_de_ventas.model.entity.InvoiceDetail;
import com.mycompany.sistema_de_ventas.view.InvoiceForm;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class InvoiceController {
    private InvoiceForm view;
    private InvoiceDao invoiceDao;
    private Invoice invoice; // Mantendrá la factura actual en memoria

    public InvoiceController(InvoiceForm view, InvoiceDao invoiceDao) {
        this.view = view;
        this.invoiceDao = invoiceDao;
        this.invoice = new Invoice(); // Nueva factura limpia

        // CONFIGURACIÓN DE LA TABLA (Resuelve pantalla gris/vacía)
        setupTable();

        // Inicializamos eventos de los botones
        initEvents();
        
        // Cargar el número de factura inicial en la vista
        this.view.txtInvoiceNumber.setText(invoiceDao.getNextInvoiceNumber());
    }

    private void setupTable() {
        // 1. Asegurar que tableModel esté inicializado en la vista
        if (this.view.tableModel == null) {
            this.view.tableModel = (DefaultTableModel) this.view.tblInvoiceDetails.getModel();
        }

        // 2. Definir explícitamente las columnas para evitar que la JTable quede sin estructura
        this.view.tableModel.setColumnIdentifiers(new String[] {
            "Producto", "Cantidad", "Precio Unitario", "Subtotal"
        });

        // 3. Vincular el modelo a la JTable visual
        this.view.tblInvoiceDetails.setModel(this.view.tableModel);

        // 4. Obligar a la tabla a llenar el JScrollPane verticalmente
        this.view.tblInvoiceDetails.setFillsViewportHeight(true);
    }

    private void initEvents() {
        // Escucha el clic del botón "Agregar Producto"
        this.view.btnAddItem.addActionListener(e -> addItem());
        
        // Escucha el clic del botón "Eliminar Producto"
        this.view.btnRemoveItem.addActionListener(e -> removeItem());
        
        // Escucha el clic del botón "Guardar Factura"
        this.view.btnSaveInvoice.addActionListener(e -> saveInvoice());
    }

    private void addItem() {
        try {
            // 1. Obtener datos de las cajas de texto de la pantalla
            String productName = view.txtProductName.getText().trim();
            String quantityText = view.txtQuantity.getText().trim();
            String priceText = view.txtUnitPrice.getText().trim();

            // Validación de campos vacíos
            if (productName.isEmpty() || quantityText.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Por favor, llene todos los campos del producto.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            double price = Double.parseDouble(priceText);

            if (quantity <= 0 || price <= 0) {
                JOptionPane.showMessageDialog(view, "La cantidad y el precio deben ser mayores a cero.", "Error de validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Crear el POJO del detalle y agregarlo a la Factura
            InvoiceDetail detail = new InvoiceDetail(1, productName, quantity, price);
            invoice.addDetail(detail);

            // 3. Refrescar la tabla en la interfaz gráfica (JTable)
            Object[] row = {
                detail.getProductName(),
                detail.getQuantity(),
                detail.getUnitPrice(),
                detail.getSubtotal()
            };
            
            view.tableModel.addRow(row);

            // Redibujar la tabla visualmente al instante
            view.tblInvoiceDetails.revalidate();
            view.tblInvoiceDetails.repaint();

            // 4. Actualizar el campo de Total en la pantalla
            view.txtTotalAmount.setText(String.format("%.2f", invoice.getTotalAmount()));

            // Limpiar cajas de texto de entrada y dar foco de nuevo al producto
            view.txtProductName.setText("");
            view.txtQuantity.setText("");
            view.txtUnitPrice.setText("");
            view.txtProductName.requestFocus();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Por favor, ingrese valores numéricos válidos en Cantidad y Precio.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeItem() {
        int selectedRow = view.tblInvoiceDetails.getSelectedRow();
        if (selectedRow >= 0) {
            // Elimina del objeto POJO Factura
            invoice.removeDetail(selectedRow);
            
            // Elimina de la tabla gráfica JTable
            view.tableModel.removeRow(selectedRow);
            
            // Redibujar la tabla visualmente
            view.tblInvoiceDetails.revalidate();
            view.tblInvoiceDetails.repaint();

            // Actualiza el campo Total en pantalla
            view.txtTotalAmount.setText(String.format("%.2f", invoice.getTotalAmount()));
        } else {
            JOptionPane.showMessageDialog(view, "Seleccione una fila para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveInvoice() {
        if (invoice.getDetails().isEmpty()) {
            JOptionPane.showMessageDialog(view, "No se puede guardar una factura sin productos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (view.txtCustomerName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Por favor, ingrese el nombre del cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Asignar datos del cliente ingresados en la vista
        invoice.setCustomerName(view.txtCustomerName.getText().trim());
        invoice.setInvoiceNumber(view.txtInvoiceNumber.getText());

        // Guardar mediante el DAO
        boolean saved = invoiceDao.save(invoice);

        if (saved) {
            JOptionPane.showMessageDialog(view, "¡Factura guardada con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
        } else {
            JOptionPane.showMessageDialog(view, "Error al guardar la factura.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        // Limpia el objeto y la pantalla para una nueva venta
        this.invoice = new Invoice();
        view.tableModel.setRowCount(0);
        view.txtCustomerName.setText("");
        view.txtTotalAmount.setText("0.00");
        view.txtInvoiceNumber.setText(invoiceDao.getNextInvoiceNumber());
        view.txtCustomerName.requestFocus();
    }
}