package com.webrock.scope;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class RequestScope 
{
private  HttpServletRequest request;

public RequestScope(HttpServletRequest request)
{
this.request = request;
}
public String getParameter(String name)
{
return request.getParameter(name);
}
public void setAttribute(String key, Object value) 
{
request.setAttribute(key, value);
}
public Object getAttribute(String key)
{
return request.getAttribute(key);
}
public void removeAttribute(String name)
{
request.removeAttribute(name);
}
public String getMethod()
{
return request.getMethod();
}
public String getRequestURI()
{
return request.getRequestURI();
}
public HttpServletRequest raw()
{
return request;
}
}
