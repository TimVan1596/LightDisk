/**
 * <h3>WebsocketServer</h3>
 * <p></p>
 *
 * @author : TimVan
 * @date : 2020-03-14 18:13
 **/

public class testSocketServer {
    static int port = 5200;

    public static void main(String[] args) {
        try {
            Server server=new Server();
            server.serverStart(port);
        } catch (Exception e) {
            System.out.println("测试服务器端监听出错: "+e.getMessage());
        }
    }
}
