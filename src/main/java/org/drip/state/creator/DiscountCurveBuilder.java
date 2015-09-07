
package org.drip.state.creator;

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
 * This class contains the builder functions that construct the discount curve (comprising both the rates and
 * 	the discount factors) instance. It contains static functions that build different types of discount curve
 *  from 3 major types of inputs:
 * 	- From a variety of ordered DF-sensitive calibration instruments and their quotes
 *  - From an array of ordered discount factors
 *  - From a serialized byte stream of the discount curve instance
 *
 * @author Lakshmi Krishnamurthy
 */

public class DiscountCurveBuilder {

	/**
	 * Constant Forward Bootstrap mode
	 */

	public static final String BOOTSTRAP_MODE_CONSTANT_FORWARD = "ConstantForward";

	/**
	 * Polynomial Spline DF Bootstrap mode
	 */

	public static final String BOOTSTRAP_MODE_POLYNOMIAL_SPLINE_DF = "PolynomialSplineDF";

	/**
	 * Build a Discount Curve from an array of discount factors
	 * 
	 * @param dtStart Start Date
	 * @param strCurrency Currency
	 * @param collatParams Collateralization Parameters
	 * @param adblDate Array of dates
	 * @param adblDF array of discount factors
	 * @param strBootstrapMode Mode of the bootstrapping to be done: "ConstantForward", "LinearForward",
	 * 	"QuadraticForward", or "CubicForward". Defaults to "ConstantForward".
	 * 
	 * @return Discount Curve
	 */

	public static final org.drip.analytics.rates.DiscountCurve BuildFromDF (
		final org.drip.analytics.date.JulianDate dtStart,
		final String strCurrency,
		final org.drip.param.valuation.CollateralizationParams collatParams,
		final double adblDate[],
		final double adblDF[],
		final String strBootstrapMode)
	{
		if (null == adblDate || 0 == adblDate.length || null == adblDF || adblDate.length != adblDF.length ||
			null == dtStart || null == strCurrency || strCurrency.isEmpty())
			return null;

		double dblDFBegin = 1.;
		double[] adblRate = new double[adblDate.length];

		double dblPeriodBegin = dtStart.julian();

		for (int i = 0; i < adblDate.length; ++i) {
			if (adblDate[i] <= dblPeriodBegin) return null;

			adblRate[i] = 365.25 / (adblDate[i] - dblPeriodBegin) * java.lang.Math.log (dblDFBegin /
				adblDF[i]);

			dblDFBegin = adblDF[i];
			dblPeriodBegin = adblDate[i];
		}

		try {
			if (null == strBootstrapMode)
				return new org.drip.state.curve.FlatForwardDiscountCurve (dtStart, strCurrency, collatParams,
					adblDate, adblRate, false, "", -1);

			if (BOOTSTRAP_MODE_POLYNOMIAL_SPLINE_DF.equalsIgnoreCase (strBootstrapMode))
				return new org.drip.state.curve.NonlinearDiscountFactorDiscountCurve (dtStart, strCurrency,
					collatParams, adblDate, adblRate);

			return new org.drip.state.curve.FlatForwardDiscountCurve (dtStart, strCurrency, collatParams,
				adblDate, adblRate, false, "", -1);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Create a discount curve from the flat rate
	 * 
	 * @param dtStart Start Date
	 * @param strCurrency Currency
	 * @param collatParams Collateralization Parameters
	 * @param dblRate Rate
	 * 
	 * @return Discount Curve
	 */

	public static final org.drip.analytics.rates.ExplicitBootDiscountCurve CreateFromFlatRate (
		final org.drip.analytics.date.JulianDate dtStart,
		final String strCurrency,
		final org.drip.param.valuation.CollateralizationParams collatParams,
		final double dblRate)
	{
		if (null == dtStart || !org.drip.quant.common.NumberUtil.IsValid (dblRate)) return null;

		try {
			return new org.drip.state.curve.FlatForwardDiscountCurve (dtStart, strCurrency, collatParams, new
				double[] {dtStart.julian()}, new double[] {dblRate}, false, "", -1);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Create a Discount Curve from the Flat Yield
	 * 
	 * @param dtStart Start Date
	 * @param strCurrency Currency
	 * @param collatParams Collateralization Parameters
	 * @param dblYield Yield
	 * @param strCompoundingDayCount Day Count Convention to be used for Discrete Compounding
	 * @param iCompoundingFreq Frequency to be used for Discrete Compounding
	 * 
	 * @return The Discount Curve Instance
	 */

	public static final org.drip.analytics.rates.ExplicitBootDiscountCurve CreateFromFlatYield (
		final org.drip.analytics.date.JulianDate dtStart,
		final String strCurrency,
		final org.drip.param.valuation.CollateralizationParams collatParams,
		final double dblYield,
		final String strCompoundingDayCount,
		final int iCompoundingFreq)
	{
		if (null == dtStart || !org.drip.quant.common.NumberUtil.IsValid (dblYield)) return null;

		try {
			return new org.drip.state.curve.FlatForwardDiscountCurve (dtStart, strCurrency, collatParams, new
				double[] {dtStart.julian()}, new double[] {dblYield}, true, strCompoundingDayCount,
					iCompoundingFreq);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Create a discount curve from an array of dates/rates
	 * 
	 * @param dtStart Start Date
	 * @param strCurrency Currency
	 * @param collatParams Collateralization Parameters
	 * @param adblDate array of dates
	 * @param adblRate array of rates
	 * @param strBootstrapMode Mode of the bootstrapping to be done: "ConstantForward", "LinearForward",
	 * 	"QuadraticForward", or "CubicForward". Defaults to "ConstantForward".
	 * 
	 * @return Creates the discount curve
	 */

	public static final org.drip.analytics.rates.ExplicitBootDiscountCurve CreateDC (
		final org.drip.analytics.date.JulianDate dtStart,
		final String strCurrency,
		final org.drip.param.valuation.CollateralizationParams collatParams,
		final double[] adblDate,
		final double[] adblRate,
		final String strBootstrapMode)
	{
		try {
			if (null == strBootstrapMode)
				return new org.drip.state.curve.FlatForwardDiscountCurve (dtStart, strCurrency, collatParams,
					adblDate, adblRate, false, "", -1);

			if (BOOTSTRAP_MODE_POLYNOMIAL_SPLINE_DF.equalsIgnoreCase (strBootstrapMode))
				return new org.drip.state.curve.NonlinearDiscountFactorDiscountCurve (dtStart, strCurrency,
					collatParams, adblDate, adblRate);

			return new org.drip.state.curve.FlatForwardDiscountCurve (dtStart, strCurrency, collatParams,
				adblDate, adblRate, false, "", -1);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
