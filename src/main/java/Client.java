
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class  Client{
    int port = 5200;


    public  void startClient(String ip ,int port) throws IOException {
        //创建连接指定Ip和端口的socket
        Socket socket = new Socket(ip,port);
        //获取系统标准输入流
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //创建一个线程用于读取服务器的信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        System.out.println(in.readLine());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //写信息给客户端

        String  line = reader.readLine();
        while (!"end".equalsIgnoreCase(line)){
            //将从键盘获取的信息给到服务器
            out.println(line);
            out.flush();
            //显示输入的信息
            line = reader.readLine();
        }
        out.close();
        in.close();
        socket.close();

    }
}
