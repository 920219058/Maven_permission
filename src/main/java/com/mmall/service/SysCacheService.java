package com.mmall.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.beans.CacheKeyConstants;
import com.mmall.model.SysUser;
import com.mmall.util.JsonMapper;
import com.mmall.util.StringUtil;
import com.thread.Test.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SysCacheService {

    @Resource(name = "redisPool")
    private RedisPool redisPool;

    public void saveCache(String toSavedValue,int timeoutSeconds, CacheKeyConstants prefix){
        saveCache(toSavedValue,timeoutSeconds,prefix,null);
    }

    public void saveCache(String toSavedValue,int timeoutSeconds, CacheKeyConstants prefix,String... keys){
        if(toSavedValue == null){
            return;
        }
        ShardedJedis shardedJedis = null;
        try{
            String cachekey = generateCacheKey(prefix,keys);
            shardedJedis =redisPool.instance();
            shardedJedis.setex(cachekey,timeoutSeconds,toSavedValue);
        }catch(Exception e){
            log.error("save cache exception ,refix:{},keys:{}.",prefix.name(), JsonMapper.obj2String(keys));
        }finally {
            redisPool.saftClose(shardedJedis);
        }
    }

    public String getFormCache(CacheKeyConstants prefix,String... keys){
        ShardedJedis shardedJedis = null;
        String cacheKey = generateCacheKey(prefix,keys);
        try{
            shardedJedis = redisPool.instance();
            String value = shardedJedis.get(cacheKey);
            return value;
        } catch(Exception e){
            log.error("get form cache exception,prefix:{},keys:{}." ,prefix.name(), JsonMapper.obj2String(keys));
        }finally {
            redisPool.saftClose(shardedJedis);
        }
        return null;
    }

    private String generateCacheKey(CacheKeyConstants prefix,String... keys){
        String key = prefix.name();
        if(keys != null && keys.length > 0){
            key += "_" + Joiner.on("_").join(keys);
        }
        return key;
    }

    //  返回用户信息map
    public List<SysUser> getAllUserFromCache(CacheKeyConstants prefix, String... keys){
        List<SysUser> userLists = Lists.newArrayList();
        ShardedJedis shardedJedis = null;
        String cacheKey = generateCacheKey(prefix,keys);
        try{
            shardedJedis = redisPool.instance();
            Map<byte[],byte[]> results = shardedJedis.hgetAll(cacheKey.getBytes());
            if(results.size() != 0 ){
                for(Map.Entry<byte[],byte[]> result: results.entrySet()){
                    SysUser user = (SysUser) StringUtil.unserialize(result.getValue());
                    userLists.add(user);
                }
                return userLists;
            }
        } catch(Exception e){
            log.error("get form cache exception,prefix:{},keys:{}." ,prefix.name(), JsonMapper.obj2String(keys));
        }finally {
            redisPool.saftClose(shardedJedis);
        }
        return null;
    }

    public boolean saveAllUserInfoToCache(CacheKeyConstants prefix, int timeoutSeconds,List<SysUser> userLists, String... keys){
        ShardedJedis shardedJedis = null;
        try{
            String cachekey = generateCacheKey(prefix,keys);
            shardedJedis = redisPool.instance();
            Map<byte[],byte[]> map = Maps.newHashMap();
            for (int i = 0; i < userLists.size(); i++) {
                map.put(userLists.get(i).getUsername().getBytes(),StringUtil.serialise(userLists.get(i)));
            }
            shardedJedis.hmset(prefix.name().getBytes(),map);
            shardedJedis.expire(prefix.name().getBytes(),timeoutSeconds);
            // shardedJedis.setex(cachekey,timeoutSeconds,toSavedValue);
        }catch(Exception e){
            log.error("save cache exception ,refix:{},keys:{}.",prefix.name(), JsonMapper.obj2String(keys));
        }finally {
            redisPool.saftClose(shardedJedis);
        }
        return false;
    }

}
