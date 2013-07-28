package com.sirra.server.templating;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.apache.commons.io.*;

/**
 * Easy support for templates. Used for sending html emails for example.
 * 
 * Put all templates in resources/templates.
 * 
 * @author aris
 */
public class Template {
	
	/**
	 * Searches for template from /resources/templates folder. You should pass in the subfolder,
	 * like "emails/hello.html".
	 */
	public static String get(String fileName, String... nameValuePairs) {
		URL url = Template.class.getClassLoader().getResource("templates/" + fileName.toLowerCase());
		
		if(url != null) {
			
			File f = new File(url.getFile());
			
			try {
				String contents = FileUtils.readFileToString(f);
				
				return replace(contents, nameValuePairs);
				
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
			
		} else {
			throw new RuntimeException("Could not find template " + fileName);
		}
	}
	
	public static String replace(String contents, String... nameValuePairs) {

		if(nameValuePairs.length > 0) {
			Map<String, String> lookup = new HashMap();
			
			for(int i=0; i<nameValuePairs.length; i+=2) {
				lookup.put(nameValuePairs[i], nameValuePairs[i+1]);
			}
			
			for(String key: lookup.keySet()) {
				String value = lookup.get(key);
				
				contents = contents.replaceAll("\\$\\{" + key + "\\}", value);
			}
		}
		
		return contents;
	}
	
	protected static Pattern attributePattern = Pattern.compile("\\$\\{[^}]*\\}");
	
	/**
	 * Given text with attributes like ${name}, return the list of attributes found.
	 * 
	 * @return
	 */
	public static List<String> extractAttributes(String contents) {
		List<String> attributes = new ArrayList();
		
		Matcher attributeMatcher = attributePattern.matcher(contents);
    	while(attributeMatcher.find()) 
		{
    		String attributeMetaId = contents.substring(
    				attributeMatcher.start()+2, attributeMatcher.end()-1);
    		
			attributes.add(attributeMetaId);
    	}
    	
    	return attributes;
	}
}
