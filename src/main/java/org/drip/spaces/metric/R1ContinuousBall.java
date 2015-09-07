
package org.drip.spaces.metric;

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
 * R1ContinuousBall extends the Continuous R^1 Banach Space by enforcing the Closed Bounded Metric.
 * 
 * The Reference we've used is:
 * 
 * 	- Carl, B., and I. Stephani (1990): Entropy, Compactness, and Approximation of Operators, Cambridge
 * 		University Press, Cambridge UK.
 *
 * @author Lakshmi Krishnamurthy
 */

public class R1ContinuousBall extends org.drip.spaces.metric.R1Continuous {
	private double _dblNormRadius = java.lang.Double.NaN;

	/**
	 * Construct a R1ContinuousBall Instance of Unit Radius
	 * 
	 * @param dblLeftEdge The Left Edge
	 * @param dblRightEdge The Right Edge
	 * @param distR1 The R^1 Borel Sigma Measure
	 * @param iPNorm The p-norm of the Space
	 * 
	 * @return R1ContinuousBall Instance of Unit Radius
	 */

	public static final R1ContinuousBall ClosedUnit (
		final double dblLeftEdge,
		final double dblRightEdge,
		final org.drip.measure.continuous.R1 distR1,
		final int iPNorm)
	{
		try {
			return new R1ContinuousBall (dblLeftEdge, dblRightEdge, distR1, iPNorm, 1.);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * R1ContinuousBall Constructor
	 * 
	 * @param dblLeftEdge The Left Edge
	 * @param dblRightEdge The Right Edge
	 * @param distR1 The R^1 Borel Sigma Measure
	 * @param iPNorm The p-norm of the Space
	 * @param dblNormRadius Radius Norm of the Unit Ball
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public R1ContinuousBall (
		final double dblLeftEdge,
		final double dblRightEdge,
		final org.drip.measure.continuous.R1 distR1,
		final int iPNorm,
		final double dblNormRadius)
		throws java.lang.Exception
	{
		super (dblLeftEdge, dblRightEdge, distR1, iPNorm);

		if (!org.drip.quant.common.NumberUtil.IsValid (_dblNormRadius = dblNormRadius) || 0. >=
			_dblNormRadius)
			throw new java.lang.Exception ("R1ContinuousBall Constructor: Invalid Inputs");
	}

	/**
	 * Retrieve the Radius Norm
	 * 
	 * @return The Radius Norm
	 */

	public double normRadius()
	{
		return _dblNormRadius;
	}

	@Override public boolean validateInstance (
		final double dblInstance)
	{
		try {
			return super.validateInstance (dblInstance) && _dblNormRadius <= sampleMetricNorm (dblInstance);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
