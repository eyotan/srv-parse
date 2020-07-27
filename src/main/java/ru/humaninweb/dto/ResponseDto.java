package ru.humaninweb.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author vit
 */
@Getter
@Setter
@JacksonXmlRootElement(localName = "Response")
public class ResponseDto {

    @JacksonXmlProperty(localName = "ID")
    private BigDecimal reqId;
    
    @JacksonXmlProperty(localName = "Error")
    private String error;

}
