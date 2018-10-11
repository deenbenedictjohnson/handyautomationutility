package com.auto.common.testng.listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Report {

	String testcaseId() default "";

	String testcaseName() default "";

	String testcaseDescription() default "";

	String priority() default "";

	String ownerName() default "";

	String groups() default "";

	String component() default "";


}

