package com.webrock.pojo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
public class SessionScope
{
private HttpSession httpSession;
public SessionScope(HttpSession session)
{
this.httpSession = session;
}
public void setSessionScope(HttpSession session)
{
this.httpSession = session;
}
public void setAttribute(String key,Object value)
{
this.httpSession.setAttribute(key,value);
}
public Object getAttribute(String key)
{
return this.httpSession.getAttribute(key);
}
}