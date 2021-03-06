package com.timvanx.web.dao;

import cn.hutool.core.util.ObjectUtil;
import com.timvanx.lightdisk.HeartBeatLog;
import com.timvanx.lightdisk.LightDisk;
import com.timvanx.web.config.LightDiskHungrySingleton;
import org.apache.com.timvanx.gossip.LocalMember;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <h3>BlockChain</h3>
 * <p>心跳消息mapper</p>
 *
 * @author : TimVan
 * @date : 2020-05-03 12:28
 **/
@Repository
public class HeartBeatDAO {

    /** 获取所有的本地心跳消息 */
    public List<HeartBeatLog> selectLocalHeatbeat() {
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if(ObjectUtil.isNull(lightDisk)){
            System.out.println("in selectLocalHeatbeat:lightDisk为空");
            return null;
        }

        return lightDisk.getLocalHeartbeatList();

    }


    /**
     * 获取分页的本地心跳消息
     * @param page 当前页
     * @param limit 每页显示的条数
     */
    public List<HeartBeatLog> selectLocalHeatbeat(int page, int limit) {
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if(ObjectUtil.isNull(lightDisk)){
            System.out.println("in selectLocalHeatbeat(int page, int limit):lightDisk为空");
            return null;
        }
        return lightDisk.getLocalHeartbeatList(page, limit);
    }

    /**
     * 获取存活节点成员列表
     * @param page 当前页
     * @param limit 每页显示的条数
     */
    public List<LocalMember> selectLiveMember(int page, int limit) {
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if(ObjectUtil.isNull(lightDisk)){
            System.out.println("in selectLiveMember(int page, int limit):lightDisk为空");
            return null;
        }
        return lightDisk.getLiveMemberList(page, limit);

    }

    /**
     * 获取死亡节点成员列表
     * @param page 当前页
     * @param limit 每页显示的条数
     */
    public List<LocalMember> selectDeadMember(int page, int limit) {
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if(ObjectUtil.isNull(lightDisk)){
            System.out.println("in selectDeadMember(int page, int limit):lightDisk为空");
            return null;
        }
        return lightDisk.getDeadMember(page, limit);

    }

    /**
     * 获得CRDT数据结构列表
     * @param page 当前页
     * @param limit 每页显示的条数
     */
    public List<Map<String,String>> selectCrdt(int page, int limit){
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if(ObjectUtil.isNull(lightDisk)){
            System.out.println("in selectCrdt(int page, int limit):lightDisk为空");
            return null;
        }
        return lightDisk.getCrdt(page, limit);
    }



    /**
     * 获得心跳消息的长度
     */
    public int getHeartbeatListSize(){
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if(ObjectUtil.isNull(lightDisk)){
            System.out.println("in getHeartbeatListSize:lightDisk为空");
            return 0;
        }
        return lightDisk.getHeartbeatListSize();
    }

    /**
     * 获得存活结点总个数
     */
    public int getLiveMemberListSize(){
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if(ObjectUtil.isNull(lightDisk)){
            System.out.println("in getLiveMemberListSize:lightDisk为空");
            return 0;
        }
        return lightDisk.getLiveMemberListSize();
    }

    /**
     * 获得CRDT总个数
     */
    public int getCrdtListSize(){
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if(ObjectUtil.isNull(lightDisk)){
            System.out.println("in getCrdtListSize:lightDisk为空");
            return 0;
        }
        return lightDisk.getCrdtListSize();
    }




}
