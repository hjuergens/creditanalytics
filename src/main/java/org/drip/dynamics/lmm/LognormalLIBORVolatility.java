
package org.drip.dynamics.lmm;

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
 * LognormalLIBORVolatility implements the Multi-Factor Log-normal LIBOR Volatility as formulated in:
 * 
 *  1) Goldys, B., M. Musiela, and D. Sondermann (1994): Log-normality of Rates and Term Structure Models,
 *  	The University of New South Wales.
 * 
 *  2) Musiela, M. (1994): Nominal Annual Rates and Log-normal Volatility Structure, The University of New
 *   	South Wales.
 * 
 * 	3) Brace, A., D. Gatarek, and M. Musiela (1997): The Market Model of Interest Rate Dynamics, Mathematical
 * 		Finance 7 (2), 127-155.
 *
 * @author Lakshmi Krishnamurthy
 */

public class LognormalLIBORVolatility extends org.drip.dynamics.hjm.MultiFactorVolatility {
	private double _dblSpotDate = java.lang.Double.NaN;
	private org.drip.state.identifier.ForwardLabel _lslForward = null;

	/**
	 * LognormalLIBORVolatility Constructor
	 * 
	 * @param dblSpotDate The Spot Date
	 * @param lslForward The Forward Label
	 * @param aMSVolatility Array of the Multi-Factor Volatility Surfaces
	 * @param pfsg Principal Factor Sequence Generator
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public LognormalLIBORVolatility (
		final double dblSpotDate,
		final org.drip.state.identifier.ForwardLabel lslForward,
		final org.drip.analytics.definition.MarketSurface[] aMSVolatility,
		final org.drip.sequence.random.PrincipalFactorSequenceGenerator pfsg)
		throws java.lang.Exception
	{
		super (aMSVolatility, pfsg);

		if (!org.drip.quant.common.NumberUtil.IsValid (_dblSpotDate = dblSpotDate) || null == (_lslForward =
			lslForward))
			throw new java.lang.Exception ("LognormalLIBORVolatility ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the Spot Date
	 * 
	 * @return The Spot Date
	 */

	public double spotDate()
	{
		return _dblSpotDate;
	}

	/**
	 * Retrieve the Forward Label
	 * 
	 * @return The Forward Label
	 */

	public org.drip.state.identifier.ForwardLabel forwardLabel()
	{
		return _lslForward;
	}

	/**
	 * Compute the Constraint in the Difference in the Volatility of the Continuously Compounded Forward Rate
	 * 	between the Target Date and the Target Date + Forward Tenor
	 * 
	 * @param fc The Forward Curve Instance
	 * @param dblTargetDate The Target Date
	 * 
	 * @return The Constraint in the Difference in the Volatility of the Continuously Compounded Forward Rate
	 */

	public double[] continuousForwardVolatilityConstraint (
		final org.drip.analytics.rates.ForwardCurve fc,
		final double dblTargetDate)
	{
		if (null == fc || !org.drip.quant.common.NumberUtil.IsValid (dblTargetDate) || dblTargetDate <=
			_dblSpotDate)
			return null;

		java.lang.String strTenor = _lslForward.tenor();

		org.drip.analytics.definition.MarketSurface[] aMS = volatilitySurface();

		try {
			double dblLIBORDCF = fc.forward (new org.drip.analytics.date.JulianDate (dblTargetDate).addTenor
				(strTenor)) * org.drip.analytics.support.AnalyticsHelper.TenorToYearFraction (strTenor);

			int iNumSurface = aMS.length;
			double dblConstraintWeight = dblLIBORDCF / (1. + dblLIBORDCF);
			double[] adblContinuousForwardVolatilityConstraint = new double[iNumSurface];

			for (int i = 0; i < iNumSurface; ++i)
				adblContinuousForwardVolatilityConstraint[i] = dblConstraintWeight * aMS[i].node
					(_dblSpotDate, dblTargetDate);

			return adblContinuousForwardVolatilityConstraint;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Compute the Volatility of the Continuously Compounded Forward Rate Up to the Target Date
	 * 
	 * @param dblTargetDate The Target Date
	 * @param fc The Forward Curve Instance
	 * 
	 * @return The Volatility of the Continuously Compounded Forward Rate Up to the Target Date
	 */

	public double[] continuousForwardVolatility (
		final double dblTargetDate,
		final org.drip.analytics.rates.ForwardCurve fc)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblTargetDate) || dblTargetDate <= _dblSpotDate ||
			null == fc)
			return null;

		org.drip.sequence.random.PrincipalFactorSequenceGenerator pfsg = msg();

		int iNumFactor = pfsg.numFactor();

		boolean bLoop = true;
		double dblEndDate = _dblSpotDate;
		double dblTenorDCF = java.lang.Double.NaN;
		double[] adblContinuousForwardVolatility = new double[iNumFactor];

		java.lang.String strTenor = _lslForward.tenor();

		try {
			dblTenorDCF = org.drip.analytics.support.AnalyticsHelper.TenorToYearFraction (strTenor);
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int i = 0; i < iNumFactor; ++i)
			adblContinuousForwardVolatility[i] = 0.;

		double[] adblFactorPointVolatility = factorPointVolatility (_dblSpotDate, dblEndDate);

		while (bLoop) {
			try {
				if ((dblEndDate = new org.drip.analytics.date.JulianDate (dblEndDate).addTenor
					(strTenor).julian()) > dblTargetDate)
					bLoop = false;

				double dblLIBORTenorDCF = fc.forward (dblEndDate) * dblTenorDCF;

				double dblLIBORLognormalVolatilityScaler = dblLIBORTenorDCF / (1. + dblLIBORTenorDCF);

				for (int i = 0; i < iNumFactor; ++i)
					adblContinuousForwardVolatility[i] += dblLIBORLognormalVolatilityScaler *
						adblFactorPointVolatility[i];
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return adblContinuousForwardVolatility;
	}

	/**
	 * Compute the Volatility of the Continuously Compounded Forward Rate Up to the Target Date
	 * 
	 * @param dblTargetDate The Target Date
	 * @param dc The Discount Curve Instance
	 * 
	 * @return The Volatility of the Continuously Compounded Forward Rate Up to the Target Date
	 */

	public double[] continuousForwardVolatility (
		final double dblTargetDate,
		final org.drip.analytics.rates.DiscountCurve dc)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblTargetDate) || dblTargetDate <= _dblSpotDate ||
			null == dc)
			return null;

		org.drip.sequence.random.PrincipalFactorSequenceGenerator pfsg = msg();

		int iNumFactor = pfsg.numFactor();

		boolean bLoop = true;
		double dblStartDate = _dblSpotDate;
		double dblTenorDCF = java.lang.Double.NaN;
		double[] adblContinuousForwardVolatility = new double[iNumFactor];

		java.lang.String strTenor = _lslForward.tenor();

		try {
			dblTenorDCF = org.drip.analytics.support.AnalyticsHelper.TenorToYearFraction (strTenor);
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int i = 0; i < iNumFactor; ++i)
			adblContinuousForwardVolatility[i] = 0.;

		double[] adblFactorPointVolatility = factorPointVolatility (_dblSpotDate, dblStartDate);

		while (bLoop) {
			try {
				double dblLIBORTenorDCF = dc.libor (dblStartDate, strTenor) * dblTenorDCF;

				double dblLIBORLognormalVolatilityScaler = dblLIBORTenorDCF / (1. + dblLIBORTenorDCF);

				for (int i = 0; i < iNumFactor; ++i)
					adblContinuousForwardVolatility[i] += dblLIBORLognormalVolatilityScaler *
						adblFactorPointVolatility[i];

				if ((dblStartDate = new org.drip.analytics.date.JulianDate (dblStartDate).addTenor
					(strTenor).julian()) > dblTargetDate)
					bLoop = false;
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return adblContinuousForwardVolatility;
	}

	/**
	 * Multi-Factor Cross Volatility Integral
	 * 
	 * @param dblForwardDate1 Forward Date #1
	 * @param dblForwardDate2 Forward Date #2
	 * @param dblTerminalDate The Terminal Date
	 * 
	 * @return The Multi-Factor Cross Volatility Integral
	 * 
	 * @throws java.lang.Exception Thrown if the Multi-Factor Cross Volatility Integral cannot be computed
	 */

	public double crossVolatilityIntegralProduct (
		final double dblForwardDate1,
		final double dblForwardDate2,
		final double dblTerminalDate)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblForwardDate1) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblForwardDate2) ||
				!org.drip.quant.common.NumberUtil.IsValid (dblTerminalDate) || dblForwardDate1 <
					dblTerminalDate || dblForwardDate2 < dblTerminalDate)
			throw new java.lang.Exception
				("LognormalLIBORVolatility::crossVolatilityIntegralProduct => Invalid Inputs");

		org.drip.function.definition.R1ToR1 crossVolR1ToR1 = new org.drip.function.definition.R1ToR1 (null) {
			@Override public double evaluate (
				final double dblDate)
				throws java.lang.Exception
			{
				double dblCrossVolProduct = 0.;

				int iNumFactor = msg().numFactor();

				for (int iFactorIndex = 0; iFactorIndex < iNumFactor; ++iFactorIndex)
					dblCrossVolProduct += factorPointVolatility (iFactorIndex, dblDate, dblForwardDate1) *
						factorPointVolatility (iFactorIndex, dblDate, dblForwardDate2);

				return dblCrossVolProduct;
			}
		};

		return crossVolR1ToR1.integrate (_dblSpotDate, dblTerminalDate);
	}
}
