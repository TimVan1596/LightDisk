package lightdisk;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;

/**
 * <h3>BlockChain</h3>
 * <p>LightDisk的测试类</p>
 *
 * @author : TimVan
 * @date : 2020-04-21 10:41
 **/
public class Test {
    public static void main(String[] args) {
        ECKey key = new ECKey();
        System.out.format("私钥 => %s\n", key.getPrivateKeyAsHex());
        System.out.format("公钥 => %s\n", key.getPublicKeyAsHex());
        System.out.format("地址 => %s\n", key.toString());


        System.out.println("测试");
    }

}
