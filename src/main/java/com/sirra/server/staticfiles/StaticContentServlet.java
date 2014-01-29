package com.sirra.server.staticfiles;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.apache.commons.io.*;

import com.sirra.server.staticfiles.cache.*;
import com.sirra.server.templating.*;

/**
 * Any request path that is not recognized by another servlet (such as ApiServlet) will come here.
 * 
 * This will serve static content located in resources/publicweb.
 * 
 * @author aris
 *
 */
@WebServlet(urlPatterns = {"/*"})
public class StaticContentServlet extends HttpServlet {

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		System.out.println("Retrieving static file at " + request.getPathInfo());
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		StaticContentCache cache = StaticContentCache.getInstance();
		
		if(cache.containsPath(request.getPathInfo())) {
			System.out.println(" -- Returning cached version of " + request.getPathInfo());
			cache.process(request.getPathInfo(), response);
			return;
		}
		
		PathData mappedPath = mapPath(request.getPathInfo());
		
		System.out.println("Static file identified: " + mappedPath.getPath());
		
		ServletContext sc = getServletContext();
		String mimeType = sc.getMimeType(mappedPath.getPath());
		response.setContentType(mimeType);
		
        if(!mappedPath.isValid()) {
        	System.out.println(" -- Path not recognized");
        	request.getRequestDispatcher("/error404.html").forward(request,response);
        	return;
        }
        
        if(mimeType != null && mimeType.equals("text/html")) {
        	// Do special processing for html files to support templating.

            String content = IOUtils.toString(mappedPath.getInputStream());
            content = fillInTemplates(content);
            
            response.getWriter().write(content);
            cache.cacheString(request.getPathInfo(), mimeType, content);
            return;
        } else {
        	// Otherwise, just write the file contents out.
        	byte[] bytes = IOUtils.toByteArray(mappedPath.getInputStream());
        	
        	response.getOutputStream().write(bytes);
        	cache.cacheBytes(request.getPathInfo(), mimeType, bytes);
        	return;
        }
    }
	
	/**
	 * Map the request path to our local directory path.
	 * Logic:
	 * 	- If the path does not have an extension, it will add ".html".
	 *  - All our static files are in /resources/public. So add "html" to the path.
	 */
	protected PathData mapPath(String path) {
		if(path.equals("/")) path = "/index.html";
		
		PathData fullPathData = tryPath(path);
		
		if(fullPathData.isValid()) return fullPathData;
		
		// Try removing last part, could be /id path parameter.
		PathData partialPathData = tryPath(path.substring(0, path.lastIndexOf("/")));
		if(partialPathData.isValid()) return partialPathData;
		
		// Return full path so error can be reported
		return fullPathData;
	}
	
	protected PathData tryPath(String path) {
		String extension = "html";
		
		// Get last path element, without forward slash.
		String lastPath = path.substring(path.lastIndexOf("/") + 1);
		
		// If the file doesn't have an extension.
		if(!lastPath.contains(".")) {
			// add .html
			path = path + ".html";
		} else {
			extension = lastPath.substring(lastPath.lastIndexOf(".") + 1);
		}
		
		// Finally, source it from the "/publicweb" subfolder.
		path = "publicweb" + path;
	
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
       	
		return new PathData(path, is, extension);
	}
	
	protected String fillInTemplates(String content) {

        List<String> attributes = Template.extractAttributes(content);
        for(String attribute: attributes) {
        	System.out.println("Found " + attribute);
        	
        	if(attribute.indexOf("template.") == 0) {
        		String templateName = attribute.substring("template.".length());
        		content = Template.replace(content, attribute, TemplateCache.getInstance().get("publicweb/" + templateName + ".html"));
        	}
        }
        
        return content;
	}
}

class PathData {
	protected String path;
	protected InputStream is;
	protected String extension;
	
	public PathData(String path, InputStream is, String extension) {
		this.path = path;
		this.is = is;
		this.extension = extension;
	}
	
	public String getPath() {
		return path;
	}
	
	public boolean isValid() {
		return is != null;
	}
	
	public InputStream getInputStream() {
		return is;
	}
	
	public String getExtension() {
		return extension;
	}
}