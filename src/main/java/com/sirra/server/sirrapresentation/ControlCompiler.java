package com.sirra.server.sirrapresentation;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import org.apache.commons.io.*;
import org.reflections.*;
import org.reflections.scanners.*;

import com.sirra.server.*;

/**
 * "Compiles" all the front-end files (html, js, css). Figures out md5 filenames so that caching
 * is never an issue.
 * 
 * @author aris
 */
public class ControlCompiler {

	protected static ControlCompiler instance;
	
	public static ControlCompiler getInstance() {
		if(instance == null) {
			instance = new ControlCompiler();
		}
		return instance;
	}
	
	protected ClassLoader classLoader;
	protected Reflections reflections;
	
	protected List<String> htmlFiles;
	protected List<String> cssFiles;
	protected List<String> jsFiles;
	
	protected Map<String, String> md5Lookup;
	
	protected StringBuffer controlString;

	private ControlCompiler() {
		classLoader = getClass().getClassLoader();
		initReflections();
		compile();
	}
	
	private void initReflections() {
		reflections = new Reflections("presentation.", new ResourcesScanner());
	}

	protected void compile() {
		htmlFiles = new ArrayList();
		cssFiles = new ArrayList();
		jsFiles = new ArrayList();
		
		md5Lookup = new HashMap();
		
		processBootstrap();
		processSirraPresentationFramework();
		
		processFiles(htmlFiles, "html");
		processFiles(cssFiles, "css");
		processFiles(jsFiles, "js");
		
		combine(htmlFiles, "html");
		combine(cssFiles, "css");
		combine(jsFiles, "js");
		
	}
	
	protected void processFiles(List<String> fileList, String extension) {
		Pattern pattern = Pattern.compile(".*\\." + extension);
		
		Set<String> files = reflections.getResources(pattern);
		
		System.out.println("Files (" + extension + "): ");
		
		for(String file: files) {
			addFile(fileList, file);
		}
	}
	
	protected void addFile(List<String> fileList, String file) {
		System.out.println("File : " + file);
		InputStream is = classLoader.getResourceAsStream(file);
		
		try {
			String content = IOUtils.toString(is);
			fileList.add(content);
		} catch(IOException e) {
			throw new RuntimeException();
		}
	}
	
	protected void combine(List<String> fileList, String extension) {
		
		if(fileList.size() == 0) return;
		
		StringBuffer str = new StringBuffer();
		for(String fileContents: fileList) {
			str.append(fileContents);
			str.append("\n\n");
		}
		
		String md5 = MD5Calculator.getMD5Checksum(str.toString());
		
		md5Lookup.put(md5 + "." + extension, str.toString());
		
		String loader = "";
		if(extension.equals("html")) {
			loader = "loadHtml";
		}
		else if(extension.equals("js")) {
			loader = "loadJs";
		}
		else if(extension.equals("css")) {
			loader = "loadCss";
		}
		
		if(extension.equals("html")) {
			controlString.append(loader + "(\"/load/" + md5 + "." + extension + "\", function(htmlContents) { _initialLoadedHtml(htmlContents); });\n");
		} else {
			controlString.append(loader + "(\"/load/" + md5 + "." + extension + "\", function() { _initialLoaded(\"" + extension + "\"); });\n");
		}
	}
	
	protected void processBootstrap() {
		// Get bootstrap
		controlString = new StringBuffer();
		InputStream is = classLoader.getResourceAsStream("sirrapresentation/sirra-bootstrap.js");
		try {
			controlString.append(IOUtils.toString(is));
			controlString.append("\n\n\n");
		} catch(IOException e) {
			throw new RuntimeException(e);
		}

		controlString.append("/////// Loaders follow ////////\n\n");

		controlString.append("loadCounter.totalHtml = 1;\n");
		controlString.append("loadCounter.totalJs = 1;\n");
		controlString.append("\n");
	}

	protected void processSirraPresentationFramework() {
		addFile(jsFiles, "sirrapresentation/ClassUtilStatic.js");
		addFile(jsFiles, "sirrapresentation/Widget.js");
		addFile(jsFiles, "sirrapresentation/Settings.js");
		addFile(jsFiles, "sirrapresentation/JQueryPlugins.js");
	}
	
	public String getControlJavaScript() {
		if(Mode.get() == Mode.Development) {
			// Recreate or rescan, but I can't find a better way to rescan.
			// Reallocate everytime is not a good idea, but for dev, this should be fine.
			initReflections();
			compile();
		}
		
		return controlString.toString();
	}
	
	public String getFileContents(String md5FileName) {
		return md5Lookup.get(md5FileName);
	}
}