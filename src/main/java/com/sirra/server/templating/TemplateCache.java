package com.sirra.server.templating;

import java.io.*;
import java.util.*;

import org.apache.commons.io.*;

import com.sirra.server.*;


/**
 * A cache of template files found in /resources/templates.
 * 
 * @author aris
 */
public class TemplateCache {

	protected static TemplateCache instance;
	
	public static TemplateCache getInstance() {
		if(instance == null) instance = new TemplateCache();
		
		return instance;
	}
	
	protected Map<String, String> lookup;
	
	protected TemplateCache() {
		lookup = new HashMap();
	}
	
	/**
	 * @param templatePath Path to the  template file in resources, 
	 * e.g. html/footer.html, or email/welcome.html
	 * 
	 * @return
	 */
	public String get(String templatePath) {
		
		// In dev mode we always reload the template, every time.
		if(Mode.get() == Mode.Production) {
			if(lookup.containsKey(templatePath)) return lookup.get(templatePath);
		}
		
		try {
			InputStream is = getClass().getClassLoader().getResourceAsStream("templates/" + templatePath);
	        String str = IOUtils.toString(is);
			
			lookup.put(templatePath, str);
			return str;
		} catch(Exception e) {
			throw new RuntimeException("Trouble finding template " + templatePath, e);
		}
	}
}
