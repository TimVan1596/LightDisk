package com.timvanx.lightdisk;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.timvanx.blockchain.model.Block;
import lombok.Getter;

/**
 * <h3>BlockChain</h3>
 * <p>心跳消息记录实体</p>
 *
 * @author : TimVan
 * @date : 2020-04-29 13:21
 **/

public class HeartBeatLog {
    @Getter
    private int type;
    @Getter
    private long heartBeatID;
    @Getter
    private String date;
    private String data;

    /**
     * PUBLISH_NEW_BLOCK_TYPE = 1-发布新区块-种类
     * REQUEST_BLOCK_TYPE = 2-请求某一区块-种类
     * REQUEST_CHAIN_TYPE = 3-请求整个链的信息-种类
     * NORMAL_TYPE = 9-普通信息-种类
     * WRONG_TYPE = 0-发生错误
     */
    public final static int PUBLISH_NEW_BLOCK_TYPE = 1;
    public final static int REQUEST_BLOCK_TYPE = 2;
    public final static int REQUEST_CHAIN_TYPE = 3;
    public final static int NORMAL_TYPE = 9;
    public final static int WRONG_TYPE = 0;

    public HeartBeatLog(int type, String data) {
        this.type = type;
        this.data = data;
    }


    public HeartBeatLog(int type, long heartBeatID, String date) {
        this.type = type;
        this.heartBeatID = heartBeatID;
        this.date = date;
    }
}
