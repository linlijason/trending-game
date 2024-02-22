package com.bistro.module.sys.service;

import com.bistro.common.exception.proxy.ProxyException;
import com.bistro.module.sys.mapper.SysSequenceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bistro.module.sys.domain.SysSequence;

@Service
public class SysSequenceServiceImpl implements  SysSequenceService {

    @Autowired
    private SysSequenceMapper sysSequenceMapper;

    @Override
    public synchronized long incrementAndGet(String key) {
        int retry = 3;
        while (retry > 0) {
            SysSequence seq = sysSequenceMapper.selectSequenceByName(key);
            int cnt = sysSequenceMapper.incrementValue(seq.getId(), seq.getValue());
            if (cnt == 1) {
                return seq.getValue() + 1;
            }
            retry--;
        }
        throw new ProxyException("自增值获取失败:" + key);
    }
}
