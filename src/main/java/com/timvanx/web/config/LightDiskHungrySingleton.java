package com.timvanx.web.config;

import com.timvanx.gossip.model.NodeURI;
import com.timvanx.lightdisk.LightDisk;

/**
 * <h3>BlockChain</h3>
 * <p>LaughDisk实例的饿汉式单例模式</p>
 *
 * @author : TimVan
 * @date : 2020-05-02 08:05
 **/
public class LightDiskHungrySingleton {
    private static final LightDiskHungrySingleton LAUGH_DISK_SINGLETON = new LightDiskHungrySingleton();
    private static LightDisk lightDisk;

    private LightDiskHungrySingleton() {
    }

    public static void initial(NodeURI uri,NodeURI seedNode, String publicKey,String privateKey){
        lightDisk = new LightDisk(uri, seedNode);

    }

    public static LightDiskHungrySingleton getInstance() {
        return LAUGH_DISK_SINGLETON;
    }
}
