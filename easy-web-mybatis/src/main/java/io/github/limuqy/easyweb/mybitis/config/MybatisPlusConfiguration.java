package io.github.limuqy.easyweb.mybitis.config;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.PostgreKeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import io.github.limuqy.easyweb.mybitis.handler.AutoFillHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AutoConfiguration
@ConditionalOnBean({SpringUtil.class, AutoFillHandler.class})
@EnableConfigurationProperties({MybatisPlusProperties.class, DataSourceProperties.class})
@RequiredArgsConstructor
public class MybatisPlusConfiguration {
    private final AutoFillHandler autoFillHandler;
    private final MybatisPlusProperties mybatisPlusProperties;
    private final DataSourceProperties dataSourceProperties;

    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig conf = mybatisPlusProperties.getGlobalConfig();
        conf.setMetaObjectHandler(autoFillHandler);
        conf.setIdentifierGenerator(new DefaultIdentifierGenerator(RandomUtil.randomLong(1, 31), RandomUtil.randomLong(1, 31)));

        List<IKeyGenerator> keyGeneratorList = new ArrayList<>();
        keyGeneratorList.add(new PostgreKeyGenerator());
        if (conf.getDbConfig() == null) {
            conf.setDbConfig(new GlobalConfig.DbConfig().setKeyGenerators(keyGeneratorList));
        } else {
            conf.getDbConfig().setKeyGenerators(keyGeneratorList);
        }
        return conf;
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        log.info("DB driver-class-name : {}", dataSourceProperties.getType().getName());
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}
