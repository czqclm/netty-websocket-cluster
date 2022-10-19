package org.zhiqi.netty.websocket.utils;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

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
}
