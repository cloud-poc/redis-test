package org.akj.redis.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Embeddable
public class Amount implements Serializable {
	@Length(min = 3, max = 3)
	private String currency = "CNY";

	@NotNull
	private BigDecimal amount;
}