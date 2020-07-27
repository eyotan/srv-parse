package ru.humaninweb.prop;

import java.nio.file.Path;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author vit
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties
@PropertySource("file:prop/app.properties")
public class Prop {

    private Path reqDir;
    private Path respDir;
    
}
