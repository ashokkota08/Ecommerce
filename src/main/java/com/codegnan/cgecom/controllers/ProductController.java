package com.codegnan.cgecom.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.codegnan.cgecom.model.Product;
import com.codegnan.cgecom.model.User;
import com.codegnan.cgecom.service.iface.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProductController {

    private final ProductService productService;

    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

  
    @GetMapping("/products")
    public String showProductList(HttpSession session, Model model) {
       
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
           
            return "redirect:/login";
        }

 
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "product-list";
    }

 
    @GetMapping("/products/add")
    public String showAddProductPage(HttpSession session) {
      
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !"ADMIN".equals(loggedInUser.getRole())) {
          
            return "redirect:/login";
        }

      
        return "add-product"; 
    }
   
    
    
    
    
    @PostMapping("/products/add")
    public String addNewProduct(@ModelAttribute Product product) {
        try {
           
            if (product.getImageFile() != null && !product.getImageFile().isEmpty()) {
                String imagePath = productService.saveProductImage(product.getImageFile());
                product.setImagePath(imagePath); 
            }

           
            productService.saveProduct(product);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/products/add?error=FileUploadFailed"; 
        }

        return "redirect:/products"; 
    }
    
    
    /*

    // POST mapping to handle form submission for adding a product
    @PostMapping("/products/add")
    public String addNewProduct(@ModelAttribute Product product) {
        productService.saveProduct(product);
        return "redirect:/products"; // Redirect to the product list after adding
    }
    */
    
    
    
    
    

    // New GET mapping for /products/edit/{id} to show the edit form
    @GetMapping("/products/edit/{id}")
    public String showEditProductPage(@PathVariable int id, HttpSession session, Model model) {
        // Check if the user is logged in and if they are an admin
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !"ADMIN".equals(loggedInUser.getRole())) {
            // If not logged in or not an admin, redirect to login
            return "redirect:/login";
        }

        // Fetch the product from the database using the productService
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/products?error=ProductNotFound";
        }

        // Add the product to the model to be used in the form
        model.addAttribute("product", product);
        return "edit-product"; // JSP file for editing a product
    }
    
    
    
    // POST mapping for updating a product
    @PostMapping("/products/edit/{id}")
    public String updateProduct(@PathVariable int id, @ModelAttribute Product product) {
        try {
            // Check if the product exists
            Product existingProduct = productService.getProductById(id);
            if (existingProduct == null) {
                return "redirect:/products?error=ProductNotFound";
            }

            // If a new image is uploaded, save it and update the image path
            if (product.getImageFile() != null && !product.getImageFile().isEmpty()) {
                String imagePath = productService.saveProductImage(product.getImageFile());
                product.setImagePath(imagePath);
            } else {
                // If no new image is uploaded, keep the old image path
                product.setImagePath(existingProduct.getImagePath());
            }

            // Set the ID of the existing product to update it
            product.setId(id);

            // Save the updated product
            productService.saveProduct(product);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/products/edit/" + id + "?error=FileUploadFailed"; // Redirect with error message
        }

        return "redirect:/products"; // Redirect to the product list after updating
    }
    
    
    // DELETE mapping for removing a product (you can call this from a delete button on the product list page)
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
        return "redirect:/products"; // Redirect to the product list after deletion
    }
    
    
    
    
    
}
