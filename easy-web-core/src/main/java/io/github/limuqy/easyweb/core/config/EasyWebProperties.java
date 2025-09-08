package io.github.limuqy.easyweb.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "easy-web")
public class EasyWebProperties {
    private RowIdProperties rowId;
}
