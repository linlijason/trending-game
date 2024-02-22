package com.bistro.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SignUtilsTest {

    @Test
    public void concatSignStringTest(){
        SignUtilsTestBean bean=new SignUtilsTestBean();
        bean.setA(1);
        bean.setB(new SignUtilsTestBean.BDTO());
        bean.getB().setX(0);
        bean.getB().setY(0);

        bean.setC("{\"x\":1,\"y\":1}");
        bean.setD("");
        bean.setE(2.5);
        bean.setF(true);
        bean.setJ(0);
        bean.setK(-1);
        String signStr=SignUtils.concatSignString(bean,"SINGKEY");
        System.out.println(signStr);
        assertThat(signStr).isEqualTo("1{\"x\":1,\"y\":1}2.5true0-1SINGKEY");
        String sign=SignUtils.sha1Sign(signStr);
        assertThat(sign).isEqualTo("f7fa79276a736c8e39893a576c8f3daa689ce5f4");
    }

    public static class SignUtilsTestBean{


        @JsonProperty("b")
        private BDTO b;
        @JsonProperty("c")
        private String c;
        @JsonProperty("d")
        private String d;
        @JsonProperty("e")
        private Double e;
        @JsonProperty("f")
        private Boolean f;
        @JsonProperty("h")
        private Object h;
        @JsonProperty("j")
        private Integer j;
        @JsonProperty("k")
        private Integer k;

        @JsonProperty("a")
        private Integer a;

        public Integer getA() {
            return a;
        }

        public void setA(Integer a) {
            this.a = a;
        }

        public BDTO getB() {
            return b;
        }

        public void setB(BDTO b) {
            this.b = b;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        public String getD() {
            return d;
        }

        public void setD(String d) {
            this.d = d;
        }

        public Double getE() {
            return e;
        }

        public void setE(Double e) {
            this.e = e;
        }

        public Boolean getF() {
            return f;
        }

        public void setF(Boolean f) {
            this.f = f;
        }

        public Object getH() {
            return h;
        }

        public void setH(Object h) {
            this.h = h;
        }

        public Integer getJ() {
            return j;
        }

        public void setJ(Integer j) {
            this.j = j;
        }

        public Integer getK() {
            return k;
        }

        public void setK(Integer k) {
            this.k = k;
        }

        public static class BDTO {
            @JsonProperty("x")
            private Integer x;
            @JsonProperty("y")
            private Integer y;

            public Integer getX() {
                return x;
            }

            public void setX(Integer x) {
                this.x = x;
            }

            public Integer getY() {
                return y;
            }

            public void setY(Integer y) {
                this.y = y;
            }
        }
    }



}