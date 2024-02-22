package com.bistro.utils;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class WeightRandomUtils<K, V extends Number> {

    private TreeMap<Double, K> weightMap = new TreeMap<Double, K>();

    public WeightRandomUtils(List<Pair<K, V>> list) {
        for (Pair<K, V> pair : list) {
            if (pair.getValue().doubleValue() <= 0) {
                continue;
            }
            double lastWeight = this.weightMap.size() == 0 ? 0 : this.weightMap.lastKey().doubleValue();//统一转为double
            this.weightMap.put(pair.getValue().doubleValue() + lastWeight, pair.getKey());//权重累加
        }
    }

    public K random() {
        double randomWeight = this.weightMap.lastKey() * Math.random();
        SortedMap<Double, K> tailMap = this.weightMap.tailMap(randomWeight, false);
        return this.weightMap.get(tailMap.firstKey());
    }
}
