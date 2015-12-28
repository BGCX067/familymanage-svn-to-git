package com.exuan.ecalendar;

import java.util.Calendar;
import java.util.Date;

public class TwoFourFestival {
	final static long[] STermInfo = new long[] { 0, 21208, 42467, 63836, 85337,
		107014, 128867, 150921, 173149, 195551, 218072, 240693, 263343,
		285989, 308563, 331033, 353350, 375494, 397447, 419210, 440795,
		462224, 483532, 504758 };

	public static int getSoralTerm(int y, int m, int d)
	{
		int solarTerms;
		if (d == sTerm(y, (m - 1) * 2))
		 solarTerms = (m - 1) * 2;
		else if (d == sTerm(y, (m - 1) * 2 + 1))
		 solarTerms = (m - 1) * 2 + 1;
		else
		{
			solarTerms = -1;
		}
		return  solarTerms;
	}	
	private static int sTerm(int y, int n)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(1900, 0, 6, 2, 5, 0);
		long temp = cal.getTime().getTime();
		cal.setTime(new Date(
		   (long) ((31556925974.7 * (y - 1900) + STermInfo[n] * 60000L) + temp)));
		
		return cal.get(Calendar.DAY_OF_MONTH);
	}
}