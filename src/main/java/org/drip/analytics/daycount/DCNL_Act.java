
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
 * This class implements the NL/Act day count convention.
 *
 * @author Lakshmi Krishnamurthy
 */

public class DCNL_Act implements org.drip.analytics.daycount.DCFCalculator {

	/**
	 * Empty DCNL_Act constructor
	 */

	public DCNL_Act()
	{
	}

	@Override public java.lang.String baseCalculationType()
	{
		return "DCNL_Act";
	}

	@Override public java.lang.String[] alternateNames()
	{
		return new java.lang.String[] {"NL/Act", "DCNL_Act"};
	}

	@Override public double yearFraction (
		final double dblStart,
		final double dblEnd,
		final boolean bApplyEOMAdj,
		final ActActDCParams actactParams,
		final java.lang.String strCalendar)
		throws java.lang.Exception
	{
		if (null == actactParams)
			throw new java.lang.Exception ("DCNL_Act.yearFraction: Invalid ActActDCParams!");

		DateEOMAdjustment dm = DateEOMAdjustment.MakeDEOMA (dblStart, dblEnd, bApplyEOMAdj);

		if (null == dm)
			throw new java.lang.Exception ("DCNL_Act.yearFraction: Cannot create DateEOMAdjustment!");

		return (dblEnd - dblStart + dm.posterior() - dm.anterior() -
			org.drip.analytics.date.DateUtil.NumFeb29 (dblStart, dblEnd,
				org.drip.analytics.date.DateUtil.RIGHT_INCLUDE)) / actactParams.freq() /
					(actactParams.end() - actactParams.start());
	}

	@Override public int daysAccrued (
		final double dblStart,
		final double dblEnd,
		final boolean bApplyEOMAdj,
		final ActActDCParams actactParams,
		final java.lang.String strCalendar)
		throws java.lang.Exception
	{
		if (null == actactParams)
			throw new java.lang.Exception ("DCNL_Act.daysAccrued: Invalid ActActDCParams!");

		DateEOMAdjustment dm = DateEOMAdjustment.MakeDEOMA (dblStart, dblEnd, bApplyEOMAdj);

		if (null == dm)
			throw new java.lang.Exception ("DCNL_Act.daysAccrued: Cannot create DateEOMAdjustment!");

		return (int) (dblEnd - dblStart + dm.posterior() - dm.anterior() -
			org.drip.analytics.date.DateUtil.NumFeb29 (dblStart, dblEnd,
				org.drip.analytics.date.DateUtil.RIGHT_INCLUDE));
	}
}
