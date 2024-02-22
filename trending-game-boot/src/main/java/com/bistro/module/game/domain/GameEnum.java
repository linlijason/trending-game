package com.bistro.module.game.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class GameEnum {

    public enum BetStatusEnum {
        NEW(1, "待扣款"),
        BET_DONE(2, "游戏中"),//下注扣款成功
        BET_FAILED(3, "下注失败"),//下注扣款失败
        WT_DONE(4, "派奖成功"),//提现成功
        WT_FAILED(5, "派奖失败"),//提现失败
        BOMB(6, "未中奖"),//未中奖
        REFUND(7, "已退款"),//退款
        PAY_ING(8, "派奖中"),//派奖中
        CANCEL(9, "取消下注"),//已取消
        ;

        private int status;
        private String name;

        BetStatusEnum(int status, String name) {
            this.status = status;
            this.name = name;
        }

        public int getStatus() {
            return status;
        }

        public String getName() {
            return name;
        }

        private static Map<Integer, String> statusMap = Arrays.stream(BetStatusEnum.values()).collect(Collectors.toMap(i -> i.status, i -> i.name
        ));

        public static String getNameByStatus(int status) {

            return statusMap.get(status);
        }


    }

    public enum PaymentBusinessTypeEnum {
        BET(1, "下注"),
        WD(2, "派奖"),
        REFUND(3, "退回投注"),
        CANCEL(4, "取消下注"),
        ;

        PaymentBusinessTypeEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        private int code;
        private String desc;

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }


}
