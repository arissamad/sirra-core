package com.sirra.server.sirrapresentation;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet(urlPatterns = {"/load/*"})
public class LoadCompiledFileServlet extends HttpServlet {
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		response.setHeader("Cache-Control", "public, max-age=2500000");
		
		String pathInfo = request.getPathInfo();
		
		String md5FileName = pathInfo.substring(1);
		System.out.println("Loading: " + md5FileName);

		String extension = md5FileName.substring(md5FileName.lastIndexOf(".")+1);
		
		if(extension.equals("html")) {
			response.setContentType("text/html");
		} else if(extension.equals("css")) {
			response.setContentType("text/css");
		} else if(extension.equals("js")) {
			response.setContentType("text/javascript");
		}
		
		String contents = ControlCompiler.getInstance().getFileContents(md5FileName);
		
		if(contents == null) {
			response.getWriter().write("Unknown file");
		} else {
			response.getWriter().write(contents);
		}
    }
}
