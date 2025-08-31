package com.thinking.machines.webrock;
import com.thinking.machines.webrock.annotations.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.thinking.machines.webrock.model.*;
import com.thinking.machines.webrock.pojo.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import com.thinking.machines.webrock.exceptions.*;
import com.google.gson.*;
public class TMWebRock extends HttpServlet
{

public void doGet(HttpServletRequest request,HttpServletResponse response)
{
processRequest(request,response,"get");
}

public void doPost(HttpServletRequest request,HttpServletResponse response)
{
processRequest(request,response,"post");
}
public void doDelete(HttpServletRequest request,HttpServletResponse response)
{
processRequest(request,response,"delete");
}

public void processRequest(HttpServletRequest request,HttpServletResponse response,String requestType)
{
try
{
PrintWriter pw = response.getWriter();
String url = request.getPathInfo();
ServletContext context = getServletContext();
Map<String,Service> map = (Map<String,Service>)context.getAttribute("services");
Service service = map.get(url);
if(service!=null)
{
String filePath = context.getRealPath(url);
ApplicationDirectory directory = new ApplicationDirectory(new File(filePath));
try
{
Class c = service.getServiceClass();
Object obj = c.newInstance();
Method m = service.getService();
if(requestType.equals ("get"))
{
if(!service.getGetAllowed())
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}
}else if(requestType.equals("post"))
{
if(!service.getPostAllowed())
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}
}
HttpSession session = request.getSession();
SessionScope sessionScope = new SessionScope(session);
ServletContext servletContext = getServletContext();
ApplicationScope applicationScope = new ApplicationScope(servletContext);

if(service.getSecuredService())
{
Class securedService = service.getCheckPost();
Method guard = service.getGuard();
if(securedService!=null && guard!=null)
{
Parameter paramsOfss[] = guard.getParameters();
Object objOfparams[] = new Object[paramsOfss.length];
int index=0;

for(Parameter param:paramsOfss)
{
Class paramClass = param.getType();
if(paramClass==HttpServletRequest.class)
{
objOfparams[index] = request;
index++;
}else if(paramClass==HttpSession.class)
{
objOfparams[index] = session;
index++;
}else if(paramClass==ServletContext.class)
{
objOfparams[index] = servletContext;
index++;
}
} 
try
{
Object ssobj = securedService.newInstance();
if(guard.getReturnType().getName().equals("void"))
{
guard.invoke(ssobj,objOfparams);
}
}catch(Exception e)
{
response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
return;
}
} // if has class and guard
}// if it is secured service

if(service.getInjectSessionScope())
{
try
{
Method method = c.getMethod("setSessionScope");
if(method!=null) method.invoke(obj,sessionScope);
}catch(NoSuchMethodException e)
{
System.out.println(e.getMessage());
}
}
if(service.getInjectRequestScope())
{
RequestScope requestScope = new RequestScope(request);
try
{
Method method = c.getMethod("setRequestScope");
if(method!=null) method.invoke(obj,requestScope);
}catch(NoSuchMethodException e)
{
System.out.println(e.getMessage());
}
}
if(service.getInjectApplicationScope())
{
try
{
Method method  = c.getMethod("setApplicationScope");
if(method!=null) method.invoke(obj,applicationScope);
}catch(NoSuchMethodException e)
{
System.out.println(e.getMessage());
}
}
List<Autowired> listOfWired = service.getAutoWired();
if(listOfWired!=null)
{
for(Autowired wired:listOfWired)
{
String name  = wired.getName();
Field field= wired.getField();
field.setAccessible(true);
Class fieldClass = field.getType();
Object actualValue = null;
String value = null;
value = request.getParameter(name);
int val = Integer.parseInt(value);
field.set(obj,val);
System.out.println("value of wired property is set it is"+field.get(obj));
}
}
Field fields[] = c.getDeclaredFields();
for(Field f:fields)
{
InjectRequestParameter injectf = f.getAnnotation(InjectRequestParameter.class);
if(injectf!=null)
{
String name  = injectf.value();
Class type = f.getType();
String value = request.getParameter(name);
try
{
Object acval = getActualTypeValue(value,type);
f.setAccessible(true);
f.set(obj,acval);
System.out.println("printing value of injectrequestPara method "+f.get(obj));
}catch(ServiceException se)
{
System.out.println(se.getMessage());
}
}
}
Parameter parameters[] = m.getParameters();
Object valueReturned=null;
Object paravalues[] =null;
if(parameters.length!=0) paravalues = new Object[parameters.length];
if(parameters.length!=0)
{
String requestDataType = request.getContentType();
System.out.println(requestDataType);
if(requestDataType!=null)
{
if(requestDataType.equals("application/json"))
{
if(requestType.equals("get"))
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}
Gson gson =null;
gson = new Gson();
if(parameters.length==1)
{
try
{
Class jsonClass = parameters[0].getType();
BufferedReader rd=request.getReader();
StringBuffer sb = new StringBuffer();
String line;
while((line=rd.readLine())!=null) sb.append(line);
String jsonString  = sb.toString();
System.out.println(jsonString);
Object objp = gson.fromJson(jsonString,jsonClass);
if(m.getReturnType().getName().equals("void"))
{
m.invoke(obj,objp);
}else
{
valueReturned = m.invoke(obj,objp);
String responseString = gson.toJson(valueReturned);
response.getWriter().print(responseString);
return;
}
}catch(Exception e)
{
response.sendError(HttpServletResponse.SC_FORBIDDEN);
}
}
else if(parameters.length>1)
{
try
{
BufferedReader rd=request.getReader();
StringBuffer sb = new StringBuffer();
String line;
while((line=rd.readLine())!=null) sb.append(line);
String jsonString  = sb.toString();
System.out.println(jsonString);
Object arguments[] = new Object[parameters.length];
for(int i=0;i<parameters.length; i++)
{
Class argClass = parameters[i].getType();
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
Class jsonClass = parameters[i].getType();
Object objp = gson.fromJson(jsonString,jsonClass);
arguments[i] = objp;
}
}// forloop 
if(m.getReturnType().getName().equals("void"))
{
m.invoke(obj,arguments);
}else
{
valueReturned = m.invoke(obj,arguments);
String responseString = gson.toJson(valueReturned);
response.getWriter().print(responseString);
System.out.println("request with gson data method contains more than one parameter");
return;
}
}catch(Exception e)
{
response.sendError(HttpServletResponse.SC_FORBIDDEN);
}
}
}
}else
{
int index=0;
boolean flag;
for(Parameter para:parameters)
{
Class type = para.getType();
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
if(annoOnParam!=null)
{
String value=null;
System.out.println("parameter name "+annoOnParam.value());
if(requestType.equals("get") || requestType.equals("delete"))
{
 value = request.getParameter(annoOnParam.value());
}else
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}
System.out.println("printing the request material "+value);
try
{
Object actualval = getActualTypeValue(value,type);
paravalues[index] = actualval;
index++;
}catch(ServiceException se)
{
System.out.println(se.getMessage());
response.sendError(HttpServletResponse.SC_BAD_REQUEST);
return;
}
}
}
} // for loop
if(m.getReturnType().getName().equals("void"))
{
m.invoke(obj,paravalues);
}else
{
valueReturned = m.invoke(obj,paravalues);
Gson gson = new Gson();
String jsonString = gson.toJson(valueReturned);
response.getWriter().print(jsonString);
System.out.println("after getting all parameter method got called");
return;
}
} //else ends
}// if parameter length is not zero
else
{
if(m.getReturnType().getName().equals("void"))
{
m.invoke(obj);
}else
{
valueReturned = m.invoke(obj);
Gson gson = new Gson();
String jsonString = gson.toJson(valueReturned);
response.getWriter().print(jsonString);
System.out.println("before forwarding condition");
return;
}
}
String forwardTo = service.getForward();
if(forwardTo!=null)
{
Service s= map.get(forwardTo);
RequestDispatcher dispatcher=null;
if(s!=null)
{
String path = s.getPath();
dispatcher = request.getRequestDispatcher(path);
}else
{
dispatcher = request.getRequestDispatcher("/"+forwardTo);
}
try
{
dispatcher.forward(request,response);
}catch(Exception e)
{
e.printStackTrace();
}
}
pw.print("done");
pw.flush();
}catch(InvocationTargetException ite)
{
Throwable cause = ite.getCause();
cause.printStackTrace();
}
}else
{
pw.print("Service for "+url+" is not found");
pw.flush();
}
}catch(Exception e)
{
e.printStackTrace();
}
}
public Object getActualTypeValue(String value,Class fieldClass) throws ServiceException
{
Object actualValue=null;
try
{
if(fieldClass==String.class) return value;
if(fieldClass==int.class || fieldClass==Integer.class)
{
actualValue = (value==null)?0:Integer.parseInt(value);
}
else if(fieldClass==boolean.class || fieldClass==Boolean.class)
{
actualValue = (value==null)?false:Boolean.parseBoolean(value);
}
else if(fieldClass==double.class || fieldClass==Double.class)
{
actualValue = (value==null)?0.0:Double.parseDouble(value);
}
else if(fieldClass==byte.class || fieldClass==Byte.class)
{
actualValue = (value==null)?(byte)0:Byte.parseByte(value);
}
else if(fieldClass==Character.class || fieldClass==char.class)
{
actualValue = (value==null)? '\u0000':value.charAt(0);
}
else if(fieldClass==long.class || fieldClass==Long.class)
{
actualValue = (value==null)?0L:Long.parseLong(value);
}
else if(fieldClass==short.class || fieldClass==Short.class)
{
actualValue = (value==null)?(short)0:Short.parseShort(value);
}
else if(fieldClass==float.class|| fieldClass==Float.class)
{
actualValue = (value==null)?0.0f:Float.parseFloat(value);
}else
{
throw new ServiceException("Invalid argument: expected type "+fieldClass); 
}
}catch(Exception e)
{
throw new ServiceException("Invalid type of argument");
}
return actualValue;
}
}