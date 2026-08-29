package com.mycompany.sistema_de_ventas.controller;

import com.mycompany.sistema_de_ventas.model.dao.InvoiceDao;
import com.mycompany.sistema_de_ventas.model.entity.Invoice;
import com.mycompany.sistema_de_ventas.model.entity.InvoiceDetail;
import com.mycompany.sistema_de_ventas.view.InvoiceForm;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class InvoiceController {

    private InvoiceForm view;
    private InvoiceDao invoiceDao;
    private Invoice invoice;

    public InvoiceController(InvoiceForm view, InvoiceDao invoiceDao) {
        this.view = view;
        this.invoiceDao = invoiceDao;
        this.invoice = new Invoice();

        setupTable();
        initEvents();
        loadRegisteredProducts();

        this.view.txtInvoiceNumber.setText(invoiceDao.getNextInvoiceNumber());
    }

    private void setupTable() {
        if (this.view.tableModel == null) {
            this.view.tableModel = (DefaultTableModel) this.view.tblInvoiceDetails.getModel();
        }

        this.view.tableModel.setColumnIdentifiers(new String[]{
            "ID", "Producto", "Cantidad", "Precio Unitario", "Subtotal"
        });

        this.view.tblInvoiceDetails.setModel(this.view.tableModel);
        this.view.tblInvoiceDetails.setFillsViewportHeight(true);
    }

    private void initEvents() {
        this.view.btnAddItem.addActionListener(e -> addItem());
        this.view.btnRemoveItem.addActionListener(e -> removeItem());
        this.view.btnSaveInvoice.addActionListener(e -> saveInvoice());
    }

    private void loadRegisteredProducts() {
        this.view.tableModel.setRowCount(0);
        List<InvoiceDetail> details = invoiceDao.findAllDetails();
        double currentTotal = 0.0;

        for (InvoiceDetail detail : details) {
            Object[] row = {
                detail.getProductId(),
                detail.getProductName(),
                detail.getQuantity(),
                detail.getUnitPrice(),
                detail.getSubtotal()
            };
            this.view.tableModel.addRow(row);
            currentTotal += detail.getSubtotal();
        }

        this.view.txtTotalAmount.setText(String.format("%.2f", currentTotal));
        this.view.tblInvoiceDetails.revalidate();
        this.view.tblInvoiceDetails.repaint();
    }

    private void addItem() {
        try {
            String productName = view.txtProductName.getText().trim();
            String quantityText = view.txtQuantity.getText().trim();
            String priceText = view.txtUnitPrice.getText().trim();

            if (productName.isEmpty() || quantityText.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Por favor, llene todos los campos del producto.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!productName.matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s]+$")) {
                JOptionPane.showMessageDialog(view, "El nombre del producto solo debe contener caracteres alfanuméricos.", "Error de formato", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int quantity = Integer.parseInt(quantityText);
            double price = Double.parseDouble(priceText);

            if (quantity <= 0 || price <= 0) {
                JOptionPane.showMessageDialog(view, "La cantidad y el precio deben ser mayores a cero.", "Error de validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            InvoiceDetail detail = new InvoiceDetail(0, productName.toUpperCase(), quantity, price);
            invoice.addDetail(detail);

            Object[] row = {
                "N/A",
                detail.getProductName(),
                detail.getQuantity(),
                detail.getUnitPrice(),
                detail.getSubtotal()
            };

            view.tableModel.addRow(row);
            view.tblInvoiceDetails.revalidate();
            view.tblInvoiceDetails.repaint();

            recalculateTotal();

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
            Object idValue = view.tableModel.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(
                view, 
                "¿Está seguro de eliminar este producto? Esta acción no se puede deshacer.", 
                "Confirmar Eliminación", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (idValue != null && !idValue.toString().equals("N/A")) {
                    int detailId = Integer.parseInt(idValue.toString());
                    boolean deleted = invoiceDao.deleteDetail(detailId);
                    if (deleted) {
                        JOptionPane.showMessageDialog(view, "Producto eliminado definitivamente de la base de datos.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(view, "No se pudo eliminar el producto de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    invoice.removeDetail(selectedRow);
                }

                view.tableModel.removeRow(selectedRow);
                recalculateTotal();
            }
        } else {
            JOptionPane.showMessageDialog(view, "Seleccione una fila para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void recalculateTotal() {
        double total = 0.0;
        for (int i = 0; i < view.tableModel.getRowCount(); i++) {
            Object subtotalValue = view.tableModel.getValueAt(i, 4);
            if (subtotalValue != null) {
                total += Double.parseDouble(subtotalValue.toString());
            }
        }
        view.txtTotalAmount.setText(String.format("%.2f", total));
    }

    private void saveInvoice() {
        if (invoice.getDetails().isEmpty()) {
            JOptionPane.showMessageDialog(view, "No se puede guardar una factura sin nuevos productos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String customerName = view.txtCustomerName.getText().trim();
        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Por favor, ingrese el nombre del cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!customerName.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            JOptionPane.showMessageDialog(view, "El nombre del cliente solo debe contener letras.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        invoice.setCustomerName(customerName.toUpperCase());
        invoice.setInvoiceNumber(view.txtInvoiceNumber.getText());

        boolean saved = invoiceDao.save(invoice);

        if (saved) {
            JOptionPane.showMessageDialog(view, "¡Factura guardada con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
        } else {
            JOptionPane.showMessageDialog(view, "Error al guardar la factura.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        this.invoice = new Invoice();
        view.tableModel.setRowCount(0);
        view.txtCustomerName.setText("");
        view.txtTotalAmount.setText("0.00");
        view.txtInvoiceNumber.setText(invoiceDao.getNextInvoiceNumber());
        loadRegisteredProducts();
        view.txtCustomerName.requestFocus();
    }
}