package com.saimon.service.impl;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;
import com.saimon.entity.ProductEntity;
import com.saimon.service.CSVService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @Author Muhammad Saimon
 * @since Jan 10, 2021 15:34
 */

@Slf4j
@Service
public class CSVServiceImpl implements CSVService {

    @Override
    public boolean generateCSV(HttpServletResponse response, List<ProductEntity> productEntities, String fileName, String[] csvHeader, String[] nameMapping) {
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
    public boolean generateCSVByOpenCSV(HttpServletResponse response, List<ProductEntity> productEntities, String[] columns, String fileName) {

        response.setContentType("text/csv");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);

        try {

            ColumnPositionMappingStrategy<ProductEntity> mapStrategy = new ColumnPositionMappingStrategy<>();

            mapStrategy.setType(ProductEntity.class);
            mapStrategy.setColumnMapping(columns);

            StatefulBeanToCsv<ProductEntity> btcsv = new StatefulBeanToCsvBuilder<ProductEntity>(response.getWriter())
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withMappingStrategy(mapStrategy)
                    .withSeparator(',')
                    .build();

            btcsv.write(productEntities);
            log.info("Successfully generate CSV file by Open CSV");
            return true;
        } catch (CsvException | IOException ex) {
            log.error("Error mapping Bean to CSV", ex);
            return false;
        }
    }
}
