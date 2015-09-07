
package org.drip.sequence.metrics;

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
 * SingleSequenceAgnosticMetrics contains the Sample Distribution Metrics and Agnostic Bounds related to the
 *  specified Sequence.
 *
 * @author Lakshmi Krishnamurthy
 */

public class SingleSequenceAgnosticMetrics {
	private boolean _bIsPositive = true;
	private double[] _adblSequence = null;
	private double _dblEmpiricalVariance = java.lang.Double.NaN;
	private double _dblEmpiricalExpectation = java.lang.Double.NaN;
	private org.drip.measure.continuous.R1 _distPopulation = null;

	/**
	 * Build out the Sequence and their Metrics
	 * 
	 * @param adblSequence Array of Sequence Entries
	 * @param distPopulation The True Underlying Generator Distribution of the Population
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public SingleSequenceAgnosticMetrics (
		final double[] adblSequence,
		final org.drip.measure.continuous.R1 distPopulation)
		throws java.lang.Exception
	{
		if (null == (_adblSequence = adblSequence))
			throw new java.lang.Exception ("SingleSequenceAgnosticMetrics ctr: Invalid Inputs");

		_dblEmpiricalExpectation = 0.;
		_distPopulation = distPopulation;
		int iNumEntry = _adblSequence.length;

		if (0 == iNumEntry)
			throw new java.lang.Exception ("SingleSequenceAgnosticMetrics ctr: Invalid Inputs");

		for (int i = 0; i < iNumEntry; ++i) {
			if (!org.drip.quant.common.NumberUtil.IsValid (_adblSequence[i]))
				throw new java.lang.Exception ("SingleSequenceAgnosticMetrics ctr: Invalid Inputs");

			_dblEmpiricalExpectation += _adblSequence[i];

			if (_adblSequence[i] < 0.) _bIsPositive = false;
		}

		_dblEmpiricalVariance = 0.;
		_dblEmpiricalExpectation /= iNumEntry;

		for (int i = 0; i < iNumEntry; ++i)
			_dblEmpiricalVariance += (_adblSequence[i] - _dblEmpiricalExpectation) * (_adblSequence[i] -
				_dblEmpiricalExpectation);

		_dblEmpiricalVariance /= iNumEntry;
	}

	/**
	 * Compute the Specified Central Moment of the Sample Sequence
	 * 
	 * @param iMoment The Moment
	 * @param bAbsolute TRUE => The Moment sought is on the Absolute Value
	 * 
	 * @return The Specified Central Moment of the Sample Sequence
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public double empiricalCentralMoment (
		final int iMoment,
		final boolean bAbsolute)
		throws java.lang.Exception
	{
		if (0 >= iMoment)
			throw new java.lang.Exception
				("SingleSequenceAgnosticMetrics::empiricalCentralMoment => Invalid Moment");

		double dblMoment = 0.;
		int iNumEntry = _adblSequence.length;

		for (int i = 0; i < iNumEntry; ++i) {
			double dblDeparture = _adblSequence[i] - _dblEmpiricalExpectation;

			dblMoment += java.lang.Math.pow (bAbsolute ? java.lang.Math.abs (dblDeparture) : dblDeparture,
				iMoment);
		}

		return dblMoment / iNumEntry;
	}

	/**
	 * Compute the Specified Raw Moment of the Sample Sequence
	 * 
	 * @param iMoment The Moment
	 * @param bAbsolute TRUE => The Moment sought is on the Absolute Value
	 * 
	 * @return The Specified Raw Moment of the Sample Sequence
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public double empiricalRawMoment (
		final int iMoment,
		final boolean bAbsolute)
		throws java.lang.Exception
	{
		if (0 >= iMoment)
			throw new java.lang.Exception
				("SingleSequenceAgnosticMetrics::empiricalRawMoment => Invalid Moment");

		double dblMoment = 0.;
		int iNumEntry = _adblSequence.length;

		for (int i = 0; i < iNumEntry; ++i)
			dblMoment += java.lang.Math.pow (bAbsolute ? java.lang.Math.abs (_adblSequence[i]) :
				_adblSequence[i], iMoment);

		return dblMoment / iNumEntry;
	}

	/**
	 * Compute the Specified Anchor Moment of the Sample Sequence
	 * 
	 * @param iMoment The Moment
	 * @param dblAnchor The Anchor Pivot off of which the Moment is calculated
	 * @param bAbsolute TRUE => The Moment sought is on the Absolute Value
	 * 
	 * @return The Specified Anchor Moment of the Sample Sequence
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public double empiricalAnchorMoment (
		final int iMoment,
		final double dblAnchor,
		final boolean bAbsolute)
		throws java.lang.Exception
	{
		if (0 >= iMoment || !org.drip.quant.common.NumberUtil.IsValid (dblAnchor))
			throw new java.lang.Exception
				("SingleSequenceAgnosticMetrics::empiricalAnchorMoment => Invalid Inputs");

		double dblMoment = 0.;
		int iNumEntry = _adblSequence.length;

		for (int i = 0; i < iNumEntry; ++i) {
			double dblPivotShift = _adblSequence[i] - dblAnchor;

			dblMoment += java.lang.Math.pow (bAbsolute ? java.lang.Math.abs (dblPivotShift) : dblPivotShift,
				iMoment);
		}

		return dblMoment / iNumEntry;
	}

	/**
	 * Generate the Metrics for the Univariate Function Sequence
	 *  
	 * @param au The Univariate Function
	 * 
	 * @return Metrics for the Univariate Function Sequence
	 */

	public SingleSequenceAgnosticMetrics functionSequenceMetrics (
		final org.drip.function.definition.R1ToR1 au)
	{
		if (null == au) return null;

		int iNumEntry = _adblSequence.length;
		double[] adblFunctionMetrics = new double[iNumEntry];

		try {
			for (int i = 0; i < iNumEntry; ++i)
				adblFunctionMetrics[i] = au.evaluate (_adblSequence[i]);

			return new SingleSequenceAgnosticMetrics (adblFunctionMetrics, null);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve the Population Distribution
	 * 
	 * @return The Population Distribution
	 */

	public org.drip.measure.continuous.R1 populationDistribution()
	{
		return _distPopulation;
	}

	/**
	 * Retrieve the Sample Expectation
	 * 
	 * @return The Sample Expectation
	 */

	public double empiricalExpectation()
	{
		return _dblEmpiricalExpectation;
	}

	/**
	 * Retrieve the Population Mean
	 * 
	 * @return The Population Mean
	 */

	public double populationMean()
	{
		return null == _distPopulation ? java.lang.Double.NaN : _distPopulation.mean();
	}

	/**
	 * Retrieve the Sample Variance
	 * 
	 * @return The Sample Variance
	 */

	public double empiricalVariance()
	{
		return _dblEmpiricalVariance;
	}

	/**
	 * Retrieve the Population Variance
	 * 
	 * @return The Population Variance
	 */

	public double populationVariance()
	{
		return null == _distPopulation ? java.lang.Double.NaN : _distPopulation.variance();
	}

	/**
	 * Retrieve the Sequence Positiveness Flag
	 * 
	 * @return TRUE => The Sequence is Positiveness
	 */

	public boolean isPositive()
	{
		return _bIsPositive;
	}

	/**
	 * Retrieve the Input Sequence
	 * 
	 * @return The Input Sequence
	 */

	public double[] sequence()
	{
		return _adblSequence;
	}

	/**
	 * Retrieve the Markov Upper Limiting Probability Bound for the Specified Level:
	 * 	- P (X >= t) <= E[f(X)] / f(t)
	 * 
	 * @param dblLevel The Specified Level
	 * @param auNonDecreasing The Non-decreasing Bounding Transformer Function
	 * 
	 * @return The Markov Upper Limiting Probability Bound for the Specified Level
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public double markovUpperProbabilityBound (
		final double dblLevel,
		final org.drip.function.definition.R1ToR1 auNonDecreasing)
		throws java.lang.Exception
	{
		if (!isPositive() || !org.drip.quant.common.NumberUtil.IsValid (dblLevel) || dblLevel <= 0.)
			throw new java.lang.Exception
				("SingleSequenceAgnosticMetrics::markovUpperProbabilityBound => Invalid Inputs");

		double dblPopulationMean = populationMean();

		double dblUpperProbabilityBound = (org.drip.quant.common.NumberUtil.IsValid (dblPopulationMean) ?
			dblPopulationMean : _dblEmpiricalExpectation) / dblLevel;

		if (null != auNonDecreasing) {
			SingleSequenceAgnosticMetrics smFunction = functionSequenceMetrics (auNonDecreasing);

			if (null == smFunction)
				throw new java.lang.Exception
					("SingleSequenceAgnosticMetrics::markovUpperProbabilityBound => Cannot generate Function Sequence Metrics");

			dblUpperProbabilityBound = smFunction.empiricalExpectation() / auNonDecreasing.evaluate
				(dblLevel);
		}

		return dblUpperProbabilityBound < 1. ? dblUpperProbabilityBound : 1.;
	}

	/**
	 * Retrieve the Mean Departure Bounds Using the Chebyshev's Inequality
	 * 
	 * @param dblLevel The Level at which the Departure is sought
	 * 
	 * @return The Mean Departure Bounds Instance
	 */

	public org.drip.sequence.metrics.PivotedDepartureBounds chebyshevBound (
		final double dblLevel)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblLevel) || dblLevel <= 0.) return null;

		double dblPopulationVariance = populationVariance();

		double dblMeanDepartureBound = (org.drip.quant.common.NumberUtil.IsValid (dblPopulationVariance) ?
			dblPopulationVariance : _dblEmpiricalVariance) / (dblLevel * dblLevel);

		dblMeanDepartureBound = dblMeanDepartureBound < 1. ? dblMeanDepartureBound : 1.;

		try {
			return new org.drip.sequence.metrics.PivotedDepartureBounds
				(org.drip.sequence.metrics.PivotedDepartureBounds.PIVOT_ANCHOR_TYPE_MEAN, java.lang.Double.NaN,
					dblMeanDepartureBound, dblMeanDepartureBound);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve the Mean Departure Bounds Using the Central Moment Bounding Inequality
	 * 
	 * @param dblLevel The Level at which the Departure is sought
	 * @param iMoment The Moment Bound sought
	 * 
	 * @return The Mean Departure Bounds Instance
	 */

	public org.drip.sequence.metrics.PivotedDepartureBounds centralMomentBound (
		final double dblLevel,
		final int iMoment)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblLevel) || dblLevel <= 0.) return null;

		try {
			double dblMeanDepartureBound = empiricalCentralMoment (iMoment, true) / java.lang.Math.pow
				(dblLevel, iMoment);

			dblMeanDepartureBound = dblMeanDepartureBound < 1. ? dblMeanDepartureBound : 1.;

			return new org.drip.sequence.metrics.PivotedDepartureBounds
				(org.drip.sequence.metrics.PivotedDepartureBounds.PIVOT_ANCHOR_TYPE_MEAN, java.lang.Double.NaN,
					dblMeanDepartureBound, dblMeanDepartureBound);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve the Mean Departure Bounds Using the Chebyshev-Cantelli Inequality
	 * 
	 * @param dblLevel The Level at which the Departure is sought
	 * 
	 * @return The Mean Departure Bounds
	 */

	public org.drip.sequence.metrics.PivotedDepartureBounds chebyshevCantelliBound (
		final double dblLevel)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblLevel) || dblLevel <= 0.) return null;

		double dblPopulationVariance = populationVariance();

		double dblVariance = (org.drip.quant.common.NumberUtil.IsValid (dblPopulationVariance) ?
			dblPopulationVariance : _dblEmpiricalVariance);

		try {
			return new org.drip.sequence.metrics.PivotedDepartureBounds
				(org.drip.sequence.metrics.PivotedDepartureBounds.PIVOT_ANCHOR_TYPE_MEAN, java.lang.Double.NaN,
					java.lang.Double.NaN, dblVariance / (dblVariance + dblLevel * dblLevel));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve the Chebyshev's Association Joint Expectation Bound
	 * 
	 * @param au1 Function 1 Operating On Sequence 1
	 * @param bNonDecreasing1 TRUE => Function 1 is non-decreasing
	 * @param au2 Function 2 Operating On Sequence 2
	 * @param bNonDecreasing2 TRUE => Function 2 is non-decreasing
	 * 
	 * @return The Chebyshev's Association Joint Expectation Bound
	 */

	public org.drip.sequence.metrics.PivotedDepartureBounds chebyshevAssociationBound (
		final org.drip.function.definition.R1ToR1 au1,
		final boolean bNonDecreasing1,
		final org.drip.function.definition.R1ToR1 au2,
		final boolean bNonDecreasing2)
	{
		if (null == au1 || null == au2) return null;

		double dblBound = functionSequenceMetrics (au1).empiricalExpectation() * functionSequenceMetrics
			(au2).empiricalExpectation();

		dblBound = dblBound < 1. ? dblBound : 1.;

		if (bNonDecreasing1 == bNonDecreasing2) {
			try {
				return new org.drip.sequence.metrics.PivotedDepartureBounds
					(org.drip.sequence.metrics.PivotedDepartureBounds.PIVOT_ANCHOR_TYPE_CUSTOM, 0.,
						dblBound, java.lang.Double.NaN);
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		try {
			return new org.drip.sequence.metrics.PivotedDepartureBounds
				(org.drip.sequence.metrics.PivotedDepartureBounds.PIVOT_ANCHOR_TYPE_CUSTOM, 0.,
					java.lang.Double.NaN, dblBound);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Estimate Mean Departure Bounds of the Average using the Weak Law of Large Numbers
	 * 
	 * @param dblLevel The Level at which the Departure is sought
	 * 
	 * @return The Mean Departure Bounds
	 */

	public org.drip.sequence.metrics.PivotedDepartureBounds weakLawAverageBounds (
		final double dblLevel)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblLevel) || dblLevel <= 0.) return null;

		double dblPopulationVariance = populationVariance();

		double dblVariance = (org.drip.quant.common.NumberUtil.IsValid (dblPopulationVariance) ?
			dblPopulationVariance : _dblEmpiricalVariance);

		double dblBound = dblVariance / (_adblSequence.length * dblLevel * dblLevel);
		dblBound = dblBound < 1. ? dblBound : 1.;

		try {
			return new org.drip.sequence.metrics.PivotedDepartureBounds
				(org.drip.sequence.metrics.PivotedDepartureBounds.PIVOT_ANCHOR_TYPE_MEAN,
					java.lang.Double.NaN, dblBound, dblBound);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
