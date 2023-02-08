package org.nekotori.utils;

import cn.hutool.core.img.FontUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.lang.func.VoidFunc0;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.freewayso.image.combiner.ImageCombiner;
import com.freewayso.image.combiner.enums.OutputFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.dreamlu.mica.core.utils.BeanUtil;
import net.mamoe.mirai.contact.Member;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.nekotori.entity.ChatMemberDo;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: JayDeng
 * @date: 2022/3/11 上午11:08
 * @description: ImageUtil
 * @version: {@link }
 */
public class ImageUtil {

    private static final Font fs = FontUtil.createFont(new File("font/fangsong.ttf"));
    private static final Font pfmb = FontUtil.createFont(new File("font/pingfangsimplemidiumblack.ttf"));
    private static final Font pf = FontUtil.createFont(new File("font/pingfangsimple.ttf"));
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
        frame = frame.trim();
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
        if (Arrays.asList("舰娘头像外框最高方案.png", "舰娘头像外框决战方案.png", "舰娘头像外框超稀有META.png", "舰娘头像外框精锐META.png").contains(frame)) {
            return 6;
        }
        return 6;
    }

    public static void downloadFrame() {
        String BLHX_URL = "https://patchwiki.biligame.com/images/blhx";
        List<String> uris = Arrays.asList("/1/15/pxho13xsnkyb546tftvh49etzdh74cf.png", "/a/a9/k8t7nx6c8pan5vyr8z21txp45jxeo66.png", "/a/a5/5whkzvt200zwhhx0h0iz9qo1kldnidj.png", "/a/a2/ptog1j220x5q02hytpwc8al7f229qk9.png", "/6/6d/qqv5oy3xs40d3055cco6bsm0j4k4gzk.png");
        for (int i = 1; i <= uris.size(); i++) {
            File file = new File("blhx/frames/" + i + "_star.png");
            HttpUtil.downloadFile(BLHX_URL + uris.get(i - 1), file);
        }
    }

    public static List<AzureLaneCard> getEventCards() {
        if (cache != null) {
            return cache;
        }
        String url = "https://wiki.biligame.com/blhx/游戏活动表";
        String s = HttpUtil.get(url);
        Document parse = Jsoup.parse(s);
        JXDocument document = new JXDocument(parse.getAllElements());
        List<JXNode> jxNodes = document.selN("./div[@class='timeline2']/dl/dd/a");
        System.out.println(jxNodes.get(0).sel("./@href").get(0));
        String s1 = jxNodes.get(0).sel("./@href").get(0).toString();
        String eventUrl = "https://wiki.biligame.com" + s1;
        Elements allElements = Jsoup.parse(HttpUtil.get(eventUrl)).getAllElements();
        JXDocument eventDocument = new JXDocument(allElements);
        JXNode timer = eventDocument.selN("./span[@class='eventTimer']").get(0);
        JXNode start = timer.sel("./@data-start").get(0);
        JXNode end = timer.sel("./@data-end").get(0);
        List<JXNode> ships = eventDocument.selN("./table[@class='shipinfo']");
        List<AzureLaneCard> azureLaneCards = new ArrayList<>();
        for (JXNode ship : ships) {
            String s2 = ship.sel("./tbody/tr/td[2]/p/a/@title").get(0).toString();
            String s3 = ship.sel("./tbody/tr/td[2]/p/small/text()").get(0).toString();
            String s4 = CollectionUtils.isEmpty(ship.sel(".//sup/text()")) ? "0%" : ship.sel(".//sup/text()").get(0).toString();
            String s5 = ship.sel("./tbody/tr/td[1]/div/div/div/a/img/@alt").get(0).toString();
            AzureLaneCard azureLaneCard = new AzureLaneCard();
            azureLaneCard.setName(s2);
            azureLaneCard.setType(s3);
            azureLaneCard.setP(Float.parseFloat(s4.substring(0, s4.length() - 1)));
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

        Integer minType = azurLaneCards.stream().min(Comparator.comparing(AzureLaneCard::getTypeInt)).map(AzureLaneCard::getTypeInt).orElse(0);

        azurLaneCards.forEach(a -> {
            a.setType(azureLaneTypes(a.getTypeInt() - minType));
            a.setLevel(azureLaneLevels(a.getFrameName()));
            if (a.getLevel() < 6) {
                a.setFrameUrl("blhx/frames/" + a.getLevel() + "_star.png");
            } else {
                String localUrl = "blhx/frames/" + a.getName() + ".png";
                File file = new File(localUrl);
                HttpUtil.downloadFile(a.getFrameUrl(), file);
                a.setFrameUrl(localUrl);
            }

            String shipInfo = HttpUtil.get("https://wiki.biligame.com/blhx/" + a.getName());
            Elements allElements = Jsoup.parse(shipInfo).getAllElements();
            JXDocument ship = new JXDocument(allElements);
            List<JXNode> nodes = ship.selN("*table[@class='wikitable sv-general']/tbody[1]/tr[4]/td[2]//text()");
            List<Integer> types = new ArrayList<>();
            if (nodes.toString().contains("无法建造")) {
                types.add(-1);
            } else if (nodes.toString().contains("活动已关闭")) {
                types.add(1);
            } else {
                types.add(0);
            }
            a.setBuildType(types);

            String localUrl = "blhx/icons/" + a.getName() + ".png";
            File file = new File(localUrl);
            HttpUtil.downloadFile(a.getIconUrl(), file);
            a.setIconUrl(localUrl);
        });
        String s1 = JSONUtil.toJsonStr(azurLaneCards);
        File file = new File("blhx/info.json");
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
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
        if (cards.size() != 10) {
            return InputStream.nullInputStream();
        }
        BufferedImage bufferedImage = new BufferedImage(1000, 400, BufferedImage.TYPE_INT_BGR);
        Graphics2D graphics = bufferedImage.createGraphics();
        for (int k = 0; k < 5; k++) {
            for (int j = 0; j < 2; j++) {
                int index = j * 5 + k;
                File file1 = new File(cards.get(index).getIconUrl());
                BufferedImage read1 = ImageIO.read(file1);
                graphics.drawImage(read1, 200 * k + 10, 200 * j + 10, 190, 180, (image, i, i1, i2, i3, i4) -> false);
                File file2 = new File(cards.get(index).getFrameUrl());
                BufferedImage read2 = ImageIO.read(file2);
                graphics.drawImage(read2, 200 * k, 200 * j, 200, 200, (image, i, i1, i2, i3, i4) -> false);
            }

        }
        return bufferedImageToInputStream(bufferedImage);
    }

    public static InputStream bufferedImageToInputStream(BufferedImage image) {
        File file = new File("temp.png");
        try {
            ImageIO.write(image, "png", file);
            return new FileInputStream(file);
        } catch (IOException ignore) {
        }
        return null;
    }


    public static void main(String[] args) throws IOException {
        ArrayList<CardAttr> cardAttrs = new ArrayList<>();
        cardAttrs.add(new CardAttr("Soldier FA",0));
        cardAttrs.add(new CardAttr("Soldier FA",0));
        cardAttrs.add(new CardAttr("Soldier FA",0));
        cardAttrs.add(new CardAttr("Soldier FA",1));
        cardAttrs.add(new CardAttr("Soldier FA",1));
        cardAttrs.add(new CardAttr("Soldier FA",2));
        cardAttrs.add(new CardAttr("Soldier FA",2));
        cardAttrs.add(new CardAttr("Soldier FA",1));
        cardAttrs.add(new CardAttr("Soldier FA",2));
        cardAttrs.add(new CardAttr("Soldier FA",2));
        InputStream dasdsadas = getNikkeGachaImageStream(cardAttrs);
        FileUtil.writeFromStream(dasdsadas,new File("jpg/test.png"));
    }

    private static long nextLevelExp(int level, long exp) {
        if (level < 100) {
            return (long) (Math.pow(2d, (int) ((double) level / 10)) * 10) - exp;
        }
        return 10240L - exp;
    }

    public static BufferedImage drawFiveChess(int[][] map, int x, int y) throws IOException {
        if (map.length <= 0) return null;
        int raw = map.length;
        int column = map[0].length;
        int backWidth = 50 * raw + 110;
        int backHeight = 50 * column + 150;
        BufferedImage bufferedImage = new BufferedImage(backWidth + 1, backHeight + 1, BufferedImage.TYPE_INT_BGR);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.lightGray);
        graphics.fillRect(0, 0, backWidth, backHeight);
        graphics.setColor(Color.black);
        graphics.fillOval(50 * (raw / 2) + 17, 50 * (column / 2) + 17, 16, 16);
        for (int i = 0; i < raw; i++) {
            for (int j = 0; j < column; j++) {
                graphics.setColor(Color.black);
                if (i < raw - 1 && j < column - 1) {
                    graphics.drawRect(50 * i + 25, 50 * j + 25, 50, 50);
                }
                if (map[i][j] == 1) {
                    graphics.setColor(Color.black);
                    graphics.fillOval(50 * i + 1, 50 * j + 1, 48, 48);
                }
                if (map[i][j] == -1) {
                    graphics.setColor(Color.white);
                    graphics.fillOval(50 * i + 1, 50 * j + 1, 48, 48);
                    graphics.setColor(Color.black);
                    graphics.drawOval(50 * i + 1, 50 * j + 1, 48, 48);
                }
            }
        }
        if (map[x][y] == -1) {
            graphics.setColor(Color.yellow);
        }
        if (map[x][y] == 1) {
            graphics.setColor(Color.blue);
        }
        if (map[x][y] != 0) {
            graphics.fillOval(50 * x + 1, 50 * y + 1, 48, 48);
            graphics.setColor(Color.black);
            graphics.drawOval(50 * x + 1, 50 * y + 1, 48, 48);
        }
        graphics.setStroke(new BasicStroke(40));
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int fontSize = 50;
        MyFont font = new MyFont(fs, fontSize);
        graphics.setFont(font);
        graphics.setColor(Color.black);
        for (int i = 0; i < raw; i++) {
            graphics.drawString(String.valueOf((char) ('A' + i)), 50 * i + 8, backHeight - 110);
        }
        for (int j = 0; j < column; j++) {
            graphics.drawString(String.valueOf((1 + j)), backWidth - 110, 50 * j + 40);
        }
        graphics.drawString("@NekoBot", 8, backHeight - 10);
        graphics.dispose();
        return bufferedImage;
    }

    public static Pair<InputStream,String> drawSignPic(Member sender, ChatMemberDo chatMemberDo, int incomeExp,int talkingIndex,int eroIndex) {
        int height = 3192;
        int width = 4096;
        int impFontSize = 200;
        int normalFontSize = 125;
        MyFont nfs = new MyFont(fs, normalFontSize);
        MyFont npf = new MyFont(pfmb, normalFontSize);
        MyFont ipf = new MyFont(pf, impFontSize);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.WHITE);
        BufferedImage back;
        if(StringUtils.hasLength(chatMemberDo.getBackgroundUri())){
            try(InputStream inputStream = HttpUtil.createGet(chatMemberDo.getBackgroundUri()).execute().bodyStream()){
                back = ImageIO.read(inputStream);
                if (back == null){
                    throw new Exception();
                }
                graphics.drawImage(back, 0, 0, width, height, null);
            }catch (Exception e){
                graphics.fillRect(0, 0, width, height);
            }
        }else {
            try {
                back = ImageIO.read(new File("jpg/background" + new Random().nextInt(5) + ".jpg"));
                graphics.drawImage(back, 0, 0, width, height, null);
            } catch (IOException e) {
                graphics.fillRect(0, 0, width, height);
            }
        }
        graphics.setColor(Color.GRAY);
        graphics.setStroke(new BasicStroke(6));
        //分割线（横）
        graphics.drawLine(50, height * 10 / 15, width - 50, height * 10 / 15);
        //分割线（竖）
        graphics.drawLine(width * 2 / 40 + 1000, 50, width * 2 / 40 + 1000, height - 50);

        graphics.setStroke(new BasicStroke(10));
        //边框线
        graphics.drawLine(200, 50, width - 200, 50);
        graphics.drawLine(200, 100, width - 200, 100);
        graphics.drawLine(200, 150, width - 200, 150);

        graphics.drawLine(200, height - 50, width - 200, height - 50);
        graphics.drawLine(200, height - 100, width - 200, height - 100);
        graphics.drawLine(200, height - 150, width - 200, height - 150);

        graphics.setColor(Color.BLACK);
        for (int i = 1; i < 14; i++) {
            graphics.fillOval(10, height / 15 * i + 10, 120, 120);
        }
        graphics.setFont(npf);
        String nick = sender == null ? "" : StringUtils.hasLength(sender.getNameCard())?sender.getNick():sender.getNameCard();
        if (nick.length() > 6) {
            nick = nick.substring(0, 6) + "...";
        }
        if(talkingIndex<10){
            graphics.setColor(Color.GRAY);
        }else if(talkingIndex<50){
            graphics.setColor(Color.CYAN);
        }else if(talkingIndex<100){
            graphics.setColor(Color.BLUE);
        }else if(talkingIndex<250){
            graphics.setColor(Color.YELLOW);
        }else if(talkingIndex<500){
            graphics.setColor(Color.ORANGE);
        }else if(talkingIndex<1000){
            graphics.setColor(Color.RED);
        }else {
            graphics.setColor(Color.BLACK);
        }
        graphics.drawString("话唠指数："+talkingIndex,200,height*10/15-(normalFontSize+50)*2);
        graphics.setColor(Color.BLACK);
        graphics.drawString("@: " + nick, 120, height * 10 / 15 + normalFontSize + 50);
        graphics.setFont(nfs);
        graphics.setColor(Color.BLACK);
        graphics.drawString("当前等级: " + chatMemberDo.getLevel(), 150, height * 11 / 15 + normalFontSize + 50);
        graphics.drawString("获得经验: " + incomeExp, 150, height * 11 / 15 + (normalFontSize + 50) * 2);
        graphics.drawString("距离下级: " + nextLevelExp(chatMemberDo.getLevel(), chatMemberDo.getExp()), 150, height * 11 / 15 + (normalFontSize + 50) * 3);

        String avatarUrl = sender == null ? "" : sender.getAvatarUrl();
        try (InputStream userAvatar = HttpUtil.createGet(avatarUrl).setConnectionTimeout(5000).execute().bodyStream()) {
            BufferedImage read = ImageIO.read(userAvatar);
            read = setRadius(read);
            graphics.drawImage(read, 150, height * 5 / 15 - 500, 1000, 1000, (image, i, i1, i2, i3, i4) -> false);

        } catch (Exception e) {
            graphics.drawOval(150, height * 5 / 15 - 500, 1000, 1000);
        } finally {
            graphics.setStroke(new BasicStroke(10));
            graphics.setColor(Color.gray);
            graphics.drawOval(150, height * 5 / 15 - 500, 1000, 1000);
        }
        String imgUrl = "";
        try {
            imgUrl = getAcgurl();
            InputStream inputStream = HttpUtil.createGet(imgUrl).setConnectionTimeout(2000).setReadTimeout(10000).execute().bodyStream();
            FileUtil.writeFromStream(inputStream, new File("jpg/temp.png"));
            inputStream.close();
            BufferedImage read = ImageIO.read(new File("jpg/temp.png"));
            FileUtil.writeFromStream(bufferedImageToInputStream(read),new File("pics/"+ UUID.randomUUID() +".png"));
            int height1 = read.getHeight() * 2400 / read.getWidth();
            graphics.drawImage(read, width * 21 / 40 + 350 - 1200, ((height * 10 / 15)+225)/2-(height1/2), 2400, height1, (image, i, i1, i2, i3, i4) -> false);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                File[] files = FileUtil.ls( "/home/nekotori/bot/pics");
                if (files != null && files.length > 0) {
                    int index = new Random().nextInt(files.length);
                    BufferedImage read = ImageIO.read(files[index]);
                    int height1 = read.getHeight() * 2400 / read.getWidth();
                    graphics.drawImage(read, width * 21 / 40 + 350 - 1200, 500, 2400, height1, (image, i, i1, i2, i3, i4) -> false);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        StringBuilder stringBuilder = new StringBuilder("今日运势:");
        incomeExp-=talkingIndex*100+eroIndex*500;
        if (incomeExp <= 100) {
            stringBuilder.append("★☆☆☆☆☆☆");
        } else if (incomeExp <= 500) {
            stringBuilder.append("★★☆☆☆☆☆");
        } else if (incomeExp <= 1000) {
            stringBuilder.append("★★★☆☆☆☆");
        } else if (incomeExp <= 5000) {
            stringBuilder.append("★★★★☆☆☆");
        } else if (incomeExp <= 10000) {
            stringBuilder.append("★★★★★☆☆");
        } else if (incomeExp <= 50000) {
            stringBuilder.append("★★★★★★☆");
        } else if (incomeExp >= 90000) {
            stringBuilder.append("★★★★★★★★");
        } else {
            stringBuilder.append("★★★★★★★");
        }
        graphics.setFont(ipf);
        graphics.setColor(Color.BLACK);
        graphics.drawString(stringBuilder.toString(), width * 2 / 40 + 1100, height * 10 / 15 + 50 + (normalFontSize + 50) * 2);

        stringBuilder = new StringBuilder("今日骚气:");
        int sq = new Random().nextInt(100);
        sq+=eroIndex;
        if (sq <= 10) {
            stringBuilder.append("★☆☆☆☆☆☆");
        } else if (sq <= 30) {
            stringBuilder.append("★★☆☆☆☆☆");
        } else if (sq <= 50) {
            stringBuilder.append("★★★☆☆☆☆");
        } else if (sq <= 70) {
            stringBuilder.append("★★★★☆☆☆");
        } else if (sq <= 90) {
            stringBuilder.append("★★★★★☆☆");
        } else if (sq <= 96) {
            stringBuilder.append("★★★★★★☆");
        } else {
            stringBuilder.append("★★★★★★★");
        }
        graphics.setFont(ipf);
        graphics.setColor(Color.getHSBColor(0.93f, 0.78f, 0.90f));
        graphics.drawString(stringBuilder.toString(), width * 2 / 40 + 1100, height * 12 / 15 - 50 + (normalFontSize + 50) * 2);

        MyFont myFont = new MyFont(fs, 75);
        graphics.setFont(myFont);
        graphics.setColor(Color.gray);
        graphics.drawString("NekoBot By Nekotori", 200, 300);
        graphics.dispose();
        try {
            //BufferedImage read = ImageIO.read(new File("jpg/stamp.png"));
            BufferedImage outImage = setRadius(bufferedImage, 600, 0, 0);
            InputStream inputStream = bufferedImageToInputStream(outImage);
            FileUtil.writeFromStream(inputStream, new File("jpg/temp_out.png"));
            ImgUtil.convert(new File("jpg/temp_out.png"), new File("jpg/temp_out.jpg"));
            return new Pair<>(FileUtil.getInputStream(new File("jpg/temp_out.jpg")),imgUrl);
        } catch (IOException e) {
            return new Pair<>(bufferedImageToInputStream(bufferedImage),imgUrl);
        }
    }

    private static String getAcgurl() {
        return HttpUtil.createGet("https://api.yimian.xyz/img/").form("type","moe").form("size","1920x1080").setConnectionTimeout(5000).setReadTimeout(10000).execute().header("Location");
    }

    /**
     * 图片设置圆角
     *
     * @param srcImage
     * @return
     * @throws IOException
     */

    public static BufferedImage setRadius(BufferedImage srcImage) throws IOException {
        int radius = (srcImage.getWidth() + srcImage.getHeight()) / 2;
        return setRadius(srcImage, radius, 0, 0);
    }

    /**
     * 图片设置圆角
     *
     * @param srcImage
     * @param radius
     * @param border
     * @param padding
     * @return
     * @throws IOException
     */

    public static BufferedImage setRadius(BufferedImage srcImage, int radius, int border, int padding) throws IOException {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        int canvasWidth = width + padding * 2;
        int canvasHeight = height + padding * 2;
        BufferedImage image = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gs = image.createGraphics();
        gs.setComposite(AlphaComposite.Src);
        gs.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gs.setColor(Color.white);
        gs.fill(new RoundRectangle2D.Float(0, 0, canvasWidth, canvasHeight, radius, radius));
        gs.setComposite(AlphaComposite.SrcAtop);
        gs.drawImage(setClip(srcImage, radius), padding, padding, null);
        if (border != 0) {
            gs.setColor(Color.GRAY);
            gs.setStroke(new BasicStroke(border));
            gs.drawRoundRect(padding, padding, canvasWidth - 2 * padding, canvasHeight - 2 * padding, radius, radius);
        }
        gs.dispose();
        return image;
    }

    /**
     * 图片切圆角
     *
     * @param srcImage
     * @param radius
     * @return
     */

    public static BufferedImage setClip(BufferedImage srcImage, int radius) {
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gs = image.createGraphics();
        gs.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gs.setClip(new RoundRectangle2D.Double(0, 0, width, height, radius, radius));
        gs.drawImage(srcImage, 0, 0, null);
        gs.dispose();
        return image;
    }


    public static class MyFont extends Font {
        private int size;

        public MyFont(String name, int style, int size) {
            super(name, style, size);
        }

        public MyFont(Font f, int size) {
            super(f);
            this.size = size;
        }

        @Override
        public int getSize() {
            return this.size;
        }

        @Override
        public float getSize2D() {
            return this.size;
        }
    }


    //nikke gacha
    public static InputStream getNikkeGachaImageStream(List<CardAttr> cardNamesAttrs){
        if(cardNamesAttrs.size()!=10){
            return null;
        }
        try {
            int cardWidth = 148;
            int cardHeight = 367;
            BufferedImage read = ImageIO.read(new File("nikke/images/bg.png"));
            int height = read.getHeight();
            int width = read.getWidth();
            int startWidth = width/2 - cardWidth/2 -20 -2*cardWidth;
            int startHeightUp = height/2 - cardHeight;
            int startHeightDown = height/2 +20;
            Graphics2D graphics = read.createGraphics();
            int w = startWidth;
            int h = startHeightUp;
            for (int i = 0; i < 5; i++) {
                CardAttr cardAttr = cardNamesAttrs.get(i);
                graphics.drawImage(drawSingleChara(cardAttr.name,cardAttr.level),w,h,(image, j, i1, i2, i3, i4) -> false);
                w+=cardWidth+10;
                h-=15;
            }
            w = startWidth;
            h = startHeightDown;
            for (int i = 0; i < 5; i++) {
                CardAttr cardAttr = cardNamesAttrs.get(i+5);
                graphics.drawImage(drawSingleChara(cardAttr.name,cardAttr.level),w,h,(image, j, i1, i2, i3, i4) -> false);
                w+=cardWidth+10;
                h-=15;
            }
            graphics.dispose();
            InputStream inputStream = bufferedImageToInputStream(read);
            return zipToJpg(height, width, inputStream);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @NotNull
    private static InputStream zipToJpg(int height, int width, InputStream inputStream) throws IOException {
        // 把图片读入到内存中
        BufferedImage bufImg = ImageIO.read(inputStream);
        // 压缩代码
        // 存储图片文件byte数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //防止图片变红
        BufferedImage newBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        newBufferedImage.createGraphics().drawImage(bufImg, 0, 0, width, height, Color.WHITE, null);
        //先转成jpg格式来压缩,然后在通过OSS来修改成源 文件本来的后缀格式
        ImageIO.write(newBufferedImage, "jpg", bos);
        //获取输出流
        inputStream = new ByteArrayInputStream(bos.toByteArray());
        return inputStream;
    }

    @Data
    @AllArgsConstructor
    public static class CardAttr{
        private String name;

        private int level;
    }


    private static BufferedImage drawSingleChara(String cardName,int level) throws IOException{
        int width = 148;
        int topHeight = 175;
        int topHeightSR = 171;
        int topHeightR = 136;
        int bgHeight = 236;
        int bottomHeight = 89;
        BufferedImage top,bottom;
        switch (level){
            case 0:{
                top = ImageIO.read(new File("nikke/images/R_top.png"));
                bottom = ImageIO.read(new File("nikke/images/R_bottom.png"));
                break;
            }
            case 1:{
                top = ImageIO.read(new File("nikke/images/SR_top.png"));
                bottom = ImageIO.read(new File("nikke/images/SR_bottom.png"));
                break;
            }
            case 2:{
                top = ImageIO.read(new File("nikke/images/SSR_top.png"));
                bottom = ImageIO.read(new File("nikke/images/SSR_bottom.png"));
                break;
            }
            default:{
                top = ImageIO.read(new File("nikke/images/R_top.png"));
                bottom = ImageIO.read(new File("nikke/images/R_bottom.png"));
                break;
            }
        }
        try {
            BufferedImage bg = ImageIO.read(new File("nikke/images/white.png"));
            BufferedImage card = ImageIO.read(new File("nikke/images/" + cardName + ".png"));
            //生成透明背景
            BufferedImage bufferedImage = new BufferedImage(width, topHeight+bgHeight+bottomHeight/2-50, BufferedImage.TYPE_INT_BGR);
            Graphics2D graphics0 = bufferedImage.createGraphics();
            bufferedImage = graphics0.getDeviceConfiguration().createCompatibleImage(width, topHeight+bgHeight+bottomHeight, Transparency.TRANSLUCENT);

            ImageCombiner imageCombiner = new ImageCombiner(bufferedImage, OutputFormat.PNG);
            imageCombiner.addImageElement(bg,0,topHeight-50);
            imageCombiner.addImageElement(top,0,level==0?topHeight-topHeightR:level==1?topHeight-topHeightSR:0 );
            imageCombiner.addImageElement(setBorderAlpha(card,0,0,30,0),0,topHeight-50);
            imageCombiner.addImageElement(bottom,0,topHeight+bgHeight-bottomHeight/2-50);
            return imageCombiner.combine();
//            Graphics2D graphics = bufferedImage.createGraphics();
//            if(level==0) {
//                graphics.drawImage(top, 0, 35, (image, i, i1, i2, i3, i4) -> false);
//
//            }else {
//                graphics.drawImage(top, 0, 0, (image, i, i1, i2, i3, i4) -> false);
//            }
//            graphics.drawImage(bg,0,topHeight,(image, i, i1, i2, i3, i4) -> false);
//            graphics.drawImage(card,0,topHeight,(image, i, i1, i2, i3, i4) -> false);
//            graphics.drawImage(bottom,0,topHeight+bgHeight-bottomHeight/2,(image, i, i1, i2, i3, i4) -> false);
//            graphics.dispose();
//            return bufferedImage;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static BufferedImage setBorderAlpha(BufferedImage image,int leftBorderSize,int rightBorderSize,int topBorderSize,int bottomBorderSize){
        BufferedImage back=new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int width = image.getWidth();
        int height = image.getHeight();
        if(topBorderSize>height/2){
            topBorderSize = height/2;
        }
        if(bottomBorderSize>height/2){
            bottomBorderSize = height/2;
        }
        if(leftBorderSize>width/2){
            leftBorderSize = width/2;
        }
        if(rightBorderSize>width/2){
            rightBorderSize = width/2;
        }
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                back.setRGB(j,i,image.getRGB(j,i));
            }
        }
        //top
        for(int i=0;i<topBorderSize;i++){
            for(int j=0;j<width;j++){
                if(getAlpha(image.getRGB(j, i))<i * (255 / topBorderSize)){
                    continue;
                }
                int rgb = image.getRGB(j, i);
                Color color = new Color(rgb);
                Color newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), i * (255 / topBorderSize));
                back.setRGB(j,i,newColor.getRGB());
            }
        }
        //bottom
        for(int i=0;i<bottomBorderSize;i++){
            for(int j=0;j<width;j++){
                if(getAlpha(image.getRGB(j,height-1-i))<i * (255 / bottomBorderSize)){
                    continue;
                }
                int rgb = image.getRGB(j,height-1-i);
                Color color = new Color(rgb);
                Color newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), i * (255 / bottomBorderSize));
                back.setRGB(j,height-1-i,newColor.getRGB());
            }
        }

        //left
        for(int i=0;i<height;i++){
            for(int j=0;j<rightBorderSize;j++){
                if(getAlpha(image.getRGB(j, i))<i * (255 / leftBorderSize)){
                    continue;
                }
                int rgb = image.getRGB(j,i);
                Color color = new Color(rgb);
                Color newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), i * (255 / leftBorderSize));
                back.setRGB(j,i,newColor.getRGB());
            }
        }

        //right
        for(int i=0;i<height;i++){
            for(int j=0;j<leftBorderSize;j++){
                if(getAlpha(image.getRGB(width-1-j,i))<i * (255 / rightBorderSize)){
                    continue;
                }
                int rgb = image.getRGB(width-1-j,i);
                Color color = new Color(rgb);
                Color newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), i * (255 / rightBorderSize));
                back.setRGB(width-1-j,i,newColor.getRGB());
            }
        }
        return back;
    }

    private static int getAlpha(int rgb){
        return (rgb >> 24) & 0xFF;
    }
}