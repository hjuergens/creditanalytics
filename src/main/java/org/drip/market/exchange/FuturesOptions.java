
package org.drip.market.exchange;

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
 * FuturesOptions contains the details of the exchange-traded Short-Term Futures Options Contracts.
 *
 * @author Lakshmi Krishnamurthy
 */

public class FuturesOptions {
	private java.lang.String _strTradingMode = "";
	private java.lang.String _strFullyQualifiedName = "";
	private
		org.drip.analytics.support.CaseInsensitiveHashMap<org.drip.product.params.LastTradingDateSetting[]>
			_mapLTDS = new
				org.drip.analytics.support.CaseInsensitiveHashMap<org.drip.product.params.LastTradingDateSetting[]>();

	/**
	 * FuturesOptions Constructor
	 * 
	 * @param strFullyQualifiedName Fully Qualified Name
	 * @param strTradingMode Trading Mode - PREMIUM | MARGIN
	 * 
	 * @throws java.lang.Exception Thrown if inputs are invalid
	 */

	public FuturesOptions (
		final java.lang.String strFullyQualifiedName,
		final java.lang.String strTradingMode)
		throws java.lang.Exception
	{
		if (null == (_strFullyQualifiedName = strFullyQualifiedName) || _strFullyQualifiedName.isEmpty() ||
			null == (_strTradingMode = strTradingMode) || _strTradingMode.isEmpty())
			throw new java.lang.Exception ("FuturesOptions ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the Fully Qualified Name
	 * 
	 * @return The Fully Qualified Name
	 */

	public java.lang.String fullyQualifiedName()
	{
		return _strFullyQualifiedName;
	}

	/**
	 * Retrieve the Trading Mode
	 * 
	 * @return The Trading Mode
	 */

	public java.lang.String tradingMode()
	{
		return _strTradingMode;
	}

	/**
	 * Add a Named Exchange LTDS Array Map Entry
	 * 
	 * @param strExchange Named Exchange
	 * @param aLTDS Array of LTDS
	 * 
	 * @return TRUE => Named Exchange LTDS Array Map Entry successfully added
	 */

	public boolean setLDTS (
		final java.lang.String strExchange,
		final org.drip.product.params.LastTradingDateSetting[] aLTDS)
	{
		if (null == strExchange || strExchange.isEmpty() || null == aLTDS || 0 == aLTDS.length) return false;

		_mapLTDS.put (strExchange, aLTDS);

		return true;
	}

	/**
	 * Retrieve the LTDS Array corresponding to the Exchange
	 * 
	 * @param strExchange The Exchange
	 * 
	 * @return The LTDS Array
	 */

	public org.drip.product.params.LastTradingDateSetting[] ltdsArray (
		final java.lang.String strExchange)
	{
		if (null == strExchange || strExchange.isEmpty() || !_mapLTDS.containsKey (strExchange)) return null;

		return _mapLTDS.get (strExchange);
	}

	/**
	 * Retrieve the Set of Traded Exchanges
	 * 
	 * @return The Set of Traded Exchanges
	 */

	public java.util.Set<java.lang.String> exchanges()
	{
		return 0 == _mapLTDS.size() ? null : _mapLTDS.keySet();
	}
}
