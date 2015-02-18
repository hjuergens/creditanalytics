
package org.drip.quant.randomsequence;

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
 * UnitSequenceAgnosticMetrics contains the Sample Distribution Metrics and Agnostic Bounds related to the
 *  specified Bounded [0, 1] Sequence.
 *
 * @author Lakshmi Krishnamurthy
 */

public class UnitSequenceAgnosticMetrics extends org.drip.quant.randomsequence.BoundedSequenceAgnosticMetrics
{
	private double _dblPopulationMean = java.lang.Double.NaN;

	/**
	 * UnitSequenceAgnosticMetrics Constructor
	 * 
	 * @param adblSequence The Random Sequence
	 * @param dblPopulationMean The Mean of the Underlying Distribution
	 * 
	 * @throws java.lang.Exception Thrown if UnitSequenceAgnosticMetrics cannot be constructed
	 */

	public UnitSequenceAgnosticMetrics (
		final double[] adblSequence,
		final double dblPopulationMean)
		throws java.lang.Exception
	{
		super (adblSequence, null, 1.);

		_dblPopulationMean = dblPopulationMean;
	}

	/**
	 * Retrieve the Mean of the Underlying Distribution
	 * 
	 * @return The Mean of the Underlying Distribution
	 */

	public double populationMean()
	{
		return _dblPopulationMean;
	}

	/**
	 * Compute the Chernoff Binomial Upper Bound
	 * 
	 * @param dblLevel The Level at which the Bound is sought
	 * 
	 * @return The Chernoff Binomial Upper Bound
	 * 
	 * @throws java.lang.Exception Thrown if the Chernoff Binomial Upper Bound cannot be computed
	 */

	public double chernoffBinomialUpperBound (
		final double dblLevel)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblLevel) || 1. < dblLevel)
			throw new java.lang.Exception
				("UnitSequenceAgnosticMetrics::chernoffBinomialUpperBound => Invalid Inputs");

		int iNumEntry = sequence().length;

		double dblPopulationMean = org.drip.quant.common.NumberUtil.IsValid (_dblPopulationMean) ?
			_dblPopulationMean : empiricalExpectation();

		double dblBound = java.lang.Math.pow (dblPopulationMean / dblLevel, iNumEntry * dblLevel) *
			java.lang.Math.pow ((1. - dblPopulationMean) / (1. - dblLevel), iNumEntry * (1. - dblLevel));

		if (!org.drip.quant.common.NumberUtil.IsValid (dblBound)) return 0.;

		return dblBound > 1. ? 1. : dblBound;
	}

	/**
	 * Compute the Chernoff-Poisson Binomial Upper Bound
	 * 
	 * @param dblLevel The Level at which the Bound is sought
	 * 
	 * @return The Chernoff-Poisson Binomial Upper Bound
	 * 
	 * @throws java.lang.Exception Thrown if the Chernoff-Poisson Binomial Upper Bound cannot be computed
	 */

	public double chernoffPoissonUpperBound (
		final double dblLevel)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblLevel) || 1. < dblLevel)
			throw new java.lang.Exception
				("UnitSequenceAgnosticMetrics::exponentialChernoffBinomialUpperBound => Invalid Inputs");

		int iNumEntry = sequence().length;

		double dblPopulationMean = org.drip.quant.common.NumberUtil.IsValid (_dblPopulationMean) ?
			_dblPopulationMean : empiricalExpectation();

		double dblBound = java.lang.Math.pow (dblPopulationMean / dblLevel, iNumEntry * dblLevel) *
			java.lang.Math.exp (iNumEntry * (dblLevel - dblPopulationMean));

		if (!org.drip.quant.common.NumberUtil.IsValid (dblBound)) return 0.;

		return dblBound > 1. ? 1. : dblBound;
	}
}
