package com.bistro.module.game.service;

import com.bistro.common.core.domain.model.LoginUser;
import com.bistro.module.game.domain.BetGameUserQuery;
import com.bistro.module.game.domain.BetGameUserVo;

import java.util.List;

public interface BetGameUserService {
    List<BetGameUserVo> search(BetGameUserQuery query);

    void exportReport(BetGameUserQuery query, LoginUser loginUser);
}
