package com.saimon.service;

/**
 * @Author Muhammad Saimon
 * @since Feb 15/02/2021 13:36
 */

public interface PdfBox {
    boolean generatePdfUsingPdfBox(String name,  String banglaName, String verificationCode, String address, String phoneNo, String location);
}
