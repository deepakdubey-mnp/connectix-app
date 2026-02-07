package com.example.usermanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_details", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"productName", "company"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetails {
    @Id
    private Long id;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private String productName;

    @Column
    private String productDescription;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Enumerated(EnumType.STRING)
    private ProductSubType productSubType;

    private String imageLocation;

    @Enumerated(EnumType.STRING)
    private QuantityType quantityType;

}
