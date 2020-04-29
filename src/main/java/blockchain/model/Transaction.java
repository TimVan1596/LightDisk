package blockchain.model;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.alibaba.fastjson.JSON;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
/**
 * <h3>BlockChain</h3>
 * <p>交易</p>
 *
 * @author : TimVan
 * @date : 2020-04-08 16:10
 **/
@Getter
public class Transaction {
    /**
     * version = 交易版本号
     * hash = 交易哈希()
     * publicKey = 发起交易者地址
     * scriptBytes = 交易的script数据
     * recieveTime = 交易被接收到的时间
     * */
//    private long version;
    private String publicKey;
    private byte[] scriptBytes;
    private long timestamp;
    private String hash;

    /**
     * 常量
     * TX_HEADER_NUM = 交易块头包含的字段（由此取哈希）
     *
     */
    private final static int TX_HEADER_NUM = 3;


    /** @param publicKey 公钥
     *  @param scriptBytes 需要存储交易数据（字节数组）
     * */
    public Transaction(String publicKey, byte[] scriptBytes) {
        this.publicKey = publicKey;
        this.scriptBytes = scriptBytes;
        this.timestamp = System.currentTimeMillis();
        generatorHash();
    }

    /** @param publicKey 公钥
     *  @param scriptStr 需要存储交易数据（字符串）
     * */
    public Transaction(String publicKey, String scriptStr) {
        this.publicKey = publicKey;
        this.scriptBytes = StrUtil.bytes(scriptStr);
        this.timestamp = System.currentTimeMillis();
        generatorHash();
    }

    /** 返回交易的script数据(scriptBytes)的字符串消息
     * */
    public String getScriptString(){
        return new String( this.getScriptBytes());
    }

    /**
     * 获得Hash值
     */
    private void generatorHash() {
        //生成本区块的Hash
        Map<String, Object> txHeader = new HashMap<>(TX_HEADER_NUM);
        txHeader.put("publicKey", this.publicKey);
        txHeader.put("scriptBytes", this.scriptBytes);
        txHeader.put("time", this.timestamp);
        Digester sha256 = new Digester(DigestAlgorithm.SHA256);
        this.hash = sha256.digestHex(JSON.toJSONString(txHeader));
    }

    public static void main(String[] args) {
        Map<String,String> map = ECKey.genKeyPair();
        String publickey = map.get("publickey");

        String str = "Hello World";
        Transaction tx = new Transaction(publickey,str);

        System.out.println("tx.getPublicKey()="+tx.getPublicKey());
        System.out.println("tx.getScriptString()="+tx.getScriptString());
    }

}
