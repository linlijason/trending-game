package com.bistro.constants;

public class RedisConstants {

    private static final String KEY_ONGOING_GAME = "ongoing_game_";//进行中的游戏_uid_code
    public static final long AUTH_TOKEN_EXPIRE = 7200;//秒
    public static final String AUTH_TOKEN_PREFIX = "authtoken_";//auth token

    public static final String SEAMLESS_TOKEN_PREFIX = "seamlesstoken_";//seamless token+uid

    public static final String LOCK_UID_GAME_CODE = "lock_uid_game_";

    public static final String FAKE_BRT_ID_INCR = "fake_brt_id_incr";

    public static final String RANK_ALL = "rank_all_zset_";
    public static final String RANK_PAY_OUT = "rank_pay_out_zset_";//payout从高到低
    public static final String RANK_MULTIPLIER = "rank_multiplier_zset_";//赔率从高到底
    public static final String RANK_SCORE_INCR = "rank_score_incr_";

    public static final String WEBSOCKET_HT = "websocket_ht_";//websocket 心跳 + id


    //币安行情
    public static final String BINANCE_TICKER = "binance_ticker_"; //+ symbol
    public static final String BINANCE_TICKER_ADD_TAG = "binance_ticker_add_tag_"; //symbol+ time
    public static final String BINANCE_TICKER_PRESET_PRICE_SET= "bnticker_preset_price_set_"; //+ symbol
    //奖期
    public static final String COIN_PERIOD_SET = "coin_period_set_"; //+ coin
    public static final String COIN_PERIOD_HASH = "coin_period_hash_"; //+ coin
    public static final String COIN_PERIOD_ROUND_NO = "coin_period_round_no_key";//+ coin
    public static final String COIN_PERIOD_ID_INIT = "10000";
    private static final String COIN_PERIOD_DRAW_TAG = "coin_period_draw_tag_";
    public static final String COIN_DRAW_KEY_RUNNING_JOB = "coin_draw_key_running_job";

    private static final String COIN_BET_ONCE_LIMIT = "coin_bet_once_limit_";//+game_code, 期数， uid
    private static final String RANK_COIN = "rank_coin_";
    private static final String COIN_USER_DRAW_RESULT_HASH = "coin_user_draw_result_hash_";//+ betid


    public static final String USER_BALANCE_CURRENCY = "user_balance_currency_";//+uid 现在只有一种currency

    public static final String COIN_TOTAL_AMOUNT_HASH = "COIN_TOTAL_AMOUNT_HASH";// member: uid_bet ,uid_payout

    public static String getKeyOngoingGame(long uid, String gameCode) {
        return new StringBuilder(KEY_ONGOING_GAME).append(uid).append("_").append(gameCode).toString();
    }

    public static String getLockUidGameCode(long uid, String gameCode) {
        return new StringBuilder(LOCK_UID_GAME_CODE).append(uid).append("_").append(gameCode).toString();
    }

    public static String getCoinBetOnceLimit(long uid, String gameCode, Long periodId) {
        return new StringBuilder(COIN_BET_ONCE_LIMIT).append(gameCode).append("_")
                .append(uid).append("_").append(periodId).toString();
    }

    public static String getCoinRankKey(String coin, String gameCode, Long periodId) {
        return new StringBuilder(RANK_COIN).append(gameCode).append("_")
                .append(coin).append("_").append(periodId).toString();
    }

    public static String getCoinUserDrawResultHashKey(String coin, String gameCode, Long periodId) {
        return new StringBuilder(COIN_USER_DRAW_RESULT_HASH).append(gameCode).append("_")
                .append(coin).append("_").append(periodId).toString();
    }

    public static String getCoinPeriodDrawTag(String coin, String gameCode, Long periodId) {
        return new StringBuilder(COIN_PERIOD_DRAW_TAG).append(gameCode).append("_")
                .append(coin).append("_").append(periodId).toString();
    }

}
