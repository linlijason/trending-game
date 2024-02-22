package com.bistro.utils;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.junit.Test;

public class Encrypt {


    @Test
    public void encrypt() {
        PooledPBEStringEncryptor pooledPBEStringEncryptor = new PooledPBEStringEncryptor();
        pooledPBEStringEncryptor.setPoolSize(1);
        pooledPBEStringEncryptor.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        pooledPBEStringEncryptor.setPassword("d6cd7cf4a4fbeb566e8a2fa0a93c44d0");
        pooledPBEStringEncryptor.setIvGenerator(new RandomIvGenerator());
        String enc = pooledPBEStringEncryptor.encrypt("47.108.189.93:8014/mapdev");
        System.out.println(enc);
        System.out.println(pooledPBEStringEncryptor.decrypt(enc));

        System.out.println(pooledPBEStringEncryptor.decrypt("/oMZgrN+eeMqNn43o8SLr7wqkAdMkhtgViIe1O8VlD/qMxcbIcUiKwYRjemfuHdp"));
    }
}
