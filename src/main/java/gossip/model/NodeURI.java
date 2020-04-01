package gossip.model;

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


    @Override
    public String toString() {
        return "NodeURI{" +
                "ip='" + ipAddress + '\'' +
                ", port='" + id + '\'' +
                '}';
    }
}
