package com.webrock.scope;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
public class ApplicationScope
{
private ServletContext context;
public  ApplicationScope(ServletContext context)
{
this.context = context; 
}
public void setApplicationScope(ServletContext context)
{
this.context = context;
}
public void setAttribute(String key,Object value)
{
context.setAttribute(key,value);
}
public Object getAttribute(String key)
{
return context.getAttribute(key);
}
public void removeAttribute(String key)
{
context.removeAttribute(key);
}
public ServletContext raw()
{
return context;
}
}