package gossip;

import gossip.model.CrdtMap;
import gossip.model.NodeURI;
import model.ResponseJson;

import java.util.Scanner;

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
     * @param uri 本节点URI
     * @param seedNodeURI 种子节点URI
     */
    public GossipCommunicateLayer(NodeURI uri, NodeURI seedNodeURI) {
        super(uri, seedNodeURI);
    }

    /**
     * 构造函数
     * @param uri 本节点URI
     * @param seedNodeURI 种子节点URI
     * @return 返回正在运行的Gossip对象
     */
    public static GossipCommunicateLayer getGossipExecute(NodeURI uri, NodeURI seedNodeURI){
        GossipCommunicateLayer gossip =
                new GossipCommunicateLayer(uri,seedNodeURI);
        gossip.exec();

        return gossip;
    }

    /** 单元测试用例*/
    public static void main(String[] args) {
        System.out.println("通信层测试 - 请输入启动节点列表编号");
        System.out.println("1、种子节点");
        System.out.println("2、启动2号节点");
        System.out.println("3、启动3号节点");

        //    udp://localhost:5400 0 udp://localhost:10000 0
//netstat -aon|findstr "5400"
// tasklist|findstr "2720"
        NodeURI uri = null;
        NodeURI seedNode = new NodeURI("udp://localhost:5400","0");

        Scanner scanner = new Scanner(System.in);
        int optionNum = scanner.nextInt();
        switch (optionNum){
            case 1:{
                 uri = new NodeURI("udp://localhost:5400","0");
                break;
            }
            case 2:{
                uri = new NodeURI("udp://localhost:5402","2");
                break;
            }
            case 3:{
                uri = new NodeURI("udp://localhost:5403","3");
                break;
            }
            default:{
                uri = new NodeURI("udp://localhost:5403","3");
            }
        }

        GossipCommunicateLayer gossip =
                new GossipCommunicateLayer(uri,seedNode);
        gossip.exec();

        //进入测试通信层主界面
        do{
            System.out.println("------------------------------------");
            System.out.println("我的ID="+uri.getId());
            System.out.println("\n\t 1、显示当前所有信息");
            System.out.println("\t 2、添加key-value");
            System.out.println("\t 3、根据key查value");
            System.out.println("\t 4、添加累加器");
            System.out.println("\t 5、根据key查累加器");
            System.out.println("------------------------------------");
            optionNum = scanner.nextInt();

            switch (optionNum){
                case 1:{
                    ResponseJson ret = gossip.get("ALL_KEY_NAME_SPACE");
                    System.out.println((String) ret.getData());
                    break;
                }

                case 2:{
                    System.out.println("请输入key");
                    String key = scanner.next();
                    System.out.println("请输入value");
                    String value = scanner.next();
                    gossip.add(value,key);
                    break;
                }
                case 3:{
                    System.out.println("请输入key");
                    String key = scanner.next();
                    ResponseJson ret = gossip.get(key);
                    if (ret.getCode() == 0){
                        System.out.println(key+"=>"+ret.getData());
                    }
                    else{
                        System.out.println("无此键值");
                    }

                    break;
                }
                case 4:{
                    System.out.println("请输入累加器名称");
                    String key = scanner.next();
                    System.out.println("请输入value");
                    Long value = scanner.nextLong();
                    gossip.accAdd(value,key);
                    break;
                }

                case 5:{
                    System.out.println("请输入累加器名称");
                    String key = scanner.next();
                    ResponseJson ret = gossip.accGet(key);
                    if (ret.getCode() == 0){
                        System.out.println(key+"=>"+ret.getData());
                    }
                    else{
                        System.out.println("无此累加器");
                    }

                    break;
                }
                default:{

                }
            }



        }
        while (optionNum != -1);

    }
}
