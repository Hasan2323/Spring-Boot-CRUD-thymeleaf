package com.saimon.service.impl;

import com.saimon.service.PdfBox;
import com.saimon.service.PdfHelperService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author Muhammad Saimon
 * @since Feb 2/17/21 1:57 PM
 */

@SpringBootTest
public class PractisePdfboxTest {

    @Autowired
    PdfHelperService pdfHelperService;

    @Autowired
    PdfBox pdfBox;

    @Test
    public void testGeneratePdfUsingPdfBox() {
        String fileLocation = pdfHelperService.getFileLocation();

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "PRA_PDF.pdf";

        String fullPath = fileLocation + fileName;

        String name = "Muhammad Abid Hasan";
        String banglaName = "মুহাম্মদ আবিদ হাসান";
        String verCode = "keokradong2021";
        String address = "House 26, Road 11, Sector 6, Uttara, Dhaka";
        String phone = "+8801822262323";

        pdfBox.generatePdfUsingPdfBox(name, banglaName, verCode, address, phone, fullPath);
    }
}