package lightdisk.model;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
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
     * publicKey = 发起交易者地址
     * scriptBytes = 交易的script数据
     * */
//    private long version;
    private String publicKey;
    private byte[] scriptBytes;

    /** @param publicKey 公钥
     *  @param scriptBytes 需要存储交易数据（字节数组）
     * */
    public Transaction(String publicKey, byte[] scriptBytes) {
        this.publicKey = publicKey;
        this.scriptBytes = scriptBytes;
    }

    /** @param publicKey 公钥
     *  @param scriptStr 需要存储交易数据（字符串）
     * */
    public Transaction(String publicKey, String scriptStr) {
        this.publicKey = publicKey;
        this.scriptBytes = StrUtil.bytes(scriptStr);
    }

    /** 返回交易的script数据(scriptBytes)的字符串消息
     * */
    public String getScriptString(){
        return new String( this.getScriptBytes());
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
