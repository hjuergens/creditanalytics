
package org.drip.analytics.rates;

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
 * ExplicitBootDiscountCurve exposes the functionality associated with the bootstrapped Discount Curve.
 *  - Generate a curve shifted using targeted basis at specific nodes.
 *  - Generate scenario tweaked Latent State from the base forward curve corresponding to mode adjusted
 *  	(flat/parallel/custom) manifest measure/quantification metric.
 *  - Retrieve array of latent state manifest measure, instrument quantification metric, and the array of
 *  	calibration components.
 *  - Set/retrieve curve construction input instrument sets.
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class ExplicitBootDiscountCurve extends org.drip.analytics.rates.DiscountCurve
	implements org.drip.analytics.definition.ExplicitBootCurve {

	protected ExplicitBootDiscountCurve (
		final double dblEpochDate,
		final String strCurrency,
		final org.drip.param.valuation.CollateralizationParams collatParams)
		throws java.lang.Exception
	{
		super (dblEpochDate, strCurrency, collatParams, null);
	}

	/**
	 * Create a shifted curve from an array of basis shifts
	 * 
	 * @param adblDate Array of dates
	 * @param adblBasis Array of basis
	 * 
	 * @return Discount Curve
	 */

	public abstract ExplicitBootDiscountCurve createBasisRateShiftedCurve (
		final double[] adblDate,
		final double[] adblBasis);

	@Override public boolean setCCIS (
		final org.drip.analytics.input.CurveConstructionInputSet ccis)
	{
		return null != (_ccis = ccis);
	}

	@Override public org.drip.product.definition.CalibratableFixedIncomeComponent[] calibComp()
	{
		return null == _ccis ? null : _ccis.components();
	}

	@Override public org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> manifestMeasure (
		final String strInstrumentCode)
	{
		if (null == _ccis) return null;

		org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>>
			mapQuote = _ccis.quoteMap();

		if (null == mapQuote || !mapQuote.containsKey (strInstrumentCode)) return null;

		return mapQuote.get (strInstrumentCode);
	}
}
