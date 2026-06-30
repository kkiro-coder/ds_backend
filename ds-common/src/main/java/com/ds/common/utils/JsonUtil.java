package com.ds.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * JSON 工具类
 *
 * @author ds
 */
public final class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    private JsonUtil() {
    }

    // ==================== 序列化 ====================

    /**
     * 对象转 JSON 字符串
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }

    /**
     * 对象转 JSON 字符串（异常时返回默认值）
     */
    public static String toJsonOrDefault(Object obj, String defaultValue) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return defaultValue;
        }
    }

    /**
     * 对象转格式化 JSON 字符串
     */
    public static String toPrettyJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }

    // ==================== 反序列化 ====================

    /**
     * JSON 字符串转对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }

    /**
     * JSON 字符串转 List
     */
    public static <T> List<T> parseArray(String json, Class<T> elementClass) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, elementClass));
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    /**
     * JSON 字符串转 List（使用 TypeReference）
     */
    public static <T> List<T> parseArray(String json, TypeReference<List<T>> typeRef) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return MAPPER.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    /**
     * JSON 字符串转 Map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseMap(String json) {
        return parseObject(json, Map.class);
    }

    /**
     * JSON 字符串转对象（异常时返回 null）
     */
    public static <T> T parseObjectQuietly(String json, Class<T> clazz) {
        try {
            return parseObject(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * JSON 字符串转 List（异常时返回空列表）
     */
    public static <T> List<T> parseArrayQuietly(String json, Class<T> elementClass) {
        try {
            return parseArray(json, elementClass);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
