package org.nekotori.utils;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GachaUtils {

    public static List<ImageUtil.AzureLaneCard> gachaAzureLane(int type){
        List<String> gacha = gacha(10, 0, 7, 12, 51, 30);
        List<ImageUtil.AzureLaneCard> resAzureLaneCards = new ArrayList<>();
        File file = new File("blhx/info.json");
        JSON json = JSONUtil.readJSON(file, StandardCharsets.UTF_8);
        List<ImageUtil.AzureLaneCard> azureLaneCards = json.toBean(new TypeReference<List<ImageUtil.AzureLaneCard>>() {
        });
        List<String> types;
        switch (type){
            case 0:types = Arrays.asList("驱逐","轻巡","维修");break;
            case 1:types = Arrays.asList("重炮","重巡","战巡","战列");break;
            default:types = Arrays.asList("航母","轻母","重巡","维修","潜艇") ;break;
        }

        azureLaneCards = azureLaneCards.stream().filter(card -> types.contains(card.getType())).collect(Collectors.toList());
        List<ImageUtil.AzureLaneCard> Ns = azureLaneCards.stream().filter((card) -> card.getLevel() == 1 && card.getBuildType().contains(0)).collect(Collectors.toList());
        List<ImageUtil.AzureLaneCard> Rs = azureLaneCards.stream().filter((card) -> card.getLevel() == 2 && card.getBuildType().contains(0)).collect(Collectors.toList());
        List<ImageUtil.AzureLaneCard> sRs = azureLaneCards.stream().filter((card) -> card.getLevel() == 3 && card.getBuildType().contains(0)).collect(Collectors.toList());
        List<ImageUtil.AzureLaneCard> ssRs = azureLaneCards.stream().filter((card) -> card.getLevel() == 4 && card.getBuildType().contains(0)).collect(Collectors.toList());
        Random random = new Random();
        for (String s : gacha) {
            switch (s) {
                case "SSR":
                    resAzureLaneCards.add(ssRs.get(random.nextInt(ssRs.size())));
                    break;
                case "SR":
                    resAzureLaneCards.add(sRs.get(random.nextInt(sRs.size())));
                    break;
                case "R":
                    resAzureLaneCards.add(Rs.get(random.nextInt(Rs.size())));
                    break;
                case "N":
                    resAzureLaneCards.add(Ns.get(random.nextInt(Ns.size())));
                    break;
            }
        }
        return resAzureLaneCards;
    }

    public static List<ImageUtil.AzureLaneCard> gachaAzureLaneAll(int type){
        List<String> gacha = gacha(10, 12, 70, 120, 260, 550);
        List<ImageUtil.AzureLaneCard> resAzureLaneCards = new ArrayList<>();
        File file = new File("blhx/info.json");
        JSON json = JSONUtil.readJSON(file, StandardCharsets.UTF_8);
        List<ImageUtil.AzureLaneCard> azureLaneCards = json.toBean(new TypeReference<List<ImageUtil.AzureLaneCard>>() {
        });
        List<String> types;
        switch (type){
            case 0:types = Arrays.asList("驱逐","轻巡","维修");break;
            case 1:types = Arrays.asList("重炮","重巡","战巡","战列");break;
            default:types = Arrays.asList("航母","轻母","重巡","维修","潜艇") ;break;
        }

        azureLaneCards = azureLaneCards.stream().filter(card -> types.contains(card.getType())).collect(Collectors.toList());
        List<ImageUtil.AzureLaneCard> Ns = azureLaneCards.stream().filter((card) -> card.getLevel() == 1 ).collect(Collectors.toList());
        List<ImageUtil.AzureLaneCard> Rs = azureLaneCards.stream().filter((card) -> card.getLevel() == 2).collect(Collectors.toList());
        List<ImageUtil.AzureLaneCard> sRs = azureLaneCards.stream().filter((card) -> card.getLevel() == 3).collect(Collectors.toList());
        List<ImageUtil.AzureLaneCard> ssRs = azureLaneCards.stream().filter((card) -> card.getLevel() == 4).collect(Collectors.toList());
        List<ImageUtil.AzureLaneCard> uRs = azureLaneCards.stream().filter((card) -> card.getLevel() == 5).collect(Collectors.toList());
        Random random = new Random();
        for (String s : gacha) {
            if(s.equals("UR")){
                resAzureLaneCards.add(uRs.get(random.nextInt(uRs.size())));
            }else if(s.equals("SSR")){
                resAzureLaneCards.add(ssRs.get(random.nextInt(ssRs.size())));
            }else if(s.equals("SR")){
                resAzureLaneCards.add(sRs.get(random.nextInt(sRs.size())));
            }else if(s.equals("R")){
                resAzureLaneCards.add(Rs.get(random.nextInt(Rs.size())));
            }else if(s.equals("N")){
                resAzureLaneCards.add(Ns.get(random.nextInt(Ns.size())));
            }
        }
        return resAzureLaneCards;
    }

    public static List<ImageUtil.AzureLaneCard> gachaAzureLaneSp(){
        List<String> gacha = gacha(10, 12, 70, 120, 260, 550);
        List<ImageUtil.AzureLaneCard> resAzureLaneCards = new ArrayList<>();
        File file = new File("blhx/info.json");
        List<ImageUtil.AzureLaneCard> eventCards = ImageUtil.getEventCards();
        JSON json = JSONUtil.readJSON(file, StandardCharsets.UTF_8);
        List<ImageUtil.AzureLaneCard> azureLaneCards = json.toBean(new TypeReference<List<ImageUtil.AzureLaneCard>>() {
        });
        List<String> collect = eventCards.stream().map(ImageUtil.AzureLaneCard::getName).collect(Collectors.toList());
        azureLaneCards = azureLaneCards.stream().filter(card -> !collect.contains(card.getName())).collect(Collectors.toList());
        List<ImageUtil.AzureLaneCard> urs = azureLaneCards.stream().filter(card ->
                collect.contains(card.getName()) &&
                eventCards.stream().filter(c->c.getP()!=0).map(ImageUtil.AzureLaneCard::getName).collect(Collectors.toList()).contains(card.getName()))
                .collect(Collectors.toList());

        List<ImageUtil.AzureLaneCard> Ns = azureLaneCards.stream().filter((card) -> card.getLevel() == 1 && card.getBuildType().contains(0)).collect(Collectors.toList());
        List<ImageUtil.AzureLaneCard> Rs = azureLaneCards.stream().filter((card) -> card.getLevel() == 2 && card.getBuildType().contains(0)).collect(Collectors.toList());
        List<ImageUtil.AzureLaneCard> sRs = azureLaneCards.stream().filter((card) -> card.getLevel() == 3 && card.getBuildType().contains(0)).collect(Collectors.toList());
        List<ImageUtil.AzureLaneCard> ssRs = azureLaneCards.stream().filter((card) -> card.getLevel() == 4 && card.getBuildType().contains(0)).collect(Collectors.toList());
        Random random = new Random();
        for (String s : gacha) {
            if(s.equals("SSR")){
                resAzureLaneCards.add(ssRs.get(random.nextInt(ssRs.size())));
            }else if(s.equals("SR")){
                resAzureLaneCards.add(sRs.get(random.nextInt(sRs.size())));
            }else if(s.equals("R")){
                resAzureLaneCards.add(Rs.get(random.nextInt(Rs.size())));
            }else if(s.equals("N")){
                resAzureLaneCards.add(Ns.get(random.nextInt(Ns.size())));
            }else {
                resAzureLaneCards.add(urs.get(random.nextInt(urs.size())));
            }
        }
        return resAzureLaneCards;
    }

  public static void main(String[] args) {
      List<ImageUtil.AzureLaneCard> azureLaneCards = gachaAzureLaneSp();
    System.out.println(azureLaneCards);
  }

    public static List<String> gacha(Integer num, Integer urP,Integer ssrP,Integer srP,Integer rP,Integer nP){
        int sum = nP+ssrP+srP+rP+urP;
        nP = (int) (nP.doubleValue()/ (double) sum * 100000);
        ssrP = (int) (ssrP.doubleValue()/ (double) sum * 100000)+nP;
        srP = (int) (srP.doubleValue()/ (double) sum * 100000)+ssrP;
        rP = (int) (rP.doubleValue()/ (double) sum * 100000)+srP;
        List<String> gachas = new ArrayList<>();
        Random random = new Random();
        for(int j = 0;j<num;j++){
            int i = random.nextInt(100000);
            if(i<nP){
                gachas.add("N");
            }
            else if(i<ssrP){
                gachas.add("SSR");
            }
            else if(i<srP){
                gachas.add("SR");
            }
            else if(i<rP){
                gachas.add("R");
            }
            else {
                gachas.add("UR");
            }
        }

        return gachas;
    }
}
