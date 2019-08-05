package org.akj.redis.entity;

import lombok.Data;
import lombok.ToString;
import org.akj.redis.entity.Amount;
import org.akj.redis.entity.Product;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @BatchSize(size = 2)
    private List<Product> products;

    private Amount totalPrice;

    private String notes;
}
