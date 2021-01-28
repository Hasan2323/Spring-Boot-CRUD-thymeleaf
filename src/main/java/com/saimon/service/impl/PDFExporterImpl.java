package com.saimon.service.impl;

import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.PageSize;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.saimon.entity.ProductEntity;
import com.saimon.service.PDFExporter;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * @Author Muhammad Saimon
 * @since Jan 24/01/2021 22:09
 */

@Service
public class PDFExporterImpl implements PDFExporter {

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.GRAY);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.BLACK);

        cell.setPhrase(new Phrase("ID", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Full Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Brand", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Made In", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Price", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table, List<ProductEntity> productEntities) {
        for (ProductEntity productEntity : productEntities) {
            table.addCell(String.valueOf(productEntity.getId()));
            table.addCell(productEntity.getName());
            table.addCell(productEntity.getBrand());
            table.addCell(productEntity.getMadeIn());
            table.addCell(String.valueOf(productEntity.getPrice()));
        }
    }

    @Override
    public void export(HttpServletResponse response, List<ProductEntity> productEntities) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLACK);

        Paragraph p = new Paragraph("List of Products", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1.5f, 3.5f, 3.0f, 3.0f, 1.5f});
        table.setSpacingBefore(10);

        writeTableHeader(table);
        writeTableData(table, productEntities);

        document.add(table);

        document.close();

    }

}
