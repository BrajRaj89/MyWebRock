# MyWebRock Documentation

This project contains three main servlets: TMWebRockStarter Servlet, TMWebRock Servlet, and Raka Servlet, along with a tool called ServiceDoc,
which generates a PDF containing information about the service classes.

## 1. TMWebRockStarter Servlet

In web.xml, map this starter servlet to the URL /startup and define initialization parameters.

### First Parameter:

Name: SERVICE_PACKAGE_PREFIX

Value: The package name (e.g., com.services.pojo).

Requirement: The package must exist in the classes folder of the project.

The Starter Servlet scans all classes within this package and creates a data structure to store the required metadata.

To enable this feature, use the @Path annotation:

On a class: @Path("className")

On a method: @Path("methodName")

### Second Parameter:

Name: jsFile

Value: Name of a JavaScript file.

The servlet automatically generates this JS file in the WEB-INF/js folder. If the folder does not exist, it will be created automatically. If no filename is provided, multiple JS files will be generated, each containing an equivalent JS class for the Java service class files.

## 2. TMWebRock Servlet

In web.xml, map this servlet to the URL pattern /my/*

All requests with /my/serviceName are handled by this servlet.

The servlet looks up the requested path in its data structure and serves it.

If the request includes parameters, they are parsed and passed as arguments to the service.

Handling JSON Requests:

Requires Content-Type: application/json

Requires Request Method: POST

The JSON is parsed into an object and passed to the service.

Annotations:

@RequestParameter("paramName") - binds request parameters to method parameters.

@AutoWired("fieldName") - automatically sets class fields from request parameters.

@Get("methodName"), @Post("methodName") - handle request methods like if a method contains @Get annotation then it will only served if the get type request is made.

 @Forward("methodName") the request is forwarded to specified service if the current service is completed and the request is forwarded 

@InjectSessionScope, @InjectApplicationScope, @InjectRequestScope - inject corresponding scope objects (requires setter methods like setSessionScope()).

@SecuredAccess(checkPost="className", guard="methodName") -if a class declared with this then before serving a request, it creates an instance of the checkPost class and executes the guard method. If successful, the service runs; otherwise, a SC_SERVICE_UNREACHABLE error is sent.

## 3. Raka Servlet

In web.xml, map this servlet to /jsFile

Requests can specify a JS file name
Example: /jsFile?name=Student.js

If the file exists in WEB-INF/js, it is served to the browser.

Any JS file in the folder can be served in this way.

## 4. ServiceDoc Tool

The ServiceDoc tool generates a PDF containing:

Class names (with package names)

Method names

Applied annotations

Parameters and their annotations

The tool is packaged as a JAR file in the ServiceDocTool folder.

Usage:

java -cp classpath_of_package ServiceDoc package-name  output-pdf

Example:

java -cp c:\itext7-core.jar;c:\tomcat11\myproject\classes; ServiceDoc com.myapp c:\java\myproject\docs\classdoc.pdf

Notes:

 classpath : Location of compiled .class files or JARs.

 package-name : Base package to scan (e.g., com.myapp).

 output-pdf : Full path for the generated PDF.

The tool uses iText7 to generate PDFs. Ensure all itext7.jar files are included in the classpath.
