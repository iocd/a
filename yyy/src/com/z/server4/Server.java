package com.z.server4;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {
	private int  defaultprot=8080;
	//服务器
	private ServerSocket server;
	
	public static void main(String[] args) {
		Server server=new Server();
		server.start();
	}

	private void start() {
		try {
			//开启一个端口用来接收数据
			server=new ServerSocket(defaultprot);
			//开始接收
			this.receive();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	private void receive() {
		try {
			//从服务器接收客户端信息，把信息封装成一个客户端，处理客户端中的信息
			//TCP
			Socket client=server.accept();
			//接收到的request  http请求
			byte[] data=new byte[20480];
			int len=client.getInputStream().read(data);
			//接受客户端的请求信息
			String requestInfo=new String(data,0,len);
			System.out.println(requestInfo);
			
			//响应http
			Response rep=new Response(client.getOutputStream());
			rep.println("<html><head></head><body>hello 107</body></html>");
			rep.pushToClient(200);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void stop() {
		
	}
	
}
