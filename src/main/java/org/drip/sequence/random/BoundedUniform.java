
package org.drip.sequence.random;

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
 * BoundedUniform implements the Bounded Uniform Distribution, with a Uniform Distribution between a lower
 *  and an upper Bound.
 *
 * @author Lakshmi Krishnamurthy
 */

public class BoundedUniform extends org.drip.sequence.random.Bounded {
	private java.util.Random _rng = new java.util.Random();

	/**
	 * BoundedUniform Distribution Constructor
	 * 
	 * @param dblLowerBound The Lower Bound
	 * @param dblUpperBound The Upper Bound
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public BoundedUniform (
		final double dblLowerBound,
		final double dblUpperBound)
		throws java.lang.Exception
	{
		super (dblLowerBound, dblUpperBound);
	}

	@Override public double random()
	{
		double dblLowerBound = lowerBound();

		return dblLowerBound + _rng.nextDouble() * (upperBound() - dblLowerBound);
	}
}
