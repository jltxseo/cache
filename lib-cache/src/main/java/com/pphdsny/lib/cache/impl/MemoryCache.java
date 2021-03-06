package com.pphdsny.lib.cache.impl;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.pphdsny.lib.cache.protocal.ICacheParams;
import com.pphdsny.lib.cache.util.CacheUtil;
import com.pphdsny.lib.cache.util.GlobalMemoryCache;

import java.util.Map;

import rx.Observable;

/**
 * Created by wangpeng on 2018/6/28.
 * 内存缓存
 * 设置了缓存key{@link com.pphdsny.lib.cache.protocal.ECache#MEMORY_KEY}则默认开启内存缓存，不同业务逻辑子类可复写
 */
public class MemoryCache<T> extends AbstractCache<T> {

    @Override
    protected Observable<T> getDataImpl(ICacheParams<T> cacheParams) {
        Map<String, Object> params = cacheParams.getParams();
        String cacheKey = CacheUtil.getMemoryCacheKey(params);
        if (params == null || TextUtils.isEmpty(cacheKey)) {
            return CacheUtil.error("未开启内存缓存");
        }
        String cacheValue = GlobalMemoryCache.getInstance().get(cacheKey);
        if (TextUtils.isEmpty(cacheValue)) {
            return CacheUtil.error("未获取到有效内存缓存");
        }
        T t = null;
        try {
            t = fromJson(params, cacheValue, cacheParams.getDataClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (t == null) {
            return CacheUtil.error("内存缓存数据解析出错");
        }
        return Observable.just(t);
    }

    @Override
    protected void saveDataIml(ICacheParams<T> cacheParams, T t) {
        Map<String, Object> params = cacheParams.getParams();
        CacheUtil.updateMemoryCache(CacheUtil.getMemoryCacheKey(params), new Gson().toJson(t));
    }
}
