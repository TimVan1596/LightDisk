package com.timvanx.blockchain;

/**
 * <h3>BlockChain</h3>
 * <p>LightDisk的测试类</p>
 *
 * @author : TimVan
 * @date : 2020-04-21 10:41
 **/
public class Test {
    public static void main(String[] args) {
//        ECKey key = new ECKey();
//        System.out.format("私钥 => %s\n", key.getPrivateKeyAsHex());
//        System.out.format("公钥 => %s\n", key.getPublicKeyAsHex());
//        System.out.format("地址 => %s\n", key.toString());
            String str = "[sdfsfdgdf,胜多负少的]";
            int index= str.indexOf(',');
            if(index > 0){
                 str = str.substring(1,index);
            }
            else{
                str = str.substring(1,str.length()-1);
            }

        System.out.println("str = "+str);


    }

}
