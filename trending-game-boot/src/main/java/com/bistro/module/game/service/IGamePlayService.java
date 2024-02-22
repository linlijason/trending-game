package com.bistro.module.game.service;

import com.bistro.message.model.MessageUser;
import com.bistro.message.model.PlayMessage;
import com.bistro.module.api.vo.OngoingGameInfo;

public interface IGamePlayService {

    PlayMessage.Response play(MessageUser from, PlayMessage.Request request);

    OngoingGameInfo getOngoingGame(Long uid, String gameCode);
}
