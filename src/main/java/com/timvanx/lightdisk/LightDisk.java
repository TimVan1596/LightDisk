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
import org.apache.com.timvanx.gossip.LocalMember;

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
     * uri=本机的URI
     * seedNode = 种子节点的URI
     */
    @Getter
    NodeURI uri;
    @Getter
    NodeURI seedNode;

    /**
     * 心跳消息列表
     */
    @Getter
    List<HeartBeatLog> heartBeatLogs = new ArrayList<>();
    /**
     * 未打包交易列表
     * 1、在Thread线程中进行检验交易是否合法（交易结构合法，交易是否已被打包）
     * 2、如果交易合法则进入 unPackTransactionList
     * 3、在挖矿时逐个更新是否交易已被打包
     * 4、已被打包则退出 unPackTransactionList
     */
    @Getter
    List<Transaction> unPackTransactionList = new ArrayList<>();

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
        //配置初始化
        gossip = GossipCommunicateLayer
                .getGossipExecute(uri, seedNode);
        blockChain = new BlockChain();
        heartBeatID = 0;
        this.uri = uri;
        this.seedNode = seedNode;

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
                        System.out.println("收到" + diff + "条新消息,id="
                                + gossipHeartBeatID);
                        System.out.println("最新gossip上heartBeatID="
                                + gossipHeartBeatID);
                        System.out.println("本地heartBeatID=" + heartBeatID);
                        for (int i = 1; i <= diff; i++) {
                            heartBeatID++;
                            System.out.println("------消息ID=" + heartBeatID);
                            HeartBeat heartBeat =
                                    getHeartBeatFromID(heartBeatID);
                            heartBeatLogs.add(new HeartBeatLog(
                                    heartBeat.getType(), heartBeatID,
                                    time + "", heartBeat.getData()));
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
        /*
         * PUBLISH_NEW_BLOCK_TYPE = 1-发布新区块-种类
         * REQUEST_BLOCK_TYPE = 2-请求某一区块-种类
         * REQUEST_CHAIN_TYPE = 3-请求整个链的信息-种类
         * PUBLISH_NEW_TRANSACTION_TYPE = 4-发布新交易-种类
         * NORMAL_TYPE = 9-普通信息-种类
         * WRONG_TYPE = 0-发生错误
         */
        //发布新区块
        if (type == HeartBeat.PUBLISH_NEW_BLOCK_TYPE) {
            processNewBlockHeartBeatHandle(heartBeat);
        }
        //发布新交易
        else if(type == HeartBeat.PUBLISH_NEW_TRANSACTION_TYPE){
            processNewTransactionHeartBeatHandle(heartBeat);
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
     * 发布收到的新交易的心跳消息
     * PUBLISH_NEW_TRANSACTION_TYPE = 4-发布新交易-种类
     *
     * @param heartBeat 接收的心跳消息
     */
    private boolean processNewTransactionHeartBeatHandle(HeartBeat heartBeat){
        Transaction transaction = Transaction
                .getTransactionFromJson(heartBeat.getData());
        //验证交易数据结构是否合法
        boolean isLegalTransaction = verifyTransaction(transaction);
        //验证交易是否已被打包
        boolean isPackageTransaction = verifyTransactionPackage(transaction);
        if (isLegalTransaction &&(!isPackageTransaction)) {
            unPackTransactionList.add(transaction);
        }
        return isLegalTransaction &&(!isPackageTransaction);
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
            //获取当前Gossip上的心跳消息编号
            int heartBeatID = (int) (getGossipHeartBeatID() + 1);
            String base64 = HeartBeat.packPublishNewBlockBase64(heartBeatID, json);
            //向所有的节点发送这个消息
            sendAllNodeHeartMsg(heartBeatID, base64);
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
     * 验证交易数据结构是否合法
     *
     * @param transaction 待验证的交易
     */
    private boolean verifyTransaction(Transaction transaction) {
        boolean isLegalBlock = false;
        isLegalBlock = true;
        return isLegalBlock;
    }

    /**
     * 验证交易是否已被打包
     *
     * @param transaction 待验证的交易
     */
    private boolean verifyTransactionPackage(Transaction transaction) {
        boolean isLegalBlock = false;
//        isLegalBlock = true;
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

        //获取当前Gossip上的心跳消息编号
        int heartBeatID = (int) (getGossipHeartBeatID() + 1);
        String base64 = HeartBeat.packPublishNewBlockBase64(heartBeatID, json);
        //向所有的节点发送这个消息
        sendAllNodeHeartMsg(heartBeatID, base64);

        return block;
    }

    /**
     * 添加交易
     */
    public Transaction addTransaction(String publicKey, String data){

        //生成一个交易
        Transaction transaction = new Transaction(publicKey, data);
        String json = JSON.toJSONString(transaction);
        //获取当前Gossip上的心跳消息编号
        int heartBeatID = (int) (getGossipHeartBeatID() + 1);
        String base64 = HeartBeat.packPublishNewTransactionBase64(heartBeatID, json);
        //向所有的节点发送这个消息
        sendAllNodeHeartMsg(heartBeatID, base64);

        return null;
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
    public void sendAllNodeHeartMsg(int heartBeatID, String message) {
        //获取当前Gossip上的心跳消息编号
        long gossipHeartBeatNum = getGossipHeartBeatID();
        gossip.add(message, HEART_BEAT_KEY_PREFIX + heartBeatID);
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
     * heartBeatLog监控板
     */
    public void heartBeatLogBoard(boolean isOpenDetail) {
        System.out.println("-------------" +
                "heartBeatLogBoard监控板---------------");
        Date date = new Date();
        DateTime time = new DateTime(date);
        System.out.println("当前时间：" + time);
        System.out.println("最新gossip上heartBeatID="
                + getGossipHeartBeatID());
        System.out.println("本地heartBeatID=" + heartBeatID);
        System.out.println("心跳消息情况：");
        int index = 0;
        for (HeartBeatLog heartBeatLog : getLocalHeartbeatList()) {
            index++;
            System.out.println("----------");
            System.out.println("序号:" + index);
            System.out.println("heartBeatID = "
                    + heartBeatLog.getHeartBeatID());
            System.out.println("type = " + heartBeatLog.getType());
            System.out.println("种类名= "
                    + heartBeatLog.getTypeString());
            System.out.println("日期 = " + heartBeatLog.getDate());
            if (isOpenDetail) {
                System.out.println("数据=" + heartBeatLog.getHeartBeatLogData());
            }
            System.out.println("----------");
        }

        System.out.println("-----------------" +
                "heartBeatLogBoard监控结束-------------------------");
    }

    /**
     * 成员节点列表监视器CMD
     */
    public void membersBoardCMD() {
        this.getGossip().liveDeadBoardCMD();
    }

    /**
     * 获取存活节点成员列表
     *
     * @param page  当前页
     * @param limit 每页显示的条数
     */
    public List<LocalMember> getLiveMemberList(int page, int limit) {

        List<LocalMember> members = this.getGossip().getLiveMembers();
        //倒序分页
        return PageUtil.startReversePage(members, page, limit);
    }

    /**
     * 获取死亡节点成员列表
     *
     * @param page  当前页
     * @param limit 每页显示的条数
     */
    public List<LocalMember> getDeadMember(int page, int limit) {

        List<LocalMember> members = this.getGossip().getDeadMembers();
        //倒序分页
        return PageUtil.startReversePage(members, page, limit);
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
     * 获得存活结点总个数
     */
    public int getLiveMemberListSize() {
        return getGossip().getLiveMembers().size();
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


    /**
     * 获得CRDT数据结构列表
     *
     * @param page  当前页
     * @param limit 每页显示的条数
     */
    public List<Map<String, String>> getCrdt(int page, int limit) {

        return PageUtil.startReversePage(gossip.getCrdtList(), page, limit);
    }


    /**
     * 获得CRDT总个数
     */
    public int getCrdtListSize() {
        return gossip.getCrdtListSize();
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

