
package org.drip.spline.tension;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2013 Lakshmi Krishnamurthy
 * 
 * This file is part of CreditAnalytics, a free-software/open-source library for fixed income analysts and
 * 		developers - http://www.credit-trader.org
 * 
 * CreditAnalytics is a free, full featured, fixed income credit analytics library, developed with a special
 * 		focus towards the needs of the bonds and credit products community.
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
 * C2BasisFunctionSet class implements per-segment function set for tension splines. Derived implementations
 *  expose explicit targeted basis functions.
 *
 * @author Lakshmi Krishnamurthy
 */

public class C2BasisFunctionSet extends org.drip.spline.basis.FunctionSet {
	protected double _dblTension = java.lang.Double.NaN;

	private static final org.drip.quant.function1D.AbstractUnivariate[] responseBasis (
		final org.drip.quant.function1D.AbstractUnivariate[] aAUHat)
	{
		if (null == aAUHat || 2 != aAUHat.length) return null;

		try {
			return new org.drip.quant.function1D.AbstractUnivariate[] {new
				org.drip.quant.function1D.Polynomial (0), new org.drip.quant.function1D.UnivariateReflection
					(new org.drip.quant.function1D.Polynomial (1)), aAUHat[0], aAUHat[1]};
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * C2BasisFunctionSet constructor
	 * 
	 * @param dblTension Tension Parameter
	 * @param aAUHat The Hat Representation Function Set
	 * 
	 * @throws java.lang.Exception Thrown if the inputs are invalid
	 */

	public C2BasisFunctionSet (
		final double dblTension,
		final org.drip.quant.function1D.AbstractUnivariate[] aAUHat)
		throws java.lang.Exception
	{
		super (responseBasis (aAUHat));

		if (!org.drip.quant.common.NumberUtil.IsValid (_dblTension = dblTension))
			throw new java.lang.Exception ("C2BasisFunctionSet ctr: Invalid Inputs!");
	}

	/**
	 * Retrieve the Tension Parameter
	 * 
	 * @return The Tension Parameter
	 */

	public double tension()
	{
		return _dblTension;
	}
}
