package com.dsi.ant.antplus.pluginsampler.datatransfer;

import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.Socket;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import static com.dsi.ant.antplus.pluginsampler.datatransfer.Controller.STATUS_ROK;

/**
 * Created by Jugo on 2018/7/5
 */
public class WheelPiesClient
{
    private final String WHEELPIES_IP = "52.69.163.30";
    private final int WHEELPIES_PORT = 2401;
    private final int nConnectTimeOut = 5000; // Socket Connect Timeout
    private final int nReceiveTimeOut = 5000; // Socket Read IO Timeout
    private final int SOCKET_CONNECT_SUCCESS = 0;
    static public Handler ParentHandler = null;
    private Socket socket = null;
    
    public WheelPiesClient()
    {
    }
    
    public void start(Handler phandler)
    {
        if (null == phandler)
        {
            ParentHandler = phandler;
        }
        else
        {
            ParentHandler = handler;
        }
        stop();
        socket = new Socket();
        
        Logs.showTrace("[WheelPiesClient] start Socket Created");
        Thread thread = new Thread(new SocketConnect(socket, ParentHandler));
        thread.start();
        
    }
    
    
    public void stop()
    {
        if (Controller.validSocket(socket))
        {
            try
            {
                socket.close();
                socket = null;
            }
            catch (Exception e)
            {
                Logs.showError("Socket Close Exception: " + e.getMessage());
            }
        }
    }
    
    public void send(JSONObject jsonObject)
    {
        send(jsonObject, handler);
    }
    
    public void send(JSONObject jsonObject, Handler sendHandler)
    {
        if (Controller.validSocket(socket))
        {
            Thread thread = new Thread(new SocketSend(socket, sendHandler, jsonObject.toString()));
            thread.start();
        }
    }
    
    private class SocketConnect implements Runnable
    {
        private Socket theSocket = null;
        private Handler theHandler = null;
        
        SocketConnect(Socket socket, Handler handler)
        {
            theSocket = socket;
            theHandler = handler;
        }
        
        @Override
        public void run()
        {
            try
            {
                theSocket.connect(new InetSocketAddress(WHEELPIES_IP, WHEELPIES_PORT), nConnectTimeOut);
                theSocket.setSoTimeout(nReceiveTimeOut);
                theHandler.sendEmptyMessage(SOCKET_CONNECT_SUCCESS);
                Logs.showTrace("[WheelPiesClient] SocketConnect : " + theSocket.isConnected());
            }
            catch (Exception e)
            {
                Logs.showError("SocketConnect Exception: " + e.toString());
            }
        }
    }
    
    private class SocketSend implements Runnable
    {
        private Socket theSocket = null;
        private Handler theHandler = null;
        private String theData = null;
        
        SocketSend(Socket socket, Handler handler, String strData)
        {
            theSocket = socket;
            theHandler = handler;
            theData = strData;
        }
        
        @Override
        public void run()
        {
            int nRespon = STATUS_ROK;
            try
            {
                if (theSocket.isConnected())
                {
                    Controller.CMP_PACKET respPacket = new Controller.CMP_PACKET();
                    nRespon = Controller.cmpRequest(Controller.wheelpies_request, theData, respPacket,
                            theSocket);
                    if (null != theHandler)
                    {
                        theHandler.sendEmptyMessage(999);
                    }
                    else
                    {
                        Logs.showTrace("[WheelPiesClient] SocketSend invalid handler");
                    }
                    
                    Logs.showTrace("[WheelPiesClient] SocketSend Response Code: " + nRespon);
                }
                else
                {
                    Logs.showError("[WheelPiesClient] SocketSend Socket is not connect");
                }
            }
            catch (Exception e)
            {
                Logs.showError("SocketSend Exception: " + e.toString());
            }
        }
    }
    
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case SOCKET_CONNECT_SUCCESS:
                    Logs.showTrace("Socket Connect Success");
//                    JSONObject jsonObject = new JSONObject();
//                    try
//                    {
//                        jsonObject.put("active", "xxxxxxxxxxxxxxxxx");
//                    }
//                    catch (Exception e)
//                    {
//                        Logs.showError("JSONObject Exception: " + e.toString());
//                    }
//
//                    send(jsonObject);
                    break;
            }
        }
    };
}
