
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
 * This interface is the stub for all the day count convention functionality. It exposes the base/alternate
 *  day count convention names, the year-fraction and the days accrued.
 *
 * @author Lakshmi Krishnamurthy
 */

public interface DCFCalculator {

	/**
	 * Retrieves the base calculation type corresponding to the DCF Calculator
	 * 
	 * @return Name of the base calculation type
	 */

	public abstract String baseCalculationType();

	/**
	 * Retrieves the full set of alternate names corresponding to the DCF Calculator
	 * 
	 * @return Array of alternate names
	 */

	public abstract String[] alternateNames();

	/**
	 * Calculates the accrual fraction in years between 2 given days
	 * 
	 * @param dblStart Start Date
	 * @param dblEnd End Date
	 * @param bApplyEOMAdj Apply end-of-month adjustment (true)
	 * @param actactParams ActActParams
	 * @param strCalendar Holiday Calendar
	 * 
	 * @return Accrual Fraction in years
	 * 
	 * @throws java.lang.Exception Thrown if the accrual fraction cannot be calculated
	 */

	public abstract double yearFraction (
		final double dblStart,
		final double dblEnd,
		final boolean bApplyEOMAdj,
		final ActActDCParams actactParams,
		final String strCalendar)
		throws java.lang.Exception;

	/**
	 * Calculates the number of days accrued between the two given days
	 * 
	 * @param dblStart Start Date
	 * @param dblEnd End Date
	 * @param bApplyEOMAdj Apply end-of-month adjustment (true)
	 * @param actactParams ActActParams
	 * @param strCalendar Holiday Calendar
	 * 
	 * @return Accrual Fraction in years
	 * 
	 * @throws java.lang.Exception Thrown if the accrual fraction cannot be calculated
	 */

	public abstract int daysAccrued (
		final double dblStart,
		final double dblEnd,
		final boolean bApplyEOMAdj,
		final ActActDCParams actactParams,
		final String strCalendar)
		throws java.lang.Exception;
}
