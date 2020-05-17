package com.timvanx.lightdisk;

import com.timvanx.blockchain.model.Block;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.timvanx.blockchain.model.Transaction;
import com.timvanx.blockchain.util.PageUtil;
import com.timvanx.gossip.GossipCommunicateLayer;
import com.timvanx.gossip.model.NodeURI;
import com.timvanx.blockchain.model.BlockChain;
import lombok.Getter;
import com.timvanx.model.ResponseJson;

import java.util.*;

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

    /**
     * 心跳消息列表
     */
    @Getter
    List<HeartBeatLog> heartBeatLogs = new ArrayList<>();

    /**
     * THREAD_EXIT = 退出线程标志位
     * volatile修饰符用来保证其它线程读取的总是该变量的最新的值
     */
    public volatile boolean THREAD_EXIT = false;
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
            while (!THREAD_EXIT) {
                try {
                    long gossipHeartBeatID = getGossipHeartBeatID();
                    long diff = gossipHeartBeatID - heartBeatID;
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
                            heartBeatLogs.add(new HeartBeatLog(heartBeat.getType()
                                    , heartBeatID, time + ""));
                            processHeartBeatSortHandle(heartBeat);
//                            System.out.println("type=" + heartBeat.getType());
//                            System.out.println("message=" + heartBeat.getData());
                        }
                        System.out.println("------------------listenThread监听结束-------------------");

                    }

                    sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        listenThread.start();


    }

    /**
     * 关闭LightDisk实例
     */
    public void shutDown() {
        //修改线程标志位为 true
        this.THREAD_EXIT = true;
        //关闭gossip
        gossip.close();
    }

    /**
     * 处理收到的心跳消息的分拣器
     *
     * @param heartBeat 接收的心跳消息
     */
    private void processHeartBeatSortHandle(HeartBeat heartBeat) {
        int type = heartBeat.getType();
        /*  心跳消息的类型
         * PUBLISH_NEW_BLOCK_TYPE = 1-发布新区块-种类
         * REQUEST_BLOCK_TYPE = 2-请求某一区块-种类
         * REQUEST_CHAIN_TYPE = 3-请求整个链的信息-种类
         * NORMAL_TYPE = 9-普通信息-种类
         * WRONG_TYPE = 0-发生错误
         */
        switch (type) {
            case HeartBeat.PUBLISH_NEW_BLOCK_TYPE: {
                processNewBlockHeartBeatHandle(heartBeat);
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * 处理收到的新区块的心跳消息
     * PUBLISH_NEW_BLOCK_TYPE = 1-发布新区块-种类
     *
     * @param heartBeat 接收的心跳消息
     */
    private boolean processNewBlockHeartBeatHandle(HeartBeat heartBeat) {
        Block block = Block.getBlockFromJson(heartBeat.getData());
        boolean isLegalBlock = verifyBlock(block);
        if (isLegalBlock) {
            blockChain.addBlock(block);
        }
        return isLegalBlock;
    }

    /**
     * 检查区块是否含创世块，若无创世块则挖出创世块并发布
     */
    private void verifyHasGenesisBlock() {
        //如果链为空，默认放置创世块
        if (blockChain.getCurrentHeight() == -1) {
            Block genesisBlock = BlockChain.generatorGenesisBlock();
            blockChain.addBlock(genesisBlock);
            String json = JSON.toJSONString(genesisBlock);
            String base64 = HeartBeat.packPublishNewBlockBase64(json);
            //向所有的节点发送这个消息
            sendAllNodeHeartMsg(base64);
        }
    }

    /**
     * 验证区块是否合法
     * PUBLISH_NEW_BLOCK_TYPE = 1-发布新区块-种类
     *
     * @param block 待验证的区块
     */
    private boolean verifyBlock(Block block) {
        boolean isLegalBlock = false;
        isLegalBlock = true;
        return isLegalBlock;
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
    public Block mineBlock(String publicKey, String data) {
        /* 检查是否有创世块 */
        verifyHasGenesisBlock();
        Block block = blockChain.mineBlock(publicKey, data);
        String json = JSON.toJSONString(block);
        String base64 = HeartBeat.packPublishNewBlockBase64(json);
        //向所有的节点发送这个消息
        sendAllNodeHeartMsg(base64);

        return block;
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

    /**
     * LightBoard监控板
     */
    public void lightBoard(boolean isOpenTX) {
        //是否打开详细交易信息
        blockChain.blockChainBoard(isOpenTX);
    }

    /**
     * 获取本地区块链上所有区块列表
     */
    public List<Block> getLocalBlockList() {
        return blockChain.getBlockList();
    }

    /**
     * 获取本地心跳消息列表
     */
    public List<HeartBeatLog> getLocalHeartbeatList() {
        return heartBeatLogs;
    }

    /**
     * 分页获取心跳消息列表
     *
     * @param page  当前页
     * @param limit 每页显示的条数
     */
    public List<HeartBeatLog> getLocalHeartbeatList(int page, int limit) {
//倒序分页
        return PageUtil.startReversePage(this.heartBeatLogs, page, limit);
    }

    /**
     * 分页获取本地区块链上区块列表
     *
     * @param page  当前页
     * @param limit 每页显示的条数
     */
    public List<Block> getLocalBlockList(int page, int limit) {
        return blockChain.getBlockListPage(page, limit);
    }

    /**
     * 获得区块链的长度
     */
    public int getBlockListSize() {
        return blockChain.getBlockList().size();
    }

    /**
     * 获得心跳消息的长度
     */
    public int getHeartbeatListSize() {
        return heartBeatLogs.size();
    }

    /**
     * 获得交易列表
     */
    public List<Transaction> getTransactionList() {
        List<Transaction> transactions = new ArrayList<>();
        for (Block block : getLocalBlockList()) {
            transactions.addAll(block.getTransactions());
        }
        return transactions;
    }

    /**
     * 分页获得交易列表
     *
     * @param page  当前页
     * @param limit 每页显示的条数
     */
    public Map<String, Object> getTransactionList(int page, int limit) {
        Map<String, Object> mjs = new LinkedHashMap<>();
        List<Transaction> transactions = new ArrayList<>();
        for (Block block : getLocalBlockList()) {
            transactions.addAll(block.getTransactions());
        }

        mjs.put("code", 0);
        mjs.put("msg", "成功");
        mjs.put("count", transactions.size());
        mjs.put("data", PageUtil.startReversePage(transactions, page, limit));

        return mjs;
    }

    /**
     * 根据公钥获得交易列表
     *
     * @param publicKey 公钥
     */
    public List<Transaction> getTransactionListByPublicKey(String publicKey) {
        List<Transaction> transactions = new ArrayList<>();
        publicKey = publicKey.trim();
        if (StrUtil.hasEmpty(publicKey)) {
            return transactions;
        } else {
            for (Block block : getLocalBlockList()) {
                for (Transaction transaction : block.getTransactions()) {
                    if (transaction.getPublicKey().equals(publicKey)) {
                        transactions.add(transaction);
                    }
                }
            }
        }
        return transactions;
    }

    /**
     * 根据公钥分页获得交易列表
     *
     * @param publicKey 公钥
     * @param page      当前页
     * @param limit     每页显示的条数
     */
    public Map<String, Object> getTransactionListByPublicKey(String publicKey
            , int page, int limit) {
        Map<String, Object> mjs = new LinkedHashMap<>();
        //交易列表
        List<Transaction> transactions = new ArrayList<>();
        publicKey = publicKey.trim();
        if (!StrUtil.hasEmpty(publicKey)) {
            for (Block block : getLocalBlockList()) {
                for (Transaction transaction : block.getTransactions()) {
                    if (StrUtil.contains(transaction.getPublicKey(), publicKey)) {
                        transactions.add(transaction);
                    }
                }
            }
        }
        mjs.put("code", 0);
        mjs.put("msg", "成功");
        mjs.put("count", transactions.size());
        mjs.put("data", PageUtil.startReversePage(transactions, page, limit));

        return mjs;
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

