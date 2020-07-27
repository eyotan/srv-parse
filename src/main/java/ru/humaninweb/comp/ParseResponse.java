package ru.humaninweb.comp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.math.BigDecimal;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import ru.humaninweb.dto.ResponseDto;

/**
 *
 * @author vit
 */
@Log4j2
@Component
public class ParseResponse {

    public String create(BigDecimal reqId) {
        try {
            XmlMapper xml = new XmlMapper();
            ResponseDto dto = new ResponseDto();
            dto.setReqId(reqId);
            return xml.writeValueAsString(dto);
        } catch (JsonProcessingException ex) {
            log.error(ex);
        }
        return "";
    }

    public String create(BigDecimal reqId, String error) {
        try {
            XmlMapper xml = new XmlMapper();
            ResponseDto dto = new ResponseDto();
            dto.setReqId(reqId);
            dto.setError(error);
            return xml.writeValueAsString(dto);
        } catch (JsonProcessingException ex) {
            log.error(ex);
        }
        return "";
    }
}
