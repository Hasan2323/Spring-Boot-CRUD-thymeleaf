package com.saimon.service.impl;

import com.openhtmltopdf.bidi.support.ICUBidiReorderer;
import com.openhtmltopdf.bidi.support.ICUBidiSplitter;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.saimon.service.PdfServiceTwo;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.validation.constraints.NotNull;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @Author Muhammad Saimon
 * @since Feb 08/02/2021 23:26
 */

@Slf4j
@Service
public class PdfServiceTwoImpl implements PdfServiceTwo {

    private static final String PDF_RESOURCES = "/pdf-resources/";

    private final SpringTemplateEngine templateEngine;

    public PdfServiceTwoImpl(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public boolean generateHtmlToPdf(Map<String, Object> nameValueMap, String inputTemplateFile, String fullPath) {

        try (OutputStream outputStream = new FileOutputStream(fullPath)) {

            log.info("generating pdf file from html template...");
            String baseUrl = new ClassPathResource(PDF_RESOURCES).getURL().toExternalForm();
            Document doc = Jsoup.parse(getHtml(inputTemplateFile, nameValueMap), "UTF-8");

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withW3cDocument(new W3CDom().fromJsoup(doc), baseUrl);
            builder.toStream(outputStream);
            builder.run();

            log.info("Html to Pdf file generation successful {}", fullPath);
            return true;
        } catch (IOException e) {
            log.error("Pdf file generation error {}", e.getMessage(), e);
            return false;
        }
    }

    private String getHtml(@NotNull String inputTemplate, Map<String, Object> map) {
        log.info("Generating html content from inputTemplate {}, values {}", inputTemplate, map.size());
        Context context = new Context(Locale.ENGLISH, map);
        String html = templateEngine.process(inputTemplate, context);
        log.info("Generated html content from inputTemplate {}, values {}", inputTemplate, map.size());
        return html;
    }

    @Override
    public boolean generatePdfUsingPdfBox(List<String> values, String fullPath) {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();

            contentStream.setFont(PDType1Font.COURIER_BOLD, 12);
            contentStream.setLeading(20);
            contentStream.setNonStrokingColor(Color.BLUE);
            contentStream.newLineAtOffset(30, 700);

            contentStream.showText(values.get(1));
            contentStream.newLine();

            String text = "This text spans multiple lines and it is added to the PDF document generated using PDFBox. " +
                    "This is a long text which spans multiple lines, this text checks if the line is changed as per the " +
                    "allotted width in the PDF or not. The Apache PDFBox library is an open source tool written " +
                    "in Java for working with PDF documents.";
            showMultiLineText(text, 400, contentStream, PDType1Font.HELVETICA, 12);

            contentStream.setFont(PDType1Font.TIMES_BOLD, 12);
            contentStream.setNonStrokingColor(Color.RED);
            contentStream.showText(values.get(1));
            contentStream.newLine();
//            for (String value : pdfData) {
//                contentStream.showText(value);
//                contentStream.newLine();
//                //contentStream.setNonStrokingColor(Color.RED);
//            }

            contentStream.endText();

            contentStream.close();
            document.save(fullPath);

            document.close();
            log.info("PDF done!");
            return true;
        } catch (IOException e) {
            log.error("PDF Error!");
            e.printStackTrace();
            return false;
        }
    }

    private static void showMultiLineText(String text, int allowedWidth,
                                          PDPageContentStream contentStream, PDFont font, int fontSize) throws IOException {
        List<String> lines = new ArrayList<>();
        String line = "";
        // split the text on spaces
        String[] words = text.split(" ");
        // split the text one or more spaces
        //String[] words = text.split("\\s+");
        for(String word : words) {
            if(!line.isEmpty()) {
                line += " ";
            }
            // check if adding the word to the line surpasses the width of the page
            // or check for width boundaries
            int size = (int) (fontSize * font.getStringWidth(line + word) / 1000);
            if(size > allowedWidth) {
                // if line + word surpasses the width of the page, add the line without the current word
                // or if line + new word > page width, add the line to the list without the word
                lines.add(line);
                // start new line with the current word
                line = word;
            } else {
                // if line + word fits the page width, add the current word to the line
                // or if line + word < page width, append the word to the line
                line += word;
            }
        }
        lines.add(line);
        // write lines to Content stream
        for(String ln : lines) {
            System.out.println("Line- " + ln);
            contentStream.showText(ln);
            contentStream.newLine();
        }
    }


    /*@Override
    public boolean generatePdfUsingPdfBox(List<String> pdfData, String fullPath) {

        log.info("Generating pdf file...");

        if (CollectionUtils.isEmpty(pdfData) || StringUtils.isBlank(fullPath)) {
            log.error("Cannot generate pdf file with Data {}, outputPath {}", pdfData.size(), fullPath);
            return false;
        }

        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.setLeading(20);

            contentStream.beginText();
            contentStream.setFont(PDType1Font.COURIER_BOLD, 12);
            contentStream.setNonStrokingColor(Color.GRAY);
            contentStream.newLineAtOffset(200, 750);
            contentStream.showText("Personal Retail Account");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 14);
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.newLineAtOffset(100, 710);
            for (String value : pdfData) {
                contentStream.showText(value);
                contentStream.newLine();
            }
            contentStream.endText();

            contentStream.close();
            document.save(fullPath);

            document.close();
            log.info("Pdf file generation successful.");
            return true;
        } catch (IOException e) {
            log.error("Pdf file generation error {}", e.getMessage(), e);
            return false;
        }
    }*/

}
