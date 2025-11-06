package bobby.test;
import com.webrock.annotations.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import com.webrock.pojo.*;


@Path("Services2")
@InjectSessionScope("Service2")
public class Services2
{
// @AutoWired(name="classNo")
private int classNo;
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
@Forward("index.jsp")
public void getTypeResponse( @RequestParameter("var1") int var1, @RequestParameter("var2") int var2 )
{
System.out.println("Response1 got called_______________________");
System.out.println(var1+var2);

}
@Post("response2")
@Path("response2")
@Forward("index.html")
public void postTypeResponse(Student student)
{
System.out.println("Response2 got called_______________________");
System.out.println("student name :"+student.getName()+", student gender :"+student.getGender());
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