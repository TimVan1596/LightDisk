package com.timvanx;

import java.io.IOException;

/**
 * <h3>BlockChain</h3>
 * <p>启动类</p>
 *
 * @author : TimVan
 * @date : 2020-03-27 20:43
 **/
public class BlockChainStarter {
    public static void main(String[] args) throws IOException {
        String ip = "192.168.0.103";
        int port = 5401;
        String[] split = ip.split("\\.");
        String id = split[3]+port;

        System.out.println("id="+id);

    }
}
