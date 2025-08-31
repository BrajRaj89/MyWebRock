package bobby.test;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.exceptions.*;
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