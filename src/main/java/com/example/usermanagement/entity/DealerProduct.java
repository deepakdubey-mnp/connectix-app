package com.example.usermanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dealer_product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DealerProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "dealerProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<DealerProductItem> items;
}
