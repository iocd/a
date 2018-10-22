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
	// ����ʽ
	private String method;// get post
	// ������Դ
	private String url;
	// �������
	private Map<String, List<String>> parameterMapValues;// name-apple name-bbb age=10 price=1.5$
															// {name{apple,bbb},age{10},price{1.5$}}
	// ׼��һ�����з����������
	private String CRLF = "\r\n";
	// ����������ȡ��������
	private InputStream is;
	// ׼��һ����Ϣͷ
	private String requestInfo;

	// ��ʼ����Ա����
	public Request() {
		method = "";
		url = "";
		parameterMapValues = new HashMap<String, List<String>>();
		requestInfo = "";
	}

	// ������ʼ��
	public Request(InputStream is) {
		this();
		this.is = is;
		try {
			byte[] data = new byte[20480];
			int len = is.read(data);
			// ���ܿͻ��˵�������Ϣ
			requestInfo = new String(data, 0, len);
		} catch (Exception e) {
			return;
		}
		// ����������Ϣ
		parseRequestInfo();
	}

	// ����������Ϣ
	// http://localhost:8080
	// http://localhost:8080/login
	// http://localhost:8080/login?
	// http://localhost:8080/login?name=
	// http://localhost:8080/login?name=apple
	// http://localhost:8080/login?name=apple&
	// http://localhost:8080/login?name=apple&password=
	// http://localhost:8080/login?name=apple&password=123&name=aaa
	private void parseRequestInfo() {// null " "
		// ���request�����߱�http-request ��Ϣͷ����Ĺ淶�����ǲ���Ϊ��һ��http������˷�������ִ�н���uri�Ĺ���
		if (null == requestInfo || requestInfo.trim().equals("")) {
			return;
		}
		// ����Ϣ�����з���������ʽ
		// get /mes/register?username=apple
		// post ��Ϣͷ�����������
		// ����get��post��˼· get ��ȡ��Ϣͷ�ĵ�һ��
		// ����post �ҳ����һ�����е�λ��

		String paramString = "";// �������ܲ���

		// GET /mes/register?username=apple HTTP/1.1
		String firstLine = requestInfo.substring(0, requestInfo.indexOf(CRLF));
		// /��ʼ��λ��
		int idx = firstLine.indexOf("/");
		// �õ�����ʽ
		this.method = firstLine.substring(0, idx).trim();// GET POST
		// ��һ������ȥ�洢/mes/register?username=apple
		// trim ָ����ȥ���ո�
		String urlStr = firstLine.substring(idx, firstLine.indexOf("HTTP/")).trim();
		System.out.println(idx+" "+method+" "+urlStr);//4 GET /index.html?useranme=asd&password=123
		// ��post��get����
		if (this.method.equalsIgnoreCase("post")) {
			// post url����
			this.url = urlStr;
			paramString = requestInfo.substring(requestInfo.lastIndexOf(CRLF)).trim();
		} else if (this.method.equalsIgnoreCase("get")) {
			// ������ʺ�,��ζ�ſ��ܴ��ڲ��� key-value
			if (urlStr.contains("?")) {
				// /mes/register?username=apple

				// /mes/register [?] username=apple&password=123
				String[] urlArrays = urlStr.split("\\?");
				this.url = urlArrays[0];
				// �������ַ�����username=apple&password=123
				paramString = urlArrays[1];
			} else {
				this.url = urlStr;
			}
		}

		//���˵paramString������û�в���
		if("".equals(paramString)) {
			return ;
		}
		//����uri�е�key-value��ʽ�Ĳ�������ȡ���������ݺ����ͷ�װ��parameterMapValues��
		parseParams(paramString);
		
	}
	//����uri�е�key-value��ʽ�Ĳ�������ȡ���������ݺ����ͷ�װ��parameterMapValues��
	//�����������װ��Map�С���HashMap<String,List<String>>()
		//uname=123&pwd=213&fav=0&fav=1&fav=2
		private void parseParams(String paramString){
			//�ָ� ���ַ���ת������
			StringTokenizer token=new StringTokenizer(paramString,"&");
			while(token.hasMoreTokens()){
				String keyValue =token.nextToken();
				//�Ⱥ����������ȫ���ó���
				String[] keyValues=keyValue.split("=");
				if(keyValues.length==1){
					//java-effective-��̬����������
					keyValues =Arrays.copyOf(keyValues, 2);
					keyValues[1] =null;
				}
				
				String key = keyValues[0].trim();
				//��������ַ��������⣬��Ҫurl���±���һ��
				String value = null==keyValues[1]?null:decode(keyValues[1].trim(),"gbk");//decodeû������ǰ����ʾ
				//ת����Map �ּ�
				if(!parameterMapValues.containsKey(key)){
					parameterMapValues.put(key,new ArrayList<String>());
				}
				List<String> values =parameterMapValues.get(key);
				values.add(value);			
			}		
		}
		
		/**
		 * ������ġ���û������ǰ�Ȳ���ʾ
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
		 * ����ҳ���name ��ȡ��Ӧ�Ķ��ֵ
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
		 * ����ҳ���name ��ȡ��Ӧ�ĵ���ֵ
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

	// getParamter(name��--String temp
	// getParameterValues(name)--String[] temp
//	public String getParameter(String name) {
//		return null;
//	}
//	public String[] getParameterValues(String name) {
//		return null;
//	}

}
