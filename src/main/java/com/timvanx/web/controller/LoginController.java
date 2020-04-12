package com.timvanx.web.controller;

import com.alibaba.fastjson.JSON;
import com.timvanx.blockchain.model.ECKey;
import com.timvanx.gossip.model.NodeURI;
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

        System.out.println("publicKey="+publicKey);
        System.out.println("privateKey="+privateKey);
        try {

            if (!ECKey.isKeyMatch(publicKey, privateKey)) {
                mjs.put("code", 1);
                mjs.put("msg", "公私钥验证失败");
            } else {
                NodeURI uri = null;

                if("1".equals(uriType)){
                    uri = new NodeURI("udp://localhost:5400", "0");
                }
                else if("2".equals(uriType)){
                    uri = new NodeURI("udp://localhost:5402", "2");
                }
                else if("3".equals(uriType)){
                    uri = new NodeURI("udp://localhost:5403", "3");
                }
                System.out.println("test");
                NodeURI seedNode = new NodeURI("udp://localhost:5400", "0");
                LightDiskHungrySingleton.initial(uri,seedNode,publicKey,privateKey);

                genKeyMap(publicKey, privateKey, mjs);

                HttpSession session = request.getSession();
                session.setAttribute("publicKey", publicKey);
                session.setAttribute("privateKey", privateKey);

            }
        } catch (Exception e) {
            mjs.put("code", 1);
            mjs.put("msg", "错误："+e.getMessage());
        }

        // 把数据转化为json格式
        String json = JSON.toJSONString(mjs);
        response.getWriter().write(json);
    }

    static void genKeyMap(String publicKey, String privateKey, Map<String, Object> mjs) {
        IndexController.genMap(mjs, publicKey, privateKey);
    }
}
