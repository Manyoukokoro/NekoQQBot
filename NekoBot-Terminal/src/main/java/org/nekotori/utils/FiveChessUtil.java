package org.nekotori.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author: JayDeng
 * @date: 2022/3/8 下午3:44
 * @description: FiveChessUtil
 * @version: {@link }
 */
public class FiveChessUtil {


    public static int[][] generateField(int size) {
        int[][] blocks = new int[size][size];
        for (int[] row : blocks) {
            for (int column : row) {
                column = 0;
            }
        }
        return blocks;
    }

    public static void main(String[] args) throws IOException {
        int[][] ints = generateField(15);
        ints[0][0] = 1;
        ints[0][1] = 1;
        ints[0][2] = 1;
        File file = new File("test.png");
        ints[4][6] = 1;
        System.out.println(isOverThree(0, 2, ints));
        BufferedImage draw = ImageUtil.drawFiveChess(ints, 4, 5);
        boolean png = ImageIO.write(draw, "png", file);
    }

    public static boolean isWin(int[][] map) {
        int temp = 0;   //记录当前位置
        int x, y;    //记录当前坐标
        for (int i = 0; i < map.length; i++)    //横向遍历棋盘
        {
            for (int j = 0; j < map.length; j++)//纵向遍历棋盘
            {
                temp = map[i][j];         //

                if (temp != 0)      //判断是否有棋子
                {
                    for (int k = 0; k < 4; k++)        //按向右、向下、右下、左下四个方向判断
                    {
                        x = i;
                        y = j;
                        switch (k) {
                            case 0:          //向右判断
                            {
                                for (int t = 0; t < 5; t++)//判断另外四个棋子
                                {
                                    if (4 == t)//t值等于4说明前面四个子和
                                    {
                                        return true;
                                    }
                                    if (map.length == ++y || temp != (map[x][y]))//y值等于BOARD_SIZE表明已超出棋盘边界
                                    {
                                        break;
                                    }
                                }
                                break;
                            }
                            case 1:         //向下判断
                            {
                                for (int t = 0; t < 5; t++)//判断另外四个棋子
                                {
                                    if (4 == t)//t值等于4说明前面四个子
                                    {
                                        return true;
                                    }
                                    if (map.length == ++x || temp != map[x][y])//y值等于BOARD_SIZE表明已超出棋盘边界
                                    {
                                        break;
                                    }
                                }
                                break;
                            }
                            case 2:        //向右下方向判断
                            {
                                for (int t = 0; t < 5; t++)//判断另外四个棋子
                                {
                                    if (4 == t)//t值等于4说明前面四个子
                                    {
                                        return true;
                                    }
                                    if (map.length == ++x || map.length == ++y || temp != map[x][y])//x、y值等于BOARD_SIZE表明已超出棋盘边界
                                    {
                                        break;
                                    }
                                }
                                break;
                            }
                            case 3:      //向左下放下判断
                            {
                                for (int t = 0; t < 5; t++)//判断另外四个棋子
                                {
                                    if (4 == t)//t值等于4说明前面四个子
                                    {
                                        return true;
                                    }
                                    if (map.length == ++x || -1 == --y || temp != map[x][y])//x等于BOARD_SIZE或者y值等于-1表明已超出棋盘边界
                                    {
                                        break;
                                    }
                                }
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static int checkRow(int r, int c, int[][] map) {
        int count = 1;
        // 向右
        for (int j = c + 1; j < map.length; j++) {
            if (map[r][c] == map[r][j]) {
                count++;
            } else {
                break;
            }
        }

        // 往左
        for (int j = c - 1; j >= 0; j--) {
            if (map[r][c] == map[r][j]) {
                count++;
            } else {
                break;
            }
        }
        return count;

    }

    public static int checkCol(int r, int c, int[][] map) {
        int count = 1;
        // 向上
        for (int j = r - 1; j >= 0; j--) {
            if (map[r][c] == map[j][c]) {
                count++;
            } else {
                break;
            }
        }

        // 往下
        for (int j = r + 1; j < map.length; j++) {
            if (map[r][c] == map[j][c]) {
                count++;
            } else {
                break;
            }
        }
        return count;

    }

    public static int checkxiezuo(int r, int c, int[][] map) {
        int count = 1;
        // 向左上
        for (int j = r - 1, i = c - 1; j >= 0 && i >= 0; j--, i--) {
            if (map[r][c] == map[j][i]) {
                count++;
            } else {
                break;
            }
        }

        // 往右下
        for (int j = r + 1, i = c + 1; j < map.length && i < map.length; j++, i++) {
            if (map[r][c] == map[j][c]) {
                count++;
            } else {
                break;
            }
        }
        return count;

    }

    public static int checkxieyou(int r, int c, int[][] map) {
        int count = 1;
        // 向左下
        for (int j = r + 1, i = c - 1; j < map.length && i >= 0; j++, i--) {
            if (map[r][c] == map[j][i]) {
                count++;
            } else {
                break;
            }
        }

        // 往右上
        for (int j = r - 1, i = c + 1; j >= 0 && i < map.length; j--, i++) {
            if (map[r][c] == map[j][c]) {
                count++;
            } else {
                break;
            }
        }
        return count;

    }


    public static boolean longLink(int r, int c, int[][] map) {
        if (map[r][c] != 0) {
            return (checkRow(r, c, map) > 5 || checkxiezuo(r, c, map) > 5
                    || checkCol(r, c, map) > 5 || checkxieyou(r, c, map) > 5);
        } else {
            return false;
        }
    }

    public static boolean isOverThree(int r, int c, int[][] map) {

        return three(r, c, map) > 1 && map[r][c] != 0;
    }

    public static int three(int r, int c, int[][] map) {
        int i = checkRow(r, c, map) >= 3 ? 1 : 0;
        int i1 = checkCol(r, c, map) >= 3 ? 1 : 0;
        int i2 = checkxieyou(r, c, map) >= 3 ? 1 : 0;
        int i3 = checkxiezuo(r, c, map) >= 3 ? 1 : 0;
        return i + i1 + i2 + i3;
    }

    public static boolean isOverFour(int r, int c, int[][] map) {
        int[][] ints = new int[map.length][map.length];
        for (int i = 0; i < ints.length; i++) {
            for (int j = 0; j < ints.length; j++) {
                if (map[i][j] == 1) {
                    ints[i][j] = 1;
                } else {
                    ints[i][j] = 0;
                }
            }
        }
        return Four(r, c, ints, 1, 'e') > 1 && map[r][c] != 0;
    }


    public static int Four(int r, int c, int[][] map, int ce, char direction) {
        if (map[r][c] == 1) {
            int i, j, count4 = 0;
            if (ce == 1) {
                // 判断横向四
                for (j = 0; j < map.length - 4; j++) {
                    if (map[r][j] + map[r][j + 1] + map[r][j + 2]
                            + map[r][j + 3] + map[r][j + 4] == 4) {
                        count4++;
                        for (int k = 0; k < 5; k++) {
                            if (map[r][j + k] == 1) {
                                count4 += Four(r, j + k, map, 0, 'a');
                                if (count4 > 1) {
                                    return 2;
                                }
                            }
                        }
                        break;
                    }
                }

                // 判断纵向四
                for (i = 0; i < map.length - 4; i++) {
                    if (map[i][c] + map[i + 1][c] + map[i + 2][c]
                            + map[i + 3][c] + map[i + 4][c] == 4) {
                        count4++;
                        for (int k = 0; k < 5; k++) {
                            if (map[i + k][c] == 1) {
                                count4 += Four(i + k, c, map, 0, 'b');
                                if (count4 > 1) {
                                    return 2;
                                }
                            }
                        }
                        break;
                    }
                }

                // 判断“\”向四
                if (c > r) {
                    for (i = 0, j = c - r; i < (map.length - 4 - c + r); i++, j++) {
                        if (map[i][j] + map[i + 1][j + 1]
                                + map[i + 2][j + 2] + map[i + 3][j + 3]
                                + map[i + 4][j + 4] == 4) {
                            count4++;
                            for (int k = 0; k < 5; k++) {
                                if (map[i + k][j + k] == 1) {
                                    count4 += Four(i + k, j + k, map, 0, 'c');
                                    if (count4 > 1) {
                                        return 2;
                                    }
                                }
                            }
                            break;
                        }
                    }
                } else {
                    for (i = r - c, j = 0; i < map.length - 4; i++, j++) { // 判断“\”向“活三”
                        if (map[i][j] + map[i + 1][j + 1]
                                + map[i + 2][j + 2] + map[i + 3][j + 3]
                                + map[i + 4][j + 4] == 4) {
                            count4++;
                            for (int k = 0; k < 5; k++) {
                                if (map[i + k][j + k] == 1) {
                                    count4 += Four(i + k, j + k, map, 0, 'c');
                                    if (count4 > 1) {
                                        return 2;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }

                if (r + c < 15) {

                    for (i = r + c, j = 0; i >= 4; i--, j++) { // 判断“/”向“活三”
                        if (map[i][j] + map[i - 1][j + 1]
                                + map[i - 2][j + 2] + map[i - 3][j + 3]
                                + map[i - 4][j + 4] == 4) {
                            count4++;
                            for (int k = 0; k < 5; k++) {
                                if (map[i - k][j + k] == 1) {
                                    count4 += Four(i - k, j + k, map, 0, 'd');
                                    if (count4 > 1) {
                                        return 2;
                                    }
                                }
                            }
                            break;
                        }
                    }
                } else {
                    for (i = map.length - 1, j = r + c - (map.length - 1); j < map.length - 4; i--, j++) { // 判断“/”向“活三”
                        if (map[i][j] + map[i - 1][j + 1]
                                + map[i - 2][j + 2] + map[i - 3][j + 3]
                                + map[i - 4][j + 4] == 4) {
                            count4++;
                            for (int k = 0; k < 5; k++) {
                                if (map[i - k][j + k] == 1) {
                                    count4 += Four(i - k, j + k, map, 0, 'd');
                                    if (count4 > 1) {
                                        return 2;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }

                if (count4 > 1) {
                    return 2;
                }

            } else {

                if (direction != 'a') {
                    // 判断横向四
                    for (j = 0; j < map.length - 4; j++) {
                        if (map[r][j] + map[r][j + 1]
                                + map[r][j + 2] + map[r][j + 3]
                                + map[r][j + 4] == 4) {
                            return 1;
                        }
                    }
                }

                if (direction != 'b') {
                    // 判断纵向四
                    for (i = 0; i < map.length - 4; i++) {
                        if (map[i][c] + map[i + 1][c]
                                + map[i + 2][c] + map[i + 3][c]
                                + map[i + 4][c] == 4) {
                            return 1;
                        }
                    }
                }

                if (direction != 'c') {
                    // 判断“\”向四
                    if (c > r) {
                        for (i = 0, j = c - r; i < (map.length - 4 - c + r); i++, j++) {
                            if (map[i][j] + map[i + 1][j + 1]
                                    + map[i + 2][j + 2]
                                    + map[i + 3][j + 3]
                                    + map[i + 4][j + 4] == 4) {
                                return 1;
                            }
                        }
                    } else {
                        for (i = r - c, j = 0; i < map.length - 4; i++, j++) { // 判断“\”向“活三”
                            if (map[i][j] + map[i + 1][j + 1]
                                    + map[i + 2][j + 2]
                                    + map[i + 3][j + 3]
                                    + map[i + 4][j + 4] == 4) {
                                return 1;
                            }
                        }
                    }
                }

                if (direction != 'd') {
                    if (r + c < 15) {

                        for (i = r + c, j = 0; i >= 4; i--, j++) { // 判断“/”向“活三”
                            if (map[i][j] + map[i - 1][j + 1]
                                    + map[i - 2][j + 2]
                                    + map[i - 3][j + 3]
                                    + map[i - 4][j + 4] == 4) {
                                return 1;
                            }
                        }
                    } else {
                        for (i = map.length - 1, j = r + c - (map.length - 1); j < map.length - 4; i--, j++) { // 判断“/”向“活三”
                            if (map[i][j] + map[i - 1][j + 1]
                                    + map[i - 2][j + 2]
                                    + map[i - 3][j + 3]
                                    + map[i - 4][j + 4] == 4) {
                                return 1;
                            }
                        }
                    }
                }
            }

            // 横向特殊四四
            if (c > 2 && c < 12 && map[r][c - 3] == 1
                    && map[r][c - 2] == 0 && map[r][c - 1] == 1
                    && map[r][c + 3] == 1 && map[r][c + 2] == 0
                    && map[r][c + 1] == 1) {
                return 2;
            } else if (c > 2 && c < map.length - 4 && map[r][c - 3] == 1
                    && map[r][c - 2] == 1 && map[r][c - 1] == 0
                    && map[r][c + 4] == 1 && map[r][c + 3] == 1
                    && map[r][c + 2] == 0 && map[r][c + 1] == 1) {
                return 2;
            } else if (c > 3 && c < map.length - 4 && map[r][c - 4] == 1
                    && map[r][c - 3] == 1 && map[r][c - 2] == 1
                    && map[r][c - 1] == 0 && map[r][c + 4] == 1
                    && map[r][c + 3] == 1 && map[r][c + 2] == 1
                    && map[r][c + 1] == 0) {
                return 2;
            } else if (c > 3 && c < map.length - 4 && map[r][c - 4] == 1
                    && map[r][c - 3] == 1 && map[r][c - 2] == 0
                    && map[r][c - 1] == 1 && map[r][c + 4] == 1
                    && map[r][c + 3] == 1 && map[r][c + 2] == 0
                    && map[r][c + 1] == 1) {
                return 2;
            }

            // 纵向特殊四四
            if (r > 2 && r < 12 && map[r - 3][c] == 1
                    && map[r - 2][c] == 0 && map[r - 1][c] == 1
                    && map[r + 3][c] == 1 && map[r + 2][c] == 0
                    && map[r + 1][c] == 1) {
                return 2;
            } else if (r > 2 && r < map.length - 4 && map[r - 3][c] == 1
                    && map[r - 2][c] == 1 && map[r - 1][c] == 0
                    && map[r + 4][c] == 1 && map[r + 3][c] == 1
                    && map[r + 2][c] == 0 && map[r + 1][c] == 1) {
                return 2;
            } else if (r > 3 && r < map.length - 4 && map[r - 4][c] == 1
                    && map[r - 3][c] == 1 && map[r - 2][c] == 1
                    && map[r - 1][c] == 0 && map[r + 4][c] == 1
                    && map[r + 3][c] == 1 && map[r + 2][c] == 1
                    && map[r + 1][c] == 0) {
                return 2;
            } else if (r > 3 && r < map.length - 4 && map[r - 4][c] == 1
                    && map[r - 3][c] == 1 && map[r - 2][c] == 0
                    && map[r - 1][c] == 1 && map[r + 4][c] == 1
                    && map[r + 3][c] == 1 && map[r + 2][c] == 0
                    && map[r + 1][c] == 1) {
                return 2;
            }

            // "\"向特殊四四
            if (c > 2 && c < 12 && r > 2 && r < 12
                    && map[r - 3][c - 3] == 1 && map[r - 2][c - 2] == 0
                    && map[r - 1][c - 1] == 1 && map[r + 3][c + 3] == 1
                    && map[r + 2][c + 2] == 0 && map[r + 1][c + 1] == 1) {
                return 2;
            } else if (c > 2 && c < map.length - 4 && r > 2 && r < map.length - 4
                    && map[r - 3][c - 3] == 1 && map[r - 2][c - 2] == 1
                    && map[r - 1][c - 1] == 0 && map[r + 4][c + 4] == 1
                    && map[r + 3][c + 3] == 1 && map[r + 2][c + 2] == 0
                    && map[r + 1][c + 1] == 1) {
                return 2;
            } else if (c > 3 && c < map.length - 4 && r > 3 && r < map.length - 4
                    && map[r - 4][c - 4] == 1 && map[r - 3][c - 3] == 1
                    && map[r - 2][c - 2] == 1 && map[r - 1][c - 1] == 0
                    && map[r + 4][c + 4] == 1 && map[r + 3][c + 3] == 1
                    && map[r + 2][c + 2] == 1 && map[r + 1][c + 1] == 0) {
                return 2;
            } else if (c > 3 && c < map.length - 4 && r > 3 && r < map.length - 4
                    && map[r - 4][c - 4] == 1 && map[r - 3][c - 3] == 1
                    && map[r - 2][c - 2] == 0 && map[r - 1][c - 1] == 1
                    && map[r + 4][c + 4] == 1 && map[r + 3][c + 3] == 1
                    && map[r + 2][c + 2] == 0 && map[r + 1][c + 1] == 1) {
                return 2;
            }

            // "/"向特殊四四
            // 101 1 101
            if (c > 2 && c < 12 && r > 2 && r < 12
                    && map[r - 3][c + 3] == 1 && map[r - 2][c + 2] == 0
                    && map[r - 1][c + 1] == 1 && map[r + 3][c - 3] == 1
                    && map[r + 2][c - 2] == 0 && map[r + 1][c - 1] == 1) {
                return 2;
            }
            // map.length-40 1 10map.length-4
            else if (c > 3 && c < 12 && r > 3 && r < map.length - 4
                    && map[r - 3][c + 3] == 1 && map[r - 2][c + 2] == 1
                    && map[r - 1][c + 1] == 0 && map[r + 4][c - 4] == 1
                    && map[r + 3][c - 3] == 1 && map[r + 2][c - 2] == 0
                    && map[r + 1][c - 1] == 1) {
                return 2;
            }
            // map.length-410 1 0map.length-41
            else if (c > 3 && c < map.length - 4 && r > 3 && r < map.length - 4
                    && map[r - 4][c + 4] == 1 && map[r - 3][c + 3] == 1
                    && map[r - 2][c + 2] == 1 && map[r - 1][c + 1] == 0
                    && map[r + 4][c - 4] == 1 && map[r + 3][c - 3] == 1
                    && map[r + 2][c - 2] == 1 && map[r + 1][c - 1] == 0) {
                return 2;
            }
            // map.length-401 1 0map.length-4
            else if (c > 3 && c < map.length - 4 && r > 3 && r < map.length - 4) {
                if (map[r - 4][c + 4] == 1 && map[r - 3][c + 3] == 1
                        && map[r - 2][c + 2] == 0
                        && map[r - 1][c + 1] == 1
                        && map[r + 4][c - 4] == 1
                        && map[r + 3][c - 3] == 1
                        && map[r + 2][c - 2] == 0
                        && map[r + 1][c - 1] == 1) {
                    return 2;
                }
            }
        }

        return 0;

    }
}

