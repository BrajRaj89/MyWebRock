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
import java.util.concurrent.ConcurrentHashMap;

public class MyWebRock extends HttpServlet
{
private final Map<Class<?>,Object> beanMap = new ConcurrentHashMap<>();
private final Set<Class<?>> creating = ConcurrentHashMap.newKeySet();

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
if(beanMap.containsKey(clazz))
{
return beanMap.get(clazz);
}
if(creating.contains(clazz))
{
throw new RuntimeException("Circular dependency detected: " + clazz.getName());
}
creating.add(clazz);
try
{
if(clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()))
{
throw new RuntimeException("Cannot instantiate interface/abstract class: " + clazz.getName());
}
Constructor<?>[] constructors = clazz.getDeclaredConstructors();
if(constructors.length != 1)
{
throw new RuntimeException("Class must have exactly ONE constructor: " + clazz.getName());
}
Constructor<?> constructor = constructors[0];
constructor.setAccessible(true);
Object instance;
if(constructor.getParameterCount() == 0)
{
instance = constructor.newInstance();
}
else
{
Class<?>[] paramTypes = constructor.getParameterTypes();
Object[] params = new Object[paramTypes.length];
for(int i = 0; i < paramTypes.length; i++)
{
params[i] = getBean(paramTypes[i]);
}
instance = constructor.newInstance(params);
}
beanMap.put(clazz, instance);
return instance;
}catch(Exception e)
{
throw new RuntimeException("Failed to create bean: " + clazz.getName(), e);
}
finally
{
creating.remove(clazz);
}
}

public void processRequest(HttpServletRequest request,HttpServletResponse response,String requestType)
{
try
{
Gson gson =null;
gson = new Gson();
String url = request.getPathInfo();
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
Method m = service.getService();
if(requestType.equals("get"))
{
if(!service.getGetAllowed())
{
response.setStatus(405);
response.getWriter().write(gson.toJson(Map.of("error", true,"message", "HTTP method not allowed for this endpoint")));
return;
}
}else if(requestType.equals("post"))
{
if(!service.getPostAllowed())
{
response.setStatus(405);
response.getWriter().write(gson.toJson(Map.of("error", true,"message", "HTTP method not allowed for this endpoint")));
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
RequestScope requestScope = new RequestScope(request);
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
if(paramClass==RequestScope.class)
{
objOfparams[index] = requestScope;
index++;
}else if(paramClass==SessionScope.class)
{
objOfparams[index] = sessionScope;
index++;
}else if(paramClass==ApplicationScope.class)
{
objOfparams[index] = applicationScope;
index++;
}
} 
try
{
Object ssobj = getBean(securedService);
boolean flag = (boolean)guard.invoke(ssobj,objOfparams);
if(!flag)
{
response.setStatus(HttpServletResponse.SC_FORBIDDEN);
response.setContentType("application/json");
response.getWriter().write(gson.toJson(Map.of("error", true,"message", "Access denied:")));
return;
}
}catch(Exception e)
{
response.setStatus(500);
response.setContentType("application/json");
response.getWriter().write(
gson.toJson(Map.of("error", true,"message", "Authorization check failed")));
return;
}
} 
}
if(service.getInjectSessionScope())
{
try
{
Method method = clazz.getMethod("setSessionScope",SessionScope.class);
if(method!=null) method.invoke(bean,sessionScope);
}catch(NoSuchMethodException e)
{
response.setStatus(500);
response.setContentType("application/json");
response.getWriter().write(
gson.toJson(Map.of("error", true,"message", "Server configuration error: setSessionScope missing")));
return;
}
}
if(service.getInjectRequestScope())
{
try
{
Method method = clazz.getMethod("setRequestScope",RequestScope.class);
if(method!=null) method.invoke(bean,requestScope);
}catch(NoSuchMethodException e)
{
response.setStatus(500);
response.setContentType("application/json");
response.getWriter().write(
gson.toJson(Map.of("error", true,"message", "Server configuration error: setRequestScope missing")));
return;
}
}
if(service.getInjectApplicationScope())
{
try
{
Method method  = clazz.getMethod("setApplicationScope",ApplicationScope.class);
if(method!=null) method.invoke(bean,applicationScope);
}catch(NoSuchMethodException e)
{
response.setStatus(500);
response.setContentType("application/json");
response.getWriter().write(
gson.toJson(Map.of("error", true,"message", "Server configuration error: setApplicationScope missing")));
return;
}
}
if(service.getInjectApplicationDirectory())
{
try
{
Method method  = clazz.getMethod("setApplicationDirectory",ApplicationDirectory.class);
if(method!=null) method.invoke(bean,applicationDirectory);
}catch(NoSuchMethodException e)
{
response.setStatus(500);
response.setContentType("application/json");
response.getWriter().write(
gson.toJson(Map.of("error", true,"message", "Server configuration error: setApplicationDirectory missing")));
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
}catch(Exception se)
{
response.setStatus(400);
response.setContentType("application/json");
response.getWriter().write(gson.toJson(Map.of("error", true,"message", se.getMessage())));
return;
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
}catch(Exception se)
{
System.out.println(se.getMessage());
response.getWriter().write(se.getMessage());
}
}
}

Parameter parameters[] = m.getParameters();
Object valueReturned = null;
Object paravalues[] = null;

if (parameters.length > 0)
{
paravalues = new Object[parameters.length];
String contentType = request.getContentType();
boolean isJson = (contentType != null && contentType.startsWith("application/json"));
if(isJson)
{
if(!requestType.equals("post") && !requestType.equals("put"))
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
return;
}
if(parameters.length != 1)
{
response.sendError(400, "Only one JSON parameter is allowed");
return;
}
Parameter p = parameters[0];
Class<?> type = p.getType();
if(type == HttpServletRequest.class || type == HttpSession.class || type == ServletContext.class)
{
response.sendError(400, "Invalid JSON parameter type");
return;
}
BufferedReader reader = request.getReader();
StringBuilder sb = new StringBuilder();
String line;
while ((line = reader.readLine()) != null) sb.append(line);
Object jsonObj=null;
try
{
jsonObj = gson.fromJson(sb.toString(), type);
}catch (com.google.gson.JsonSyntaxException je)
{
response.setStatus(400);
response.getWriter().write(gson.toJson(Map.of("error", true,"message", "Malformed JSON request body")));
return;
}
paravalues[0] = jsonObj;
}
else
{
int index = 0;
for(Parameter para : parameters)
{
Class<?> type = para.getType();
boolean resolved = false;
if(type == HttpServletRequest.class)
{
paravalues[index++] = request;
resolved = true;
}
else if (type == HttpSession.class)
{
paravalues[index++] = session;
resolved = true;
}
else if (type == ServletContext.class)
{
paravalues[index++] = servletContext;
resolved = true;
}
if (!resolved)
{
RequestParameter rp = para.getAnnotation(RequestParameter.class);
if (rp == null)
{
response.sendError(400,"Missing @RequestParameter for parameter: " + para.getName());
return;
}
String rawValue = request.getParameter(rp.value());
try
{
Object actualValue = getActualTypeValue(rawValue, type);
paravalues[index++] = actualValue;
}
catch(Exception se)
{
response.sendError(400, se.getMessage());
return;
}
}
}
}
try
{
if (m.getReturnType() == void.class)
{
m.invoke(bean, paravalues);
return;
}
else
{
valueReturned = m.invoke(bean, paravalues);
if(forwardTo == null)
{
if(valueReturned == null)
{
response.getWriter().write("null");
}
else if(valueReturned instanceof String || valueReturned instanceof Number || valueReturned instanceof Boolean || valueReturned instanceof Character)
{
response.getWriter().print(valueReturned);
}
else
{
response.setContentType("application/json");
response.getWriter().write(gson.toJson(valueReturned));
}
return;
}
}
}
catch(InvocationTargetException ite)
{
Throwable cause = ite.getCause();
response.setContentType("application/json");
response.getWriter().write(new Gson().toJson(Map.of("error", true,"message", cause.getMessage())));
}
}else
{
valueReturned = m.invoke(bean);
if(forwardTo == null)
{
if (valueReturned == null)
{
response.getWriter().write("null");
}
else if(valueReturned instanceof String || valueReturned instanceof Number ||valueReturned instanceof Boolean || valueReturned instanceof Character)
{
response.getWriter().print(valueReturned);
}
else
{
response.setContentType("application/json");
response.getWriter().write(gson.toJson(valueReturned));
}
return;
}
}
}catch(Exception se)
{
response.setStatus(500);
response.setContentType("application/json");
response.getWriter().write(gson.toJson(Map.of("error", true,"message", "Internal server error")));
return;
}
}
}catch(Exception e)
{
try
{
response.setStatus(400);
String json = new Gson().toJson(Map.of("error", true,"message",e.getMessage()));
response.getWriter().write(json);
}catch(Exception ee)
{
System.out.println(ee.getMessage());
}
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
System.out.println(e.getMessage());
throw new ServiceException("Invalid type of argument");
}
return actualValue;
}
} 