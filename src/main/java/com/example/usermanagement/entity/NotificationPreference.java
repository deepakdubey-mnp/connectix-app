package com.example.usermanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_preference")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private Boolean offersEnabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean saleEnabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean orderStatusEnabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean limitedStockEnabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean systemDowntimeEnabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean personalizedEnabled = true;

    @PrePersist
    public void applyDefaults() {
        if (offersEnabled == null) offersEnabled = true;
        if (saleEnabled == null) saleEnabled = true;
        if (orderStatusEnabled == null) orderStatusEnabled = true;
        if (limitedStockEnabled == null) limitedStockEnabled = true;
        if (systemDowntimeEnabled == null) systemDowntimeEnabled = true;
        if (personalizedEnabled == null) personalizedEnabled = true;
    }
}
