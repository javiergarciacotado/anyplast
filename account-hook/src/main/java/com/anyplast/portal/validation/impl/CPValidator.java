package com.anyplast.portal.validation.impl;

import com.anyplast.portal.validation.Validador;

public class CPValidator implements Validador<String> {

	public boolean validate(String field) {
		return field.matches("\\d{5}");
	}

}
