package com.molecode.w2k.fetcher;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by YP on 2015-12-29.
 */
public class ArticleWriter {
	public void write(File articleFile, String articleContent) throws IOException {
		FileUtils.write(articleFile, articleContent);
	}
}
