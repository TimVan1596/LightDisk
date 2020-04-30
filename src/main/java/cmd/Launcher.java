package cmd;

import cn.hutool.core.date.DateTime;
import gossip.GossipCommunicateLayer;
import gossip.model.NodeURI;
import blockchain.model.BlockChain;
import blockchain.model.ECKey;
import lightdisk.LightDisk;


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
                publicKey = "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTF" +
                        "DwRd8qauALGyVQxuTeycxYYdwo2zfHF1" +
                        "H8SeR7GYS65yspJCJwCTrE1PUhfvrSE18CcvWRfQ795Tt3cb6kmCxTK";
                privateKey = "2RxV4m78RGjpDZ5VV6TTtkSkRKgEC58qoYKq5rA4" +
                        "jkjPUbhn8ttdF6mMUUcJ3nAQwqT4FsrChjkTBeikyT1UN7zjBiXC";
                break;
            }
            case 2: {
                uri = new NodeURI("udp://localhost:5402", "2");
                publicKey = "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXW" +
                        "gJTbTJ91KtuDAStqNhnh68PM8MMhiX8yUAbLCzpEsgFRc7" +
                        "baT4qg8W4ny6tWn4PtFuB1HqGGknyjGNRKwgrsyGokLJehD";
                privateKey = "2RxV4m78RGjpDZ5VV6TTtkSkRKgEC58qoYKq5rA4" +
                        "jkjPUbhnCQkzGwutagtHaMSDmrHqEj2NnBhMMPo7SBJ8f3a2dEnv";
                break;
            }
            case 3: {
                uri = new NodeURI("udp://localhost:5403", "3");
                publicKey = "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTF" +
                        "YwMxsJVcoU1VqUfWC2wTjEVNtqVzT7EYANzcQPzX4VUCKv" +
                        "FSepmbp8hA8B4HwyLSZoMiwgsQebBRpV5n3V7x4Sh";
                privateKey = "2RxV4m78RGjpDZ5VV6TTtkSkRKgEC58qoYKq5rA4jkjP" +
                        "UbhnFFguqMWUwM9ab815dpDwLZiTL7dABYbyjYGPvw7u1P6g";
                break;
            }
            case 4: {
                getKeyUI();
                return true;
            }
            case 5: {
                uri = new NodeURI("udp://localhost:5403", "3");
                publicKey = "aSq9DsNNvGhYxXyqA9wd2eduEAZ5AXWgJTbTF" +
                        "YwMxsJVcoU1VqUfWC2wTjEVNtqVzT7EYANzcQPzX4VUCKv" +
                        "FSepmbp8hA8B4HwyLSZoMiwgsQebBRpV5n3V7x4Sh";
                privateKey = "2RxV4m78RGjpDZ5VV6TTtkSkRKgEC58qoYKq5rA4jkjP" +
                        "UbhnFFguqMWUwM9ab815dpDwLZiTL7dABYbyjYGPvw7u1P6g";
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
