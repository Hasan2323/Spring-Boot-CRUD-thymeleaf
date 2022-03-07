package com.saimon.service;

import java.util.Map;

/**
 * @Author Muhammad Saimon
 * @since Feb 2/18/21 9:29 AM
 */

public interface FlyingSaucerPdf {
    boolean pdfWithFS(Map<String, Object> nameValueMap, String inputTemplateFile, String fullPath);
}
