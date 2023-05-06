package com.saimon.controller;// Merging multiple pdf documents here

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;

public class MergePdf {

    public static void main(String[] args)
            throws IOException
    {

        // loading all the pdf files we wish to merge

        File file1 = new File("/home/saimon/Desktop/Files/m/My Bio-data/Hasan_bio_1_v2.pdf");
        File file2 = new File("/home/saimon/Desktop/Files/m/My Bio-data/Hasan_bio_2.pdf");

        // Instantiating PDFMergerUtility class

        PDFMergerUtility obj = new PDFMergerUtility();

        // Setting the destination file path

        obj.setDestinationFileName("/home/saimon/Desktop/Files/m/My Bio-data/Final/Hasan_bio_data_2.pdf");

        // Add all source files, to be merged

        obj.addSource(file1);
        obj.addSource(file2);

        // Merging documents

//        obj.mergeDocuments();
        obj.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());

        System.out.println("PDF Documents merged to a single file");
    }
}
