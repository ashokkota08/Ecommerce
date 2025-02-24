package com.codegnan.cgecom.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    @Value("${razorpay.key}")
    private String razorpayKey;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    public Order createOrder(double amount, String currency, String receipt) throws Exception {
       
        RazorpayClient client = new RazorpayClient(razorpayKey, razorpaySecret);

     
        JSONObject options = new JSONObject();
        options.put("amount", (int) (amount * 100)); 
        options.put("currency", currency);
        options.put("receipt", receipt);

 
        return client.orders.create(options);
    }
}
