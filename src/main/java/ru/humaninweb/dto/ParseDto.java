package ru.humaninweb.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author vit
 */
@Getter
@Setter
@EqualsAndHashCode
public class ParseDto {
  
    private BigDecimal reqId;
    private String reqLink;
    private LocalDateTime respTime;
    private String respLink;
    
}
