package bobby.test;
import com.webrock.annotations.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

@Path("aaa")
@SecuredAccess(checkPost="bobby.test.login",guard="login")
public class aaa
{
@Path("getTypeResponse")
public void getTypeResponse()
{
System.out.println("Response1 got called_______________________ from aaa");
}
@Path("postTypeResponse")
public void postTypeResponse()
{
System.out.println("Response2 got called_______________________ from aaa");
}

}