package cn.lottery.framework.util;

import java.text.DecimalFormat;

public class MathUtil {

	public static String format4point(double d)
	{
		DecimalFormat df = new DecimalFormat("#####0.0000");   
		return df.format(d); 
	}
	
	public static String format2point(double d)
	{
		DecimalFormat df = new DecimalFormat("#####0.00");   
		return df.format(d); 
	}
	
	public static String format1point(double d)
	{
		DecimalFormat df = new DecimalFormat("#####0.0");   
		return df.format(d); 
	}
	
	public static String format0point(double d)
	{
		DecimalFormat df = new DecimalFormat("#####0");   
		return df.format(d); 
	}
	
	
	public static String format4point(float d)
	{
		DecimalFormat df = new DecimalFormat("#####0.0000");   
		return df.format(d); 
	}
	
	public static String format2point(float d)
	{
		DecimalFormat df = new DecimalFormat("#####0.00");   
		return df.format(d); 
	}
	
	public static String format1point(float d)
	{
		DecimalFormat df = new DecimalFormat("#####0.0");   
		return df.format(d); 
	}
	
	public static String format0point(float d)
	{
		DecimalFormat df = new DecimalFormat("#####0");   
		return df.format(d); 
	}
	
	
	/*
	 
	  //int ii=255;		
		//byte b=(byte)ii; //可以强转				
		
		 DecimalFormat myformat=new DecimalFormat("0.00");
		 double c=3.154215;		 
		 String str = myformat.format(c); 
		 System.out.println(str);
		 
		 c=3.155215;		 
		 str = myformat.format(c); 
		 System.out.println(str);
		 
		 double D=Double.parseDouble("3.10");		 
		 str = myformat.format(D); 
		 System.out.println(str);
		 
		 double E=Double.parseDouble("3.09");		 
		 str = myformat.format(E); 
		 System.out.println(str);
		 
		 c=D+E;		 
		 str = myformat.format(c); 
		 System.out.println(str);
		 
		 c=D-E;		 
		 str = myformat.format(c); 
		 System.out.println(str);
		 
		 D=1.00;
	     E=0.90;	
	     c=D-E;	
		 System.out.println(c);
		 
		 BigDecimal a =new BigDecimal("1");
		 BigDecimal b = new BigDecimal("0.09");
		 BigDecimal d =a.subtract(b);
		 c=d.doubleValue();
		 System.out.println("d="+c);
	  
	     double b=1.459;
		
		BigDecimal a= BigDecimal.valueOf(b);
		
		System.out.println(MathUtil.format0point(a.doubleValue())); 1
		System.out.println(MathUtil.format1point(a.doubleValue())); 1.5
		System.out.println(MathUtil.format2point(a.doubleValue())); 1.46
	  
	 * */
	
}
