package com.saimon.service;

import java.util.List;
import java.util.Map;

/**
 * @Author Muhammad Saimon
 * @since Feb 08/02/2021 23:24
 */

public interface PdfServiceTwo {

    boolean generatePdfUsingPdfBox(List<String> values, String fullPath);

    boolean generateHtmlToPdf(Map<String, Object> nameValueMap, String inputTemplateFile, String fullPath);

}
