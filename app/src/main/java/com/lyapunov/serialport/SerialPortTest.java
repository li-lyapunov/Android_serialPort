package com.lyapunov.serialport;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;
import utils.ChangeTool;
import utils.SerialPortTool;

public class SerialPortTest extends AppCompatActivity {

    private String TAG="SerialPortTest";

    //串口工具;
    private SerialPortTool serialPortTool=new SerialPortTool("dev/ttySAC3",57600);


    //控件
    private TextView TV_recv;
    private Button Btn_recv;
    private Button Btn_send;
    private Button Btn_close;
    private Button Btn_open;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serialporttest);



        //控件绑定
        TV_recv=(TextView)findViewById(R.id.RecvByte);
        Btn_recv=(Button)findViewById(R.id.Btn_recv);
        Btn_send=(Button)findViewById(R.id.Btn_send);
        Btn_close=(Button)findViewById(R.id.CloseSerial);
        Btn_open=(Button)findViewById(R.id.Btn_open);

        //打开串口;
        serialPortTool.openSerialPort();



        //接收
        Btn_recv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] arr=serialPortTool.getAllRecvData();

                TV_recv.setText(ChangeTool.ByteArrToHex(arr));
                Log.d(TAG, "onClick: 接收数据");
                for(byte x:arr)
                    Log.d(TAG, "x:"+x);
            }
        });
        //发送
        Btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialPortTool.dataSend(new byte[]{1,2,3,4,5,6,7,8,9,10,11,12});
            }
        });
        //关闭
        Btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialPortTool.closeSerialPort();
            }
        });
        //打开
        Btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialPortTool.openSerialPort();
            }
        });


    }
}
