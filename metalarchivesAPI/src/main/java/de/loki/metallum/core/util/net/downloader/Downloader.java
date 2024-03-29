package de.loki.metallum.core.util.net.downloader;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;

import de.loki.metallum.core.util.net.downloader.interfaces.IContentDownloader;

public class Downloader {

	private static Logger	logger	= Logger.getLogger(Downloader.class);

	public static BufferedImage getImage(final String urlString) throws ExecutionException {
		return get(urlString, new ImageDownloader(urlString));
	}

	public static String getHTML(final String urlString) throws ExecutionException {
		return get(urlString, new HTMLDownloader(urlString));
	}

	private static <T> T get(final String request, final IContentDownloader<T> downloader) throws ExecutionException {
		try {
			return ThreadExecuter.getInstance().submit(downloader).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error("Failed to download: " + request, e);
		}
		return null;
	}

}
