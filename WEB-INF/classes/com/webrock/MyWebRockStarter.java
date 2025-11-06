package com.webrock;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import java.lang.annotation.*;
import com.webrock.annotations.*;
import com.webrock.pojo.*;
import com.webrock.model.*;

public class MyWebRockStarter extends HttpServlet
{
class onStart implements Comparable<onStart>
{
private Method m;
private Class<?> c;
private int priority;
public void setPriority(int priority)
{
this.priority = priority;
}
public int getPriority()
{
return this.priority;
}
public void setClassOfM(Class<?> c)
{
this.c = c;
}
public Class<?> getClassOfM()
{
return this.c;
}
public void setMethod(Method m)
{
this.m = m;
}
public Method getMethod()
{
return this.m;
}
public int compareTo(onStart other)
{
return Integer.compare(this.priority,other.priority);
}
}
public void init() throws ServletException
{
ServletContext context = getServletContext();
String contextPath = context.getContextPath();
Set<onStart> onStartobjs = new TreeSet<>();
Map<String,Service> services = new HashMap<>();
String packagename = getServletConfig().getInitParameter("SERVICE_PACKAGE_PREFIX");
try
{
String packageName = packagename;
String path = packageName.replace('.', '/');
ClassLoader loader = Thread.currentThread().getContextClassLoader();
URL resource = loader.getResource(path);
if(resource==null) 
{
throw new IllegalArgumentException("Package not found: " + packageName);
}
File dir = new File(resource.toURI());
if(!dir.exists() || !dir.isDirectory())
{
throw new IllegalArgumentException("Not a directory: " + dir.getAbsolutePath());
}
List<Class<?>> classes = new ArrayList<>();
for(File file : dir.listFiles())
{
if(file.getName().endsWith(".class") && !file.getName().contains("$"))
{
String className = packageName+"."+file.getName().replace(".class", "");
try
{
classes.add(Class.forName(className));
}catch(ClassNotFoundException e)
{
continue;
}
}
}
try
{
String pathForJs = getServletContext().getRealPath("WEB-INF");
File folder  = new File(pathForJs,"/js");
if(!folder.exists())
{
folder.mkdirs();
}
String fileName = getServletConfig().getInitParameter("jsFile");
boolean flag=false;
File DestinationFile=null;
RandomAccessFile raf=null;

if(fileName==null || fileName.length()==0)
{
flag=true;
}else
{
if(!fileName.contains("/")) fileName = "/"+fileName;
DestinationFile = new File(folder+fileName);
if(DestinationFile.exists())
{
DestinationFile.delete();
}
raf = new RandomAccessFile(DestinationFile,"rw");
}

for(Class<?> clazz:classes)
{
String className = clazz.getSimpleName(); 
if(flag)
{
fileName ="/"+className+".js";
DestinationFile = new File(folder+fileName);
if(DestinationFile.exists())
{
DestinationFile.delete();
}
raf = new RandomAccessFile(DestinationFile,"rw");
}
raf.writeBytes("class "+className+"\n");
raf.writeBytes("{\n");
Field fields[] = clazz.getDeclaredFields();
Class<?> fieldsClass[]=new Class[fields.length];
if(fields.length!=0)
{
int index=0;
for(Field f:fields)
{
raf.writeBytes(f.getName()+";\n");
fieldsClass[index] = f.getType();
index++;
}
try
{
Constructor<?> cons= clazz.getDeclaredConstructor(fieldsClass);
raf.writeBytes("constructor(");
int count=0;
for(Field f:fields)
{
if(count>0) raf.writeBytes(",");
raf.writeBytes(f.getName());
count++;
}
raf.writeBytes(")\n");
raf.writeBytes("{\n");
for(Field f:fields)
{
raf.writeBytes("this."+f.getName()+"="+f.getName()+";\n");
}
raf.writeBytes("}\n");
}catch(NoSuchMethodException e)
{
//do nothing
}
}
Method methods[] = clazz.getDeclaredMethods();
for(Method m:methods)
{
String name = m.getName();
raf.writeBytes(name+"(");
Class returnType = m.getReturnType();
Parameter params[] = m.getParameters();
int count=0;
for(Parameter para:params)
{
if(count>0) raf.writeBytes(",");
Class<?> paraType = para.getType();
raf.writeBytes(para.getName());
count++;
}
raf.writeBytes(")\n");
raf.writeBytes("{\n");
String acName = name;
name = name.toLowerCase();
for(Field f:fields)
{
String fn = f.getName();
if(name.contains("set")) 
{
for(Parameter p:params)
{
if(f.getType().equals(p.getType()))
{
String mtdName=null;
try
{
String msn = acName.substring(3);
char c = Character.toLowerCase(msn.charAt(0));
mtdName = c+msn.substring(1);
}catch(Exception e)
{
System.out.println(e.getMessage());
}
if(fn.equals(mtdName)) raf.writeBytes("this."+fn+"="+p.getName()+";\n");
}
}
}
if(name.contains("get"))
{
if(f.getType().equals(returnType))
{
String mtdName=null;
try
{
String msn = acName.substring(3);
char c = Character.toLowerCase(msn.charAt(0));
mtdName = c+msn.substring(1);
}catch(Exception e)
{
System.out.println(e.getMessage());
}
if(fn.equals(mtdName)) raf.writeBytes("return this."+fn+";\n");
}
}
}
if(name.contains("add"))
{
String requesturl= contextPath+"/framework/"+className+"/"+m.getName();
raf.writeBytes("var requesturl='"+requesturl+"';\n");
raf.writeBytes("var promise = new Promise(function(resolve,reject){\n");
raf.writeBytes("$.ajax({url:requesturl,\n");
raf.writeBytes("type:'POST',\n");
raf.writeBytes("data:JSON.stringify("+params[0].getName()+"),\n");
raf.writeBytes("contentType:'application/json',\n");
raf.writeBytes("success:function(response)\n");
raf.writeBytes("{\n");
raf.writeBytes("resolve(response);\n");
raf.writeBytes("},\n");
raf.writeBytes("error:function(jqXHR, textStatus, errorThrown)\n");
raf.writeBytes("{\n");
raf.writeBytes("let message = errorThrown || textStatus || 'Unknown AJAX error';\n");
raf.writeBytes("reject(new Error(message));\n");
raf.writeBytes("}\n");
raf.writeBytes("});\n");
raf.writeBytes("});\n");
raf.writeBytes("return promise;\n");
}
else if(name.contains("update"))
{
String requesturl= contextPath+"/framework/"+className+"/"+m.getName();
raf.writeBytes("var requesturl='"+requesturl+"';\n");
raf.writeBytes("var promise = new Promise(function(resolve,reject){\n");
raf.writeBytes("$.ajax({url:requesturl,\n");
raf.writeBytes("type:'POST',\n");
raf.writeBytes("contentType:'application/json',\n");
raf.writeBytes("data:JSON.stringify("+params[0].getName()+"),\n");
raf.writeBytes("success:function(response)\n");
raf.writeBytes("{\n");
raf.writeBytes("resolve(response);\n");
raf.writeBytes("},\n");
raf.writeBytes("error: function(jqXHR, textStatus, errorThrown)\n");
raf.writeBytes("{\n");
raf.writeBytes("let message = errorThrown || textStatus || 'Unknown AJAX error';\n");
raf.writeBytes("reject(new Error(message));\n");
raf.writeBytes("}\n");
raf.writeBytes("});\n");
raf.writeBytes("});\n");
raf.writeBytes("return promise;\n");
}else if(name.contains("delete"))
{
String methodName=null;
for(Method ms:methods)
{
String nameOfm = ms.getName();
if(nameOfm.startsWith("getBy"))
{
methodName = nameOfm;
}
}
String mname=null;
String pn=null;
String pname=null;
try
{
mname = m.getName();
pn = methodName.substring(5);
char c = Character.toLowerCase(pn.charAt(0));
pname= c+pn.substring(1);
}catch(Exception e)
{
System.out.println(e.getMessage());
}
String requesturl=contextPath+"/framework/"+className+"/"+mname+"?"+pname+"=";
raf.writeBytes("var requesturl='"+requesturl+"'+"+params[0].getName()+";\n");
raf.writeBytes("var promise =new Promise(function(resolve,reject){\n");
raf.writeBytes("$.ajax({url:requesturl,\n");
raf.writeBytes("type:'DELETE',\n");
raf.writeBytes("success:function(response)\n");
raf.writeBytes("{\n");
raf.writeBytes("resolve(response);\n");
raf.writeBytes("},\n");
raf.writeBytes("error: function(jqXHR, textStatus, errorThrown)\n");
raf.writeBytes("{\n");
raf.writeBytes("let message = errorThrown || textStatus || 'Unknown AJAX error';\n");
raf.writeBytes("reject(new Error(message));\n");
raf.writeBytes("}\n");
raf.writeBytes("});\n");
raf.writeBytes("});\n");
raf.writeBytes("return promise;\n");
}
else if(name.contains("getby"))
{
String mname=null;
String pn=null;
String pname=null;
try
{
mname = m.getName();
pn = mname.substring(5);
char c = Character.toLowerCase(pn.charAt(0));
pname= c+pn.substring(1);
}catch(Exception e)
{
System.out.println(e.getMessage());
}
String requesturl=contextPath+"/framework/"+className+"/"+mname+"?"+pname+"=";
raf.writeBytes("var requesturl='"+requesturl+"'+"+params[0].getName()+";\n");
raf.writeBytes("var promise = new Promise(function(resolve,reject){\n");
raf.writeBytes("$.ajax({url:requesturl,\n");
raf.writeBytes("type:'GET',\n");
raf.writeBytes("success:function(response)\n");
raf.writeBytes("{\n");
raf.writeBytes("resolve(response);\n");
raf.writeBytes("},\n");
raf.writeBytes("error: function(jqXHR, textStatus, errorThrown)\n");
raf.writeBytes("{\n");
raf.writeBytes("let message = errorThrown || textStatus || 'Unknown AJAX error';\n");
raf.writeBytes("reject(new Error(message));\n");
raf.writeBytes("}\n");
raf.writeBytes("});\n");
raf.writeBytes("});\n");
raf.writeBytes("return promise;\n");
}else if(name.contains("getall"))
{
String requesturl=contextPath+"/framework/"+className+"/"+m.getName();
raf.writeBytes("var requesturl='"+requesturl+"';\n");
raf.writeBytes("var promise = new Promise(function(resolve,reject){\n");
raf.writeBytes("$.ajax({url:requesturl,\n");
raf.writeBytes("type:'GET',\n");
raf.writeBytes("success:function(response)\n");
raf.writeBytes("{\n");
raf.writeBytes("resolve(response);\n");
raf.writeBytes("},\n");
raf.writeBytes("error: function(jqXHR, textStatus, errorThrown)\n");
raf.writeBytes("{\n");
raf.writeBytes("let message = errorThrown || textStatus || 'Unknown AJAX error';\n");
raf.writeBytes("reject(new Error(message));\n");
raf.writeBytes("}\n");
raf.writeBytes("});\n");
raf.writeBytes("});\n");
raf.writeBytes("return promise;\n");
}
raf.writeBytes("}\n");
}
raf.writeBytes("}\n");
if(flag)
{
raf.close();
}
}
raf.close();
}catch(Exception e)
{
e.printStackTrace();
}
String fullPath =null;
for(Class<?> clazz:classes)
{ 
Method methods[] = clazz.getMethods();
for(Method m:methods)
{
OnStartup onStartup = m.getAnnotation(OnStartup.class);
if(onStartup!=null)
{
Class<?> returnType =  m.getReturnType();
Class<?> parameters[] = m.getParameterTypes();
if(parameters.length!=0)
{
System.out.println("Method with zero parameter is allowed");
continue;
}
if(!returnType.getName().equals("void"))
{
System.out.println("this is the wrong method a method with return type void is allowed on startup");
continue;
}
int priority = onStartup.priority();
onStart os = new onStart();
os.setPriority(priority);
os.setMethod(m);
os.setClassOfM(clazz);
onStartobjs.add(os);
}
}

Path annoOnC = (Path)clazz.getAnnotation(Path.class);
if(annoOnC!=null)
{
Get getOnC = (Get)clazz.getAnnotation(Get.class);
Post postOnC = (Post)clazz.getAnnotation(Post.class);
InjectSessionScope  injectSessionScope = (InjectSessionScope)clazz.getAnnotation(InjectSessionScope.class);
InjectRequestScope  injectRequestScope  = (InjectRequestScope)clazz.getAnnotation(InjectRequestScope.class);
InjectApplicationScope injectApplicationScope = (InjectApplicationScope)clazz.getAnnotation(InjectApplicationScope.class);
InjectApplicationDirectory injectApplicationDirectory= (InjectApplicationDirectory)clazz.getAnnotation(InjectApplicationDirectory.class);
SecuredAccess securedAccessOnC = (SecuredAccess)clazz.getAnnotation(SecuredAccess.class);
Field fields[] = clazz.getDeclaredFields();
List<Autowired> listOfWired  = new ArrayList<>();
for(Field f:fields)
{
AutoWired annoOnF = f.getAnnotation(AutoWired.class);
if(annoOnF!=null)
{
Autowired wired = new Autowired(f.getName(),f);
listOfWired.add(wired);
}
}
for(Method m:methods)
{
Path annoOnM = m.getAnnotation(Path.class);
Service sobj =null;
String forwardto =null;
if(annoOnM!=null)
{
String classN = annoOnC.value();
String methodN = annoOnM.value();
Forward forwardA = m.getAnnotation(Forward.class);
Get getOnM = m.getAnnotation(Get.class);
Post postOnM= m.getAnnotation(Post.class);
fullPath = "/"+classN+"/"+methodN;
sobj = new Service();
sobj.setServiceClass(clazz);
sobj.setPath(fullPath);
sobj.setService(m);
if(listOfWired.size()!=0) sobj.setAutoWired(listOfWired);
else sobj.setAutoWired(null);
if(securedAccessOnC!=null)
{
String securedServiceClass = securedAccessOnC.checkPost();
String guardName = securedAccessOnC.guard();
sobj.setSecuredService(true);
Class<?> sac = Class.forName(securedServiceClass);
Method methodsOfSc[] = sac.getDeclaredMethods();
Method methodOnsc=null;
for(Method method:methodsOfSc)
{
if(method.getName().equals(guardName))
{
methodOnsc = method;
break;
}
}
sobj.setSecuredService(true);
sobj.setCheckPost(sac);
sobj.setGuard(methodOnsc);
}
if(forwardA!=null)
{
forwardto = forwardA.value();
sobj.setForward(forwardto);
}
if(getOnC!=null)
{
sobj.setGetAllowed(true);
}else if(postOnC!=null)
{
sobj.setPostAllowed(true);
}
else if(getOnM!=null)
{
sobj.setGetAllowed(true);
}
else if(postOnM!=null)
{
sobj.setPostAllowed(true);
}else
{
sobj.setPostAllowed(true);
sobj.setGetAllowed(true);
}
if(injectSessionScope!=null)
{
sobj.setInjectSessionScope(true);
}
if(injectRequestScope!=null)
{
sobj.setInjectRequestScope(true);
}
if(injectApplicationScope!=null)
{
sobj.setInjectApplicationScope(true);
}
if(injectApplicationDirectory!=null)
{
sobj.setInjectApplicationDirectory(true);
}
services.put(fullPath,sobj);
}
}
}
}
context.setAttribute("services",services);
for(onStart os:onStartobjs)
{
try
{
Class<?> c = os.getClassOfM();
Object obj = c.getDeclaredConstructor().newInstance();
Method m = os.getMethod();
m.invoke(obj);
}catch(InvocationTargetException e)
{
e.printStackTrace();
}
}
}catch(Exception e)
{
e.printStackTrace();
}
}
}