
package org.drip.analytics.period;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
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
 * CashflowPeriodCurveFactors is an enhancement of the period class for holding the curve period measures. It
 * 	exposes the following functionality:
 * 
 * 	- Start/end survival probabilities, start/end notional, and period start/end discount factor.
 * 	- Period Reference Index Rate, Spread, and the full Coupon Rate
 * 	- Serialization into and de-serialization out of byte arrays.
 *
 * @author Lakshmi Krishnamurthy
 */

public class CashflowPeriodCurveFactors extends Period {
	protected double _dblEndDF = java.lang.Double.NaN;
	protected double _dblSpread = java.lang.Double.NaN;
	protected double _dblStartDF = java.lang.Double.NaN;
	protected double _dblIndexRate = java.lang.Double.NaN;
	protected double _dblEndNotional = java.lang.Double.NaN;
	protected double _dblEndSurvival = java.lang.Double.NaN;
	protected double _dblStartNotional = java.lang.Double.NaN;
	protected double _dblStartSurvival = java.lang.Double.NaN;
	protected double _dblFullCouponRate = java.lang.Double.NaN;

	/**
	 * Construct the CashflowPeriodCurveFactors class using the corresponding period curve measures.
	 * 
	 * @param dblStart Period Start date
	 * @param dblEnd Period end date
	 * @param dblAccrualStart Period accrual Start date
	 * @param dblAccrualEnd Period Accrual End date
	 * @param dblPay Period Pay date
	 * @param dblDCF Period day count fraction
	 * @param dblFullCouponRate Period Full (i.e., annualized Coupon Rate
	 * @param dblStartNotional Period Start Notional
	 * @param dblEndNotional Period End Notional
	 * @param dblStartDF Period Start discount factor
	 * @param dblEndDF Period End discount factor
	 * @param dblStartSurvival Period Start Survival
	 * @param dblEndSurvival Period End Survival
	 * @param dblSpread Period floater spread (Optional)
	 * @param dblIndexRate Period floating reference rate (Optional)
	 * 
	 * @throws java.lang.Exception Thrown if the inputs are invalid
	 */

	public CashflowPeriodCurveFactors (
		final double dblStart,
		final double dblEnd,
		final double dblAccrualStart,
		final double dblAccrualEnd,
		final double dblPay,
		final double dblDCF,
		final double dblFullCouponRate,
		final double dblStartNotional,
		final double dblEndNotional,
		final double dblStartDF,
		final double dblEndDF,
		final double dblStartSurvival,
		final double dblEndSurvival,
		final double dblSpread,
		final double dblIndexRate)
		throws java.lang.Exception
	{
		super (dblStart, dblEnd, dblAccrualStart, dblAccrualEnd, dblPay, dblDCF);

		if (!org.drip.quant.common.NumberUtil.IsValid (_dblFullCouponRate = dblFullCouponRate) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblStartNotional = dblStartNotional) ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblEndNotional = dblEndNotional) ||
					!org.drip.quant.common.NumberUtil.IsValid (_dblStartDF = dblStartDF) ||
						!org.drip.quant.common.NumberUtil.IsValid (_dblEndDF = dblEndDF) ||
							!org.drip.quant.common.NumberUtil.IsValid (_dblStartSurvival = dblStartSurvival)
								|| !org.drip.quant.common.NumberUtil.IsValid (_dblEndSurvival =
									dblEndSurvival))
			throw new java.lang.Exception ("CashflowPeriodCurveFactors ctr: Invalid Inputs");

		_dblSpread = dblSpread;
		_dblIndexRate = dblIndexRate;
	}

	/**
	 * De-serialization of CashflowPeriodCurveFactors from byte stream
	 * 
	 * @param ab Byte stream
	 * 
	 * @throws java.lang.Exception Thrown if cannot properly de-serialize CashflowPeriodCurveFactors
	 */

	public CashflowPeriodCurveFactors (
		final byte[] ab)
		throws java.lang.Exception
	{
		super (ab);

		if (null == ab || 0 == ab.length)
			throw new java.lang.Exception
				("CashflowPeriodCurveFactors de-serialize: Invalid byte stream input");

		java.lang.String strRawString = new java.lang.String (ab);

		if (null == strRawString || strRawString.isEmpty())
			throw new java.lang.Exception ("CashflowPeriodCurveFactors de-serializer: Empty state");

		java.lang.String strCP = strRawString.substring (0, strRawString.indexOf (objectTrailer()));

		if (null == strCP || strCP.isEmpty())
			throw new java.lang.Exception ("CashflowPeriodCurveFactors de-serializer: Cannot locate state");

		java.lang.String[] astrField = org.drip.quant.common.StringUtil.Split (strCP, fieldDelimiter());

		if (null == astrField || 10 > astrField.length)
			throw new java.lang.Exception
				("CashflowPeriodCurveFactors de-serialize: Invalid number of fields");

		if (null == astrField[1] || astrField[1].isEmpty() ||
			org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[1]))
			throw new java.lang.Exception
				("CashflowPeriodCurveFactors de-serializer: Cannot locate full coupon rate");

		_dblFullCouponRate = new java.lang.Double (astrField[1]);

		if (null == astrField[2] || astrField[2].isEmpty() ||
			org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[2]))
			throw new java.lang.Exception
				("CashflowPeriodCurveFactors de-serializer: Cannot locate start notional");

		_dblStartNotional = new java.lang.Double (astrField[2]);

		if (null == astrField[3] || astrField[3].isEmpty() ||
			org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[3]))
			throw new java.lang.Exception
				("CashflowPeriodCurveFactors de-serializer: Cannot locate end notional");

		_dblEndNotional = new java.lang.Double (astrField[3]);

		if (null == astrField[4] || astrField[4].isEmpty() ||
			org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[4]))
			throw new java.lang.Exception
				("CashflowPeriodCurveFactors de-serializer: Cannot locate start survival");

		_dblStartSurvival = new java.lang.Double (astrField[4]);

		if (null == astrField[5] || astrField[5].isEmpty() ||
			org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[5]))
			throw new java.lang.Exception
				("CashflowPeriodCurveFactors de-serializer: Cannot locate end survival");

		_dblEndSurvival = new java.lang.Double (astrField[5]);

		if (null == astrField[6] || astrField[6].isEmpty() ||
			org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[6]))
			throw new java.lang.Exception
				("CashflowPeriodCurveFactors de-serializer: Cannot locate start DF");

		_dblStartDF = new java.lang.Double (astrField[6]);

		if (null == astrField[7] || astrField[7].isEmpty() ||
			org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[7]))
			throw new java.lang.Exception
				("CashflowPeriodCurveFactors de-serializer: Cannot locate end DF");

		_dblEndDF = new java.lang.Double (astrField[7]);

		if (null == astrField[8] || astrField[8].isEmpty() ||
			org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[8]))
			throw new java.lang.Exception
				("CashflowPeriodCurveFactors de-serializer: Cannot locate floater spread");

		_dblSpread = new java.lang.Double (astrField[8]);

		if (null == astrField[9] || astrField[9].isEmpty() ||
			org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[9]))
			throw new java.lang.Exception
				("CashflowPeriodCurveFactors de-serializer: Cannot locate index rate");

		_dblIndexRate = new java.lang.Double (astrField[9]);
	}

	/**
	 * Get the period full coupon rate (annualized quote)
	 * 
	 * @return Period Full Coupon Rate
	 */

	public double getFullCouponRate()
	{
		return _dblFullCouponRate;
	}

	/**
	 * Get the period spread over the floating index
	 * 
	 * @return Period Spread
	 */

	public double getSpread()
	{
		return _dblSpread;
	}

	/**
	 * Get the period index rate
	 * 
	 * @return Period Index Reference Rate
	 */

	public double getIndexRate()
	{
		return _dblIndexRate;
	}

	/**
	 * Get the period start Notional
	 * 
	 * @return Period Start Notional
	 */

	public double getStartNotional()
	{
		return _dblStartNotional;
	}

	/**
	 * Get the period end Notional
	 * 
	 * @return Period end Notional
	 */

	public double getEndNotional()
	{
		return _dblEndNotional;
	}

	/**
	 * Get the period end discount factor
	 * 
	 * @return Period end discount factor
	 */

	public double getEndDF()
	{
		return _dblEndDF;
	}

	/**
	 * Get the period end survival probability
	 * 
	 * @return Period end survival probability
	 */

	public double getEndSurvival()
	{
		return _dblEndSurvival;
	}

	@Override public java.lang.String fieldDelimiter()
	{
		return "#";
	}

	@Override public java.lang.String objectTrailer()
	{
		return "^";
	}

	@Override public byte[] serialize()
	{
		java.lang.StringBuffer sb = new java.lang.StringBuffer();

		sb.append (new java.lang.String (super.serialize()) + fieldDelimiter() + _dblFullCouponRate +
			fieldDelimiter() + _dblStartNotional + fieldDelimiter() + _dblEndNotional + fieldDelimiter() +
				_dblStartSurvival + fieldDelimiter() + _dblEndSurvival + fieldDelimiter() + _dblStartDF +
					fieldDelimiter() + _dblEndDF + fieldDelimiter() + _dblSpread + fieldDelimiter() +
						_dblIndexRate);

		return sb.append (objectTrailer()).toString().getBytes();
	}
}
