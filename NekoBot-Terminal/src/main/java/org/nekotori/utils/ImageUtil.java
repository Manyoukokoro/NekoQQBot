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

        private String id;

        private Integer level;

        private String frameName;

        private String frameUrl;

        private String iconUrl;

        private Integer typeInt;

        private String type;

        private List<Integer> buildType;
    }


    public static void main(String[] args) throws IOException {
//        downloadAzureLaneSrc();

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
        for(JXNode ship:ships){
            String s2 = ship.sel("./tbody/tr/td[2]/p/a/@title").get(0).toString();
            String s3 = ship.sel("./tbody/tr/td[2]/p/small/text()").get(0).toString();
            String s4 =CollectionUtils.isEmpty(ship.sel(".//sup/text()"))?"0":
                    ship.sel(".//sup/text()").get(0).toString();
            String s5 = ship.sel("./tbody/tr/td[1]/div/div/div/a/img/@alt").get(0).toString();
            System.out.println(s2);
            System.out.println(s3);
            System.out.println(s4);
            System.out.println(s5);
        }
    }
}



//
//    class AzurChar(BaseData):
//    type_: str  # 舰娘类型
//
//    @property
//    def star_str(self) -> str:
//            return ["白", "蓝", "紫", "金"][self.star - 1]
//
//
//    class UpChar(_UpChar):
//    type_: str  # 舰娘类型
//
//
//    class UpEvent(_UpEvent):
//    up_char: List[UpChar]  # up对象
//
//
//    class AzurHandle(BaseHandle[AzurChar]):
//    def __init__(self):
//            super().__init__("azur", "碧蓝航线")
//    self.max_star = 4
//    self.config = draw_config.azur
//    self.ALL_CHAR: List[AzurChar] = []
//    self.UP_EVENT: Optional[UpEvent] = None
//
//    def get_card(self, pool_name: str, **kwargs) -> AzurChar:
//            if pool_name == "轻型":
//    type_ = ["驱逐", "轻巡", "维修"]
//    elif pool_name == "重型":
//    type_ = ["重巡", "战列", "战巡", "重炮"]
//            else:
//    type_ = ["维修", "潜艇", "重巡", "轻航", "航母"]
//    up_pool_flag = pool_name == "活动"
//            # Up
//            up_ship = (
//            [x for x in self.UP_EVENT.up_char if x.zoom > 0] if self.UP_EVENT else []
//            )
//            # print(up_ship)
//    acquire_char = None
//        if up_ship and up_pool_flag:
//    up_zoom: List[Tuple[float, float]] = [(0, up_ship[0].zoom / 100)]
//            # 初始化概率
//            cur_ = up_ship[0].zoom / 100
//            for i in range(len(up_ship)):
//            try:
//            up_zoom.append((cur_, cur_ + up_ship[i + 1].zoom / 100))
//    cur_ += up_ship[i + 1].zoom / 100
//    except IndexError:
//    pass
//            rand = random.random()
//            # 抽取up
//            for i, zoom in enumerate(up_zoom):
//            if zoom[0] <= rand <= zoom[1]:
//            try:
//    acquire_char = [
//    x for x in self.ALL_CHAR if x.name == up_ship[i].name
//                        ][0]
//    except IndexError:
//    pass
//        # 没有up或者未抽取到up
//        if not acquire_char:
//    star = self.get_star(
//            [4, 3, 2, 1],
//            [
//    self.config.AZUR_FOUR_P,
//    self.config.AZUR_THREE_P,
//    self.config.AZUR_TWO_P,
//    self.config.AZUR_ONE_P,
//            ],
//            )
//    acquire_char = random.choice(
//            [
//    x
//                    for x in self.ALL_CHAR
//                    if x.star == star and x.type_ in type_ and not x.limited
//                ]
//                        )
//                        return acquire_char
//
//    def draw(self, count: int, **kwargs) -> Message:
//    index2card = self.get_cards(count, **kwargs)
//    cards = [card[0] for card in index2card]
//    up_list = [x.name for x in self.UP_EVENT.up_char] if self.UP_EVENT else []
//    result = self.format_result(index2card, **{**kwargs, "up_list": up_list})
//            return MessageSegment.image(self.generate_img(cards).pic2bs4()) + result
//
//    def generate_card_img(self, card: AzurChar) -> BuildImage:
//    sep_w = 5
//    sep_t = 5
//    sep_b = 20
//    w = 100
//    h = 100
//    bg = BuildImage(w + sep_w * 2, h + sep_t + sep_b)
//    frame_path = str(self.img_path / f"{card.star}_star.png")
//    frame = BuildImage(w, h, background=frame_path)
//    img_path = str(self.img_path / f"{cn2py(card.name)}.png")
//    img = BuildImage(w, h, background=img_path)
//        # 加圆角
//        frame.circle_corner(6)
//                img.circle_corner(6)
//                bg.paste(img, (sep_w, sep_t), alpha=True)
//            bg.paste(frame, (sep_w, sep_t), alpha=True)
//            # 加名字
//            text = card.name[:6] + "..." if len(card.name) > 7 else card.name
//            font = load_font(fontsize=14)
//    text_w, text_h = font.getsize(text)
//    draw = ImageDraw.Draw(bg.markImg)
//            draw.text(
//            (sep_w + (w - text_w) / 2, h + sep_t + (sep_b - text_h) / 2),
//    text,
//    font=font,
//    fill=["#808080", "#3b8bff", "#8000ff", "#c90", "#ee494c"][card.star - 1],
//            )
//            return bg



//    url = "https://wiki.biligame.com/blhx/游戏活动表"
//    result = await self.get_url(url)
//        if not result:
//            logger.warning(f"{self.game_name_cn}获取活动表出错")
//            return
//            try:
//    dom = etree.HTML(result, etree.HTMLParser())
//    dd = dom.xpath("//div[@class='timeline2']/dl/dd/a")[0]
//    except Exception as e:
//            logger.warning(f"{self.game_name_cn}UP更新出错 {type(e)}：{e}")
//
//    async def _reload_pool(self) -> Optional[Message]:
//    await self.update_up_char()
//        self.load_up_char()
//                if self.UP_EVENT:
//            return Message(f"重载成功！\n当前活动：{self.UP_EVENT.title}")
//}
