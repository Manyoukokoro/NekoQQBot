package org.nekotori.atme.impl;

import com.github.plexpt.chatgpt.Chatbot;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.nekotori.annotations.AtMe;
import org.nekotori.atme.NoAuthAtMeResponse;
import org.nekotori.common.InnerConstants;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

@AtMe
public class ChatGPTResponse extends NoAuthAtMeResponse {

    private static Map<Member,Chatbot> targetMap = new HashMap<>();
    private static Map<Long, Long> lastQues = new HashMap<>();

    public static void deleteCache(){
        targetMap = new HashMap<>();
    }

    @Override
    public MessageChain response(GroupMessageEvent groupMessageEvent) {
        Member sender = groupMessageEvent.getSender();
        long nowTime = System.currentTimeMillis();
        if (sender.getId() != InnerConstants.admin && lastQues.get(sender.getId())!=null && (nowTime-lastQues.get(sender.getId()))<120*1000L){
            return new MessageChainBuilder().append(new At(sender.getId()))
                    .append("\n")
                    .append("我检测到您曾经在不到两分钟之前于本群或其他群向我提问，请减缓频率防止给服务器带来过大压力").build();
        }
        if(targetMap.get(sender)==null){
            Chatbot chatbot = new Chatbot("eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2R0NNIn0..Etkyqw-Pkfu76dfz.301dctJNGa0gxJSJVg94IeeQXckRO9Q4tp7JZ2ygJI_0HcgTjgObUWHL_S4thCCRupQkWWGDfWJh0gbP5pIVKPs0q3Ui8mUOcuMflqpSkGGIUCs-fL1dK2vb1O0u0ipNRUuJrrP8msIl-29rjqQCLo7YR3RxbnvhnvZsdGG0h-9MEKJ_QFnHMmwrvu_48tx3Cx7_bJWX-9-1TAFtXTUAmqe2PIY-5iX6iw7xSj1RJptK_M4IUXOCaYAvUv2Y9Ud9GsskKCCzF9O6_XORqkU7LwOyrIQ895taUlLRyWILjDNz5HM0hyl6OefuTyL_tfpOF8usV3rbY-dMkVPi8BNl2GVAgcDCYkUsvit0LgSlUKq0P4q391zNkx_40ln4yn-XQdESrPdq0eV2WgiLpGOjWp4CunSrbN70YZrJikVUmz2ZPm5FU8ycVH-5vS2VbrNKWRUknLrypjJH5RFkY5_KhaLVhoTDGL_tt7Os-pKkVuwuH9hmv5ZOqnAoFn1E9w4xpEQnBYaIHDRsDgtb-UGM9wW-XiWMVzn-Crh8XOkf8VHfUTPqPivfFpXo0PR17Ikq5J6RqNE4LyC2gglN6wEF_K1rV4-hD697_3sxVkz_tqY3bujS2IDQwg3GRKqVxV4x39xX76vCz2RNzdrXBhd8q7Vx4o9U_Ews8e27IhFf65SmPrTf4Wgv7sS-_nxrX0NeiqPrY7XWVFCTu1Quye2gH7YMnthQ2fUNwWgNsx3u3NFdRsNGjeqD5Ne5okXsHMYR-7Ah4l-sKkZQU5T_5rdTsaNdsVYMWrrOxwPECcPzAaxSdvgghwD-SeaI9Qhw6-PYa1aCkA_vBzBM3sno1hxIo0mRWOaJFgTrHD2S9sL4lpzc1yt00WxzBlYl_nbKm2rbDCQMne7vFutaqQeDFPRzji_71nTDOBuI0GyhD1oGO5W6g2jVyMBMz_asuq48-KiXPjAljELkPQT5uFRQWM_qiHQBu8jazamohLFsWfXemHpjU9jvFO4s4LJFePnfzLqLfoTqiKkS9WgSiACB-kI44dOeFI0X8ywhINdJ_uU9bxfpAoNw8QqTDYWiRNVpBkExpi8aFgw_KHCN00uT8MDtERraHhp6-AU81T6UDLse4hER45znlJiRNZsdTr9yki8buuSAIqoO2ZG2yXrjIA2rIfCVtYtsyMUlYqc77MXF-yDkFt-7mjDdk_6rq3_yBJPTPn2iBvLpgOOihSzIS-hMCPay1s-LtdP1NYiMqVHwAtBOAf93dEpzAQUHhqxTDR9hP55QwNDW3o30OoiAnRVop3pBUfNFWNlXGYQ4ZyVynibgYmAN2fxOkr0bCr0qAf4u8-hgt28IT73rOZFNHDZ0Qx945glYAT1WMKeWdKb6VagFTy6JLOnNmANaZmcujwqyEdR7d_SwiFlkL1zbhtSk0zi3LPfoWG2ww7n6vWcqA2ijq4n0B_VxrTi62RcFhOOWWMAijHd_Nea8iDpd4ItXnpdqJUf92FROemzZqzbaqH3Yvcq4wZCXjgrUOF5MyZ_0xVYkUKWGnHQ7qBm-NcCBzv57sRJodC5CmlXyIByj9KYqRx1sNhU-nFS-24YxILakPSse_gIooC-8DBUEQ1FQilB2EHh7nZQtek7NOVcpT-MwD47AIzNXhi-PL-lpgFVo8tJiaPMba1GQMMjCi8wy6x1j4Ze6Y-J4JxdOSSFVCHFQxYG5SxaGBBwlFSPTXbz4b9NXXdAZUEixKauhY-Eq22B_qkuPVB6ySfrKwK4TXb80cazuIS9nqBf1XuNKKHvf5RSKxSYoRCk9HjlVHGugBCOZumChz95qx2DRUKJxQqWJzekYvMg6tE001DXWkKkieBJ18oqc0aKbRde1SJRU4n0x6blXrXDxlOmerek7D3qrjBHAz20bh2R--ucJVZWXK-ihw-JQ832znMxWK9NBDEYHzOssRSwcpJhkeVHkmn5A8ifVj6MlyLROdn1xIBxnP1qDU4K1nRnhU6xjn45W_l_cJM937aBsz3-xAx6sxcdrpc92RNnLzBV5fHc9E1Y4KT-k4llynbdKc0puzJLEGFEMtZisig0Z10JHCOTu54eiPYtXxYkcDFI0HLQQx1mj5Iknz-qPmX3YKhlzFZpQ2yoK_J1Vdp62E1u_XxQ2oAdlvAyRDSMHMm5etVZb5MyVUeuMRS7cURf9awzdGXatkU3fRQMX7h5SRwfT3vsz_RXUhaAuoId81xfvJzceYtDfu1whaZrqqC9FOqg8czXdn8KTkdRvZPnsy9n10oQBQbs.28oJf90AdjQZaI-WYW0QAQ");
            targetMap.put(sender,chatbot);
        }
        if(lastQues.get(sender.getId())!=null){
            lastQues.replace(sender.getId(),System.currentTimeMillis());
        }else {
            lastQues.put(sender.getId(),System.currentTimeMillis());
        }
        Chatbot chatbot = targetMap.get(sender);
        MessageChain messageChain = groupMessageEvent.getMessage();
        String s1 = messageChain.serializeToMiraiCode();
        String trim = s1.replaceAll("\\[.*]", "").trim();
        Map<String, Object> chatResponse = chatbot.getChatResponse(trim);
        Object message = chatResponse.get("message");
        if(ObjectUtils.isEmpty(message)){
            return new MessageChainBuilder().append(new At(sender.getId())).append("\n").append("ChatGPT服务已离线").build();
        }
        return new MessageChainBuilder().append(new At(sender.getId())).append("\n").append(String.valueOf(message)).build();
    }
}
