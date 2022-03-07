package com.saimon.service.impl;

import com.saimon.service.PdfBox;
import com.saimon.service.PdfServiceTwo;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.Type1Encoding;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.util.Charsets;
import org.apache.pdfbox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * @Author Muhammad Saimon
 * @since Feb 09/02/2021 22:16
 */

/*contentStream.setNonStrokingColor(Color.white);
        contentStream.addRect(50, 200, 500, 100);
        contentStream.fillAndStrokeEvenOdd();

        contentStream.setNonStrokingColor(Color.black);

        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 20);
        contentStream.newLineAtOffset(250, 500);
        contentStream.showText("Connect with Us:");
        contentStream.endText();

        contentStream.setStrokingColor(Color.red);
        contentStream.addRect(230, 480, 200, 50);
        contentStream.setLineJoinStyle(1);
        contentStream.closeAndStroke();*/

@Service
public class PractisePdfbox implements PdfBox {

    private static final float FONT_SIZE = 10f;
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean generatePdfUsingPdfBox(String name,  String banglaName, String verificationCode, String address,
                                          String phoneNo, String location) {

        try (PDDocument doc = new PDDocument()) {
            PDPage pdPage = new PDPage(PDRectangle.A4);
            doc.addPage(pdPage);

            String date = "তারিখঃ ৪ঠা এপ্রিল ২০২১";
            String cautiousStr = "সতর্কতাঃ";
            float pageHeight = pdPage.getBBox().getHeight(); //841.8898
            float pageWidth = pdPage.getBBox().getWidth(); //595.27563
            float oneThirdOfHeight = pageHeight / 3;
            //Color color = new Color(228, 75, 111);
            Color color = new Color(224, 29, 111);

            PDPageContentStream cs = new PDPageContentStream(doc, pdPage);

            cs.setLeading(14f);
            cs.setNonStrokingColor(color);

            // 1st portion
            addBar(pageWidth, color, cs, pageHeight - 30);
            writeTextToPage(doc, date, cautiousStr, verificationCode, banglaName, pageWidth, pageHeight, cs);
            addBar(pageWidth, color, cs, oneThirdOfHeight * 2 + 30);

            // 2nd portion
            writeReceiverInfo(name, address, phoneNo, oneThirdOfHeight, cs);

            // 3rd portion
            addImageToPage(doc, oneThirdOfHeight, pageWidth, cs);

            pageDivider(oneThirdOfHeight, pageWidth, cs);

            cs.close();

            doc.save(location);
            log.info("PDF created.");
            return true;
        } catch (IOException | URISyntaxException e) {
            log.error("PDF Error : {}", e.getMessage(), e);
            return false;

        }

    }

    private void addImageToPage(PDDocument doc, float oneThirdOfHeight, float pageWidth,
                                PDPageContentStream cs) throws URISyntaxException, IOException {
        Path path = Paths.get(ClassLoader.getSystemResource("logo-lg.png").toURI());

        PDImageXObject image = PDImageXObject.createFromFile(path.toAbsolutePath().toString(), doc);
        float startX = (pageWidth - 100) / 2;
        float startY = (oneThirdOfHeight - 60) / 2;
        cs.drawImage(image, startX, startY, 100, 60);
    }

    private void pageDivider(float oneThirdOfHeight, float pageWidth, PDPageContentStream cs) throws IOException {
        cs.setLineWidth(2);
        cs.moveTo(0, oneThirdOfHeight);
        cs.lineTo(pageWidth, oneThirdOfHeight);
        cs.moveTo(0, oneThirdOfHeight * 2);
        cs.lineTo(pageWidth, oneThirdOfHeight * 2);
        cs.setLineDashPattern(new float[]{3}, 0);
        cs.stroke();
    }

    private void writeReceiverInfo(String name, String address, String phoneNo, float oneThirdOfHeight, PDPageContentStream cs) throws IOException {
        cs.beginText();
        cs.setFont(PDType1Font.COURIER, FONT_SIZE);
        cs.newLineAtOffset(100, 460);
        cs.showText("To,");

        doubleSpace(cs);

        cs.showText(name);
        cs.newLine();
        cs.showText(address);
//        cs.newLine();
//        cs.showText("Uttara, Dhaka 1230");

        doubleSpace(cs);

        cs.showText("Mobile: " + phoneNo);
        cs.endText();

        cs.setLineWidth(1);
        cs.addRect(70, oneThirdOfHeight + 70, 350, 140);
        cs.closeAndStroke();
    }

    private void writeTextToPage(PDDocument doc, String date, String cautiousStr, String verificationCode,  String banglaName, float pageWidth,
                                 float pageHeight, PDPageContentStream cs) throws IOException, URISyntaxException {
        //cs.newLineAtOffset(85, pageHeight - 60);


        File fontFile = new File("src/main/resources/Azad_27-02-2006.ttf");

//        TTFParser parser = new TTFParser(false, false);
//        TrueTypeFont ttf = parser.parse(fontFile);

        InputStream fontFileStream = new FileInputStream("src/main/resources/Azad_27-02-2006.ttf");


        PDFont banglaFont = PDType0Font.load(doc, fontFileStream, false);

        cs.beginText();
        cs.setFont(banglaFont, FONT_SIZE);

        cs.setTextMatrix(Matrix.getRotateInstance(Math.toRadians(180), pageWidth - 85, pageHeight - 220));
        cs.showText(date);

        doubleSpace(cs);

        cs.showText("অভিনন্দন! প্রিয় " + banglaName + ",");

        doubleSpace(cs);

        cs.showText("অভিনন্দন! বিকাশ রিটেইল একাউন্ট রেজিস্ট্রেশনের জন্য প্রযোজ্য ভেরিফিকেশন কোডটি আপনার প্রদত্ত ব্যবসা");
        cs.newLine();
        cs.showText("প্রতিষ্ঠানের ঠিকানায় আপনি সফলভাবে গ্রহণ করেছেন।আপনার ভেরিফিকেশান কোডটি হল, \"" + verificationCode + "\"");
        cs.newLine();
        cs.showText("কোডটি www.bkash.com/pra/verification -এ প্রবেশ করিয়ে ভেরিফাই করে রেজিস্ট্রেশন প্রক্রিয়া সম্পন্ন করুন।");

        doubleSpace(cs);

        cs.showText(cautiousStr);
        cs.showText(" ভেরিফিকেশন কোডটি কারো সাথে শেয়ার করবেন না এবং ভেরিফিকেশন সফলভাবে সম্পন্ন হয়ে গেলে কোডটি");
        cs.newLine();
        cs.showText("নষ্ট করে ফেলুন।");

        doubleSpace(cs);

        cs.showText("ধন্যবাদান্তে,");
        cs.newLine();
        cs.showText("বিকাশ লিমিটেড");

        cs.endText();

        addOverline(cs, banglaFont, date, pageWidth - 85, pageHeight - 220);
        addOverline(cs, banglaFont, cautiousStr, pageWidth - 85, pageHeight - 110);
    }

    // https://stackoverflow.com/questions/26105254/setting-a-text-style-to-underlined-in-pdfbox
    // Minus (-) value in position field create under line. you can use positive value for over-line and stroke-line.
    // (For example -2 for underline, 10 for over-line, 2 for stroke-line for above code)
    private void addOverline(PDPageContentStream contentStream, PDFont font, String text,
                             float sx, float sy) throws IOException {
        //Calculate String width
        float stringWidth = FONT_SIZE * font.getStringWidth(text) / 1000;
        float lineStartPoint = sx - stringWidth;
        float linePosition = 3;

        //begin to draw our line
        contentStream.setLineWidth(1);
        contentStream.moveTo(lineStartPoint, sy + linePosition);
        contentStream.lineTo(sx, sy + linePosition);
        contentStream.stroke();
    }

    private void doubleSpace(PDPageContentStream cs) throws IOException {
        cs.newLine();
        cs.newLine();
    }

    private void addBar(float pageWidth, Color color, PDPageContentStream cs, float y) throws IOException {
        cs.setLineWidth(8);
        cs.setStrokingColor(color);
        cs.moveTo(70, y);
        cs.lineTo(pageWidth - 70, y);
        cs.stroke();
    }

}

