package com.timvanx.gossip;

import cn.hutool.core.util.StrUtil;
import com.timvanx.gossip.model.CrdtMap;
import com.timvanx.gossip.model.NodeURI;
import com.timvanx.model.ResponseJson;
import org.apache.com.timvanx.gossip.GossipSettings;
import org.apache.com.timvanx.gossip.LocalMember;
import org.apache.com.timvanx.gossip.RemoteMember;
import org.apache.com.timvanx.gossip.manager.GossipManager;
import org.apache.com.timvanx.gossip.manager.GossipManagerBuilder;

import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <h3>BlockChain</h3>
 * <p>Gossip点对点通信层底层-抽象类</p>
 *
 * @author : TimVan
 * @date : 2020-04-01 14:47
 **/
abstract public class BaseGossipCommunicate  {
    /** CLUSTER_NAME = 集群名称
     *  WINDOW_SIZE = 窗口期
     *  GOSSIP_INTERVAL = Gossip通信间隔时间
     * */
    private final static String CLUSTER_NAME = "BlockChain";
    private final static int WINDOW_SIZE = 500;
    private final static int GOSSIP_INTERVAL = 500;

    /** gossipService = gossip底层服务
     *  crdtMap  = gossipService对应的一个key-value Map集合（增删改查+累加器）
     * */
    private GossipManager gossipService  = null;
    private CrdtMap crdtMap = null;

    public static Pattern pattern
            = Pattern.compile("^[-\\+]?[\\d]*$");

    /**
     * 构造函数
     * */
    public BaseGossipCommunicate(NodeURI uri
            , NodeURI seedNodeURI){
        //初始化Gossip配置参数,窗口期和间隔都为500ms
        GossipSettings settings = new GossipSettings();
        settings.setWindowSize(WINDOW_SIZE);
        settings.setGossipInterval(GOSSIP_INTERVAL);

         gossipService  = GossipManagerBuilder.newBuilder()
                .cluster(CLUSTER_NAME)
                .uri(URI.create(uri.getIpAddress())).id(uri.getId())
                .gossipMembers(Collections
                        .singletonList(new RemoteMember(CLUSTER_NAME
                                , URI.create(seedNodeURI.getIpAddress()), seedNodeURI.getId())))
                .gossipSettings(settings).build();


    }

    /**
     * 主运行函数
     * */
    public void exec(){
        gossipService.init();
        crdtMap = new CrdtMap(gossipService);

    }

    /**
     * 向对应主键命名空间，添加值
     *
     * @param val 待添加值
     * @param key 对应命名空间名称
     * @return 接口返回值
     */
    public ResponseJson add(String val, String key) {
        ResponseJson resJson = new ResponseJson();
        crdtMap.push(val,key);
        resJson.setCodeSuccessful();
        return resJson;
    }


    /**
     * 向对应主键命名空间，获取值
     *
     * @param key 对应命名空间名称
     * @return 返回对应值
     */
    public ResponseJson get(String key) {
        ResponseJson resJson = new ResponseJson();
        String value = crdtMap.get(key);
        if (value.length() > 0){
            resJson.setCodeSuccessful();
            resJson.setData(value);
        }else{
            resJson.setCodeFailed();
            resJson.setMsg("获取失败，为空");
        }
        return resJson;
    }


    /**
     * 向对应累加器名称，添加值
     *
     * @param val 待添加值
     * @param key 对应命名空间名称
     * @return 接口返回值
     */
    public ResponseJson accAdd(String val, String key) {
        ResponseJson resJson = new ResponseJson();
        crdtMap.accAdd(val,key);
        resJson.setCodeSuccessful();
        return resJson;
    }


    /**
     * 向对应主键命名空间，获取值
     *
     * @param key 对应命名空间名称
     * @return 返回对应值
     */
    public ResponseJson accGet(String key) {

        ResponseJson resJson = new ResponseJson();
        String value = crdtMap.get(key);
        if (!StrUtil.hasEmpty(value)){
            resJson.setCodeSuccessful();
            resJson.setData(value);
        }else{
            resJson.setCodeFailed();
            resJson.setMsg("获取失败，为空");
        }

        return resJson;
    }

    /** 获得存活节点列表 */
    public List<LocalMember> getLiveMembers() {
        List<LocalMember> members = gossipService.getLiveMembers();
//        if (members.isEmpty()) {
//            System.out.println("Live: (none)");
//            return;
//        }
//        System.out.println("Live: " + members.get(0));
//        for (int i = 1; i < members.size(); i++) {
//            System.out.println("    : " + members.get(i));
//        }
        return members;
    }

    /** 获得死亡节点列表 */
    public List<LocalMember>  getDeadMembers() {
        List<LocalMember> members = gossipService.getDeadMembers();
//        if (members.isEmpty()) {
//            System.out.println("Dead: (none)");
//            return;
//        }
//        System.out.println("Dead: " + members.get(0));
//        for (int i = 1; i < members.size(); i++) {
//            System.out.println("    : " + members.get(i));
//        }
        return members;
    }

    /**
     * 获得CRDT列表
     */
    public List<Map<String, String>> getCrdtList() {

        String keySetStr = crdtMap.get("ALL_KEY_NAME_SPACE");
        if (StrUtil.hasEmpty(keySetStr)) {
            keySetStr = "[]";
        }
        keySetStr = keySetStr.substring(1, keySetStr.length() - 1);
        String[] strArray = keySetStr.split(",");
        List<Map<String, String>> mapList = new ArrayList<>();

        for (int i = 0; i < strArray.length; i++) {
            Map<String, String> hashMap = new HashMap<>();
            String key = strArray[i].trim();
            String valueSetStr = crdtMap.get(key);
            if (StrUtil.hasEmpty(valueSetStr)) {
                valueSetStr = "[]";
            }else if(isInteger(valueSetStr)){
                valueSetStr = "["+valueSetStr+"]";
            }

            valueSetStr = valueSetStr.substring(1, valueSetStr.length() - 1);
            String[] valueArr = valueSetStr.split(",");
            hashMap.put("key", key);
            hashMap.put("value", valueArr[0]);

//            ResponseJson retCache = this.get(key);
//            String values = null;
//            if (ret.getCode() == 0) {
//                values = retCache.getData();
//            } else {
//                values = "[]";
//            }
//            values = values.substring(1, values.length() - 1);
//            String[] valueArr = values.split(",");
//            hashMap.put("key", key);
//            hashMap.put("value", valueArr[0]);

            mapList.add(hashMap);
        }
        return mapList;
    }

    /**
     * 关闭Gossip
     * */
    public void close(){
        gossipService.shutdown();
    }

    /*
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */


    public static boolean isInteger(String str) {

        return pattern.matcher(str).matches();
    }
}
