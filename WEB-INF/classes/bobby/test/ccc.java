package bobby.test;
import com.thinking.machines.webrock.annotations.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

@Path("ccc")
@SecuredAccess(checkPost="bobby.test.login",guard="login")
public class ccc
{
@Path("getTypeResponse")
public void getTypeResponse()
{
System.out.println("Response1 got called_______________________from ccc");
}
@Path("postTypeResponse")
public void postTypeResponse()
{
System.out.println("Response2 got called_______________________from ccc");
}

}