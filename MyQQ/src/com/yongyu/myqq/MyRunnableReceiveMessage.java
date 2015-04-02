package com.yongyu.myqq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class MyRunnableReceiveMessage implements Runnable
{
    private Socket      socket;
    private IGetMessage iget;
    private String      message = "";

    public MyRunnableReceiveMessage(Socket _socket, IGetMessage _get)
    {
        this.socket = _socket;
        this.iget = _get;
    }

    @Override
    public void run()
    {
        InputStream in = null;
        if (!isConnected())
            return;
        try
        {
            // 尝试打开输入流
            in = this.socket.getInputStream();
        } catch (IOException e1)
        {
            return;
        }
        while (true)
        {
            try
            {
                if (!isConnected())
                {
                    break;
                }
                // 读取消息-------------------------
                InputStreamReader inReader = new InputStreamReader(
                        socket.getInputStream());
                BufferedReader reader = new BufferedReader(inReader);
                message = reader.readLine();
                // --------------------------------
                // 直接在异步线程里更新UI会导致：Only the original thread that created a view
                // hierarchy can touch its views.
                if (message == null)
                    continue;
                this.iget.onGetMessage(message);
                message = "";
            } catch (SocketException e)
            {
                // 如果本地套接字关闭，退出输入尝试
                if (e.getMessage().toLowerCase().trim()
                        .equals(("Socket is closed").toLowerCase().trim()))
                    break;
            } catch (Exception e0)
            {
                // 继续输入尝试
                continue;
            }
        }
        try
        {
            socket.shutdownOutput();
        } catch (IOException e)
        {
        }
    }

    private boolean isConnected()
    {
        // 注意，在套接字编程中，我们要时刻注意对网络连接状态进行判断，java中的Socket提供的isClosed(),isConnected(),isInputShutdown(),sOutputShutdown()都是用于检查本地Socket状态的，不是用于检查远程Socket状态的，所以是无效的的。
        // 要检查远程Socket的状态可以用：
        try
        {
            socket.sendUrgentData(0xFF);
            return true;
        } catch (Exception e)
        {
            return false;
        }

    }

}
