using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace ConsoleApplication1
{
    class Program
    {
        static void Main(string[] args)
        {
            //第一个参数指定寻址方案，AddressFamily.InterNetwork代表的是ipv4寻址方案
            //第二个参数指定连接类型，SocketType.Stream代表的是基于字节流的可靠双工连接
            //第三个参数指定运输层协议，这里指定用tcp协议承载
            Socket soc_server = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            //指定套接字监听的IP及端口。IP是指客户端的IP，Any表示允许任何客户端接入。端口是本地端口。这样的设置意味着允许任意IP的客户端通过本机2222端口接入
            IPEndPoint ip = new IPEndPoint(IPAddress.Any, 2222);
            soc_server.Bind(ip);
            //10表示挂起连接队列的最大长度是10
            soc_server.Listen(10);
            while (true)
            {
                Console.WriteLine(soc_server.LocalEndPoint.ToString() + "等待客户端连接");
                //当有连接接入时，创建新套接字为这个连接服务。我们将使用这个新套接字与客户端通信
                //Accept方法是一个同步方法，会阻塞所在线程。也就是说，此处Accept函数调用时会停下来等待，直到有连接接入才会返回继续执行后面的代码
                Socket soc_new = soc_server.Accept();
                Console.WriteLine(soc_new.RemoteEndPoint.ToString() + "已连接");

                string all = string.Empty;
                while (true)
                {
                    string input = string.Empty;
                    //建立输入缓存
                    byte[] receiveBytes = new byte[1024];

                    //每次读取1024个字节，直到读到指定的字符串，一次读取结束。
                    //如果没有可读取的数据，则 Receive 方法将一直处于阻止状态
                    //如果远程主机使用 Shutdown 方法关闭了 Socket 连接，并且所有可用数据均已收到，则 Receive 方法将立即完成并返回零字节。
                    int byteNums = soc_new.Receive(receiveBytes);
                    if (byteNums == 0)
                    {
                        Console.WriteLine("远程主机主动关闭");
                        break;
                    }

                    //编码数据
                    input = Encoding.ASCII.GetString(receiveBytes, 0, byteNums);
                    all += input;
                    Console.WriteLine("收到：" + input);

                    byte[] reply = Encoding.ASCII.GetBytes(input + ":receive succeed\n");
                    soc_new.Send(reply);

                    //当接收到的"[FINAL]"时，会话结束
                    if (input.IndexOf("[final]") > -1)
                    {
                        Console.WriteLine("收到结束字符串");
                        break;
                    }


                }
                //禁止客户端收发数据
                soc_new.Shutdown(SocketShutdown.Both);
                soc_new.Close();
            }
            soc_server.Close();
        }
    }
}














