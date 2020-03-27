import Gossip.StandAloneNodeCrdtOrSet;

import java.io.IOException;
import java.util.Scanner;

/**
 * <h3>BlockChain</h3>
 * <p>启动类</p>
 *
 * @author : TimVan
 * @date : 2020-03-27 20:43
 **/
public class BlockChainStarter {
    public static void main(String[] args) throws IOException {


        //    udp://localhost:5400 0 udp://localhost:10000 0
//netstat -aon|findstr "5400"
// tasklist|findstr "2720"

        String[] paraURL1 = {"udp://localhost:5400","0","udp://localhost:5400","0"};
        String[] paraURL2 = {"udp://localhost:5401","1","udp://localhost:5400","0"};
        String[] paraURL3 = {"udp://localhost:5402","2","udp://localhost:5400","0"};
        String[] paraURL4 = {"udp://localhost:5403","3","udp://localhost:5400","0"};

        System.out.println("StandAloneNodeCrdtOrSet - 请输入启动节点列表编号");
        Scanner scanner = new Scanner(System.in);
        String[] paraURL = null;
        int optionNum = scanner.nextInt();
        switch (optionNum){
            case 1:{
                paraURL = paraURL1;
                break;
            }
            case 2:{
                paraURL = paraURL2;
                break;
            }
            case 3:{
                paraURL = paraURL3;
                break;
            }
            default:{
                paraURL = paraURL4;

            }
        }
        System.out.println(String.format("%d号节点开始运行 -- port = %s , id = %s"
                , optionNum,paraURL[0].substring(paraURL[0].length()-4,paraURL[0].length()),paraURL[1]));


        StandAloneNodeCrdtOrSet example = new StandAloneNodeCrdtOrSet(paraURL);
        boolean willRead = true;
        example.exec(willRead);

    }
}
