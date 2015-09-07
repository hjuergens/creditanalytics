
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
 * LognormalLIBORCurveEvolver sets up and implements the Multi-Factor No-arbitrage Dynamics of the full Curve
 * 	Rates State Quantifiers traced from the Evolution of the LIBOR Forward Rate as formulated in:
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

public class LognormalLIBORCurveEvolver implements org.drip.dynamics.evolution.CurveStateEvolver {
	private int _iNumForwardTenor = -1;
	private org.drip.state.identifier.ForwardLabel _lslForward = null;
	private org.drip.state.identifier.FundingLabel _lslFunding = null;
	private org.drip.spline.params.SegmentCustomBuilderControl[] _aSCBCLIBOR = null;
	private org.drip.spline.params.SegmentCustomBuilderControl[] _aSCBCDiscountFactor = null;
	private org.drip.spline.params.SegmentCustomBuilderControl[] _aSCBCLIBORIncrement = null;
	private org.drip.spline.params.SegmentCustomBuilderControl[] _aSCBCSpotRateIncrement = null;
	private org.drip.spline.params.SegmentCustomBuilderControl[] _aSCBCDiscountFactorIncrement = null;
	private org.drip.spline.params.SegmentCustomBuilderControl[] _aSCBCContinuousForwardIncrement = null;
	private org.drip.spline.params.SegmentCustomBuilderControl[] _aSCBCInstantaneousNominalForward = null;
	private org.drip.spline.params.SegmentCustomBuilderControl[] _aSCBCInstantaneousEffectiveForward = null;

	class TenorDate {
		double[] _adblLIBOR = null;
		double[] _adblTenorDate = null;

		TenorDate (
			final double[] adblTenorDate,
			final double[] adblLIBOR)
		{
			_adblLIBOR = adblLIBOR;
			_adblTenorDate = adblTenorDate;
		}

		double[] tenorDates()
		{
			return _adblTenorDate;
		}

		double[] liborRates()
		{
			return _adblLIBOR;
		}
	};

	/**
	 * Create a LognormalLIBORCurveEvolver Instance
	 * 
	 * @param lslFunding The Funding Latent State Label
	 * @param lslForward The Forward Latent State Label
	 * @param iNumForwardTenor Number of Forward Tenors to Build the Span
	 * @param scbc The Common Span Segment Custom Builder Control Instance
	 * 
	 * @return The LognormalLIBORCurveEvolver Instance
	 */

	public static final LognormalLIBORCurveEvolver Create (
		final org.drip.state.identifier.FundingLabel lslFunding,
		final org.drip.state.identifier.ForwardLabel lslForward,
		final int iNumForwardTenor,
		final org.drip.spline.params.SegmentCustomBuilderControl scbc)
		throws java.lang.Exception
	{
		try {
			return new LognormalLIBORCurveEvolver (lslFunding, lslForward, iNumForwardTenor, scbc, scbc,
				scbc, scbc, scbc, scbc, scbc, scbc);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private double forwardDerivative (
		final org.drip.analytics.rates.ForwardCurve fc,
		final double dblTargetPointDate)
		throws java.lang.Exception
	{
		org.drip.function.definition.R1ToR1 freR1ToR1 = new org.drip.function.definition.R1ToR1 (null)
		{
			@Override public double evaluate (
				final double dblDate)
				throws java.lang.Exception
			{
				return fc.forward (dblDate);
			}
		};

		return freR1ToR1.derivative (dblTargetPointDate, 1);
	}

	private double continuousForwardRateIncrement (
		final double dblViewDate,
		final double dblSpotTimeIncrement,
		final double dblSpotTimeIncrementSQRT,
		final org.drip.analytics.rates.ForwardCurve fc,
		final double[] adblMultivariateRandom,
		final org.drip.dynamics.lmm.LognormalLIBORVolatility llv)
		throws java.lang.Exception
	{
		final int iNumFactor = adblMultivariateRandom.length;

		org.drip.function.definition.R1ToR1 continuousForwardRateR1ToR1 = new
			org.drip.function.definition.R1ToR1 (null) {
			@Override public double evaluate (
				final double dblDate)
				throws java.lang.Exception
			{
				double dblForwardPointVolatilityModulus = 0.;
				double dblPointVolatilityMultifactorRandom = 0.;

				double[] adblContinuousForwardVolatility = llv.continuousForwardVolatility (dblDate, fc);

				if (null != adblContinuousForwardVolatility) {
					for (int i = 0; i < iNumFactor; ++i) {
						dblForwardPointVolatilityModulus += adblContinuousForwardVolatility[i] *
							adblContinuousForwardVolatility[i];
						dblPointVolatilityMultifactorRandom += adblContinuousForwardVolatility[i] *
							adblMultivariateRandom[i];
					}
				}

				return (fc.forward (dblDate) + 0.5 * dblForwardPointVolatilityModulus) *
					dblSpotTimeIncrement + dblPointVolatilityMultifactorRandom * dblSpotTimeIncrementSQRT;
			}
		};

		return continuousForwardRateR1ToR1.derivative (dblViewDate, 1);
	}

	private double spotRateIncrement (
		final double dblViewDate,
		final double dblSpotTimeIncrement,
		final double dblSpotTimeIncrementSQRT,
		final org.drip.analytics.rates.DiscountCurve dc,
		final double[] adblMultivariateRandom,
		final org.drip.dynamics.lmm.LognormalLIBORVolatility llv)
		throws java.lang.Exception
	{
		final int iNumFactor = adblMultivariateRandom.length;

		org.drip.function.definition.R1ToR1 spotRateR1ToR1 = new org.drip.function.definition.R1ToR1
			(null) {
			@Override public double evaluate (
				final double dblDate)
				throws java.lang.Exception
			{
				double dblPointVolatilityMultifactorRandom = 0.;

				double[] adblContinuousForwardVolatility = llv.continuousForwardVolatility (dblDate, dc);

				if (null != adblContinuousForwardVolatility) {
					for (int i = 0; i < iNumFactor; ++i)
						dblPointVolatilityMultifactorRandom += adblContinuousForwardVolatility[i] *
							adblMultivariateRandom[i];
				}

				return dc.forward (dblDate, dblDate + 1.) * dblSpotTimeIncrement +
					dblPointVolatilityMultifactorRandom * dblSpotTimeIncrementSQRT;
			}
		};

		return spotRateR1ToR1.derivative (dblViewDate, 1);
	}

	private org.drip.dynamics.lmm.BGMForwardTenorSnap timeSnap (
		final double dblSpotDate,
		final double dblTargetPointDate,
		final double dblSpotTimeIncrement,
		final double dblSpotTimeIncrementSQRT,
		final java.lang.String strForwardTenor,
		final org.drip.analytics.rates.ForwardCurve fc,
		final org.drip.analytics.rates.DiscountCurve dc,
		final org.drip.dynamics.lmm.LognormalLIBORVolatility llv)
	{
		double[] adblLognormalFactorPointVolatility = llv.factorPointVolatility (dblSpotDate,
			dblTargetPointDate);

		double[] adblContinuousForwardVolatility = llv.continuousForwardVolatility (dblTargetPointDate, fc);

		double[] adblMultivariateRandom = llv.msg().random();

		double dblCrossVolatilityDotProduct = 0.;
		double dblLognormalPointVolatilityModulus = 0.;
		double dblLIBORVolatilityMultiFactorRandom = 0.;
		double dblContinuousForwardVolatilityModulus = 0.;
		double dblForwardVolatilityMultiFactorRandom = 0.;
		int iNumFactor = adblLognormalFactorPointVolatility.length;

		for (int i = 0; i < iNumFactor; ++i) {
			dblLognormalPointVolatilityModulus += adblLognormalFactorPointVolatility[i] *
				adblLognormalFactorPointVolatility[i];
			dblCrossVolatilityDotProduct += adblLognormalFactorPointVolatility[i] *
				adblContinuousForwardVolatility[i];
			dblLIBORVolatilityMultiFactorRandom += adblLognormalFactorPointVolatility[i] *
				adblMultivariateRandom[i] * dblSpotTimeIncrementSQRT;
			dblContinuousForwardVolatilityModulus += adblContinuousForwardVolatility[i] *
				adblContinuousForwardVolatility[i];
			dblForwardVolatilityMultiFactorRandom += adblContinuousForwardVolatility[i] *
				adblMultivariateRandom[i] * dblSpotTimeIncrementSQRT;
		}

		try {
			double dblLIBOR = fc.forward (dblTargetPointDate);

			double dblDiscountFactor = dc.df (dblTargetPointDate);

			double dblSpotRate = dc.forward (dblSpotDate, dblSpotDate + 1.);

			double dblContinuousForwardRate = fc.forward (dblTargetPointDate);

			double dblDCF = org.drip.analytics.support.AnalyticsHelper.TenorToYearFraction (strForwardTenor);

			double dblLIBORDCF = dblDCF * dblLIBOR;

			double dblLIBORIncrement = dblSpotTimeIncrement * (forwardDerivative (fc, dblTargetPointDate) +
				dblLIBOR * dblCrossVolatilityDotProduct + (dblLognormalPointVolatilityModulus * dblLIBOR *
					dblLIBORDCF / (1. + dblLIBORDCF))) + dblLIBOR * dblLIBORVolatilityMultiFactorRandom;

			double dblDiscountFactorIncrement = dblDiscountFactor * (dblSpotRate - dblContinuousForwardRate)
				* dblSpotTimeIncrement - dblForwardVolatilityMultiFactorRandom;

			double dblContinuousForwardRateIncrement = continuousForwardRateIncrement (dblTargetPointDate,
				dblSpotTimeIncrement, dblSpotTimeIncrementSQRT, fc, adblMultivariateRandom, llv);

			double dblSpotRateIncrement = spotRateIncrement (dblTargetPointDate, dblSpotTimeIncrement,
				dblSpotTimeIncrementSQRT, dc, adblMultivariateRandom, llv);

			double dblContinuousForwardRateEvolved = dblContinuousForwardRate +
				dblContinuousForwardRateIncrement;

			return new org.drip.dynamics.lmm.BGMForwardTenorSnap (dblTargetPointDate, dblLIBOR +
				dblLIBORIncrement, dblLIBORIncrement, dblDiscountFactor + dblDiscountFactorIncrement,
					dblDiscountFactorIncrement, dblContinuousForwardRateIncrement, dblSpotRateIncrement,
						java.lang.Math.exp (dblContinuousForwardRateEvolved) - 1., (java.lang.Math.exp
							(dblDCF * dblContinuousForwardRateEvolved) - 1.) / dblDCF, java.lang.Math.sqrt
								(dblLognormalPointVolatilityModulus), java.lang.Math.sqrt
									(dblContinuousForwardVolatilityModulus));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * LognormalLIBORCurveEvolver Constructor
	 * 
	 * @param lslFunding The Funding Latent State Label
	 * @param lslForward The Forward Latent State Label
	 * @param iNumForwardTenor Number of Forward Tenors to Build the Span
	 * @param scbcLIBOR LIBOR Span Segment Custom Builder Control Instance
	 * @param scbcDiscountFactor Discount Factor Span Segment Custom Builder Control Instance
	 * @param scbcLIBORIncrement LIBOR Increment Span Segment Custom Builder Control Instance
	 * @param scbcDiscountFactorIncrement Discount Factor Increment Span Segment Custom Builder Control
	 * 		Instance
	 * @param scbcContinuousForwardIncrement Instantaneous Continuously Compounded Forward Rate Increment
	 *  	Span Segment Custom Builder Control Instance
	 * @param scbcSpotRateIncrement Spot Rate Increment Span Segment Custom Builder Control Instance
	 * @param scbcInstantaneousEffectiveForward Instantaneous Effective Annual Forward Rate Span Segment
	 * 		Custom Builder Control Instance
	 * @param scbcInstantaneousNominalForward Instantaneous Nominal Annual Forward Rate Span Segment Custom
	 * 		Builder Control Instance
	 * 
	 * @throws java.lang.Exception Thrown if Inputs are Invalid
	 */

	public LognormalLIBORCurveEvolver (
		final org.drip.state.identifier.FundingLabel lslFunding,
		final org.drip.state.identifier.ForwardLabel lslForward,
		final int iNumForwardTenor,
		final org.drip.spline.params.SegmentCustomBuilderControl scbcLIBOR,
		final org.drip.spline.params.SegmentCustomBuilderControl scbcDiscountFactor,
		final org.drip.spline.params.SegmentCustomBuilderControl scbcLIBORIncrement,
		final org.drip.spline.params.SegmentCustomBuilderControl scbcDiscountFactorIncrement,
		final org.drip.spline.params.SegmentCustomBuilderControl scbcContinuousForwardIncrement,
		final org.drip.spline.params.SegmentCustomBuilderControl scbcSpotRateIncrement,
		final org.drip.spline.params.SegmentCustomBuilderControl scbcInstantaneousEffectiveForward,
		final org.drip.spline.params.SegmentCustomBuilderControl scbcInstantaneousNominalForward)
		throws java.lang.Exception
	{
		if (null == (_lslFunding = lslFunding) || null == (_lslForward = lslForward) || 1 >=
			(_iNumForwardTenor = iNumForwardTenor) || null == scbcLIBOR || null == scbcLIBORIncrement || null
				== scbcDiscountFactor || null == scbcDiscountFactorIncrement || null ==
					scbcContinuousForwardIncrement || null == scbcSpotRateIncrement || null ==
						scbcInstantaneousEffectiveForward)
			throw new java.lang.Exception ("LognormalLIBORCurveEvolver ctr: Invalid Inputs");

		_aSCBCLIBOR = new org.drip.spline.params.SegmentCustomBuilderControl[iNumForwardTenor];
		_aSCBCDiscountFactor = new org.drip.spline.params.SegmentCustomBuilderControl[iNumForwardTenor];
		_aSCBCLIBORIncrement = new org.drip.spline.params.SegmentCustomBuilderControl[iNumForwardTenor];
		_aSCBCDiscountFactorIncrement = new
			org.drip.spline.params.SegmentCustomBuilderControl[iNumForwardTenor];
		_aSCBCContinuousForwardIncrement = new
			org.drip.spline.params.SegmentCustomBuilderControl[iNumForwardTenor];
		_aSCBCSpotRateIncrement = new org.drip.spline.params.SegmentCustomBuilderControl[iNumForwardTenor];
		_aSCBCInstantaneousNominalForward = new
			org.drip.spline.params.SegmentCustomBuilderControl[iNumForwardTenor];
		_aSCBCInstantaneousEffectiveForward = new
			org.drip.spline.params.SegmentCustomBuilderControl[iNumForwardTenor];

		for (int i = 0; i < iNumForwardTenor; ++i) {
			_aSCBCLIBOR[i] = scbcLIBOR;
			_aSCBCDiscountFactor[i] = scbcDiscountFactor;
			_aSCBCLIBORIncrement[i] = scbcLIBORIncrement;
			_aSCBCDiscountFactorIncrement[i] = scbcDiscountFactorIncrement;
			_aSCBCContinuousForwardIncrement[i] = scbcContinuousForwardIncrement;
			_aSCBCSpotRateIncrement[i] = scbcSpotRateIncrement;
			_aSCBCInstantaneousEffectiveForward[i] = scbcInstantaneousEffectiveForward;
			_aSCBCInstantaneousNominalForward[i] = scbcInstantaneousNominalForward;
		}
	}

	/**
	 * Retrieve the Funding Label
	 * 
	 * @return The Funding Label
	 */

	public org.drip.state.identifier.FundingLabel fundingLabel()
	{
		return _lslFunding;
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
	 * Retrieve the Number of Forward Tenors comprising the Span Tenor
	 * 
	 * @return Number of Forward Tenors comprising the Span Tenor
	 */

	public int spanTenor()
	{
		return _iNumForwardTenor;
	}

	/**
	 * Retrieve the LIBOR Curve Segment Custom Builder Control Instance
	 * 
	 * @return The LIBOR Curve Segment Custom Builder Control Instance
	 */

	public org.drip.spline.params.SegmentCustomBuilderControl scbcLIBOR()
	{
		return _aSCBCLIBOR[0];
	}

	/**
	 * Retrieve the Discount Factor Segment Custom Builder Control Instance
	 * 
	 * @return The Discount Factor Segment Custom Builder Control Instance
	 */

	public org.drip.spline.params.SegmentCustomBuilderControl scbcDiscountFactor()
	{
		return _aSCBCDiscountFactor[0];
	}

	/**
	 * Retrieve the LIBOR Increment Segment Custom Builder Control Instance
	 * 
	 * @return The LIBOR Increment Segment Custom Builder Control Instance
	 */

	public org.drip.spline.params.SegmentCustomBuilderControl scbcLIBORIncrement()
	{
		return _aSCBCLIBORIncrement[0];
	}

	/**
	 * Retrieve the Discount Factor Increment Segment Custom Builder Control Instance
	 * 
	 * @return The Discount Factor Increment Segment Custom Builder Control Instance
	 */

	public org.drip.spline.params.SegmentCustomBuilderControl scbcDiscountFactorIncrement()
	{
		return _aSCBCDiscountFactorIncrement[0];
	}

	/**
	 * Retrieve the Instantaneous Continuously Compounded Forward Rate Increment Segment Custom Builder
	 *  Control Instance
	 * 
	 * @return The Instantaneous Continuously Compounded Forward Rate Increment Segment Custom Builder
	 *  Control Instance
	 */

	public org.drip.spline.params.SegmentCustomBuilderControl scbcContinuousForwardIncrement()
	{
		return _aSCBCContinuousForwardIncrement[0];
	}

	/**
	 * Retrieve the Spot Rate Increment Segment Custom Builder Control Instance
	 * 
	 * @return The Spot Rate Increment Segment Custom Builder Control Instance
	 */

	public org.drip.spline.params.SegmentCustomBuilderControl scbcSpotRateIncrement()
	{
		return _aSCBCSpotRateIncrement[0];
	}

	/**
	 * Retrieve the Instantaneous Effective Annual Forward Rate Increment Segment Custom Builder Control
	 *  Instance
	 * 
	 * @return The Instantaneous Effective Annual Forward Rate Increment Segment Custom Builder Control
	 *  Instance
	 */

	public org.drip.spline.params.SegmentCustomBuilderControl scbcInstantaneousEffectiveForward()
	{
		return _aSCBCInstantaneousEffectiveForward[0];
	}

	/**
	 * Retrieve the Instantaneous Nominal Annual Forward Rate Increment Segment Custom Builder Control
	 *  Instance
	 * 
	 * @return The Instantaneous Nominal Annual Forward Rate Increment Segment Custom Builder Control
	 *  Instance
	 */

	public org.drip.spline.params.SegmentCustomBuilderControl scbcInstantaneousNominalForward()
	{
		return _aSCBCInstantaneousNominalForward[0];
	}

	@Override public org.drip.dynamics.lmm.BGMCurveUpdate evolve (
		final double dblSpotDate,
		final double dblViewDate,
		final double dblSpotTimeIncrement,
		final org.drip.dynamics.evolution.LSQMCurveUpdate lsqmPrev)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblSpotDate) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblViewDate) || dblSpotDate > dblViewDate ||
				!org.drip.quant.common.NumberUtil.IsValid (dblSpotTimeIncrement) || null == lsqmPrev ||
					!(lsqmPrev instanceof org.drip.dynamics.lmm.BGMCurveUpdate))
			return null;

		org.drip.dynamics.lmm.BGMCurveUpdate bgmPrev = (org.drip.dynamics.lmm.BGMCurveUpdate) lsqmPrev;
		org.drip.dynamics.lmm.BGMForwardTenorSnap[] aBGMTS = new
			org.drip.dynamics.lmm.BGMForwardTenorSnap[_iNumForwardTenor + 1];

		double dblSpotTimeIncrementSQRT = java.lang.Math.sqrt (dblSpotTimeIncrement);

		org.drip.analytics.rates.ForwardCurve fc = bgmPrev.forwardCurve();

		org.drip.analytics.rates.DiscountCurve dc = bgmPrev.discountCurve();

		org.drip.dynamics.lmm.LognormalLIBORVolatility llv = bgmPrev.lognormalLIBORVolatility();

		java.lang.String strForwardTenor = _lslForward.tenor();

		try {
			org.drip.analytics.date.JulianDate dtTargetPoint = new org.drip.analytics.date.JulianDate
				(dblViewDate);

			for (int i = 0; i <= _iNumForwardTenor; ++i) {
				if (null == (aBGMTS[i] = timeSnap (dblSpotDate, dtTargetPoint.julian(), dblSpotTimeIncrement,
					dblSpotTimeIncrementSQRT, strForwardTenor, fc, dc, llv)) || null == (dtTargetPoint =
						dtTargetPoint.addTenor (strForwardTenor)))
					return null;
			}

			org.drip.dynamics.lmm.BGMTenorNodeSequence btns = new org.drip.dynamics.lmm.BGMTenorNodeSequence
				(aBGMTS);

			org.drip.spline.stretch.BoundarySettings bs =
				org.drip.spline.stretch.BoundarySettings.NaturalStandard();

			java.lang.String strForwardLabelName = _lslForward.fullyQualifiedName();

			java.lang.String strFundingLabelName = _lslFunding.fullyQualifiedName();

			double[] adblTenorDate = btns.dates();

			org.drip.state.curve.BasisSplineForwardRate fcLIBOR = new
				org.drip.state.curve.BasisSplineForwardRate (_lslForward, new
					org.drip.spline.grid.OverlappingStretchSpan
						(org.drip.spline.stretch.MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator
							(strForwardLabelName + "_QM_LIBOR", adblTenorDate, btns.liborRates(),
								_aSCBCLIBOR, null, bs,
									org.drip.spline.stretch.MultiSegmentSequence.CALIBRATE)));

			org.drip.state.curve.DiscountFactorDiscountCurve dcDiscountFactor = new
				org.drip.state.curve.DiscountFactorDiscountCurve (_lslForward.currency(), null, new
					org.drip.spline.grid.OverlappingStretchSpan
						(org.drip.spline.stretch.MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator
							(strFundingLabelName + "_QM_DISCOUNTFACTOR", adblTenorDate,
								btns.discountFactors(), _aSCBCDiscountFactor, null, bs,
									org.drip.spline.stretch.MultiSegmentSequence.CALIBRATE)));

			org.drip.spline.stretch.MultiSegmentSequence mssDiscountFactorIncrement =
				org.drip.spline.stretch.MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator
					(strFundingLabelName + "_INCREMENT", adblTenorDate, btns.discountFactorIncrements(),
						_aSCBCDiscountFactorIncrement, null, bs,
							org.drip.spline.stretch.MultiSegmentSequence.CALIBRATE);

			org.drip.spline.stretch.MultiSegmentSequence mssContinuousForwardRateIncrement =
				org.drip.spline.stretch.MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator
					(strForwardLabelName + "_CONT_FWD_INCREMENT", adblTenorDate,
						btns.continuousForwardRateIncrements(), _aSCBCContinuousForwardIncrement, null, bs,
							org.drip.spline.stretch.MultiSegmentSequence.CALIBRATE);

			org.drip.spline.stretch.MultiSegmentSequence mssSpotRateIncrement =
				org.drip.spline.stretch.MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator
					(strForwardLabelName + "_SPOT_RATE_INCREMENT", adblTenorDate, btns.spotRateIncrements(),
						_aSCBCSpotRateIncrement, null, bs,
							org.drip.spline.stretch.MultiSegmentSequence.CALIBRATE);

			org.drip.spline.stretch.MultiSegmentSequence mssInstantaneousEffectiveForwardRate =
				org.drip.spline.stretch.MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator
					(strForwardLabelName + "_EFFECTIVE_ANNUAL_FORWARD", adblTenorDate,
						btns.instantaneousEffectiveForwardRates(), _aSCBCInstantaneousEffectiveForward, null,
							bs, org.drip.spline.stretch.MultiSegmentSequence.CALIBRATE);

			org.drip.spline.stretch.MultiSegmentSequence mssInstantaneousNominalForwardRate =
				org.drip.spline.stretch.MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator
					(strForwardLabelName + "_NOMINAL_ANNUAL_FORWARD", adblTenorDate,
						btns.instantaneousNominalForwardRates(), _aSCBCInstantaneousNominalForward, null, bs,
							org.drip.spline.stretch.MultiSegmentSequence.CALIBRATE);

			return org.drip.dynamics.lmm.BGMCurveUpdate.Create (_lslFunding, _lslForward, dblSpotDate,
				dblSpotDate + dblSpotTimeIncrement, fcLIBOR, new org.drip.spline.grid.OverlappingStretchSpan
					(org.drip.spline.stretch.MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator
						(strForwardLabelName + "_INCREMENT", adblTenorDate, btns.liborRateIncrements(),
							_aSCBCLIBORIncrement, null, bs,
								org.drip.spline.stretch.MultiSegmentSequence.CALIBRATE)), dcDiscountFactor,
									new org.drip.spline.grid.OverlappingStretchSpan
										(mssDiscountFactorIncrement), new
											org.drip.spline.grid.OverlappingStretchSpan
												(mssContinuousForwardRateIncrement), new
													org.drip.spline.grid.OverlappingStretchSpan
														(mssSpotRateIncrement), new
															org.drip.spline.grid.OverlappingStretchSpan
																(mssInstantaneousEffectiveForwardRate), new
																	org.drip.spline.grid.OverlappingStretchSpan
				(mssInstantaneousNominalForwardRate), bgmPrev.lognormalLIBORVolatility());
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private TenorDate simulateLIBOR (
		final double dblEvolutionDate,
		final double dblViewDate,
		final double dblEvolutionIncrement,
		final double dblEvolutionIncrementSQRT,
		final org.drip.analytics.rates.ForwardCurve fc,
		final java.lang.String strForwardTenor,
		final double dblForwardDCF,
		final org.drip.dynamics.lmm.LognormalLIBORVolatility llv)
	{
		double[] adblLIBOR = new double[_iNumForwardTenor + 1];
		double[] adblTenorDate = new double[_iNumForwardTenor + 1];

		double[] adblMultivariateRandom = llv.msg().random();

		try {
			org.drip.analytics.date.JulianDate dtTargetPoint = new org.drip.analytics.date.JulianDate
				(dblViewDate);

			for (int i = 0; i <= _iNumForwardTenor; ++i) {
				double dblTargetPointDate = dtTargetPoint.julian();

				double[] adblLognormalFactorPointVolatility = llv.factorPointVolatility (dblEvolutionDate,
					dblTargetPointDate);

				double[] adblContinuousForwardVolatility = llv.continuousForwardVolatility
					(dblTargetPointDate, fc);

				double dblCrossVolatilityDotProduct = 0.;
				double dblLognormalPointVolatilityModulus = 0.;
				double dblLIBORVolatilityMultiFactorRandom = 0.;
				int iNumFactor = adblLognormalFactorPointVolatility.length;

				for (int j = 0; j < iNumFactor; ++j) {
					dblLognormalPointVolatilityModulus += adblLognormalFactorPointVolatility[j] *
						adblLognormalFactorPointVolatility[j];
					dblCrossVolatilityDotProduct += adblLognormalFactorPointVolatility[j] *
						adblContinuousForwardVolatility[j];
					dblLIBORVolatilityMultiFactorRandom += adblLognormalFactorPointVolatility[j] *
						adblMultivariateRandom[j] * dblEvolutionIncrementSQRT;
				}

				double dblLIBOR = fc.forward (dblTargetPointDate);

				double dblLIBORDCF = dblForwardDCF * dblLIBOR;
				adblTenorDate[i] = dblTargetPointDate;

				adblLIBOR[i] = dblLIBOR + dblEvolutionIncrement * (forwardDerivative (fc, dblTargetPointDate)
					+ dblLIBOR * dblCrossVolatilityDotProduct + (dblLognormalPointVolatilityModulus *
						dblLIBOR * dblLIBORDCF / (1. + dblLIBORDCF))) + dblLIBOR *
							dblLIBORVolatilityMultiFactorRandom;

				dtTargetPoint = dtTargetPoint.addTenor (strForwardTenor);
			}

			return new TenorDate (adblTenorDate, adblLIBOR);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public double[][] simulatePrincipalMetric (
		final double dblEvolutionStartDate,
		final double dblEvolutionFinishDate,
		final double dblEvolutionIncrement,
		final double dblViewDate,
		final org.drip.dynamics.evolution.LSQMCurveUpdate lsqmStart,
		final int iNumSimulation)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblViewDate) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblEvolutionStartDate) || dblEvolutionStartDate >
				dblViewDate || !org.drip.quant.common.NumberUtil.IsValid (dblEvolutionFinishDate) ||
					dblEvolutionFinishDate <= dblEvolutionStartDate || dblEvolutionFinishDate > dblViewDate
						|| !org.drip.quant.common.NumberUtil.IsValid (dblEvolutionIncrement) ||
							dblEvolutionIncrement <= 0. || null == lsqmStart || !(lsqmStart instanceof
								org.drip.dynamics.lmm.BGMCurveUpdate) || 1 >= iNumSimulation)
			return null;

		org.drip.dynamics.lmm.BGMCurveUpdate bgmMetrics = (org.drip.dynamics.lmm.BGMCurveUpdate) lsqmStart;

		org.drip.dynamics.lmm.LognormalLIBORVolatility llv = bgmMetrics.lognormalLIBORVolatility();

		java.lang.String strForwardLabel = _lslForward.fullyQualifiedName() + "_QM_LIBOR";

		org.drip.analytics.rates.ForwardCurve fc = bgmMetrics.forwardCurve();

		java.lang.String strForwardTenor = _lslForward.tenor();

		int iNumTimeStep = (int) ((dblEvolutionFinishDate - dblEvolutionStartDate) / dblEvolutionIncrement) +
			1;
		double[][] aadblTenorLIBOR = new double[iNumTimeStep][_iNumForwardTenor + 1];
		double dblAnnualEvolutionIncrement = dblEvolutionIncrement / 365.;
		org.drip.analytics.rates.ForwardCurve fcLIBOR = fc;
		double dblForwardDCF = java.lang.Double.NaN;

		double dblAnnualEvolutionIncrementSQRT = java.lang.Math.sqrt (dblAnnualEvolutionIncrement);

		org.drip.spline.stretch.BoundarySettings bs =
			org.drip.spline.stretch.BoundarySettings.NaturalStandard();

		try {
			dblForwardDCF = org.drip.analytics.support.AnalyticsHelper.TenorToYearFraction (strForwardTenor);
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int i = 0 ; i < iNumTimeStep; ++i) {
			for (int j = 0 ; j <= _iNumForwardTenor; ++j)
				aadblTenorLIBOR[i][j] = 0.;
		}

		for (int iSimulationIndex = 0; iSimulationIndex < iNumSimulation; ++iSimulationIndex) {
			int iEvolutionTimeIndex = 0;

			for (double dblEvolutionDate = dblEvolutionStartDate; dblEvolutionDate <= dblEvolutionFinishDate;
				dblEvolutionDate += dblEvolutionIncrement) {
				TenorDate td = simulateLIBOR (dblEvolutionDate, dblViewDate, dblAnnualEvolutionIncrement,
					dblAnnualEvolutionIncrementSQRT, fcLIBOR, strForwardTenor, dblForwardDCF, llv);

				if (null == td) return null;

				double[] adblSimulatedLIBOR = td.liborRates();

				try {
					fcLIBOR = new org.drip.state.curve.BasisSplineForwardRate (_lslForward, new
						org.drip.spline.grid.OverlappingStretchSpan
							(org.drip.spline.stretch.MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator
						(strForwardLabel + "_QM_LIBOR", td.tenorDates(), adblSimulatedLIBOR, _aSCBCLIBOR, null,
							bs, org.drip.spline.stretch.MultiSegmentSequence.CALIBRATE)));
				} catch (java.lang.Exception e) {
					e.printStackTrace();

					return null;
				}

				for (int j = 0 ; j <= _iNumForwardTenor; ++j)
					aadblTenorLIBOR[iEvolutionTimeIndex][j] += adblSimulatedLIBOR[j];

				iEvolutionTimeIndex++;
			}
		}

		for (int i = 0 ; i < iNumTimeStep; ++i) {
			for (int j = 0 ; j <= _iNumForwardTenor; ++j)
				aadblTenorLIBOR[i][j] /= iNumSimulation;
		}

		return aadblTenorLIBOR;
	}
}
