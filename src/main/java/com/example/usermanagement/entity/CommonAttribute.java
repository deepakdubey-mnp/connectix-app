package com.example.usermanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "common_attribute", uniqueConstraints = {
    @jakarta.persistence.UniqueConstraint(columnNames = {"key", "language"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonAttribute  {
    @Id
    private Long id;

    @Column(nullable = false)
    private String key;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String value;



}
