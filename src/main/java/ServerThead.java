/**
 * <h3>WebsocketServer</h3>
 * <p></p>
 *
 * @author : TimVan
 * @date : 2020-03-14 21:50
 **/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 服务器线程，主要来处理多个客户端的请求
 */
public class ServerThead extends Server implements Runnable {

    Socket socket;
    String socketName;
    int port = 5200;

    public ServerThead(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //设置该客户端的端点地址
            socketName = socket.getRemoteSocketAddress().toString();
            System.out.println("Client@" + socketName + "已加入聊天");
            print("Client@" + socketName + "已加入聊天");
            boolean flag = true;
            while (flag) {
                //阻塞，等待该客户端的输出流
                String line = reader.readLine();
                //若客户端退出，则退出连接。
                if (line == null) {
                    flag = false;
                    continue;
                }

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String dateStr = sdf.format(date);
                String msg = "【客户端@" + socketName +"】 "+dateStr+ "\n \t"+ line;
                System.out.println(msg);
                //向在线客户端输出信息
                print(msg);
            }

            closeConnect();
        } catch (IOException e) {
            try {
                closeConnect();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 向所有在线客户端socket转发消息
     *
     * @param msg
     * @throws IOException
     */
    private void print(String msg) throws IOException {
        PrintWriter out = null;
        synchronized (sockets) {
            for (Socket sc : sockets) {
                out = new PrintWriter(sc.getOutputStream());
                out.println(msg);
                out.flush();
            }
        }
    }

    /**
     * 关闭该socket的连接
     *
     * @throws IOException
     */
    public void closeConnect() throws IOException {
        System.out.println("Client@" + socketName + "已退出聊天");
        print("Client@" + socketName + "已退出聊天");
        //移除没连接上的客户端
        synchronized (sockets) {
            sockets.remove(socket);
        }
        socket.close();
    }
}
