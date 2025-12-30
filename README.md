# JWebRock Framework

JWebRock is a **lightweight Java web framework** built on top of the **Servlet API**, designed to help developers and students understand how modern Java web frameworks work internally.

It focuses on **annotation-based service configuration**, **centralized request handling**, **dependency injection**, and **clean separation of concerns**, without relying on heavy external frameworks.

> ⚠️ JWebRock is **not a replacement for Spring or Spring Boot**.  
> It is an **educational and experimental framework** created to demonstrate core backend framework concepts.

---

## Key Features

- Annotation-based service and method mapping
- Centralized front-controller architecture
- JSON request and response handling
- GET and POST request support
- Request parameter binding
- Dependency injection
- Request forwarding between services
- Security guard support using annotations
- Automatic JavaScript client generation
- PDF-based service documentation generation
- Clean separation of framework and application logic

---

## Core Components

JWebRock consists of the following major components:

1. **MyWebRockStarter Servlet** – Framework initialization and metadata scanning
2. **MyWebRock Servlet** – Central request dispatcher (Front Controller)
3. **Raka Servlet** – JavaScript file serving servlet
4. **ServiceDoc Tool** – Service documentation generator (PDF)

---

## 1. MyWebRockStarter Servlet

The `JWebRockStarter` servlet initializes the framework at application startup.

### Configuration (web.xml)

Map the servlet to `/startup` and define the following initialization parameters.

### Initialization Parameters

#### 1. `SERVICE_PACKAGE_PREFIX`

- **Description:** Base package to scan for service classes
- **Example:** `com.services.pojo`
- **Requirement:**  
  The package must exist inside the compiled `classes` directory

All classes inside this package are scanned, and framework metadata is generated.

#### 2. `Second Parameter`

- **Name**: jsFile
- **Value**: Name of a JavaScript file.

The servlet automatically generates this JS file in the WEB-INF/js folder. If the folder does not exist, it will be created automatically. If no filename is provided, multiple JS files will be generated, each containing an equivalent JS class for the Java service class files.


---

### Service Mapping using `@Path`

Services and their methods are mapped using the `@Path` annotation.

```java
@Path("student")
public class StudentService {

    @Path("add")
    public void addStudent() {
        ...
    }
}
```

## JavaScript Client Generation (`jsFile`)

**Description:**  
Specifies the name of the JavaScript file to be generated for client-side access to services.

**Output Location:**  

WEB-INF/js/

**Behavior:**
- The `js` folder is created automatically if it does not exist
- If no filename is provided:
  - Multiple JavaScript files are generated
  - Each file corresponds to a Java service class
- Generated JavaScript files act as **client-side proxies** for Java services

---

## MyWebRock Servlet (Front Controller)

The **JWebRock servlet** acts as the **central dispatcher** of the framework.

### Configuration

In web.xml, map this servlet to the URL pattern /framework/*

All requests with /framework/serviceName are handled by this servlet.

The servlet looks up the requested path in its data structure and serves it.

---

### Request Flow

1. Client sends a request
2. JWebRock servlet resolves the requested service path
3. Request parameters or JSON payload are parsed
4. Target service method is invoked
5. Response is generated and returned to the client

---

### JSON Request Handling

- **HTTP Method:** `POST`
- **Content-Type:** `application/json`

**Behavior:**
- JSON payload is parsed into Java objects
- Parsed objects are automatically passed to service methods

---

## Supported Annotations

| Annotation | Description |
|---------|------------|
| `@Path` | Maps a class or method to a URL path |
| `@RequestParameter("name")` | Binds request parameters to method arguments |
| `@AutoWired("fieldName")` | Injects request parameters into class fields |
| `@Get("path")` | Allows only GET requests |
| `@Post("path")` | Allows only POST requests |
| `@Forward("servicePath")` | Forwards the request after execution |
| `@SecuredAccess` | Make Service Secure |
| `@InjectSessionScope` | Injects session scope |
| `@InjectApplicationScope` | Injects application scope |
| `@InjectRequestScope` | Injects request scope |
| `@InjectApplicationDirectory` | Injects Project Directory name |



> Scope injection requires setter methods such as `setSessionScope()`.

---

## Security Support

### `@SecuredAccess`

```java
@SecuredAccess(checkPost="AuthGuard", guard="validate")

Behavior: Executes the specified guard method before service execution

If validation fails: Service execution is blocked

SC_SERVICE_UNREACHABLE response is returned
This mechanism enables custom authentication and authorization logic.

```
---


## Raka Servlet (JavaScript Serving)

```
The Raka servlet serves JavaScript files generated by JWebRock.

Configuration
Map the servlet to:  /jsFile
Usage Example  /jsFile?name=Student.js

Notes:
JavaScript files are served from WEB-INF/js
Any file inside this folder can be accessed securely

```
---

## ServiceDoc Tool
ServiceDoc is a standalone utility that generates PDF documentation for service classes.

  ### Information Included
- Fully qualified class names
- Method names
- Applied annotations
- Parameters and their annotations

```
Usage : java -cp <classpath> ServiceDoc <package-name> <output-pdf>

Example : java -cp c:\itext7-core.jar;c:\tomcat11\myproject\classes; 
ServiceDoc com.myapp c:\java\myproject\docs\classdoc.pdf
Notes
Ensure all iText7 JARs are included in the classpath
The base package is scanned recursively

```

### Using JWebRock in a Project


1. Copy the JAR mywebrock.jar to use in your web application:
2. Add in WEB-INF/lib/ of your project 
3. Configure required servlets in web.xml
4. Create service classes using JWebRock annotations


### Example Application

***SmartWallet – Mini Wallet Web Application***
***SmartWallet is a Java-based mini wallet web application built using the JWebRock framework.***

***Features***
- Secure user login
- Token-based authentication
- Dashboard with balance inquiry
- Fund transfer functionality
- Transaction history viewing
- Logout support


*This project serves as a reference implementation to demonstrate the real-world usage and working of the JWebRock framework JAR.*

**📌 Repository:**
SmartWallet-JWebRock - https://github.com/BrajRaj89/SmartWallet
