package com.saimon.controller;

import com.saimon.entity.ProductEntity;
import com.saimon.service.CSVService;
import com.saimon.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CSVService csvService;

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

    @GetMapping("/products/export")
    public void exportToCSV(HttpServletResponse response) {

        DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy_HHmmss");
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Products_" + currentDateTime + ".csv";
        String[] csvHeader = {"Product ID", "Name", "Brand", "Made in", "Price"};
        String[] nameMapping = {"id", "name", "brand", "madeIn", "price"};
        List<ProductEntity> products = productService.getAllProducts();

        csvService.generateCSV(response, products, fileName, csvHeader, nameMapping);

    }

    @RequestMapping(value = "/products/opencsv", produces = "text/csv")
    public void exportToCSVUsingOpenCSV(HttpServletResponse response) {

        DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy_HHmmss");
        String currentDateTime = dateFormatter.format(new Date());
        String fileName = "Products_" + currentDateTime + ".csv";

        List<ProductEntity> products = productService.getAllProducts();
        String[] csvHeader = {"Pro ID", "Name", "Brand", "Made In", "Price"};
        String[] columns = new String[]{"id", "name", "brand", "madeIn", "price"};

        csvService.generateCSVByOpenCSV(response, products, csvHeader, columns, fileName);
    }

    // https://bezkoder.com/spring-boot-download-csv-file/
    @GetMapping("/products/apachecommoncsv")
    public ResponseEntity<Resource> exportToCSVUsingApacheCommonCSV() {

        DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy_HHmmss");
        String currentDateTime = dateFormatter.format(new Date());
        String filename = "Products_using_Apache_common_" + currentDateTime + ".csv";

        List<ProductEntity> products = productService.getAllProducts();
        String[] csvHeader = {"Product ID", "Name", "Brand", "Made in", "Price"};

        ByteArrayInputStream in = csvService.generateCSVByApacheCommonCSV(products, csvHeader);

        InputStreamResource file = new InputStreamResource(in);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }

}
