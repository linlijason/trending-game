package com.bistro.module.test.domain;

import com.bistro.common.annotation.Excel;
import com.bistro.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * test_addr对象 test_addr
 * 
 * @author jason.lin
 * @date 2021-12-17
 */
public class TestAddr extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Integer id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String addr;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String words;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String addr2;

    private String pass;

    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }
    public void setAddr(String addr) 
    {
        this.addr = addr;
    }

    public String getAddr() 
    {
        return addr;
    }
    public void setWords(String words) 
    {
        this.words = words;
    }

    public String getWords() 
    {
        return words;
    }
    public void setAddr2(String addr2) 
    {
        this.addr2 = addr2;
    }

    public String getAddr2() 
    {
        return addr2;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("addr", getAddr())
            .append("words", getWords())
            .append("addr2", getAddr2())
            .toString();
    }
}
