
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
 * UnivariateSequenceGenerator implements the Univariate Random Sequence Generator Functionality.
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class UnivariateSequenceGenerator {

	/**
	 * Generate a Random Number according to the specified rule
	 * 
	 * @return The Random Number
	 */

	public abstract double random();

	/**
	 * Generate a Random Sequence
	 * 
	 * @param iNumEntry Number of Entries in the Sequence
	 * 
	 * @return The Random Sequence
	 */

	public double[] sequence (
		final int iNumEntry)
	{
		if (0 >= iNumEntry) return null;

		double[] adblSequence = new double[iNumEntry];

		for (int i = 0; i < iNumEntry; ++i)
			adblSequence[i] = random();

		return adblSequence;
	}

	/**
	 * Generate a Random Sequence along with its Metrics
	 * 
	 * @param iNumEntry Number of Entries in the Sequence
	 * @param distPopulation The True Underlying Generator Distribution of the Population
	 * 
	 * @return The Random Sequence (along with its Metrics)
	 */

	public org.drip.sequence.metrics.SingleSequenceAgnosticMetrics sequence (
		final int iNumEntry,
		final org.drip.measure.continuous.R1 distPopulation)
	{
		try {
			return new org.drip.sequence.metrics.SingleSequenceAgnosticMetrics (sequence (iNumEntry),
				distPopulation);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
