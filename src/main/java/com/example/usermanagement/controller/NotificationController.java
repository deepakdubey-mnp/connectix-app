package com.example.usermanagement.controller;

import com.example.usermanagement.dto.CreateNotificationRequestDto;
import com.example.usermanagement.dto.NotificationListResponseDto;
import com.example.usermanagement.dto.NotificationPreferenceDto;
import com.example.usermanagement.dto.NotificationPublishResponseDto;
import com.example.usermanagement.dto.NotificationResponseDto;
import com.example.usermanagement.entity.NotificationType;
import com.example.usermanagement.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEALER')")
    public ResponseEntity<NotificationPublishResponseDto> publishNotification(
            @Valid @RequestBody CreateNotificationRequestDto request) {
        log.info("Publish notification request for type: {}", request.getType());
        NotificationPublishResponseDto response = notificationService.publishNotification(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<NotificationListResponseDto> getMyNotifications(
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) Boolean unreadOnly) {
        NotificationListResponseDto response = notificationService.getMyNotifications(type, unreadOnly);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/{notificationId}/read")
    public ResponseEntity<NotificationResponseDto> markMyNotificationAsRead(@PathVariable Long notificationId) {
        NotificationResponseDto response = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/preferences")
    public ResponseEntity<NotificationPreferenceDto> getMyPreferences() {
        NotificationPreferenceDto response = notificationService.getMyPreferences();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/preferences")
    public ResponseEntity<NotificationPreferenceDto> updateMyPreferences(
            @RequestBody NotificationPreferenceDto request) {
        NotificationPreferenceDto response = notificationService.updateMyPreferences(request);
        return ResponseEntity.ok(response);
    }
}
