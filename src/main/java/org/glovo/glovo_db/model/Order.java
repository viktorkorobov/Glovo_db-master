package org.glovo.glovo_db.model;

import jakarta.persistence.*;
import java.util.*;

import lombok.*;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_datetime", nullable = false)
    private Date creationDateTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modification_datetime")
    private Date modificationDateTime;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "mobile", nullable = false, length = 20)
    private String mobile;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "Order_Product",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;
}



