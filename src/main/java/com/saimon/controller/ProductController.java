package com.saimon.controller;

import com.lowagie.text.DocumentException;
import com.saimon.entity.ProductEntity;
import com.saimon.service.PDFExporter;
import com.saimon.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private PDFExporter pdfExporter;

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

}
