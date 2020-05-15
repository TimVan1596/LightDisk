package com.timvanx.cmd;

import cn.hutool.core.date.DateTime;
import com.timvanx.gossip.model.NodeURI;
import com.timvanx.blockchain.model.ECKey;
import com.timvanx.lightdisk.LightDisk;


import java.util.Date;
import java.util.Map;
import java.util.Scanner;

/**
 * <h3>BlockChain</h3>
 * <p>命令行版本启动器</p>
 *
 * @author : TimVan
 * @date : 2020-04-28 20:46
 **/
public class Launcher {

    /**
     * 主页UI
     *
     * @param uri        网址
     * @param publicKey  公钥字符串
     * @param privateKey 私钥字符串
     */
    private static void indexUI(NodeURI uri, String publicKey, String privateKey) {
        if (!ECKey.isKeyMatch(publicKey, privateKey)) {
            System.out.println("验证失败");
            return;
        }
        System.out.println("登录成功");

        NodeURI seedNode = new NodeURI("udp://localhost:5400", "0");

        LightDisk lightDisk = new LightDisk(uri, seedNode);

        //是否终止循环
        boolean isBreakLoop = false;
        while (!isBreakLoop) {
            System.out.println("-------------------");
            System.out.println("网络ID:" + uri.getId() + "=>" + uri.getIpAddress());
            System.out.println("我的公钥:" + publicKey);
            System.out.println("\n\t 1、Gossip监控");
            System.out.println("\t 2、挖矿");
            System.out.println("\t 9、区块链监控");
            System.out.println("\t 0、退出登录");
            Scanner scanner = new Scanner(System.in);
            int optionNum = scanner.nextInt();

            switch (optionNum) {
                case 1: {
                    Date date = new Date();
                    DateTime time = new DateTime(date);
                    System.out.println("当前时间：" + time);
                    lightDisk.getGossip().gossipBoardCMD();
                    break;
                }
                case 2: {
                    System.out.println("请输出您需要存储的信息");
                    String data = scanner.next();
                    System.out.println("+++++挖矿中....");
                    lightDisk.mineBlock(publicKey,data);
                    System.out.println("+++++成功挖出");
                    System.out.println("最新区块高度为："+lightDisk.getLocalChainHeight());
                    break;
                }
                case 9: {
                    Date date = new Date();
                    DateTime time = new DateTime(date);
                    System.out.println("当前时间：" + time);

                    System.out.println("需要详细信息请输入1");
                    int data = scanner.nextInt();
                    //是否打开详细信息
                    boolean isOpenTX = false;
                    if (data == 1){
                        isOpenTX =true;
                    }
                    lightDisk.lightBoard(isOpenTX);
                    break;
                }
                default: {
                    isBreakLoop = true;
                    lightDisk.shutDown();
                    break;
                }
            }

        }

    }

    private static boolean loginUI() {
        //是否终止循环
        boolean isBreakLoop = false;

        System.out.println("-----------------------------------");
        System.out.println("1、启动种子节点");
        System.out.println("2、启动2号节点");
        System.out.println("3、启动3号节点");
        System.out.println("4、生成公私钥账号");
        System.out.println("5、错误公私钥测试");
        System.out.println("0、关闭界面");

        NodeURI uri = null;
        Scanner scanner = new Scanner(System.in);
        String publicKey = null;
        String privateKey = null;
        int optionNum = scanner.nextInt();
        switch (optionNum) {
            case 1: {
                uri = new NodeURI("udp://localhost:5400", "0");
                publicKey = "Vj75CuZgqYqhewfDfF9KQEdejjqbnoDiRjuXUnwD37fGGhG7DHdGK2DpmVUAZFteDRx8tkNWrsCy9nSTXeDrRUf7QHhf2DtMBpaSi9pu4ZeWpHc7TzRbWMUBiE94rTMJVzAJMfVM4MtfCES1Vn22ezZY7YiGhbEtVgjHotrtktBiNypaoQR9kWQzFxhdjhEKAsKK46aVC8hm1Q1NJ5bwFne4Ye3VA";
                privateKey = "6TAzA5ZnZvXfnkvfgG5EZF978HcKn5QHT26e3r9GXrp2J1GDG2s63NeupL1bkBpextYMb8oc8994aQ3dBMx9ymjFCNLhQK33wssFuGSoHe6vpG4QybbxGc6v7qTYpuCgx4VXW6tZHUbGkWCS9MTvfTtd26Mq4ZQw2HPahgvFAvzZk1ZozznmUz6UbabVhs47tPjSqj33XWGZhfN5qvhc6sRtS93jfzfyD52hcF7jCYDrhPZZ2D51tSsXWT6XJ98nnQb78FMvCtjwg1tXEE5AsBLhKRJqRJNREpoVLrkyvNB9XRDjhfXACyZ85FrtNjqkTmd1pUXQ1Va5bXKN7G2PctvR39Ju93tXcK86n3yiGjh5ajVpKUALj19QEM823DPYGrqdbuyBdzsuQzKEwswYi8kHaYZgGyBShwRM3cV95eMyLdFTc4ARzqZS6x5vF75MUSsTZiNuKMiNZLvoNUfb9Md7h6PEUZt1UKUSSTK2Q38V5rCrngpKmtu26Tr6ywmranTv2wktsKM4huRFggGqaVJCFKyGJwhWsCVbPHzQW8k8t9uj1dFr1aeZEbEQy6JPgu2ihddNavhu5Q7fKoRw4sroxmMNW2GxPP9FAFB1c98pmJ8eLSMMiGtd3eqKaE3i8W89sBhCt7BQhjtL5q5ma7cTWYZLUBTqMDMrS1xNjNGT9jkt1JoXMMJ3Vg5xEhpaaXC4HkYKtNJ9YBQoYqRJT71wi9LnJBrUBTuAB1enh3Zb4MNFdr8EDmQGmZbs662GmDucRRoLJE8ZPJBYKciynEEhsUVEfVCbyfdmZXsS5YCm3GbG3uqcRAaREKDiaeoJ4G4k8cvegpRGUuujqFoVVVhJPta1HS4PX1";
                break;
            }
            case 2: {
                uri = new NodeURI("udp://localhost:5402", "2");
                publicKey = "Vj75CuZgqYqhewfDfF9KQEdejjqbnoDiRjuXUnwEvrF1HRD5uJpP2Po124MaKFucrzq8rBFS9RdzcGiFn5Shb6YMKP98rqK5erQeKb92J7h8VpcyHs2PExxC9hQ6Mc43ocz7NTVtZagDMdR6zEWvYPQUJS1Gns2G98SwGQPUPzJtvfZBkobUrNLqFfk5y28Wekwfoe4BZBquGMAMtusG36DzbxaGY";
                privateKey = "2EdhbDGp6Ni449TvBMugXsBZKnr2Hq4gdg4NxnYhKDtPoMtqUidueUuQk88LzQ7SF4pL9UQ39ABESashwGy2mcHqvoTFyCeVHGg12D9ki2C9HgLLpBBPVanLJ8k2KM8aLSU5bzpJsfKnqRTmKn7VJbmPTjcikKpaT3SU32SFpLayWT5PFuEPHgPVHUPDoDFSvNk4TWsYBdtVz6Td7PmVymU2ZwdZononK8HDvCxGwUkx9GkD3pCEU3e9zMV2dURfX521rZiguKCBCSCwTuFDQcZLpkJBecTu3h2FUTA4RQzXBXmmTpmxqgZi6usW4qYPgLhWxYzBzkXKJQhcXg9EcDnLTpLenJNWABNnhLQgeLwN8LeYXv8uygVHyC7oETf7fPBQxPTKcH3neKvAUkSTaxpgWo7AdJNo3srwmdxe6NWyWEHiSykL3pg5gt1Qmh5UdPcLrT1WshxrkG1LrvjzXuPNy2DMaEvYrDYPGdV1dXZLxcXAQnd2FWZwcjpmJwJPZRKsCBsLvKPWNCMBpUWXGcrnsthtaR33pepSqCcy4zjkbHFkd4vGxdDwNiNhRCHMEoPZmwJmRZB7HG145D6tJQCzcbRwsoPF3cgPDA3WJKDLifwCm3JGTCqpQ12oH4g1kSztgSfJDSBjUG6ED5NKyff5PFNLRQYpPiBmR6GBjfcZjE3rq4REnmrT2YASZ3i3hnrZ1iWEPC31e3EXX6Qyfb3WAcj4MvHuE8Gb8jcaqwKtqGBP6DpjGKTTVCeun9MNC8ePbxfAUoaMCA2CDDZqXrQ3HKabF3JCZRaMaAZPwiEfXab4KpgLEygxSj7vBdQfLupqpdz3oc35Nbiy7MRhRQGffJ2b7f9vc";
                break;
            }
            case 3: {
                uri = new NodeURI("udp://localhost:5403", "3");
                publicKey = "Vj75CuZgqYqhewfDfF9KQEdejjqbnoDiRjuXUnwFDsa5tP2JN5uKR2nMsM3LPtCcW5QH8J8sTjS9aHVTgUAPMGxvpj5baTwrtYng2XM8ozopQgScinkncaxJLAppao2RQqBFTNzB1RKd9CJNMvHa7FoAJmDrfYiehSj5AfGwAHTy5DjzRiNHKbqWpaje2pKu6knJMiXQVphp5wNpnfnWVVWmD43SQ";
                privateKey = "2qCXjvtcr4nR8xRX2v7yEntuNKBonfFVT1PWVpAN1Zk9YvEWYr4b4aTexXkg48gExEhU8xBiBPcG5bKgM2jxxmN3h7CiYPP4wAo6UC1JDTmX87yWm6s94YCwYCZ8sFRof5yTGGxgbagdTDrvuaVjAmur48MmaGGJiZNfRsfQuu4nGGkGkJnTVQ7Whj4GnjXDy87Z6WzneuYfCZnN6BLcxn75pe8QHUzJeCVyJSfZ8gasZQM5ikWVVNcKEEKbpUVeGXRFkScfDXPc8NxvWcF71ZdeFdTBagMXdvQtiR1b7iiWGd6b9FoJYP6MRwvRmfMA8cbJ5V2XgN9uJZeXoJnJwguJjSSgLV8NirjUyt6e7piuWkCsK4wVDsEQ3fSPkT7jDfA3cmruCsQ7EWKa3UbeQk7thAUY3XhgA1F7CNNSnAR72qHB48WfgmqZVrQLVEA5LRmq9pWs6zwQNPWkmh8DtHtghY7AhhmcHKthVyxtry88u72xu9jC3Pwiy7tJPEYTAuvxdRS25v8fKAvdEWghTCcP95pqgwVUWStyfPCHofNJNNrEVGWRN7tKet3hSDRwoGo6ywDU7WsifrN976NvBDWCYB6RL7XbjnaGRUtuoEQEZoxP9cVcTfvYYqq4jKmJmuETuvRaMaPLeCbMkLGCoK5uGi5h1J7eZxRE5WegxdRdhjNje3tXwNuHUySXpt4Jzm7DinkH7bmiaTjDd6jyDn5FguWyHenTw3nnBkKEH6DFG5F5aB8JBGLmLhMxYTmVE5hbiKMxteb1gSWshXajEN5h9U8LBGnvTiFKmG3ZMn3twhwsNmhnvvQeAXRzXspcjp6EGB4VKmopfVqjTJWNq7TezKGPWG1fzV9RC";
                break;
            }
            case 4: {
                getKeyUI();
                return true;
            }
            case 5: {
                uri = new NodeURI("udp://localhost:5403", "3");
                publicKey = "Vj75CuZgqYqhewfDfF9KQEdejjqbnoDiRjuXUnwFDsa5tP2JN5uKR2nMsM3LPtCcW5QH8J8sTjS9aHVTgUAPMGxvpj5baTwrtYng2XM8ozopQgScinkncaxJLAppao2RQqBFTNzB1RKd9CJNMvHa7FoAJmDrfYiehSj5AfGwAHTy5DjzRiNHKbqWpaje2pKu6knJMiXQVphp5wNpnfnWVVWmD43SQ";
                privateKey = "2qCXjvtcr4nR8xRX2v7yEntuNKBonfFVT1PWVpAN1Zk9YvEWYr4b4aTexXkg48gExEhU8xBiBPcG5bKgM2jxxmN3h7CiYPP4wAo6UC1JDTmX87yWm6s94YCwYCZ8sFRof5yTGGxgbagdTDrvuaVjAmur48MmaGGJiZNfRsfQuu4nGGkGkJnTVQ7Whj4GnjXDy87Z6WzneuYfCZnN6BLcxn75pe8QHUzJeCVyJSfZ8gasZQM5ikWVVNcKEEKbpUVeGXRFkScfDXPc8NxvWcF71ZdeFdTBagMXdvQtiR1b7iiWGd6b9FoJYP6MRwvRmfMA8cbJ5V2XgN9uJZeXoJnJwguJjSSgLV8NirjUyt6e7piuWkCsK4wVDsEQ3fSPkT7jDfA3cmruCsQ7EWKa3UbeQk7thAUY3XhgA1F7CNNSnAR72qHB48WfgmqZVrQLVEA5LRmq9pWs6zwQNPWkmh8DtHtghY7AhhmcHKthVyxtry88u72xu9jC3Pwiy7tJPEYTAuvxdRS25v8fKAvdEWghTCcP95pqgwVUWStyfPCHofNJNNrEVGWRN7tKet3hSDRwoGo6ywDU7WsifrN976NvBDWCYB6RL7XbjnaGRUtuoEQEZoxP9cVcTfvYYqq4jKmJmuETuvRaMaPLeCbMkLGCoK5uGi5h1J7eZxRE5WegxdRdhjNje3tXwNuHUySXpt4Jzm7DinkH7bmiaTjDd6jyDn5FguWyHenTw3nnBkKEH6DFG5F5aB8JBGLmLhMxYTmVE5hbiKMxteb1gSWshXajEN5h9U8LBGnvTiFKmG3ZMn3twhwsNmhnvvQeAXRzXspcjp6EGB4VKmopfVqjTJWNq7TezKGPWG1fzV9R";
                break;
            }
            default: {
                //关闭界面
                return true;
            }
        }

        //进入主页
        indexUI(uri, publicKey, privateKey);

        return isBreakLoop;
    }

    /**
     * 获取公私钥的UI
     */
    private static void getKeyUI() {
        Map<String, String> keyPairMap = ECKey.genKeyPair();

        String publicKeyStr = keyPairMap.get("publickey");
        String privateKeyStr = keyPairMap.get("privatekey");
        System.out.println("生成的公私钥信息如下：");
        System.out.println("publicKeyStr =" + publicKeyStr);
        System.out.println("privateKeyStr =" + privateKeyStr);
    }

    public static void main(String[] args) {
        //udp://localhost:5400 0 udp://localhost:10000 0
        //netstat -aon|findstr "5400"
        //tasklist|findstr "2720"
        System.out.println("----------" + Constant.SOFT_NAME + "-"
                + Constant.VERSION + "-----------");

        while (!loginUI()) {
            ;
        }

        System.out.println("-----------------程序结束----------------");


    }
}
