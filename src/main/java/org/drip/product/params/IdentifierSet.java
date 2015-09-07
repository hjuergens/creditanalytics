
package org.drip.product.params;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
 * Copyright (C) 2012 Lakshmi Krishnamurthy
 * Copyright (C) 2011 Lakshmi Krishnamurthy
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
 * IdentifierSet contains the component's identifier parameters - ISIN, CUSIP, ID, and ticker. It exports
 *  serialization into and de-serialization out of byte arrays.
 *
 * @author Lakshmi Krishnamurthy
 */

public class IdentifierSet implements org.drip.product.params.Validatable {
	private java.lang.String _strID = "";
	private java.lang.String _strISIN = "";
	private java.lang.String _strCUSIP = "";
	private java.lang.String _strTicker = "";

	/**
	 * Construct the IdentifierSet from ISIN, CUSIP, ID, and ticker.
	 * 
	 * @param strISIN ISIN
	 * @param strCUSIP CUSIP
	 * @param strID component ID
	 * @param strTicker Ticker
	 */

	public IdentifierSet (
		final java.lang.String strISIN,
		final java.lang.String strCUSIP,
		final java.lang.String strID,
		final java.lang.String strTicker)
	{
		_strISIN = strISIN;
		_strCUSIP = strCUSIP;
		_strID = strID;
		_strTicker = strTicker;
	}

	@Override public boolean validate()
	{
		if ((null == _strISIN || _strISIN.isEmpty()) && (null == _strCUSIP || _strCUSIP.isEmpty()))
			return false;

		if (null == _strID || _strID.isEmpty()) {
			if (null == (_strID = _strISIN) || _strID.isEmpty()) _strID = _strCUSIP;
		}

		return true;
	}

	/**
	 * Retrieve the ID
	 * 
	 * @return The ID
	 */

	public java.lang.String id()
	{
		return _strID;
	}

	/**
	 * Retrieve the ISIN
	 * 
	 * @return The ISIN
	 */

	public java.lang.String isin()
	{
		return _strISIN;
	}

	/**
	 * Retrieve the CUSIP
	 * 
	 * @return The CUSIP
	 */

	public java.lang.String cusip()
	{
		return _strCUSIP;
	}

	/**
	 * Retrieve the Ticker
	 * 
	 * @return The Ticker
	 */

	public java.lang.String ticker()
	{
		return _strTicker;
	}
}
