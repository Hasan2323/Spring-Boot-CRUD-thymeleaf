package com.saimon.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saimon.entity.ProductEntity;
import com.saimon.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    private int count = 0;
    private Object jsonData;

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

    @RequestMapping("/")
    public String viewHomePage(Model model){
        List<ProductEntity> productEntities = productService.getAllProducts();
        model.addAttribute("products", productEntities);
        return "index";
    }

    @RequestMapping("/new")
    public String addNewForm(Model model){
        ProductEntity productEntity = new ProductEntity();
        model.addAttribute("productEntity", productEntity);
        return "addProduct";
    }

//    @RequestMapping(value = "/add", method = RequestMethod.POST)
//    public String addProduct(@ModelAttribute("productEntity") ProductEntity productEntity){
//        productService.create(productEntity);
//        return "redirect:/";
//    }  nicher ta likhle o kaj hoi...so no need to write extra thing

    @RequestMapping("/add")  // this works for adding and updating product.
    public String addProduct(ProductEntity productEntity){
        productService.create(productEntity);
        return "redirect:/";
    }

    @RequestMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id){
        productService.delete(id);
        return "redirect:/";
    }

    @RequestMapping("/edit/{id}")
    @ResponseBody
    public Object getProductByIdForUpdate(@PathVariable Long id,Model model) throws IOException {
        ProductEntity productEntity = productService.getProductById(id);

        Gson json = new GsonBuilder().setPrettyPrinting().create();

        // Java object to JSON String.
        String jsonData = json.toJson(productEntity);

        // Java object to JSON file. Unfortunately its not working
        FileWriter writer = new FileWriter("/home/saimon/workspace/product.json");
        json.toJson(productEntity, writer);

        System.out.println(jsonData);
        return jsonData;

        //model.addAttribute(productEntity);
        //return "editProduct";
    }//or

//    @RequestMapping("/edit/{id}")
//    public ModelAndView getProductByIdForUpdate(@PathVariable(name = "id") Long id){
//        ModelAndView mav = new ModelAndView("editProduct");
//        ProductEntity productEntity = productService.getProductById(id);
//        mav.addObject("productEntity", productEntity);
//        return mav;
//    }

    @RequestMapping("/json")
    //@ResponseBody
    public void jsonData() throws IOException {
        //File file = new File("/home/saimon/workspace/saimon-project/ProductManager/bookDetails.json");
        ObjectMapper mapper = new ObjectMapper();
//        TypeReference<List<Object>> typeReference = new TypeReference<>() {
//        };
//        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/bookDetails.json");
//        Object jsonData = mapper.readValue(inputStream, typeReference);

        //for saving to db
//        try {
//            List<User> users = mapper.readValue(inputStream,typeReference);
//            userService.save(users);
//            System.out.println("Users Saved!");
//        } catch (IOException e){
//            System.out.println("Unable to save users: " + e.getMessage());
//        }

        try {
            String jsonStr = Files.lines(Paths.get("/home/saimon/workspace/saimon-project/ProductManager/src/main/resources/json/bookDetails.json")).collect(Collectors.joining(System.lineSeparator()));
            JsonNode jsonNode = mapper.readTree(jsonStr);
            if (jsonNode.isObject()) {
                jsonNode.fields().forEachRemaining(entry -> {
                    String fieldName = entry.getKey();
                    JsonNode nestedNode = entry.getValue();
                    System.out.println(fieldName);
                    System.out.println(nestedNode);
                });
            } else if (jsonNode.isArray()) {
                jsonNode.elements().forEachRemaining(subElem -> {
                    int price = subElem.get("price").asInt();
                    //System.out.println("Name : " + name);
                    cal(price);

                });
            } else {
                String value = jsonNode.asText();
                System.out.println(value);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void cal(int price) {
        count += price;
    }

}
