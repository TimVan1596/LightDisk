package lightdisk.model;

import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * <h3>BlockChain</h3>
 * <p>区块数据结构</p>
 *
 * @author : TimVan
 * @date : 2020-04-08 15:22
 **/
@Getter
public class Block {

    /**协议格式的一部分定义字段。
     * version = 区块版本号
     * prevBlockHash = 前一个区块的Hash值
     * merkleRoot = Merkle Tree的根Hash值
     * time = 时间戳(若无交易以实例化时间为准，若有交易以最后一个打包的交易为准)
     * difficultyTarget = 难度系数 "nBits"
     * nonce = 随机数
     * hash = 本区块的块头hash
     * transactions = 存储所有的交易信息
     * */
//    private long version;
    private String prevBlockHash;
    private String merkleRoot;
    private long time;
//    private long difficultyTarget;
    private long nonce;
    private String hash;
    private List<Transaction> transactions;

    public Block() {

        this.time = System.currentTimeMillis();
    }

    /** 获得Hash值*/
    public String getHash() {

        return "hello";
    }

    public void addTransaction(Transaction tx) {
        transactions.add(tx);
        this.time = System.currentTimeMillis();
    }

    public static void main(String[] args) {
        Block block = new Block();

    }

}
