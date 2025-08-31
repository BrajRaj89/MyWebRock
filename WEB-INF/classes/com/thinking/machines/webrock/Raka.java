package com.thinking.machines.webrock;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.lang.reflect.*;

public class Raka extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
String name = request.getParameter("name");
System.out.println(name);
String path = request.getServletContext().getRealPath("/WEB-INF/js/"+name);
System.out.println(path);
PrintWriter pw = response.getWriter();
File file = new File(path);
RandomAccessFile raf = new RandomAccessFile(file,"r");
StringBuilder sb = new StringBuilder();
String line;
while((line=raf.readLine())!=null) sb.append(line);
raf.close();
response.setContentType("text/plain");
String responseString = sb.toString();
pw.write(responseString);
}catch(Exception e)
{
e.printStackTrace();
}
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
try
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
}catch(Exception e)
{
System.out.println(e.getMessage());
}
}
}