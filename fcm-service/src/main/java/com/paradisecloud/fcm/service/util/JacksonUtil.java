package com.paradisecloud.fcm.service.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JacksonUtil {

    private static final Log logger = LogFactory.getLog(JacksonUtil.class);

    public static String parseString(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null)
                return leaf.asText();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static Long parseLong(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null)
                return leaf.asLong();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static List<String> parseStringList(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);

            if (leaf != null)
                return mapper.convertValue(leaf, new TypeReference<List<String>>() {
                });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static Integer parseInteger(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null)
                return leaf.asInt();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static List<Integer> parseIntegerList(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);

            if (leaf != null)
                return mapper.convertValue(leaf, new TypeReference<List<Integer>>() {
                });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    public static Boolean parseBoolean(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null)
                return leaf.asBoolean();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static Short parseShort(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                Integer value = leaf.asInt();
                return value.shortValue();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static Byte parseByte(String body, String field) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                Integer value = leaf.asInt();
                return value.byteValue();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T parseObject(String body, String field, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            node = mapper.readTree(body);
            node = node.get(field);
            return mapper.treeToValue(node, clazz);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T parseObject(String body, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            node = mapper.readTree(body);
            return mapper.treeToValue(node, clazz);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T parseObject(String body, String field, TypeReference<T> toValueTypeRef) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                return mapper.convertValue(leaf, toValueTypeRef);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static Object toNode(String json) {
        if (json == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {

            return mapper.readTree(json);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public static Map<String, String> toMap(String data) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(data, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String toJson(Object data) {
        return toJson(data, null);
    }

    /**
     * 内容为null的剔除
     *
     * @param data
     * @return
     */
    public static String toJsonNonNULL(Object data) {
        return toJson(data, JsonInclude.Include.NON_NULL);
    }

    /**
     * 内容为null 或者 为""的剔除
     *
     * @param data
     * @return
     */
    public static String toJsonNonEmpty(Object data) {
        return toJson(data, JsonInclude.Include.NON_EMPTY);
    }

    /**
     * 内容为默认属性的序列化
     *
     * @param data
     * @return
     */
    public static String toJsonNonDefault(Object data) {
        return toJson(data, JsonInclude.Include.NON_DEFAULT);
    }

    /**
     *
     * @param data
     * @param jsonInclude 内容为null的剔除
     * @return
     */
    public static String toJson(Object data, JsonInclude.Include jsonInclude) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (jsonInclude != null) {
                objectMapper.setSerializationInclusion(jsonInclude);
            }
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
