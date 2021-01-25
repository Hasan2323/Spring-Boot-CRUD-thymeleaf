package com.saimon.service.impl;

import com.itextpdf.html2pdf.HtmlConverter;
import com.saimon.service.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.validation.constraints.NotNull;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/**
 * @Author Muhammad Saimon
 * @since 25/01/2021 14:58
 */

@Service
@Slf4j
public class PdfServiceImpl implements PdfService {

    private final SpringTemplateEngine templateEngine;

    public PdfServiceImpl(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public boolean generateHtmlToPdf(Map<String, Object> nameValueMap, String inputTemplateFile, String fullPath) {
        try (FileOutputStream outputStream = new FileOutputStream(fullPath)) {
            log.info("generating pdf file from html template");
            HtmlConverter.convertToPdf(getHtml(inputTemplateFile, nameValueMap), outputStream);
            log.info("Pdf file generation successful {}", fullPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Pdf file generation error {}", e.getMessage());
            return false;
        }
    }

    private String getHtml(@NotNull String inputTemplate, Map<String, Object> map) {
        log.info("Generating html content from inputTemplate {}, values {}", inputTemplate, map.size());
        Context ctx = new Context(Locale.ENGLISH, map);
//        OR
//        Context ctx = new Context(Locale.ENGLISH);
//        ctx.setVariables(map);
        String html = templateEngine.process(inputTemplate, ctx);
        log.info("Generated html content from inputTemplate {}, values {}", inputTemplate, map.size());
        return html;
    }

}
