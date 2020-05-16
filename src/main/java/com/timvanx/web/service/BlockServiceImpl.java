package com.timvanx.web.service;

import com.timvanx.blockchain.model.Block;
import com.timvanx.web.dao.BlockDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
     * 并处理为倒序
     */
    public List<Block> selectLocalBlock(){
        List<Block> blockList = blockDAO.selectLocalBlock();
        // 倒序排列
//        Collections.reverse(blockList);
        return blockList;
    }


    /**
     * 获取分页的本地区块
     * @param page 当前页
     * @param limit 每页显示的条数
     */
    public List<Block> selectLocalBlock(int page,int limit){
        List<Block> blockList = blockDAO.selectLocalBlock(page,limit);
//        // 倒序排列
//        Collections.reverse(blockList);
        return blockList;
    }

    /**
     * 获得区块链的长度
     */
    public int getBlockListSize(){
        return blockDAO.getBlockListSize();
    }

}
