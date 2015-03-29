package com.anyplast.portal.validation;

public interface Validador<T> {
	
	boolean validate(T field);

}
