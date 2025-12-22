package com.webrock;
import com.webrock.annotations.*;
import com.webrock.model.*;
import com.webrock.pojo.*;
import com.webrock.scope.*;
import com.webrock.exceptions.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import com.google.gson.*;
public class MyWebRock extends HttpServlet
{
private Map<Class<?>,Object> Bean_map = new HashMap<>();
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
public Object getBean(Class<?> clazz)
{
if(Bean_map.containsKey(clazz))
{
return Bean_map.get(clazz);
}
try
{
Constructor<?>[] constructors = clazz.getConstructors();
Constructor<?> targetConstructor = null;
for (Constructor<?> c : constructors)
{
if(c.getParameterCount() > 0)
{
targetConstructor = c;
break;
}
}
Object obj;
if(targetConstructor == null)
{
Constructor<?> defaultConstructor = clazz.getDeclaredConstructor();
defaultConstructor.setAccessible(true);
obj = defaultConstructor.newInstance();
} 
// 4. Else → resolve dependencies
else
{
Class<?>[] paramTypes = targetConstructor.getParameterTypes();
Object[] params = new Object[paramTypes.length];
for (int i = 0; i < paramTypes.length; i++)
{
params[i] = getBean(paramTypes[i]);
}
obj = targetConstructor.newInstance(params);
}
// 5. Cache singleton
Bean_map.put(clazz, obj);
return obj;
}catch(Exception e)
{
throw new RuntimeException("Failed to create bean: " + clazz.getName(), e);
}
}
public void processRequest(HttpServletRequest request,HttpServletResponse response,String requestType)
{
try
{
Gson gson =null;
gson = new Gson();
String url = request.getPathInfo();
System.out.println("request Arrived for url->"+url);
ServletContext context = getServletContext();
Map<String,Service> map = (Map<String,Service>)context.getAttribute("services");
Service service = map.get(url);
if(service == null)
{
String resourcePath = url; 
if(resourcePath == null) resourcePath = "/";
String realPath = context.getRealPath(resourcePath);
if(realPath != null)
{
File staticFile = new File(realPath);
if(staticFile.exists() && staticFile.isFile())
{
System.out.println(request.getContextPath()+resourcePath);
response.sendRedirect(request.getContextPath() + resourcePath);
return;
}
}
response.sendError(HttpServletResponse.SC_NOT_FOUND);
return;
}else
{
try
{
Class<?> clazz = service.getServiceClass();
Object bean = getBean(clazz); 
if(bean==null)
{
bean = clazz.getDeclaredConstructor().newInstance();
System.out.println("object for class "+url+" not found in data structure");
}else
{
System.out.println("object for class "+url+" found in data structure");
}
Method m = service.getService();
if(requestType.equals("get"))
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
String forwardTo = service.getForward();
HttpSession session = request.getSession();
ServletContext servletContext = getServletContext();
String path = context.getRealPath("/");
ApplicationDirectory applicationDirectory = new ApplicationDirectory(path);
SessionScope sessionScope = new SessionScope(session);
ApplicationScope applicationScope = new ApplicationScope(servletContext);

if(service.getSecuredService())
{
Class<?> securedService = service.getCheckPost();
Method guard = service.getGuard();
if(securedService!=null && guard!=null)
{
Parameter paramsOfss[] = guard.getParameters();
Object objOfparams[] = new Object[paramsOfss.length];
int index=0;
for(Parameter param:paramsOfss)
{
Class<?> paramClass = param.getType();
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
Object ssobj = securedService.getDeclaredConstructor().newInstance();
boolean flag = (boolean)guard.invoke(ssobj,objOfparams);
if(!flag)
{
System.out.println("Unauthorized access");

return;
}
}catch(Exception e)
{
System.out.println(e.getMessage());
response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
return;
}
} // if has class and guard
}// if it is secured service
if(service.getInjectSessionScope())
{
try
{
Method method = clazz.getMethod("setSessionScope");
if(method!=null) method.invoke(bean,sessionScope);
}catch(NoSuchMethodException e)
{
System.out.println("The method setSessionScope Not Found");
response.getWriter().write("The method setSessionScope Not Found");
return;
}
}
if(service.getInjectRequestScope())
{
RequestScope requestScope = new RequestScope(request);
try
{
Method method = clazz.getMethod("setRequestScope");
if(method!=null) method.invoke(bean,requestScope);
}catch(NoSuchMethodException e)
{
System.out.println("The method setRequestScope Not Found");
response.getWriter().write("The method setRequestScope Not Found");
return;
}
}
if(service.getInjectApplicationScope())
{
try
{
Method method  = clazz.getMethod("setApplicationScope");
if(method!=null) method.invoke(bean,applicationScope);
}catch(NoSuchMethodException e)
{
System.out.println("The method setApplicationScope Not Found");
response.getWriter().write("The method setApplicationScope  Not Found");
return;
}
}
if(service.getInjectApplicationDirectory())
{
try
{
Method method  = clazz.getMethod("setApplicationDirectory");
if(method!=null) method.invoke(bean,applicationDirectory);
}catch(NoSuchMethodException e)
{
System.out.println("The method setApplicationDirectory  Not Found");
response.getWriter().write("The method setApplicationDirectory Not Found");
return;
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
Class<?> fieldType= field.getType();
Object actualValue = null;
String value = null;
value = request.getParameter(name);
if(value==null) continue;
try
{
actualValue = getActualTypeValue(value,fieldType);
field.set(bean,actualValue);
}catch(ServiceException se)
{
System.out.println(se.getMessage());
}
}
}
Field fields[] = clazz.getDeclaredFields();
for(Field f:fields)
{
InjectRequestParameter injectf = f.getAnnotation(InjectRequestParameter.class);
if(injectf!=null)
{
String name  = injectf.value();
Class<?> type = f.getType();
String value = request.getParameter(name);
if(value==null) continue;
try
{
Object acval = getActualTypeValue(value,type);
f.setAccessible(true);
f.set(bean,acval);
}catch(ServiceException se)
{
System.out.println(se.getMessage());
response.getWriter().write(se.getMessage());
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
if(requestDataType!=null)
{
if(requestDataType.equals("application/json"))
{
if(requestType.equals("get"))
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}
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
response.setContentType("text/plain");
response.getWriter().write(message);
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
}
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
if(annoOnParam!=null)
{
String value=null;
if(requestType.equals("get") || requestType.equals("delete"))
{
value = request.getParameter(annoOnParam.value());
}else
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}
try
{
Object actualval = getActualTypeValue(value,type);
paravalues[index] = actualval;
index++;
}catch(ServiceException se)
{
System.out.println("Error while getting the actual param");
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
}
else
{
if(m.getReturnType().getName().equals("void"))
{
m.invoke(bean);
}else
{
valueReturned = m.invoke(bean);
if(forwardTo==null)
{
if(valueReturned.getClass().isPrimitive())
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
response.setContentType("text/plain");
response.getWriter().write(message.replace("'", "\\'"));
}
}
}catch(Exception e)
{
System.out.println(e.getMessage());
e.printStackTrace();
}
}
public Object getActualTypeValue(String value,Class<?> fieldClass) throws ServiceException
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
System.out.println("string value from request "+value);
System.out.println("field class "+fieldClass);
System.out.println(e.getMessage());
throw new ServiceException("Invalid type of argument");
}
return actualValue;
}
} 