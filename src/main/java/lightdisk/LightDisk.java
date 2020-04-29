package lightdisk;

import blockchain.model.Block;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import gossip.GossipCommunicateLayer;
import gossip.model.NodeURI;
import blockchain.model.BlockChain;
import lombok.Getter;
import model.ResponseJson;

import java.util.Date;
import java.util.Scanner;

import static java.lang.Thread.sleep;

/**
 * <h3>BlockChain</h3>
 * <p>Gossip和区块链的整合</p>
 * <p>这是一个纯工具类</p>
 *
 * @author : TimVan
 * @date : 2020-04-29 11:54
 **/
public class LightDisk {
    @Getter
    GossipCommunicateLayer gossip = null;
    BlockChain blockChain = null;
    long heartBeatID;

    Thread listenThread = null;

    /**
     * HEART_BEAT_KEY_PREFIX = 心跳连接Key前缀，如：heartbeat-1001
     */
    private final static String HEART_BEAT_KEY_PREFIX = "heartbeat-";


    /**
     * 构造函数的初始化
     */
    public LightDisk(NodeURI uri, NodeURI seedNode) {
        gossip = GossipCommunicateLayer
                .getGossipExecute(uri, seedNode);
        blockChain = new BlockChain();
        heartBeatID = 0;

        listenThread = new Thread(() -> {
            while (true) {
                try {
                    long gossipHeartBeatID = getGossipHeartBeatID();
                    long diff = gossipHeartBeatID - heartBeatID;
                    System.out.println("最新gossip上heartBeatID=" + gossipHeartBeatID);
                    if (diff > 0) {
                        System.out.println("------------------listenThread监听器-------------------");
                        Date date = new Date();
                        DateTime time = new DateTime(date);
                        System.out.println("当前时间：" + time);
                        System.out.println("收到" + diff + "条新消息,id=" + gossipHeartBeatID);
                        System.out.println("最新gossip上heartBeatID=" + gossipHeartBeatID);
                        System.out.println("本地heartBeatID=" + heartBeatID);
                        for (int i = 1; i <= diff; i++) {
                            heartBeatID++;
                            System.out.println("------消息ID=" + heartBeatID);
                            HeartBeat heartBeat = getHeartBeatFromID(heartBeatID);

//                            System.out.println("type=" + heartBeat.getType());
//                            System.out.println("message=" + heartBeat.getData());
                        }
                        System.out.println("------------------监听结束-------------------");

                    }

                    sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        listenThread.start();


    }

    /**
     * 获取本地区块链高度
     */
    public long getLocalChainHeight() {
        return blockChain.getCurrentHeight();
    }

    /**
     * 获取gossip上区块链高度
     */
    public long getGossipChainHeight() {
        String value = gossip.getAcc("height");
        if (!StrUtil.hasEmpty(value)) {
            return Long.parseLong(value);
        } else {
            return 0;
        }
    }

    /**
     * 挖矿
     */
    public void mineBlock(String publicKey, String data) {
        Block block = blockChain.mineBlock(publicKey, data);
        String json = JSON.toJSONString(block);
        String base64 = HeartBeat.packPublishNewBlockBase64(json);
        //向所有的节点发送这个消息
        sendAllNodeHeartMsg(base64);
    }

    /**
     * 将本地数据与Gossip上数据进行同步
     */
    private void syncGossip() {

    }

    /**
     * 向所有的节点发送心跳消息
     *
     * @param message 待发送的信息(base64压缩过的HeartBeat实体JSON)
     */
    public void sendAllNodeHeartMsg(String message) {
        //获取当前Gossip上的心跳消息编号
        long gossipHeartBeatNum = getGossipHeartBeatID();
        gossip.add(message, HEART_BEAT_KEY_PREFIX + (gossipHeartBeatNum + 1));
        gossip.accAdd("1", "heartbeat");
    }

    /**
     * 从ID中获取心跳数据
     *
     * @param heartBeatID 待发送的信息(base64压缩过的HeartBeat实体JSON)
     * @return 返回心跳数据包
     */
    public HeartBeat getHeartBeatFromID(long heartBeatID) {
        ResponseJson ret = gossip.get(HEART_BEAT_KEY_PREFIX + (heartBeatID));
        if (ret.getCode() == 0) {
            String retData = ret.getData();
            //TODO:如有逗号，按逗号分割取第一个(CRDT中键名相同重合的问题)
            int index = retData.indexOf(',');
            if (index > 0) {
                retData = retData.substring(1, index);
            } else {
                retData = retData.substring(1, retData.length() - 1);
            }
            return HeartBeat.decodePublishNewBlockBase64(retData);
        } else {
            return HeartBeat.packWrongHeartBeat("无此键值");
        }

    }

    /**
     * 获取gossip上心跳消息编号
     */
    private long getGossipHeartBeatID() {
        String value = gossip.getAcc("heartbeat");
        if (!StrUtil.hasEmpty(value)) {
            return Long.parseLong(value);
        } else {
            return 0;
        }
    }

    public static void main(String[] args) {
        NodeURI seedNode = new NodeURI("udp://localhost:5400", "0");

        LightDisk lightDisk = new LightDisk(seedNode, seedNode);

        System.out.println("请输入你想要的结果");

        Scanner scanner = new Scanner(System.in);
        int optionNum = scanner.nextInt();

        System.out.println("-------------- optionNum=" + optionNum);

    }
}

