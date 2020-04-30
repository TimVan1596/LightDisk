package com.timvanx.gossip.impl;

/**
 * <h3>BlockChain</h3>
 * <p>Crdt数据类型-增删改查-接口</p>
 *
 * @author : TimVan
 * @date : 2020-4-1 16:08:01
 **/
public interface ImplCrdtOperator {
    /** 向对应主键命名空间，添加值
     * @param val 待添加值
     * @param key 对应命名空间名称
     * @return 是否添加成功
     * */
    boolean push(String val , String key);

    /** 向对应主键命名空间，删除值
     * @param val 待添加值
     * @param key 对应命名空间名称
     * @return 是否删除成功
     * */
    boolean remove(String val , String key);

    /** 向对应主键命名空间，获取值
     * @param key 对应命名空间名称
     * @return 返回对应值
     * */
    String get(String key);

    /** 向对应累加器，添加数字, acc = accumulator(累加器)
     * @param val 待添加值
     * @param key 对应命名空间名称
     * @return 是否添加成功
     * * */
    boolean accAdd(String val , String key);

    /** 向对应累加器，获取值
     * @param key 对应命名空间名称
     * @return 获取的值     * */
    String accGet(String key);

}
