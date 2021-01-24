package com.saimon.service.impl;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;
import com.saimon.entity.ProductEntity;
import com.saimon.service.CSVService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Muhammad Saimon
 * @since Jan 10, 2021 15:34
 */

@Slf4j
@Service
public class CSVServiceImpl implements CSVService {

    @Override
    public boolean generateCSV(HttpServletResponse response, List<ProductEntity> productEntities, String fileName,
                               String[] csvHeader, String[] nameMapping) {
        response.setContentType("text/csv");
//        response.setContentType("application/csv");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);

        ICsvBeanWriter csvWriter = null;
        try {
            csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
            csvWriter.writeHeader(csvHeader);

            for (ProductEntity product : productEntities) {
                csvWriter.write(product, nameMapping);
            }
            csvWriter.close();
            log.info("Successfully generate CSV file");
            return true;
        } catch (IOException e) {
            log.error("Failed to generate CSV file");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean generateCSVByOpenCSV(HttpServletResponse response, List<ProductEntity> productEntities,
                                        String[] csvHeader, String[] columns, String fileName) {

        response.setContentType("text/csv");

//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=" + fileName;
//        response.setHeader(headerKey, headerValue);
        // OR
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        try (
                //CSVWriter csvWriter = new CSVWriter(new FileWriter(fullPath), or
                CSVWriter csvWriter = new CSVWriter(response.getWriter(),
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);
        ) {
            csvWriter.writeNext(csvHeader);
            ColumnPositionMappingStrategy<ProductEntity> mapStrategy = new ColumnPositionMappingStrategy<>();

            mapStrategy.setType(ProductEntity.class);
            mapStrategy.setColumnMapping(columns);

            StatefulBeanToCsv<ProductEntity> writer = new StatefulBeanToCsvBuilder<ProductEntity>(response.getWriter())
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withMappingStrategy(mapStrategy)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
//                    .withOrderedResults(false) // optional
                    .build();

            writer.write(productEntities);
            log.info("Successfully generate CSV file by Open CSV");
            return true;
        } catch (CsvException | IOException ex) {
            log.error("CSV file generation error {}", ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public ByteArrayInputStream generateCSVByApacheCommonCSV(List<ProductEntity> productEntities, String[] csvHeader) {
//        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        final CSVFormat format = CSVFormat.DEFAULT.withHeader(csvHeader);

        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);
        ) {
            for (ProductEntity productEntity : productEntities) {
                List<String> data = Arrays.asList(
                        String.valueOf(productEntity.getId()),
                        productEntity.getName(),
                        productEntity.getBrand(),
                        productEntity.getMadeIn(),
                        String.valueOf(productEntity.getPrice())
                );
                csvPrinter.printRecord(data);
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException ex) {
            throw new RuntimeException("fail to import data to CSV file: " + ex.getMessage());
//            log.error("CSV file generation error {}", ex.getMessage(), ex);
        }
    }

    @Override
    public void generateCSVByApacheCommonCSVTwo(HttpServletResponse response, List<ProductEntity> productEntities,
                                                String[] csvHeader, String fileName) {

        response.setContentType("text/csv");

//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=" + fileName;
//        response.setHeader(headerKey, headerValue);
        // OR
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        try (
                CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), CSVFormat.DEFAULT.withHeader(csvHeader));
        ) {
            for (ProductEntity productEntity : productEntities) {
                List<String> data = Arrays.asList(
                        String.valueOf(productEntity.getId()),
                        productEntity.getName(),
                        productEntity.getBrand(),
                        productEntity.getMadeIn(),
                        String.valueOf(productEntity.getPrice())
                );

                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
        } catch (Exception e) {
            log.error("CSV file generation error {}", e.getMessage(), e);
        }
    }
}
