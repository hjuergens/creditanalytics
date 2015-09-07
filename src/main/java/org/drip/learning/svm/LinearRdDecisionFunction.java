
package org.drip.learning.svm;

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
 * LinearRdDecisionFunction implements the Linear R^d Decision Function-Based SVM Functionality for
 *  Classification and Regression.
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class LinearRdDecisionFunction extends org.drip.learning.svm.RdDecisionFunction {

	/**
	 * LinearRdDecisionFunction Constructor
	 * 
	 * @param gmvsPredictor The R^d Metric Input Predictor Space
	 * @param rmnsInverseMargin The Inverse Margin Weights R^d L2 Space
	 * @param adblInverseMarginWeight Array of Inverse Margin Weights
	 * @param dblB The Offset
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public LinearRdDecisionFunction (
		final org.drip.spaces.tensor.RdGeneralizedVector gmvsPredictor,
		final org.drip.spaces.metric.RdNormed rmnsInverseMargin,
		final double[] adblInverseMarginWeight,
		final double dblB)
		throws java.lang.Exception
	{
		super (gmvsPredictor, rmnsInverseMargin, adblInverseMarginWeight, dblB);
	}

	@Override public double evaluate (
		final double[] adblX)
		throws java.lang.Exception
	{
		if (!predictorSpace().validateInstance (adblX))
			throw new java.lang.Exception ("LinearRdDecisionFunction::evaluate => Invalid Inputs");

		double dblDotProduct = 0.;
		int iDimension = adblX.length;

		double[] adblInverseMarginWeight = inverseMarginWeights();

		for (int i = 0; i < iDimension; ++i)
			dblDotProduct += adblInverseMarginWeight[i] * adblX[i];

		return dblDotProduct + offset();
	}
}
