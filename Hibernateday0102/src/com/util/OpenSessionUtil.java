package com.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class OpenSessionUtil {
	private static SessionFactory buildSessionFactory;
	static {
		Configuration configure = new Configuration().configure("hibernate.cfg.xml");
		buildSessionFactory = configure.buildSessionFactory();
	}
	//定义个方法，用来返回buildSessionFactory
	public static Session getOpenSession() {
		return buildSessionFactory.openSession();
	}
	
	//定义个方法，用来返回buildSessionFactory,并且是与当前线程绑定之后的
	public static Session getCurrentSession() {
			return buildSessionFactory.getCurrentSession();
	}
	//关闭buildSessionFactory这个session工厂
	public static void closeFactorySession() {
		buildSessionFactory.close();
	}
}
