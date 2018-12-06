package de.cognicrypt.staticanalyzer.annotations.impl;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target({ FIELD, PARAMETER, ElementType.METHOD, LOCAL_VARIABLE })
public @interface SecureSource {
	String info();
	String var();
}


