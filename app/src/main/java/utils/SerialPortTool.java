package utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import android_serialport_api.SerialPort;


/**
 * Author by Xiaowei,2019.5.21
 * SerialPortUtils类
 * 提供串口功能性调用;
 *      打开
 *      关闭
 *      发送
 *      接收
 *          接收Len个字节
 *          接收全部
 * */
public class SerialPortTool {

    private String TAG = "SerialPortUtils1";

    //串口设置;
    private String path = "";
    private int baudrate = 0;

    //SerialPort类,由google提供的SDK.
    private SerialPort serialPort = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    //串口状态
    //ture means SerialPort is Open;
    private boolean serialStat = false;


    //串口接收数据保存在队列中
    private Queue<Byte> RecvByte=new LinkedList<Byte>();

    //读取串口数据的线程.
    private ReadThread mReadThread;
    //线程状态,
    private  boolean isStop=false;


    public SerialPortTool(String path, int baudrate) {
        this.path = path;
        this.baudrate = baudrate;
    }

    /**
     * 检测串口是否已经打开,
     * @return ture,打开;false,关闭;
     * */
    public boolean isSerialPortOpen()
    {
        return serialStat;
    }
    /**
     * 打开串口
     *
     * @return
     */
    public void openSerialPort()  {
        //如果串口是关闭的.
        if(this.serialStat==false)
        {
            try {
                //完善,+path和baudrate检测;
                serialPort = new SerialPort(new File(path), baudrate, 0);
                if (serialPort != null)
                {
                    this.serialStat = true;
                    inputStream = serialPort.getInputStream();
                    outputStream = serialPort.getOutputStream();
                    //开启线程监听串口数据.
                    isStop=false;
                    mReadThread=new ReadThread();
                    mReadThread.start();
                }
                else
                    Log.d(TAG, "openSerialPort: 打开串口失败.");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭串口
     *
     * */
    public void closeSerialPort()
    {
        try
        {
            //关闭监听线程.
            if(mReadThread!=null)
            {
                mReadThread.interrupt();
                isStop=true;
            }

            //串口状态是打开;
            if(this.serialStat==true)
            {

                inputStream.close();
                outputStream.close();
                this.serialStat=false;
                serialPort.close();
                Log.d(TAG, "closeSerialPort: 串口关闭成功");
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "closeSerialPort: 串口关闭异常");
            e.printStackTrace();
        }
    }

    /**
     * 发送byte数组
     * @param arr 要发送的数据,byte类型
     * */
    public void dataSend(byte[] arr)
    {
        try
        {
            if(arr.length>0)
                outputStream.write(arr);
            outputStream.flush();
            Log.d(TAG, "dataSend: 串口发送数据,成功");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取串口接收的数据,从队列中提取len个字节数据.
     * @param len:需要提取的字节数.
     * */
    public byte[] getRecvData(int len)
    {
        byte[] arr=new byte[len];
        for(int i=0;i<len;i++)
        {
            if(!RecvByte.isEmpty())
                arr[i]=RecvByte.remove();
        }
        return arr;
    }
    /**
     * 获取串口队列中全部数据;
     * @return 串口接收全部数据;
     * */
    public byte[] getAllRecvData()
    {
        List<Byte> arr=new ArrayList<Byte>();
        //将数据从队列取出,放入List<Byte>
        while(!RecvByte.isEmpty())
        {
            arr.add(RecvByte.remove());
        }
        //将数据拷贝到byte[]数组;
        byte[] temp=new byte[arr.size()];
        for(int i=0;i<arr.size();i++)
        {
            temp[i]=arr.get(i);
        }
        return temp;
    }

    /**
     * 读取串口数据线程,接收数据,存储在RecvByte中.
     * RecvByte:队列<byte>
     *
     * */
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isStop && !isInterrupted())
            {
                int size=0;     //读取流中字节数;
                byte[] tempArr=new byte[64];    //临时存放读取流中的数据;
                try {
                    //文件流中有数据.
                    if((size=inputStream.available())>0)
                    {
                        size=inputStream.read(tempArr);

                        //将数据存储在队列中;
                        for(int i=0;i<size;i++)
                        {
                            RecvByte.add(tempArr[i]);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}