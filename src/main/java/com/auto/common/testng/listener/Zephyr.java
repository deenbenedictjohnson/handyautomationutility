package com.auto.common.testng.listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author "Tejas Sonchhatra"
 * <p/>
 * project codeName : Kal-El created on 19 Feb 2016
 */

@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Zephyr {
	// id of the testcase on Zephyr
	String[] id() default "";
}
