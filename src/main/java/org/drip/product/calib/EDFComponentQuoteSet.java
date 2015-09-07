
package org.drip.product.calib;

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
 * EDFComponentQuoteSet extends the ProductQuoteSet by implementing the Calibration Parameters for the EDF
 * 	Component. Currently it exposes the Price and the Rate Quote Fields.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class EDFComponentQuoteSet extends org.drip.product.calib.ProductQuoteSet {

	/**
	 * EDFComponentQuoteSet Constructor
	 * 
	 * @param aLSS Array of Latent State Specification
	 * 
	 * @throws java.lang.Exception Thrown if Inputs are invalid
	 */

	public EDFComponentQuoteSet (
		final org.drip.state.representation.LatentStateSpecification[] aLSS)
		throws java.lang.Exception
	{
		super (aLSS);
	}

	/**
	 * Set the Price
	 * 
	 * @param dblPrice The Price
	 * 
	 * @return TRUE => Price successfully set
	 */

	public boolean setPrice (
		final double dblPrice)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblPrice)) return false;

		_mapQuote.put ("Price", dblPrice);

		return true;
	}

	/**
	 * Indicate if the Price Field exists
	 * 
	 * @return TRUE => Price Field Exists
	 */

	public boolean containsPrice()
	{
		return _mapQuote.containsKey ("Price");
	}

	/**
	 * Retrieve the Price
	 * 
	 * @return The Price
	 * 
	 * @throws java.lang.Exception Thrown if the Price Field does not exist
	 */

	public double price()
		throws java.lang.Exception
	{
		if (!containsPrice())
			throw new java.lang.Exception ("EDFComponentQuoteSet::price => Does not contain Price");

		return _mapQuote.get ("Price");
	}

	/**
	 * Set the Rate
	 * 
	 * @param dblRate The Rate
	 * 
	 * @return TRUE => The Rate successfully set
	 */

	public boolean setRate (
		final double dblRate)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblRate)) return false;

		_mapQuote.put ("Rate", dblRate);

		return true;
	}

	/**
	 * Indicate if the Rate Field exists
	 * 
	 * @return TRUE => Rate Field Exists
	 */

	public boolean containsRate()
	{
		return _mapQuote.containsKey ("Rate");
	}

	/**
	 * Retrieve the Rate
	 * 
	 * @return The Rate
	 * 
	 * @throws java.lang.Exception Thrown if the Rate Field does not exist
	 */

	public double rate()
		throws java.lang.Exception
	{
		if (!containsRate())
			throw new java.lang.Exception ("EDFComponentQuoteSet::rate => Does not contain rate");

		return _mapQuote.get ("Rate");
	}
}
