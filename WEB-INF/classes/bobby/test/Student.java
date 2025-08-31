package bobby.test;
import java.io.*;

public class Student implements Serializable
{
private int rollNumber;
private String name;
private String gender;
public void setRollNumber(int rollNumber)
{
this.rollNumber = rollNumber;
}
public int getRollNumber()
{
return rollNumber;
}
public void setName(String name)
{
this.name =name;
}
public String getName()
{
return this.name;
}
public void setGender(String gender)
{
this.gender = gender;
}
public String getGender()
{
return this.gender;
}
}