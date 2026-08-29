package com.mycompany.sistema_de_ventas.model.dao.impl;

import com.mycompany.sistema_de_ventas.config.DatabaseConnection;
import com.mycompany.sistema_de_ventas.model.dao.InvoiceDao;
import com.mycompany.sistema_de_ventas.model.entity.Invoice;
import com.mycompany.sistema_de_ventas.model.entity.InvoiceDetail;

import javax.swing.JOptionPane;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDaoImpl implements InvoiceDao {

    @Override
    public boolean save(Invoice invoice) {
        if (invoice == null) {
            JOptionPane.showMessageDialog(null, 
                "La estructura de la factura no puede ser nula.", 
                "Error de Validación", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (invoice.getDetails() == null || invoice.getDetails().isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                "No se puede guardar una factura sin productos en el detalle.", 
                "Error de Validación", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (invoice.getTotalAmount() <= 0) {
            JOptionPane.showMessageDialog(null, 
                "El monto total de la factura debe ser mayor a cero.", 
                "Error de Validación", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null || conn.isClosed()) {
                JOptionPane.showMessageDialog(null, 
                    "Error de conexión a la base de datos MySQL.", 
                    "Error de Conexión", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Fallo al establecer comunicación con MySQL:\n" + e.getMessage(), 
                "Error de Conexión", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String sqlInvoice = "INSERT INTO factura (fecha, total) VALUES (?, ?)";
        String sqlDetail = "INSERT INTO detalle_factura (factura_id, producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

        try {
            conn.setAutoCommit(false);

            PreparedStatement stmtInvoice = conn.prepareStatement(sqlInvoice, Statement.RETURN_GENERATED_KEYS);
            stmtInvoice.setTimestamp(1, invoice.getIssueDate() != null ? Timestamp.valueOf(invoice.getIssueDate().atStartOfDay()) : new Timestamp(System.currentTimeMillis()));
            stmtInvoice.setDouble(2, invoice.getTotalAmount());
            stmtInvoice.executeUpdate();

            ResultSet rs = stmtInvoice.getGeneratedKeys();
            int invoiceId = 0;
            if (rs.next()) {
                invoiceId = rs.getInt(1);
            }

            PreparedStatement stmtDetail = conn.prepareStatement(sqlDetail);
            for (InvoiceDetail detail : invoice.getDetails()) {
                if (detail.getProductName() == null || detail.getProductName().trim().isEmpty() || detail.getQuantity() <= 0 || detail.getUnitPrice() <= 0) {
                    conn.rollback();
                    JOptionPane.showMessageDialog(null, 
                        "Uno de los productos contiene datos inválidos.", 
                        "Error de Validación", 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                stmtDetail.setInt(1, invoiceId);
                stmtDetail.setString(2, detail.getProductName().trim().toUpperCase());
                stmtDetail.setInt(3, detail.getQuantity());
                stmtDetail.setDouble(4, detail.getUnitPrice());
                stmtDetail.setDouble(5, detail.getSubtotal());
                stmtDetail.addBatch();
            }
            stmtDetail.executeBatch();

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, 
                "Error al guardar la factura en MySQL:\n" + e.getMessage(), 
                "Error SQL", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getNextInvoiceNumber() {
        String sql = "SELECT COUNT(*) + 1 AS next_id FROM factura";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int nextId = rs.getInt("next_id");
                return String.format("INV-%03d", nextId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "INV-001";
    }

    @Override
    public Invoice findById(int id) {
        if (id <= 0) {
            return null;
        }
        String sql = "SELECT * FROM factura WHERE id = ?";
        Invoice invoice = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    invoice = new Invoice();
                    invoice.setInvoiceId(rs.getInt("id"));
                    invoice.setIssueDate(rs.getTimestamp("fecha").toLocalDateTime().toLocalDate());
                    invoice.setTotalAmount(rs.getDouble("total"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoice;
    }

    @Override
    public List<Invoice> findAll() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM factura";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("id"));
                invoice.setIssueDate(rs.getTimestamp("fecha").toLocalDateTime().toLocalDate());
                invoice.setTotalAmount(rs.getDouble("total"));
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    @Override
    public List<InvoiceDetail> findAllDetails() {
        List<InvoiceDetail> details = new ArrayList<>();
        String sql = "SELECT id, producto, cantidad, precio_unitario FROM detalle_factura";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                InvoiceDetail detail = new InvoiceDetail(
                    rs.getInt("id"),
                    rs.getString("producto"),
                    rs.getInt("cantidad"),
                    rs.getDouble("precio_unitario")
                );
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public boolean deleteDetail(int detailId) {
        if (detailId <= 0) {
            return false;
        }
        String sql = "DELETE FROM detalle_factura WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detailId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Error al eliminar el producto de la base de datos:\n" + e.getMessage(), 
                "Error SQL", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}