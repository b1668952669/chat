package com.qqhr.chat;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer extends Thread {
    public   ChatServer() throws IOException {
        createSocket();
    }
    public void run(){
        Socket client;
        String txt;
        Log.i("信息","成功开始服务器，端口为："+PORT);
        try{
            while (true){
                client=ResponseSocket();
                while(true){
                    txt=ReceiveMsg(client);
                    System.out.println(txt);
                    Message message1 = new Message();
                    message1.obj =txt+"\n";
                    com.qqhr.chat.MainActivity.handler.sendMessage(message1);
                    if(true)
                        break;
                }
//                CloseSocket(client);
            }
        }catch (IOException e){

        }
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {

        };
    };
    public  ServerSocket server=null;
    private   int PORT =5000;
    private BufferedWriter writer;
    private BufferedReader reader;

    public void createSocket() throws IOException{
        server=new ServerSocket(PORT,100);
    }

    public Socket ResponseSocket() throws IOException{
        Socket client =server.accept();
        return client;
    }

    public void CloseSocket(Socket socket) throws IOException {
        reader.close();
        socket.close();
        server.close();
    }

    public void sendMsg(Socket socket,String string)throws IOException{
        writer=new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())
        );
        writer.write("msg"+string);
        writer.flush();
    }

    public String ReceiveMsg(Socket socket) throws IOException{
        reader=new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );
        String txt ="【对方】"+reader.readLine();
        return txt;
    }

}