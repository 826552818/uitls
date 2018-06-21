package com.utils.poi;

import java.io.File;

/**
 * 组合excel文件和对应的VO
 * 三位一体,VO,File,sortIndex
 * 
 * @author luoshouqiang
 *
 *         2016年9月13日
 */
public class ExcelFileMapper {

	/**
	 * excel文件
	 */
	private File file;
	/**
	 * 排序映射
	 */
	private Class voMapper;

	public ExcelFileMapper(File file, Class voMapper) {
		this.file = file;
		this.voMapper = voMapper;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Class getVoMapper() {
		return voMapper;
	}

	public void setVoMapper(Class voMapper) {
		this.voMapper = voMapper;
	}
	
	

}
