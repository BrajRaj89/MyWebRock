package com.webrock.pojo;

import java.io.BufferedReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

import com.webrock.annotations.RequestParameter;
import com.webrock.exceptions.ServiceException;

public class content {
public void prmresolve()
{

if(parameters.length!=0)
{
paravalues = new Object[parameters.length];
String requestDataType = request.getContentType();
if(requestDataType != null && requestDataType.startsWith("application/json"))
{
if(parameters.length==1)
{
Class<?> jsonClass = parameters[0].getType();
BufferedReader rd=request.getReader();
StringBuffer sb = new StringBuffer();
String line;
while((line=rd.readLine())!=null) sb.append(line);
String jsonString  = sb.toString();
System.out.println("request string _________________ "+jsonString);
Object objp = gson.fromJson(jsonString,jsonClass);
System.out.println(jsonClass.getSimpleName());
System.out.println("printing the data object "+objp);
try
{
if(m.getReturnType().getName().equals("void"))
{
m.invoke(bean,objp);
}else
{
valueReturned = m.invoke(bean,objp);
if(forwardTo==null)
{
if(!valueReturned.getClass().isPrimitive())
{
String jString = gson.toJson(valueReturned);
response.getWriter().write(jString);
}else
{
response.getWriter().print(valueReturned);
}
return;
}
}
}catch(InvocationTargetException e)
{
System.out.println(e.getMessage());
Throwable cause = e.getCause();
String message;
if(cause instanceof java.sql.SQLIntegrityConstraintViolationException)
{
message = "Duplicate record! This entry already exists.";
}
else if(cause instanceof java.sql.SQLException)
{
message = "Database error: " + cause.getMessage();
}
else
{
message = "Unexpected error occurred!";
}
response.setStatus(400);
String json = gson.toJson(Map.of("error", true,"message",message));
response.getWriter().write(json);
return;
}
}else if(parameters.length>1)
{
try
{
BufferedReader rd=request.getReader();
StringBuffer sb = new StringBuffer();
String line;
while((line=rd.readLine())!=null) sb.append(line);
String jsonString  = sb.toString();
Object arguments[] = new Object[parameters.length];
for(int i=0;i<parameters.length; i++)
{
System.out.println("param name of method "+m.getName()+" is "+parameters[i].getType().getName());
Class<?> argClass = parameters[i].getType();
if(argClass==HttpServletRequest.class && parameters[i].getAnnotation(RequestParameter.class)==null)
{
arguments[i] = request;
}else if(argClass==HttpSession.class && parameters[i].getAnnotation(RequestParameter.class)==null)
{
arguments[i] = session;
}else if(argClass==ServletContext.class && parameters[i].getAnnotation(RequestParameter.class)==null)
{
arguments[i] = servletContext;
}
else
{
Class<?> jsonClass = parameters[i].getType();
Object objp = gson.fromJson(jsonString,jsonClass);
arguments[i] = objp;
}
} //for loop ends
if(m.getReturnType().getName().equals("void"))
{
m.invoke(bean,arguments);
}else
{
valueReturned = m.invoke(bean,arguments);
if(forwardTo==null)
{
if(!valueReturned.getClass().isPrimitive())
{
String jString = gson.toJson(valueReturned);
response.getWriter().write(jString);
}else
{
response.getWriter().print(valueReturned);
}
return;
}
}
}catch(Exception e)
{
System.out.println(e.getMessage());
response.sendError(HttpServletResponse.SC_FORBIDDEN);
}
}
}else
{
int index=0;
boolean flag;
for(Parameter para:parameters)
{
Class<?> type = para.getType();
flag = false;
if(type==HttpServletRequest.class)
{
paravalues[index] = request;
index++;
flag=true;
}else if(type==HttpSession.class)
{
paravalues[index] =session;
index++;
flag=true;
}else if(type==ServletContext.class)
{
paravalues[index] = servletContext;
index++;
flag=true;
}
if(!flag)
{
RequestParameter annoOnParam = para.getAnnotation(RequestParameter.class);
if(annoOnParam != null)
{
String value = request.getParameter(annoOnParam.value());
try
{
Object actualval = getActualTypeValue(value, type);
paravalues[index] = actualval;
index++;
}catch(ServiceException se)
{
response.sendError(HttpServletResponse.SC_BAD_REQUEST);
return;
}
}
}
} // for loop
if(m.getReturnType().getName().equals("void"))
{
m.invoke(bean,paravalues);
}else
{
valueReturned = m.invoke(bean,paravalues);
String jsonString = gson.toJson(valueReturned);
response.getWriter().print(jsonString);
return;
}
}
}else
{
if(m.getReturnType().getName().equals("void"))
{
m.invoke(bean);
}else
{
valueReturned = m.invoke(bean);
System.out.println(m.getName());
if(forwardTo==null)
{
if(valueReturned.getClass().isPrimitive() || valueReturned instanceof Integer || valueReturned instanceof Boolean || valueReturned instanceof Double || valueReturned instanceof String ||
valueReturned instanceof Long || valueReturned instanceof Byte || valueReturned instanceof Short)
{
response.getWriter().print(valueReturned);
}else
{
String jsonString = gson.toJson(valueReturned);
response.getWriter().write(jsonString);
}
}
return;
}
}
if(forwardTo != null)
{
Service nextService = map.get(forwardTo);
if(nextService != null)
{
Method nextMethod = nextService.getService();
nextMethod.invoke(bean, valueReturned);
return;
}
if(forwardTo.contains("."))
{
System.out.println(request.getContextPath());
System.out.println(forwardTo);
response.sendRedirect(request.getContextPath() + forwardTo);
return;
}
response.sendError(HttpServletResponse.SC_NOT_FOUND);
return;
}
}catch(InvocationTargetException ite)
{
Throwable cause = ite.getCause();
String message;
if(cause instanceof java.sql.SQLIntegrityConstraintViolationException)
{
message = "Duplicate record! This entry already exists.";
}
else if(cause instanceof java.sql.SQLException)
{
message = "Database error: " + cause.getMessage();
}
else
{
message = "Unexpected error occurred!";
}
response.setStatus(400);
String json = gson.toJson(Map.of("error", true,"message",message));
response.getWriter().write(json);
return;
}
}
}
