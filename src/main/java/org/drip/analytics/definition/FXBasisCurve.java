
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
 * FXBasisCurve implements the curve representing the FXBasis nodes. It extends the Curve class, and exposes
 * 	the following functionality:
 * 	- Retrieve the spot parameters (FX Spot, Spot Date, and the currency pair)
 *  - Indicate if the basis has been bootstrapped
 *  - Calculate the Complete set of FX Forward corresponding to each basis node
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class FXBasisCurve implements org.drip.analytics.definition.Curve {

	/**
	 * Returns the Spot Date
	 * 
	 * @return Spot Date
	 */

	public abstract org.drip.analytics.date.JulianDate spotDate();

	/**
	 * Get the FX Spot
	 * 
	 * @return FX Spot
	 */

	public abstract double fxSpot();

	/**
	 * Return the currency pair instance
	 * 
	 * @return CurrencyPair object instance
	 */

	public abstract org.drip.product.params.CurrencyPair currencyPair();

	/**
	 * Return if the inputs are for bootstrapped FX basis
	 * 
	 * @return True if the inputs are for bootstrapped FX basis
	 */

	public abstract boolean isBasisBootstrapped();

	/**
	 * Return the array of full FX Forwards
	 * 
	 * @param valParam ValuationParams
	 * @param dcNum Discount Curve Numerator
	 * @param dcDenom Discount Curve Numerator
	 * @param bBasisOnDenom True if the basis is on the denominator
	 * @param bFwdAsPIP True if the FX Forwards are to represented as PIP
	 * 
	 * @return Array of FXForward
	 */

	public abstract double[] fxForward (
		final org.drip.param.valuation.ValuationParams valParam,
		final org.drip.analytics.rates.DiscountCurve dcNum,
		final org.drip.analytics.rates.DiscountCurve dcDenom,
		final boolean bBasisOnDenom,
		final boolean bFwdAsPIP);
}
