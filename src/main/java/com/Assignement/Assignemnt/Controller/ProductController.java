package com.Assignement.Assignemnt.Controller;

import com.Assignement.Assignemnt.Entity_Models.Product;
import com.Assignement.Assignemnt.Services.ProductService;
//import com.Assignement.Assignemnt.Model.Product; // Adjust the import based on your package structure
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private  ProductService productService;

//    // Constructor injection for ProductService
//
//    public ProductController(ProductService productService) {
//        this.productService = productService;
//    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            logger.error("File is empty");
            return ResponseEntity.badRequest().body("File is empty");
        }
        try {
            // Process the Excel file and store the data
            productService.saveProductsFromExcel(file);
            logger.info("File uploaded and processed successfully: {}", file.getOriginalFilename());
            return ResponseEntity.ok("File uploaded and processed successfully");
        } catch (Exception e) {
            logger.error("Error processing file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file: " + e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadJsonFile() {
        try {
            List<Product> products = productService.findAllProducts(); // Assuming you have a method in your service
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(products);

            ByteArrayResource resource = new ByteArrayResource(json.getBytes());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.json")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(resource);
        } catch (IOException e) {
            logger.error("Error generating JSON file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


   


}
