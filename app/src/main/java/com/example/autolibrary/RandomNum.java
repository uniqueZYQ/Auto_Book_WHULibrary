package com.example.autolibrary;

import java.util.Random;

public class RandomNum {
    /**
     * 生成int整型常量，因为lvt和lvpt的含义尚不清晰
     * @param max 上界
     * @param min 下界
     * @return 生成的32位长度的16进制字符串
     */
    public String initInt(int min, int max){
        Random random = new Random();
        return String.valueOf(random.nextInt(max)%(max-min+1) + min);
    }

    public static final String[] POOL = new String[]{"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};

    /**
     * 生成32位16进制字符串
     * @return 生成的32位长度的16进制字符串
     */
    public String initHex(){
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 32; i++) {
            sb.append(POOL[random.nextInt(POOL.length)]);
        }
        return sb.toString();
    }
}
