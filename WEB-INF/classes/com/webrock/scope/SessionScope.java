package com.webrock.scope;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
public class SessionScope
{
private HttpSession session;
public SessionScope(HttpSession session)
{
this.session = session;
}
public void setAttribute(String key,Object value)
{
this.session.setAttribute(key,value);
}
public Object getAttribute(String key)
{
return this.session.getAttribute(key);
}
public void invalidate()
{
this.session.invalidate();
}
public void removeAttribute(String key)
{
session.removeAttribute(key);
}
public String getId()
{
return session.getId();
}
public HttpSession raw()
{
return session;
}
}