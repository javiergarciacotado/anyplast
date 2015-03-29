package com.anyplast.portal.validation.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.anyplast.portal.validation.Validador;

public class EmailValidator implements Validador<String> {

	@Override
	public boolean validate(String field) {
		String emailPattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = java.util.regex.Pattern.compile(emailPattern);
        Matcher m = p.matcher(field);
        return m.matches();
	}

}
