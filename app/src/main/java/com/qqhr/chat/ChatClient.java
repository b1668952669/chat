package com.qqhr.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by asus on 2017/3/16.
 */

public class ChatClient {
    public Socket requestSocket(String host) throws UnknownHostException,IOException{
        Socket socket=new Socket(host,5000);
        return socket;
    }

    public void sendMsg(Socket socket,String msg)throws IOException{
        BufferedWriter writer=new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())
        );
        writer.write(msg.replace("\n","")+"\n");
        writer.flush();
    }

    public String receiveMsg(Socket socket)throws  IOException{
        BufferedReader reader=new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );
        String txt=reader.readLine();
        return txt;
    }
}
