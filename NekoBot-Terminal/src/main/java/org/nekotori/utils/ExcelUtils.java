package org.nekotori.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ExcelUtils {


    public static void main(String[] args) throws IOException {
        JSONArray objects = new JSONArray();
        JSONObject income = new JSONObject();
        income.putOnce("nowStage",111);
        income.putOnce("nextStage",111);
        income.putOnce("totalDamage",11111111);
        income.putOnce("timestamp",System.currentTimeMillis());
        income.putOnce("time",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        income.putOnce("reporterId",121223143L);
        income.putOnce("reporter", "das");
        income.putOnce("thisDamage",3424324);
        objects.add(income);
    }


    public static InputStream generateWBExcel(String fileName, Collection<JSONObject> array) {
        List<WorldBossHistoryData> collect = array.stream().map(o -> {
            String s = o.toString();
            return JSONUtil.toBean(s, WorldBossHistoryData.class);
        }).collect(Collectors.toList());
        fileName = "excel/"+fileName+System.currentTimeMillis()+".xlsx";
        EasyExcel.write(fileName,WorldBossHistoryData.class).sheet(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).doWrite(()-> collect);
        return FileUtil.getInputStream(new File(fileName));
    }



    @Getter
    @Setter
    @EqualsAndHashCode
    public static class WorldBossHistoryData{

        @ExcelProperty({"出刀统计", "时间"})
        @DateTimeFormat("yyyy年MM月dd日-HH时mm分ss秒")
        @ColumnWidth(30)
        private Date time;

        @ExcelProperty({"出刀统计", "出刀人"})
        @ColumnWidth(15)
        private String reporter;

        @ExcelProperty({"出刀统计", "本次伤害"})
        @ColumnWidth(10)
        private Long thisDamage;

        @ExcelProperty({"出刀统计", "阶段"})
        @ColumnWidth(10)
        private String nowStageName;

        @ExcelProperty({"出刀统计", "阶段索引"})
        @ColumnWidth(15)
        private String nowStage;
    }
}



