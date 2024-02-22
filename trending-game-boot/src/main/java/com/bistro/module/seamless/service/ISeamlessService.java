package com.bistro.module.seamless.service;

import com.bistro.module.api.vo.PlayerInfo;
import com.bistro.module.seamless.vo.MySeamlessRequest;

import java.util.Map;

public interface ISeamlessService {

    Map generateToken(MySeamlessRequest request);

    Map createPlayerAccount(MySeamlessRequest request);

    String verifySignAndAuthToken(MySeamlessRequest request);

    Map gameLanuncher(MySeamlessRequest request, String signKey);

    String getSeamleesToken(Long uid);

    PlayerInfo getUserInfo(Long uid);




}
