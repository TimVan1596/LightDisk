package lightdisk.model;

import java.util.ArrayList;

/**
 * <h3>BlockChain</h3>
 * <p>交易</p>
 *
 * @author : TimVan
 * @date : 2020-04-08 16:10
 **/
public class Transaction {
    /**
     * version = 交易版本号
     * scriptBytes = 交易的script数据
     * */
    private long version;
    private byte[] scriptBytes;

}
