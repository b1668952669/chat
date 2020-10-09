package com.qqhr.chat;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.Socket;

import static com.qqhr.chat.R.id.receiveMessage;
/*
*基于SOCKET的信息传输APP
* 需要两台设备在同一WIFI下利用IP地址进行信息传输

*/

public class MainActivity extends AppCompatActivity {
    final ChatServer chatServer=null;
    static String text="";
    public static String textIP="";
    static EditText receivemessage=null;
    final ChatClient chatClient = new ChatClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        定义相关的变量
        receivemessage=(EditText) findViewById(receiveMessage);
        final  EditText message=(EditText)findViewById(R.id.message);
        final EditText IP = (EditText)findViewById(R.id.IP);
        final Button sendmessage = (Button)findViewById(R.id.sendMessage);
        final Button myIP =(Button) findViewById(R.id.myIP);
//        获取wifi下的IP地址并显示
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        final int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        myIP.setText(ip);

//      点击实现复制内容到粘贴板
        myIP.setClickable(true);
        myIP.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(myIP.getText().toString());

            }
        });


//        初始化看是服务器
        try{
            ChatServer chatServer=new ChatServer();
            chatServer.start();
        }catch(IOException e){
            Log.e("错误","开启服务器失败");
            e.printStackTrace();
        }
//        点击按钮发送数据给相应的IP地址
        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard(sendmessage);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Socket socket=chatClient.requestSocket(IP.getText().toString());
                            chatClient.sendMsg(socket,"("+intToIp(ipAddress)+"):"+message.getText().toString());

                            Message message1 = new Message();
                            message1.obj ="【自己】"+"("+intToIp(ipAddress)+"):"+message.getText().toString()+"\n";
                            handler.sendMessage(message1);

                        }catch (IOException e){
                            Log.e("错误","发送失败");
                        }
                    }
                }).start();


            }

        });


    }

    //    用于将结果以IP的形式放出
    public String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    public static Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("信息","其他线程发过来："+msg);
            text+=msg.obj.toString();;
            receivemessage.setText(text);
            receivemessage.setSelection(receivemessage.getText().length(), receivemessage.getText().length());
        }
    };


    //隐藏虚拟键盘
    public static void HideKeyboard(View v)
    {
        InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );
        if ( imm.isActive( ) ) {
            imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );

        }
    }


}
