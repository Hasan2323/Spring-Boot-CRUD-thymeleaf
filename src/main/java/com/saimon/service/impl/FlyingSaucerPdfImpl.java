package com.saimon.service.impl;

import com.lowagie.text.pdf.BaseFont;
import com.saimon.service.FlyingSaucerPdf;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.Map;

/**
 * @Author Muhammad Saimon
 * @since Feb 2/18/21 9:30 AM
 */

@Slf4j
@Service
public class FlyingSaucerPdfImpl implements FlyingSaucerPdf {
    private static final String PDF_RESOURCES = "/pdf-resources/";

    private final SpringTemplateEngine templateEngine;

    public FlyingSaucerPdfImpl(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public boolean pdfWithFS(Map<String, Object> nameValueMap, String inputTemplateFile, String fullPath) {

        try (OutputStream outputStream = new FileOutputStream(fullPath)) {

            log.info("Flying Saucer : generating pdf file from html template...");
            String baseUrl = new ClassPathResource(PDF_RESOURCES).getURL().toExternalForm();
            Document doc = Jsoup.parse(getHtml(inputTemplateFile, nameValueMap), "UTF-8");
            doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
            String html = getHtml(inputTemplateFile, nameValueMap);

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(doc.html(), baseUrl);
            renderer.getFontResolver().addFont("src/main/resources/pdf-resources/fonts/Kalpurush.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            renderer.layout();
            renderer.createPDF(outputStream);

            log.info("Html to Pdf file generation successful {}", fullPath);
            return true;
        } catch (IOException e) {
            log.error("Pdf file generation error {}", e.getMessage(), e);
            return false;
        }
    }

    private String getHtml(@NotNull String inputTemplate, Map<String, Object> map) {
        log.info("Flying Saucer : Generating html content from inputTemplate {}, values {}", inputTemplate, map.size());
        Context context = new Context();
        context.setVariables(map);
        //context.setVariable("banglaWord", "মোট উপার্জন");
        String html = templateEngine.process(inputTemplate, context);
        log.info("Generated html content from inputTemplate {}, values {} html {}", inputTemplate, map.size(), html);
        return html;
    }
}
