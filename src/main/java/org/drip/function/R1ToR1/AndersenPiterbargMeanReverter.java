
package org.drip.function.R1ToR1;

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
 * AndersenPiterbargMeanReverter implements the mean-reverting Univariate Function detailed in:
 * 
 * 	- Andersen and Piterbarg (2010): Interest Rate Modeling (3 Volumes), Atlantic Financial Press.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class AndersenPiterbargMeanReverter extends org.drip.function.definition.R1ToR1 {
	private org.drip.function.R1ToR1.ExponentialDecay _auExpDecay = null;
	private org.drip.function.definition.R1ToR1 _auSteadyState = null;

	/**
	 * AndersenPiterbargMeanReverter constructor
	 * 
	 * @param auExpDecay The Exponential Decay Univariate Function
	 * @param auSteadyState The Steady State (i.e., Infinite Time) Undamped Behavior Univariate Function
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public AndersenPiterbargMeanReverter (
		final org.drip.function.R1ToR1.ExponentialDecay auExpDecay,
		final org.drip.function.definition.R1ToR1 auSteadyState)
		throws java.lang.Exception
	{
		super (null);

		if (null == (_auExpDecay = auExpDecay) || null == (_auSteadyState = auSteadyState))
			throw new java.lang.Exception ("AndersenPiterbargMeanReverter ctr => Invalid Inputs");
	}

	@Override public double evaluate (
		final double dblVariate)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblVariate))
			throw new java.lang.Exception ("AndersenPiterbargMeanReverter::evaluate => Invalid Inputs");

		return (1. - _auExpDecay.evaluate (dblVariate)) * _auSteadyState.evaluate (dblVariate);
	}
}
