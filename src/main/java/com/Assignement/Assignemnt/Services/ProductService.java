package com.Assignement.Assignemnt.Services;

import com.Assignement.Assignemnt.Entity_Models.Product;
import com.Assignement.Assignemnt.Repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    public void saveProductsFromExcel(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);  // Assuming the first sheet is for products

        List<Product> products = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;  // Skip header row

            Product product = new Product();

            // Handle name
            Cell nameCell = row.getCell(0);
            if (nameCell != null) {
                product.setName(nameCell.toString());  // Convert cell to string
            } else {
                product.setName("");  // Set a default value or handle as needed
            }

            // Handle description
            Cell descriptionCell = row.getCell(1);
            if (descriptionCell != null) {
                product.setDescription(descriptionCell.toString());  // Convert cell to string
            } else {
                product.setDescription("");  // Set a default value or handle as needed
            }

            // Handle price
            Cell priceCell = row.getCell(2);
            if (priceCell != null) {
                switch (priceCell.getCellType()) {
                    case NUMERIC:
                        product.setPrice(priceCell.getNumericCellValue());
                        break;
                    case STRING:
                        try {
                            product.setPrice(Double.parseDouble(priceCell.getStringCellValue()));
                        } catch (NumberFormatException e) {
                            product.setPrice(0.0);  // Handle invalid number formats
                        }
                        break;
                    default:
                        product.setPrice(0.0);  // Handle other types or default value
                        break;
                }
            } else {
                product.setPrice(0.0);  // Set a default value or handle as needed
            }

            products.add(product);
        }

        productRepository.saveAll(products);
        workbook.close();
    }

    // Method to retrieve all products
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    // Method to convert all products to JSON format
    public String downloadProductsAsJson() {
        List<Product> products = findAllProducts();
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(products);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting products to JSON: " + e.getMessage());
        }
    }

}
