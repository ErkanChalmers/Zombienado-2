package com.erkan.zombienado2.data.world;

/**
 * Created by Erik on 2018-08-05.
 */
public class Tuple<K, V> {
    private K k;
    private V v;

    public Tuple(K k, V v){
        this.k = k;
        this.v = v;
    }

    public K getFirst(){
        return k;
    }

    public V getSecond(){
        return v;
    }
}
