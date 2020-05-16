package com.timvanx.web.dao;

import cn.hutool.core.util.ObjectUtil;
import com.timvanx.blockchain.model.Block;
import com.timvanx.lightdisk.LightDisk;
import com.timvanx.web.config.LightDiskHungrySingleton;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <h3>BlockChain</h3>
 * <p>区块mapper</p>
 *
 * @author : TimVan
 * @date : 2020-05-03 12:28
 **/
@Repository
public class BlockDAO {

    /** 获取所有的本地区块 */
    public List<Block> selectLocalBlock() {
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if(ObjectUtil.isNull(lightDisk)){
            System.out.println("lightDisk为空");
            return null;
        }
//        System.out.println("height="+lightDisk.getLocalChainHeight());

        return lightDisk.getLocalBlockList();

    }


    /**
     * 获取分页的本地区块
     * @param page 当前页
     * @param limit 每页显示的条数
     */
    public List<Block> selectLocalBlock(int page,int limit) {
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if(ObjectUtil.isNull(lightDisk)){
            System.out.println("lightDisk为空");
            return null;
        }

        return lightDisk.getLocalBlockList( page, limit);

    }

    /**
     * 获得区块链的长度
     */
    public int getBlockListSize(){
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if(ObjectUtil.isNull(lightDisk)){
            System.out.println("lightDisk为空");
            return 0;
        }

        return lightDisk.getBlockListSize();
    }

    public List<Block> mineBlock() {
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if(ObjectUtil.isNull(lightDisk)){
            return null;
        }
        System.out.println("height="+lightDisk.getLocalChainHeight());

        return lightDisk.getLocalBlockList();



    }
}
