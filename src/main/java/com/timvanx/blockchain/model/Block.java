package com.timvanx.blockchain.model;

import cn.hutool.core.date.DateTime;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * <h3>BlockChain</h3>
 * <p>区块数据结构</p>
 *
 * @author : TimVan
 * @date : 2020-04-08 15:22
 **/
public class Block {

    /**
     * 协议格式的一部分定义字段。
     * version = 区块版本号
     * prevBlockHash = 前一个区块的Hash值
     * height = 区块高度
     * merkleRoot = Merkle Tree的根Hash值
     * time = 时间戳(若无交易以实例化时间为准，若有交易以最后一个打包的交易为准)
     * difficultyTarget = 难度系数 "nBits"
     * nonce = 随机数
     * hash = 本区块的块头hash(注意本区块的Hash值计算，不包括hash和transactions字段)
     * transactions = 存储所有的交易信息
     */
    @Getter
    @Setter
    private String prevBlockHash;
    @Getter
    private long height;
    @Getter
    private String merkleRoot;
    @Getter
    private long timestamp;
    @Getter
    @Setter
    private long nonce;
    @Getter
    private String hash;
    @Getter
    @Setter
    private List<Transaction> transactions = new ArrayList<>();
    @Getter
    @Setter
    private long difficultyTarget = 3;
    //private long version;

    /**
     * 常量
     * BLOCK_HEADER_NUM = 块头包含的字段（由此取哈希）
     */
    private final static int BLOCK_HEADER_NUM = 6;


    public Block(String prevBlockHash, long height) {
        this.prevBlockHash = prevBlockHash;
        this.height = height;
        this.merkleRoot = "0";
        this.timestamp = System.currentTimeMillis();
        this.nonce = 0L;
        generatorHash();
    }

    public Block(String prevBlockHash, long height, String merkleRoot, long timestamp, long nonce, String hash, List<Transaction> transactions, long difficultyTarget) {
        this.prevBlockHash = prevBlockHash;
        this.height = height;
        this.merkleRoot = merkleRoot;
        this.timestamp = timestamp;
        this.nonce = nonce;
        this.hash = hash;
        this.transactions = transactions;
        this.difficultyTarget = difficultyTarget;
    }


    /**
     * 获得Hash值
     */
    private void generatorHash() {
        //生成块头
        Map<String, Object> blockHeader = new HashMap<>(BLOCK_HEADER_NUM);
        blockHeader.put("prevBlockHash", this.prevBlockHash);
        blockHeader.put("merkleRoot", this.merkleRoot);
        blockHeader.put("time", this.timestamp);
        blockHeader.put("nonce", this.nonce);
        blockHeader.put("difficultyTarget", this.difficultyTarget);
        blockHeader.put("height", this.height);
        Digester sha256 = new Digester(DigestAlgorithm.SHA256);
        this.hash = sha256.digestHex(JSON.toJSONString(blockHeader));
    }

    public void addTransaction(Transaction tx) {
        transactions.add(tx);
        this.timestamp = System.currentTimeMillis();
        this.merkleRoot = MerkleTree.getRootHash(transactions);
        generatorHash();
    }

    public void addTransaction(String publickey, String str) {
        transactions.add(new Transaction(publickey, str));
        this.timestamp = System.currentTimeMillis();
        this.merkleRoot = MerkleTree.getRootHash(transactions);
        generatorHash();
    }

    /**
     * 取得铸币交易
     *
     * @return 返回铸币交易
     */
    private Transaction getCoinbaseTx() {
        return transactions.get(0);
    }

    /**
     * 交易的监控板
     */
    public void transactionBoard() {
        System.out.println("--交易监控板----");

        for (Transaction transaction : transactions) {
            System.out.println("----");
            DateTime blockTime = new DateTime(transaction.getTimestamp());
            System.out.println("生成时间戳:" + transaction.getTimestamp() + "(" + blockTime + ")");
            System.out.println("交易hash:" + transaction.getHash());
            System.out.println("收款人:" + transaction.getPublicKey());
            System.out.println("script字符串:" + transaction.getScriptString());
            System.out.println("----");
        }

        System.out.println("-------");
    }

    /**
     * 搜索交易的监控板
     */
    public static void transactionSearchBoard(List<Transaction> transactions) {
        System.out.println("--交易监控板----");

        for (Transaction transaction : transactions) {
            System.out.println("----");
            DateTime blockTime = new DateTime(transaction.getTimestamp());
            System.out.println("生成时间戳:" + transaction.getTimestamp() + "(" + blockTime + ")");
            System.out.println("交易hash:" + transaction.getHash());
            System.out.println("收款人:" + transaction.getPublicKey());
            System.out.println("script字符串:" + transaction.getScriptString());
            System.out.println("----");
        }

        System.out.println("-------");
    }

    /**
     * 从JSON数据获得Block
     *
     * @return 返回JSON生成的block
     */
    public static Block getBlockFromJson(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        Block block = JSON.parseObject(json, Block.class);

        List<Transaction> txList = JSONObject.parseArray(jsonObject
                .getString("transactions"), Transaction.class);
        block.setTransactions(txList);



        return block;
    }

    public static void main(String[] args) {
//        String publickey1 = "aSq9DsNNvGhYxYyqA9wd2e" +
////                "duEAZ5AXWgJTbTKRS3hMqtXzj6gpne4s5RBEbRNN7yk3g1qs3j4PE7tJyh8RGg8" +
////                "GpFyEqq57ciEB6jndDaFEmjKZt8WFmQBmKF4wM8";
////        String str1 = "我是第一条信息";
////
////        Block block = new Block("0", 0);
////        block.addTransaction(publickey1, str1);
////        String str3 = " success(res) {  console.log(res); let msg = '';";
////        block.addTransaction(new Transaction(publickey1, str3));
////
////        System.out.println("block.generatorHash()=" + block.getHash());
        String blockStr = "{\"difficultyTarget\":3,\"hash\":\"5573b2bcd7b62fcfbcf60f835893205e84c5536a0a83ec3f75eccb1548bc1a00\",\"height\":0,\"merkleRoot\":\"0\",\"nonce\":0,\"prevBlockHash\":\"0\",\"timestamp\":1588252183502,\"transactions\":[]}";
        Block block =  Block.getBlockFromJson(blockStr);
        System.out.println("block.getHash="+block.getHash());
        System.out.println("block.getMerkleRoot="+block.getMerkleRoot());
    }

}
