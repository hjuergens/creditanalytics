
package org.drip.market.exchange;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
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
 * BondFuturesConvention contains the Details for the Futures Basket of the Exchange-Traded Bond Futures
 *  Contracts.
 *
 * @author Lakshmi Krishnamurthy
 */

public class BondFuturesConvention {
	private String _strName = "";
	private String _strCalendar = "";
	private String _strCurrency = "";
	private String[] _astrCode = null;
	private String[] _astrExchange = null;
	private String _strMaturityTenor = "";
	private String _strUnderlierType = "";
	private String _strUnderlierSubtype = "";
	private double _dblBasketNotional = java.lang.Double.NaN;
	private org.drip.market.exchange.BondFuturesSettle _bfs = null;
	private org.drip.analytics.eventday.DateInMonth _dimExpiry = null;
	private double _dblComponentNotionalMinimum = java.lang.Double.NaN;
	private org.drip.market.exchange.BondFuturesEligibility _bfe = null;

	/**
	 * BondFuturesConvention Constructor
	 * 
	 * @param strName The Futures Name
	 * @param astrCode The Array of the Futures Codes
	 * @param strCurrency The Futures Currency
	 * @param strCalendar The Futures Settle Calendar
	 * @param strMaturityTenor The Maturity Tenor
	 * @param dblBasketNotional Basket Notional
	 * @param dblComponentNotionalMinimum The Minimum Component Notional
	 * @param astrExchange Exchange Array
	 * @param strUnderlierType Underlier Type
	 * @param strUnderlierSubtype Underlier Sub-Type
	 * @param bfe Eligibility Settings
	 * @param bfs Settlement Settings
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public BondFuturesConvention (
		final String strName,
		final String[] astrCode,
		final String strCurrency,
		final String strCalendar,
		final String strMaturityTenor,
		final double dblBasketNotional,
		final double dblComponentNotionalMinimum,
		final String[] astrExchange,
		final String strUnderlierType,
		final String strUnderlierSubtype,
		final org.drip.analytics.eventday.DateInMonth dimExpiry,
		final org.drip.market.exchange.BondFuturesEligibility bfe,
		final org.drip.market.exchange.BondFuturesSettle bfs)
		throws java.lang.Exception
	{
		if (null == (_strName = strName) || _strName.isEmpty() || null == (_astrCode = astrCode) || 0 ==
			_astrCode.length || null == (_strCurrency = strCurrency) || _strCurrency.isEmpty() || null ==
				(_strMaturityTenor = strMaturityTenor) || _strMaturityTenor.isEmpty() ||
					!org.drip.quant.common.NumberUtil.IsValid (_dblBasketNotional = dblBasketNotional) ||
						!org.drip.quant.common.NumberUtil.IsValid (_dblComponentNotionalMinimum =
							dblComponentNotionalMinimum) || null == (_astrExchange = astrExchange) || 0 ==
								_astrExchange.length || null == (_strUnderlierType = strUnderlierType) ||
									_strUnderlierType.isEmpty() || null == (_strUnderlierSubtype =
										strUnderlierSubtype) || _strUnderlierSubtype.isEmpty() || null ==
											(_dimExpiry = dimExpiry) || null == (_bfe = bfe) || null == (_bfs
												= bfs))
			throw new java.lang.Exception ("BondFuturesConvention ctr: Invalid Inputs");

		_strCalendar = strCalendar;
	}

	/**
	 * Retrieve the Bond Futures Name
	 * 
	 * @return The Bond Futures Name
	 */

	public String name()
	{
		return _strName;
	}

	/**
	 * Retrieve the Bond Futures Settle Calendar
	 * 
	 * @return The Bond Futures Settle Calendar
	 */

	public String calendar()
	{
		return _strCalendar;
	}

	/**
	 * Retrieve the Bond Futures Code Array
	 * 
	 * @return The Bond Futures Code Array
	 */

	public String[] codes()
	{
		return _astrCode;
	}

	/**
	 * Retrieve the Bond Futures Currency
	 * 
	 * @return The Bond Futures Currency
	 */

	public String currency()
	{
		return _strCurrency;
	}

	/**
	 * Retrieve the Bond Futures Maturity Tenor
	 * 
	 * @return The Bond Futures Maturity Tenor
	 */

	public String maturityTenor()
	{
		return _strMaturityTenor;
	}

	/**
	 * Retrieve the Bond Futures Basket Notional
	 * 
	 * @return The Bond Futures Basket Notional
	 */

	public double basketNotional()
	{
		return _dblBasketNotional;
	}

	/**
	 * Retrieve the Minimum Bond Futures Component Notional
	 * 
	 * @return The Minimum Bond Futures Component Notional
	 */

	public double minimumComponentNotional()
	{
		return _dblComponentNotionalMinimum;
	}

	/**
	 * Retrieve the Bond Futures Exchanges Array
	 * 
	 * @return The Bond Futures Exchanges Array
	 */

	public String[] exchanges()
	{
		return _astrExchange;
	}

	/**
	 * Retrieve the Bond Futures Underlier Type
	 * 
	 * @return The Bond Futures Underlier Type
	 */

	public String underlierType()
	{
		return _strUnderlierType;
	}

	/**
	 * Retrieve the Bond Futures Underlier Sub-type
	 * 
	 * @return The Bond Futures Underlier Sub-type
	 */

	public String underlierSubtype()
	{
		return _strUnderlierSubtype;
	}

	/**
	 * Retrieve the Date In Month Expiry Settings
	 * 
	 * @return The Date In Month Expiry Settings
	 */

	public org.drip.analytics.eventday.DateInMonth dimExpiry()
	{
		return _dimExpiry;
	}

	/**
	 * Retrieve the Bond Futures Eligibility Settings
	 * 
	 * @return The Bond Futures Eligibility Settings
	 */

	public org.drip.market.exchange.BondFuturesEligibility eligibility()
	{
		return _bfe;
	}

	/**
	 * Retrieve the Bond Futures Settle Settings
	 * 
	 * @return The Bond Futures Settle Settings
	 */

	public org.drip.market.exchange.BondFuturesSettle settle()
	{
		return _bfs;
	}

	/**
	 * Retrieve the BondFuturesEventDates Instance corresponding to the Futures Expiry Year/Month
	 * 
	 * @param iYear Futures Year
	 * @param iMonth Futures Month
	 * 
	 * @return The BondFuturesEventDates Instance
	 */

	public org.drip.market.exchange.BondFuturesEventDates eventDates (
		final int iYear,
		final int iMonth)
	{
		org.drip.analytics.date.JulianDate dtExpiry = _dimExpiry.instanceDay (iYear, iMonth, _strCalendar);

		if (null == dtExpiry) return null;

		try {
			return new org.drip.market.exchange.BondFuturesEventDates (dtExpiry, dtExpiry.addBusDays
				(_bfs.expiryFirstDeliveryLag(), _strCalendar), dtExpiry.addBusDays
					(_bfs.expiryFinalDeliveryLag(), _strCalendar), dtExpiry.addBusDays
						(_bfs.expiryDeliveryNoticeLag(), _strCalendar), dtExpiry.addBusDays
							(_bfs.expiryFirstDeliveryLag(), _strCalendar));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Compute the Reference Bond Price from the Quoted Futures Index Level
	 * 
	 * @param dblFuturesQuotedIndex The Quoted Futures Index Level
	 * 
	 * @return The Reference Price
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public double referencePrice (
		final double dblFuturesQuotedIndex)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblFuturesQuotedIndex))
			throw new java.lang.Exception ("BondFuturesConvention::referencePrice => Invalid Inputs!");

		double dblPeriodReferenceYield = 0.5 * (1. - dblFuturesQuotedIndex);

		double dblCompoundedDF = java.lang.Math.pow (1. / (1. + dblPeriodReferenceYield),
			org.drip.analytics.support.AnalyticsHelper.TenorToMonths (_strMaturityTenor) / 6);

		return dblCompoundedDF + 0.5 * _bfs.currentReferenceYield() * (1. - dblCompoundedDF) /
			dblPeriodReferenceYield;
	}

	/**
	 * Compute the Reference Bond Price from the Quoted Futures Index Level
	 * 
	 * @param dtValue The Valuation Date
	 * @param bond The Bond Instance
	 * @param dblFuturesQuotedIndex The Quoted Futures Index Level
	 * 
	 * @return The Reference Price
	 * 
	 * @throws java.lang.Exception Thrown if the Bond Futures Price Generic cannot be computed
	 */

	public double referencePrice (
		final org.drip.analytics.date.JulianDate dtValue,
		final org.drip.product.definition.Bond bond,
		final double dblFuturesQuotedIndex)
		throws java.lang.Exception
	{
		if (null == dtValue || null == bond) return referencePrice (dblFuturesQuotedIndex);

		if (!org.drip.quant.common.NumberUtil.IsValid (dblFuturesQuotedIndex))
			throw new java.lang.Exception ("AnalyticsHelper::referencePrice => Invalid Inputs");

		return bond.priceFromYield (new org.drip.param.valuation.ValuationParams (dtValue, dtValue, null),
			null, null, 1. - dblFuturesQuotedIndex);
	}

	/**
	 * Indicate whether the given bond is eligible to be delivered
	 * 
	 * @param dtValue The Value Date
	 * @param bond The Bond whose Eligibility is to be evaluated
	 * @param dblOutstandingNotional The Outstanding Notional
	 * @param strIssuer The Issuer
	 * 
	 * @return TRUE => The given bond is eligible to be delivered
	 */

	public boolean isEligible (
		final org.drip.analytics.date.JulianDate dtValue,
		final org.drip.product.definition.Bond bond,
		final double dblOutstandingNotional,
		final String strIssuer)
	{
		return _bfe.isEligible (dtValue, bond, dblOutstandingNotional, strIssuer);
	}

	@Override public String toString()
	{
		String strDump = "Name: " + _strName + " | Currency: " + _strCurrency + " | Calendar: " +
			_strCalendar + " | Underlier Type: " + _strUnderlierType + " | Underlier Sub-type: " +
				_strUnderlierSubtype + " | Maturity Tenor: " + _strMaturityTenor + " | Basket Notional: " +
					_dblBasketNotional + " | Component Notional Minimum: " + _dblComponentNotionalMinimum;

		for (int i = 0; i < _astrCode.length; ++i) {
			if (0 == i)
				strDump += " | CODES => {";
			else
				strDump += ", ";

			strDump += _astrCode[i];

			if (_astrExchange.length - 1 == i) strDump += "}";
		}

		for (int i = 0; i < _astrExchange.length; ++i) {
			if (0 == i)
				strDump += " | EXCHANGES => (";
			else
				strDump += ", ";

			strDump += _astrExchange[i];

			if (_astrExchange.length - 1 == i) strDump += ") ";
		}

		return strDump + "\n\t\t" + _dimExpiry + "\n\t\t" + _bfe + "\n\t\t" + _bfs;
	}
}
