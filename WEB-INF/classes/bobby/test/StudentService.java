package bobby.test;
import com.webrock.annotations.*;
import com.webrock.exceptions.*; 
import java.util.*;
import java.sql.*;

@Path("StudentService")
public class StudentService
{
@Path("add")
public void add(Student student) throws ServiceException
{
int rollNumber  = student.getRollNumber();
if(rollNumber<0) throw new ServiceException("invalid roll number");
String name = student.getName();
if(name==null ||name.length()==0)  throw new ServiceException("invalid name ");
String gender = student.getGender();
if(gender==null)  throw new ServiceException("invalid gender");
try
{
Connection con = DAOConnection.getConnection();
PreparedStatement preparedStatement = con.prepareStatement("insert into student values(?,?,?)");
preparedStatement.setInt(1,rollNumber);
preparedStatement.setString(2,name);
preparedStatement.setString(3,gender);
preparedStatement.executeUpdate();
preparedStatement.close();
con.close();
System.out.println("method executed successfully");
}catch(SQLException sqle)
{
throw new ServiceException(sqle.getMessage());
}
}
@Path("update")
public void update(Student student) throws ServiceException 
{
int rollNumber  = student.getRollNumber();
if(rollNumber<0) throw new ServiceException("invalid roll number");
String name = student.getName();
if(name==null ||name.length()==0)  throw new ServiceException("invalid name ");
String gender = student.getGender();
if(gender==null)  throw new ServiceException("invalid gender");
try
{
Connection con = DAOConnection.getConnection();
PreparedStatement preparedStatement=null;
preparedStatement = con.prepareStatement("update student set name=?, gender=? where rollNumber=?");
preparedStatement.setString(1,name);
preparedStatement.setString(2,gender);
preparedStatement.setInt(3,rollNumber);
int row= preparedStatement.executeUpdate();
preparedStatement.close();
con.close();
}catch(SQLException sqle)
{
throw new ServiceException(sqle.getMessage());
}
}
@Path("delete")
public void delete(@RequestParameter("rollNumber") int rollNumber) throws ServiceException
{
System.out.println(rollNumber);
if(rollNumber<0) throw new ServiceException("invalid roll number");
try
{
Connection con = DAOConnection.getConnection();
PreparedStatement preparedStatement=null;
preparedStatement = con.prepareStatement("delete from student where rollNumber=?");
preparedStatement.setInt(1,rollNumber);
preparedStatement.executeUpdate();
preparedStatement.close();
con.close();
}catch(SQLException sqle)
{
throw new ServiceException(sqle.getMessage());
}
}
@Path("getByRollNumber")
public Student getByRollNumber(@RequestParameter("rollNumber") int rollNumber) throws ServiceException
{
if(rollNumber<0) throw new ServiceException("invalid roll number");
Student student=null;
try
{
Connection con = DAOConnection.getConnection();
PreparedStatement preparedStatement=null;
preparedStatement = con.prepareStatement("select * from student where rollNumber=?");
preparedStatement.setInt(1,rollNumber);
ResultSet resultSet = preparedStatement.executeQuery();
while(resultSet.next())
{
rollNumber = resultSet.getInt("rollNumber");
String name = resultSet.getString("name");
String gender = resultSet.getString("gender");
student = new Student();
student.setRollNumber(rollNumber);
student.setName(name);
student.setGender(gender);
}
resultSet.close();
preparedStatement.close();
con.close();
}catch(SQLException sqle)
{
throw new ServiceException(sqle.getMessage());
}
return student;
}
@Path("getAll")
public List<Student> getAll()
{
List<Student> students = new ArrayList<>();
try
{
Connection con = DAOConnection.getConnection();
PreparedStatement preparedStatement=null;
preparedStatement = con.prepareStatement("select * from student");
ResultSet resultSet = preparedStatement.executeQuery();
Student student;
while(resultSet.next())
{
int rollNumber = resultSet.getInt("rollNumber");
String name = resultSet.getString("name");
String gender = resultSet.getString("gender");
student = new Student();
student.setRollNumber(rollNumber);
student.setName(name);
student.setGender(gender);
students.add(student);
}
resultSet.close();
preparedStatement.close();
con.close();
}catch(SQLException sqle)
{
System.out.println(sqle.getMessage());
}
return students;
}
}