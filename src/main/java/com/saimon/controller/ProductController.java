package com.saimon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.DocumentException;
import com.saimon.entity.ProductEntity;
import com.saimon.service.*;
import com.saimon.utils.NumberToSpelling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private PDFExporter pdfExporter;

    @Autowired
    private PdfHelperService pdfHelperService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private ObjectMapper objectMapper;

    /*
    the below method for Error/Exception handling // tutorial a eta chilo.
    This can also be written in individual CLASS. check DatabaseErrorHandler class within this package.
     */
//    @ExceptionHandler(DataAccessException.class)
//    public String handleDatabaseException(Model model, DataAccessException exception) {
//        model.addAttribute("error", exception.getMessage());
//        return "error";
//    }
//    @ExceptionHandler(Exception.class)
//    public String handleDatabaseException(Model model, Exception exception) {
//        model.addAttribute("errorWithClass", exception);
//        model.addAttribute("error", exception.getMessage());
//        model.addAttribute("errorClass", exception.getClass());
//        return "error";
//    }

    @GetMapping(value = {"/", "/home"})
    public String viewHomePage(Model model) {
        List<ProductEntity> productEntities = productService.getAllProducts();
        model.addAttribute("products", productEntities);
        return "index";
    }

    @GetMapping("/new")
    public String addNewForm(ProductEntity productEntity) {
        return "addProduct";
    }

//    @RequestMapping(value = "/add", method = RequestMethod.POST)
//    public String addProduct(@ModelAttribute("productEntity") ProductEntity productEntity){
//        productService.create(productEntity);
//        return "redirect:/";
//    }  nicher ta likhle o kaj hoi...so no need to write extra thing

    @PostMapping("/add")  // this works for adding and updating product.
    public String addProduct(ProductEntity productEntity) {
        productService.createProduct(productEntity);
        return "redirect:/";
    }

    @RequestMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String getProductByIdForUpdate(@PathVariable Long id, Model model) {
        ProductEntity productEntity = productService.getProductById(id);
        model.addAttribute(productEntity);
        return "editProduct";
    }//or

//    @RequestMapping("/edit/{id}")
//    public ModelAndView getProductByIdForUpdate(@PathVariable(name = "id") Long id){
//        ModelAndView mav = new ModelAndView("editProduct");
//        ProductEntity productEntity = productService.getProductById(id);
//        mav.addObject("productEntity", productEntity);
//        return mav;
//    }

    // https://www.codejava.net/frameworks/spring-boot/pdf-export-example
    @GetMapping("/products/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Products_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<ProductEntity> productEntities = productService.getAllProducts();

        pdfExporter.export(response, productEntities);
    }

    @GetMapping("/products/htmltopdf")
    public ResponseEntity<Resource> htmlToPDF() throws IOException {

        String fileLocation = pdfHelperService.getFileLocation();

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Products_report_" + currentDateTime + ".pdf";

        String fullPath = fileLocation + fileName;

        List<ProductEntity> productEntities = productService.getAllProducts();
        ProductEntity product = productEntities.get(0);
        log.info("generating {} pdf file", fileName);

        String inputTemplateName = "/pdf_template";

        Map<String, Object> nameValueMap = new HashMap<>();
        nameValueMap.put("currentDate", currentDateTime);
        nameValueMap.put("bankName", product.getName());
        setRefundAmount(BigDecimal.valueOf(product.getPrice()), nameValueMap);
        nameValueMap.put("branchAddress", product.getMadeIn().split("#"));
        nameValueMap.put("productEntity", product);

        pdfService.generateHtmlToPdf(nameValueMap, inputTemplateName, fullPath);
        return downloadService.downloadFile(fileLocation, fileName);
    }

    private void setRefundAmount(BigDecimal grandTotal, Map<String, Object> nameValueMap) {
        String grandTotalString = NumberToSpelling.convertToCurrencyFormat(grandTotal.toString());
        nameValueMap.put("grandTotal", grandTotalString);
        nameValueMap.put("grandTotalWord", NumberToSpelling
                .generateBalanceInWord(grandTotalString.replaceAll(",", "")));
    }


    @GetMapping("/products/topdf")
    public ResponseEntity<Resource> generatePDF() throws IOException {

        String fileLocation = pdfHelperService.getFileLocation();
        List<List<String>> listOfFileDetails = new ArrayList<>();

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Products_data_" + currentDateTime + ".pdf";

        String fullPath = fileLocation + fileName;

        String[] headers = {"ID", "Product Name", "Brand", "Made In", "Price"};
        String[] keys = {"id", "name", "brand", "madeIn", "price"};

        List<ProductEntity> productEntities = productService.getAllProducts();
        double grandTotal = productEntities.stream().mapToDouble(ProductEntity::getPrice).sum();
        productEntities.forEach(product -> {
            List<String> list = new ArrayList<>();
            Map convertValue = objectMapper.convertValue(product, Map.class);
            for (String key : keys) {
                list.add(String.valueOf(convertValue.get(key)));
            }
            listOfFileDetails.add(list);
        });

        pdfService.generatePdf(grandTotal, "Product List", headers, new float[]{4, 4, 4, 4, 4}, listOfFileDetails, fullPath);

        return downloadService.downloadFile(fileLocation, fileName);
    }


}
