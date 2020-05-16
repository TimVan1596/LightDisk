package com.timvanx.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.timvanx.blockchain.model.ECKey;
import com.timvanx.gossip.GossipCommunicateLayer;
import com.timvanx.gossip.model.NodeURI;
import com.timvanx.lightdisk.LightDisk;
import com.timvanx.web.config.LightDiskHungrySingleton;
import com.timvanx.web.config.ReqContants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <h3>BlockChain</h3>
 * <p>主页控制器</p>
 *
 * @author : TimVan
 * @date : 2020-05-01 22:49
 **/
@Controller
public class LoginController {

    /**
     * 默认欢迎页 page文件夹下的login-1.html
     */
    @RequestMapping("/")
    public void index(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getRequestDispatcher("/page/login-1.html").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭整个系统，回到登录页 /shutdown
     */
    @PostMapping(ReqContants.REQ_SHUTDOWN)
    public void shutdown(HttpServletRequest request, HttpServletResponse response, SessionStatus sessionStatus) throws IOException {
        // get解决中文乱码
        response.setContentType("application/text; charset=utf-8");
        LightDisk lightDisk = LightDiskHungrySingleton.getLightDisk();
        Map<String, Object> mjs = new LinkedHashMap<>();
        //删除session
        HttpSession session = request.getSession();
        session.setAttribute("publicKey", "");
        session.setAttribute("privateKey", "");
        session.invalidate();
        sessionStatus.setComplete();

        try {
            GossipCommunicateLayer gossip = lightDisk.getGossip();
            if (ObjectUtil.isNull(gossip)) {
                mjs.put("code", 1);
                mjs.put("msg", "Gossip为空");
            } else {
                //关闭lightDisk
                lightDisk.shutDown();

                mjs.put("code", 0);
                mjs.put("msg", "获取成功");
            }
        } catch (Exception e) {
            mjs.put("code", 2);
            mjs.put("msg", "LightDisk为空");
            e.printStackTrace();
        }

        // 把数据转化为json格式
        String json = JSON.toJSONString(mjs);
        response.getWriter().write(json);
    }

    /**
     * 登录页 /login
     */
    @PostMapping(ReqContants.REQ_LOGIN)
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // get解决中文乱码
        response.setContentType("application/text; charset=utf-8");

        String publicKey = request.getParameter("userName");
        String privateKey = request.getParameter("passWord");
        String uriType = request.getParameter("uriType");
        Map<String, Object> mjs = new LinkedHashMap<>();

//        System.out.println("publicKey="+publicKey);
//        System.out.println("privateKey="+privateKey);
        try {

            if (!ECKey.isKeyMatch(publicKey, privateKey)) {
                mjs.put("code", 1);
                mjs.put("msg", "公私钥验证失败");
            } else {
                NodeURI uri = null;

                if ("1".equals(uriType)) {
                    uri = new NodeURI("udp://localhost:5400", "0");
                } else if ("2".equals(uriType)) {
                    uri = new NodeURI("udp://localhost:5402", "2");
                } else if ("3".equals(uriType)) {
                    uri = new NodeURI("udp://localhost:5403", "3");
                }
                NodeURI seedNode = new NodeURI("udp://localhost:5400", "0");
                LightDiskHungrySingleton.initial(uri, seedNode, publicKey, privateKey);

                genKeyMap(publicKey, privateKey, mjs);

                HttpSession session = request.getSession();
                session.setAttribute("publicKey", publicKey);
                session.setAttribute("privateKey", privateKey);

            }
        } catch (Exception e) {
            mjs.put("code", 1);
            mjs.put("msg", "错误：" + e.getMessage());
        }

        // 把数据转化为json格式
        String json = JSON.toJSONString(mjs);
        response.getWriter().write(json);
    }

    /**
     * 注册/生成新钱包 /registry
     */
    @PostMapping(ReqContants.REQ_REGISTRY)
    public void registry(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // get解决中文乱码
        response.setContentType("application/text; charset=utf-8");

        Map<String, Object> mjs = new LinkedHashMap<>();

        Map<String, String> keyPairMap = ECKey.genKeyPair();

        String publicKeyStr = keyPairMap.get("publickey");
        String privateKeyStr = keyPairMap.get("privatekey");

        Map<String, Object> dataMap = new LinkedHashMap<>();

        dataMap.put("publicKey", publicKeyStr);
        dataMap.put("privateKey", privateKeyStr);

        mjs.put("code", 0);
        mjs.put("msg", "获取成功");
        mjs.put("data", keyPairMap);

        // 把数据转化为json格式
        String json = JSON.toJSONString(mjs);
        response.getWriter().write(json);
    }

    static void genKeyMap(String publicKey, String privateKey, Map<String, Object> mjs) {
        IndexController.genMap(mjs, publicKey, privateKey);
    }
}
