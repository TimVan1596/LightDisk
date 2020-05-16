package com.timvanx.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.timvanx.blockchain.model.Block;
import com.timvanx.blockchain.model.ECKey;
import com.timvanx.lightdisk.LightDisk;
import com.timvanx.web.config.LightDiskHungrySingleton;
import com.timvanx.web.config.ReqContants;
import com.timvanx.web.dao.BlockDAO;
import com.timvanx.web.service.BlockServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
 **/
@Controller
@RequestMapping(value = ReqContants.REQ_BLOCK)
public class BlockController {

    /**
     * 构造器注入
     */
    final BlockServiceImpl blockService;

    @Autowired
    public BlockController(BlockServiceImpl blockService) {
        this.blockService = blockService;
    }

    /**
     * 获得区块列表 /block/GetBlockList
     */
    @PostMapping(ReqContants.REQ_BLOCK_GET_BLOCK_LIST)
    public void GetBlockList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // get解决中文乱码
        response.setContentType("application/text; charset=utf-8");
        Map<String, Object> mjs = new LinkedHashMap<>();
        //page: 2  limit: 10
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        List<Block> blockList = blockService.selectLocalBlock(page,limit);
        if (ObjectUtil.isNull(blockList)) {
            mjs.put("code", 1);
            mjs.put("msg", "区块列表为空");
            mjs.put("count", 0);
        } else {
            mjs.put("code", 0);
            mjs.put("msg", "成功");
            mjs.put("count", blockService.getBlockListSize());
            mjs.put("data", blockList);
        }

        // 把数据转化为json格式
        String json = JSON.toJSONString(mjs);
        response.getWriter().write(json);
    }

    /**
     * 挖矿 /block/mineBlock
     */
    @PostMapping(ReqContants.REQ_BLOCK_MINE_BLOCK)
    public void mineBlock(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // get解决中文乱码
        response.setContentType("application/text; charset=utf-8");
        Map<String, Object> mjs = new LinkedHashMap<>();
        String dataStr = request.getParameter("data");


        HttpSession session = request.getSession();
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if (ObjectUtil.isNull(session)) {
            mjs.put("code", 1);
            mjs.put("msg", "获取失败,session失效");
        } else if (ObjectUtil.isNull(lightDisk)) {
            mjs.put("code", 2);
            mjs.put("msg", "获取失败,LightDisk实例异常");
        } else {


            String publickey = (String) session.getAttribute("publicKey");

            try {
                byte[] encryptData = ECKey.encryptByPublicKey(dataStr.getBytes()
                        , publickey);
                String encryptString = ECKey.encodeBase58(encryptData);
                //存入加密字节数组的字符串形式
                Block block = lightDisk.mineBlock(publickey, encryptString);
                mjs.put("code", 0);
                mjs.put("msg", "成功");
                mjs.put("data", block);
            } catch (Exception e) {
                mjs.put("code", 3);
                mjs.put("msg", "使用公钥加密文件失败");
                e.printStackTrace();
            }


        }


        // 把数据转化为json格式
        String json = JSON.toJSONString(mjs);
        response.getWriter().write(json);
    }

    /**
     * 测试文件是否能用私钥解码成JSON格式 /block/decodeJsonFromPrivateKey
     */
    @PostMapping(ReqContants.REQ_DECODE_JSON_FROM_PRIVATE_KEY)
    public void decodeJsonFromPrivateKey(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // get解决中文乱码
        response.setContentType("application/text; charset=utf-8");
        Map<String, Object> mjs = new LinkedHashMap<>();
        String privateKey = request.getParameter("privateKey");
        String encrptString = request.getParameter("data");

        try {
            byte[] decryptData = ECKey.decryptByPrivateKey(ECKey.decodeBASE64(encrptString),privateKey);
            mjs.put("code", 0);
            mjs.put("msg", "");
            mjs.put("data", new String(decryptData));
        } catch (Exception e) {
            mjs.put("code", 1);
            mjs.put("msg", "私钥匹配错误，请重试");
            e.printStackTrace();
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
