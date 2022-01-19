/**
 * @file: Databus from/to server
 * 
 * @author: Kay Qiang
 * @author: qiangkj@cmu.edu
 */
package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;

public class Connection {

    public static Socket socket;
    public static HashMap<Integer, LinkedList<String>> recvBuffer = new HashMap<Integer, LinkedList<String>>();
    public static PrintWriter pw;
    public static BufferedReader is;

    private static Menu menu;
    private static GameLogic gameLogic;

    public static Thread heartbeatThread;
    public static Thread recvThread;
    public static boolean stopThread;

    public static boolean host = false;

    public static boolean mutex = true;

    public Connection(){
        // NOOP because this is static class
    }

    public static void setMenu(Menu menu) {
        Connection.menu = menu;
    }

    public static void setGame(GameLogic game) {
        Connection.gameLogic = game;
    }

    public static void putMessage(String msg) {
        while(!mutex) {
            try{
                Thread.sleep(100);
            } catch (InterruptedException e) {
                continue;
            }
        }
        mutex = false;
        pw.println(msg);
        pw.flush();
        mutex =  true;
    }

    public static String getMessage(int item) {
        while((!recvBuffer.containsKey(item)) || (recvBuffer.get(item).size() == 0)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return recvBuffer.get(item).poll();
    }

    public static int setSocket(Socket socket) {
        Connection.socket = socket;
        stopThread = false;
        heartbeatThread = new Thread() {
            public void run() {
                Connection.sendHeartbeat();
            }
        };

        recvThread = new Thread(){
            public void run() {
                Connection.recv();
            }
        };
        try{
            pw = new PrintWriter(socket.getOutputStream());
            is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        } 
        return 0;
    }

    public static void recv() {
        while(true) {
            if(stopThread)
                break;
            try{
                String buffer = is.readLine();
                if(buffer == null || buffer.length() == 0) {
                    continue;
                }
                int tag = Integer.valueOf(buffer.split(" ")[0]);
                if(recvBuffer.containsKey(tag)) {
                    recvBuffer.get(tag).add(buffer);
                } else {
                    recvBuffer.put(tag, new LinkedList<String>());
                    recvBuffer.get(tag).add(buffer);
                }
                HandleMessage(buffer);
            } catch(IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    private static void HandleMessage(String msg) {
        int tag = Integer.valueOf(msg.split(" ")[0]);

        switch(tag) {
            case 0:
                break;
            case 2:
                menu.startMultiplay();
                break;
            case 5:
                stopThread = true;
                System.out.println("Connection lost");
                gameLogic.gameEnd();
                break;
            default:
                break;
        }
    }

    public static void sendHeartbeat() {
        while(true){
            if(stopThread)
                break;
            try{
                Thread.sleep(1000);
                putMessage("0 OK \r\n");
            } catch (InterruptedException e) {
                continue;
            }
        }
    }


}
