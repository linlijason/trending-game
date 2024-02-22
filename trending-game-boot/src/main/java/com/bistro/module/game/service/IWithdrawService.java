package com.bistro.module.game.service;

import com.bistro.message.model.MessageUser;
import com.bistro.message.model.WithdrawMessage;

public interface IWithdrawService {

    WithdrawMessage.Response withdraw(MessageUser from, WithdrawMessage.Request request);
}
