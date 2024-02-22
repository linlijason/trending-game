package com.bistro.module.game.service;

import java.math.BigDecimal;

public interface IManualTransactionService {

    BigDecimal payoutFailed(long betId);

    BigDecimal refund(long betId);
}
