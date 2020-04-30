package com.timvanx.gossip.model;

import com.timvanx.gossip.impl.ImplCrdtOperator;
import org.apache.gossip.crdt.GrowOnlyCounter;
import org.apache.gossip.crdt.OrSet;
import org.apache.gossip.manager.GossipManager;
import org.apache.gossip.model.SharedDataMessage;

import java.util.*;

/**
 * <h3>BlockChain</h3>
 * <p>Crdt键值对</p>
 *
 * @author : TimVan
 * @date : 2020-04-01 16:53
 **/
public class CrdtMap implements ImplCrdtOperator {
    /**
     * ALL_KEY_NAME_SPACE = 保存所有key名称的固定集合
     */
    private final static String ALL_KEY_NAME_SPACE = "ALL_KEY_NAME_SPACE";

    /**
     * gossipService = gossip底层服务
     */
    private GossipManager gossipService = null;


    public CrdtMap(GossipManager gossipService) {
        this.gossipService = gossipService;
    }

    /**
     * 向对应主键命名空间，添加值
     *
     * @param val 待添加值
     * @param key 对应命名空间名称
     * @return 是否添加成功
     */
    @Override
    public boolean push(String val, String key) {
        boolean isSuccess = false;
        if (isKeyEqualsToAllKeyNameSpace(key)) {
            isSuccess = false;
        } else {
            addToAllKeyNameSpace(key);
            //讲添加信息封装为一个 SharedDataMessage
            SharedDataMessage message = new SharedDataMessage();
            message.setExpireAt(Long.MAX_VALUE);
            //设置主键
            message.setKey(key);
            //装载 待添加字符串
            message.setPayload(new OrSet<>(val));
            message.setTimestamp(System.currentTimeMillis());
            //gossipService整合消息
            gossipService.merge(message);
            isSuccess = true;
        }


        return isSuccess;
    }

    /**
     * 向对应主键命名空间，删除值
     *
     * @param val 待添加值
     * @param key 对应命名空间名称
     * @return 是否删除成功
     */
    @Override
    public boolean remove(String val, String key) {
        boolean isSuccess = false;
        //TODO:删除功能暂未实现
        System.out.println("删除功能暂未实现!!!!");
        return isSuccess;
    }

    /**
     * 向对应主键命名空间，获取值
     *
     * @param key 对应命名空间名称
     * @return 返回对应值
     */
    @Override
    public String get(String key) {
        return (gossipService.findCrdt(key) == null
                ? "" : gossipService.findCrdt(key).value().toString());
    }

    /**
     * 向对应累加器，添加数字, acc = accumulator(累加器)
     *
     * @param value 待添加值
     * @param key 对应命名空间名称
     * @return 是否添加成功
     * *
     */
    @Override
    public boolean accAdd(String value, String key) {

        boolean isSuccess = false;
        if (isKeyEqualsToAllKeyNameSpace(key)) {
            isSuccess = false;
        } else {
            addToAllKeyNameSpace(key);
            GrowOnlyCounter counter = (GrowOnlyCounter) gossipService.findCrdt(key);
            Long aLong = Long.valueOf(value);
            if (counter == null) {
                counter = new GrowOnlyCounter(new GrowOnlyCounter
                        .Builder(gossipService).increment((aLong)));
            } else {
                counter = new GrowOnlyCounter(counter, new GrowOnlyCounter
                        .Builder(gossipService).increment((aLong)));
            }
            SharedDataMessage message = new SharedDataMessage();
            message.setExpireAt(Long.MAX_VALUE);
            message.setKey(key);
            message.setPayload(counter);
            message.setTimestamp(System.currentTimeMillis());
            gossipService.merge(message);
        }

        return isSuccess;
    }

    /**
     * (弃用)向对应累加器，获取值
     *
     * @param key 对应命名空间名称
     * @return 获取的值
     */
    @Override @Deprecated
    public String accGet(String key) {
        return (gossipService.findCrdt(key) == null
                ? "" : gossipService.findCrdt(key).value().toString());

    }

    /**
     * 返回此映射中包含的键的 Set 视图
     */
    public Set<String> keySet() {
        Set<String> keySet = new HashSet<>();
        String keySetStr =
                (gossipService.findCrdt(ALL_KEY_NAME_SPACE) == null ?
                        "" : gossipService.findCrdt(ALL_KEY_NAME_SPACE)
                        .value().toString());


        keySetStr = keySetStr.substring(1, keySetStr.length() - 1);
        String[] strArray = keySetStr.split(",");
        Collections.addAll(keySet, strArray);
        return keySet;
    }

    /**
     * 检查是否对 ALL_KEY_NAME_SPACE 操作
     */
    private boolean isKeyEqualsToAllKeyNameSpace(String key) {
        return key.equals(ALL_KEY_NAME_SPACE);
    }

    /**
     * 检查是否对 ALL_KEY_NAME_SPACE 操作
     */
    private void addToAllKeyNameSpace(String key) {
        //讲添加信息封装为一个 SharedDataMessage
        SharedDataMessage message = new SharedDataMessage();
        message.setExpireAt(Long.MAX_VALUE);
        //设置主键
        message.setKey(ALL_KEY_NAME_SPACE);
        //装载 待添加字符串
        message.setPayload(new OrSet<String>(key));
        message.setTimestamp(System.currentTimeMillis());
        //gossipService整合消息
        gossipService.merge(message);
    }
}
