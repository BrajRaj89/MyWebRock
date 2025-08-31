package bobby.test;
import java.sql.*;

public class DAOConnection 
{
public static Connection getConnection()
{
Connection c=null;
try
{
Class.forName("com.mysql.cj.jdbc.Driver");
c = DriverManager.getConnection("jdbc:mysql://localhost:3306/j2studentdb","studentdbuser","studentdbuser");
}catch(Exception exception)
{
System.out.println(exception.getMessage());
}
return c;
}
}






