package bobby.test;
import com.webrock.annotations.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.webrock.pojo.*;
import com.webrock.scope.*;

@Path("Services2")
@InjectSessionScope
public class Services2
{
@AutoWired(name="student")
private  Student student;

private SessionScope sessionScope;
private ServletContext servletContext;
public void setSessionScope(SessionScope sessionScope)
{
this.sessionScope = sessionScope;
}
public SessionScope getSessionScope()
{
return this.sessionScope;
}
@Get("response1")
@Path("response1")
// @Forward("index.jsp")
public boolean getTypeResponse( @RequestParameter("var1") String var1, @RequestParameter("var2") String var2 )
{
System.out.println("Response1 got called_______________________");
System.out.println("Request variable is "+var1+"and "+var2);
return true;
}
@Post("response2")
@Path("response2")
// @Forward("/resources/login.html")
public Object postTypeResponse()
{
System.out.println("Response2 got called_______________________");
// System.out.println("student name :"+stu.getName());
User us = new User();
us.username = "brajraj";
us.balance = "50000000";
return us;
}

@Post("response3")
@Path("response3")
@Forward("response.jsp")
public void postTypeResponse(Student student,String data,int count)
{
System.out.println("Response2 got called_______________________");
System.out.println("student name :"+student.getName()+" student gender :"+student.getGender());
System.out.println("second param "+data+" third param "+count);

}

@OnStartup(priority=1)
public void runOnStartup1()
{
System.out.println("runOnStartup with priority 1 of Service2 executed");
}
}