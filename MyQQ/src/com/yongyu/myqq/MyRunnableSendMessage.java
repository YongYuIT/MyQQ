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
            // ���Դ������
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

                // ��������׽��ֹرգ��˳��������
                if (e.getMessage().toLowerCase().trim()
                        .equals(("Socket is closed").toLowerCase().trim()))
                    break;
            } catch (Exception e0)
            {

                // �����������
                continue;
            }
        }
        try
        {
            // ������socket�Ͻ��������루�������һ������close�����رգ������socket�رա���������socket����������룩��Ҳ��������
            // ����socket.shutdownOutputStream()ֻ�ᵥ����ر�����������ᵼ��socket�رգ���ʱ����socket�����������ǿ��õ�
            socket.shutdownOutput();

        } catch (IOException e)
        {

        }
    }

    private boolean isConnected()
    {
        // ע�⣬���׽��ֱ���У�����Ҫʱ��ע�����������״̬�����жϣ�java�е�Socket�ṩ��isClosed(),isConnected(),isInputShutdown(),sOutputShutdown()�������ڼ�鱾��Socket״̬�ģ��������ڼ��Զ��Socket״̬�ģ���������Ч�ĵġ�
        // Ҫ���Զ��Socket��״̬�����ã�
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
