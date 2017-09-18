/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.cadutils;

import java.io.Serializable;

/**
 * 文件对象实体类
 * <p/>
 * 创建时间: 2014年11月17日 下午12:59:38 <br/>
 * 
 * @author xnjiang
 * @version
 * @since v0.0.1
 */
public class FileModel implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 文件Id
	 */
	private String fileId;
	/**
	 * 文件名称
	 */
	private String fileName;
	/**
	 * 文件图标
	 */
	private String fileIcon;
	/**
	 * 文件路径
	 */
	private String filePath;
	/**
	 * 文件日期
	 */
	private long fileDate;
	/**
	 * 文件日期，主要用于显示
	 */
	private String fileDateShow;
	/**
	 * 文件大小
	 */
	private long fileSize;
	/**
	 * 文件大小，主要用于显示
	 */
	private String fileSizeShow;
	/**
	 * 文件类型，文件后缀名
	 */
	private String fileType;
	/**
	 * 是否文件
	 */
	private boolean isFile = false;
	/**
	 * 是否目录
	 */
	private boolean isDirectory = false;
	/**
	 * 文件选择状态
	 */
	private boolean fileSelected = false;

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileIcon() {
		return fileIcon;
	}

	public void setFileIcon(String fileIcon) {
		this.fileIcon = fileIcon;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public long getFileDate() {
		return fileDate;
	}

	public void setFileDate(long fileDate) {
		this.fileDate = fileDate;
	}

	public String getFileDateShow() {
		return fileDateShow;
	}

	public void setFileDateShow(String fileDateShow) {
		this.fileDateShow = fileDateShow;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileSizeShow() {
		return fileSizeShow;
	}

	public void setFileSizeShow(String fileSizeShow) {
		this.fileSizeShow = fileSizeShow;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public boolean isFile() {
		return isFile;
	}

	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public boolean isFileSelected() {
		return fileSelected;
	}

	public void setFileSelected(boolean fileSelected) {
		this.fileSelected = fileSelected;
	}

}
