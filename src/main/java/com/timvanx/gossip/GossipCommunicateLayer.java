package com.timvanx.gossip;

import cn.hutool.Hutool;
import cn.hutool.core.util.StrUtil;
import com.timvanx.gossip.model.NodeURI;
import com.timvanx.model.ResponseJson;
import org.apache.com.timvanx.gossip.LocalMember;

import java.util.*;

/**
 * <h3>BlockChain</h3>
 * <p>支持Gossip协议的通信层底层</p>
 *
 * @author : TimVan
 * @date : 2020-04-01 17:13
 **/
public class GossipCommunicateLayer extends BaseGossipCommunicate {

    /**
     * 构造函数
     *
     * @param uri         本节点URI
     * @param seedNodeURI 种子节点URI
     */
    public GossipCommunicateLayer(NodeURI uri, NodeURI seedNodeURI) {
        super(uri, seedNodeURI);
    }

    /**
     * 构造函数
     *
     * @param uri         本节点URI
     * @param seedNodeURI 种子节点URI
     * @return 返回正在运行的Gossip对象
     */
    public static GossipCommunicateLayer getGossipExecute(NodeURI uri, NodeURI seedNodeURI) {
        GossipCommunicateLayer gossip =
                new GossipCommunicateLayer(uri, seedNodeURI);
        gossip.exec();

        return gossip;
    }


    /**
     * 封装版的获取键值对
     *
     * @param key 键
     * @return 获取值，若无键值返回null
     */
    public String getKey(String key) {
        String value = null;

        ResponseJson ret = this.get(key);
        if (ret.getCode() == 0) {
            value = (String) ret.getData();
        }
        return value;
    }

    /**
     * 封装版的获取ACC累加器
     *
     * @param accName acc名称
     * @return 获取值，若无键值返回null
     */
    public String getAcc(String accName) {
        String value = null;
        ResponseJson ret = this.accGet(accName);
        if (ret.getCode() == 0) {
            value = ret.getData();
        }
        return value;
    }

    /**
     * gossip监控（命令行版本）
     */
    public void gossipBoardCMD() {
        Scanner scanner = new Scanner(System.in);
        int optionNum = 0;
        //进入测试通信层主界面
        do {
            System.out.println("------------------------------------");
            System.out.println("\n\t 1、显示当前所有信息");
            System.out.println("\t 2、添加key-value");
            System.out.println("\t 3、根据key查value");
            System.out.println("\t 4、添加累加器");
            System.out.println("\t 5、根据key查累加器");
            System.out.println("\t 6、查看CRDT列表");
            System.out.println("\t 输入-1退出gossip监控");
            System.out.println("------------------------------------");
            optionNum = scanner.nextInt();

            switch (optionNum) {
                case 1: {
                    ResponseJson ret = this.get("ALL_KEY_NAME_SPACE");
                    System.out.println((String) ret.getData());
                    break;
                }
                case 2: {
                    System.out.println("请输入key");
                    String key = scanner.next();
                    System.out.println("请输入value");
                    String value = scanner.next();
                    this.add(value, key);
                    break;
                }
                case 3: {
                    System.out.println("请输入key");
                    String key = scanner.next();

                    System.out.println(key + "=>" + getValue(key));

                    break;
                }
                case 4: {
                    System.out.println("请输入累加器名称");
                    String key = scanner.next();
                    System.out.println("请输入value");
                    Long value = scanner.nextLong();
                    this.accAdd(String.valueOf(value), key);


                    break;
                }
                case 5: {
                    System.out.println("请输入累加器名称");
                    String key = scanner.next();
                    ResponseJson ret = this.accGet(key);
                    if (ret.getCode() == 0) {
                        System.out.println(key + "=>" + ret.getData());
                    } else {
                        System.out.println("无此累加器");
                    }

                    break;
                }
                case 6: {
                    List<Map<String, String>> mapList = getCrdtList();
                    int i = 0;
                    for (Map<String, String> map : mapList) {
                        System.out.println((i++) + "=>{");
                        System.out.println("\tkey=" + map.get("key"));
                        System.out.println("\tvalue=" + map.get("value"));
                        System.out.println("}");
                    }

                    break;
                }
                default: {

                }
            }


        }
        while (optionNum != -1);
    }

    private String getValue(String key) {
        ResponseJson ret = this.get(key);
        if (ret.getCode() == 0) {
            return ret.getData();
        } else {
            return "[]";
        }
    }





    /**
     * 获得CRDT列表的节点个数
     */
    public int getCrdtListSize() {
        ResponseJson ret = this.get("ALL_KEY_NAME_SPACE");
        String keySetStr = ret.getData();
        keySetStr = keySetStr.substring(1, keySetStr.length() - 1);
        String[] strArray = keySetStr.split(",");
        return strArray.length;
    }

    /**
     * gossip监控（命令行版本）
     */
    public void liveDeadBoardCMD() {
        List<LocalMember> liveMembers = getLiveMembers();
        List<LocalMember> deadMembers = getDeadMembers();

        printMemberInfo(liveMembers, "Live");
        printMemberInfo(deadMembers, "Dead");
    }

    /**
     * cmd打印成员节点信息
     *
     * @param members 节点列表
     * @param state   状态,Live或Dead
     */
    private void printMemberInfo(List<LocalMember> members, String state) {
        if (members.isEmpty()) {
            System.out.println(state + ": (none)");
            return;
        }
        System.out.println(state + ": " + members.get(0));
        for (int i = 1; i < members.size(); i++) {
            System.out.println("    : " + members.get(i));
        }
    }


    /**
     * 单元测试用例
     */
    public static void main(String[] args) {
        System.out.println("通信层测试 - 请输入启动节点列表编号");
        System.out.println("1、种子节点");
        System.out.println("2、启动2号节点");
        System.out.println("3、启动3号节点");

        //    udp://localhost:5400 0 udp://localhost:10000 0
//netstat -aon|findstr "5400"
// tasklist|findstr "2720"
        NodeURI uri = null;
        NodeURI seedNode = new NodeURI("udp://localhost:5400", "0");

        Scanner scanner = new Scanner(System.in);
        int optionNum = scanner.nextInt();
        switch (optionNum) {
            case 1: {
                uri = new NodeURI("udp://localhost:5400", "0");
                break;
            }
            case 2: {
                uri = new NodeURI("udp://localhost:5402", "2");
                break;
            }
            case 3: {
                uri = new NodeURI("udp://localhost:5403", "3");
                break;
            }
            default: {
                uri = new NodeURI("udp://localhost:5403", "3");
            }
        }

        GossipCommunicateLayer gossip =
                new GossipCommunicateLayer(uri, seedNode);
        gossip.exec();

        //进入测试通信层主界面
        do {
            System.out.println("------------------------------------");
            System.out.println("我的ID=" + uri.getId());
            System.out.println("\n\t 1、显示当前所有信息");
            System.out.println("\t 2、添加key-value");
            System.out.println("\t 3、根据key查value");
            System.out.println("\t 4、添加累加器");
            System.out.println("\t 5、根据key查累加器");
            System.out.println("------------------------------------");
            optionNum = scanner.nextInt();

            switch (optionNum) {
                case 1: {
                    ResponseJson ret = gossip.get("ALL_KEY_NAME_SPACE");
                    System.out.println((String) ret.getData());
                    break;
                }

                case 2: {
                    System.out.println("请输入key");
                    String key = scanner.next();
                    System.out.println("请输入value");
                    String value = scanner.next();
                    gossip.add(value, key);
                    break;
                }
                case 3: {
                    System.out.println("请输入key");
                    String key = scanner.next();
                    ResponseJson ret = gossip.get(key);
                    if (ret.getCode() == 0) {
                        System.out.println(key + "=>" + ret.getData());
                    } else {
                        System.out.println("无此键值");
                    }

                    break;
                }
                case 4: {
                    System.out.println("请输入累加器名称");
                    String key = scanner.next();
                    System.out.println("请输入value");
                    Long value = scanner.nextLong();
                    gossip.accAdd(String.valueOf(value), key);


                    break;
                }

                case 5: {
                    System.out.println("请输入累加器名称");
                    String key = scanner.next();
                    ResponseJson ret = gossip.accGet(key);
                    if (ret.getCode() == 0) {
                        System.out.println(key + "=>" + ret.getData());
                    } else {
                        System.out.println("无此累加器");
                    }

                    break;
                }
                default: {

                }
            }


        }
        while (optionNum != -1);

    }
}
