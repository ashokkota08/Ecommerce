package com.codegnan.cgecom.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codegnan.cgecom.model.Order;
import com.codegnan.cgecom.model.OrderItem;
import com.codegnan.cgecom.model.Product;
import com.codegnan.cgecom.model.User;
import com.codegnan.cgecom.service.iface.OrderService;
import com.codegnan.cgecom.service.iface.ProductService;
import com.codegnan.cgecom.service.impl.RazorpayService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final ProductService productService;
    private final OrderService orderService;
    private final RazorpayService razorpayService;

    public CartController(ProductService productService, OrderService orderService,RazorpayService razorpayService) {
        this.productService = productService;
        this.orderService = orderService;
        this. razorpayService=razorpayService;
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
       
        List<OrderItem> cartItems = getCartItemsFromSession(session);

        double totalPrice = 0;
        for (OrderItem item : cartItems) {
            totalPrice += item.getPrice() * item.getQuantity();
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        return "cart"; 
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam int productId, @RequestParam int quantity, HttpSession session) {
      
        List<OrderItem> cartItems = getCartItemsFromSession(session);

  
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Invalid product ID");
        }

       
        boolean productExists = false;
        for (OrderItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                item.setQuantity(item.getQuantity() + quantity);
                productExists = true;
                break;
            }
        }

      
        if (!productExists) {
            OrderItem newItem = new OrderItem();
            newItem.setProduct(product);
            newItem.setPrice(product.getPrice());
            newItem.setQuantity(quantity);
            cartItems.add(newItem);
        }

      
        session.setAttribute("cartItems", cartItems);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam int productId, @RequestParam int quantity, HttpSession session) {
        List<OrderItem> cartItems = getCartItemsFromSession(session);

        
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                if (quantity == 0) {
                	orderItems.add(item);
                } else {
                    item.setQuantity(quantity);
                }
            }
        }

    
        cartItems.removeAll(orderItems);

        session.setAttribute("cartItems", cartItems);
        
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<OrderItem> cartItems = (List<OrderItem>) session.getAttribute("cartItems");
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (cartItems == null || cartItems.isEmpty() || loggedInUser == null) {
            return "redirect:/cart";
        }

        
        Order order = new Order();
        order.setUser(loggedInUser);
        order.setOrderStatus("COMPLETED");

        double totalPrice = 0;
        for (OrderItem item : cartItems) {
            item.setOrder(order);
            totalPrice += item.getPrice() * item.getQuantity();
        }

     
        order.setTotalPrice(BigDecimal.valueOf(totalPrice));

        order.setOrderItems(cartItems);

      
        orderService.saveOrder(order);
        session.removeAttribute("cartItems");

        model.addAttribute("order", order);
        return "checkout-success"; 
    }
    
    @SuppressWarnings("unchecked")
    private List<OrderItem> getCartItemsFromSession(HttpSession session) {
        List<OrderItem> cartItems = (List<OrderItem>) session.getAttribute("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
            session.setAttribute("cartItems", cartItems);
        }
        return cartItems;
    }
    
    @GetMapping("/payment-success")
    public String showPaymentSuccessPage(HttpSession session, Model model) {
     
        session.removeAttribute("cartItems");

      
        model.addAttribute("message", "Your payment was successful, and your cart has been cleared!");

       
        return "payment-success";
    }
    
    
    @PostMapping("/razorpayOrder")
    @ResponseBody
    public Map<String, Object> createRazorpayOrder(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
        
            List<OrderItem> cartItems = getCartItemsFromSession(session);
            if (cartItems == null || cartItems.isEmpty()) {
                throw new IllegalStateException("Cart is empty");
            }

            double totalPrice = cartItems.stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();

         
            com.razorpay.Order razorpayOrder = razorpayService.createOrder(totalPrice, "INR", "OrderReceipt#123");

            response.put("id", razorpayOrder.get("id"));
            response.put("amount", razorpayOrder.get("amount"));
            response.put("currency", razorpayOrder.get("currency"));
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Failed to create Razorpay order: " + e.getMessage());
        }
        return response;
    }
    
    
    
}
