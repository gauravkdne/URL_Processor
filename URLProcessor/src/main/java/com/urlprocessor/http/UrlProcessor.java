/**
 * 
 */
package com.urlprocessor.http;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.urlprocessor.utils.AppUtils;
import com.urlprocessor.utils.StatisticInfo;

/**
 * @author gauravkahadane
 * A Runnable to process URLs
 */
public class UrlProcessor implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(UrlProcessor.class);

	private String url = null;
	private Map<String, Integer> wordMap;
	private Stack<StatisticInfo> statistics;
	private CountDownLatch latch;

	public UrlProcessor(String url, Map<String, Integer> wordMap, Stack<StatisticInfo> statistics, CountDownLatch latch) {
		super();
		this.url = url;
		this.wordMap = wordMap;
		this.latch = latch;
		this.statistics = statistics;
	}
	
	/**
	 * Executes get request for a given URL and processes the response for containing words
	 */
	@Override
	public void run() {
		String msg = "\n"+Thread.currentThread().getName() + "(Start) - " + this.url + "  at "+AppUtils.getTime();
		LOGGER.info(msg);
		try {
			HttpClient httpClient = HttpClients.custom()
					.setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
					.build();

			HttpGet httpget = new HttpGet(this.url);

			ResponseProcessor responseProcessor = new ResponseProcessor();
			String responseBody = httpClient.execute(httpget, responseProcessor);

			this.processWords(responseBody);
		} catch (ClientProtocolException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		} finally {
			latch.countDown();
		    msg = "\n"+Thread.currentThread().getName() + "(End) - " + this.url + "  at "+AppUtils.getTime();
			LOGGER.info(msg);
		}
	}

	/**
	 * Extracts words from a given response and updates wordMap with frequency of a given word
	 * @param responseBody
	 */
	private synchronized void processWords(String responseBody) {
		Pattern pattern = Pattern.compile("[a-zA-Z]+");
		Matcher matcher = pattern.matcher(responseBody);

		int wordCount = 0;
		while (matcher.find()) {
			Integer word = wordMap.get(matcher.group());
			if (word != null) {
				wordMap.put(matcher.group(), ++word);
			} else {
				wordMap.put(matcher.group(), 1);
			}
			wordCount++;
		}

		//Generate thread statistics
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		StatisticInfo statInfo = new StatisticInfo();
		statInfo.setUrl(this.url);
		statInfo.setThreadName(Thread.currentThread().getName());
		statInfo.setProcessingTime(threadMXBean.getCurrentThreadUserTime());
		statInfo.setWordCount(wordCount);
		statistics.push(statInfo);
	}

}
