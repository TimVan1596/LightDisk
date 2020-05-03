package com.timvanx.web.service;

import com.timvanx.blockchain.model.Block;
import com.timvanx.web.dao.BlockDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <h3>BlockChain</h3>
 * <p>区块服务层</p>
 *
 * @author : TimVan
 * @date : 2020-05-03 12:09
 **/
@Service
public class BlockServiceImpl {

    /**构造器注入*/
    final BlockDAO blockDAO;

    @Autowired
    public BlockServiceImpl(BlockDAO blockDAO) {
        this.blockDAO = blockDAO;
    }

    /**
     * 获取所有的本地区块
     */
    public List<Block> selectLocalBlock(){
        return blockDAO.selectLocalBlock();
    }

}
