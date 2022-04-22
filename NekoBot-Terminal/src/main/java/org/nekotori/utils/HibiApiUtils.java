package org.nekotori.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.nekotori.entity.SauceNaoData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

//@Component
@Deprecated
public class HibiApiUtils {


    private static String sauceNao;

    @Value("${api.hibiapi.sauce}")
    public void setSauceNao(String sauceNao) {
        HibiApiUtils.sauceNao = sauceNao;
    }

    public List<SauceNaoData> queryImage(byte[] image) {
        HttpResponse file = HttpRequest.post(sauceNao).form("file", image, "file.jpg").setConnectionTimeout(20 * 1000).setReadTimeout(20 * 1000).execute();
        if (!JSONUtil.isJson(file.body())) return new ArrayList<>();
        JSONObject jsonObject = JSONUtil.parseObj(file.body());
        if (ObjectUtils.isEmpty(jsonObject) || jsonObject.isEmpty()) return new ArrayList<>();
        JSONArray results = jsonObject.getJSONArray("results");
        if (ObjectUtils.isEmpty(results) || results.isEmpty()) return new ArrayList<>();
        int size = results.size();
        List<SauceNaoData> sauceNaoDataList = new ArrayList<>();
        for (int i = 0; i < size && i < 3; i++) {
            SauceNaoData sauceNaoData = new SauceNaoData();
            JSONObject jsonObject1 = JSONUtil.parseObj(results.get(i));
            JSONObject header = jsonObject1.getJSONObject("header");
            JSONObject data = jsonObject1.getJSONObject("data");
            sauceNaoData.setSimilarity(header.get("similarity").toString());
            sauceNaoData.setThumbnailUrl(header.get("thumbnail").toString());
            sauceNaoData.setTittle(ObjectUtils.isEmpty(data.get("title")) ? "" : data.get("title").toString());
            sauceNaoData.setExtUrls(CollectionUtils.isEmpty(data.getJSONArray("ext_urls")) ? new ArrayList<>() : data.getJSONArray("ext_urls").toList(String.class));
            sauceNaoDataList.add(sauceNaoData);
        }
        return sauceNaoDataList;
    }
}
