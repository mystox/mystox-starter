package com.kongtrolink.framework.scloud.util;

/**
 * 一些 常用的 定时任务 粒度
 * @author John
 */

public class JonCron {
	public static final String everyMinute1 = "0 * * * * ?";//每分钟执行一次
	public static final String everyMinute5 = "0 0/5 * * * ?";//每隔5分钟执行一次
	public static final String everyMinute15 ="0 0/15 * * * ?";//每隔15分钟执行一次
	public static final String everyMinute30 ="0 0/30 * * * ?";//每隔30分钟执行一次		
	public static final String everyHour1 ="0 0 * * * ?";//每隔1小时执行一次
	public static final String everyHour2 ="0 0 */2 * * ?";//每隔2小时执行一次
	public static final String everySe10 ="0/10 * * * * ?";//每天早上1点执行一次
	public static final String everySe15 ="0/15 * * * * ?";//每天早上1点30分执行一次
	public static final String everySe20 ="0/20 * * * * ?";//每天早上2点执行一次
	/**
	 * 自定义时间 粒度
	 */
	public static final String selfMinute00 ="0 0,15,30,45 * * * ?";//在 0分,15分、30分、45分执行一次：
	public static final String selfHour00 ="0 0 0,6,12,18 * * ?";//每天的0点、6点、12点、18点都执行一次：
	/**
	 * 蓄电池报表时间粒度
	 */
	public static final String batteryHisMonth ="0 0/20 * * * ?";//每20分钟 触发一次
	public static final String batteryHisMonth1 ="0 0,40 0,2,4,6,8,10,12,14,16,18,20,22 * * ?";//偶数小时的0分、40分触发
	public static final String batteryHisMonth2 ="0 20 1,3,5,7,9,11,13,15,17,19,21,23 * * ?";// 奇数小时的20分触发
	public static final String batteryHisYear ="0 0 0,8,16 * * ?";//每天0点、8点、16点触发，即每隔8小时触发 ：
	
	
}
