package com.timvanx.web.service;

import com.timvanx.web.dao.TransactionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <h3>BlockChain</h3>
 * <p>交易列表服务层</p>
 *
 * @author : TimVan
 * @date : 2020-05-03 12:09
 **/
@Service
public class TransactionServiceImpl {

    /**构造器注入*/
    final TransactionDAO transactionDAO;

    @Autowired
    public TransactionServiceImpl(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    /**
     * 获取分页的本地交易列表
     * @param page 当前页
     * @param limit 每页显示的条数
     */
    public Map<String, Object> selectTransaction(int page, int limit){

        return transactionDAO.selectTransaction(page,limit);
    }


    /**
     * 通过公钥，获取分页的本地交易列表
     * @param publicKey 公钥
     * @param page 当前页
     * @param limit 每页显示的条数
     */
    public Map<String, Object> selectTransaction(String publicKey,int page, int limit){

        return transactionDAO.selectTransaction(publicKey,page,limit);
    }



}
