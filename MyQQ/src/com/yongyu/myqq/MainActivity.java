package com.yongyu.myqq;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements IConnectSuccess,
        IGetMessage
{
    private Socket             connect;
    private EditText           edt_input;
    private ArrayBlockingQueue queue;
    private TextView           txt_message;
    private Handler            handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button btn_send_message = (Button) findViewById(R.id.btn_send_message);
        // ÿ��Handlerʵ��������󶨵����������߳��С�Handlerʵ�����Էַ�Message�����Runnable������󶨵��߳��в��Ż�ִ��
        handler = new Handler();

        edt_input = (EditText) findViewById(R.id.edt_input);
        txt_message = (TextView) findViewById(R.id.txt_message);
        btn_send_message.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                if (connect == null)
                {
                    Toast t = Toast.makeText(MainActivity.this, "�����쳣���������ã�",
                            2000);
                    t.show();
                    return;

                }
                if (edt_input.getText().toString().equals(""))
                {
                    Toast t = Toast.makeText(MainActivity.this, "��������Ϣ��", 2000);
                    t.show();
                    return;
                } else
                {
                    String str = edt_input.getText().toString();
                    try
                    {
                        MainActivity.this.queue.put(str);
                    } catch (InterruptedException e)
                    {
                        Log.e("com.yongyu.myqq.MainActivity.onCreate",
                                e.getMessage());
                    }
                    txt_message.setText(txt_message.getText() + "\n��������Ϣ�ǣ�"
                            + str);
                    edt_input.setText("");
                }

            }
        });

        Task_Connect task_conn = new Task_Connect(this);
        task_conn.execute();

    }

    @Override
    public void onGetSocket(Socket s)
    {
        this.connect = s;
        this.queue = new ArrayBlockingQueue<String>(10);
        Thread t_s = new Thread(new MyRunnableSendMessage(connect, queue));
        t_s.start();

        Thread t_r = new Thread(new MyRunnableReceiveMessage(connect, this));
        t_r.start();

    }

    @Override
    public void onGetMessage(String str)
    {
        myUpdateUIRunnable runnable = new myUpdateUIRunnable(str, txt_message);
        handler.post(runnable);
    }

}

class myUpdateUIRunnable implements Runnable
{
    private String   msg;
    private TextView text;

    public myUpdateUIRunnable(String str, TextView t)
    {
        msg = str;
        text = t;
    }

    @Override
    public void run()
    {
        text.setText(text.getText() + "\n�յ�����Ϣ�ǣ�" + msg);
    }
}
