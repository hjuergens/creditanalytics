
package org.drip.learning.kernel;

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
 * SymmetricRdToNormedRdKernel exposes the Functionality behind the Kernel that is Normed R^d X Normed R^d ->
 *  Normed R^d, that is, a Kernel that symmetric in the Input Metric Vector Space in terms of both the
 *  Metric and the Dimensionality.
 *  
 *  The References are:
 *  
 *  1) Ash, R. (1965): Information Theory, Inter-science New York.
 *  
 *  2) Konig, H. (1986): Eigenvalue Distribution of Compact Operators, Birkhauser, Basel, Switzerland. 
 *  
 *  3) Smola, A. J., A. Elisseff, B. Scholkopf, and R. C. Williamson (2000): Entropy Numbers for Convex
 *  	Combinations and mlps, in: Advances in Large Margin Classifiers, A. Smola, P. Bartlett, B. Scholkopf,
 *  	and D. Schuurmans - editors, MIT Press, Cambridge, MA.
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class SymmetricRdToNormedRdKernel {
	private org.drip.spaces.metric.RdNormed _rdContinuousInput = null;
	private org.drip.spaces.metric.RdNormed _rdContinuousOutput = null;

	/**
	 * SymmetricRdToNormedRdKernel Constructor
	 * 
	 * @param rdContinuousInput The Symmetric Input R^d Metric Vector Space
	 * @param rdContinuousOutput The Output R^d Metric Vector Space
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public SymmetricRdToNormedRdKernel (
		final org.drip.spaces.metric.RdNormed rdContinuousInput,
		final org.drip.spaces.metric.RdNormed rdContinuousOutput)
		throws java.lang.Exception
	{
		if (null == (_rdContinuousInput = rdContinuousInput) || null == (_rdContinuousOutput =
			rdContinuousOutput))
			throw new java.lang.Exception ("SymmetricRdToNormedR1Kernel ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the Symmetric Input Metric R^d Vector Space
	 * 
	 * @return The Symmetric Input Metric R^d Vector Space
	 */

	public org.drip.spaces.metric.RdNormed inputMetricVectorSpace()
	{
		return _rdContinuousInput;
	}

	/**
	 * Retrieve the Output R^d Metric Vector Space
	 * 
	 * @return The Output R^d Metric Vector Space
	 */

	public org.drip.spaces.metric.RdNormed outputMetricVectorSpace()
	{
		return _rdContinuousOutput;
	}

	/**
	 * Compute the Feature Space Input Dimension
	 * 
	 * @return The Feature Space Input Dimension
	 */

	public int featureSpaceDimension()
	{
		return _rdContinuousOutput.dimension();
	}

	/**
	 * Compute the Kernel's R^d X R^d -> R^1 Dot-Product Value
	 * 
	 * @param adblX Validated Vector Instance X
	 * @param adblY Validated Vector Instance Y
	 * 
	 * @return The Kernel's R^d X R^d -> R^1 Dot-Product Value
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public abstract double evaluate (
		final double[] adblX,
		final double[] adblY)
		throws java.lang.Exception;
}
