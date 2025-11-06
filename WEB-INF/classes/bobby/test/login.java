package bobby.test;
import com.webrock.annotations.*;
import com.webrock.exceptions.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

@InjectSessionScope("login")
public class login
{
public void login(HttpServletRequest request,ServletContext servletContext,HttpSession session) throws ServiceException
{
System.out.println("successfully logined");
throw new ServiceException("not logged in");
}
}