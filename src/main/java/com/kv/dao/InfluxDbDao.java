package com.kv.dao;

public interface InfluxDbDao<T> {
    void save(T t);
}
