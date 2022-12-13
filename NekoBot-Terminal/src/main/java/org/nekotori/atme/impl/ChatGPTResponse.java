package org.nekotori.atme.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import org.nekotori.adaptor.ChatBot;
import org.nekotori.adaptor.ChatGptAdaptor;
import org.nekotori.annotations.AtMe;
import org.nekotori.atme.NoAuthAtMeResponse;
import org.nekotori.common.InnerConstants;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@AtMe
public class ChatGPTResponse extends NoAuthAtMeResponse {


    private static Map<Member,ChatBot> targetMap = new HashMap<>();
    private static Map<Long, Long> lastQues = new HashMap<>();

    public static void deleteCache(){
        targetMap = new HashMap<>();
    }

    @Override
    public MessageChain response(GroupMessageEvent groupMessageEvent) {
        String chatGptConfS = FileUtil.readString(new File("chat-gpt.conf"), StandardCharsets.UTF_8);
        String key = "";
        try {
            JSONObject chatGptConf = JSONUtil.parseObj(chatGptConfS);
            key = chatGptConf.getStr("api-key");
        }catch (Exception e){
            return null;
        }
        Member sender = groupMessageEvent.getSender();
        long nowTime = System.currentTimeMillis();
        if (sender.getId() != InnerConstants.admin && lastQues.get(sender.getId())!=null && (nowTime-lastQues.get(sender.getId()))<60*1000L){
            return new MessageChainBuilder().append(new At(sender.getId()))
                    .append("\n")
                    .append("我检测到您曾经在不到1分钟之前于本群或其他群向我提问，请减缓频率防止给服务器带来过大压力").build();
        }
        if(targetMap.get(sender)==null){
            ChatBot chatbot = new ChatGptAdaptor(key);
            targetMap.put(sender,chatbot);
        }
        if(lastQues.get(sender.getId())!=null){
            lastQues.replace(sender.getId(),System.currentTimeMillis());
        }else {
            lastQues.put(sender.getId(),System.currentTimeMillis());
        }
        ChatBot chatbot = targetMap.get(sender);
        MessageChain messageChain = groupMessageEvent.getMessage();
        String s1 = messageChain.serializeToMiraiCode();
        String trim = s1.replaceAll("\\[.*]", "").trim();
        trim = trim.replaceFirst(".* ", "");
        if("重置".equals(trim)){
            return new MessageChainBuilder().append(chatbot.refresh() ? "重置成功" : "重置失败").build();
        }
        try {
            String message = chatbot.getReply(trim, sender.getGroup() + String.valueOf(sender.getId() + sender.getId()));
            return new MessageChainBuilder().append(new QuoteReply(groupMessageEvent.getMessage())).append("\n").append(message).build();
        }catch (Exception ignore){
        }
        return new MessageChainBuilder().append(new At(sender.getId())).append("\n").append("ChatGPT服务已离线").build();
    }

    public static void main(String[] args) {
        ChatBot chatBot = new ChatGptAdaptor("eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2R0NNIn0..P-bQztfaSTJDd2OC.vT1W652Vl1EigkWXPFuH6HPxnJ60FMlC0J8fDvp28yZNvlmAQ94ePYpssic0VJ2-AIm0ez2bJhhvaeiv7GR21mArknIX8QY1qF_ED5Tlg2BZXvbFQaNgxDt0lT1vncQNa1jKwTiY0NM7og0w4JayQt277mGL16UJz_U3zkAk8SgpkVXUxNpQ-5Jd59Mn5t3xhUQzVhkH2XzydMqfKFzU5tculAzA8Afir1dxJn4P9_4elIt2KWRoFtCdpApIOyQ4XZ9KtxsKiozh23Tp2ekEXjDuo_Y4ouQwX-IXkthA80hyIDrWZ7bQ946m40D49wrOZxkYzc-YThFdji2U7a47KIfF9d0-GAKxhKdok5tu7h1pyT0Q5KjE4noKwdn4wUiKn_kEouk4Sp0ydlDemolFGP8-jYn9Sa7O1enqKudjh3X0GDf6EKhBtV2RdK97Jc5fcuTH19hPCpPY4ML-la_S8J0Y7ARWND_WUwIXsGNjBt919PvbSBSC1umcIXgFqCG20kdGgsDCetEo5zcz-x3cXgEKMMNaYhaAmJV-gjYc3bRfmSvNwgDn6QPKAOnk1hUF4Q1pmVejWiTCq24p_n-EMupd88LBJrJgx8NVn-h6xgsy91D6wodU6aRB0uh6UWnTRwm-wIdiCUDcxwlcZGXukyzcnG-nu1GNZiIUvNKH1dqPwQj5kefCm2UIpXu-iYAftxBJxjyUqbD-tkNZUgstCwt4AXVCh3tDo_SP1dL53LaekxWtQJ2_51FMT5quRaIudcMRDXaEnN1DH2pciub9aTesLYFtaL2Ys9n3KHd0itFbKt9emi642_Hw9cIwDhTYqCJB5FkROTzL8axxEEQIyFZ9j-kS9dFxbTZ4JJVL1B95LEsH4l1kHOE56guuKsZWnHr7Uk06EtSmsxUoMNB2FQaADTSda8PLPFSG-Vv1SMcqkBhBjb9oW0amHobmEk0r6-3nkuJhx0Rywb_xCioSk1h52L1xdVdvhRi6j5VEFX3wRpwhLyRVGTD0getkhqXcPk1mLR11WLVvniNB3-ICMIX9EdTpMkdZkeM5OLgTZBiigIbgvV3N-Qhb1GSeFjH2ZhxVBxwBoKt1leWeF45E__6Q4yL83xh6M1AbOmR-D36UddxS1lZrTuO_stpE8C1zdmhMeEMkDKovBsUW9DTvqZY7qxpZNEUT_m7MoVXX22EKJf0poWc_ksOHGeOtUUdr_1Rwp84-49pW4Du2AfIMB4r1i5Ex3RjIl3896TNFQuGMkzd4KMBrxfmCLlmNcAVdydFdhBUmEAOh_rgc5ZnXXReQp9_0iWwbsa9isZ-OrZnyEd2uVpSUYJKWrAO38sdKb5-HX0nb-jUoSFgCP2sqk8nuSp8cIbOZvg9msDb-MVZ0ds-5EKY_DFC-vc0GG05-5xFD2kiY_Vlhv-5DZkGkHec0tAsPTzwzOQQ_w3QKdF-5FERtXLAbKgb1KlucKGsjtB9C_TGH0cf7bUJ9gSmzHmSGjAt7FnmFaOKXp03tR3CgEN_uVhRh0PWIfdQ2lM7qYAzcHcShVE_YFhHFKZXR54SDz_ea2RGXXgtgIA6650sDWaTV11WbYAmJ-rCRYHVdt4etoaMJ20vsksepbElFbxOrFGkzMSPzRNkKo2W0QEKC-TrwEu6zVQkmmWMbAtC1WwnqkiVxz9HP5nnWlDUOp4OJCmiBwTcujHmTLS07YIcfvOx8K5XwIPxZwfQYN2fpPBLZJW_oWatU9Skm8tuFAgk0rItc-34ohR8eeNfQWR3gZn3HQFl3TYnaTCIq3vjwIuGnKLj0RTrZcREu0qOuusy0JQgDSJgdSwvUwkpcCfBt1Olm0XQ8SIuxw8TGKvi4jQ8sK2INos64GBHmwQ18ubMAGfmWFK_cPpVLiYG_czVFsQPPYnNqDYlWz5eKnLOLu6dbMoGG8J49BrZ3Nq2iaXz21vjpgp9nz1IW6u-AB7iy7S-c_9xtm4zZApsrEZcrLMn_eEuOsnojfhkKViPpOyU0Qk9mM95zPQT63xiiyNQl2Mo2Sn0ztiX4ClwhzEgw2sKzzgbEOHTkHBMNeLSfS36jh1WuBUKN_FrGWDbes6FX3CEXjbri_bJWdH4hZCX6kNWyIQBP5VywPARhmcTAfLO0PAnR2JfGlgZQwPykz5P7N5rLV4uxPKPKA_j0B0LDYY1B2xZR-AKQWMkGOoeionuUhSL5UnmbK_Wej_nycdVGFpbNYexvG0FdDJm0L-R0rfKf9zaE_RVUMLr263ETi_mXQTYLVJOuL_gaMGdrg-I.2Cb5fSEZe4FnwbKXvEqmEg");
        System.out.println(chatBot.getReply("你好",null));
    }

}
