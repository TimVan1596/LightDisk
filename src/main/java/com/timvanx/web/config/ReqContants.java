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


    /**------------------------分类-主页 REQ_INDEX-------------------------*/
    /** REQ_GET_WALLTE = 获得钱包信息 */
    public static final String REQ_GET_WALLTE = "/GetWallet";


    /**------------------------分类-区块 REQ_BLOCK-------------------------*/
    /** REQ_BLOCK_GET_BLOCK_LIST = 获得区块列表 */
    public static final String REQ_BLOCK_GET_BLOCK_LIST = "/GetBlockList";


    // 进入登录页面 /login
    public static final String REQ_LOGIN = "/login";
}
