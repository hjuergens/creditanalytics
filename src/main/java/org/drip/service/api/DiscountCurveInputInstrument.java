
package org.drip.service.api;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
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
 * DiscountCuveInputInstrument contains the input instruments and their quotes.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class DiscountCurveInputInstrument {
	private org.drip.analytics.date.JulianDate _dt = null;
	private java.util.List<java.lang.Double> _lsCashQuote = null;
	private java.util.List<String> _lsCashTenor = null;
	private java.util.List<java.lang.Double> _lsSwapQuote = null;
	private java.util.List<String> _lsSwapTenor = null;
	private java.util.List<java.lang.Double> _lsFutureQuote = null;
	private java.util.List<String> _lsFutureTenor = null;

	/**
	 * DiscountCurveInputInstrument constructor
	 * 
	 * @param dt Curve Epoch Date
	 * @param lsCashTenor List of Cash Tenors
	 * @param lsCashQuote List of Cash Quotes
	 * @param lsFutureTenor List of Future Tenors
	 * @param lsFutureQuote List of Future Quotes
	 * @param lsSwapTenor List of Swap Tenors
	 * @param lsSwapQuote List of Swap Quotes
	 * 
	 * @throws java.lang.Exception Thrown if Inputs are invalid
	 */

	public DiscountCurveInputInstrument (
		final org.drip.analytics.date.JulianDate dt,
		final java.util.List<String> lsCashTenor,
		final java.util.List<java.lang.Double> lsCashQuote,
		final java.util.List<String> lsFutureTenor,
		final java.util.List<java.lang.Double> lsFutureQuote,
		final java.util.List<String> lsSwapTenor,
		final java.util.List<java.lang.Double> lsSwapQuote)
		throws java.lang.Exception
	{
		if (null == (_dt = dt))
			throw new java.lang.Exception ("DiscountCurveInputInstrument ctr: Invalid Inputs");

		int iNumCashQuote = null == (_lsCashQuote = lsCashQuote) ? 0 : _lsCashQuote.size();

		int iNumCashTenor = null == (_lsCashTenor = lsCashTenor) ? 0 : _lsCashTenor.size();

		int iNumFutureQuote = null == (_lsFutureQuote = lsFutureQuote) ? 0 : _lsFutureQuote.size();

		int iNumFutureTenor = null == (_lsFutureTenor = lsFutureTenor) ? 0 : _lsFutureTenor.size();

		int iNumSwapQuote = null == (_lsSwapQuote = lsSwapQuote) ? 0 : _lsSwapQuote.size();

		int iNumSwapTenor = null == (_lsSwapTenor = lsSwapTenor) ? 0 : _lsSwapTenor.size();

		if (iNumCashQuote != iNumCashTenor || iNumFutureQuote != iNumFutureTenor || iNumSwapQuote !=
			iNumSwapTenor || (0 == iNumCashTenor && 0 == iNumFutureTenor && 0 == iNumSwapTenor))
			throw new java.lang.Exception ("DiscountCurveInputInstrument ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the Curve Epoch Date
	 * 
	 * @return The Curve Epoch Date
	 */

	public org.drip.analytics.date.JulianDate date()
	{
		return _dt;
	}

	/**
	 * Retrieve the Array of Cash Quotes
	 * 
	 * @return The Array of Cash Quotes
	 */

	public double[] cashQuote()
	{
		if (null == _lsCashQuote) return null;

		int iNumQuote = _lsCashQuote.size();

		if (0 == iNumQuote) return null;

		int i = 0;
		double[] adblQuote = new double[iNumQuote];

		for (double dblQuote : _lsCashQuote)
			adblQuote[i++] = dblQuote;

		return adblQuote;
	}

	/**
	 * Retrieve the Array of Cash Tenors
	 * 
	 * @return The Array of Cash Tenors
	 */

	public String[] cashTenor()
	{
		if (null == _lsCashTenor) return null;

		int iNumTenor = _lsCashTenor.size();

		if (0 == iNumTenor) return null;

		int i = 0;
		String[] astrTenor = new String[iNumTenor];

		for (String strTenor : _lsCashTenor)
			astrTenor[i++] = strTenor;

		return astrTenor;
	}

	/**
	 * Retrieve the Array of Future Quotes
	 * 
	 * @return The Array of Future Quotes
	 */

	public double[] futureQuote()
	{
		if (null == _lsFutureQuote) return null;

		int iNumQuote = _lsFutureQuote.size();

		if (0 == iNumQuote) return null;

		int i = 0;
		double[] adblQuote = new double[iNumQuote];

		for (double dblQuote : _lsFutureQuote)
			adblQuote[i++] = dblQuote;

		return adblQuote;
	}

	/**
	 * Retrieve the Array of Future Tenors
	 * 
	 * @return The Array of Future Tenors
	 */

	public String[] futureTenor()
	{
		if (null == _lsFutureTenor) return null;

		int iNumTenor = _lsFutureTenor.size();

		if (0 == iNumTenor) return null;

		int i = 0;
		String[] astrTenor = new String[iNumTenor];

		for (String strTenor : _lsFutureTenor)
			astrTenor[i++] = strTenor;

		return astrTenor;
	}

	/**
	 * Retrieve the Array of Swap Quotes
	 * 
	 * @return The Array of Swap Quotes
	 */

	public double[] swapQuote()
	{
		if (null == _lsSwapQuote) return null;

		int iNumQuote = _lsSwapQuote.size();

		if (0 == iNumQuote) return null;

		int i = 0;
		double[] adblQuote = new double[iNumQuote];

		for (double dblQuote : _lsSwapQuote)
			adblQuote[i++] = dblQuote;

		return adblQuote;
	}

	/**
	 * Retrieve the Array of Swap Tenors
	 * 
	 * @return The Array of Swap Tenors
	 */

	public String[] swapTenor()
	{
		if (null == _lsSwapTenor) return null;

		int iNumTenor = _lsSwapTenor.size();

		if (0 == iNumTenor) return null;

		int i = 0;
		String[] astrTenor = new String[iNumTenor];

		for (String strTenor : _lsSwapTenor)
			astrTenor[i++] = strTenor;

		return astrTenor;
	}
}
