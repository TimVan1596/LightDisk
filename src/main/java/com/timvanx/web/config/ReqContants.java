package com.timvanx.web.config;

/**
 * <h3>BlockChain</h3>
 * <p>前端发出请求的常量类</p>
 *
 * @author : TimVan
 * @date : 2020-05-01 22:55
 **/
public class ReqContants {
    /**-----------------------------------大类--------------------------------*/
    /** REQ_INDEX = 主页大类 */
    public static final String REQ_INDEX = "/common";
    /** REQ_BLOCK = 区块大类 */
    public static final String REQ_BLOCK = "/block";
    /** REQ_HEARTBEAT = 心跳消息大类 */
    public static final String REQ_HEARTBEAT = "/heartbeat";


    /**------------------------分类-主页 REQ_INDEX-------------------------*/
    /** REQ_GET_WALLTE = 获得钱包信息 */
    public static final String REQ_GET_WALLTE = "/GetWallet";


    /**------------------------分类-区块 REQ_BLOCK-------------------------*/
    /** REQ_BLOCK_GET_BLOCK_LIST = 获得区块列表 */
    public static final String REQ_BLOCK_GET_BLOCK_LIST = "/GetBlockList";
    /**  REQ_BLOCK_MINE_BLOCK = 挖矿   */
    public static final String REQ_BLOCK_MINE_BLOCK = "/mineBlock";
    /** REQ_DECODE_JSON_FROM_PRIVATE_KEY = 测试文件是否能用私钥解码成JSON格式 */
    public static final String REQ_DECODE_JSON_FROM_PRIVATE_KEY = "/decodeJsonFromPrivateKey";


    /**------------------------分类-心跳消息 REQ_HEARTBEAT-------------------------*/
    /** REQ_BLOCK_GET_BLOCK_LIST = 获得心跳消息列表 */
    public static final String REQ_HEARTBEAT_GET_HEARTBEAT_LIST = "/GetHeartbeatList";

    /** REQ_LOGIN = 进入登录页面 /login */
    public static final String REQ_LOGIN = "/login";
    /** REQ_SHUTDOWN = 关闭整个系统，回到登录页 /shutdown */
    public static final String REQ_SHUTDOWN = "/shutdown";

    /** REQ_REGISTRY = 注册/生成新钱包 /registry */
    public static final String REQ_REGISTRY = "/registry";
}
