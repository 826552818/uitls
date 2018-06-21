package com.utils.poi;


/**
 * 用来对应excel的vo和排序字段
 * @author luoshouqiang
 *
 * 2016年9月13日
 */
public class ClassSortMapper {

	/**
	 * 用来映射excel需要对应解析的VO
	 */
	private Class<? extends ExcelMapper> voClass;
	/**
	 * 排序字段
	 */
	private int sortIndex;

	public ClassSortMapper(Class<? extends ExcelMapper> voClass, int sortIndex) {
		this.voClass = voClass;
		this.sortIndex = sortIndex;
	}

	public Class<? extends ExcelMapper> getVoClass() {
		return voClass;
	}

	public void setVoClass(Class<? extends ExcelMapper> voClass) {
		this.voClass = voClass;
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

}
