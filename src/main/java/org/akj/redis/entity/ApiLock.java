package org.akj.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "api_lock", uniqueConstraints = {@UniqueConstraint(columnNames = {"url", "apiKey", "businessDate"})})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiLock {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String apiKey;

    private String url;

    private LocalDate businessDate;
}
