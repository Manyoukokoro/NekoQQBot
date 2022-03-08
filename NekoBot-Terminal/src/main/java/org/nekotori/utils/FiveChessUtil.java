package org.nekotori.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author: JayDeng
 * @date: 2022/3/8 下午3:44
 * @description: FiveChessUtil
 * @version: {@link }
 */
public class FiveChessUtil {



    public static int[][] generateField(int size){
        int[][] blocks = new int[size][size];
        for(int[] row:blocks){
            for (int column:row){
                column = 0;
            }
        }
        return blocks;
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

    public static BufferedImage draw(int[][] map) throws IOException {
        if(map.length<=0) return null;
        int raw = map.length;
        int column = map[0].length;
        int backWidth = 50*raw+60;
        int backHeight = 50*column+50;
        BufferedImage bufferedImage = new BufferedImage(backWidth+1, backHeight+1 , BufferedImage.TYPE_INT_BGR);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.white);
        graphics.fillRect(0,0,backWidth,backHeight);
        for(int i=0;i<raw;i++){
            for(int j=0;j<column;j++){
                graphics.setColor(Color.black);
                graphics.drawRect(50*i,50*j,50,50);
                if(map[i][j]==1) {
                    graphics.setColor(Color.black);
                    graphics.fillOval(50*i+1,50*j+1,48,48);
                }
                if(map[i][j]==-1){
                    graphics.setColor(Color.black);
                    graphics.drawOval(50*i+1,50*j+1,48,48);
                }
            }
        }
        graphics.setStroke(new BasicStroke(40));
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int fontSize = 50;
        Font font = new Font(Font.MONOSPACED,Font.BOLD,fontSize);
        graphics.setFont(font);
        for(int i=0;i<raw;i++){
            graphics.drawString(String.valueOf((char)('A'+i)),50*i+8,backHeight-10);
        }
        for(int j=0;j<column;j++){
            graphics.drawString(String.valueOf((1+j)),backWidth-60,50*j+40);
        }
        return bufferedImage;
    }

    public static void main(String[] args) throws IOException {
        draw(generateField(14));
    }

    public static int isWin(int x, int y, int[][] map) {


        // 对胜负判断
        // 4个方向 左右 上下 左斜 右斜
        // 对一个棋子的一个方向的10颗棋子进行判断 是否满足5子连续
        // 左右
        int num = 1;
    for (int i = 0; i < map.length-1; i++) {
            if (map[x][i] != 0) {
                if (map[x][i] == map[x][i + 1]) {
                    num++;
                    if (num >= 5) {
                        return map[x][y];
                    }
                } else {
                    num = 1;
                }
            }
        }

        // 上下
        num = 1;
    for (int i = 0; i < map.length; i++) {
            if (map[i][y] != 0) {
                if (map[i][y] == map[i + 1][y]) {
                    num++;
                    if (num >= 5) {
                        return map[x][y];
                    }
                } else {
                    num = 1;
                }
            }
        }

        num=1;
        // 右斜 x-1 j+1
        for (int i = 0; i < map.length-1; i++) {
            for (int j = 1; j < map.length ; j++) {
                if (((i - j) >= 0) && ((i - j) < map.length)) {
                    if(map[j][i-j]!=0){
                        if(map[j][i-j]==map[j-1][i-j+1]){
                            num++;
                            if (num >= 5) {
                                return map[x][y];
                            }
                        }else{
                            num=1;
                        }
                    }
                }
            }
        }

        num=1;
        //左斜 x+1 y+1
        for (int i = -map.length; i < map.length; i++) {
            for (int j = 1; j < map.length; j++) {
                if(((i+j)>=0)&&((i+j)<map.length)){
                    if(map[j][j+i]!=0){
                        if(map[j][i+j]==map[j+1][i+j+1]){
                            num++;
                            if (num >= 5) {
                                return map[x][y];
                            }
                        }else{
                            num=1;
                        }
                    }
                }
            }
        }

        return 0;
    }

    public static boolean isWin(int[][] map)
    {
        int temp = 0;   //记录当前位置
        int x,y;    //记录当前坐标
        for (int i = 0 ; i < map.length; i++)    //横向遍历棋盘
        {
            for ( int j = 0 ; j < map.length ; j++)//纵向遍历棋盘
            {
                temp = map[i][j];         //

                if (temp !=0)      //判断是否有棋子
                {
                    for (int k=0;k<4 ;k++ )        //按向右、向下、右下、左下四个方向判断
                    {
                        x = i;
                        y = j;
                        switch (k)
                        {
                            case 0:          //向右判断
                            {
                                for (int t=0;t<5 ;t++ )//判断另外四个棋子
                                {
                                    if (4 == t)//t值等于4说明前面四个子和
                                    {
                                        return true;
                                    }
                                    if (map.length == ++y||temp !=(map[x][y]))//y值等于BOARD_SIZE表明已超出棋盘边界
                                    {
                                        break;
                                    }
                                }
                                break;
                            }
                            case 1:         //向下判断
                            {
                                for (int t=0;t<5 ;t++ )//判断另外四个棋子
                                {
                                    if (4 == t)//t值等于4说明前面四个子
                                    {
                                        return true;
                                    }
                                    if (map.length == ++x||temp != map[x][y])//y值等于BOARD_SIZE表明已超出棋盘边界
                                    {
                                        break;
                                    }
                                }
                                break;
                            }
                            case 2:        //向右下方向判断
                            {
                                for (int t=0;t<5 ;t++ )//判断另外四个棋子
                                {
                                    if (4 == t)//t值等于4说明前面四个子
                                    {
                                        return true;
                                    }
                                    if (map.length == ++x||map.length == ++y||temp != map[x][y])//x、y值等于BOARD_SIZE表明已超出棋盘边界
                                    {
                                        break;
                                    }
                                }
                                break;
                            }
                            case 3:      //向左下放下判断
                            {
                                for (int t=0;t<5 ;t++ )//判断另外四个棋子
                                {
                                    if (4 == t)//t值等于4说明前面四个子
                                    {
                                        return true;
                                    }
                                    if (map.length == ++x||-1 == --y||temp !=map[x][y])//x等于BOARD_SIZE或者y值等于-1表明已超出棋盘边界
                                    {
                                        break;
                                    }
                                }
                                break;
                            }
                            default:
                            {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}

