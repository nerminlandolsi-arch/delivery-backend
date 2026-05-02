package com.delivery.controller;

import com.delivery.dto.request.RequestDTOs.ChatbotRequest;
import com.delivery.dto.response.ResponseDTOs.ChatbotResponse;
import com.delivery.dto.response.ResponseDTOs.ApiResponse;
import com.delivery.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/livreur/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/question")
    public ResponseEntity<ApiResponse<ChatbotResponse>> poserQuestion(
            @RequestBody ChatbotRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(
                chatbotService.repondre(request.getQuestion(), userDetails.getUsername())
        ));
    }
}