package com.webrock.annotations;
import java.lang.annotation.*;
import java.io.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface Path
{
public String value();
}