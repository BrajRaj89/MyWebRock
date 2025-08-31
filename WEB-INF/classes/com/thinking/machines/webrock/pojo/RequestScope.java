package com.thinking.machines.webrock.pojo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
public class RequestScope
{
private HttpServletRequest httpServletRequest;
public RequestScope(HttpServletRequest request)
{
httpServletRequest = request;
}
public void setRequestScope(HttpServletRequest request)
{
httpServletRequest = request;
}
public void setAttribute(String key,Object value)
{
httpServletRequest.setAttribute(key,value);
}
public Object getAttribute(String key)
{
return httpServletRequest.getAttribute(key);
}
}