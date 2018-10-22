package com.z.server4;

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
			byte[] data=new byte[20480];
			int len=client.getInputStream().read(data);
			//���ܿͻ��˵�������Ϣ
			String requestInfo=new String(data,0,len);
			System.out.println(requestInfo);
			
			//��Ӧhttp
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
