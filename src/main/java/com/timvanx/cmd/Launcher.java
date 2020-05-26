package com.timvanx.cmd;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.RandomUtil;
import com.timvanx.blockchain.model.Block;
import com.timvanx.blockchain.model.Transaction;
import com.timvanx.gossip.KillServer;
import com.timvanx.gossip.model.NodeURI;
import com.timvanx.blockchain.model.ECKey;
import com.timvanx.lightdisk.LightDisk;


import java.util.Date;
import java.util.List;
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
    private static void indexUI(NodeURI uri, NodeURI seedNode, String publicKey, String privateKey) {
        if (!ECKey.isKeyMatch(publicKey, privateKey)) {
            System.out.println("验证失败");
            return;
        }


        LightDisk lightDisk = new LightDisk(uri, seedNode);

        System.out.println("登录成功");


        //是否终止循环
        boolean isBreakLoop = false;
        while (!isBreakLoop) {
            System.out.println("-------------------");
            System.out.println("您的ID:" + lightDisk.getUri().getId()
                    + "=>" + lightDisk.getUri().getIpAddress());
            System.out.println("种子ID:" + lightDisk.getSeedNode().getId()
                    + "=>" + lightDisk.getSeedNode().getIpAddress());
            System.out.println("我的公钥:" + publicKey);
            System.out.println("\n\t 1、Gossip监控");
            System.out.println("\t 2、挖矿");
            System.out.println("\t 3、搜索交易");
            System.out.println("\t 4、心跳消息监控");
            System.out.println("\t 5、查看节点列表");
            System.out.println("\t 6、发布交易");
            System.out.println("\t 7、查看未打包交易列表");
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
                //挖矿
                case 2: {
                    System.out.println("请输出您需要存储的CoinBase信息");
                    String data = scanner.next();
                    System.out.println("+++++挖矿中....");
                    lightDisk.mineBlock(publicKey, data);
                    System.out.println("+++++成功挖出");
                    System.out.println("最新区块高度为："
                            + lightDisk.getLocalChainHeight());
                    break;
                }
                case 3: {
                    System.out.println("请输出需要查询的公钥(搜索全部输入-1)");
                    String option = scanner.next();
                    if ("-1".equals(option)) {
                        List<Transaction> transactions = lightDisk.getTransactionList();
                        Block.transactionSearchBoard(transactions);
                    } else {
                        List<Transaction> transactions
                                = lightDisk.getTransactionListByPublicKey(option);
                        Block.transactionSearchBoard(transactions);
                    }
                    break;
                }
                //心跳消息监控
                case 4: {
                    Date date = new Date();
                    DateTime time = new DateTime(date);
                    System.out.println("当前时间：" + time);

                    System.out.println("需要详细信息请输入1");
                    int data = scanner.nextInt();
                    //是否打开详细信息
                    boolean isOpenDetail = false;
                    if (data == 1) {
                        isOpenDetail = true;
                    }
                    lightDisk.heartBeatLogBoard(isOpenDetail);

                    break;
                }
                //查看节点列表
                case 5: {
                    lightDisk.membersBoardCMD();
                    break;
                }
                //发布交易
                case 6:{
                    System.out.println("请输出您需要存储的信息");
                    String data = scanner.next();
                    lightDisk.addTransaction(publicKey, data);
                    break;
                }
                //显示未打包的交易列表
                case 7:{
                        unPackTransactionListBoard(
                                lightDisk.getUnPackTransactionList());
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
                    if (data == 1) {
                        isOpenTX = true;
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

    /** 显示未打包的交易列表 */
    private static void unPackTransactionListBoard(List<Transaction> unPackTransactionList){
        System.out.println("--未打包的交易列表 UnPackTransactionList----");
        for (Transaction transaction : unPackTransactionList){
            System.out.println("----");
            DateTime blockTime = new DateTime(transaction.getTimestamp());
            System.out.println("生成时间戳:" + transaction.getTimestamp() + "(" + blockTime + ")");
            System.out.println("交易hash:" + transaction.getHash());
            System.out.println("收款人:" + transaction.getPublicKey());
            System.out.println("script字符串:" + transaction.getScriptString());
            System.out.println("----");
        }
        System.out.println("-------");

    }

    private static boolean loginUI() {
        //是否终止循环
        boolean isBreakLoop = false;

        System.out.println("-----------------------------------");
        String ip = NodeURI.getLocalIP();
        System.out.println("您的IP地址为" + ip);
        System.out.println("1、作为种子节点登入");
        System.out.println("2、作为普通节点登入");
        System.out.println("3、生成公私钥账号");
        System.out.println("4、错误公私钥测试");
        System.out.println("5、一键清空端口占用");
        System.out.println("6、自定义登录");
        System.out.println("0、关闭界面");

        NodeURI uri = null;
        NodeURI seedNode = null;
        String publicKey = null;
        String privateKey = null;
        int port = 5400;
        int id = RandomUtil.randomInt(2000000, 3000000);
        Scanner scanner = new Scanner(System.in);
        int optionNum = scanner.nextInt();

        switch (optionNum) {
            //作为种子节点登入，在[1, 1000000)中自动生成端口号
            case 1: {
                System.out.println("请输入作为种子节点的端口号");
                port = scanner.nextInt();
                id = Integer.parseInt(ip.split("\\.")[3]+port);
                uri = new NodeURI(ip, port, String.valueOf(id));
                seedNode = uri;

                publicKey = "Vj75CuZgqYqhewfDfF9KQEdejjqbnoDiRjuXUnwD37fGGhG7DHdGK2DpmVUAZFteDRx8tkNWrsCy9nSTXeDrRUf7QHhf2DtMBpaSi9pu4ZeWpHc7TzRbWMUBiE94rTMJVzAJMfVM4MtfCES1Vn22ezZY7YiGhbEtVgjHotrtktBiNypaoQR9kWQzFxhdjhEKAsKK46aVC8hm1Q1NJ5bwFne4Ye3VA";
                privateKey = "6TAzA5ZnZvXfnkvfgG5EZF978HcKn5QHT26e3r9GXrp2J1GDG2s63NeupL1bkBpextYMb8oc8994aQ3dBMx9ymjFCNLhQK33wssFuGSoHe6vpG4QybbxGc6v7qTYpuCgx4VXW6tZHUbGkWCS9MTvfTtd26Mq4ZQw2HPahgvFAvzZk1ZozznmUz6UbabVhs47tPjSqj33XWGZhfN5qvhc6sRtS93jfzfyD52hcF7jCYDrhPZZ2D51tSsXWT6XJ98nnQb78FMvCtjwg1tXEE5AsBLhKRJqRJNREpoVLrkyvNB9XRDjhfXACyZ85FrtNjqkTmd1pUXQ1Va5bXKN7G2PctvR39Ju93tXcK86n3yiGjh5ajVpKUALj19QEM823DPYGrqdbuyBdzsuQzKEwswYi8kHaYZgGyBShwRM3cV95eMyLdFTc4ARzqZS6x5vF75MUSsTZiNuKMiNZLvoNUfb9Md7h6PEUZt1UKUSSTK2Q38V5rCrngpKmtu26Tr6ywmranTv2wktsKM4huRFggGqaVJCFKyGJwhWsCVbPHzQW8k8t9uj1dFr1aeZEbEQy6JPgu2ihddNavhu5Q7fKoRw4sroxmMNW2GxPP9FAFB1c98pmJ8eLSMMiGtd3eqKaE3i8W89sBhCt7BQhjtL5q5ma7cTWYZLUBTqMDMrS1xNjNGT9jkt1JoXMMJ3Vg5xEhpaaXC4HkYKtNJ9YBQoYqRJT71wi9LnJBrUBTuAB1enh3Zb4MNFdr8EDmQGmZbs662GmDucRRoLJE8ZPJBYKciynEEhsUVEfVCbyfdmZXsS5YCm3GbG3uqcRAaREKDiaeoJ4G4k8cvegpRGUuujqFoVVVhJPta1HS4PX1";
                break;
            }
            //作为普通节点登入，在[1000000, 2000000)中自动生成端口号
            case 2: {

                System.out.println("请输入您的端口号");
                port = scanner.nextInt();
                id = Integer.parseInt(ip.split("\\.")[3]+port);

                System.out.println("请输入种子节点IP地址");
                String seedNodeIP = scanner.next();
                System.out.println("请输入种子节点端口号");
                int seedNodePort = scanner.nextInt();
                int seedNodeID = Integer.parseInt(seedNodeIP
                        .split("\\.")[3]+seedNodePort);
                uri = new NodeURI(ip, port, String.valueOf(id));
                seedNode = new NodeURI(seedNodeIP, seedNodePort
                        , String.valueOf(seedNodeID));
                publicKey = "Vj75CuZgqYqhewfDfF9KQEdejjqbnoDiRjuXUnwEvrF1HRD5uJpP2Po124MaKFucrzq8rBFS9RdzcGiFn5Shb6YMKP98rqK5erQeKb92J7h8VpcyHs2PExxC9hQ6Mc43ocz7NTVtZagDMdR6zEWvYPQUJS1Gns2G98SwGQPUPzJtvfZBkobUrNLqFfk5y28Wekwfoe4BZBquGMAMtusG36DzbxaGY";
                privateKey = "2EdhbDGp6Ni449TvBMugXsBZKnr2Hq4gdg4NxnYhKDtPoMtqUidueUuQk88LzQ7SF4pL9UQ39ABESashwGy2mcHqvoTFyCeVHGg12D9ki2C9HgLLpBBPVanLJ8k2KM8aLSU5bzpJsfKnqRTmKn7VJbmPTjcikKpaT3SU32SFpLayWT5PFuEPHgPVHUPDoDFSvNk4TWsYBdtVz6Td7PmVymU2ZwdZononK8HDvCxGwUkx9GkD3pCEU3e9zMV2dURfX521rZiguKCBCSCwTuFDQcZLpkJBecTu3h2FUTA4RQzXBXmmTpmxqgZi6usW4qYPgLhWxYzBzkXKJQhcXg9EcDnLTpLenJNWABNnhLQgeLwN8LeYXv8uygVHyC7oETf7fPBQxPTKcH3neKvAUkSTaxpgWo7AdJNo3srwmdxe6NWyWEHiSykL3pg5gt1Qmh5UdPcLrT1WshxrkG1LrvjzXuPNy2DMaEvYrDYPGdV1dXZLxcXAQnd2FWZwcjpmJwJPZRKsCBsLvKPWNCMBpUWXGcrnsthtaR33pepSqCcy4zjkbHFkd4vGxdDwNiNhRCHMEoPZmwJmRZB7HG145D6tJQCzcbRwsoPF3cgPDA3WJKDLifwCm3JGTCqpQ12oH4g1kSztgSfJDSBjUG6ED5NKyff5PFNLRQYpPiBmR6GBjfcZjE3rq4REnmrT2YASZ3i3hnrZ1iWEPC31e3EXX6Qyfb3WAcj4MvHuE8Gb8jcaqwKtqGBP6DpjGKTTVCeun9MNC8ePbxfAUoaMCA2CDDZqXrQ3HKabF3JCZRaMaAZPwiEfXab4KpgLEygxSj7vBdQfLupqpdz3oc35Nbiy7MRhRQGffJ2b7f9vc";
                break;
            }
            case 3: {
                getKeyUI();
                return false;
            }
            //错误公私钥测试
            case 4: {
                publicKey = "Vj75CuZgqYqhewfDfF9KQEdejjqbnoDiRjuXUnwFDsa5tP2JN5uKR2nMsM3LPtCcW5QH8J8sTjS9aHVTgUAPMGxvpj5baTwrtYng2XM8ozopQgScinkncaxJLAppao2RQqBFTNzB1RKd9CJNMvHa7FoAJmDrfYiehSj5AfGwAHTy5DjzRiNHKbqWpaje2pKu6knJMiXQVphp5wNpnfnWVVWmD43SQ";
                privateKey = "2qCXjvtcr4nR8xRX2v7yEntuNKBonfFVT1PWVpAN1Zk9YvEWYr4b4aTexXkg48gExEhU8xBiBPcG5bKgM2jxxmN3h7CiYPP4wAo6UC1JDTmX87yWm6s94YCwYCZ8sFRof5yTGGxgbagdTDrvuaVjAmur48MmaGGJiZNfRsfQuu4nGGkGkJnTVQ7Whj4GnjXDy87Z6WzneuYfCZnN6BLcxn75pe8QHUzJeCVyJSfZ8gasZQM5ikWVVNcKEEKbpUVeGXRFkScfDXPc8NxvWcF71ZdeFdTBagMXdvQtiR1b7iiWGd6b9FoJYP6MRwvRmfMA8cbJ5V2XgN9uJZeXoJnJwguJjSSgLV8NirjUyt6e7piuWkCsK4wVDsEQ3fSPkT7jDfA3cmruCsQ7EWKa3UbeQk7thAUY3XhgA1F7CNNSnAR72qHB48WfgmqZVrQLVEA5LRmq9pWs6zwQNPWkmh8DtHtghY7AhhmcHKthVyxtry88u72xu9jC3Pwiy7tJPEYTAuvxdRS25v8fKAvdEWghTCcP95pqgwVUWStyfPCHofNJNNrEVGWRN7tKet3hSDRwoGo6ywDU7WsifrN976NvBDWCYB6RL7XbjnaGRUtuoEQEZoxP9cVcTfvYYqq4jKmJmuETuvRaMaPLeCbMkLGCoK5uGi5h1J7eZxRE5WegxdRdhjNje3tXwNuHUySXpt4Jzm7DinkH7bmiaTjDd6jyDn5FguWyHenTw3nnBkKEH6DFG5F5aB8JBGLmLhMxYTmVE5hbiKMxteb1gSWshXajEN5h9U8LBGnvTiFKmG3ZMn3twhwsNmhnvvQeAXRzXspcjp6EGB4VKmopfVqjTJWNq7TezKGPWG1fzV9R";
                break;
            }
            //一键清空端口占用
            case 5: {
                KillServer.gossipPortKill();
                return false;
            }
            case 6:{
                System.out.println("请输入本节点IP地址");
                ip = scanner.next();
                System.out.println("请输入本节点端口号");
                port = scanner.nextInt();
                System.out.println("请输入本节点ID");
                id = scanner.nextInt();

                System.out.println("请输入种子节点IP地址");
                String seedNodeIP = scanner.next();
                System.out.println("请输入种子节点端口号");
                int seedNodePort = scanner.nextInt();
                System.out.println("请输入种子节点ID");
                int seedNodeID = scanner.nextInt();

                uri = new NodeURI(ip, port, String.valueOf(id));
                seedNode = new NodeURI(seedNodeIP, seedNodePort
                        , String.valueOf(seedNodeID));
                publicKey = "Vj75CuZgqYqhewfDfF9KQEdejjqbnoDiRjuXUnwEvrF1HRD5uJpP2Po124MaKFucrzq8rBFS9RdzcGiFn5Shb6YMKP98rqK5erQeKb92J7h8VpcyHs2PExxC9hQ6Mc43ocz7NTVtZagDMdR6zEWvYPQUJS1Gns2G98SwGQPUPzJtvfZBkobUrNLqFfk5y28Wekwfoe4BZBquGMAMtusG36DzbxaGY";
                privateKey = "2EdhbDGp6Ni449TvBMugXsBZKnr2Hq4gdg4NxnYhKDtPoMtqUidueUuQk88LzQ7SF4pL9UQ39ABESashwGy2mcHqvoTFyCeVHGg12D9ki2C9HgLLpBBPVanLJ8k2KM8aLSU5bzpJsfKnqRTmKn7VJbmPTjcikKpaT3SU32SFpLayWT5PFuEPHgPVHUPDoDFSvNk4TWsYBdtVz6Td7PmVymU2ZwdZononK8HDvCxGwUkx9GkD3pCEU3e9zMV2dURfX521rZiguKCBCSCwTuFDQcZLpkJBecTu3h2FUTA4RQzXBXmmTpmxqgZi6usW4qYPgLhWxYzBzkXKJQhcXg9EcDnLTpLenJNWABNnhLQgeLwN8LeYXv8uygVHyC7oETf7fPBQxPTKcH3neKvAUkSTaxpgWo7AdJNo3srwmdxe6NWyWEHiSykL3pg5gt1Qmh5UdPcLrT1WshxrkG1LrvjzXuPNy2DMaEvYrDYPGdV1dXZLxcXAQnd2FWZwcjpmJwJPZRKsCBsLvKPWNCMBpUWXGcrnsthtaR33pepSqCcy4zjkbHFkd4vGxdDwNiNhRCHMEoPZmwJmRZB7HG145D6tJQCzcbRwsoPF3cgPDA3WJKDLifwCm3JGTCqpQ12oH4g1kSztgSfJDSBjUG6ED5NKyff5PFNLRQYpPiBmR6GBjfcZjE3rq4REnmrT2YASZ3i3hnrZ1iWEPC31e3EXX6Qyfb3WAcj4MvHuE8Gb8jcaqwKtqGBP6DpjGKTTVCeun9MNC8ePbxfAUoaMCA2CDDZqXrQ3HKabF3JCZRaMaAZPwiEfXab4KpgLEygxSj7vBdQfLupqpdz3oc35Nbiy7MRhRQGffJ2b7f9vc";
                break;
            }
            default: {
                //关闭界面
                return true;
            }
        }

        //进入主页
        indexUI(uri, seedNode, publicKey, privateKey);

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
