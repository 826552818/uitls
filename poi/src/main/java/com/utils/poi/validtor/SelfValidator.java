package com.utils.poi.validtor;

/**
 * 用于检测每个解析后的excel对象自身的一些属性的正确与否
 * @author luoshouqiang
 *
 * 2016年9月21日
 */
public interface SelfValidator {
		
	/**
	 * 检测不成功返回错误信息，成功返回空字符串
	 * @return
	 */
	public String selfCheck ();
}
