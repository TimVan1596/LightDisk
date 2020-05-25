package com.timvanx.lightdisk;

import com.timvanx.blockchain.model.Block;
import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import lombok.Getter;

/**
 * <h3>BlockChain</h3>
 * <p>心跳消息实体</p>
 *
 * @author : TimVan
 * @date : 2020-04-29 13:21
 **/
@Getter
public class HeartBeat {
    private int heartBeatID;
    private int type;
    private String data;

    /**
     * PUBLISH_NEW_BLOCK_TYPE = 1-发布新区块-种类
     * REQUEST_BLOCK_TYPE = 2-请求某一区块-种类
     * REQUEST_CHAIN_TYPE = 3-请求整个链的信息-种类
     * PUBLISH_NEW_TRANSACTION_TYPE = 4-发布新交易-种类
     * NORMAL_TYPE = 9-普通信息-种类
     * WRONG_TYPE = 0-发生错误
     */
    public final static int PUBLISH_NEW_BLOCK_TYPE = 1;
    public final static int REQUEST_BLOCK_TYPE = 2;
    public final static int REQUEST_CHAIN_TYPE = 3;
    public final static int PUBLISH_NEW_TRANSACTION_TYPE = 4;
    public final static int NORMAL_TYPE = 9;
    public final static int WRONG_TYPE = 0;

    public HeartBeat(int type, String data) {
        this.type = type;
        this.data = data;
    }

    public HeartBeat(int heartBeatID, int type, String data) {
        this.heartBeatID = heartBeatID;
        this.type = type;
        this.data = data;
    }

    /**
     * 打包新区块的心跳消息
     *
     * @return 返回心跳实体
     */
    public static HeartBeat packPublishNewBlock(String data) {
        return new HeartBeat(PUBLISH_NEW_BLOCK_TYPE, data);
    }

    /**
     * 打包新区块的心跳消息
     *
     * @return 返回心跳实体
     */
    public static HeartBeat packPublishNewBlock(int heartBeatID,String data) {
        return new HeartBeat(heartBeatID,PUBLISH_NEW_BLOCK_TYPE, data);
    }

    /**
     * 打包新交易的心跳消息
     *
     * @return 返回心跳实体
     */
    public static HeartBeat packPublishTransaction(int heartBeatID,String data) {
        return new HeartBeat(heartBeatID,PUBLISH_NEW_TRANSACTION_TYPE, data);
    }


    /**
     * 将心跳的Base64编码解码成心跳实体
     *
     * @return 返回心跳实体
     */
    public static HeartBeat decodePublishNewBlockBase64(String base64){
        String json = Base64.decodeStr(base64);
        return JSON.parseObject(json,HeartBeat.class);
    }


    /**
     * 返回错误信息的心跳实体
     * @param msg 错误信息
     * @return 返回心跳实体(含错误信息)
     */
    public static HeartBeat packWrongHeartBeat(String msg){
        return new HeartBeat(WRONG_TYPE, msg);
    }

    /**
     * 打包新区块的心跳消息
     * @param data 信息
     * @return 返回心跳实体JSON的Base64编码信息
     */
    public static String packPublishNewBlockBase64(String data) {
        HeartBeat heartBeat = packPublishNewBlock(data);
        String json = JSON.toJSONString(heartBeat);
        return Base64.encode(json);
    }

    /**
     * 发新交易
     * @param data 信息
     * @return 返回心跳实体JSON的Base64编码信息
     */
    public static String packPublishNewTransactionBase64(int heartBeatID,String data) {
        HeartBeat heartBeat = packPublishTransaction(heartBeatID,data);
        String json = JSON.toJSONString(heartBeat);
        return Base64.encode(json);
    }


    /**
     * 打包新区块的心跳消息
     * @param data 信息
     * @return 返回心跳实体JSON的Base64编码信息
     */
    public static String packPublishNewBlockBase64(int heartBeatID,String data) {
        HeartBeat heartBeat = packPublishNewBlock(heartBeatID,data);
        String json = JSON.toJSONString(heartBeat);
        return Base64.encode(json);
    }


    public static void main(String[] args) {
        String msg = "北京：调整响应级别绝不意味着防控标准降低 小区出入仍";
        long heatBeatNum = 1000;
        String base64Value = HeartBeat.packPublishNewBlockBase64(msg);

        base64Value = "eyJkYXRhIjoie1wiZGlmZmljdWx0eVRhcmdldFwiOjMsXCJoYXNoXCI6XCIwMDA4ZTQ5NmE3NGNhYzgxYWJmNDA3N2IzNGQyMzY0ODk4NTczYjQ4ZWMwNGY4NjcwM2Q1ZDFlYjM4ODc1MGZjXCIsXCJoZWlnaHRcIjoxLFwibWVya2xlUm9vdFwiOlwiYTNlMDg5ZWM4YWNjNDA0NTZiMjgxYzhkODZkMzhlZGRjYzQ1ZDNjYWY5OTVjNjg2ODU1NGU3ZGQyZDNhMjAyM1wiLFwibm9uY2VcIjo5OTY1MCxcInByZXZCbG9ja0hhc2hcIjpcIjNiNWMzYWQ4YmJjZDUzYTk5YTBmYjkxMjQ4ODNhNzgzNzI1Y2FjNTZmOTU3YmZlZTdiM2E1MDIzZDAxMzU5NWRcIixcInRpbWVzdGFtcFwiOjE1ODgxNjc5OTkyNDMsXCJ0cmFuc2FjdGlvbnNcIjpbe1wiaGFzaFwiOlwiNGE4N2EwZjdjMTUzMDg0MGVlYWRlOWUxYTg2NWM3MTQ2ZDRkODFmNjczYTU5Y2FhZDAwOTgwNDhhMDk4ZWIzYVwiLFwicHVibGljS2V5XCI6XCJhU3E5RHNOTnZHaFl4WXlxQTl3ZDJlZHVFQVo1QVhXZ0pUYlRGRHdSZDhxYXVBTEd5VlF4dVRleWN4WVlkd28yemZIRjFIOFNlUjdHWVM2NXlzcEpDSndDVHJFMVBVaGZ2clNFMThDY3ZXUmZRNzk1VHQzY2I2a21DeFRLXCIsXCJzY3JpcHRCeXRlc1wiOlwiWm5aelpBPT1cIixcInNjcmlwdFN0cmluZ1wiOlwiZnZzZFwiLFwidGltZXN0YW1wXCI6MTU4ODE2Nzk5NjMyM31dfSIsInR5cGUiOjF9, eyJkYXRhIjoie1wiZGlmZmljdWx0eVRhcmdldFwiOjMsXCJoYXNoXCI6XCIwMDBlMzQ3YjZmNDgyNjI0ZmNmN2QyYzIwODM5NjUzMDk0ZDIzZDliZThkOTliYjA5MWQyMDQ0MWMwZWFlMzhjXCIsXCJoZWlnaHRcIjo0LFwibWVya2xlUm9vdFwiOlwiOTc5NDkxMDk4YjU2ZTBmNTVhMWVmYzU4MTgzYjA5OTE5ZDYwMTBiZWZjY2M4YjBhODM2YTE1NGEyNTZjNGMwYlwiLFwibm9uY2VcIjozMDI3MzUsXCJwcmV2QmxvY2tIYXNoXCI6XCIwMDAwZDZmYmI0N2RhZmU2NWI2NTgzN2FiNmFjMGVmODVhYmE2YTNhYWQ3NjBkM2MwZTk5ODc0MjU4NzExOTlhXCIsXCJ0aW1lc3RhbXBcIjoxNTg4MTY3OTAwOTgwLFwidHJhbnNhY3Rpb25zXCI6W3tcImhhc2hcIjpcIjVkYjk5YWRlYTQxZjdjYzQzOWU1YmI4NGIxMzRjZWMxNTk2ZWYzYTQxYzA3OTUyNTEyMzQzYjQ0NWUxNDJjMTRcIixcInB1YmxpY0tleVwiOlwiYVNxOURzTk52R2hZeFl5cUE5d2QyZWR1RUFaNUFYV2dKVGJURkR3UmQ4cWF1QUxHeVZReHVUZXljeFlZZHdvMnpmSEYxSDhTZVI3R1lTNjV5c3BKQ0p3Q1RyRTFQVWhmdnJTRTE4Q2N2V1JmUTc5NVR0M2NiNmttQ3hUS1wiLFwic2NyaXB0Qnl0ZXNcIjpcImEycG9hV3M9XCIsXCJzY3JpcHRTdHJpbmdcIjpcImtqaGlrXCIsXCJ0aW1lc3RhbXBcIjoxNTg4MTY3ODk1MzA5fV19IiwidHlwZSI6MX0=, eyJkYXRhIjoie1wiZGlmZmljdWx0eVRhcmdldFwiOjMsXCJoYXNoXCI6XCIwMDBjMjExMDYyYzZmMTVjNTAxZTE4ZjgzYTBhYjg2ZmIwZWU0NGU5MGJhNjZlZWFiMzVlMmZjYTdiNGVmZDkyXCIsXCJoZWlnaHRcIjoyLFwibWVya2xlUm9vdFwiOlwiNWM1YzI3MTQ2YzQ3M2UxZGRkODgyOTIxMjM1YTQxOGE4ZGE4NzA3YmYyYmI3NjUzOGNjNDE4NjdkYjI1MDNmNFwiLFwibm9uY2VcIjo1MTk5NjEsXCJwcmV2QmxvY2tIYXNoXCI6XCIwMDBhM2Y2OGQ4OTQ5M2NkMWYyZjBjMzI2YWU3MDhjMmJlODFkNzE2ZTdiZTRjMzNlZDQwOTdlYzc0YTcyMmU4XCIsXCJ0aW1lc3RhbXBcIjoxNTg4MTY3NTk4NzI2LFwidHJhbnNhY3Rpb25zXCI6W3tcImhhc2hcIjpcImEyZDUyMjk3MjM1YTE2MTUyMTY0N2QwNDA3MDk4ZGY5NTFhNjM4ODI2YjdhYTViMDU4MDcyNDg5NWM3MGM1MjNcIixcInB1YmxpY0tleVwiOlwiYVNxOURzTk52R2hZeFl5cUE5d2QyZWR1RUFaNUFYV2dKVGJURkR3UmQ4cWF1QUxHeVZReHVUZXljeFlZZHdvMnpmSEYxSDhTZVI3R1lTNjV5c3BKQ0p3Q1RyRTFQVWhmdnJTRTE4Q2N2V1JmUTc5NVR0M2NiNmttQ3hUS1wiLFwic2NyaXB0Qnl0ZXNcIjpcIlpHWjJaQT09XCIsXCJzY3JpcHRTdHJpbmdcIjpcImRmdmRcIixcInRpbWVzdGFtcFwiOjE1ODgxNjc1ODkxOTJ9XX0iLCJ0eXBlIjoxfQ==, eyJkYXRhIjoie1wiZGlmZmljdWx0eVRhcmdldFwiOjMsXCJoYXNoXCI6XCIwMDBhM2Y2OGQ4OTQ5M2NkMWYyZjBjMzI2YWU3MDhjMmJlODFkNzE2ZTdiZTRjMzNlZDQwOTdlYzc0YTcyMmU4XCIsXCJoZWlnaHRcIjoxLFwibWVya2xlUm9vdFwiOlwiYWFkMGY5MzkzOTI2M2I4OTg4NzJlZDljOGJkYjljZWFlNjE2YzQwNzY0Y2E5ODg1OGU0MmJjOTgzOGM5MzhiZlwiLFwibm9uY2VcIjo3NjU0MixcInByZXZCbG9ja0hhc2hcIjpcIjAyOGExYjM1N2ZkYTdlMjBlNmYyOWQ0ODA2ZmQ2YjliNmM4NDU5MDE5OTU4MzE1MTMzZGU2YmJkMWE1MTg3NTdcIixcInRpbWVzdGFtcFwiOjE1ODgxNjc1MDY0MzEsXCJ0cmFuc2FjdGlvbnNcIjpbe1wiaGFzaFwiOlwiZWNkNzMyNDIyMjAxNzQ3Mzc2OTBiMDVlMDc4MmIzZGQ4NzI5YTczN2QwMGU0OWFhNjAzNmY5NmY0NTg0ZTcxYVwiLFwicHVibGljS2V5XCI6XCJhU3E5RHNOTnZHaFl4WXlxQTl3ZDJlZHVFQVo1QVhXZ0pUYlRGRHdSZDhxYXVBTEd5VlF4dVRleWN4WVlkd28yemZIRjFIOFNlUjdHWVM2NXlzcEpDSndDVHJFMVBVaGZ2clNFMThDY3ZXUmZRNzk1VHQzY2I2a21DeFRLXCIsXCJzY3JpcHRCeXRlc1wiOlwiTlRGa2MyWnpaQT09XCIsXCJzY3JpcHRTdHJpbmdcIjpcIjUxZHNmc2RcIixcInRpbWVzdGFtcFwiOjE1ODgxNjc1MDQxODR9XX0iLCJ0eXBlIjoxfQ==, eyJkYXRhIjoie1wiZGlmZmljdWx0eVRhcmdldFwiOjMsXCJoYXNoXCI6XCIwMDAwZDZmYmI0N2RhZmU2NWI2NTgzN2FiNmFjMGVmODVhYmE2YTNhYWQ3NjBkM2MwZTk5ODc0MjU4NzExOTlhXCIsXCJoZWlnaHRcIjozLFwibWVya2xlUm9vdFwiOlwiOTQyMjMyNmVkNTJiYTVjNjgzYTFlN2FjNjY0ZTE5MTdmMmQ5ZDcxMTgwYWMzMDE4YTFmMWQzZmQ1OGU1NzE1M1wiLFwibm9uY2VcIjoxNzc0MjMsXCJwcmV2QmxvY2tIYXNoXCI6XCIwMDBjMjExMDYyYzZmMTVjNTAxZTE4ZjgzYTBhYjg2ZmIwZWU0NGU5MGJhNjZlZWFiMzVlMmZjYTdiNGVmZDkyXCIsXCJ0aW1lc3RhbXBcIjoxNTg4MTY3ODgwNDMyLFwidHJhbnNhY3Rpb25zXCI6W3tcImhhc2hcIjpcIjUzNzI4MTg5Y2RjMTYwZTg0Njg0YmI2YzRmZmUyNDgxMTU1NzdjYWQyMmQwNWU2ZWYzNTIzZTY2ZjM2ZGI3NTRcIixcInB1YmxpY0tleVwiOlwiYVNxOURzTk52R2hZeFl5cUE5d2QyZWR1RUFaNUFYV2dKVGJURkR3UmQ4cWF1QUxHeVZReHVUZXljeFlZZHdvMnpmSEYxSDhTZVI3R1lTNjV5c3BKQ0p3Q1RyRTFQVWhmdnJTRTE4Q2N2V1JmUTc5NVR0M2NiNmttQ3hUS1wiLFwic2NyaXB0Qnl0ZXNcIjpcImJtdHFibUpyYWc9PVwiLFwic2NyaXB0U3RyaW5nXCI6XCJua2puYmtqXCIsXCJ0aW1lc3RhbXBcIjoxNTg4MTY3ODc3MTA0fV19IiwidHlwZSI6MX0=";

        System.out.println(Base64.decodeStr(base64Value));

        HeartBeat heartBeat = decodePublishNewBlockBase64(base64Value);
        System.out.println("type="+heartBeat.type);
        System.out.println("message="+heartBeat.data);

        Block block = Block.getBlockFromJson(heartBeat.data);
        block.transactionBoard();

    }
}
