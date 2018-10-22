package com.z.server5;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {
	private int  defaultprot=8080;
	//������
	private ServerSocket server;
	
	public static void main(String[] args) {
		Server server=new Server();
		server.start();
	}

	private void start() {
		try {
			//����һ���˿�������������
			server=new ServerSocket(defaultprot);
			//��ʼ����
			this.receive();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	private void receive() {
		try {
			//�ӷ��������տͻ�����Ϣ������Ϣ��װ��һ���ͻ��ˣ�����ͻ����е���Ϣ
			//TCP
			Socket client=server.accept();
			//���յ���request  http����
			Request request=new Request(client.getInputStream());
			
			//��Ӧhttp
			Response response=new Response(client.getOutputStream());
			response.println("<html><head></head><body>hello ").println(request.getParameter("username")).println("</body></html>");
			response.pushToClient(200);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void stop() {
		
	}
	
}
