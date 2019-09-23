package com.test;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import com.domain.Customer;
import com.util.OpenSessionUtil;

public class TestCla {
	@Test
	public void test1() {
		//1.加载配置信息，默认情况下加载的是src下面的hibernate.cfg.xml
		Configuration cf = new Configuration().configure("hibernate.cfg.xml");
		//2.根据配置信息，创建一个sessionFactory:用来创建SessionFactory的，一般消耗资源较多
		SessionFactory sessionFactory = cf.buildSessionFactory();
		//3.根据sessionFactory拿取到一个session对象
		Session openSession = sessionFactory.openSession();
		//4.开启事务
		Transaction beginTransaction = openSession.beginTransaction();
		
		//5.执行相关操作
		Customer customer = new Customer();
		customer.setCust_name("name02");
		customer.setCust_phone("12213");
		openSession.save(customer);//持久化，有id，有session关联
		
		//6.提交事务
		beginTransaction.commit();
		//7.关闭资源
		openSession.close();
		sessionFactory.close();
	}
	
	@Test//删除指定ID的数据
	public void delTest() {
		//加载配置
		Configuration configure = new Configuration().configure("hibernate.cfg.xml");
		//获取到sessionFactory
		SessionFactory sessionFactory = configure.buildSessionFactory();
		//由sessionFactory拿到openSession 
		Session openSession = sessionFactory.openSession();
		//开启事务
		Transaction beginTransaction = openSession.beginTransaction();
		//执行操作
		Customer customer = openSession.get(Customer.class, 8L);
		openSession.delete(customer);
		//提交事务
		beginTransaction.commit();
		openSession.close();
		sessionFactory.close();
	}
	
	@Test//修改指定ID的数据
	public void updateTest() {
		//加载配置
		Configuration configure = new Configuration().configure("hibernate.cfg.xml");
		//获取到sessionFactory
		SessionFactory sessionFactory = configure.buildSessionFactory();
		//由sessionFactory拿到openSession 
		Session openSession = sessionFactory.openSession();
		//开启事务
		Transaction beginTransaction = openSession.beginTransaction();
		//执行操作
		Customer customer = openSession.get(Customer.class, 9L);//持久化状态的对象的任何变化都会自动更新，无需update操作
		customer.setCust_name("youchange");
//		openSession.update(customer);
		//提交事务
		beginTransaction.commit();
		openSession.close();
		sessionFactory.close();
	}
	
	@Test//获取指定ID的数据
	public void selectTest() {
		//加载配置
		Configuration configure = new Configuration().configure("hibernate.cfg.xml");
		//获取到sessionFactory
		SessionFactory sessionFactory = configure.buildSessionFactory();
		//由sessionFactory拿到openSession 
		Session openSession = sessionFactory.openSession();
		//开启事务
		Transaction beginTransaction = openSession.beginTransaction();
		//执行操作
		Customer customer = openSession.get(Customer.class, 9L);
		System.out.println(customer);
		//提交事务
		beginTransaction.commit();
		openSession.close();
		sessionFactory.close();
	}
	
	@Test//用来测试封装的工具类是否可以使用
	public void testUtil() {
		//从工具类中调用封装的方法
		Session openSession = OpenSessionUtil.getOpenSession();
		Transaction beginTransaction = openSession.beginTransaction();
		//执行操作
		Customer customer = openSession.get(Customer.class, 9L);
		System.out.println(customer);	
				
		beginTransaction.commit();
		openSession.close();
	}
	
	@Test//用来测试封装的工具类是否可以使用
	public void testGetSession() {
		//从工具类中调用封装的方法,拿到的session是两个不同的
		Session openSession1 = OpenSessionUtil.getOpenSession();
		Session openSession2 = OpenSessionUtil.getOpenSession();
		System.out.println(openSession1==openSession2);//false
		//拿到的是两个相同的session，与当前线程绑定的session
		Session currentSession1 = OpenSessionUtil.getCurrentSession();
		Session currentSession2 = OpenSessionUtil.getCurrentSession();
		System.out.println(currentSession1==currentSession2);//true
	}
	
	@Test//Hibernate中执行HQL语句：在利用HQL进行查询的时候，我们不需要写select
	public void queryHQL() {
		Session openSession = OpenSessionUtil.getOpenSession();
		Transaction beginTransaction = openSession.beginTransaction();
		//查询所有
		Query createQuery1 = openSession.createQuery("from Customer");
		createQuery1.setFirstResult(0);//表示从哪一个开始
		createQuery1.setMaxResults(2);//表示一页的最大值
		List<Customer> list = createQuery1.list();
		System.out.println(list);
		//指定条件查询:占位符方式，设置值的时候利用setString的方法
		Query createQuery2 = openSession.createQuery("from Customer where cust_name = ?");
		createQuery2.setString(0,"Python");
		List<Customer> list2 = createQuery2.list();
		System.out.println(list2);
		//指定条件查询：命名空间方式，相当于把占位符中替换了：别名
		Query createQuery3 = openSession.createQuery("from Customer where cust_name = :a and cust_source = :b");
		createQuery3.setString("a","Python");
		createQuery3.setString("b", "12");
//		List<Customer> list3 = createQuery3.list();效果同样的
		Customer uniqueResult = (Customer) createQuery3.uniqueResult();
		System.out.println(uniqueResult);
		
		beginTransaction.commit();
		openSession.close();
	}
	
	@Test//测试利用Criteria来进行查询：完全面向对象，没有SQL
	public void testCriteria() {
		Session openSession = OpenSessionUtil.getOpenSession();
		Transaction beginTransaction = openSession.beginTransaction();
		//利用criteria查询所有
		/*Criteria criteria1 = openSession.createCriteria(Customer.class);
		List<Customer> list = criteria1.list();
		System.out.println(list);*/
		//根据条件进行查询
		Criteria criteria2 = openSession.createCriteria(Customer.class);
		criteria2.add(Restrictions.eq("cust_name", "Python"));//添加条件，设置查询限制条件Restrictions
		criteria2.add(Restrictions.eq("cust_id", 3L));
		List<Customer> list2 = criteria2.list();
		System.out.println(list2);
		//分页进行查找
		
		Criteria criteria3 = openSession.createCriteria(Customer.class);
		criteria3.setFirstResult(2);
		criteria3.setMaxResults(2);
		List<Customer> list3 = criteria3.list();
		System.out.println(list3);
		
		beginTransaction.commit();
		openSession.close();	
		OpenSessionUtil.closeFactorySession();
	}
	
	@Test//利用SQLquery来进行查找
	public void testSqlQuery() {
		Session openSession = OpenSessionUtil.getOpenSession();
		Transaction beginTransaction = openSession.beginTransaction();
		String sql01 = "select * from cst_customer";
		SQLQuery sqlQuery = openSession.createSQLQuery(sql01);
		List<Object []> list1 = sqlQuery.list();
		for(Object [] objs : list1) {
			System.out.println(Arrays.toString(objs));
		}	
		String sql02 = "select * from cst_customer where cust_id = ?";
		SQLQuery createSQLQuery = openSession.createSQLQuery(sql02);
		createSQLQuery.setParameter(0, 3L);//设置查询条件参数
		createSQLQuery.addEntity(Customer.class);//注入到实体中
		List<Customer> list2 = createSQLQuery.list();
		System.out.println(list2);
		beginTransaction.commit();
		openSession.close();
	}
}
