package com.project.Controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.DTO.EmailRequest;
import com.project.Service.ContactService;

@RestController
@RequestMapping("/contact")
public class ContactController {
	@Autowired
    private ContactService emailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest req) {
        emailService.sendMail(req.getTo(), req.getSubject(), req.getBody());
        return ResponseEntity.ok().build();
    }
}
