
package org.drip.spaces.RxToR1;

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
 * NormedR1CombinatorialToR1Continuous implements the f : Validated Normed R^1 Combinatorial -> Validated
 * 	Normed R^1 Continuous Function Spaces.
 * 
 * The Reference we've used is:
 * 
 * 	- Carl, B., and I. Stephani (1990): Entropy, Compactness, and Approximation of Operators, Cambridge
 * 		University Press, Cambridge UK.
 *
 * @author Lakshmi Krishnamurthy
 */

public class NormedR1CombinatorialToR1Continuous extends org.drip.spaces.RxToR1.NormedR1ToNormedR1 {

	/**
	 * NormedR1CombinatorialToR1Continuous Function Space Constructor
	 * 
	 * @param r1CombinatorialInput The Combinatorial R^1 Input Metric Vector Space
	 * @param r1ContinuousOutput The Continuous R^1 Output Metric Vector Space
	 * @param funcR1ToR1 The R1ToR1 Function
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public NormedR1CombinatorialToR1Continuous (
		final org.drip.spaces.metric.R1Combinatorial r1CombinatorialInput,
		final org.drip.spaces.metric.R1Continuous r1ContinuousOutput,
		final org.drip.function.definition.R1ToR1 funcR1ToR1)
		throws java.lang.Exception
	{
		super (r1CombinatorialInput, r1ContinuousOutput, funcR1ToR1);
	}

	@Override public double populationMetricNorm()
		throws java.lang.Exception
	{
		int iPNorm = outputMetricVectorSpace().pNorm();

		if (java.lang.Integer.MAX_VALUE == iPNorm) return populationSupremumMetricNorm();

		org.drip.spaces.metric.R1Combinatorial r1Combinatorial = (org.drip.spaces.metric.R1Combinatorial)
			inputMetricVectorSpace();

		org.drip.function.definition.R1ToR1 funcR1ToR1 = function();

		org.drip.measure.continuous.R1 distR1 = r1Combinatorial.borelSigmaMeasure();

		if (null == distR1 || null == funcR1ToR1)
			throw new java.lang.Exception
				("NormedR1CombinatorialToR1Continuous::populationMetricNorm => Cannot compute Population Norm");

		java.util.List<java.lang.Double> lsElem = r1Combinatorial.elementSpace();

		double dblPopulationMetricNorm  = 0.;
		double dblNormalizer = 0.;

		for (double dblElement : lsElem) {
			double dblProbabilityDensity = distR1.density (dblElement);

			dblNormalizer += dblProbabilityDensity;

			dblPopulationMetricNorm += dblProbabilityDensity * java.lang.Math.pow (java.lang.Math.abs
				(funcR1ToR1.evaluate (dblElement)), iPNorm);
		}

		return java.lang.Math.pow (dblPopulationMetricNorm / dblNormalizer, 1. / iPNorm);
	}
}
