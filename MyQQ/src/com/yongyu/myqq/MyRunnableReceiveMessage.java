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
            // ���Դ�������
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
                // ��ȡ��Ϣ-------------------------
                InputStreamReader inReader = new InputStreamReader(
                        socket.getInputStream());
                BufferedReader reader = new BufferedReader(inReader);
                message = reader.readLine();
                // --------------------------------
                // ֱ�����첽�߳������UI�ᵼ�£�Only the original thread that created a view
                // hierarchy can touch its views.
                if (message == null)
                    continue;
                this.iget.onGetMessage(message);
                message = "";
            } catch (SocketException e)
            {
                // ��������׽��ֹرգ��˳����볢��
                if (e.getMessage().toLowerCase().trim()
                        .equals(("Socket is closed").toLowerCase().trim()))
                    break;
            } catch (Exception e0)
            {
                // �������볢��
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
