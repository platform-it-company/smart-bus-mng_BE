package egovframework.smartbusmng.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "custom")
@Getter @Setter
public class CustomProperties {
    private int pageUnit;
    private int pageSize;
}