
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
 * EmpiricalPenaltySupremumMetrics computes Efron-Stein Metrics for the Penalty Supremum R^x -> R^1
 *  Functions.
 *
 * @author Lakshmi Krishnamurthy
 */

public class EmpiricalPenaltySupremumMetrics extends org.drip.sequence.functional.EfronSteinMetrics {
	private org.drip.learning.RxToR1.EmpiricalPenaltySupremumEstimator _epse = null;
	private org.drip.learning.bound.MeasureConcentrationExpectationBound _mceb = null;

	/**
	 * EmpiricalPenaltySupremumMetrics Constructor
	 * 
	 * @param epse R^x -> R^1 The Empirical Penalty Supremum Estimator Instance
	 * @param aSSAM Array of the Individual Single Sequence Metrics
	 * @param mceb The Concentration-of-Measure Loss Expectation Bound Estimator
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public EmpiricalPenaltySupremumMetrics (
		final org.drip.learning.RxToR1.EmpiricalPenaltySupremumEstimator epse,
		final org.drip.sequence.metrics.SingleSequenceAgnosticMetrics[] aSSAM,
		final org.drip.learning.bound.MeasureConcentrationExpectationBound mceb)
		throws java.lang.Exception
	{
		super (epse, aSSAM);

		if (null == (_epse = epse) || null == (_mceb = mceb))
			throw new java.lang.Exception ("EmpiricalPenaltySupremumMetrics ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the Empirical Penalty Supremum Function
	 * 
	 * @return The Empirical Penalty Supremum Function
	 */

	public org.drip.learning.RxToR1.EmpiricalPenaltySupremumEstimator empiricalPenaltySupremumEstimator()
	{
		return _epse;
	}

	/**
	 * Retrieve the Univariate Sequence Dependent Variance Bound
	 * 
	 * @param adblVariate The univariate Sequence
	 * 
	 * @return The Univariate Sequence Dependent Variance Bound
	 * 
	 * @throws java.lang.Exception Thrown if the Date Dependent Variance Bound cannot be Computed
	 */

	public double dataDependentVarianceBound (
		final double[] adblVariate)
		throws java.lang.Exception
	{
		return _epse.evaluate (adblVariate) / adblVariate.length;
	}

	/**
	 * Retrieve the Multivariate Sequence Dependent Variance Bound
	 * 
	 * @param aadblVariate The Multivariate Sequence
	 * 
	 * @return The Multivariate Sequence Dependent Variance Bound
	 * 
	 * @throws java.lang.Exception Thrown if the Date Dependent Variance Bound cannot be Computed
	 */

	public double dataDependentVarianceBound (
		final double[][] aadblVariate)
		throws java.lang.Exception
	{
		return _epse.evaluate (aadblVariate) / aadblVariate.length;
	}

	/**
	 * Compute the Lugosi Data-Dependent Variance Bound from the Sample and the Classifier Class Asymptotic
	 * 	Behavior: Source =>
	 * 
	 * 		G. Lugosi (2002): Pattern Classification and Learning Theory, in: L.Gyorfi, editor, Principles of
	 * 			Non-parametric Learning, 5-62, Springer, Wien.
	 * 
	 * @param adblVariate The Sample Univariate Array
	 * 
	 * @return The Lugosi Data-Dependent Variance Bound
	 * 
	 * @throws java.lang.Exception Thrown if the Lugosi Data-Dependent Variance Bound cannot be computed
	 */

	public double lugosiVarianceBound (
		final double[] adblVariate)
		throws java.lang.Exception
	{
		org.drip.function.definition.R1ToR1 supR1ToR1 = _epse.supremumR1ToR1 (adblVariate);

		if (null == supR1ToR1)
			throw new java.lang.Exception
				("EmpiricalPenaltySupremumMetrics::lugosiVarianceBound => Cannot Find Supremum Classifier");

		return dataDependentVarianceBound (adblVariate) + _mceb.constant() + java.lang.Math.pow
			(adblVariate.length, _mceb.exponent());
	}

	/**
	 * Compute the Lugosi Data-Dependent Variance Bound from the Sample and the Classifier Class Asymptotic
	 * 	Behavior: Source =>
	 * 
	 * 		G. Lugosi (2002): Pattern Classification and Learning Theory, in: L.Gyorfi, editor, Principles of
	 * 			Non-parametric Learning, 5-62, Springer, Wien.
	 * 
	 * @param aadblVariate The Sample Multivariate Array
	 * 
	 * @return The Lugosi Data-Dependent Variance Bound
	 * 
	 * @throws java.lang.Exception Thrown if the Lugosi Data-Dependent Variance Bound cannot be computed
	 */

	public double lugosiVarianceBound (
		final double[][] aadblVariate)
		throws java.lang.Exception
	{
		org.drip.function.definition.RdToR1 supRdToR1 = _epse.supremumRdToR1 (aadblVariate);

		if (null == supRdToR1)
			throw new java.lang.Exception
				("EmpiricalPenaltySupremumMetrics::lugosiVarianceBound => Cannot Find Supremum Classifier");

		return dataDependentVarianceBound (aadblVariate) + _mceb.constant() + java.lang.Math.pow
			(aadblVariate.length, _mceb.exponent());
	}
}
