
package org.drip.market.definition;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
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
 * FloaterIndex contains the definitions of the floating rate indexes of different jurisdictions.
 *
 * @author Lakshmi Krishnamurthy
 */

abstract public class FloaterIndex {
	private String _strName = "";
	private String _strFamily = "";
	private int _iAccrualCompoundingRule = -1;
	private String _strCalendar = "";
	private String _strCurrency = "";
	private String _strDayCount = "";

	/**
	 * IBORIndex Constructor
	 * 
	 * @param strName Index Name
	 * @param strFamily Index Family
	 * @param strCurrency Index Currency
	 * @param strDayCount Index Day Count
	 * @param strCalendar Index Holiday Calendar
	 * @param iSpotLag Spot Lag
	 * @param iAccrualCompoundingRule Accrual Compounding Rule
	 * 
	 * @throws java.lang.Exception Thrown if Inputs are invalid
	 */

	public FloaterIndex (
		final String strName,
		final String strFamily,
		final String strCurrency,
		final String strDayCount,
		final String strCalendar,
		final int iSpotLag,
		final int iAccrualCompoundingRule)
		throws java.lang.Exception
	{
		if (null == (_strName = strName) || _strName.isEmpty() || null == (_strFamily = strFamily) ||
			_strFamily.isEmpty() || null == (_strCurrency = strCurrency) || _strCurrency.isEmpty() || null ==
				(_strDayCount = strDayCount) || _strDayCount.isEmpty() ||
					!org.drip.analytics.support.CompositePeriodBuilder.ValidateCompoundingRule
						(_iAccrualCompoundingRule = iAccrualCompoundingRule))
			throw new java.lang.Exception ("FloaterIndex ctr: Invalid Inputs");

		_strCalendar = strCalendar;
	}

	/**
	 * Retrieve the Index Name
	 * 
	 * @return The Index Name
	 */

	public String name()
	{
		return _strName;
	}

	/**
	 * Retrieve the Index Family
	 * 
	 * @return The Index Family
	 */

	public String family()
	{
		return _strFamily;
	}

	/**
	 * Retrieve the Index Holiday Calendar
	 * 
	 * @return The Index Holiday Calendar
	 */

	public String calendar()
	{
		return _strCalendar;
	}

	/**
	 * Retrieve the Index Currency
	 * 
	 * @return The Index Currency
	 */

	public String currency()
	{
		return _strCurrency;
	}

	/**
	 * Retrieve the Index Day Count Convention
	 * 
	 * @return The Index Day Count Convention
	 */

	public String dayCount()
	{
		return _strDayCount;
	}

	/**
	 * Retrieve the Accrual Compounding Rule
	 * 
	 * @return The Accrual Compounding Rule
	 */

	public int accrualCompoundingRule()
	{
		return _iAccrualCompoundingRule;
	}

	/**
	 * Retrieve the Spot Lag DAP with Date Roll Previous
	 * 
	 * @return The Spot Lag DAP with Date Roll Previous
	 */

	public org.drip.analytics.daycount.DateAdjustParams spotLagDAPBackward()
	{
		return new org.drip.analytics.daycount.DateAdjustParams
			(org.drip.analytics.daycount.Convention.DATE_ROLL_PREVIOUS, spotLag(), _strCalendar);
	}

	/**
	 * Retrieve the Spot Lag DAP with Date Roll Following
	 * 
	 * @return The Spot Lag DAP with Date Roll Following
	 */

	public org.drip.analytics.daycount.DateAdjustParams spotLagDAPForward()
	{
		return new org.drip.analytics.daycount.DateAdjustParams
			(org.drip.analytics.daycount.Convention.DATE_ROLL_FOLLOWING, spotLag(), _strCalendar);
	}

	/**
	 * Retrieve the Index Spot Lag
	 * 
	 * @return The Index Spot Lag
	 */

	abstract public int spotLag();
}
