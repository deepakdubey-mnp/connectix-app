package com.example.usermanagement.service;

import com.example.usermanagement.dto.CreateNotificationRequestDto;
import com.example.usermanagement.dto.NotificationListResponseDto;
import com.example.usermanagement.dto.NotificationPreferenceDto;
import com.example.usermanagement.dto.NotificationPublishResponseDto;
import com.example.usermanagement.dto.NotificationResponseDto;
import com.example.usermanagement.entity.Notification;
import com.example.usermanagement.entity.NotificationPreference;
import com.example.usermanagement.entity.NotificationType;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.NotificationNotFoundException;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.repository.NotificationPreferenceRepository;
import com.example.usermanagement.repository.NotificationRepository;
import com.example.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final UserRepository userRepository;

    @Transactional
    public NotificationPublishResponseDto publishNotification(CreateNotificationRequestDto request) {
        List<User> targets = resolveTargets(request);
        int publishedCount = 0;

        for (User user : targets) {
            NotificationPreference preference = getOrCreatePreference(user);
            if (!isNotificationAllowed(request.getType(), preference)) {
                continue;
            }

            Notification notification = Notification.builder()
                    .user(user)
                    .type(request.getType())
                    .title(request.getTitle())
                    .message(request.getMessage())
                    .read(false)
                    .build();
            notificationRepository.save(notification);
            publishedCount++;
        }

        log.info("Published {} notifications for type {}", publishedCount, request.getType());
        return NotificationPublishResponseDto.builder()
                .status("success")
                .message("Notification published successfully")
                .publishedCount(publishedCount)
                .build();
    }

    public NotificationListResponseDto getMyNotifications(NotificationType type, Boolean unreadOnly) {
        User user = getAuthenticatedUser();

        List<Notification> notifications;
        if (Boolean.TRUE.equals(unreadOnly)) {
            notifications = notificationRepository.findByUserAndReadOrderByCreatedAtDesc(user, false);
        } else {
            notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        }

        if (type != null) {
            notifications = notifications.stream()
                    .filter(notification -> notification.getType() == type)
                    .collect(Collectors.toList());
        }

        long unreadCount = notificationRepository.countByUserAndRead(user, false);

        return NotificationListResponseDto.builder()
                .status("success")
                .message("Notifications fetched successfully")
                .unreadCount(unreadCount)
                .notifications(notifications.stream().map(this::toResponseDto).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public NotificationResponseDto markAsRead(Long notificationId) {
        User user = getAuthenticatedUser();
        Notification notification = notificationRepository.findByIdAndUser(notificationId, user)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with id: " + notificationId));

        notification.setRead(true);
        Notification updated = notificationRepository.save(notification);
        return toResponseDto(updated);
    }

    public NotificationPreferenceDto getMyPreferences() {
        User user = getAuthenticatedUser();
        NotificationPreference preference = getOrCreatePreference(user);
        return toPreferenceDto(preference);
    }

    @Transactional
    public NotificationPreferenceDto updateMyPreferences(NotificationPreferenceDto request) {
        User user = getAuthenticatedUser();
        NotificationPreference preference = getOrCreatePreference(user);

        if (request.getOffersEnabled() != null) preference.setOffersEnabled(request.getOffersEnabled());
        if (request.getSaleEnabled() != null) preference.setSaleEnabled(request.getSaleEnabled());
        if (request.getOrderStatusEnabled() != null) preference.setOrderStatusEnabled(request.getOrderStatusEnabled());
        if (request.getLimitedStockEnabled() != null) preference.setLimitedStockEnabled(request.getLimitedStockEnabled());
        if (request.getSystemDowntimeEnabled() != null) preference.setSystemDowntimeEnabled(request.getSystemDowntimeEnabled());
        if (request.getPersonalizedEnabled() != null) preference.setPersonalizedEnabled(request.getPersonalizedEnabled());

        NotificationPreference updated = notificationPreferenceRepository.save(preference);
        return toPreferenceDto(updated);
    }

    private List<User> resolveTargets(CreateNotificationRequestDto request) {
        if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
            return request.getUserIds().stream()
                    .filter(Objects::nonNull)
                    .map(userId -> userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId)))
                    .collect(Collectors.toList());
        }

        if (request.getRole() != null) {
            return userRepository.findByRole(request.getRole());
        }

        return new ArrayList<>(userRepository.findAll());
    }

    private boolean isNotificationAllowed(NotificationType type, NotificationPreference preference) {
        return switch (type) {
            case OFFER -> Boolean.TRUE.equals(preference.getOffersEnabled());
            case SALE -> Boolean.TRUE.equals(preference.getSaleEnabled());
            case ORDER_STATUS_CHANGE -> Boolean.TRUE.equals(preference.getOrderStatusEnabled());
            case LIMITED_STOCK -> Boolean.TRUE.equals(preference.getLimitedStockEnabled());
            case SYSTEM_DOWNTIME -> Boolean.TRUE.equals(preference.getSystemDowntimeEnabled());
            case PERSONALIZED -> Boolean.TRUE.equals(preference.getPersonalizedEnabled());
        };
    }

    private NotificationResponseDto toResponseDto(Notification notification) {
        return NotificationResponseDto.builder()
                .notificationId("ntf_" + notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    private NotificationPreferenceDto toPreferenceDto(NotificationPreference preference) {
        return NotificationPreferenceDto.builder()
                .offersEnabled(preference.getOffersEnabled())
                .saleEnabled(preference.getSaleEnabled())
                .orderStatusEnabled(preference.getOrderStatusEnabled())
                .limitedStockEnabled(preference.getLimitedStockEnabled())
                .systemDowntimeEnabled(preference.getSystemDowntimeEnabled())
                .personalizedEnabled(preference.getPersonalizedEnabled())
                .build();
    }

    private NotificationPreference getOrCreatePreference(User user) {
        return notificationPreferenceRepository.findByUser(user)
                .orElseGet(() -> notificationPreferenceRepository.save(
                        NotificationPreference.builder().user(user).build()
                ));
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotFoundException("Authenticated user not found");
        }

        String phoneNumber = authentication.getName();
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException("User not found for phone: " + phoneNumber));
    }
}
