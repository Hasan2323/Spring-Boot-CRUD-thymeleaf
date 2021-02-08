package com.saimon.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author Muhammad Saimon
 * @since 25/01/2021 14:56
 */

public interface PdfService {

    boolean generateHtmlToPdf(Map<String, Object> nameValueMap, String inputTemplateFile, String fullPath);

    boolean generatePdf(double grandTotal, String attachment, String[] columns,
                        float[] columnWidths, List<List<String>> listOfFileDetails, String fullPath);

}
