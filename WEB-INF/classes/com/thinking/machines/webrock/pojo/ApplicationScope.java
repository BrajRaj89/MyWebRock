package com.thinking.machines.webrock.pojo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
public class ApplicationScope
{
private ServletContext servletContext;
public  ApplicationScope(ServletContext servletContext)
{
this.servletContext = servletContext; 
}
public void setApplicationScope(ServletContext servletContext)
{
this.servletContext = servletContext;
}
public void setAttribute(String key,Object value)
{
servletContext.setAttribute(key,value);
}
public Object getAttribute(String key)
{
return servletContext.getAttribute(key);
}
}