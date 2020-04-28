package lightdisk.model;

import cn.hutool.core.date.DateTime;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <h3>BlockChain</h3>
 * <p>区块链实体</p>
 *
 * @author : TimVan
 * @date : 2020-04-28 12:44
 **/
@Getter
public class BlockChain {

    /**
     * blockList = 区块集合
     * currentHeight = 区块链的当前高度
     */
    private final List<Block> blockList = new ArrayList<>();
    private long currentHeight = -1;

    /**
     * 添加新区块
     *
     * @param block 向此链中添加的新区块
     */
    private void addBlock(Block block) {
        if (block.getHeight() == currentHeight + 1) {
            blockList.add(block);
            currentHeight++;
        } else {
            System.out.println("in BlockChain.addBlock 新添加区块高度有误！");
        }
    }

    /**
     * 获得最后一个区块
     *
     * @return 返回区块列表中的最后一个区块
     */
    public Block getLastBlock() {
        return blockList.get(blockList.size() - 1);
    }

    /**
     * 生成创世区块
     *
     * @return 返回创世区块
     */
    private Block generatorGenesisBlock() {
        return new Block("0", 0L);
    }

    /**
     * 挖矿
     *
     * @param publicKey 挖矿者公钥字符串
     * @param data      矿工待存入数据
     */
    public void mineBlock(String publicKey, String data) {
        //如果链为空，默认放置创世块
        if (currentHeight == -1) {
            addBlock(generatorGenesisBlock());
        }

        Block block = null;
        long nonce = 0;
        //生成coinbase交易
        Transaction coinbase = new Transaction(publicKey, data);
        do {
            //生成待提交区块
            block = new Block(getLastBlock().getHash(), currentHeight + 1);
            //打包coinbase交易
            block.addTransaction(coinbase);
            //测试block
            block.setNonce(nonce);
            nonce++;
        }while (!block.getHash().startsWith("000"));

        if(block.getHash().startsWith("000")){
            System.out.println("经过"+nonce+"个nonce，挖出区块");
            addBlock(block);
        }


    }

    /**
     * 区块链的监控板
     */
    public void lightBoard() {
        System.out.println("-------------LightDisk监控板------------");
        Date date = new Date();
        DateTime time = new DateTime(date);
        System.out.println("当前时间：" + time);
        System.out.println("当前高度：" + this.getCurrentHeight());
        System.out.println("区块详细信息：");

        for (Block block : blockList) {
            System.out.println("------");
            System.out.println("区块高度:" + block.getHeight());
            DateTime blockTime = new DateTime(block.getTimestamp());
            System.out.println("生成时间戳:" + block.getTimestamp() + "(" + blockTime + ")");
            System.out.println("前一个区块的Hash值:" + block.getPrevBlockHash());
            System.out.println("本区块的块头Hash值:" + block.getHash());
            System.out.println("Merkle Tree的Hash值:" + block.getMerkleRoot());
            System.out.println("交易数:" + block.getTransactions().size());

        }

        System.out.println("----------------------------------------");


    }

    public static void main(String[] args) {

        BlockChain LightDisk = new BlockChain();

        String publickey1 = "aSq9DsNNvGhYxYyqA9wd2e" +
                "duEAZ5AXWgJTbTKRS3hMqtXzj6gpne4s5RBEbRNN7yk3g1qs3j4PE7tJyh8RGg8" +
                "GpFyEqq57ciEB6jndDaFEmjKZt8WFmQBmKF4wM8";

        for (int i = 0; i < 3; i++) {
            LightDisk.mineBlock(publickey1, "这是第" + i + "次挖矿的结果");

//            try {
//                //程序暂停1000 毫秒，也就是1秒.
//                Thread.sleep(1000);
//            } catch (InterruptedException ex) {
//                Thread.currentThread().interrupt();
//            }
        }

        LightDisk.lightBoard();

    }
}
