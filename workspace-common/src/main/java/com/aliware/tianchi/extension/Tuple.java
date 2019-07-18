package com.aliware.tianchi.extension;

import java.util.Objects;

/**
 * @author Fan Huaran
 * created on 2019/7/15
 * @description
 */
public class Tuple<T,V> {
    private T item1;

    private V item2;

    public Tuple() {
    }

    public Tuple(T item1, V item2) {
        this.item1 = item1;
        this.item2 = item2;
    }

    public T getItem1() {
        return item1;
    }

    public void setItem1(T item1) {
        this.item1 = item1;
    }

    public V getItem2() {
        return item2;
    }

    public void setItem2(V item2) {
        this.item2 = item2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple)) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        return Objects.equals(item1, tuple.item1) &&
                Objects.equals(item2, tuple.item2);
    }

    @Override
    public int hashCode() {

        return Objects.hash(item1, item2);
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "item1=" + item1 +
                ", item2=" + item2 +
                '}';
    }
}
