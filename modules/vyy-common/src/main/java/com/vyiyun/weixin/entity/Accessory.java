package com.vyiyun.weixin.entity;

import java.io.Serializable;

/**
 * 附件
 * 
 * @author tf
 * 
 * @date 2015年6月29日 上午10:00:59
 * @version 1.0
 */
public class Accessory implements Serializable {

	private static final long serialVersionUID = 3647233284813657927L;

	/*
	 * 文件ID
	 */
	private String id;
	/*
	 * 文件名称
	 */
	private String fileName;
	/*
	 * 文件存储路径
	 */
	private String filePath;
	/*
	 * 图片缩放存储路径
	 */
	private String zoomPath;

	private String bType;

	public Accessory() {
		super();
	}

	public Accessory(String id, String fileName, String filePath, String zoomPath) {
		super();
		this.id = id;
		this.fileName = fileName;
		this.filePath = filePath;
		this.zoomPath = zoomPath;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getZoomPath() {
		return zoomPath;
	}

	public void setZoomPath(String zoomPath) {
		this.zoomPath = zoomPath;
	}

	public String getbType() {
		return bType;
	}

	public void setbType(String bType) {
		this.bType = bType;
	}

}
