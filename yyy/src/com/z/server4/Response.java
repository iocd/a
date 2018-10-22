package com.z.server4;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

//��װ��Ӧ��Ϣ���򻯷�����Ϣ�Ĵ�������
public class Response {
	
	//�洢�ո���ַ���
	public static final String BLANK=" ";
	//�洢���е��ַ���
	public static final String CRLF="\r\n";
	//��Ϣͷ
	private StringBuilder headInfo;
	//��������
	private StringBuilder content;
	//���ĳ���
	private int len=0;
	//������
	private BufferedWriter bw;
	
	public Response() {
		headInfo=new StringBuilder();
		content=new StringBuilder();
		len=0;
	}
		
	public Response(OutputStream os) {
		//��ʼ����Ա����
		this();
		try {
			bw=new BufferedWriter(new OutputStreamWriter(os));
		} catch (Exception e) {
			headInfo=null;
		}
	}
	//�������ص���������
	public Response print(String info) {
		content.append(info);
		len+=info.getBytes().length;
		return this;
	}
	//�����������ݺ���Ϣͷ֮��Ļ���
	public Response println(String info) {
		content.append(info).append(CRLF);//  \r\n
		len+=(info+CRLF).getBytes().length;
		return this;
	}
	//������Ϣͷ  200 404 500
	public Response createHeadInfo(int code) {
		//header��  HTTP/1.1 200  ok  error  notfound
		headInfo.append("HTTP/1.1").append(BLANK).append(code).append(BLANK);
		//.append("ok").append(CRLF);
		switch(code) {
		case 200:headInfo.append("OK");break;
		case 500:headInfo.append("SERVER ERROR");break;
		case 404:headInfo.append("NOT FOUND");break;
		}
		headInfo.append(CRLF);
		//response   
		//header��  HTTP/1.1 200  ok  error  notfound 
		headInfo.append("Server:107Server").append(CRLF);
		//Server:Apache
		headInfo.append("Date:").append(new Date()).append(CRLF);
		//Date:Wed, 17 Oct 2018 01:18:04 GMT
		headInfo.append("Content-Type:text/html;charset=GBK").append(CRLF);
		//Content-Type:image/svg+xml
		headInfo.append("Content-Length:").append(len).append(CRLF);
		//Content-Length:1734
		//������Ϊ�˴洢��ҳ�ļ�,���һ�����з�
		headInfo.append(CRLF);
		return this;
	}
	
	//����ҳ�����ݵ��ͻ���  code 200   ok  404  not found 500  server error
	public void pushToClient(int code)throws IOException {
		if(null==headInfo) {
			code=500;
		}
		//�����Ϣͷ�Ĺ���
		createHeadInfo(code);
		//����ҳ�����ݵ��ͻ���
		bw.append(headInfo.toString());
		bw.append(content.toString());
		bw.flush();
	}
	
	//�ر���
	public void close() {
		//bw.close();
		CloseUtil.closeIO(bw);
	}
	
	
	
	
}
