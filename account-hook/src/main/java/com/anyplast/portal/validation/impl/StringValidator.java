package com.anyplast.portal.validation.impl;

import com.anyplast.portal.validation.Validador;

public class StringValidator implements Validador<String> {

	public boolean validate(String field) {
		if (field != null && !field.isEmpty()) return true;
		return false;
	}

}
