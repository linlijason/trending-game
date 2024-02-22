package com.bistro.module.sys.mapper;

import com.bistro.module.sys.domain.SysSequence;
import org.apache.ibatis.annotations.Param;

public interface SysSequenceMapper {

    SysSequence selectSequenceByName(@Param("name") String name);

    int incrementValue(@Param("id") long id, @Param("value") int value);
}
