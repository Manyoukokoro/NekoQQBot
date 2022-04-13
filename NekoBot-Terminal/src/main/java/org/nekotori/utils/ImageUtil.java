package org.nekotori.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.util.CollectionUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author: JayDeng
 * @date: 2022/3/11 上午11:08
 * @description: ImageUtil
 * @version: {@link }
 */
public class ImageUtil {

    private static List<AzureLaneCard> cache;

    public static InputStream pcrGachaImage(List<String> ids) throws IOException {
        if (ids == null || ids.size() != 10) {
            return InputStream.nullInputStream();
        }
        BufferedImage bufferedImage = new BufferedImage(500, 200, BufferedImage.TYPE_INT_BGR);
        Graphics2D graphics = bufferedImage.createGraphics();
        for (int k = 0; k < 5; k++) {
            for (int j = 0; j < 2; j++) {
                File file = new File("jpg/" + ids.get(0) + ".jpg");
                BufferedImage read = ImageIO.read(file);
                graphics.drawImage(read, 100 * k, 100 * j, 100, 100, (image, i, i1, i2, i3, i4) -> false);
                ImageIO.write(bufferedImage, "png", new File("test.png"));
            }

        }
        return null;
    }



    public static String azureLaneTypes(int index) {
        List<String> types = Arrays.asList("驱逐", "轻巡", "重巡", "超巡", "战巡", "战列", "航母", "航站", "轻航", "重炮", "维修", "潜艇", "运输");
        return types.get(index);
    }

    public static int azureLaneLevels(String frame) {
        frame =frame.trim();
        if (Arrays.asList("舰娘头像外框普通.png", "舰娘头像外框白色.png").contains(frame)) {
            return 1;
        }
        if (Arrays.asList("舰娘头像外框稀有.png", "舰娘头像外框蓝色.png").contains(frame)) {
            return 2;
        }
        if (Arrays.asList("舰娘头像外框精锐.png", "舰娘头像外框紫色.png").contains(frame)) {
            return 3;
        }
        if (Arrays.asList("舰娘头像外框超稀有.png", "舰娘头像外框金色.png").contains(frame)) {
            return 4;
        }
        if (Arrays.asList("舰娘头像外框海上传奇.png", "舰娘头像外框彩色.png").contains(frame)) {
            return 5;
        }
        if (Arrays.asList("舰娘头像外框最高方案.png",
                "舰娘头像外框决战方案.png",
                "舰娘头像外框超稀有META.png",
                "舰娘头像外框精锐META.png").contains(frame)) {
            return 6;
        }
        return 6;
    }

    public static void downloadFrame() {
        String BLHX_URL = "https://patchwiki.biligame.com/images/blhx";
        List<String> uris = Arrays.asList("/1/15/pxho13xsnkyb546tftvh49etzdh74cf.png",
                "/a/a9/k8t7nx6c8pan5vyr8z21txp45jxeo66.png",
                "/a/a5/5whkzvt200zwhhx0h0iz9qo1kldnidj.png",
                "/a/a2/ptog1j220x5q02hytpwc8al7f229qk9.png",
                "/6/6d/qqv5oy3xs40d3055cco6bsm0j4k4gzk.png");
        for(int i=1;i<=uris.size();i++) {
            File file = new File("blhx/frames/" + i + "_star.png");
            HttpUtil.downloadFile(BLHX_URL+uris.get(i-1),file);
        }
    }

    public static List<AzureLaneCard> getEventCards(){
        if(cache!=null){
            return cache;
        }
        String url = "https://wiki.biligame.com/blhx/游戏活动表";
        String s = HttpUtil.get(url);
        Document parse = Jsoup.parse(s);
        JXDocument document = new JXDocument(parse.getAllElements());
        List<JXNode> jxNodes = document.selN("./div[@class='timeline2']/dl/dd/a");
        System.out.println(jxNodes.get(0).sel("./@href").get(0));
        String s1 = jxNodes.get(0).sel("./@href").get(0).toString();
        String eventUrl ="https://wiki.biligame.com" + s1;
        Elements allElements = Jsoup.parse(HttpUtil.get(eventUrl)).getAllElements();
        JXDocument eventDocument = new JXDocument(allElements);
        JXNode timer = eventDocument.selN("./span[@class='eventTimer']").get(0);
        JXNode start = timer.sel("./@data-start").get(0);
        JXNode end = timer.sel("./@data-end").get(0);
        List<JXNode> ships = eventDocument.selN("./table[@class='shipinfo']");
        List<AzureLaneCard> azureLaneCards = new ArrayList<>();
        for(JXNode ship:ships){
            String s2 = ship.sel("./tbody/tr/td[2]/p/a/@title").get(0).toString();
            String s3 = ship.sel("./tbody/tr/td[2]/p/small/text()").get(0).toString();
            String s4 =CollectionUtils.isEmpty(ship.sel(".//sup/text()"))?"0%":
                    ship.sel(".//sup/text()").get(0).toString();
            String s5 = ship.sel("./tbody/tr/td[1]/div/div/div/a/img/@alt").get(0).toString();
            AzureLaneCard azureLaneCard = new AzureLaneCard();
            azureLaneCard.setName(s2);
            azureLaneCard.setType(s3);
            azureLaneCard.setP(Float.parseFloat(s4.substring(0,s4.length()-1)));
            azureLaneCard.setFrameName(s5);
        }
        cache = azureLaneCards;
        return azureLaneCards;
    }

    public static void downloadAzureLaneSrc() {
        String s = HttpUtil.get("https://wiki.biligame.com/blhx/舰娘图鉴");
        Document parse = Jsoup.parse(s);
        JXDocument jxDocument = new JXDocument(parse.getAllElements());
        List<JXNode> jxNodes = jxDocument.selN("*div[@class='resp-tabs-container']/div[@class='resp-tab-content'");
        List<AzureLaneCard> azurLaneCards = new ArrayList<>();
        for (int i = 0; i < jxNodes.size(); i++) {
            JXNode jxNode = jxNodes.get(i);
            List<JXNode> sel = jxNode.sel("./table/tbody/tr[2]/td/div/div/div/div");
            for (JXNode jx : sel) {
                AzureLaneCard azurLaneCard = new AzureLaneCard();
                azurLaneCard.setTypeInt(i);
                azurLaneCard.setIconUrl(jx.sel("./a/img/@srcset").get(0).toString().split(" ")[0]);
                azurLaneCard.setFrameName(jx.sel("./div/a/img/@alt").get(0).toString());
                azurLaneCard.setFrameUrl(jx.sel("./div/a/img/@src").get(0).toString());
                azurLaneCard.setName(jx.sel("./a/@title").get(0).toString());
                azurLaneCards.add(azurLaneCard);
            }
        }

        downloadFrame();

        Integer minType =
                azurLaneCards.stream().min(Comparator.comparing(AzureLaneCard::getTypeInt)).map(AzureLaneCard::getTypeInt).orElse(0);

        azurLaneCards.forEach(a-> {
            a.setType(azureLaneTypes(a.getTypeInt() - minType));
            a.setLevel(azureLaneLevels(a.getFrameName()));
            if(a.getLevel()<6) {
                a.setFrameUrl("blhx/frames/" + a.getLevel() + "_star.png");
            }else{
                String localUrl = "blhx/frames/" + a.getName() + ".png";
                File file = new File(localUrl);
                HttpUtil.downloadFile(a.getFrameUrl(),file);
                a.setFrameUrl(localUrl);
            }

            String shipInfo = HttpUtil.get("https://wiki.biligame.com/blhx/" + a.getName());
            Elements allElements = Jsoup.parse(shipInfo).getAllElements();
            JXDocument ship = new JXDocument(allElements);
            List<JXNode> nodes = ship.selN("*table[@class='wikitable sv-general']/tbody[1]/tr[4]/td[2]//text()");
            List<Integer> types = new ArrayList<>();
            if(nodes.toString().contains("无法建造")){
                types.add(-1);
            }else if(nodes.toString().contains("活动已关闭")){
                types.add(1);
            }else {
                types.add(0);
            }
            a.setBuildType(types);

            String localUrl = "blhx/icons/" + a.getName() + ".png";
            File file = new File(localUrl);
            HttpUtil.downloadFile(a.getIconUrl(),file);
            a.setIconUrl(localUrl);
        });
        String s1 = JSONUtil.toJsonStr(azurLaneCards);
        File file = new File("blhx/info.json");
        try(FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(s1.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Data
    public static class AzureLaneCard {

        private String name;

        private Float p;

        private Integer level;

        private String frameName;

        private String frameUrl;

        private String iconUrl;

        private Integer typeInt;

        private String type;

        private List<Integer> buildType;
    }

    public static InputStream generateAzureCardImage(List<AzureLaneCard> cards) throws IOException {
        if(cards.size()!=10){
            return InputStream.nullInputStream();
        }
        BufferedImage bufferedImage = new BufferedImage(1000, 400, BufferedImage.TYPE_INT_BGR);
        Graphics2D graphics = bufferedImage.createGraphics();
        for (int k = 0; k < 5; k++) {
            for (int j = 0; j < 2; j++) {
                int index = j*5 + k;
                File file1 = new File(cards.get(index).getIconUrl());
                BufferedImage read1 = ImageIO.read(file1);
                graphics.drawImage(read1, 200 * k+10, 200 * j+10, 190, 180, (image, i, i1, i2, i3, i4) -> false);
                File file2 = new File(cards.get(index).getFrameUrl());
                BufferedImage read2 = ImageIO.read(file2);
                graphics.drawImage(read2, 200 * k, 200 * j, 200, 200, (image, i, i1, i2, i3, i4) -> false);
            }

        }
        return bufferedImageToInputStream(bufferedImage);
    }

    public static InputStream bufferedImageToInputStream(BufferedImage image){
        File file = new File("temp.png");
        try {
            ImageIO.write(image, "png", file);
            return new FileInputStream(file);
        } catch (IOException ignore) {
        }
        return null;
    }


    public static void main(String[] args) throws IOException {
        List<AzureLaneCard> azureLaneCards = GachaUtils.gachaAzureLaneSp();
        InputStream inputStream = generateAzureCardImage(azureLaneCards);
    }
}
