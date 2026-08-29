package com.mycompany.sistema_de_ventas.model.dao;

import com.mycompany.sistema_de_ventas.model.entity.Invoice;
import com.mycompany.sistema_de_ventas.model.entity.InvoiceDetail;
import java.util.List;

public interface InvoiceDao {

    boolean save(Invoice invoice);

    String getNextInvoiceNumber();

    Invoice findById(int id);

    List<Invoice> findAll();

    List<InvoiceDetail> findAllDetails();

    boolean deleteDetail(int detailId);
}