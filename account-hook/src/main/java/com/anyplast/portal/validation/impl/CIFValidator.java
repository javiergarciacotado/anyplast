package com.anyplast.portal.validation.impl;

import com.anyplast.portal.validation.Validador;

public class CIFValidator implements Validador<String> {

	@Override
	public boolean validate(String field) {
		
		boolean result = false;

		try {
			String vCif =  field.trim();

			int sum = 0;
			int count = 0;
			int temp = 0;
			int codeControl = 0;
			String stringTemp = null;
			String letterCIF = "ABCDEFGHJKLMNPQRSUVW";
			String controlLetterCIF = "0123456789";
			String numericSocLetter = "KLMNPQRSW";
			String firstLetter = null;
			String lastLetter = null;

			if (!(vCif.length() == 9))
				return false;

			if (vCif.matches("[^A-Za-z0-9]"))
				return false;

			vCif = vCif.toUpperCase();
			firstLetter = vCif.substring(0, 1);
			lastLetter = vCif.substring(8, 9);

			if (letterCIF.indexOf(firstLetter) < 0)
				return false;

			sum = sum + Integer.parseInt(vCif.substring(2, 3)) + Integer.parseInt(vCif.substring(4, 5))
					+ Integer.parseInt(vCif.substring(6, 7));

			for (count = 1; count < 8; count = count + 2) {
				temp = (Integer.parseInt(vCif.substring(count, count + 1)) * 2);
				
				if (temp < 10)
					sum = sum + temp;
				else {
					stringTemp = String.valueOf(temp);
					sum = sum + (Integer.parseInt(stringTemp.substring(0, 1)))
							+ (Integer.parseInt(stringTemp.substring(1, 2)));
				}
			}

			codeControl = ((10 - (sum % 10)) % 10);

			if (numericSocLetter.indexOf(firstLetter) >= 0) {
				byte[] ascii = new byte[1];

				if (codeControl == 0)
					codeControl = 10;
				codeControl = codeControl + 64;
				ascii[0] = (Integer.valueOf(codeControl)).byteValue();

				result = (lastLetter.equals(new String(ascii)));
			} else {
				result = (codeControl == controlLetterCIF.indexOf(lastLetter));
			}
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

}
