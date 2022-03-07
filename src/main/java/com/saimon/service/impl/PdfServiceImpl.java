package com.saimon.service.impl;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.saimon.service.PdfService;
import com.saimon.utils.NumberToSpelling;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.validation.constraints.NotNull;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @Author Muhammad Saimon
 * @since 25/01/2021 14:58
 */

@Service
@Slf4j
public class PdfServiceImpl implements PdfService {

    private final float FONT_SIZE_10 = 10F;
    private final float FONT_SIZE_15 = 15F;
    private final float FONT_SIZE_8 = 8F;

    private final SpringTemplateEngine templateEngine;

    public PdfServiceImpl(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public boolean generateHtmlToPdf(Map<String, Object> nameValueMap, String inputTemplateFile, String fullPath) {
        try (FileOutputStream outputStream = new FileOutputStream(fullPath)) {
            log.info("generating pdf file from html template");
            String html = getHtml(inputTemplateFile, nameValueMap);
            //HtmlConverter.convertToPdf(getHtml(inputTemplateFile, nameValueMap), outputStream);
            PdfWriter pdfWriter = new PdfWriter(outputStream);
            Document document = HtmlConverter.convertToDocument(html, pdfWriter);
            PdfFont font = PdfFontFactory.createFont("src/main/resources/fonts/Kalpurush.ttf");
            document.setFont(font);
            //document.setFontFamily("Kalpurush", "arial", "sans-serif");
            document.close();
            log.info("Pdf file generation successful {}", fullPath);
            return true;
        } catch (IOException e) {
            log.error("Pdf file generation error {}", e.getMessage(), e);
            return false;
        }
    }

    private String getHtml(@NotNull String inputTemplate, Map<String, Object> map) {
        log.info("Generating html content from inputTemplate {}, values {}", inputTemplate, map.size());
        Context ctx = new Context();
//        OR
//        Context ctx = new Context(Locale.ENGLISH);
        ctx.setVariables(map);
        String html = templateEngine.process(inputTemplate, ctx);
        log.info("Generated html content from inputTemplate {}, values {} {}", inputTemplate, map.size(), html);
        return html;
    }


    @Override
    public boolean generatePdf(double grandTotal,
                               String attachment,
                               String[] headers,
                               float[] columnWidths,
                               List<List<String>> listOfFileDetails,
                               String fullPath) {
        try {
            int itemSize = listOfFileDetails != null ? listOfFileDetails.size() : 0;
            log.info("Generating pdf file with headers {}, items {}, outputPath {}", headers, itemSize, fullPath);

            if (headers == null || headers.length == 0
                    || listOfFileDetails == null || listOfFileDetails.isEmpty()
                    || columnWidths == null || columnWidths.length == 0
                    || fullPath == null) {
                log.error("Cannot generate pdf file with headers {}, items {}, outputPath {}",
                        headers, itemSize, fullPath);
                return false;
            }

            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(fullPath));
            Document doc = new Document(pdfDoc, PageSize.A4);
            doc.setMargins(60, 60, 60, 60);

            Footer footerHandler = new Footer();
            pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, footerHandler);

//            PdfFont bold = PdfFontFactory.createFont("/fonts/calibri-bold.ttf");
//            PdfFont font = PdfFontFactory.createFont("/fonts/calibri-regular.ttf");

            PdfFont bold = PdfFontFactory.createFont(StandardFonts.COURIER_BOLD);
            PdfFont font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            PdfFont banglaFont = PdfFontFactory.createFont("src/main/resources/fonts/arialuni.ttf", PdfEncodings.IDENTITY_H);

            doc.add(new Paragraph(attachment).setFont(banglaFont).setFontSize(FONT_SIZE_15));

            Table table = new Table(UnitValue.createPercentArray(columnWidths), true);

            generateHeader(headers, bold, table);
            // For the "large tables" they shall be added to the document before its child elements are populated
            doc.add(table);

            log.info("Generating row for pdf file");

            for (List<String> listOfFileDetail : listOfFileDetails) {
                for (String value : listOfFileDetail) {
                    table.addCell(createCell(font, value, FONT_SIZE_10, TextAlignment.LEFT, 0,0,0,0));
                }
                table.flush();
            }
            // https://kb.itextpdf.com/home/it7kb/examples/large-tables
            // Flushes the rest of the content and indicates that no more content will be added to the table
            table.complete();
            createTotalAmountTable(doc, grandTotal, bold, columnWidths);

            footerHandler.writeTotal(pdfDoc);
            doc.close();
            pdfDoc.close();
            log.info("Pdf file generation successful with headers {}, items {}, outputPath {}",
                    headers, itemSize, fullPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Pdf file generation error {}", e.getMessage());
            return false;
        }
    }

    private void generateHeader(String[] headers, PdfFont font, Table table) {
        log.info("Generating header for pdf file");
        for (String header : headers) {
            table.addHeaderCell(createCell(font, header, FONT_SIZE_10, TextAlignment.LEFT, 0,0,0,0));
        }
    }

    private void createTotalAmountTable(Document doc, double grandTotal, PdfFont bold, float[] columnWidths) {
        String grandTotalString = NumberToSpelling.convertToCurrencyFormat(Double.toString(grandTotal));
        String grandTotalWord = NumberToSpelling
                .generateBalanceInWord(grandTotalString.replaceAll(",", ""));

        if (columnWidths.length < 4) {
            log.error("Total number of column is less than 4. No total amount row will be added");
            return;
        }
        float firstColumnWidth = columnWidths[0];
        float lastColumnWidth = columnWidths[columnWidths.length - 1] + columnWidths[columnWidths.length - 2];
        float middleWidth = 0;
        for (int i = 1; i < columnWidths.length - 2; i++) {
            middleWidth += columnWidths[i];
        }

        Table totalAmountTable = new Table(UnitValue.createPercentArray(new float[]{firstColumnWidth,
                middleWidth, lastColumnWidth}), true);

        totalAmountTable.addCell(createCell(bold, "Total", FONT_SIZE_10, TextAlignment.RIGHT,
                0, 0, 0, 0));

        totalAmountTable.addCell(createCell(bold, grandTotalWord, FONT_SIZE_10, TextAlignment.LEFT,
                0, 0, 0, 0));

        totalAmountTable.addCell(createCell(bold, grandTotalString, FONT_SIZE_10, TextAlignment.RIGHT,
                0, 0, 0, 0));

        doc.add(totalAmountTable);
        totalAmountTable.complete();
    }

    private Cell createCell(PdfFont font, String value,
                            float fontSize,
                            TextAlignment textAlignment,
                            float marginTop,
                            float marginRight,
                            float marginBottom,
                            float marginLeft) {
        return new Cell().setKeepTogether(true)
                .setBorder(new SolidBorder(1F)) // .setBorder(Border.NO_BORDER)
                .add(new Paragraph(value)
                        .setFont(font)
                        .setFontSize(fontSize)
                        .setTextAlignment(textAlignment)
                        .setMargins(marginTop, marginRight, marginBottom, marginLeft));
    }

    // Footer event handler
    protected class Footer implements IEventHandler {
        protected PdfFormXObject placeholder;
        protected float width = 50;
        protected float side = 20;
        protected float x = 300;
        protected float y = 20;

        public Footer() {
            placeholder = new PdfFormXObject(new Rectangle(0, 0, width, side));
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            int pageNumber = pdf.getPageNumber(page);
            Rectangle pageSize = page.getPageSize();

            // Creates drawing canvas
            PdfCanvas pdfCanvas = new PdfCanvas(page);
            Canvas canvas = new Canvas(pdfCanvas, pageSize);
            canvas.showTextAligned(new Paragraph()
                    .setFontSize(FONT_SIZE_8)
                    .add("Page ")
                    .add(String.valueOf(pageNumber))
                    .add(" of"), x, y, TextAlignment.RIGHT);
            canvas.close();

            // Create placeholder object to write number of pages
            pdfCanvas.addXObject(placeholder, x + 4.5F, y);
            pdfCanvas.release();
        }

        public void writeTotal(PdfDocument pdf) {
            Canvas canvas = new Canvas(placeholder, pdf);
            canvas.showTextAligned(new Paragraph(String.valueOf(pdf.getNumberOfPages()))
                    .setFontSize(FONT_SIZE_8), 0, 0, TextAlignment.LEFT);
            canvas.close();
        }
    }

}
