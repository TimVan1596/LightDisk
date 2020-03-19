import jgossip.GossipService;

/**
 * <h3>WebsocketServer</h3>
 * <p></p>
 *
 * @author : TimVan
 * @date : 2020-03-19 11:40
 **/
public class TestGossipService {
    public static void main(String[] args) throws Exception {
        GossipService service = new GossipService();
        service.startGossip();
    }
}
