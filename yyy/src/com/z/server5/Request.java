package com.z.server5;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Request {
	// 请求方式
	private String method;// get post
	// 请求资源
	private String url;
	// 请求参数
	private Map<String, List<String>> parameterMapValues;// name-apple name-bbb age=10 price=1.5$
															// {name{apple,bbb},age{10},price{1.5$}}
	// 准备一个换行符，方便解析
	private String CRLF = "\r\n";
	// 输入流，读取请求内容
	private InputStream is;
	// 准备一个消息头
	private String requestInfo;

	// 初始化成员变量
	public Request() {
		method = "";
		url = "";
		parameterMapValues = new HashMap<String, List<String>>();
		requestInfo = "";
	}

	// 对流初始化
	public Request(InputStream is) {
		this();
		this.is = is;
		try {
			byte[] data = new byte[20480];
			int len = is.read(data);
			// 接受客户端的请求信息
			requestInfo = new String(data, 0, len);
		} catch (Exception e) {
			return;
		}
		// 分析请求信息
		parseRequestInfo();
	}

	// 处理请求信息
	// http://localhost:8080
	// http://localhost:8080/login
	// http://localhost:8080/login?
	// http://localhost:8080/login?name=
	// http://localhost:8080/login?name=apple
	// http://localhost:8080/login?name=apple&
	// http://localhost:8080/login?name=apple&password=
	// http://localhost:8080/login?name=apple&password=123&name=aaa
	private void parseRequestInfo() {// null " "
		// 如果request本身不具备http-request 消息头这里的规范，我们不认为是一个http请求，因此服务器不执行解析uri的功能
		if (null == requestInfo || requestInfo.trim().equals("")) {
			return;
		}
		// 从信息的首行分析出请求方式
		// get /mes/register?username=apple
		// post 消息头后面的正文里
		// 解析get和post的思路 get 获取消息头的第一行
		// 解析post 找出最后一个空行的位置

		String paramString = "";// 用来接受参数

		// GET /mes/register?username=apple HTTP/1.1
		String firstLine = requestInfo.substring(0, requestInfo.indexOf(CRLF));
		// /开始的位置
		int idx = firstLine.indexOf("/");
		// 拿到请求方式
		this.method = firstLine.substring(0, idx).trim();// GET POST
		// 用一个变量去存储/mes/register?username=apple
		// trim 指代是去掉空格
		String urlStr = firstLine.substring(idx, firstLine.indexOf("HTTP/")).trim();
		System.out.println(idx+" "+method+" "+urlStr);//4 GET /index.html?useranme=asd&password=123
		// 做post和get解析
		if (this.method.equalsIgnoreCase("post")) {
			// post url解析
			this.url = urlStr;
			paramString = requestInfo.substring(requestInfo.lastIndexOf(CRLF)).trim();
		} else if (this.method.equalsIgnoreCase("get")) {
			// 如果有问号,意味着可能存在参数 key-value
			if (urlStr.contains("?")) {
				// /mes/register?username=apple

				// /mes/register [?] username=apple&password=123
				String[] urlArrays = urlStr.split("\\?");
				this.url = urlArrays[0];
				// 参数的字符串？username=apple&password=123
				paramString = urlArrays[1];
			} else {
				this.url = urlStr;
			}
		}

		//如果说paramString根本就没有参数
		if("".equals(paramString)) {
			return ;
		}
		//处理uri中的key-value形式的参数，提取出参数内容和类型封装到parameterMapValues中
		parseParams(paramString);
		
	}
	//处理uri中的key-value形式的参数，提取出参数内容和类型封装到parameterMapValues中
	//将请求参数封装到Map中——HashMap<String,List<String>>()
		//uname=123&pwd=213&fav=0&fav=1&fav=2
		private void parseParams(String paramString){
			//分割 将字符串转成数组
			StringTokenizer token=new StringTokenizer(paramString,"&");
			while(token.hasMoreTokens()){
				String keyValue =token.nextToken();
				//等号左面的内容全部拿出来
				String[] keyValues=keyValue.split("=");
				if(keyValues.length==1){
					//java-effective-静态容器创建类
					keyValues =Arrays.copyOf(keyValues, 2);
					keyValues[1] =null;
				}
				
				String key = keyValues[0].trim();
				//解决中文字符乱码问题，需要url重新编译一下
				String value = null==keyValues[1]?null:decode(keyValues[1].trim(),"gbk");//decode没出问题前不演示
				//转换成Map 分拣
				if(!parameterMapValues.containsKey(key)){
					parameterMapValues.put(key,new ArrayList<String>());
				}
				List<String> values =parameterMapValues.get(key);
				values.add(value);			
			}		
		}
		
		/**
		 * 解决中文——没出问题前先不演示
		 * @param value
		 * @param code
		 * @return
		 */
		private String decode(String value,String code){
			try {
				return java.net.URLDecoder.decode(value, code);
			} catch (UnsupportedEncodingException e) {
				//e.printStackTrace();
			}
			return null;
		}
		/**
		 * 根据页面的name 获取对应的多个值
		 * @param args
		 */
		public String[] getParameterValues(String name){
			List<String> values=null;
			if((values=parameterMapValues.get(name))==null){
				return null;
			}else{
				return values.toArray(new String[0]);
			}
		}
		/**
		 * 根据页面的name 获取对应的单个值
		 * @param args
		 */
		public String getParameter(String name){
			String[] values =getParameterValues(name);
			if(null==values){
				return null;
			}
			return values[0];
		}
		public String getUrl() {
			return url;
		}

	// getParamter(name）--String temp
	// getParameterValues(name)--String[] temp
//	public String getParameter(String name) {
//		return null;
//	}
//	public String[] getParameterValues(String name) {
//		return null;
//	}

}
