package io.github.limuqy.easyweb.cache.template;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.limuqy.easyweb.core.constant.Constant;
import io.github.limuqy.easyweb.core.exception.BusinessException;
import io.github.limuqy.easyweb.core.util.JsonUtil;
import io.github.limuqy.easyweb.core.util.ObjectUtil;
import io.github.limuqy.easyweb.core.util.StringUtil;
import io.github.limuqy.easyweb.core.util.TypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheTemplate {

    private final StringRedisTemplate stringRedisTemplate;

    public String getString(String module, String key) {
        return get(module, key, String.class);
    }

    public Long getLong(String module, String key) {
        return get(module, key, Long.class);
    }

    public Integer getInt(String module, String key) {
        return get(module, key, Integer.class);
    }

    public Long getIncrement(String module, String key) {
        return this.getIncrement(module, key, 1L, -1, TimeUnit.SECONDS);
    }

    public Long getIncrement(String module, String key, long delta) {
        return this.getIncrement(module, key, delta, -1, TimeUnit.SECONDS);
    }

    public Long getIncrement(String module, String key, int timeout, TimeUnit timeUnit) {
        return this.getIncrement(module, key, 1L, timeout, timeUnit);
    }

    private Long getIncrement(String module, String key, long delta, int timeout, TimeUnit timeUnit) {
        Long value;
        try {
            value = stringRedisTemplate.opsForHash().increment(cacheModule(module), key, delta);
            expire(cacheKey(module, key), timeout, timeUnit);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public Object get(String module, String key) {
        return get(module, key, Object.class);
    }

    public <T> T get(String module, String key, Class<T> clazz) {
        T result;
        Object value = stringRedisTemplate.opsForHash().get(cacheModule(module), key);
        //基本类型
        if (TypeUtil.isBasicType(clazz)) {
            result = TypeUtil.convert(value, clazz);
        } else {
            result = JsonUtil.parseObject(String.valueOf(value), clazz);
        }
        return result;
    }

    public <T> T get(String module, String key, TypeReference<T> type) {
        T result;
        Object value = stringRedisTemplate.opsForHash().get(cacheModule(module), key);
        result = JsonUtil.parseObject(String.valueOf(value), type);
        return result;
    }

    public void put(String module, String key, Object value) {
        put(module, key, value, null, null);
    }

    public void put(String module, String key, Object value, Long time) {
        put(module, key, value, time, null);
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param module   module
     * @param key      键
     * @param value    值
     * @param time     时间（秒） time要大于0 如果time小于等于0 将设置无限期
     * @param timeUnit 时间类型
     */
    public void put(String module, String key, Object value, Long time, TimeUnit timeUnit) {
        if (Objects.nonNull(value) && !TypeUtil.isBasicType(value.getClass())) {
            value = JsonUtil.toJSONString(value);
        }
        if (time != null && time > 0) {
            stringRedisTemplate.opsForHash().put(cacheModule(module), key, value);
            expire(cacheModule(module), key, time, ObjectUtil.getNotEmptyElse(timeUnit, TimeUnit.MINUTES));
        } else {
            stringRedisTemplate.opsForHash().put(cacheModule(module), key, value);
        }
    }

    public void delete(String module, String... keys) {
        stringRedisTemplate.opsForHash().delete(this.cacheModule(module), (Object[]) keys);
    }

    public void deleteByValue(String module, String key, String value) {
        if (StringUtil.isEmpty(value)) {
            return;
        }
        String redisValue = this.getString(module, key);
        if (value.equals(redisValue)) {
            this.delete(module, key);
        }
    }

    public String cacheModule(String module) {
        if (module == null) {
            return Constant.DEFAULT_REDIS_MODULE_NAME;
        }
        return String.format("%s:%s", Constant.DEFAULT_REDIS_MODULE_NAME, module);
    }

    public String cacheKey(String module, String key) {
        return String.format("%s:%s", cacheModule(module), key);
    }

    /**
     * 重新设置key的值，可以用于锁(过期时间默认按秒)
     *
     * @param module module
     * @param key    键
     * @param value  值
     * @param time   时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return 是否存在
     */
    public boolean setIfAbsent(String module, String key, Object value, long time) {
        return this.setIfAbsent(module, key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 重新设置key的值，可以用于锁
     *
     * @param module   module
     * @param key      键
     * @param value    值
     * @param time     时间 time要大于0 如果time小于等于0 将设置无限期
     * @param timeUnit 时间类型
     * @return 是否存在
     */
    public boolean setIfAbsent(String module, String key, Object value, long time, TimeUnit timeUnit) {
        return this.setIfAbsent(this.cacheKey(module, key), value, time, timeUnit);
    }

    private boolean setIfAbsent(String key, Object value, long time, TimeUnit timeUnit) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(key, String.valueOf(value), time, timeUnit));
    }

    /**
     * 重新设置key的值，可以用于锁(不删除的话，当前锁永不过期)
     *
     * @param module module
     * @param key    键
     * @param value  值
     * @return 是否存在
     */
    public boolean setIfAbsent(String module, String key, Object value) {
        return this.setIfAbsent(this.cacheKey(module, key), value, -1, TimeUnit.SECONDS);
    }

    /**
     * 获取下一个唯一ID
     *
     * @param module    module
     * @param keyPrefix 这个key 可以看做是业务key，不同的业务对应不同的key
     * @return 唯一序列ID
     */
    public Long nextId(String module, String keyPrefix) {
        if (StringUtils.isEmpty(keyPrefix)) {
            throw new BusinessException("business key can not empty！");
        }
        // 2.2 自增长
        return stringRedisTemplate.opsForHash().increment(this.cacheModule(module), keyPrefix, 1);
    }

    /**
     * 指定缓存失效时间
     *
     * @param module module
     * @param key    键
     * @param time   时间(秒)
     * @return true:设置成功，false：设置失败
     */
    public boolean expire(String module, String key, long time) {
        return this.expire(module, key, time, TimeUnit.SECONDS);
    }

    /**
     * 指定缓存失效时间
     *
     * @param module   module
     * @param key      键
     * @param time     时间(秒)
     * @param timeUnit 时间单位
     * @return true:设置成功，false：设置失败
     */
    public boolean expire(String module, String key, long time, TimeUnit timeUnit) {
        return this.expire(this.cacheKey(module, key), time, timeUnit);
    }

    private boolean expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                stringRedisTemplate.opsForHash().getOperations().expire(key, time, ObjectUtil.getNotEmptyElse(timeUnit, TimeUnit.MINUTES));
            }
            return true;
        } catch (Exception e) {
            log.error("缓存失效时间设置失败：", e);
            return false;
        }
    }

    public Long getExpire(String module, String key, TimeUnit timeUnit) {
        return stringRedisTemplate.opsForHash().getOperations().getExpire(this.cacheKey(module, key), timeUnit);
    }

    public long getExpire(String module, String key) {
        return this.getExpire(module, key, TimeUnit.MILLISECONDS);
    }

}
