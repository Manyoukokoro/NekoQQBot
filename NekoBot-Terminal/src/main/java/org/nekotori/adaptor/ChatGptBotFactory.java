package org.nekotori.adaptor;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.plexpt.chatgpt.Chatbot;

public class ChatGptBotFactory {


    private static long cfClearanceLastTime= 0L;

    private static String userAgent;

    private static String cfClearance;


    public static Chatbot NEW_INSTANCE(String token){
        if(System.currentTimeMillis()-cfClearanceLastTime>30*60*100L) {
            String url = "http://127.0.0.1:8000/challenge";
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("timeout", 20);
            jsonObject.set("url", "https://chat.openai.com/api/auth/session");
            String body = HttpUtil.createPost(url)
                    .header("Content-Type", "application/json")
                    .body(jsonObject.toString())
                    .execute().body();
            JSONObject responseJson = JSONUtil.parseObj(body);
            userAgent = responseJson.getStr("user_agent");
            System.out.println(userAgent);
            JSONObject cookies = responseJson.getJSONObject("cookies");
            cfClearance = cookies.getStr("cf_clearance");
            System.out.println(cfClearance);
        }
        return new Chatbot(token, cfClearance, userAgent);
    }
}
