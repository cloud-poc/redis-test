package org.akj.redis.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Table(name = "products")
@Data
public class Product implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(nullable = false, unique = true)
    @NotNull
    private String code;

    @Column(nullable = false)
    private String name;

    @Embedded
    @Valid
    private Amount price;

    private String description;

    @Transient
    private boolean inStock = false;
}