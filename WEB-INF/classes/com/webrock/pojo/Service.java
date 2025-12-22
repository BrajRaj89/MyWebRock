package com.webrock.pojo;
import java.lang.reflect.*;
import java.util.*;
public class Service
{
private Class<?> serviceClass;
private String path;
private String forward;
private Method service;
private boolean isPostAllowed;
private boolean isGetAllowed;
private boolean runOnStartup;
private boolean injectSessionScope;
private boolean injectRequestScope;
private boolean injectApplicationScope;
private boolean injectApplicationDirectory;
private List<Autowired> autoWired;
private boolean securedService;
private Class<?> checkPost;
private Method guard;  
public void setServiceClass(Class<?> serviceClass)
{
this.serviceClass = serviceClass;
}
public Class<?> getServiceClass()
{
return this.serviceClass;
}
public void setPath(String path)
{
this.path = path;
}
public String getPath()
{
return this.path;
}
public void setService(Method service)
{
this.service = service;
}
public Method getService()
{
return this.service;
}
public void setForward(String forward)
{
this.forward = forward;
}
public String getForward()
{
return this.forward;
}
public void setPostAllowed(boolean isPostAllowed)
{
this.isPostAllowed = isPostAllowed;
}
public boolean getPostAllowed()
{
return isPostAllowed;
}
public void setGetAllowed(boolean isGetAllowed)
{
this.isGetAllowed = isGetAllowed;
}
public boolean getGetAllowed()
{
return this.isGetAllowed;
}
public void setOnStartup(boolean runOnStartup)
{
this.runOnStartup = runOnStartup;
}
public boolean getOnStartup()
{
return runOnStartup;
}
public void setInjectSessionScope(boolean injectSessionScope)
{
this.injectSessionScope = injectSessionScope; 
}
public boolean getInjectSessionScope()
{
return injectSessionScope;
}
public void setInjectRequestScope(boolean injectRequestScope)
{
this.injectRequestScope = injectRequestScope; 
}
public boolean getInjectRequestScope()
{
return injectRequestScope;
}
public void setInjectApplicationScope(boolean injectApplicationScope)
{
this.injectApplicationScope = injectApplicationScope; 
}
public boolean getInjectApplicationScope()
{
return injectApplicationScope;
}
public void setInjectApplicationDirectory(boolean injectApplicationDirectory)
{
this.injectApplicationDirectory = injectApplicationDirectory; 
}
public boolean getInjectApplicationDirectory()
{
return injectApplicationDirectory;
}
public void setAutoWired(List<Autowired> autoWired)
{
this.autoWired = autoWired;
}
public List<Autowired> getAutoWired()
{
return this.autoWired;
}
public void setSecuredService(boolean securedService)
{
this.securedService = securedService;
}
public boolean getSecuredService()
{
return this.securedService;
}
public void setCheckPost(Class checkPost)
{
this.checkPost = checkPost;
}
public Class getCheckPost()
{
return this.checkPost;
}
public void setGuard(Method guard)
{
this.guard = guard;
}
public Method getGuard()
{
return this.guard;
}
}
