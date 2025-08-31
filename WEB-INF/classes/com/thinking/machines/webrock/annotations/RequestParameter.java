package com.thinking.machines.webrock.annotations;
import java.lang.annotation.*;
import java.io.*;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParameter
{
public String value();
}