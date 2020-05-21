package com.timvanx.web.service;

import com.timvanx.lightdisk.HeartBeatLog;
import com.timvanx.web.dao.HeartBeatDAO;
import org.apache.com.timvanx.gossip.LocalMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <h3>BlockChain</h3>
 * <p>心跳消息服务层</p>
 *
 * @author : TimVan
 * @date : 2020-05-03 12:09
 **/
@Service
public class HeartBeatServiceImpl {

    /**构造器注入*/
    final HeartBeatDAO heartBeatDAO;

    @Autowired
    public HeartBeatServiceImpl(HeartBeatDAO heartBeatDAO) {
        this.heartBeatDAO = heartBeatDAO;
    }

    /**
     * 获取所有的本地区块
     * 并处理为倒序
     */
    public List<HeartBeatLog> selectLocalHeartbeat(){
        List<HeartBeatLog> blockList = heartBeatDAO.selectLocalHeatbeat();
        // 倒序排列
//        Collections.reverse(blockList);
        return blockList;
    }


    /**
     * 获取分页的本地区块
     * @param page 当前页
     * @param limit 每页显示的条数
     */
    public List<HeartBeatLog> selectLocalHeartbeat(int page, int limit){
        List<HeartBeatLog> heartBeatList = heartBeatDAO.selectLocalHeatbeat(page,limit);
//        // 倒序排列
//        Collections.reverse(blockList);
        return heartBeatList;
    }


    /**
     * 获取存活节点成员列表
     * @param page 当前页
     * @param limit 每页显示的条数
     */
    public List<LocalMember> selectLiveMember(int page, int limit){
        List<LocalMember> liveMemberList = heartBeatDAO.selectLiveMember(page,limit);
//        // 倒序排列
//        Collections.reverse(blockList);
        return liveMemberList;
    }

    /**
     * 获取死亡节点成员列表
     * @param page 当前页
     * @param limit 每页显示的条数
     */
    public List<LocalMember> selectDeadMember(int page, int limit){
        List<LocalMember> liveMemberList = heartBeatDAO.selectDeadMember(page,limit);
//        // 倒序排列
//        Collections.reverse(blockList);
        return liveMemberList;
    }

    /**
     * 获得CRDT数据结构列表
     * @param page 当前页
     * @param limit 每页显示的条数
     */
    public List<Map<String,String>> selectCrdt(int page, int limit){
        //        // 倒序排列
//        Collections.reverse(blockList);
        return heartBeatDAO.selectCrdt(page,limit);
    }

    /**
     * 获得心跳消息长度
     */
    public int getHeartbeatListSize(){
        return heartBeatDAO.getHeartbeatListSize();
    }

    /**
     * 获得存活结点总个数
     */
    public int getLiveMemberListSize(){
        return heartBeatDAO.getLiveMemberListSize();
    }

    /**
     * 获得CRDT总个数
     */
    public int getCrdtListSize(){
        return heartBeatDAO.getCrdtListSize();
    }
}
