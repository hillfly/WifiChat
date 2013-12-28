package com.immomo.momo.sql;

/*该类存放是用户信息
 * 所有用户信息的属性是私有的
 *能够通过该类的公用方法获取里面的私有信息
 */
public class userInfo
{
	private int id;               //用户ID
	private String name;          //用户名字
	private int age;              //用户年龄
	private String sex;           //用户性别
	private String imei;          //用户手机序列码
	private String ipAddr;        //用户IP地址
	private int isOnline;         //用户在线状态
	private int avater;           //用户头像
	
	//以下为用户的构造函数
	
	public userInfo()
	{
		super();
	}

	
	public userInfo(String name,String imei){
		this.name=name;
		this.imei=imei;
	}
	
	public userInfo(String name,String sex,String imei,int isOnline,int avater)
	{
		this(name,imei);
		this.isOnline=isOnline;
		this.sex=sex;
		this.avater=avater;
	}
	public userInfo(String name,int age,String sex,String imei,String ipAddr,int isOnline,int avater){
		this(name,sex,imei,isOnline,avater);
		this.age=age;
		this.ipAddr=ipAddr;
	}
	
	public userInfo(int id,String name,int age,String sex,String imei,String ipAddr,int isOnline,int avater){
		this(name,age,sex,imei,ipAddr,isOnline,avater);
		this.id=id;
	}
	

	//设置ID函数
	public void setId(int id)
	{
		this.id=id;
	}

	//获取ID函数
	public int getId()
	{
		return id;
	}
	
	//设置用户名函数
	public void setName(String name)
	{
		this.name=name;
	}
	
	//获取用户名函数
	public String getName()
	{
		return name;
	}
	
	//设置用户年龄
	public void setAge(int age)
	{
		this.age=age;
	}
	
	//获取用户年龄
	public int getAge()
	{
		return age;
	}
	
	//设置用户性别
	public void setSex(String sex)
	{
		this.sex=sex;
	}
	
	//获取用户性别
	public String getSex()
	{
		return sex;
	}
	
	//设置手机序列码
	public void setIMEI(String imei)
	{
		this.imei=imei;
	}
	
	//获取手机序列码
	public String getIMEI()
	{
		return imei;
	}
	
	//设置IP地址
	public void setIPAddr(String ipAddr)
	{
		this.ipAddr=ipAddr;
	}
	
	//获取IP地址
	public String getIPAddr()
	{
		return ipAddr;
	}
	
	//设置用户在线状态
	public void setIsOnline(int isOnline)
	{
		this.isOnline=isOnline;
	}
	
	//获取用户在线状态
	public int getIsOnline()
	{
		return isOnline;
	}
	
	//设置用户头像
	public void setAvater(int avater)
	{
		this.avater=avater;
	}
		
	//获取用户头像
	public int getAvater()
	{
		return avater;
	}
		
	//输出所有用户信息
	public String toString()
	{
		return "id:"+getId()+" name:"+getName()+" sex:"+getSex()+" age:"+getAge()+" IMEI:"+getIMEI()
				  +" ip:"+getIPAddr()+" status:"+getIsOnline()+" avaert:"+getAvater();
	}
}
