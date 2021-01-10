package com.saimon.service.impl;

import com.saimon.entity.ProductEntity;
import com.saimon.service.CSVService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Author Muhammad Saimon
 * @since Jan 10, 2021 15:34
 */

@Slf4j
@Service
public class CSVServiceImpl implements CSVService {

    @Override
    public boolean generateCSV(HttpServletResponse response, List<ProductEntity> productEntity, String fileName, String[] csvHeader, String[] nameMapping) {
        response.setContentType("text/csv");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);

        ICsvBeanWriter csvWriter = null;
        try {
            csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
            csvWriter.writeHeader(csvHeader);

            for (ProductEntity product : productEntity) {
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
}
