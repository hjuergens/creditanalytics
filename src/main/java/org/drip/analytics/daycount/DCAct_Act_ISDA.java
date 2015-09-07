
package org.drip.analytics.daycount;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
 * Copyright (C) 2012 Lakshmi Krishnamurthy
 * 
 *  This file is part of DRIP, a free-software/open-source library for fixed income analysts and developers -
 * 		http://www.credit-trader.org/Begin.html
 * 
 *  DRIP is a free, full featured, fixed income rates, credit, and FX analytics library with a focus towards
 *  	pricing/valuation, risk, and market making.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   	you may not use this file except in compliance with the License.
 *   
 *  You may obtain a copy of the License at
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  	distributed under the License is distributed on an "AS IS" BASIS,
 *  	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  
 *  See the License for the specific language governing permissions and
 *  	limitations under the License.
 */

/**
 * This class implements the ISDA Act/Act day count convention.
 *
 * @author Lakshmi Krishnamurthy
 */

public class DCAct_Act_ISDA implements org.drip.analytics.daycount.DCFCalculator {

	/**
	 * Empty DCAct_Act_ISDA constructor
	 */

	public DCAct_Act_ISDA()
	{
	}

	@Override public String baseCalculationType()
	{
		return "DCAct_Act_ISDA";
	}

	@Override public String[] alternateNames()
	{
		return new String[] {"Actual/Actual ISDA", "Act/Act ISDA", "US:WIT Act/Act",
			"DCAct_Act_ISDA"};
	}

	@Override public double yearFraction (
		final double dblStart,
		final double dblEnd,
		final boolean bApplyEOMAdj,
		final ActActDCParams actactParams,
		final String strCalendar)
		throws java.lang.Exception
	{
		DateEOMAdjustment dm = DateEOMAdjustment.MakeDEOMA (dblStart, dblEnd, bApplyEOMAdj);

		if (null == dm)
			throw new java.lang.Exception ("DCAct_Act_ISDA.yearFraction: Cannot create DateEOMAdjustment!");

		return ((org.drip.analytics.date.DateUtil.DaysRemaining (dblStart) - dm.anterior()) /
			(org.drip.analytics.date.DateUtil.IsLeapYear (dblStart) ? 366. : 365.)) +
				((org.drip.analytics.date.DateUtil.DaysElapsed (dblEnd) + dm.posterior()) /
					(org.drip.analytics.date.DateUtil.IsLeapYear (dblEnd) ? 366. : 365.)) +
						org.drip.analytics.date.DateUtil.Year (dblEnd) -
							org.drip.analytics.date.DateUtil.Year (dblStart) - 1;
	}

	@Override public int daysAccrued (
		final double dblStart,
		final double dblEnd,
		final boolean bApplyEOMAdj,
		final ActActDCParams actactParams,
		final String strCalendar)
		throws java.lang.Exception
	{
		DateEOMAdjustment dm = DateEOMAdjustment.MakeDEOMA (dblStart, dblEnd, bApplyEOMAdj);

		if (null == dm)
			throw new java.lang.Exception ("DCAct_Act_ISDA.daysAccrued: Cannot create DateEOMAdjustment!");

		int iDaysAccrued = 0;

		int iStartYear = org.drip.analytics.date.DateUtil.Year (dblStart);

		int iEndYear = org.drip.analytics.date.DateUtil.Year (dblEnd);

		if (iEndYear == iStartYear) iDaysAccrued -= 0 == iEndYear % 4 ? 366 : 365;

		for (int iYear = iStartYear + 1; iYear < iEndYear; ++iYear)
			iDaysAccrued += 0 == iYear % 4 ? 366 : 365;

		return iDaysAccrued + org.drip.analytics.date.DateUtil.DaysRemaining (dblStart) - dm.anterior() +
			org.drip.analytics.date.DateUtil.DaysElapsed (dblEnd) + dm.posterior();
	}
}
