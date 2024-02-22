package com.bistro.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bistro.common.annotation.Excel;
import com.bistro.common.utils.DateUtils;
import com.bistro.common.utils.poi.ExcelUtil;
import com.bistro.module.game.domain.BetGameUserVo;
import com.bistro.module.game.domain.GameEnum;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExcelUtilsTest {

    @Test
    public void export_3million() throws Exception {
        List<BetGameUserVo> vos = new ArrayList<>();
        for (int i = 0; i < 3000000; i++) {
            BetGameUserVo vo = new BetGameUserVo();
            vo.setBetAmount(BigDecimal.valueOf(100));
            vo.setBetId(10);
            vo.setCurrency("IDR");
            vo.setGameCode("60");
            vo.setGameName("mines");
            vo.setMultiplier(BigDecimal.ONE);
            vo.setMerchantCode("1029841");
            vo.setPayoutAmount(BigDecimal.ONE);
            vo.setStatus((short) 0);
            vo.setSubGameCode("21");
            vo.setBetTime(LocalDateTime.now());
            vos.add(vo);
        }
        ExcelUtil<BetGameUserVo> excelUtils = new ExcelUtil(BetGameUserVo.class);
        System.out.println(System.currentTimeMillis());
        excelUtils.init(vos, "sheet1", "", Excel.Type.EXPORT);
        excelUtils.exportExcel(new FileOutputStream("ttt.xlsx"));
        System.out.println(System.currentTimeMillis());
    }

    @Test
    public void export_3million2() throws Exception {
        List<Pair<String, Integer>> list = new ArrayList<>();
        list.add(new ImmutablePair<>("用户id", 0));
        list.add(new ImmutablePair<>("用户名", 0));
        list.add(new ImmutablePair<>("赔率", 0));
        list.add(new ImmutablePair<>("状态", 0));
        list.add(new ImmutablePair<>("派奖", 0));
        list.add(new ImmutablePair<>("时间", 1));
        List datas = new ArrayList();
        for (int i = 0; i < 1000000; i++) {
            List data = new ArrayList();
            data.add(Integer.valueOf(i));
            data.add("张三" + i);
            data.add(Double.valueOf(12.99));
            data.add("成功");
            data.add(Double.valueOf(99.99));
            data.add(LocalDateTime.now());
            datas.add(data);
        }

        long start = System.currentTimeMillis();
        ExcelUtil.exportExcel(list, datas, new FileOutputStream("ttt.xlsx"));
        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    public void export_csv() throws IOException {

        List<String> headers = Arrays.stream(BetGameUserVo.class.getDeclaredFields()).filter(
                a -> a.getAnnotation(Excel.class) != null
        ).map(f -> f.getName()).toList();
        System.out.println(System.currentTimeMillis());
        FileWriter out = new FileWriter("ttt.csv");
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader(headers.toArray(new String[0])))) {
            for (int i = 0; i < 3000000; i++) {
                String tmp = i + "";
                printer.printRecord(headers.stream().map(h -> h + tmp)
                        .collect(Collectors.toList()));
            }
        }
        System.out.println(System.currentTimeMillis());

    }

    @Test
    public void importFile() {
        try {
//            InputStream is = new FileInputStream("C:\\Users\\dandg\\Desktop\\abc.xls");
            File file = new File("C:\\Users\\dandg\\Desktop\\bbbbb.xlsx");
            InputStream is = new FileInputStream(file);
            List<BetInfo> list = new ExcelUtil<BetInfo>(BetInfo.class).importExcel(is);
            System.out.println(JSONObject.toJSONString(list.get(1)));
            List<Pair<String, Integer>> header = new ArrayList<>();


            header.add(new ImmutablePair<>("betId", 0));
            header.add(new ImmutablePair<>("用户名", 0));
            header.add(new ImmutablePair<>("选项", 0));
            header.add(new ImmutablePair<>("下注金额", 0));
            header.add(new ImmutablePair<>("派奖金额", 0));
            header.add(new ImmutablePair<>("状态", 0));
            header.add(new ImmutablePair<>("下注时间", 0));
            header.add(new ImmutablePair<>("开始下注时间", 0));

            List datas = new ArrayList();

            for (BetInfo betInfo : list) {
                List<JSONObject> contents = JSON.parseObject(betInfo.getGameContent()).getJSONArray("content").toJavaList(JSONObject.class);
                for (JSONObject item : contents) {

                    List data = new ArrayList();
                    data.add(betInfo.getBetId());
                    data.add(betInfo.getUserName());
                    data.add(item.get("option"));
                    data.add(item.get("betAmount"));
                    data.add(item.get("payoutAmount"));
                    if (item.getBigDecimal("payoutAmount").compareTo(BigDecimal.ZERO) == 0) {
                        data.add("未中奖");
                    } else {
                        data.add(GameEnum.BetStatusEnum.getNameByStatus(Integer.valueOf(betInfo.getStatus())));
                    }

                    data.add(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, betInfo.getBetTime()));
                    data.add(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, betInfo.getBetStartTime()));

                    datas.add(data);
                }

            }
            ExcelUtil.exportExcel(header, datas, new FileOutputStream("数据.xlsx"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
