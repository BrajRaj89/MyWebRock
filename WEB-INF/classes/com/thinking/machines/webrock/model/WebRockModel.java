package com.thinking.machines.webrock.model;
import com.thinking.machines.webrock.pojo.*;
import java.util.*;
public class WebRockModel
{
private Map<String,Service> services;
public WebRockModel()
{
services = new HashMap<>();
}
public void setService(String path,Service service)
{
services.put(path,service);
}
public Map<String,Service> getService()
{
return this.services;
}
}