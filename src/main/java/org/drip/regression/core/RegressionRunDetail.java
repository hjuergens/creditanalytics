
package org.drip.regression.core;

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
 * RegressionRunDetail contains named field level detailed output of the regression activity.
 *
 * @author Lakshmi Krishnamurthy
 */

public class RegressionRunDetail {
	private org.drip.analytics.support.CaseInsensitiveTreeMap<String> _mapNVDetails = new
		org.drip.analytics.support.CaseInsensitiveTreeMap<String>();

	/**
	 * Empty constructor: Regression detail fields will be initialized
	 */

	public RegressionRunDetail()
	{
	}

	/**
	 * Set the Key Value Map Entry
	 * 
	 * @param strKey Name of the regression detail field
	 * @param strValue Value of the regression detail field
	 *
	 * @return TRUE => "set" succeeded
	 */

	public boolean set (
		final String strKey,
		final String strValue)
	{
		if (null == strKey || strKey.isEmpty() || null == strValue || strValue.isEmpty()) return false;

		_mapNVDetails.put (strKey, strValue);

		return true;
	}

	/**
	 * Retrieve the field map
	 * 
	 * @return The Field Map
	 */

	public org.drip.analytics.support.CaseInsensitiveTreeMap<String> getFieldMap()
	{
		return _mapNVDetails;
	}
}
