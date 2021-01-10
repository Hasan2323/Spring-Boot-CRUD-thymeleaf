package com.saimon.service;

import com.saimon.entity.ProductEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * @Author Muhammad Saimon
 * @since Jan 10, 2021 15:33
 */

public interface CSVService {

    boolean generateCSV(HttpServletResponse response, List<ProductEntity> productEntities, String fileName, String[] csvHeader, String[] nameMapping);

    boolean generateCSVByOpenCSV(HttpServletResponse response, List<ProductEntity> productEntities, String[] csvHeader, String[] columns, String fileName);

    ByteArrayInputStream generateCSVByApacheCommonCSV(List<ProductEntity> productEntities, String[] csvHeader);

    void generateCSVByApacheCommonCSVTwo(HttpServletResponse response, List<ProductEntity> productEntities, String[] csvHeader, String fileName);

}
