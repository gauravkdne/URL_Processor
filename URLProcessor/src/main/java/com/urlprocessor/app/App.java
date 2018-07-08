package com.urlprocessor.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.urlprocessor.http.UrlProcessor;
import com.urlprocessor.utils.AppUtils;
import com.urlprocessor.utils.StatisticInfo;

public class App {
	private static final Logger LOGGER = LogManager.getLogger(App.class);

	public static void main(String[] args) {
		System.out.print("Enter 5 URLs (Type 'exit' to stop entering URLs): ");
		Scanner scanner = new Scanner(System.in);

		List<String> urlList = new ArrayList<String>();
		int argCtr = 5;
		while (argCtr > 0) {
			String url = scanner.nextLine();

			if ("exit".equals(url)) {
				break;
			} else if (AppUtils.isValidUrl(url)) {
				urlList.add(url);
				argCtr--;
			} else {
				LOGGER.error("Invalid URL: " + url);
				LOGGER.info("The URL should begin with http or https such as http://oath.com");
			}
		}
		scanner.close();

		if(urlList.size() > 0) {
			// Process URLs parallely
			parallelProcessiong(urlList);

			// Process URLs asynchronously
			asyncProcessing(urlList);
		}else {
			LOGGER.error("Insufficient number of URLs");
		}
	}

	/**
	 * Spawns only one thread to process URLs asynchronously
	 * 
	 * @param urlList a list of valid urls
	 */
	private static void asyncProcessing(List<String> urlList) {
		LOGGER.info("\n\n\n\t### Processing URLs asynchronously ###\n");

		Map<String, Integer> wordMap = new ConcurrentHashMap<String, Integer>();
		;
		Stack<StatisticInfo> statistics = new Stack<StatisticInfo>();

		final CountDownLatch latch = new CountDownLatch(urlList.size());

		ExecutorService executerService = Executors.newSingleThreadExecutor();
		for (int i = 0; i < urlList.size(); i++) {
			Runnable urlProcessor = new UrlProcessor(urlList.get(i), wordMap, statistics, latch);
			executerService.submit(urlProcessor);
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}

		executerService.shutdown();

		AppUtils.writeToFile(wordMap, "Asynchronous Processing Statistics");
		AppUtils.writeToFile(statistics);
	}

	/**
	 * Spawns one thread for each URL contained in urlList and processes URLs
	 * parallely
	 * 
	 * @param urlList a list of valid urls
	 */
	private static void parallelProcessiong(List<String> urlList) {
		LOGGER.info("\n\n\n\t### Processing URLs parallely ###\n");

		Map<String, Integer> wordMap = new ConcurrentHashMap<String, Integer>();
		Stack<StatisticInfo> statistics = new Stack<StatisticInfo>();

		final CountDownLatch latch = new CountDownLatch(urlList.size());

		ExecutorService executerService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		for (int i = 0; i < urlList.size(); i++) {
			Runnable urlProcessor = new UrlProcessor(urlList.get(i), wordMap, statistics, latch);
			executerService.execute(urlProcessor);
		}
		executerService.shutdown();

		try {
			latch.await();
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}

		AppUtils.writeToFile(wordMap, "Parallel Processing Statistics");
		AppUtils.writeToFile(statistics);
	}
}
