package com.timvanx.web.dao;

import cn.hutool.core.util.ObjectUtil;
import com.timvanx.lightdisk.HeartBeatLog;
import com.timvanx.lightdisk.LightDisk;
import com.timvanx.web.config.LightDiskHungrySingleton;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <h3>BlockChain</h3>
 * <p>交易列表mapper</p>
 *
 * @author : TimVan
 * @date : 2020-05-03 12:28
 **/
@Repository
public class TransactionDAO {

    /** 获取所有的本地交易列表 */
    public List<HeartBeatLog> selectTransaction() {
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if(ObjectUtil.isNull(lightDisk)){
            System.out.println("in selectTransaction:lightDisk为空");
            return null;
        }

        return lightDisk.getLocalHeartbeatList();

    }


    /**
     * 获取分页的本地交易列表
     * @param page 当前页
     * @param limit 每页显示的条数
     */
    public Map<String, Object> selectTransaction(int page, int limit) {
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if(ObjectUtil.isNull(lightDisk)){
            System.out.println("in selectTransaction(int page, int limit):lightDisk为空");
            return null;
        }
        return lightDisk.getTransactionList(page, limit);
    }

    /**
     * 通过公钥，获取分页的本地交易列表
     * @param publicKey 公钥
     * @param page 当前页
     * @param limit 每页显示的条数
     */
    public Map<String, Object> selectTransaction(String publicKey,int page, int limit) {
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if(ObjectUtil.isNull(lightDisk)){
            System.out.println("in selectTransaction(int page, int limit):lightDisk为空");
            return null;
        }
        return lightDisk.getTransactionListByPublicKey(publicKey,page, limit);
    }



}
