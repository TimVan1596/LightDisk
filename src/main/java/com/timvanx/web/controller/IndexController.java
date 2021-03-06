package com.timvanx.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.timvanx.lightdisk.LightDisk;
import com.timvanx.web.config.LightDiskHungrySingleton;
import com.timvanx.web.config.ReqContants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <h3>BlockChain</h3>
 * <p>主页</p>
 *
 * @author : TimVan
 * @date : 2020-05-02 09:42
 **/
@Controller
@RequestMapping(value = ReqContants.REQ_INDEX)
public class IndexController {
    /**
     * 获得钱包信息 /common/GetWallet
     */
    @PostMapping(ReqContants.REQ_GET_WALLTE)
    public void GetWallet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // get解决中文乱码
        response.setContentType("application/text; charset=utf-8");
        HttpSession session = request.getSession();
        Map<String, Object> mjs = new LinkedHashMap<>();


        if (session != null) {


            if (!(session.getAttribute("publicKey") == null)) {

                String publickey = (String) session.getAttribute("publicKey");
                String privatekey = (String) session.getAttribute("privateKey");

//                System.out.println("in session");
//                System.out.println("publickey="+publickey);
//                System.out.println("privatekey="+privatekey);

                genMap(mjs, publickey, privatekey);
            } else {
                mjs.put("code", 1);
                mjs.put("msg", "登录信息失效");
            }


        } else {
            mjs.put("code", 1);
            mjs.put("msg", "获取失败");
        }

        // 把数据转化为json格式
        String json = JSON.toJSONString(mjs);
        response.getWriter().write(json);
    }

    /**
     * 获得IP地址 /common/GetIP
     */
    @PostMapping(ReqContants.REQ_GET_IP_ADDRESS)
    public void getIP(HttpServletRequest request, HttpServletResponse response)throws IOException{
        // get解决中文乱码
        response.setContentType("application/text; charset=utf-8");
        Map<String, Object> mjs = new LinkedHashMap<>();

        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();
        if (ObjectUtil.isNull(lightDisk)) {
            mjs.put("code", 1);
            mjs.put("msg", "获取失败,LightDisk实例异常");
        } else {
            mjs.put("code", 0);
            mjs.put("msg", "");
            Map<String, Object> uriMap = new LinkedHashMap<>();
            uriMap.put("URI",lightDisk.getUri().getIpAddress());
            uriMap.put("id",lightDisk.getUri().getId());
            uriMap.put("SeedNode",lightDisk.getSeedNode().getIpAddress());
            uriMap.put("SeedNodeID",lightDisk.getSeedNode().getId());
            mjs.put("data", uriMap);
        }

        // 把数据转化为json格式
        String json = JSON.toJSONString(mjs);
        response.getWriter().write(json);
    }




    static void genMap(Map<String, Object> mjs, String publickey, String privatekey) {
        mjs.put("code", 0);
        mjs.put("msg", "成功");
        Map<String, String> data = new LinkedHashMap<>();
        data.put("publickey",publickey);
        data.put("privatekey",privatekey);
        mjs.put("data", data);
    }

}
