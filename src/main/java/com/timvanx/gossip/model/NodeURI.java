package com.timvanx.gossip.model;

import java.net.InetAddress;

/**
 * <h3>BlockChain</h3>
 * <p>IP地址和端口号的实体类</p>
 *
 * @author : TimVan
 * @date : 2020-04-01 15:10
 **/
public class NodeURI {
    private String ipAddress;
    private String id;

    public NodeURI(String ipAddress, String id) {
        this.ipAddress = ipAddress;
        this.id = id;
    }


    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static void getLocalIP() {
        // TODO Auto-generated method stub
        InetAddress ia=null;
        try {
            ia= InetAddress.getLocalHost();

            String localname=ia.getHostName();
            String localip=ia.getHostAddress();
            System.out.println("本机名称是："+ localname);
            System.out.println("本机的ip是 ："+localip);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        getLocalIP();
    }


    @Override
    public String toString() {
        return "NodeURI{" +
                "ip='" + ipAddress + '\'' +
                ", port='" + id + '\'' +
                '}';
    }
}
