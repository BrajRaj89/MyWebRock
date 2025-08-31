package com.thinking.machines.webrock.pojo;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;

public class ApplicationDirectory
{
File directory;
public ApplicationDirectory(File directory)
{
this.directory = directory;
}
public File getDirectory()
{
return this.directory;
}
}