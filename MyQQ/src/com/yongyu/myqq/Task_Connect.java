package com.yongyu.myqq;

import java.net.Socket;

import android.os.AsyncTask;
import android.util.Log;

public class Task_Connect extends AsyncTask<Void, Void, Socket>
{
    private IConnectSuccess iget;

    public Task_Connect(IConnectSuccess _iget)
    {
        this.iget = _iget;
    }

    @Override
    protected Socket doInBackground(Void... arg0)
    {
        Socket connect = null;
        try
        {
            connect = new Socket("192.168.10.111", 2222);
        } catch (Exception e)
        {
            connect = null;
            Log.e(" com.yongyu.myqq.Task_Connect.doInBackground",
                    e.getMessage());
        }
        return connect;
    }

    @Override
    protected void onPostExecute(Socket result)
    {
        this.iget.onGetSocket(result);
    }

}
