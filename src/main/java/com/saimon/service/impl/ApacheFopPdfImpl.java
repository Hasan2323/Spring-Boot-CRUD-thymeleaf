package com.saimon.service.impl;

import com.saimon.service.ApacheFopPdf;
import lombok.extern.slf4j.Slf4j;
import org.apache.fop.apps.*;
import org.apache.fop.fonts.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Muhammad Saimon
 * @since Feb 2/18/21 4:11 PM
 */

@Slf4j
@Service
public class ApacheFopPdfImpl implements ApacheFopPdf {

    private static final String PDF_RESOURCES = "/pdf-resources/";

    @Override
    public boolean fopPdf(String outPath) {
        try {
            File xmlFile = new File("src/main/resources/templates/name.xml");
            File xslFile = new File("src/main/resources/templates/style.xsl");


            // configure fopFactory as desired
            final FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
            String baseUrl = new ClassPathResource(PDF_RESOURCES).getURL().toExternalForm();
            FontUris fontUris = new FontUris(new File("src/main/resources/fonts/Kalpurush.ttf").toURI(), null);
            List<FontTriplet> triplets = new ArrayList<>();
            triplets.add(new FontTriplet("Kalpurush", Font.STYLE_NORMAL, Font.WEIGHT_NORMAL));
            EmbedFontInfo fontInfo = new EmbedFontInfo(fontUris, false, false, triplets, null);
            List<EmbedFontInfo> fontInfoList = new ArrayList<>();
            fontInfoList.add(fontInfo);
            fopFactory.getFontManager().updateReferencedFonts(fontInfoList);
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            // configure foUserAgent as desired
            // Setup output
            OutputStream out = new FileOutputStream(outPath);
            out = new java.io.BufferedOutputStream(out);
            try {
                // Construct fop with desired output format
                Fop fop;
                fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

                // Setup XSLT
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer transformer = factory.newTransformer(new StreamSource(xslFile));

                // Setup input for XSLT transformation
                Source src = new StreamSource(xmlFile);

                // Resulting SAX events (the generated FO) must be piped through to FOP
                Result res = new SAXResult(fop.getDefaultHandler());

                // Start XSLT transformation and FOP processing
                transformer.transform(src, res);
                log.info("FOP : pdf done.");
                return true;
            } catch (FOPException | TransformerException e) {
                log.error("Pdf file generation error {}", e.getMessage(), e);
                return false;
            } finally {
                out.close();
            }
        } catch (IOException e) {
            log.error("Pdf file generation error {}", e.getMessage(), e);
            return false;
        }
    }
}
