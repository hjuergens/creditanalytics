
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
 * NormedRxToNormedR1 is the Abstract Class that exposes f : Normed R^x (x >= 1) -> Normed R^1 Function
 *  Space.
 * 
 * The Reference we've used is:
 * 
 * 	- Carl, B., and I. Stephani (1990): Entropy, Compactness, and Approximation of Operators, Cambridge
 * 		University Press, Cambridge UK.
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class NormedRxToNormedR1 {

	/**
	 * Retrieve the Input Metric Vector Space
	 * 
	 * @return The Input Metric Vector Space
	 */

	public abstract org.drip.spaces.metric.GeneralizedMetricVectorSpace inputMetricVectorSpace();

	/**
	 * Retrieve the Output Metric Vector Space
	 * 
	 * @return The Output Metric Vector Space
	 */

	public abstract org.drip.spaces.metric.R1Normed outputMetricVectorSpace();

	/**
	 * Retrieve the Sample Supremum Norm
	 * 
	 * @param gvvi The Validated Vector Space Instance
	 * 
	 * @return The Sample Supremum Norm
	 * 
	 * @throws java.lang.Exception Thrown if the Supremum Norm cannot be computed
	 */

	public abstract double sampleSupremumNorm (
		final org.drip.spaces.instance.GeneralizedValidatedVector gvvi)
		throws java.lang.Exception;

	/**
	 * Retrieve the Sample Metric Norm
	 * 
	 * @param gvvi The Validated Vector Space Instance
	 * 
	 * @return The Sample Metric Norm
	 * 
	 * @throws java.lang.Exception Thrown if the Sample Metric Norm cannot be computed
	 */

	public abstract double sampleMetricNorm (
		final org.drip.spaces.instance.GeneralizedValidatedVector gvvi)
		throws java.lang.Exception;

	/**
	 * Retrieve the Sample Covering Number
	 * 
	 * @param gvvi The Validated Vector Space Instance
	 * @param dblCover The Cover
	 * 
	 * @return The Sample Covering Number
	 * 
	 * @throws java.lang.Exception Thrown if the Sample Covering Number cannot be computed
	 */

	public double sampleCoveringNumber (
		final org.drip.spaces.instance.GeneralizedValidatedVector gvvi,
		final double dblCover)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblCover) || 0. >= dblCover)
			throw new java.lang.Exception ("NormedRxToNormedR1::sampleCoveringNumber => Invalid Inputs");

		return sampleMetricNorm (gvvi) / dblCover;
	}

	/**
	 * Retrieve the Sample Supremum Covering Number
	 * 
	 * @param gvvi The Validated Vector Space Instance
	 * @param dblCover The Cover
	 * 
	 * @return The Sample Supremum Covering Number
	 * 
	 * @throws java.lang.Exception Thrown if the Sample Covering Number cannot be computed
	 */

	public double sampleSupremumCoveringNumber (
		final org.drip.spaces.instance.GeneralizedValidatedVector gvvi,
		final double dblCover)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblCover) || 0. >= dblCover)
			throw new java.lang.Exception
				("NormedRxToNormedR1::sampleSupremumCoveringNumber => Invalid Inputs");

		return sampleSupremumNorm (gvvi) / dblCover;
	}

	/**
	 * Retrieve the Population ESS (Essential Spectrum)
	 * 
	 * @return The Population ESS (Essential Spectrum)
	 * 
	 * @throws java.lang.Exception Thrown if the Population ESS (Essential Spectrum) cannot be computed
	 */

	public abstract double populationESS()
		throws java.lang.Exception;

	/**
	 * Retrieve the Population Metric Norm
	 * 
	 * @return The Population Metric Norm
	 * 
	 * @throws java.lang.Exception Thrown if the Population Metric Norm cannot be computed
	 */

	public abstract double populationMetricNorm()
		throws java.lang.Exception;

	/**
	 * Retrieve the Population Supremum Metric Norm
	 * 
	 * @return The Population Supremum Metric Norm
	 * 
	 * @throws java.lang.Exception Thrown if the Population Supremum Metric Norm cannot be computed
	 */

	public double populationSupremumMetricNorm()
		throws java.lang.Exception
	{
		return populationESS();
	}

	/**
	 * Retrieve the Population Covering Number
	 * 
	 * @param dblCover The Cover
	 * 
	 * @return The Population Covering Number
	 * 
	 * @throws java.lang.Exception Thrown if the Population Covering Number cannot be computed
	 */

	public double populationCoveringNumber (
		final double dblCover)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblCover) || 0. >= dblCover)
			throw new java.lang.Exception ("NormedRxToNormedR1::populationCoveringNumber => Invalid Inputs");

		return populationMetricNorm() / dblCover;
	}

	/**
	 * Retrieve the Population Supremum Covering Number
	 * 
	 * @param dblCover The Cover
	 * 
	 * @return The Population Supremum Covering Number
	 * 
	 * @throws java.lang.Exception Thrown if the Population Supremum Covering Number cannot be computed
	 */

	public double populationSupremumCoveringNumber (
		final double dblCover)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblCover) || 0. >= dblCover)
			throw new java.lang.Exception
				("NormedRxToNormedR1::populationSupremumCoveringNumber => Invalid Inputs");

		return populationSupremumMetricNorm() / dblCover;
	}
}
