package lightdisk.model;

/**
 * <h3>BlockChain</h3>
 * <p>区块数据结构</p>
 *
 * @author : TimVan
 * @date : 2020-04-08 15:22
 **/
public class Block {

    /**协议格式的一部分定义字段。
     * version = 区块版本号
     * prevBlockHash = 前一个区块的Hash值
     * merkleRoot = Merkle Tree的根Hash值
     * time = 时间戳
     * difficultyTarget = 难度系数 "nBits"
     * nonce = 随机数
     * hash = 本区块的块头hash
     * */
//    private long version;

    private Sha256Hash prevBlockHash;
    private Sha256Hash merkleRoot;
//    private long time;
//    private long difficultyTarget;

    private long nonce;
    private Sha256Hash hash;

    public static void main(String[] args) {

    }
}
