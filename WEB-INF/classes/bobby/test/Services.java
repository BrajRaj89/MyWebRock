package bobby.test;
import com.thinking.machines.webrock.annotations.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

@Path("Services")
public class Services
{
@Get("response1")
@Path("response1")
@Forward("index.html")
public void getTypeResponse()
{
System.out.println("Response1 got called_______________________");
}
@Post("response2")
@Path("response2")
@Forward("index.jsp")
public void postTypeResponse()
{
System.out.println("Response2 got called_______________________");
}

@OnStartup(priority=1)
public void runOnStartup1()
{
System.out.println("runOnStartup with priority 1 executed");
}

@OnStartup(priority=2)
public int runOnStartup2()
{
System.out.println("runOnStartup with priority 2 executed");
return 2000;
}

@OnStartup(priority=3)
public void runOStartup3(int var)
{
System.out.println("runOnStartup with priority 3 executed");
}
}