package com.timvanx.web.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.timvanx.web.config.ReqContants;
import com.timvanx.web.service.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * <h3>BlockChain</h3>
 * <p>交易列表相关控制器</p>
 *
 * @author : TimVan
 * @date : 2020-05-03 12:45
 **/
@Controller
@RequestMapping(value = ReqContants.REQ_TRANSACTION)
public class TransactionController {

    /**
     * 构造器注入
     */
    final TransactionServiceImpl transactionService;

    @Autowired
    public TransactionController(TransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * 获得交易列表列表 /transaction/GetTransactionList
     */
    @PostMapping(ReqContants.REQ_TRANSACTION_GET_TRANSACTION_LIST)
    public void GetBlockList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // get解决中文乱码
        response.setContentType("application/text; charset=utf-8");

        //page: 2  limit: 10
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        String publicKey = (String)(request.getParameter("publicKey"));

        Map<String, Object> mjs = null;
        if(StrUtil.hasEmpty(publicKey)){
            mjs = transactionService.selectTransaction(page,limit);
        }else{
            publicKey = publicKey.trim();
            mjs = transactionService.selectTransaction(publicKey,page,limit);
        }


        // 把数据转化为json格式
        String json = JSON.toJSONString(mjs);
        response.getWriter().write(json);
    }


}
