/**
 * <h3>WebsocketServer</h3>
 * <p></p>
 *
 * @author : TimVan
 * @date : 2020-03-14 18:11
 **/
public class TextSocketClient {// Socket客户端类


    public static void main(String[] args) {
        String ip = "localhost";
        int port = 5200;

        try {
            Client client =  new Client();
            client.startClient(ip, port);
        } catch (Exception e) {
            System.out.println("测试客户端连接出错：" + e.getMessage());
        }
    }
}