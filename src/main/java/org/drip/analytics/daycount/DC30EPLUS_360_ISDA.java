
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
 * This class implements the 30E+/360 ISDA day count convention.
 *
 * @author Lakshmi Krishnamurthy
 */

public class DC30EPLUS_360_ISDA implements org.drip.analytics.daycount.DCFCalculator {

	/**
	 * Empty DC30EPLUS_360_ISDA constructor
	 */

	public DC30EPLUS_360_ISDA()
	{
	}

	@Override public String baseCalculationType()
	{
		return "DC30EPLUS_360_ISDA";
	}

	@Override public String[] alternateNames()
	{
		return new String[] {"30E+/360", "30E+/360 ISDA", "30E+/360 (ISDA)", "DC30EPLUS_360_ISDA"};
	}

	@Override public double yearFraction (
		final double dblStart,
		final double dblEnd,
		final boolean bApplyEOMAdj,
		final ActActDCParams actactParams,
		final String strCalendar)
		throws java.lang.Exception
	{
		DateEOMAdjustment dm = DateEOMAdjustment.MakeDEOMA30EPLUS_360_ISDA (dblStart, dblEnd, bApplyEOMAdj);

		if (null == dm)
			throw new java.lang.Exception ("DC30EPLUS_360.yearFraction: Cannot create DateEOMAdjustment!");

		return (360. * (org.drip.analytics.date.DateUtil.Year (dblEnd) -
			org.drip.analytics.date.DateUtil.Year (dblStart)) + 30. * (org.drip.analytics.date.DateUtil.Month
				(dblEnd) - org.drip.analytics.date.DateUtil.Month (dblStart)) +
					(org.drip.analytics.date.DateUtil.Day (dblEnd) - org.drip.analytics.date.DateUtil.Day
						(dblStart)) + dm.posterior() - dm.anterior()) / 360.;
	}

	@Override public int daysAccrued (
		final double dblStart,
		final double dblEnd,
		final boolean bApplyEOMAdj,
		final ActActDCParams actactParams,
		final String strCalendar)
		throws java.lang.Exception
	{
		DateEOMAdjustment dm = DateEOMAdjustment.MakeDEOMA30EPLUS_360_ISDA (dblStart, dblEnd, bApplyEOMAdj);

		if (null == dm)
			throw new java.lang.Exception ("DC30EPLUS_360.daysAccrued: Cannot create DateEOMAdjustment!");

		return 360 * (org.drip.analytics.date.DateUtil.Year (dblEnd) - org.drip.analytics.date.DateUtil.Year
			(dblStart)) + 30 * (org.drip.analytics.date.DateUtil.Month (dblEnd) -
				org.drip.analytics.date.DateUtil.Month (dblStart)) + (org.drip.analytics.date.DateUtil.Day
					(dblEnd) - org.drip.analytics.date.DateUtil.Day (dblStart)) + dm.posterior() -
						dm.anterior();
	}
}
