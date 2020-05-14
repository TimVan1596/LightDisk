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

    public List<Block> selectLocalBlock() {
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if(ObjectUtil.isNull(lightDisk)){
            System.out.println("lightDisk为空");
            return null;
        }
        System.out.println("height="+lightDisk.getLocalChainHeight());

        return lightDisk.getLocalBlockList();



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
