package com.molecode.w2k.kindle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by YP on 2015-12-28.
 */
public class KindleGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(KindleGenerator.class);

	public File generate(File originalFile) {
		File kindleFile = null;
		try {
			String originalFilePath = originalFile.getAbsolutePath();
			int exitValue = Runtime.getRuntime().exec("kindlegen " + originalFilePath).waitFor();
			if (exitValue < 2) {
				kindleFile = new File(originalFilePath.substring(0, originalFilePath.lastIndexOf('.')) + ".mobi");
				if (exitValue == 1) {
					LOG.info("Mobi file generated with warnings: {}", originalFilePath);
				}
			} else {
				LOG.warn("Failed to generate mobi file: {}", originalFilePath);
			}
		} catch (IOException e) {
			LOG.warn("Failed to execute the kindlegen command.", e);
		} catch (InterruptedException e) {
			LOG.warn("Failed to execute the kindlegen command.", e);
		}
		return kindleFile;
	}
}
