package com.webrock.annotations;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface SecuredAccess
{
public String  checkPost();
public String guard();
}