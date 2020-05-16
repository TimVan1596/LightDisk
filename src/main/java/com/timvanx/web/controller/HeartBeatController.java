package com.timvanx.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.timvanx.blockchain.model.ECKey;
import com.timvanx.lightdisk.HeartBeat;
import com.timvanx.lightdisk.HeartBeatLog;
import com.timvanx.web.config.ReqContants;
import com.timvanx.web.service.HeartBeatServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <h3>BlockChain</h3>
 * <p>区块相关控制器</p>
 *
 * @author : TimVan
 * @date : 2020-05-03 12:45
 * 心跳消息大类 /heartbeat   */
@Controller
@RequestMapping(value = ReqContants.REQ_HEARTBEAT)
public class HeartBeatController {

    /**
     * 构造器注入
     */
    final HeartBeatServiceImpl heartBeatService;

    @Autowired
    public HeartBeatController(HeartBeatServiceImpl heartBeatService) {
        this.heartBeatService = heartBeatService;
    }

    /**
     * 获得区块列表 /heartbeat/GetHeartbeatList
     */
    @PostMapping(ReqContants.REQ_HEARTBEAT_GET_HEARTBEAT_LIST)
    public void GetBlockList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // get解决中文乱码
        response.setContentType("application/text; charset=utf-8");
        Map<String, Object> mjs = new LinkedHashMap<>();
        //page: 2  limit: 10
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        List<HeartBeatLog> heartBeatList = heartBeatService.selectLocalHeartbeat(page,limit);
        if (ObjectUtil.isNull(heartBeatList)) {
            mjs.put("code", 1);
            mjs.put("msg", "心跳消息列表为空");
            mjs.put("count", 0);
        } else {
            mjs.put("code", 0);
            mjs.put("msg", "成功");
            mjs.put("count", heartBeatService.getHeartbeatListSize());
            mjs.put("data", heartBeatList);
        }

        // 把数据转化为json格式
        String json = JSON.toJSONString(mjs);
        response.getWriter().write(json);
    }


    public static void main(String[] args) {
        String dataStr = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAB9SURBVDhPnZCxEYAgDEXBEezttNF9HNJ9tHIGVlA+wl0CIaKPO0jz+Ens5TE/+Cy6YQpvF+5GkgSIuPvTTpG4WhsrDk0DYqt5ci6BQtz8rpZYa7xuVUoD6lZrEqiKudQfZ6we1EQNcUaalpLcPLLUQpTmogI+AKxVbRkcY240rS8WT1LaOAAAAABJRU5ErkJggg==";
        String publickey = "Vj75CuZgqYqhewfDfF9KQEdejjqbnoDiRjuXUnwFDsa5tP2JN5uKR2nMsM3LPtCcW5QH8J8sTjS9aHVTgUAPMGxvpj5baTwrtYng2XM8ozopQgScinkncaxJLAppao2RQqBFTNzB1RKd9CJNMvHa7FoAJmDrfYiehSj5AfGwAHTy5DjzRiNHKbqWpaje2pKu6knJMiXQVphp5wNpnfnWVVWmD43SQ";
        String privateKey = "2qCXjvtcr4nR8xRX2v7yEntuNKBonfFVT1PWVpAN1Zk9YvEWYr4b4aTexXkg48gExEhU8xBiBPcG5bKgM2jxxmN3h7CiYPP4wAo6UC1JDTmX87yWm6s94YCwYCZ8sFRof5yTGGxgbagdTDrvuaVjAmur48MmaGGJiZNfRsfQuu4nGGkGkJnTVQ7Whj4GnjXDy87Z6WzneuYfCZnN6BLcxn75pe8QHUzJeCVyJSfZ8gasZQM5ikWVVNcKEEKbpUVeGXRFkScfDXPc8NxvWcF71ZdeFdTBagMXdvQtiR1b7iiWGd6b9FoJYP6MRwvRmfMA8cbJ5V2XgN9uJZeXoJnJwguJjSSgLV8NirjUyt6e7piuWkCsK4wVDsEQ3fSPkT7jDfA3cmruCsQ7EWKa3UbeQk7thAUY3XhgA1F7CNNSnAR72qHB48WfgmqZVrQLVEA5LRmq9pWs6zwQNPWkmh8DtHtghY7AhhmcHKthVyxtry88u72xu9jC3Pwiy7tJPEYTAuvxdRS25v8fKAvdEWghTCcP95pqgwVUWStyfPCHofNJNNrEVGWRN7tKet3hSDRwoGo6ywDU7WsifrN976NvBDWCYB6RL7XbjnaGRUtuoEQEZoxP9cVcTfvYYqq4jKmJmuETuvRaMaPLeCbMkLGCoK5uGi5h1J7eZxRE5WegxdRdhjNje3tXwNuHUySXpt4Jzm7DinkH7bmiaTjDd6jyDn5FguWyHenTw3nnBkKEH6DFG5F5aB8JBGLmLhMxYTmVE5hbiKMxteb1gSWshXajEN5h9U8LBGnvTiFKmG3ZMn3twhwsNmhnvvQeAXRzXspcjp6EGB4VKmopfVqjTJWNq7TezKGPWG1fzV9RC";
        try {
            byte[] encryptData = ECKey.encryptByPublicKey(dataStr.getBytes()
                    , publickey);

            String encrptString = ECKey.encodeBase58(encryptData);
            System.out.println("加密字符串："+encrptString);

            byte[] decryptData = ECKey.decryptByPrivateKey(ECKey.decodeBASE64(encrptString),privateKey);
            System.out.println("解密字符串："+new String(decryptData));

        } catch (Exception e) {
            System.out.println("异常");
            e.printStackTrace();
        }
    }
}
