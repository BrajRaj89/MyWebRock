package bobby.test;
import com.webrock.annotations.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

@Path("bbb")
@SecuredAccess(checkPost="bobby.test.login",guard="login")
public class bbb
{
@Path("getTypeResponse")
public void getTypeResponse()
{
System.out.println("Response1 got called_______________________ from bbb");
}
@Path("postTypeResponse")
public void postTypeResponse()
{
System.out.println("Response2 got called_______________________ from bbb");
}

}