
package org.drip.learning.RxToR1;

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
 * L1LossLearner implements the Learner Class that holds the Space of Normed R^x -> Normed R^1 Learning
 * 	Functions that employs L1 Empirical Loss Routine. Class-Specific Asymptotic Sample, Covering Number based
 *  Upper Probability Bounds and other Parameters are also maintained.
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

public class L1LossLearner extends org.drip.learning.RxToR1.GeneralizedLearner {
	private org.drip.learning.bound.MeasureConcentrationExpectationBound _cleb = null;

	/**
	 * L1LossLearner Constructor
	 * 
	 * @param funcClassRxToR1 R^x -> R^1 Function Class
	 * @param cdpb The Covering Number based Deviation Upper Probability Bound Generator
	 * @param regularizerFunc The Regularizer Function
	 * @param cleb The Concentration of Measure based Loss Expectation Upper Bound Evaluator
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public L1LossLearner (
		final org.drip.spaces.functionclass.NormedRxToNormedR1Finite funcClassRxToR1,
		final org.drip.learning.bound.CoveringNumberLossBound cdpb,
		final org.drip.learning.regularization.RegularizationFunction regularizerFunc,
		final org.drip.learning.bound.MeasureConcentrationExpectationBound cleb)
		throws java.lang.Exception
	{
		super (funcClassRxToR1, cdpb, regularizerFunc);

		if (null == (_cleb = cleb)) throw new java.lang.Exception ("L1LossLearner ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the Concentration of Measure based Loss Expectation Upper Bound Evaluator Instance
	 * 
	 * @return The Concentration of Measure based Loss Expectation Upper Bound Evaluator Instance
	 */

	public org.drip.learning.bound.MeasureConcentrationExpectationBound concentrationLossBoundEvaluator()
	{
		return _cleb;
	}

	@Override public double lossSampleCoveringNumber (
		final org.drip.spaces.instance.GeneralizedValidatedVector gvvi,
		final double dblEpsilon,
		final boolean bSupremum)
		throws java.lang.Exception
	{
		org.drip.spaces.functionclass.NormedRxToNormedR1Finite funcClassRxToR1 = functionClass();

		return bSupremum ? funcClassRxToR1.sampleSupremumCoveringNumber (gvvi, dblEpsilon) :
			funcClassRxToR1.sampleCoveringNumber (gvvi, dblEpsilon);
	}

	@Override public double empiricalLoss (
		final org.drip.function.definition.R1ToR1 funcLearnerR1ToR1,
		final org.drip.spaces.instance.GeneralizedValidatedVector gvviX,
		final org.drip.spaces.instance.GeneralizedValidatedVector gvviY)
		throws java.lang.Exception
	{
		if (null == funcLearnerR1ToR1 || null == gvviX || !(gvviX instanceof
			org.drip.spaces.instance.ValidatedR1) || null == gvviY || !(gvviY instanceof
				org.drip.spaces.instance.ValidatedR1))
			throw new java.lang.Exception ("L1LossLearner::empiricalLoss => Invalid Inputs");

		double[] adblX = ((org.drip.spaces.instance.ValidatedR1) gvviX).instance();

		double[] adblY = ((org.drip.spaces.instance.ValidatedR1) gvviY).instance();

		double dblEmpiricalLoss = 0.;
		int iNumSample = adblX.length;

		if (iNumSample != adblY.length)
			throw new java.lang.Exception ("L1LossLearner::empiricalLoss => Invalid Inputs");

		for (int i = 0; i < iNumSample; ++i)
			dblEmpiricalLoss += java.lang.Math.abs (funcLearnerR1ToR1.evaluate (adblX[i]) - adblY[i]);

		return dblEmpiricalLoss;
	}

	@Override public double empiricalLoss (
		final org.drip.function.definition.RdToR1 funcLearnerRdToR1,
		final org.drip.spaces.instance.GeneralizedValidatedVector gvviX,
		final org.drip.spaces.instance.GeneralizedValidatedVector gvviY)
		throws java.lang.Exception
	{
		if (null == funcLearnerRdToR1 || null == gvviX || !(gvviX instanceof
			org.drip.spaces.instance.ValidatedRd) || null == gvviY || !(gvviY instanceof
				org.drip.spaces.instance.ValidatedR1))
			throw new java.lang.Exception ("L1LossLearner::empiricalLoss => Invalid Inputs");

		double[][] aadblX = ((org.drip.spaces.instance.ValidatedRd) gvviX).instance();

		double[] adblY = ((org.drip.spaces.instance.ValidatedR1) gvviY).instance();

		double dblEmpiricalLoss = 0.;
		int iNumSample = aadblX.length;

		if (iNumSample != adblY.length)
			throw new java.lang.Exception ("L1LossLearner::empiricalLoss => Invalid Inputs");

		for (int i = 0; i < iNumSample; ++i)
			dblEmpiricalLoss += java.lang.Math.abs (funcLearnerRdToR1.evaluate (aadblX[i]) - adblY[i]);

		return dblEmpiricalLoss;
	}

	@Override public double empiricalRisk (
		final org.drip.measure.continuous.R1R1 distR1R1,
		final org.drip.function.definition.R1ToR1 funcLearnerR1ToR1,
		final org.drip.spaces.instance.GeneralizedValidatedVector gvviX,
		final org.drip.spaces.instance.GeneralizedValidatedVector gvviY)
		throws java.lang.Exception
	{
		if (null == distR1R1 || null == funcLearnerR1ToR1 || null == gvviX || !(gvviX instanceof
			org.drip.spaces.instance.ValidatedR1) || null == gvviY || !(gvviY instanceof
				org.drip.spaces.instance.ValidatedR1))
			throw new java.lang.Exception ("L1LossLearner::empiricalRisk => Invalid Inputs");

		double[] adblX = ((org.drip.spaces.instance.ValidatedR1) gvviX).instance();

		double[] adblY = ((org.drip.spaces.instance.ValidatedR1) gvviY).instance();

		double dblNormalizer = 0.;
		double dblEmpiricalLoss = 0.;
		int iNumSample = adblX.length;

		if (iNumSample != adblY.length)
			throw new java.lang.Exception ("L1LossLearner::empiricalRisk => Invalid Inputs");

		for (int i = 0; i < iNumSample; ++i) {
			double dblDensity = distR1R1.density (adblX[i], adblY[i]);

			dblNormalizer += dblDensity;

			dblEmpiricalLoss += dblDensity * java.lang.Math.abs (funcLearnerR1ToR1.evaluate (adblX[i]) -
				adblY[i]);
		}

		return dblEmpiricalLoss / dblNormalizer;
	}

	@Override public double empiricalRisk (
		final org.drip.measure.continuous.RdR1 distRdR1,
		final org.drip.function.definition.RdToR1 funcLearnerRdToR1,
		final org.drip.spaces.instance.GeneralizedValidatedVector gvviX,
		final org.drip.spaces.instance.GeneralizedValidatedVector gvviY)
		throws java.lang.Exception
	{
		if (null == distRdR1 || null == funcLearnerRdToR1 || null == gvviX || !(gvviX instanceof
			org.drip.spaces.instance.ValidatedRd) || null == gvviY || !(gvviY instanceof
				org.drip.spaces.instance.ValidatedR1))
			throw new java.lang.Exception ("L1LossLearner::empiricalRisk => Invalid Inputs");

		double[][] aadblX = ((org.drip.spaces.instance.ValidatedRd) gvviX).instance();

		double[] adblY = ((org.drip.spaces.instance.ValidatedR1) gvviY).instance();

		double dblNormalizer = 0.;
		double dblEmpiricalLoss = 0.;
		int iNumSample = aadblX.length;

		if (iNumSample != adblY.length)
			throw new java.lang.Exception ("L1LossLearner::empiricalRisk => Invalid Inputs");

		for (int i = 0; i < iNumSample; ++i) {
			double dblDensity = distRdR1.density (aadblX[i], adblY[i]);

			dblNormalizer += dblDensity;

			dblEmpiricalLoss += dblDensity * java.lang.Math.abs (funcLearnerRdToR1.evaluate (aadblX[i]) -
				adblY[i]);
		}

		return dblEmpiricalLoss / dblNormalizer;
	}
}
