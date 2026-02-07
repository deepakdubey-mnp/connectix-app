package com.example.usermanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dealer_product_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DealerProductItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_product_id")
    private DealerProduct dealerProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_details_id")
    private ProductDetails productDetails;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private Double price;
}
