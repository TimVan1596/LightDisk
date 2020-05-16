package com.timvanx.web.config;

import cn.hutool.core.util.StrUtil;
import com.timvanx.gossip.GossipCommunicateLayer;
import com.timvanx.lightdisk.LightDisk;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * <h3>BlockChain</h3>
 * <p>登录拦截</p>
 *
 * @author : TimVan
 * @date : 2020-05-16 19:07
 **/
public class LoginHandlerInterceptor implements HandlerInterceptor {

    /**
     * 目标方法执行之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();

        String publickey = (String) session.getAttribute("publicKey");

        if (StrUtil.hasEmpty(publickey)) {
            return true;
        } else if (publickey.length() < 2) {

            session.setAttribute("publicKey", "");
            session.setAttribute("privateKey", "");

            try {
                LightDisk lightDisk =
                        LightDiskHungrySingleton.getLightDisk();
                GossipCommunicateLayer gossip = lightDisk.getGossip();
                //关闭lightDisk
                lightDisk.shutDown();
            } catch (Exception e) {
                e.printStackTrace();
            }

            request.getRequestDispatcher("/index.html").forward(request, response);
            return false;
        } else {
            // 已登录，放行
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
