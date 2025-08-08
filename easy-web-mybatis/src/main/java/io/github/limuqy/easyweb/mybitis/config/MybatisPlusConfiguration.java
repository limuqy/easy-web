package io.github.limuqy.easyweb.mybitis.config;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.PostgreKeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import io.github.limuqy.easyweb.mybitis.handler.AutoFillHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@AutoConfiguration
public class MybatisPlusConfiguration {

    @Bean
    @ConditionalOnBean({AutoFillHandler.class, MybatisPlusProperties.class})
    public GlobalConfig globalConfig(AutoFillHandler autoFillHandler, MybatisPlusProperties mybatisPlusProperties) {
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
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}
