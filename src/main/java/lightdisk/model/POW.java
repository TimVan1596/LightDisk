package lightdisk.model;

/**
 * <h3>BlockChain</h3>
 * <p>工作量证明</p>
 *
 * @author : TimVan
 * @date : 2020-04-28 17:56
 **/
public class POW {
    public static boolean checkHashByDifficultTarget(String hash,long target){
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < target; i++) {
            stringBuffer.append("0");
        }
        return hash.startsWith(stringBuffer.toString());
    }

}
