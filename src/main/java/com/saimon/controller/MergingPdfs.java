package com.saimon.controller;

/**
 * @Author Muhammad Saimon
 * @since Nov 11/8/22 2:03 AM
 */

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;

public class MergingPdfs {
    public static void main(String[] args) throws IOException {

        //Loading an existing PDF document
        File file1 = new File("/home/saimon/Downloads/its/Can-Moves/WES/wes/WES_1.pdf");
        PDDocument doc1 = PDDocument.load(file1);

        File file2 = new File("/home/saimon/Downloads/its/Can-Moves/WES/wes/WES_2.pdf");
        PDDocument doc2 = PDDocument.load(file2);

        File file3 = new File("/home/saimon/Downloads/its/Can-Moves/WES/wes/WES_3.pdf");
        PDDocument doc3 = PDDocument.load(file3);

        File file4 = new File("/home/saimon/Downloads/its/Can-Moves/WES/wes/WES_4.pdf");
        PDDocument doc4 = PDDocument.load(file4);

        File file5 = new File("/home/saimon/Downloads/its/Can-Moves/WES/wes/WES_5.pdf");
        PDDocument doc5 = PDDocument.load(file5);


        //Instantiating PDFMergerUtility class
        PDFMergerUtility PDFMerger = new PDFMergerUtility();

        //Setting the destination file
        PDFMerger.setDestinationFileName("/home/saimon/Downloads/its/Can-Moves/WES/wes/WES_Report_Hasan.pdf");

        //adding the source files
        PDFMerger.addSource(file1);
        PDFMerger.addSource(file2);
        PDFMerger.addSource(file3);
        PDFMerger.addSource(file4);
        PDFMerger.addSource(file5);

        //Merging the two documents
        PDFMerger.mergeDocuments();
        System.out.println("Documents merged");

        //Closing the documents
        doc1.close();
        doc2.close();
        doc3.close();
        doc4.close();
        doc5.close();
    }
}