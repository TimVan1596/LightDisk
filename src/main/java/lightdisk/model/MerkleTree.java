package lightdisk.model;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.Digester;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <h3>BlockChain</h3>
 * <p>默克尔树</p>
 *  使用自下而上的方法构建
 * @author : TimVan
 * @date : 2020-04-27 17:44
 **/
@Getter
public class MerkleTree {

    /**
     * 根节点
     */
    private Node root;
    /**
     * 叶子节点Hash
     */
    private byte[][] leafHashes;

    private MerkleTree(byte[][] leafHashes) {
        constructTree(leafHashes);
    }

    /**
     * @param txs 交易列表
     */
    public MerkleTree(List<Transaction> txs) {
        byte[][] leafHashes = new byte[txs.size()][];
        for (int i = 0; i < txs.size(); i++) {
            leafHashes[i] = DigestUtils.sha256(JSONObject.toJSONString(txs.get(i)));
        }
        constructTree(leafHashes);
    }

    /**
     * 从底部叶子节点开始往上构建整个Merkle Tree
     * 并改变根节点
     *
     * @param leafHashes 叶子节点
     */
    private void constructTree(byte[][] leafHashes) {
        if (leafHashes == null || leafHashes.length < 1) {
            throw new RuntimeException("ERROR:Fail to construct merkle tree ! leafHashes data invalid ! ");
        }
        this.leafHashes = leafHashes;
        //1、构建最底部的Node列表
        List<Node> parents = constructBottomLevel(leafHashes);
        while (parents.size() > 1) {
            parents = constructInternalLevel(parents);
        }
        root = parents.get(0);
    }

    /**
     * 构建一个层级节点
     *
     * @param children 第n+1层（更靠近叶子节点）的孩子节点列表
     * @return 第n层（上一层）的孩子节点列表
     */
    private List<Node> constructInternalLevel(List<Node> children) {
        List<Node> parents = Lists.newArrayListWithCapacity(children.size() / 2);
        for (int i = 0; i < children.size() - 1; i += 2) {
            Node child1 = children.get(i);
            Node child2 = children.get(i + 1);

            Node parent = constructInternalNode(child1, child2);
            parents.add(parent);
        }

        // 内部节点奇数个，只对left节点进行计算
        if (children.size() % 2 != 0) {
            Node child = children.get(children.size() - 1);
            Node parent = constructInternalNode(child, null);
            parents.add(parent);
        }

        return parents;
    }

    /**
     * 底部节点构建
     *
     * @param hashes 最底层所有的hash数组
     * @return 最底层的Node列表
     */
    private List<Node> constructBottomLevel(byte[][] hashes) {
        List<Node> parents = Lists.newArrayListWithCapacity(hashes.length / 2);

        for (int i = 0; i < hashes.length - 1; i += 2) {
            Node leaf1 = constructLeafNode(hashes[i]);
            Node leaf2 = constructLeafNode(hashes[i + 1]);

            Node parent = constructInternalNode(leaf1, leaf2);
            parents.add(parent);
        }

        // 奇数个节点的情况，复制最后一个节点
        if (hashes.length % 2 != 0) {
            Node leaf = constructLeafNode(hashes[hashes.length - 1]);
            Node parent = constructInternalNode(leaf, leaf);
            parents.add(parent);
        }

        return parents;
    }

    /**
     * 构建叶子节点
     *
     * @param hash 叶子实体的hash
     * @return 叶子节点
     */
    private static Node constructLeafNode(byte[] hash) {
        Node leaf = new Node();
        leaf.hash = hash;
        return leaf;
    }

    /**
     * 构建内部节点
     *
     * @param leftChild 左节点
     * @param rightChild 右节点
     * @return 父节点
     */
    private Node constructInternalNode(Node leftChild, Node rightChild) {
        Node parent = new Node();
        //右节点为空
        if (rightChild == null) {
            parent.hash = leftChild.hash;
        } else {
            parent.hash = internalHash(leftChild.hash, rightChild.hash);
        }
        parent.left = leftChild;
        parent.right = rightChild;
        return parent;
    }

    /**
     * 计算内部（左右）节点Hash
     *
     * @param leftChildHash 左节点的hash
     * @param rightChildHash 右节点的hash
     * @return 父节点hash
     */
    private byte[] internalHash(byte[] leftChildHash, byte[] rightChildHash) {
        byte[] mergedBytes = ArrayUtil.addAll(leftChildHash, rightChildHash);
        return DigestUtils.sha256(mergedBytes);
    }

    /** 仅获取根Hash值 */
    public static String getRootHash( List<Transaction> txs) {
        //使用hutool封装的SHA256消息摘要
        Digester sha256 = new Digester(DigestAlgorithm.SHA256);
        return sha256.digestHex((new MerkleTree(txs).getRoot().getHash()));
    }

    /**
     * Merkle Tree节点
     */
    @Data
    public static class Node {
        private byte[] hash;
        private Node left;
        private Node right;
    }

    public static void main(String[] args) {
        List<Transaction> txs = new ArrayList<>();
        String publickey1 = "aSq9DsNNvGhYxYyqA9wd2e" +
                "duEAZ5AXWgJTbTKRS3hMqtXzj6gpne4s5RBEbRNN7yk3g1qs3j4PE7tJyh8RGg8" +
                "GpFyEqq57ciEB6jndDaFEmjKZt8WFmQBmKF4wM8";
        String str1 = "我是第一条信息";
        txs.add(new Transaction(publickey1, str1));

        String publickey2 = "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTFXBHpAqfeRZZcUmEHecNop6nEjsXQd38LVQ2BgAFUjNf2absZnLfiNf2W31ciTiQztCmabrJmjr8QyyP5CLdCTS5";
        String str2 = "-Hello：我是2条信息吗？:)";
        txs.add(new Transaction(publickey2, str2));


        String str3 = " success(res) {  console.log(res); let msg = '';";
        txs.add(new Transaction(publickey1, str3));

        String rootHash = Base64.encode((new MerkleTree(txs).getRoot().getHash()));
        System.out.println("rootHash = " + rootHash);
        System.out.println("rootHash = " + getRootHash(txs));

//        Transaction tx = new Transaction(publickey1, str3);
//        String jsonObject = JSONObject.toJSONString(tx);
//        ;
//        System.out.println("jsonObject=" + jsonObject);
//
//        Transaction newTx = JSONObject
//                .parseObject(jsonObject, Transaction.class);
//        System.out.println("newTx.getPublicKey()=" + newTx.getPublicKey());
//        System.out.println("newTx.getScriptString()=" + newTx.getScriptString());


//        String md5Hex1 = DigestUtil.sha256();

    }

}
