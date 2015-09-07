
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
 * NormedR1ToNormedR1 is the Abstract Class underlying the f : Validated Normed R^1 -> Validated Normed R^1
 *  Function Spaces.
 * 
 * The Reference we've used is:
 * 
 * 	- Carl, B., and I. Stephani (1990): Entropy, Compactness, and Approximation of Operators, Cambridge
 * 		University Press, Cambridge UK.
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class NormedR1ToNormedR1 extends org.drip.spaces.RxToR1.NormedRxToNormedR1 {
	private org.drip.spaces.metric.R1Normed _r1Input = null;
	private org.drip.spaces.metric.R1Normed _r1Output = null;
	private org.drip.function.definition.R1ToR1 _funcR1ToR1 = null;

	protected NormedR1ToNormedR1 (
		final org.drip.spaces.metric.R1Normed r1Input,
		final org.drip.spaces.metric.R1Normed r1Output,
		final org.drip.function.definition.R1ToR1 funcR1ToR1)
		throws java.lang.Exception
	{
		if (null == (_r1Input = r1Input) || null == (_r1Output = r1Output))
			throw new java.lang.Exception ("NormedR1ToNormedR1 ctr: Invalid Inputs");

		_funcR1ToR1 = funcR1ToR1;
	}

	/**
	 * Retrieve the Underlying R1ToR1 Function
	 * 
	 * @return The Underlying R1ToR1 Function
	 */

	public org.drip.function.definition.R1ToR1 function()
	{
		return _funcR1ToR1;
	}

	@Override public double sampleSupremumNorm (
		final org.drip.spaces.instance.GeneralizedValidatedVector gvvi)
		throws java.lang.Exception
	{
		if (null == _funcR1ToR1 || null == gvvi || !gvvi.tensorSpaceType().match (_r1Input))
			throw new java.lang.Exception ("NormedR1ToNormedR1::sampleSupremumNorm => Invalid Input");

		double[] adblInstance = ((org.drip.spaces.instance.ValidatedR1) gvvi).instance();

		int iNumSample = adblInstance.length;

		double dblSupremumNorm = java.lang.Math.abs (_funcR1ToR1.evaluate (adblInstance[0]));

		for (int i = 1; i < iNumSample; ++i) {
			double dblResponse = java.lang.Math.abs (_funcR1ToR1.evaluate (adblInstance[i]));

			if (dblResponse > dblSupremumNorm) dblSupremumNorm = dblResponse;
		}

		return dblSupremumNorm;
	}

	@Override public double sampleMetricNorm (
		final org.drip.spaces.instance.GeneralizedValidatedVector gvvi)
		throws java.lang.Exception
	{
		int iPNorm = _r1Output.pNorm();

		if (java.lang.Integer.MAX_VALUE == iPNorm) return sampleSupremumNorm (gvvi);

		if (null == _funcR1ToR1 || null == gvvi || !gvvi.tensorSpaceType().match (_r1Input))
			throw new java.lang.Exception ("NormedR1ToNormedR1::sampleMetricNorm => Invalid Input");

		double[] adblInstance = ((org.drip.spaces.instance.ValidatedR1) gvvi).instance();

		double dblNorm = 0.;
		int iNumSample = adblInstance.length;

		for (int i = 0; i < iNumSample; ++i)
			dblNorm += java.lang.Math.pow (java.lang.Math.abs (_funcR1ToR1.evaluate (adblInstance[i])),
				iPNorm);

		return java.lang.Math.pow (dblNorm, 1. / iPNorm);
	}

	@Override public double populationESS()
		throws java.lang.Exception
	{
		if (null == _funcR1ToR1)
			throw new java.lang.Exception ("NormedR1ToNormedR1::populationESS => Invalid Input");

		return _funcR1ToR1.evaluate (_r1Input.populationMode());
	}

	@Override public org.drip.spaces.metric.R1Normed outputMetricVectorSpace()
	{
		return _r1Output;
	}

	@Override public org.drip.spaces.metric.R1Normed inputMetricVectorSpace()
	{
		return _r1Input;
	}
}
