package com.sirra.server.sirrapresentation;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

/**
 * Returns the control javascript file. This is a list of all channels and all associated md5 names for html, css and js.
 * 
 * @author aris
 */
@WebServlet(urlPatterns = {"/control.js"})
public class ControlServlet extends HttpServlet {

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/javascript");
		response.setHeader("Cache-Control", "no-store, no-cache");
	
		ControlCompiler controlCompiler = ControlCompiler.getInstance();		
		response.getWriter().write(controlCompiler.getControlJavaScript());
    }
}