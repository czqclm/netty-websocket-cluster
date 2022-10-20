package org.zhiqi.netty.websocket.utils;

import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 简易 redis 工具类
 *
 * @author zhiqi
 * @version 1.0
 */
@Component
@AllArgsConstructor
public class EasyRedisUtils {

    private StringRedisTemplate stringRedisTemplate;

    public static final String DELIMITER = ":";

    public static final String WILDCARD = "*";


    public <T> T oGet(String key, Class<T> clazz) {
        return stringToBean(stringRedisTemplate.opsForValue().get(key), clazz);
    }

    public <T> void oSet(String key, @NonNull T value) {
        stringRedisTemplate.opsForValue().set(key, beanToString(value));
    }

    public <T> void oSet(String key, @NonNull T value, Long timeout, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, beanToString(value), timeout, timeUnit);
    }

    /**
     * 根据前缀获取所有的键
     *
     * @param prefixKey 前缀
     * @param clazz     类型
     * @param <T>       泛型
     * @return Map<String, T>
     */
    public <T> Map<String, T> oGetMap(String prefixKey, Class<T> clazz) {
        if (StringUtils.isEmpty(prefixKey)) {
            return null;
        }
        if (!prefixKey.contains(WILDCARD)) {
            prefixKey = prefixKey.endsWith(DELIMITER) ? prefixKey + WILDCARD : prefixKey + DELIMITER + WILDCARD;
        }
        Set<String> keySet = stringRedisTemplate.keys(prefixKey);
        if (CollectionUtils.isEmpty(keySet)) {
            return null;
        }
        Map<String, T> map = new HashMap<>();
        for (String key : keySet) {
            map.put(key, oGet(key, clazz));
        }
        return map;
    }

    public <T> Set<T> sGet(String key, Class<T> clazz) {
        Set<String> range = stringRedisTemplate.opsForSet().members(key);
        if (CollectionUtils.isEmpty(range)) {
            return null;
        }
        Set<T> set = new HashSet<>();
        for (String value : range) {
            set.add(stringToBean(value, clazz));
        }
        return set;
    }

    public <T> void sAdd(String key, @NonNull T value) {
        stringRedisTemplate.opsForSet().add(key, beanToString(value));
    }

    public <T> void sRemove(String key, @NonNull T value) {
        stringRedisTemplate.opsForSet().remove(key, beanToString(value));
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    public <T> Map<String, T> mGet(String key, Class<T> clazz) {
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(key);
        Map<String, T> resultMap = new HashMap<>();
        map.forEach((k, v) -> {
            resultMap.put(k.toString(), stringToBean(v.toString(), clazz));
        });
        return resultMap;
    }

    public <T> T mGetByObjectKey(String key, String objectKey, Class<T> clazz) {
        return stringToBean(stringRedisTemplate.opsForHash().get(key, objectKey), clazz);
    }

    public <T> void mPush(String key, @NonNull Map<String, T> map) {
        Map<String, String> resultMap = new HashMap<>();
        map.forEach((k, v) -> {
            resultMap.put(k, beanToString(v));
        });
        stringRedisTemplate.opsForHash().putAll(key, resultMap);
    }

    public <T> void mPush(String key, @NonNull String objectKey, @NonNull T value) {
        stringRedisTemplate.opsForHash().put(key, objectKey, beanToString(value));
    }

    public void mRemoveObject(String key, String objectKey) {
        stringRedisTemplate.opsForHash().delete(key, objectKey);
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间（秒）
     * @return true / false
     */
    public Boolean expire(String key, Long time, TimeUnit timeUnit) {
        return stringRedisTemplate.expire(key, time, timeUnit);
    }

    private <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == Integer.class) {
            return "" + value;
        } else if (clazz == Long.class) {
            return "" + value;
        } else if (clazz == String.class) {
            return (String) value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    public <T> T stringToBean(Object value, Class<T> clazz) {
        if (Objects.isNull(value)) {
            return null;
        }
        return stringToBean(value.toString(), clazz);
    }

    private <T> T stringToBean(String value, Class<T> clazz) {
        if (value == null || value.length() == 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(value);
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(value);
        } else if (clazz == String.class) {
            return (T) value;
        } else {
            return JSON.parseObject(value, clazz);
        }
    }

}
