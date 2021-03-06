
package org.drip.analytics.definition;

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
 * FXForwardCurve implements the curve representing the FXForward nodes. It extends the Curve class, and
 * 	exposes the following functionality:
 * 	- Retrieve the spot parameters (FX Spot, Spot Date, and the currency pair)
 *  - Calculate the Zero set of FX Basis/Zero Rate nodes corresponding to each basis node
 *  - Bootstrap basis points/discount curves corresponding to the FXForward node set
 *  - Imply the zero rate to a given date from the FXForward curve
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class FXForwardCurve implements org.drip.analytics.definition.Curve {

	/**
	 * 
	 * Return the Spot Date
	 * 
	 * @return Spot Date
	 */

	public abstract org.drip.analytics.date.JulianDate spotDate();

	/**
	 * Return the FX Spot
	 * 
	 * @return FXSpot
	 */

	public abstract double fxSpot();

	/**
	 * Return the CurrencyPair
	 * 
	 * @return CurrencyPair
	 */

	public abstract org.drip.product.params.CurrencyPair currencyPair();

	/**
	 * Calculate the set of Zero basis given the input discount curves
	 * 
	 * @param valParam ValuationParams
	 * @param dcNum Discount Curve Numerator
	 * @param dcDenom Discount Curve Denominator
	 * @param bBasisOnDenom True if the basis is calculated on the denominator discount curve
	 * 
	 * @return Array of the computed basis
	 */

	public abstract double[] zeroBasis (
		final org.drip.param.valuation.ValuationParams valParam,
		final org.drip.analytics.rates.DiscountCurve dcNum,
		final org.drip.analytics.rates.DiscountCurve dcDenom,
		final boolean bBasisOnDenom);

	/**
	 * Bootstrap the basis to the discount curve inputs
	 * 
	 * @param valParam ValuationParams
	 * @param dcNum Discount Curve Numerator
	 * @param dcDenom Discount Curve Denominator
	 * @param bBasisOnDenom True if the basis is calculated on the denominator discount curve
	 * 
	 * @return Array of the computed basis
	 */

	public abstract double[] bootstrapBasis (
		final org.drip.param.valuation.ValuationParams valParam,
		final org.drip.analytics.rates.DiscountCurve dcNum,
		final org.drip.analytics.rates.DiscountCurve dcDenom,
		final boolean bBasisOnDenom);

	/**
	 * Bootstrap the discount curve from the discount curve inputs
	 * 
	 * @param valParam ValuationParams
	 * @param dcNum Discount Curve Numerator
	 * @param dcDenom Discount Curve Denominator
	 * @param bBasisOnDenom True if the basis is calculated on the denominator discount curve
	 * 
	 * @return Array of the computed basis
	 */

	public abstract org.drip.analytics.rates.DiscountCurve bootstrapBasisDC (
		final org.drip.param.valuation.ValuationParams valParam,
		final org.drip.analytics.rates.DiscountCurve dcNum,
		final org.drip.analytics.rates.DiscountCurve dcDenom,
		final boolean bBasisOnDenom);

	/**
	 * Calculate the rates implied by the discount curve inputs
	 * 
	 * @param valParam ValuationParams
	 * @param dcNum Discount Curve Numerator
	 * @param dcDenom Discount Curve Denominator
	 * @param bBasisOnDenom True if the basis is calculated on the denominator discount curve
	 * 
	 * @return Array of the computed implied rates
	 */

	public abstract double[] impliedNodeRates (
		final org.drip.param.valuation.ValuationParams valParam,
		final org.drip.analytics.rates.DiscountCurve dcNum,
		final org.drip.analytics.rates.DiscountCurve dcDenom,
		final boolean bBasisOnDenom);

	/**
	 * Calculate the rate implied by the discount curve inputs to a specified date
	 * 
	 * @param valParam ValuationParams
	 * @param dcNum Discount Curve Numerator
	 * @param dcDenom Discount Curve Denominator
	 * @param dblDate Date to which the implied rate is sought
	 * @param bBasisOnDenom True if the implied rate is calculated on the denominator discount curve
	 * 
	 * @return Implied rate
	 * 
	 * @throws java.lang.Exception Thrown if the implied rate cannot be calculated
	 */

	public abstract double rate (
		final org.drip.param.valuation.ValuationParams valParam,
		final org.drip.analytics.rates.DiscountCurve dcNum,
		final org.drip.analytics.rates.DiscountCurve dcDenom,
		final double dblDate,
		final boolean bBasisOnDenom)
		throws java.lang.Exception;
}
