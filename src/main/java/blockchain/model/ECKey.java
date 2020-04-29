package blockchain.model;

import io.github.novacrypto.base58.Base58;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * <h3>BlockChain</h3>
 * <p>公钥、秘钥与钱包地址(椭圆曲线加密)</p>
 *
 * @author : TimVan
 * @date : 2020-04-21 12:03
 **/
public class ECKey {

    static {
        //使用 Bouncey Castle 加密包
        try {
            Security.addProvider(new BouncyCastleProvider());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * KEY_ALGORITHM = 非对称密钥算法采用EC(Elliptic Curves)
     * SIGN_ALGORITHM = 签名所使用的算法
     */
    public static final String KEY_ALGORITHM = "EC";
    public static final String SIGN_ALGORITHM = "SHA1withECDSA";

    /**
     * 私钥签名
     * @param src 签名数据源
     * @param privateKeyStr 私钥字符串
     * @return byte[] 加密数据
     */
    public static byte[] signByPrivateKey(byte[] src, String privateKeyStr) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec =
                new PKCS8EncodedKeySpec(decodeBASE64(privateKeyStr));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(src);
        return signature.sign();
    }


    /**
     * 公钥验证
     * @param dest 签名结果
     * @param src 签名数据源
     * @param publicKeyStr 公钥字符串
     * @return boolean 验证结果
     */
    public static boolean verifyByPublicKey(byte[] dest,byte[] src
            , String publicKeyStr) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec =
                new X509EncodedKeySpec(decodeBASE64(publicKeyStr));
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Signature signature = Signature.getInstance("SHA1withECDSA");
        signature.initVerify(publicKey);
        signature.update(src);
        return signature.verify(dest);
    }


    /**
     * 公钥加密
     *
     * @param data 待加密数据
     * @param publicKeyStr 公钥字符串
     * @return byte[] 加密数据
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKeyStr) throws Exception {

        //实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        //初始化公钥
        //密钥材料转换
        X509EncodedKeySpec x509KeySpec =
                new X509EncodedKeySpec(decodeBASE64(publicKeyStr));
        //产生公钥
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        //数据加密
        //TODO:ECC算法在jdk1.5后加入支持，目前仅仅只能完成密钥的生成与解析。 如果想要获得ECC算法实现，需要调用硬件完成加密/解密（ECC算法相当耗费资源，如果单纯使用CPU进行加密/解密，效率低下）
        Cipher cipher = new NullCipher();
//        cipher = Cipher.getInstance(pubKey.getAlgorithm(),"BC");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(data);
    }


    /**
     * 私钥解密数据
     * @param data  待解密数据
     * @param privateKeyStr 密钥
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPrivateKey(byte[] data, String privateKeyStr) throws Exception {
        //取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec =
                new PKCS8EncodedKeySpec(decodeBASE64(privateKeyStr));
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //数据解密
        Cipher cipher = new NullCipher();
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);

    }

    /**
     * 生成公私钥对Map<>
     * @return Map的key为publickey和privatekey
     */
    public static Map<String, String> genKeyPair() {
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("keyPairGenerator静态工厂返回实例失败");
            e.printStackTrace();
            return null;
        }
        keyPairGenerator.initialize(256);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//        ECPublicKey ecPublicKey = (ECPublicKey) keyPair.getPublic();
//        ECPrivateKey ecPrivateKey = (ECPrivateKey) keyPair.getPrivate();

        Map<String, String> keyPairMap = new HashMap<>(2);
        keyPairMap.put("publickey", encodeBase58(keyPair.getPublic().getEncoded()));
        keyPairMap.put("privatekey", encodeBase58(keyPair.getPrivate().getEncoded()));

        return keyPairMap;
    }


    /**
     * 公私钥是否符合
     *
     * @param publicKeyStr 公钥字符串
     * @param privateKeyStr 私钥字符串
     * @return 公私钥是否符合
     */
    public static boolean isKeyMatch(String publicKeyStr,String privateKeyStr){
            boolean isMatch = false;
            String sign = "The Times 03/Jan/2009 Chancellor on brink" +
                    " of second bailout for banks.\n";
            //私钥签名
        try {
            byte[] dest = signByPrivateKey(sign.getBytes(),privateKeyStr);
            isMatch =  verifyByPublicKey(dest,sign.getBytes(),publicKeyStr);
        } catch (Exception e) {
            isMatch = false;
            e.printStackTrace();
        }
        return isMatch;
    }


    public static void main(String[] args) {
//        byte[] arr = new byte[32];
//        new Random(0).nextBytes(arr);
//        System.out.println(Arrays.toString(arr));
//        System.out.println("字节数组的长度="+arr.length);

        Map<String, String> keyPairMap = genKeyPair();
        assert keyPairMap != null;

        String publicKeyStr = keyPairMap.get("publickey");
        String privateKeyStr = keyPairMap.get("privatekey");

        //公钥加密，私钥解密
        String dataStr = "The Times 03/Jan/2009 Chancellor on brink of second bailout for banks.";
        try {
            byte[] encryptData = encryptByPublicKey(dataStr.getBytes()
                    , publicKeyStr);
            byte[] decryptData = decryptByPrivateKey(encryptData,privateKeyStr);
            System.out.println("decryptData =" + new String(decryptData));

        } catch (Exception e) {
            System.out.println("解码有误，datastr=" + dataStr);
            e.printStackTrace();
        }

        try {
            byte[] dest =  signByPrivateKey("hello".getBytes(),privateKeyStr);
            boolean res =  verifyByPublicKey(dest,"hello".getBytes(),publicKeyStr);
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println(keyPairMap.get("publickey"));
        System.out.println(keyPairMap.get("publickey").length());
        System.out.println(keyPairMap.get("privatekey"));
        System.out.println(keyPairMap.get("privatekey").length());


    }

    /**
     * Base58编码方式 用于产生公私钥和地址的字符串
     * 相比Base64，Base58不使用数字"0"，字母大写"O"，
     * 字母大写"I"，和字母小写"l"，以及"+"和"/"符号。
     */
    public static String encodeBase58(byte[] bytes) {
        return Base58.newInstance().encode(bytes);
    }

    public static byte[] decodeBASE64(String key) {
        return Base58.newInstance().decode(key);

    }
}
