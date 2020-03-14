
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class Server {
    //将接收到的socket变成一个集合
    protected static   List<Socket> sockets = new Vector<>();
    int port = 5200;

    public void serverStart(int port) throws IOException {
        //创建服务端
        ServerSocket server = new ServerSocket(port);
        System.out.println("------- 服务器已启动，监听端口号为：" + port);

        boolean flag = true;
        //接受客户端请求
        while (flag){
            try {
                //阻塞等待客户端的连接
                Socket accept = server.accept();
                synchronized (sockets){
                    sockets.add(accept);
                }
                //多个服务器线程进行对客户端的响应
                Thread thread = new Thread(new ServerThead(accept));
                thread.start();
                //捕获异常。
            }catch (Exception e){
                flag = false;
                e.printStackTrace();
            }
        }
        //关闭服务器
        server.close();
    }

}

//                    Date date = new Date();
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                    String dateStr = sdf.format(date);
//
//                    System.out.println("客户端 "+ dateStr +"\n \t"+ line);