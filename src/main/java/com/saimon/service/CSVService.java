package com.saimon.service;

import com.saimon.entity.ProductEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author Muhammad Saimon
 * @since Jan 10, 2021 15:33
 */

public interface CSVService {

    boolean generateCSV(HttpServletResponse response, List<ProductEntity> productEntity, String fileName, String[] csvHeader, String[] nameMapping);

}
