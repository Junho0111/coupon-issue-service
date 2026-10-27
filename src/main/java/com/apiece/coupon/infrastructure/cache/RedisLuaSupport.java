package com.apiece.coupon.infrastructure.cache;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Arrays;
import java.util.List;

/**
 * Lua 스크립트 실행 헬퍼. 결과 타입별 캐스팅과 null 처리를 한곳에 모은다.
 */
public final class RedisLuaSupport {

    private RedisLuaSupport() {
    }

    @SuppressWarnings("rawtypes")
    public static RedisScript<List> listLuaScript(String path) {
        return RedisScript.of(new ClassPathResource(path), List.class);
    }

    public static RedisScript<Long> longLuaScript(String path) {
        return RedisScript.of(new ClassPathResource(path), Long.class);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<String> runForStrings(
            StringRedisTemplate redisTemplate,
            RedisScript<List> script,
            List<String> keys,
            Object... args
    ) {
        return (List<String>) redisTemplate.execute(script, keys, toStringArgs(args));
    }

    public static long runForLong(
            StringRedisTemplate redisTemplate,
            RedisScript<Long> script,
            List<String> keys,
            Object... args
    ) {
        Long result = redisTemplate.execute(script, keys, toStringArgs(args));
        if (result == null) {
            throw new IllegalStateException("Lua 결과 null");
        }
        return result;
    }

    private static Object[] toStringArgs(Object[] args) {
        return Arrays.stream(args).map(String::valueOf).toArray();
    }
}
