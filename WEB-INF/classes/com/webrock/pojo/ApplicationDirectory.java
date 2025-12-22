package com.webrock.pojo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;

public class ApplicationDirectory
{
final private String directory;
public ApplicationDirectory(String directory)
{
this.directory = directory;
}
public String getDirectory()
{
return this.directory;
}
}