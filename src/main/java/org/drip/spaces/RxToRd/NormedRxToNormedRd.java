
package org.drip.spaces.RxToRd;

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
 * NormedRxToNormedRd is the abstract Class that exposes f : Normed R^x (x >= 1) -> Normed R^d Function
 *  Space.
 * 
 * The Reference we've used is:
 * 
 * 	- Carl, B., and I. Stephani (1990): Entropy, Compactness, and Approximation of Operators, Cambridge
 * 		University Press, Cambridge UK.
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class NormedRxToNormedRd {

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

	public abstract org.drip.spaces.metric.RdNormed outputMetricVectorSpace();

	/**
	 * Retrieve the Sample Supremum Norm Array
	 * 
	 * @param gvvi The Validated Vector Space Instance
	 * 
	 * @return The Sample Supremum Norm Array
	 */

	public abstract double[] sampleSupremumNorm (
		final org.drip.spaces.instance.GeneralizedValidatedVector gvvi);

	/**
	 * Retrieve the Sample Metric Norm Array
	 * 
	 * @param gvvi The Validated Vector Space Instance
	 * 
	 * @return The Sample Metric Norm Array
	 */

	public abstract double[] sampleMetricNorm (
		final org.drip.spaces.instance.GeneralizedValidatedVector gvvi);

	/**
	 * Retrieve the Sample Covering Number Array
	 * 
	 * @param gvvi The Validated Vector Space Instance
	 * @param dblCover The Cover
	 * 
	 * @return The Sample Covering Number Array
	 */

	public double[] sampleCoveringNumber (
		final org.drip.spaces.instance.GeneralizedValidatedVector gvvi,
		final double dblCover)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblCover) || 0. >= dblCover) return null;

		double[] adblSampleMetricNorm = sampleMetricNorm (gvvi);

		if (null == adblSampleMetricNorm) return null;

		int iOutputDimensionality = adblSampleMetricNorm.length;
		double[] adblSampleCoveringNumber = new double[iOutputDimensionality];

		if (0 == iOutputDimensionality) return null;

		double dblCoverBallVolume = java.lang.Math.pow (dblCover, outputMetricVectorSpace().pNorm());

		for (int i = 0; i < iOutputDimensionality; ++i)
			adblSampleCoveringNumber[i] = adblSampleMetricNorm[i] / dblCoverBallVolume;

		return adblSampleCoveringNumber;
	}

	/**
	 * Retrieve the Sample Supremum Covering Number Array
	 * 
	 * @param gvvi The Validated Vector Space Instance
	 * @param dblCover The Cover
	 * 
	 * @return The Sample Supremum Covering Number Array
	 */

	public double[] sampleSupremumCoveringNumber (
		final org.drip.spaces.instance.GeneralizedValidatedVector gvvi,
		final double dblCover)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblCover) || 0. >= dblCover) return null;

		double[] adblSampleSupremumNorm = sampleSupremumNorm (gvvi);

		if (null == adblSampleSupremumNorm) return null;

		int iOutputDimensionality = adblSampleSupremumNorm.length;
		double[] adblSampleSupremumCoveringNumber = new double[iOutputDimensionality];

		if (0 == iOutputDimensionality) return null;

		double dblCoverBallVolume = java.lang.Math.pow (dblCover, outputMetricVectorSpace().pNorm());

		for (int i = 0; i < iOutputDimensionality; ++i)
			adblSampleSupremumCoveringNumber[i] = adblSampleSupremumNorm[i] / dblCoverBallVolume;

		return adblSampleSupremumCoveringNumber;
	}

	/**
	 * Retrieve the Population ESS (Essential Spectrum) Array
	 * 
	 * @return The Population ESS (Essential Spectrum) Array
	 */

	public abstract double[] populationESS();

	/**
	 * Retrieve the Population Metric Norm Array
	 * 
	 * @return The Population Metric Norm Array
	 */

	public abstract double[] populationMetricNorm();

	/**
	 * Retrieve the Population Supremum Norm Array
	 * 
	 * @return The Population Supremum Norm Array
	 */

	public double[] populationSupremumNorm()
	{
		return populationMetricNorm();
	}

	/**
	 * Retrieve the Population Covering Number Array
	 * 
	 * @param dblCover The Cover
	 * 
	 * @return The Population Covering Number Array
	 */

	public double[] populationCoveringNumber (
		final double dblCover)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblCover) || 0. >= dblCover) return null;

		double[] adblPopulationMetricNorm = populationMetricNorm();

		if (null == adblPopulationMetricNorm) return null;

		int iOutputDimensionality = adblPopulationMetricNorm.length;
		double[] adblPopulationCoveringNumber = new double[iOutputDimensionality];

		if (0 == iOutputDimensionality) return null;

		double dblCoverBallVolume = java.lang.Math.pow (dblCover, outputMetricVectorSpace().pNorm());

		for (int i = 0; i < iOutputDimensionality; ++i)
			adblPopulationCoveringNumber[i] = adblPopulationMetricNorm[i] / dblCoverBallVolume;

		return adblPopulationCoveringNumber;
	}

	/**
	 * Retrieve the Population Supremum Covering Number Array
	 * 
	 * @param dblCover The Cover
	 * 
	 * @return The Population Supremum Covering Number Array
	 */

	public double[] populationSupremumCoveringNumber (
		final double dblCover)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblCover) || 0. >= dblCover) return null;

		double[] adblPopulationSupremumNorm = populationSupremumNorm();

		if (null == adblPopulationSupremumNorm) return null;

		int iOutputDimensionality = adblPopulationSupremumNorm.length;
		double[] adblPopulationSupremumCoveringNumber = new double[iOutputDimensionality];

		if (0 == iOutputDimensionality) return null;

		double dblCoverBallVolume = java.lang.Math.pow (dblCover, outputMetricVectorSpace().pNorm());

		for (int i = 0; i < iOutputDimensionality; ++i)
			adblPopulationSupremumCoveringNumber[i] = adblPopulationSupremumNorm[i] / dblCoverBallVolume;

		return adblPopulationSupremumCoveringNumber;
	}
}
