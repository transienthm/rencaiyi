// Copyright (C) 2015 Meituan
// All rights reserved
package hr.wozai.service.servicecommons.thrift.model;

/**
 * @author liangyafei
 * @version 1.0
 * @created 15-8-30 下午2:17
 */
public class Entry<T1, T2> {
    private T1 t1;
    private T2 t2;

    public Entry(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public T1 getT1() {
        return t1;
    }

    public void setT1(T1 t1) {
        this.t1 = t1;
    }

    public T2 getT2() {
        return t2;
    }

    public void setT2(T2 t2) {
        this.t2 = t2;
    }

    @Override
    public String toString() {
        return "Entry{" + "t1=" + t1 + ", t2=" + t2 + '}';
    }
}
