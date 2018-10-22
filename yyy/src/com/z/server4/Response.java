package com.z.server4;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

//封装响应信息，简化返回消息的处理流程
public class Response {
	
	//存储空格的字符串
	public static final String BLANK=" ";
	//存储换行的字符串
	public static final String CRLF="\r\n";
	//消息头
	private StringBuilder headInfo;
	//正文内容
	private StringBuilder content;
	//正文长度
	private int len=0;
	//输入流
	private BufferedWriter bw;
	
	public Response() {
		headInfo=new StringBuilder();
		content=new StringBuilder();
		len=0;
	}
		
	public Response(OutputStream os) {
		//初始化成员变量
		this();
		try {
			bw=new BufferedWriter(new OutputStreamWriter(os));
		} catch (Exception e) {
			headInfo=null;
		}
	}
	//构建返回的正文内容
	public Response print(String info) {
		content.append(info);
		len+=info.getBytes().length;
		return this;
	}
	//构建正文内容和消息头之间的换行
	public Response println(String info) {
		content.append(info).append(CRLF);//  \r\n
		len+=(info+CRLF).getBytes().length;
		return this;
	}
	//构建消息头  200 404 500
	public Response createHeadInfo(int code) {
		//header：  HTTP/1.1 200  ok  error  notfound
		headInfo.append("HTTP/1.1").append(BLANK).append(code).append(BLANK);
		//.append("ok").append(CRLF);
		switch(code) {
		case 200:headInfo.append("OK");break;
		case 500:headInfo.append("SERVER ERROR");break;
		case 404:headInfo.append("NOT FOUND");break;
		}
		headInfo.append(CRLF);
		//response   
		//header：  HTTP/1.1 200  ok  error  notfound 
		headInfo.append("Server:107Server").append(CRLF);
		//Server:Apache
		headInfo.append("Date:").append(new Date()).append(CRLF);
		//Date:Wed, 17 Oct 2018 01:18:04 GMT
		headInfo.append("Content-Type:text/html;charset=GBK").append(CRLF);
		//Content-Type:image/svg+xml
		headInfo.append("Content-Length:").append(len).append(CRLF);
		//Content-Length:1734
		//这里是为了存储网页文件,最后一个换行符
		headInfo.append(CRLF);
		return this;
	}
	
	//推送页面内容到客户端  code 200   ok  404  not found 500  server error
	public void pushToClient(int code)throws IOException {
		if(null==headInfo) {
			code=500;
		}
		//完成消息头的构建
		createHeadInfo(code);
		//回送页面内容到客户端
		bw.append(headInfo.toString());
		bw.append(content.toString());
		bw.flush();
	}
	
	//关闭流
	public void close() {
		//bw.close();
		CloseUtil.closeIO(bw);
	}
	
	
	
	
}
