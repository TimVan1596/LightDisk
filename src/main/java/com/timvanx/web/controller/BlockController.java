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

        mjs.put("code", 0);
        mjs.put("msg", "成功");
        List<Block> blockList = blockService.selectLocalBlock();
        mjs.put("data", blockList);
        if (ObjectUtil.isNull(blockList)) {
            mjs.put("count", 0);
        } else {
            mjs.put("count", blockList.size());

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
                //存入加密字节数组的字符串形式
                Block block = lightDisk.mineBlock(publickey, new String(encryptData));
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
        String data = request.getParameter("data");


        // 把数据转化为json格式
        String json = JSON.toJSONString(mjs);
        response.getWriter().write(json);
    }

    public static void main(String[] args) {
        String dataStr = "美元指数延续弱势 在岸人民币收报7.0937贬值51点";
        String publickey = "aSq9DsNNvGhYxYyqA9wd2eduEAZ5AXWgJTbTGV7wVRjRm4zioR31ecXwi9xLmhkrcdzm2EJC68XCyz1VPPpezTW6cYaTmSG8zGkrAjtk7VHqpgF5roaU61nS1a64";
        try {
            byte[] encryptData = ECKey.encryptByPublicKey(dataStr.getBytes()
                    , publickey);
            System.out.println(new String(encryptData));
        } catch (Exception e) {
            System.out.println("异常");
            e.printStackTrace();
        }
    }
}
