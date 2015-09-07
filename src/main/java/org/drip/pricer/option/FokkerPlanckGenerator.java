
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
 * FokkerPlanckGenerator holds the base functionality that the performs the PDF evolution oriented Option
 *  Pricing.
 * 
 * @author Lakshmi Krishnamurthy
 */

public abstract class FokkerPlanckGenerator implements org.drip.param.pricer.GenericPricer {

	/**
	 * Compute the Expected Payoff of the Option from the Inputs
	 * 
	 * @param dblStrike Option Strike
	 * @param dblTimeToExpiry Option Time To Expiry
	 * @param dblRiskFreeRate Option Risk Free Rate
	 * @param dblUnderlier Option Underlier Value
	 * @param bIsPut TRUE => The Option is a Put
	 * @param bIsForward TRUE => The Underlier represents the Forward, FALSE => it represents Spot
	 * @param dblInitialVolatility Option Initial Volatility Value
	 * @param bAsPrice TRUE => Return the Discounted Payoff
	 * 
	 * @return The Expected Option Payoff
	 * 
	 * @throws java.lang.Exception Thrown if the Expected Payoff cannot be calculated
	 */

	public abstract double payoff (
		final double dblStrike,
		final double dblTimeToExpiry,
		final double dblRiskFreeRate,
		final double dblUnderlier,
		final boolean bIsPut,
		final boolean bIsForward,
		final double dblInitialVolatility,
		final boolean bAsPrice)
		throws java.lang.Exception;

	/**
	 * Carry out a Sensitivity Run and generate the Pricing related measure set
	 * 
	 * @param dblStrike Option Strike
	 * @param dblTimeToExpiry Option Time To Expiry
	 * @param dblRiskFreeRate Option Risk Free Rate
	 * @param dblUnderlier Option Underlier Value
	 * @param bIsPut TRUE => The Option is a Put
	 * @param bIsForward TRUE => The Underlier represents the Forward, FALSE => it represents Spot
	 * @param dblInitialVolatility Option Initial Volatility Value
	 * 
	 * @return The Greeks Sensitivities Output
	 */

	public abstract org.drip.pricer.option.Greeks greeks (
		final double dblStrike,
		final double dblTimeToExpiry,
		final double dblRiskFreeRate,
		final double dblUnderlier,
		final boolean bIsPut,
		final boolean bIsForward,
		final double dblInitialVolatility);

	/**
	 * Compute the Expected Payoff of the Option from the Inputs
	 * 
	 * @param dblSpotDate Spot Date
	 * @param dblExpiryDate Expiry Date
	 * @param dblStrike Option Strike
	 * @param dcFunding The Funding Curve
	 * @param dblUnderlier Option Underlier Value
	 * @param bIsPut TRUE => The Option is a Put
	 * @param bIsForward TRUE => The Underlier represents the Forward, FALSE => it represents Spot
	 * @param dblInitialVolatility Option Initial Volatility Value
	 * @param bAsPrice TRUE => Return the Discounted Payoff
	 * 
	 * @return The Expected Option Payoff
	 * 
	 * @throws java.lang.Exception Thrown if the Expected Payoff cannot be calculated
	 */

	public double payoff (
		final double dblSpotDate,
		final double dblExpiryDate,
		final double dblStrike,
		final org.drip.analytics.rates.DiscountCurve dcFunding,
		final double dblUnderlier,
		final boolean bIsPut,
		final boolean bIsForward,
		final double dblInitialVolatility,
		final boolean bAsPrice)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblSpotDate) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblExpiryDate) || dblExpiryDate <= dblSpotDate ||
				!org.drip.quant.common.NumberUtil.IsValid (dblStrike) || null == dcFunding ||
					!org.drip.quant.common.NumberUtil.IsValid (dblInitialVolatility))
			throw new java.lang.Exception ("FokkerPlanckGenerator::payoff => Invalid Inputs");

		return payoff (dblStrike, (dblExpiryDate - dblSpotDate) / 365.25, dcFunding.libor (dblSpotDate,
			dblExpiryDate), dblUnderlier, bIsPut, bIsForward, dblInitialVolatility, bAsPrice);
	}

	/**
	 * Compute the Expected Payoff of the Option from the Inputs
	 * 
	 * @param dblSpotDate Spot Date
	 * @param dblExpiryDate Expiry Date
	 * @param dblStrike Option Strike
	 * @param dcFunding The Funding Curve
	 * @param dblUnderlier Option Underlier Value
	 * @param bIsPut TRUE => The Option is a Put
	 * @param bIsForward TRUE => The Underlier represents the Forward, FALSE => it represents Spot
	 * @param funcVolatilityR1ToR1 The R^1 -> R^1 Volatility Term Structure
	 * @param bAsPrice TRUE => Return the Discounted Payoff
	 * 
	 * @return The Expected Option Payoff
	 * 
	 * @throws java.lang.Exception Thrown if the Expected Payoff cannot be calculated
	 */

	public double payoff (
		final double dblSpotDate,
		final double dblExpiryDate,
		final double dblStrike,
		final org.drip.analytics.rates.DiscountCurve dcFunding,
		final double dblUnderlier,
		final boolean bIsPut,
		final boolean bIsForward,
		final org.drip.function.definition.R1ToR1 funcVolatilityR1ToR1,
		final boolean bAsPrice)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblSpotDate) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblExpiryDate) || dblExpiryDate <= dblSpotDate ||
				!org.drip.quant.common.NumberUtil.IsValid (dblStrike) || null == dcFunding || null ==
					funcVolatilityR1ToR1)
			throw new java.lang.Exception ("FokkerPlanckGenerator::payoff => Invalid Inputs");

		double dblDaysToExpiry = dblExpiryDate - dblSpotDate;

		double dblRiskFreeRate = dcFunding.libor (dblSpotDate, dblExpiryDate);

		org.drip.function.definition.R1ToR1 funcVarianceR1ToR1 = new org.drip.function.definition.R1ToR1
			(null) {
			@Override public double evaluate (
				final double dblX)
				throws java.lang.Exception
			{
				return funcVolatilityR1ToR1.evaluate (dblX) * funcVolatilityR1ToR1.evaluate (dblX);
			}
		};

		double dblEffectiveVolatility = java.lang.Math.sqrt (funcVarianceR1ToR1.integrate (dblSpotDate,
			dblExpiryDate) / dblDaysToExpiry);

		return payoff (dblStrike, dblDaysToExpiry / 365.25, dblRiskFreeRate, dblUnderlier, bIsPut,
			bIsForward, dblEffectiveVolatility, bAsPrice);
	}

	/**
	 * Carry out a Sensitivity Run and generate the Pricing related measure set
	 * 
	 * @param dblSpotDate Spot Date
	 * @param dblExpiryDate Expiry Date
	 * @param dblStrike Option Strike
	 * @param dcFunding The Funding Curve
	 * @param dblUnderlier Option Underlier Value
	 * @param bIsPut TRUE => The Option is a Put
	 * @param bIsForward TRUE => The Underlier represents the Forward, FALSE => it represents Spot
	 * @param dblIntegratedSurfaceVariance The Integrated Surface Variance
	 * 
	 * @return The Greeks Output generated from the Sensitivities Run
	 */

	public org.drip.pricer.option.Greeks greeks (
		final double dblSpotDate,
		final double dblExpiryDate,
		final double dblStrike,
		final org.drip.analytics.rates.DiscountCurve dcFunding,
		final double dblUnderlier,
		final boolean bIsPut,
		final boolean bIsForward,
		final double dblIntegratedSurfaceVariance)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblSpotDate) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblExpiryDate) || dblExpiryDate <= dblSpotDate ||
				!org.drip.quant.common.NumberUtil.IsValid (dblStrike) || null == dcFunding ||
					!org.drip.quant.common.NumberUtil.IsValid (dblIntegratedSurfaceVariance))
			return null;

		double dblTimeToExpiry = (dblExpiryDate - dblSpotDate) / 365.25;

		try {
			return greeks (dblStrike, dblTimeToExpiry, dcFunding.libor (dblSpotDate, dblExpiryDate),
				dblUnderlier, bIsPut, bIsForward, java.lang.Math.sqrt (dblIntegratedSurfaceVariance /
					dblTimeToExpiry));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Carry out a Sensitivity Run and generate the Pricing related measure set
	 * 
	 * @param dblSpotDate Spot Date
	 * @param dblExpiryDate Expiry Date
	 * @param dblStrike Option Strike
	 * @param dcFunding The Funding Curve
	 * @param dblUnderlier Option Underlier Value
	 * @param bIsPut TRUE => The Option is a Put
	 * @param bIsForward TRUE => The Underlier represents the Forward, FALSE => it represents Spot
	 * @param funcVolatilityR1ToR1 The R^1 -> R^1 Volatility Term Structure
	 * @param bCalibMode TRUE => Run on Calibration Mode
	 * 
	 * @return The Greeks Output generated from the Sensitivities Run
	 */

	public org.drip.pricer.option.Greeks greeks (
		final double dblSpotDate,
		final double dblExpiryDate,
		final double dblStrike,
		final org.drip.analytics.rates.DiscountCurve dcFunding,
		final double dblUnderlier,
		final boolean bIsPut,
		final boolean bIsForward,
		final org.drip.function.definition.R1ToR1 funcVolatilityR1ToR1)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblSpotDate) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblExpiryDate) || dblExpiryDate <= dblSpotDate ||
				!org.drip.quant.common.NumberUtil.IsValid (dblStrike) || null == dcFunding || null ==
					funcVolatilityR1ToR1)
			return null;

		double dblRiskFreeRate = java.lang.Double.NaN;
		double dblEffectiveVolatility = java.lang.Double.NaN;
		double dblTimeToExpiry = (dblExpiryDate - dblSpotDate) / 365.25;

		org.drip.function.definition.R1ToR1 funcVarianceR1ToR1 = new org.drip.function.definition.R1ToR1
			(null) {
			@Override public double evaluate (
				final double dblX)
				throws java.lang.Exception
			{
				return funcVolatilityR1ToR1.evaluate (dblX) * funcVolatilityR1ToR1.evaluate (dblX);
			}
		};

		try {
			dblRiskFreeRate = dcFunding.libor (dblSpotDate, dblExpiryDate);

			dblEffectiveVolatility = java.lang.Math.sqrt (funcVarianceR1ToR1.integrate (dblSpotDate,
				dblExpiryDate) / (365.25 * dblTimeToExpiry));
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		return greeks (dblStrike, dblTimeToExpiry, dblRiskFreeRate, dblUnderlier, bIsPut, bIsForward,
			dblEffectiveVolatility);
	}

	/**
	 * Imply the Effective Volatility From the Option Price
	 * 
	 * @param dblStrike Strike
	 * @param dblTimeToExpiry Time To Expiry
	 * @param dblRiskFreeRate Risk Free Rate
	 * @param dblUnderlier The Underlier
	 * @param bIsPut TRUE => The Option is a Put
	 * @param bIsForward TRUE => The Underlier represents the Forward, FALSE => it represents Spot
	 * @param dblPrice The Price
	 * 
	 * @return The Implied Effective Volatility
	 * 
	 * @throws java.lang.Exception Thrown if the Effective Volatility cannot be implied
	 */

	public double impliedVolatilityFromPrice (
		final double dblStrike,
		final double dblTimeToExpiry,
		final double dblRiskFreeRate,
		final double dblUnderlier,
		final boolean bIsPut,
		final boolean bIsForward,
		final double dblPrice)
		throws java.lang.Exception
	{
		org.drip.function.definition.R1ToR1 au = new org.drip.function.definition.R1ToR1 (null) {
			@Override public double evaluate (
				final double dblSpotVolatility)
				throws java.lang.Exception
			{
				return payoff (dblStrike, dblTimeToExpiry, dblRiskFreeRate, dblUnderlier, bIsPut, bIsForward,
					dblSpotVolatility, true) - dblPrice;
			}
		};

		org.drip.function.solverR1ToR1.FixedPointFinderOutput fpop = new
			org.drip.function.solverR1ToR1.FixedPointFinderBrent (0., au, true).findRoot();

		if (null == fpop || !fpop.containsRoot())
			throw new java.lang.Exception
				("FokkerPlanckGenerator::impliedVolatilityFromPrice => Cannot imply Volatility");

		return java.lang.Math.abs (fpop.getRoot());
	}

	/**
	 * Imply the Effective Volatility From the Option Price
	 * 
	 * @param dblSpotDate Spot Date
	 * @param dblExpiryDate Expiry Date
	 * @param dblStrike Option Strike
	 * @param dcFunding The Funding Curve
	 * @param dblUnderlier Option Underlier Value
	 * @param bIsPut TRUE => The Option is a Put
	 * @param bIsForward TRUE => The Underlier represents the Forward, FALSE => it represents Spot
	 * @param dblPrice The Price
	 * 
	 * @return The Implied Effective Volatility
	 * 
	 * @throws java.lang.Exception Thrown if the Effective Volatility cannot be implied
	 */

	public double impliedVolatilityFromPrice (
		final double dblSpotDate,
		final double dblExpiryDate,
		final double dblStrike,
		final org.drip.analytics.rates.DiscountCurve dcFunding,
		final double dblUnderlier,
		final boolean bIsPut,
		final boolean bIsForward,
		final double dblPrice)
		throws java.lang.Exception
	{
		org.drip.function.definition.R1ToR1 au = new org.drip.function.definition.R1ToR1 (null) {
			@Override public double evaluate (
				final double dblInitialVolatility)
				throws java.lang.Exception
			{
				return payoff (dblSpotDate, dblExpiryDate, dblStrike, dcFunding, dblUnderlier, bIsPut,
					bIsForward, dblInitialVolatility, true) - dblPrice;
			}
		};

		org.drip.function.solverR1ToR1.FixedPointFinderOutput fpop = new
			org.drip.function.solverR1ToR1.FixedPointFinderBrent (0., au, true).findRoot();

		if (null == fpop || !fpop.containsRoot())
			throw new java.lang.Exception
				("FokkerPlanckGenerator::impliedVolatilityFromPrice => Cannot imply Volatility");

		return java.lang.Math.abs (fpop.getRoot());
	}

	/**
	 * Imply the Effective Black-Scholes Volatility From the Option Price
	 * 
	 * @param dblStrike Strike
	 * @param dblTimeToExpiry Time To Expiry
	 * @param dblRiskFreeRate Risk Free Rate
	 * @param dblUnderlier The Underlier
	 * @param bIsPut TRUE => The Option is a Put
	 * @param bIsForward TRUE => The Underlier represents the Forward, FALSE => it represents Spot
	 * @param dblPrice The Price
	 * 
	 * @return The Implied Black Scholes Effective Volatility
	 * 
	 * @throws java.lang.Exception Thrown if the Black Scholes Effective Volatility cannot be implied
	 */

	public double impliedBlackScholesVolatility (
		final double dblStrike,
		final double dblTimeToExpiry,
		final double dblRiskFreeRate,
		final double dblUnderlier,
		final boolean bIsPut,
		final boolean bIsForward,
		final double dblPrice)
		throws java.lang.Exception
	{
		return new org.drip.pricer.option.BlackScholesAlgorithm().impliedVolatilityFromPrice (dblStrike,
			dblTimeToExpiry, dblRiskFreeRate, dblUnderlier, bIsPut, bIsForward, dblPrice);
	}
}
