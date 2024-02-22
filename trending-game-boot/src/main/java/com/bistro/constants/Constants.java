package com.bistro.constants;

import java.math.BigDecimal;

public class Constants {
    public static final String MINES_INDEX_ARRAY = "randomIndexArray";//存入游戏记录中的雷索引素组
    public static final String MINES_COMMISSION_RATE = "commissionRate";//存入游戏记录中的抽成
    public static final String MINES_BET_AMOUNT = "betAmount";//存入游戏记录中的下注金额
    public static final String MINES_MAX_PAYOUT_AMOUNT = "maxPayAmount";//存入游戏记录中最大奖励金额
    public static final String MINES_BET_CURRENCY = "currency";//存入游戏记录中下注货币
    public static final String MINES_BET_MULTIPLIER = "multiplier_map";//赔率


    public static final int SELECT_MINES_BLOCKS_MIN = 4;//选择雷最小数量
    public static final int MINES_BLOCKS = 25;
    public static final int PAYOUT_AMOUNT_DECIMAL_SCALE = 2;
    public static final int BET_AMOUNT_DECIMAL_SCALE = 2;

    public static BigDecimal mockAmount = new BigDecimal("10000000.12");

    public static final String MINES_GAME_CODE= "60";
    public static final String COIN_GAME_CODE= "61";
    public static final String COIN_BET_CONTENT= "content";
    public static final String COIN_BET_CONTENT_MERCHANT= "merchant";
    public static final String COIN_BET_CONTENT_MAX_PAYOUT= "maxPayAmount";
    public static final String COIN_BET_CONTENT_BET_PRICE= "betPrice";
    public static final String COIN_BET_CONTENT_BET_TIME= "betTime";
    public static final String COIN_BET_CONTENT_DRAW_RESULT= "drawResult";

    public static final int BINANCE_TICKER_SIZE = 360; //默认600条


    public static final String COIN_GOTIE = "GoTie"; //平

//    public static final String SIGN_KEY="dd35aPO0bd186dc6ace6We2e0fb48s70";//todo 测试用
//    public static final String MERCHANT_CODE="dd35aPO0bd186dc6ace6We2e0fb48s70";//todo 测试用
//    public static final String SECURE_KEY="dd35aPO0bd186dc6ace6We2e0fb48s70";//todo 测试用

}
