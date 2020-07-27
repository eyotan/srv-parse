package ru.humaninweb.prop;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.humaninweb.dto.ParseDto;

/**
 *
 * @author vit
 */
@Configuration
public class ParseQueueProp {

    /**
     * Очередь новых файлов
     *
     * @return ConcurrentLinkedQueue(String)
     */
    @Bean
    public ConcurrentLinkedQueue<ParseDto> parseQueue() {
        return new ConcurrentLinkedQueue<>();
    }

}
