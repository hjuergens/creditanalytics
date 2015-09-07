
package org.drip.spaces.instance;

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
 * ValidatedR1 holds the Validated R^1 Vector Instance Sequence and the Corresponding Generalized Vector
 *  Space Type.
 * 
 * The Reference we've used is:
 * 
 * 	- Carl, B., and I. Stephani (1990): Entropy, Compactness, and Approximation of Operators, Cambridge
 * 		University Press, Cambridge UK.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ValidatedR1 implements org.drip.spaces.instance.GeneralizedValidatedVector {
	private double[] _adblInstance = null;
	private org.drip.spaces.tensor.R1GeneralizedVector _gvR1 = null;

	/**
	 * ValidatedR1 Constructor
	 * 
	 * @param gvR1 The R^1 Tensor Space Type
	 * @param adblInstance The Data Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ValidatedR1 (
		final org.drip.spaces.tensor.R1GeneralizedVector gvR1,
		final double[] adblInstance)
		throws java.lang.Exception
	{
		if (null == (_gvR1 = gvR1) || null == (_adblInstance = adblInstance) || 0 == _adblInstance.length)
			throw new java.lang.Exception ("ValidatedR1 ctr: Invalid Inputs");
	}

	@Override public org.drip.spaces.tensor.R1GeneralizedVector tensorSpaceType()
	{
		return _gvR1;
	}

	/**
	 * Retrieve the Instance Sequence
	 * 
	 * @return The Instance Sequence
	 */

	public double[] instance()
	{
		return _adblInstance;
	}

	@Override public int sampleSize()
	{
		return _adblInstance.length;
	}
}
