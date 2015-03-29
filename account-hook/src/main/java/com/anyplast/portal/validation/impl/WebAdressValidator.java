package com.anyplast.portal.validation.impl;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import com.anyplast.portal.validation.Validador;

public class WebAdressValidator implements Validador<String> {

	@Override
	public boolean validate(String field) {
		
		Pattern p = Pattern.compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?");  
		return p.matcher(field).matches();
		
	}

}
