package com.saimon.service;

import com.saimon.entity.ProductEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Author Muhammad Saimon
 * @since Jan 24/01/2021 22:16
 */

public interface PDFExporter {

    void export(HttpServletResponse response, List<ProductEntity> productEntities) throws IOException;

}
