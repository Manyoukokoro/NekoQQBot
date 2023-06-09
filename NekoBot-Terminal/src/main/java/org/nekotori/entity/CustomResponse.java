package org.nekotori.entity;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: JayDeng
 * @date: 2022/3/3 上午11:37
 * @description: CustomRespose
 * @version: {@link }
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomResponse {

    public static void main(String[] args) {
        System.out.println(genResponse("{0}{1}12{3}56{2}", "dadsa12345678","{0}{3}1is{1},2is{2}"));
        CustomResponse dasasds = CustomResponse.builder().keyWord("1111").way(WAY.FORMAT).response("dasasds").build();
        String s = JSONUtil.toJsonStr(dasasds);
        System.out.println(s);
        CustomResponse customResponse = JSONUtil.toBean(s, CustomResponse.class);
        System.out.println(customResponse);
    }

    public enum WAY {
        FULL_CONTEXT("全文"),
        FORMAT("格式化"),
        CONTAINS("包含"),
        REGEX("正则"),
        BEGIN("开头"),
        END("结尾"),
        ALIAS("别名");

        private final String description;

        WAY(String description) {
            this.description = description;
        }

        public static WAY of(String description) {
            for (WAY way : WAY.values()) {
                if (way.description.equals(description)) {
                    return way;
                }
            }
            return null;
        }


        @Override
        public String toString() {
            return this.description;
        }
    }

    private String keyWord;

    private WAY way;

    private String response;

    public static boolean canFormat(String s,String format){
        String regex = format.replaceAll("\\{[^{^}]*}", ".*");
        return Pattern.compile(regex).matcher(s).matches();
    }

    public static String genResponse(String format,String origin,String formatResp){
        List<FormatPattern> formatPatterns = resolveFormat(format, origin);
        for (FormatPattern formatPattern : formatPatterns) {
            formatResp = formatResp.replace(formatPattern.getPattern(),formatPattern.getText());
        }
        return formatResp;
    }

    @Data
    public static class FormatPattern{
        String pattern;

        String text;
    }

    public static List<FormatPattern> resolveFormat(String format,String origin){
        if (!canFormat(origin,format)) {
            return ListUtil.empty();
        }
        Matcher matcher = Pattern.compile("\\{[^{^}]*}").matcher(format);
        List<String> matcherList = matcher.results().map(MatchResult::group).collect(Collectors.toList());
        String[] split = format.split("\\{[^{^}]*}");
        for (String s : split) {
            origin = origin.replaceFirst(s,"#rep#this-is-salt#");
        }
        String[] split1 = origin.split("#rep#this-is-salt#");
        List<String> orginCollect = Stream.of(split1).collect(Collectors.toList());
        orginCollect.remove(0);
        if (matcherList.size()!=orginCollect.size()){
            return ListUtil.empty();
        }
        List<FormatPattern> formatPatterns = new ArrayList<>();
        for (int i = 0; i < orginCollect.size(); i++) {
            FormatPattern formatPattern = new FormatPattern();
            formatPattern.pattern = matcherList.get(i);
            formatPattern.text = orginCollect.get(i);
            formatPatterns.add(formatPattern);
        }
        return formatPatterns;
    }
}
