package org.nekotori.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String object2Json(Object o) {
        if (o == null)
            return null;

        String s = null;

        try {
            s = mapper.writeValueAsString(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static <T> List<String> listObject2ListJson(List<T> objects) {
        if (objects == null)
            return null;

        List<String> lists = new ArrayList<String>();
        for (T t : objects) {
            lists.add(JsonUtils.object2Json(t));
        }

        return lists;
    }

    public static <T> List<T> listJson2ListObject(List<String> jsons, Class<T> c) {
        if (jsons == null)
            return null;

        List<T> ts = new ArrayList<T>();
        for (String j : jsons) {
            ts.add(JsonUtils.json2Object(j, c));
        }

        return ts;
    }

    public static <T> T json2Object(String json, Class<T> c) {
        if (!StringUtils.hasLength(json))
            return null;

        T t = null;
        try {
            t = mapper.readValue(json, c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    @SuppressWarnings("unchecked")
    public static <T> T json2Object(String json, TypeReference<T> tr) {
        if (!StringUtils.hasLength(json))
            return null;

        T t = null;
        try {
            t = (T) mapper.readValue(json, tr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) t;
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }
}