package com.urlprocessor.utils;

import java.io.Serializable;

/**
 * @author gauravkahadane Encapsulation for URL statistics
 */
public class StatisticInfo implements Serializable {

	private static final long serialVersionUID = -8300949087977411303L;

	private String url;
	private String threadName;
	private Long processingTime;
	private Integer wordCount;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public Integer getWordCount() {
		return wordCount;
	}

	public void setWordCount(Integer wordCount) {
		this.wordCount = wordCount;
	}

	public Long getProcessingTime() {
		return processingTime;
	}

	public void setProcessingTime(Long processingTime) {
		this.processingTime = processingTime;
	}

	@Override
	public String toString() {
		return "StatisticInfo [url=" + url + ", threadName=" + threadName + ", processingTime=" + processingTime
				+ ", wordCount=" + wordCount + "]";
	}

}
