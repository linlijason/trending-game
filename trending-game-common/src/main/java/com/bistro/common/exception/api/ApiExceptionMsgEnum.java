package com.bistro.common.exception.api;

public enum ApiExceptionMsgEnum {

    //100001 100_001  通用
    COM_UNKNOWN_ERROR(100000),//未知错误，系统错误
    COM_PARAM_ERROR(100001),//参数错误
    GAME_NOT_FOUND(100002),//游戏不存在
    USER_INFO_ERROR(100003),//玩家信息获取失败
    LOCK_GET_ERROR(100004),//操作太频繁
    COM_NOT_OPEN_ERROR(100005),//游戏暂时关闭
    SEAMLESS_TOKEN_EXPIRED_ERROR(100006),//token过期，请重新启动游戏
    COM_SEAMLESS_ERROR(100007),//平台接口返回错误信息，原样展示不翻译

    //110 000
    BET_PAY_ERROR(110000),//下注扣款失败
    BET_AMOUNT_MAX_LIMIT(110001),//下注金额超过最大限制
    BET_AMOUNT_MIN_LIMIT(110002),//下注金额超过最小限制
    BET_ONCE_LIMIT(110003),//一期只能下单一次
    BET_TIME_LIMIT(110004),//下注时间已过,不能下单
    BET_TIME_LIMIT_CANCEL(110005),//下注时间已过,不能取消
    BET_CANCEL_ERROR(110006),//取消失败

    PAYOUT_PAY_ERROR(120000),//提现支付失败
    PAYOUT_AUTO_PAY_ERROR(120001),//自动模式下提现支付失败
    PAYOUT_DONE_ERROR(120002);//已提取过了


    private int code;

    ApiExceptionMsgEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }


}
