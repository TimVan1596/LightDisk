package gossip;

import gossip.model.CrdtMap;
import gossip.model.NodeURI;
import model.ResponseJson;
import org.apache.gossip.GossipSettings;
import org.apache.gossip.RemoteMember;
import org.apache.gossip.manager.GossipManager;
import org.apache.gossip.manager.GossipManagerBuilder;

import java.net.URI;
import java.util.Collections;

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
    ResponseJson add(String val, String key) {
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
    ResponseJson get(String key) {
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

}
