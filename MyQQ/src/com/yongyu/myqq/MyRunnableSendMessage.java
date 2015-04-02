package com.yongyu.myqq;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;

public class MyRunnableSendMessage implements Runnable
{
    private Socket             socket;
    private ArrayBlockingQueue queue;

    public MyRunnableSendMessage(Socket _socket, ArrayBlockingQueue _que)
    {
        this.socket = _socket;
        this.queue = _que;
    }

    @Override
    public void run()
    {
        OutputStream out;
        if (!isConnected())
            return;
        try
        {
            // 尝试打开输出流
            out = this.socket.getOutputStream();
        } catch (IOException e1)
        {
            return;
        }
        while (true)
        {
            try
            {
                String message = (String) queue.take();
                if (!isConnected())
                {
                    break;
                }
                out.write((message).getBytes());

            } catch (SocketException e)
            {

                // 如果本地套接字关闭，退出输出尝试
                if (e.getMessage().toLowerCase().trim()
                        .equals(("Socket is closed").toLowerCase().trim()))
                    break;
            } catch (Exception e0)
            {

                // 继续输出尝试
                continue;
            }
        }
        try
        {
            // 对于在socket上建立的输入（输出）流一旦调用close函数关闭，会造成socket关闭。这样基于socket的输出（输入）流也将不可用
            // 调用socket.shutdownOutputStream()只会单方面关闭输出流，不会导致socket关闭，此时基于socket的输入流还是可用的
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
