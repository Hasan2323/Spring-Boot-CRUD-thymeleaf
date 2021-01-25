package com.saimon.service;

import java.util.Map;

/**
 * @Author Muhammad Saimon
 * @since 25/01/2021 14:56
 */

public interface PdfService {

    boolean generateHtmlToPdf(Map<String, Object> nameValueMap, String inputTemplateFile, String fullPath);

}
