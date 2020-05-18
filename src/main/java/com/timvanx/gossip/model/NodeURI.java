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

    /**
     * @param ip ip地址，如192.168.1.1
     * @param port 端口号
     * @param id id号
     * */
    public NodeURI(String ip,int port, String id) {
        //udp://localhost:5400
        this.ipAddress = "udp://" + ip + ":" + port;
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

    /** 获取本地IP地址 */
    public static String getLocalIP() {
        // TODO Auto-generated method stub
        InetAddress ia=null;
        try {
            ia= InetAddress.getLocalHost();
            return ia.getHostAddress();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "localhost";
    }

    public static void main(String[] args) {

        System.out.println(getLocalIP());
    }


    @Override
    public String toString() {
        return "NodeURI{" +
                "ip='" + ipAddress + '\'' +
                ", port='" + id + '\'' +
                '}';
    }
}
