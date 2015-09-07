
package org.drip.function.R1ToR1;

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
 * NaturalLogSeriesElement implements an element in the natural log series expansion.
 *
 * @author Lakshmi Krishnamurthy
 */

public class NaturalLogSeriesElement extends org.drip.function.definition.R1ToR1 {
	private int _iExponent = -1;

	/**
	 * NaturalLogSeriesElement constructor
	 * 
	 * @param iExponent The series exponent
	 * 
	 * @throws java.lang.Exception Thrown if the inputs are invalid
	 */

	public NaturalLogSeriesElement (
		final int iExponent)
		throws java.lang.Exception
	{
		super (null);

		if (0 > (_iExponent = iExponent))
			throw new java.lang.Exception ("NaturalLogSeriesElement ctr: Invalid Inputs");
	}

	@Override public double evaluate (
		final double dblVariate)
		throws java.lang.Exception
	{
		return java.lang.Math.pow (dblVariate, _iExponent) / org.drip.quant.common.NumberUtil.Factorial
			(_iExponent);
	}

	@Override public double derivative (
		final double dblVariate,
		final int iOrder)
		throws java.lang.Exception
	{
		return iOrder > _iExponent ? 0. : java.lang.Math.pow (dblVariate, _iExponent - iOrder) /
			org.drip.quant.common.NumberUtil.Factorial (_iExponent - iOrder);
	}

	@Override public double integrate (
		final double dblBegin,
		final double dblEnd)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblBegin) || !org.drip.quant.common.NumberUtil.IsValid
			(dblEnd))
			throw new java.lang.Exception ("NaturalLogSeriesElement::integrate => Invalid Inputs");

		return (java.lang.Math.pow (dblEnd, _iExponent) - java.lang.Math.pow (dblBegin, _iExponent)) /
			org.drip.quant.common.NumberUtil.Factorial (_iExponent + 1);
	}

	/**
	 * Retrieve the exponent in the natural log series
	 * 
	 * @return Exponent in the natural log series
	 */

	public int getExponent()
	{
		return _iExponent;
	}
}
