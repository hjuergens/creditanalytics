
package org.drip.learning.regularization;

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
 * RegularizerRdContinuousToR1Continuous computes the Structural Loss and Risk for the specified Normed R^d
 * 	Continuous -> Normed R^1 Continuous Learning Function.
 *  
 * The References are:
 *  
 *  1) Alon, N., S. Ben-David, N. Cesa Bianchi, and D. Haussler (1997): Scale-sensitive Dimensions, Uniform
 *  	Convergence, and Learnability, Journal of Association of Computational Machinery, 44 (4) 615-631.
 * 
 *  2) Anthony, M., and P. L. Bartlett (1999): Artificial Neural Network Learning - Theoretical Foundations,
 *  	Cambridge University Press, Cambridge, UK.
 *  
 *  3) Kearns, M. J., R. E. Schapire, and L. M. Sellie (1994): Towards Efficient Agnostic Learning, Machine
 *  	Learning, 17 (2) 115-141.
 *  
 *  4) Lee, W. S., P. L. Bartlett, and R. C. Williamson (1998): The Importance of Convexity in Learning with
 *  	Squared Loss, IEEE Transactions on Information Theory, 44 1974-1980.
 * 
 *  5) Vapnik, V. N. (1998): Statistical Learning Theory, Wiley, New York.
 *
 * @author Lakshmi Krishnamurthy
 */

public class RegularizerRdContinuousToR1Continuous extends
	org.drip.spaces.RxToR1.NormedRdContinuousToR1Continuous implements
		org.drip.learning.regularization.RegularizerRdToR1 {
	private double _dblLambda = java.lang.Double.NaN;

	/**
	 * RegularizerRdContinuousToR1Continuous Function Space Constructor
	 * 
	 * @param funcRdToR1 The R^d -> R^1 Function
	 * @param rdContinuousInput The Continuous R^d Input Metric Vector Space
	 * @param r1ContinuousOutput The Continuous R^1 Output Metric Vector Space
	 * @param dblLambda The Regularization Lambda
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public RegularizerRdContinuousToR1Continuous (
		final org.drip.function.definition.RdToR1 funcRdToR1,
		final org.drip.spaces.metric.RdContinuousBanach rdContinuousInput,
		final org.drip.spaces.metric.R1Continuous r1ContinuousOutput,
		final double dblLambda)
		throws java.lang.Exception
	{
		super (rdContinuousInput, r1ContinuousOutput, funcRdToR1);

		if (!org.drip.quant.common.NumberUtil.IsValid (_dblLambda = dblLambda) || 0 > _dblLambda)
			throw new java.lang.Exception
				("RegularizerRdContinuousToR1Continuous Constructor => Invalid Inputs");
	}

	@Override public double lambda()
	{
		return _dblLambda;
	}

	@Override public double structuralLoss (
		final org.drip.function.definition.RdToR1 funcRdToR1,
		final double[][] aadblX)
		throws java.lang.Exception
	{
		if (null == funcRdToR1 || null == aadblX)
			throw new java.lang.Exception
				("RegularizerRdContinuousToR1Continuous::structuralLoss => Invalid Inputs");

		double dblLoss = 0.;
		int iNumSample = aadblX.length;

		if (0 == iNumSample)
			throw new java.lang.Exception
				("RegularizerRdContinuousToR1Continuous::structuralLoss => Invalid Inputs");

		int iPNorm = outputMetricVectorSpace().pNorm();

		org.drip.function.definition.RdToR1 funcRegularizerRdToR1 = function();

		if (java.lang.Integer.MAX_VALUE == iPNorm) {
			double dblSupremum = 0.;

			for (int i = 0; i < iNumSample; ++i) {
				double dblRegularizedValue = java.lang.Math.abs (funcRegularizerRdToR1.evaluate (aadblX[i]) *
					funcRdToR1.evaluate (aadblX[i]));

				if (dblSupremum < dblRegularizedValue) dblSupremum = dblRegularizedValue;
			}

			return dblSupremum;
		}

		for (int i = 0; i < iNumSample; ++i)
			dblLoss += java.lang.Math.pow (java.lang.Math.abs (funcRegularizerRdToR1.evaluate (aadblX[i]) *
				funcRdToR1.evaluate (aadblX[i])), iPNorm);

		return dblLoss / iPNorm;
	}

	@Override public double structuralRisk (
		final org.drip.measure.continuous.RdR1 distRdR1,
		final org.drip.function.definition.RdToR1 funcRdToR1,
		final double[][] aadblX,
		final double[] adblY)
		throws java.lang.Exception
	{
		if (null == funcRdToR1 || null == aadblX || null == adblY)
			throw new java.lang.Exception
				("RegularizerRdContinuousToR1Continuous::structuralRisk => Invalid Inputs");

		double dblLoss = 0.;
		double dblNormalizer = 0.;
		int iNumSample = aadblX.length;

		if (0 == iNumSample || iNumSample != adblY.length)
			throw new java.lang.Exception
				("RegularizerRdContinuousToR1Continuous::structuralRisk => Invalid Inputs");

		int iPNorm = outputMetricVectorSpace().pNorm();

		org.drip.function.definition.RdToR1 funcRegularizerRdToR1 = function();

		if (java.lang.Integer.MAX_VALUE == iPNorm) {
			double dblSupremumDensity = 0.;
			double dblSupremumNodeValue = 0.;

			for (int i = 0; i < iNumSample; ++i) {
				double dblDensity = distRdR1.density (aadblX[i], adblY[i]);

				if (dblDensity > dblSupremumDensity) {
					dblSupremumDensity = dblDensity;

					dblSupremumNodeValue = java.lang.Math.abs (funcRegularizerRdToR1.evaluate (aadblX[i]) *
						funcRdToR1.evaluate (aadblX[i]));
				}
			}

			return dblSupremumNodeValue;
		}

		for (int i = 0; i < iNumSample; ++i) {
			double dblDensity = distRdR1.density (aadblX[i], adblY[i]);

			dblNormalizer += dblDensity;

			dblLoss += dblDensity * java.lang.Math.pow (java.lang.Math.abs (funcRegularizerRdToR1.evaluate
				(aadblX[i]) * funcRdToR1.evaluate (aadblX[i])), iPNorm);
		}

		return dblLoss / iPNorm / dblNormalizer;
	}
}
