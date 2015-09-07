
package org.drip.quant.calculus;

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
 * DerivativeControl provides bumps needed for numerically approximating derivatives. Bumps can be absolute
 * 	or relative, and they default to a floor.
 *
 * @author Lakshmi Krishnamurthy
 */

public class DerivativeControl {
	private static final double ABSOLUTE_INCREMENT = 1.e-05;
	private static final double RELATIVE_INCREMENT = 1.e-06;

	private double _dblBumpFactor = RELATIVE_INCREMENT;

	/**
	 * Empty DerivativeControl constructor
	 */

	public DerivativeControl()
	{
	}

	/**
	 * DerivativeControl constructor
	 * 
	 * @param dblBumpFactor Bump Factor
	 * 
	 * @throws java.lang.Exception Thrown if inputs are invalid
	 */

	public DerivativeControl (
		final double dblBumpFactor)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblBumpFactor))
			throw new java.lang.Exception ("DerivativeControl constructor: Invalid inputs!");
	}

	/**
	 * Retrieve the bump factor
	 * 
	 * @return The Bump Factor
	 */

	public double getBumpFactor()
	{
		return _dblBumpFactor;
	}

	/**
	 * Calculate and return the variate infinitesimal
	 * 
	 * @param dblVariate Variate Input
	 * 
	 * @return Variate Infinitesimal
	 */

	public double getVariateInfinitesimal (
		final double dblVariate)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblVariate))
			throw new java.lang.Exception ("DerivativeControl::getVariateInfinitesimal => Invalid input");

		double dblVariateInfinitesimal = dblVariate * getBumpFactor();

		if (java.lang.Math.abs (dblVariateInfinitesimal) < ABSOLUTE_INCREMENT) return ABSOLUTE_INCREMENT;

		return dblVariateInfinitesimal;
	}
}
