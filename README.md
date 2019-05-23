# 串口工具
加载了google提供的开源库，并将串口进行封装；
SerialPortTool类提供串口的一些功能：
## void openSerialPort()
打开串口;
## void closeSerialPort()
关闭串口;
## void dataSend(byte[] arr)
发送byte[]数组;
## byte[] getRecvData(int len)
接收len个串口字节数据,如果数据不足len个,则用0填充.
##  byte[] getAllRecvData()
获取所有串口数据