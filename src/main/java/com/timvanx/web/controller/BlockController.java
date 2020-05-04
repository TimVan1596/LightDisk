package com.timvanx.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.timvanx.blockchain.model.Block;
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

    /**构造器注入*/
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
        if(ObjectUtil.isNull(blockList)){
            mjs.put("count",0);
        }else{
            mjs.put("count", blockList.size());

        }


        // 把数据转化为json格式
        String json = JSON.toJSONString(mjs);
        response.getWriter().write(json);
    }

    /**
     * 获得区块列表 /block/mineBlock
     */
    @PostMapping(ReqContants.REQ_BLOCK_MINE_BLOCK)
    public void mineBlock(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // get解决中文乱码
        response.setContentType("application/text; charset=utf-8");
        Map<String, Object> mjs = new LinkedHashMap<>();
        String data = request.getParameter("data");


        HttpSession session = request.getSession();
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();

        if(ObjectUtil.isNull(session) ){
            mjs.put("code", 1);
            mjs.put("msg", "获取失败,session失效");
        }
        else if(ObjectUtil.isNull(lightDisk)){
            mjs.put("code", 2);
            mjs.put("msg", "获取失败,LightDisk实例异常");
        }
        else{
            mjs.put("code", 0);

            String publickey = (String) session.getAttribute("publicKey");
            Block block = lightDisk.mineBlock(publickey,data);
            mjs.put("msg", "成功");
            mjs.put("data", block);
        }



        // 把数据转化为json格式
        String json = JSON.toJSONString(mjs);
        response.getWriter().write(json);
    }
}
