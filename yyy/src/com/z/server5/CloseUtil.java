package com.z.server5;

import java.io.Closeable;

final public class CloseUtil {
	
	//�ر�һ��������
	public static <T extends Closeable> void closeIO(Closeable... io) {
		for(Closeable temp:io) {
			try {
				if(null!=temp) {
					temp.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
