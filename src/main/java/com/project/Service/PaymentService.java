package com.project.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Entity.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

@Service
public class PaymentService {

    @Value("${client_id}")
    private String clientId;

    @Value("${api_key}")
    private String apiKey;

    @Value("${return_url}")
    private String returnUrl;

    @Value("${cancel_url}")
    private String cancelUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    private String createSignature(Map<String, Object> data, String checksumKey) throws Exception {
        // Sắp xếp key theo alphabet
        String dataStr = data.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .filter(e -> e.getValue() != null && !e.getValue().toString().isEmpty())
                .map(e -> e.getKey() + "=" + e.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(checksumKey.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(dataStr.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String createPaymentLink(Order order) throws Exception {
        String url = "https://api-merchant.payos.vn/v2/payment-requests";

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("orderCode", order.getOrderId().longValue());
        body.put("amount", order.getTotalPrice().intValue());
        body.put("description", "Thanh toán đơn hàng #" + order.getOrderId());
        body.put("returnUrl", returnUrl + "?orderId=" + order.getOrderId());
        body.put("cancelUrl", cancelUrl + "?orderId=" + order.getOrderId());

        // Tạo signature đúng theo docs mới
        String signature = createSignature(body, "9e7bbebffd69c7178826e10c3420dbd0b863a42bdaf825065b3f281d9a25b0cb"); 
        body.put("signature", signature);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-client-id", clientId);
        headers.set("x-api-key", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            return (String) data.get("checkoutUrl");
        } else {
            throw new RuntimeException("PayOS error: " + response.getBody());
        }
    }

    // Check status
    public Map<String, Object> getPaymentInfo(Long orderCode) throws Exception {
        String url = "https://api-merchant.payos.vn/v2/payment-requests/" + orderCode;

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-client-id", clientId);
        headers.set("x-api-key", apiKey);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        return (Map<String, Object>) response.getBody().get("data");
    }
}