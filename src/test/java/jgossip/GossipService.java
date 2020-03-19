package jgossip;

import net.lvsq.jgossip.core.GossipSettings;
import net.lvsq.jgossip.model.SeedMember;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GossipService {

    @Test
    public void startGossip() throws Exception {
        String cluster = "testcluster";
        String ipAddress = "127.0.0.1";
        int port = 5000;
        List<SeedMember> seedNodes = new ArrayList<>();
        SeedMember seed = new SeedMember();
        seed.setCluster(cluster);
        seed.setIpAddress(ipAddress);
        seed.setPort(port);
        seedNodes.add(seed);

        for (int i = 0; i < 1; i++) {
            net.lvsq.jgossip.core.GossipService gossipService = null;
            try {
                gossipService = new net.lvsq.jgossip.core.GossipService(cluster, ipAddress,
                        port + i, null, seedNodes,
                        new GossipSettings(), (member, state) -> {
                    System.out.println("member:" + member + "  state: " + state);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            gossipService.start();
        }
    }


}