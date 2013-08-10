package com.sirra.server.staticfiles;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.apache.commons.io.*;

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

	protected String extension;
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		//String mappedPath = mapPath(request.getPathInfo().toLowerCase());
		String mappedPath = mapPath(request.getPathInfo());
		
		System.out.println("\nRetrieving static file: " + mappedPath);
		
		ServletContext sc = getServletContext();
		String mimeType = sc.getMimeType(mappedPath);
		response.setContentType(mimeType);
		
        InputStream is = getClass().getClassLoader().getResourceAsStream(mappedPath);
        
        if (is == null) {
        	System.out.println(" -- Path not recognized");
        	request.getRequestDispatcher("/error404.html").forward(request,response);
        	return;
        }
        
        if(mimeType != null && mimeType.equals("text/html")) {
        	// Do special processing for html files to support templating.

            String content = IOUtils.toString(is);
            content = fillInTemplates(content);
            
            response.getWriter().write(content);
        } else {
        	// Otherwise, just write the file contents out.
        	response.getOutputStream().write(IOUtils.toByteArray(is));
        	return;
        }
    }
	
	/**
	 * Map the request path to our local directory path.
	 * Logic:
	 * 	- If the path does not have an extension, it will add ".html".
	 *  - All our static files are in /resources/public. So add "html" to the path.
	 */
	protected String mapPath(String path) {
		if(path.equals("/")) path = "/index.html";
		
		extension = "html";
		
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
		
		return path;
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