
package org.drip.pricer.option;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
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
 * BlackScholesAlgorithm implements the Black Scholes based European Call and Put Options Pricer.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class BlackScholesAlgorithm extends org.drip.pricer.option.FokkerPlanckGenerator {

	/**
	 * Empty BlackScholesAlgorithm Constructor - nothing to be filled in with
	 */

	public BlackScholesAlgorithm()
	{
	}

	@Override public double payoff (
		final double dblStrike,
		final double dblTimeToExpiry,
		final double dblRiskFreeRate,
		final double dblUnderlier,
		final boolean bIsPut,
		final boolean bIsForward,
		final double dblVolatility,
		final boolean bAsPrice)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblStrike) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblUnderlier) ||
				!org.drip.quant.common.NumberUtil.IsValid (dblVolatility) ||
					!org.drip.quant.common.NumberUtil.IsValid (dblTimeToExpiry) ||
						!org.drip.quant.common.NumberUtil.IsValid (dblRiskFreeRate))
			throw new java.lang.Exception ("BlackScholesAlgorithm::payoff => Invalid Inputs");

		double dblD1D2Diff = dblVolatility * java.lang.Math.sqrt (dblTimeToExpiry);

		double dblDF = java.lang.Math.exp (-1. * dblRiskFreeRate * dblTimeToExpiry);

		double dblD1 = java.lang.Double.NaN;
		double dblD2 = java.lang.Double.NaN;
		double dblForward = bIsForward ? dblUnderlier : dblUnderlier / dblDF;

		if (0. != dblVolatility) {
			dblD1 = (java.lang.Math.log (dblForward / dblStrike) + dblTimeToExpiry * (0.5 * dblVolatility *
				dblVolatility)) / dblD1D2Diff;

			dblD2 = dblD1 - dblD1D2Diff;
		} else {
			dblD1 = dblForward > dblStrike ? java.lang.Double.POSITIVE_INFINITY :
				java.lang.Double.NEGATIVE_INFINITY;
			dblD2 = dblD1;
		}

		double dblCallPayoff = dblForward * org.drip.measure.continuous.Gaussian.CDF (dblD1) - dblStrike *
			org.drip.measure.continuous.Gaussian.CDF (dblD2);

		if (!bAsPrice) return bIsPut ? dblCallPayoff + dblStrike - dblForward : dblCallPayoff;

		return bIsPut ? dblDF * (dblCallPayoff + dblStrike - dblForward) : dblDF * dblCallPayoff;
	}

	@Override public org.drip.pricer.option.Greeks greeks (
		final double dblStrike,
		final double dblTimeToExpiry,
		final double dblRiskFreeRate,
		final double dblUnderlier,
		final boolean bIsPut,
		final boolean bIsForward,
		final double dblVolatility)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblStrike) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblUnderlier) ||
				!org.drip.quant.common.NumberUtil.IsValid (dblVolatility) ||
					!org.drip.quant.common.NumberUtil.IsValid (dblTimeToExpiry) ||
						!org.drip.quant.common.NumberUtil.IsValid (dblRiskFreeRate))
			return null;

		double dblD1D2Diff = dblVolatility * java.lang.Math.sqrt (dblTimeToExpiry);

		double dblDF = java.lang.Math.exp (-1. * dblRiskFreeRate * dblTimeToExpiry);

		double dblD1 = java.lang.Double.NaN;
		double dblD2 = java.lang.Double.NaN;
		double dblVega = java.lang.Double.NaN;
		double dblVeta = java.lang.Double.NaN;
		double dblATMD1 = java.lang.Double.NaN;
		double dblATMD2 = java.lang.Double.NaN;
		double dblCharm = java.lang.Double.NaN;
		double dblColor = java.lang.Double.NaN;
		double dblGamma = java.lang.Double.NaN;
		double dblSpeed = java.lang.Double.NaN;
		double dblVanna = java.lang.Double.NaN;
		double dblVomma = java.lang.Double.NaN;
		double dblUltima = java.lang.Double.NaN;
		double dblCallProb1 = java.lang.Double.NaN;
		double dblCallProb2 = java.lang.Double.NaN;
		double dblTimeDecay = java.lang.Double.NaN;
		double dblATMCallProb1 = java.lang.Double.NaN;
		double dblATMCallProb2 = java.lang.Double.NaN;
		double dblForward = bIsForward ? dblUnderlier : dblUnderlier / dblDF;

		if (0. != dblVolatility) {
			dblD1 = (java.lang.Math.log (dblForward / dblStrike) + dblTimeToExpiry * (0.5 * dblVolatility *
				dblVolatility)) / dblD1D2Diff;

			dblATMD1 = dblTimeToExpiry * (0.5 * dblVolatility * dblVolatility) / dblD1D2Diff;
			dblD2 = dblD1 - dblD1D2Diff;
			dblATMD2 = -1. * dblATMD1;
		} else {
			dblD1 = dblForward > dblStrike ? java.lang.Double.POSITIVE_INFINITY :
				java.lang.Double.NEGATIVE_INFINITY;
			dblD2 = dblD1;
			dblATMD1 = 0.;
			dblATMD2 = 0.;
		}

		try {
			dblCallProb1 = org.drip.measure.continuous.Gaussian.CDF (dblD1);

			dblCallProb2 = org.drip.measure.continuous.Gaussian.CDF (dblD2);

			dblATMCallProb1 = org.drip.measure.continuous.Gaussian.CDF (dblATMD1);

			dblATMCallProb2 = org.drip.measure.continuous.Gaussian.CDF (dblATMD2);

			double dblD1Density = org.drip.measure.continuous.Gaussian.Density (dblD1);

			double dblTimeRoot = java.lang.Math.sqrt (dblTimeToExpiry);

			dblVega = dblD1Density * dblUnderlier * dblTimeRoot;
			dblVomma = dblVega * dblD1 * dblD2 / dblVolatility;
			dblGamma = dblD1Density / (dblUnderlier * dblVolatility * dblTimeRoot);
			dblUltima = dblGamma * (dblD1 * dblD2 - 1.) / dblVolatility;
			dblSpeed = -1. * dblGamma / dblUnderlier * (1. + (dblD1 / (dblVolatility * dblTimeRoot)));
			dblTimeDecay = -0.5 * dblUnderlier * dblD1Density * dblVolatility / dblTimeRoot;
			dblVanna = dblVega / dblUnderlier * (1. - (dblD1 / (dblVolatility * dblTimeRoot)));
			dblCharm = dblD1Density * (2. * dblRiskFreeRate * dblTimeToExpiry - dblVolatility * dblD2 *
				dblTimeRoot) / (2. * dblVolatility * dblTimeToExpiry * dblTimeRoot);
			dblVeta = dblUnderlier * dblD1Density * dblTimeRoot * ((dblRiskFreeRate * dblD1 / (dblVolatility
				* dblTimeRoot)) - ((1. + dblD1 * dblD2) / (2. * dblTimeToExpiry)));
			dblColor = -0.5 * dblD1Density / (dblUnderlier * dblVolatility * dblTimeToExpiry * dblTimeRoot) *
				(1. + dblD1 * (2. * dblRiskFreeRate * dblTimeToExpiry - dblVolatility * dblD2 * dblTimeRoot)
					/ (dblVolatility * dblTimeRoot));
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		double dblExpectedCallPayoff = dblForward * dblCallProb1 - dblStrike * dblCallProb2;
		double dblExpectedATMCallPayoff = dblForward * (dblATMCallProb1 - dblATMCallProb2);
		double dblCallRho = dblUnderlier * dblTimeToExpiry * dblCallProb2;
		double dblCallPrice = dblDF * dblExpectedCallPayoff;

		try {
			if (!bIsPut)
				return new org.drip.pricer.option.Greeks (
					dblDF,
					dblVolatility,
					dblExpectedCallPayoff,
					dblExpectedATMCallPayoff,
					dblCallPrice,
					dblCallProb1,
					dblCallProb2,
					dblCallProb1,
					dblVega,
					dblTimeDecay - dblRiskFreeRate * dblCallRho,
					dblCallRho,
					dblGamma,
					dblVanna,
					dblVomma,
					dblCharm,
					dblVeta,
					dblColor,
					dblSpeed,
					dblUltima
				);

			double dblPutProb1 = org.drip.measure.continuous.Gaussian.CDF (-1. * dblD1);

			double dblPutProb2 = org.drip.measure.continuous.Gaussian.CDF (-1. * dblD2);

			double dblPutRho = -1. * dblUnderlier * dblTimeToExpiry * dblPutProb2;

			return new org.drip.pricer.option.PutGreeks (
				dblDF,
				dblVolatility,
				dblExpectedCallPayoff + dblStrike - dblForward,
				dblExpectedATMCallPayoff,
				dblDF * (-1. * dblForward * dblPutProb1 + dblStrike * dblPutProb2),
				dblCallPrice + dblDF * (dblStrike - dblForward),
				dblPutProb1,
				dblPutProb2,
				-1. * dblPutProb1,
				dblVega,
				dblTimeDecay - dblRiskFreeRate * dblPutRho,
				dblPutRho,
				dblGamma,
				dblVanna,
				dblVomma,
				dblCharm,
				dblVeta,
				dblColor,
				dblSpeed,
				dblUltima
			);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
