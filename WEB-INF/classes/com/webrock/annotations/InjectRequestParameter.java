package com.webrock.annotations;
import java.lang.annotation.*;
import java.io.*;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InjectRequestParameter
{
public String value();
}