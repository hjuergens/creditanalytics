
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
 * BGMCurveUpdate contains the Instantaneous Snapshot of the Evolving Discount Curve Latent State
 *  Quantification Metrics Updated using the BGM LIBOR Update Dynamics.
 *
 * @author Lakshmi Krishnamurthy
 */

public class BGMCurveUpdate extends org.drip.dynamics.evolution.LSQMCurveUpdate {
	private org.drip.state.identifier.ForwardLabel _lslForward = null;
	private org.drip.state.identifier.FundingLabel _lslFunding = null;
	private org.drip.dynamics.lmm.LognormalLIBORVolatility _llv = null;

	/**
	 * Construct an Instance of BGMCurveUpdate
	 * 
	 * @param lslFunding The Funding Latent State Label
	 * @param lslForward The Forward Latent State Label
	 * @param dblInitialDate The Initial Date
	 * @param dblFinalDate The Final Date
	 * @param fc The LIBOR Forward Curve Snapshot
	 * @param spanLIBORIncrement The LIBOR Forward Curve Span Increment
	 * @param dc The Discount Factor Discount Curve
	 * @param spanDiscountFactorIncrement The Discount Factor Discount Curve Span Increment
	 * @param spanContinuousForwardRateIncrement The Continuous Forward Rate Discount Curve Span Increment
	 * @param spanSpotRateIncrement The Spot Rate Discount Curve Span Increment
	 * @param spanInstantaneousEffectiveForward The Instantaneous Effective Forward Rate Span
	 * @param spanInstantaneousNominalForward The Instantaneous Nominal Forward Rate Span
	 * @param llv The Log-normal LIBOR Rate Volatility
	 * 
	 * @return Instance of BGMCurveUpdate
	 */

	public static final BGMCurveUpdate Create (
		final org.drip.state.identifier.FundingLabel lslFunding,
		final org.drip.state.identifier.ForwardLabel lslForward,
		final double dblInitialDate,
		final double dblFinalDate,
		final org.drip.analytics.rates.ForwardCurve fc,
		final org.drip.spline.grid.Span spanLIBORIncrement,
		final org.drip.analytics.rates.DiscountCurve dc,
		final org.drip.spline.grid.Span spanDiscountFactorIncrement,
		final org.drip.spline.grid.Span spanContinuousForwardRateIncrement,
		final org.drip.spline.grid.Span spanSpotRateIncrement,
		final org.drip.spline.grid.Span spanInstantaneousEffectiveForward,
		final org.drip.spline.grid.Span spanInstantaneousNominalForward,
		final org.drip.dynamics.lmm.LognormalLIBORVolatility llv)
	{
		org.drip.dynamics.evolution.LSQMCurveSnapshot snapshot = new
			org.drip.dynamics.evolution.LSQMCurveSnapshot();

		if (!snapshot.setQMCurve (org.drip.analytics.definition.LatentStateStatic.FORWARD_QM_LIBOR_RATE, fc))
			return null;

		if (!snapshot.setQMCurve
			(org.drip.analytics.definition.LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR, dc))
			return null;

		org.drip.dynamics.evolution.LSQMCurveIncrement increment = new
			org.drip.dynamics.evolution.LSQMCurveIncrement();

		if (null != spanLIBORIncrement && !increment.setQMSpan (lslForward,
			org.drip.analytics.definition.LatentStateStatic.FORWARD_QM_LIBOR_RATE, spanLIBORIncrement))
			return null;

		if (null != spanContinuousForwardRateIncrement && !increment.setQMSpan (lslForward,
			org.drip.analytics.definition.LatentStateStatic.FORWARD_QM_CONTINUOUSLY_COMPOUNDED_FORWARD_RATE,
				spanContinuousForwardRateIncrement))
			return null;

		if (null != spanDiscountFactorIncrement && !increment.setQMSpan (lslFunding,
			org.drip.analytics.definition.LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR,
				spanDiscountFactorIncrement))
			return null;

		if (null != spanSpotRateIncrement && !increment.setQMSpan (lslFunding,
			org.drip.analytics.definition.LatentStateStatic.DISCOUNT_QM_ZERO_RATE, spanSpotRateIncrement))
			return null;

		if (null != spanInstantaneousEffectiveForward && !increment.setQMSpan (lslForward,
			org.drip.analytics.definition.LatentStateStatic.FORWARD_QM_INSTANTANEOUS_EFFECTIVE_FORWARD_RATE,
				spanInstantaneousEffectiveForward))
			return null;

		if (null != spanInstantaneousNominalForward && !increment.setQMSpan (lslForward,
			org.drip.analytics.definition.LatentStateStatic.FORWARD_QM_INSTANTANEOUS_NOMINAL_FORWARD_RATE,
				spanInstantaneousNominalForward))
			return null;

		try {
			return new BGMCurveUpdate (lslFunding, lslForward, dblInitialDate, dblFinalDate, snapshot,
				increment, llv);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private BGMCurveUpdate (
		final org.drip.state.identifier.FundingLabel lslFunding,
		final org.drip.state.identifier.ForwardLabel lslForward,
		final double dblInitialDate,
		final double dblFinalDate,
		final org.drip.dynamics.evolution.LSQMCurveSnapshot snapshot,
		final org.drip.dynamics.evolution.LSQMCurveIncrement increment,
		final org.drip.dynamics.lmm.LognormalLIBORVolatility llv)
		throws java.lang.Exception
	{
		super (dblInitialDate, dblFinalDate, snapshot, increment);

		if (null == (_lslFunding = lslFunding) || null == (_lslForward = lslForward) || null == (_llv = llv))
			throw new java.lang.Exception ("BGMCurveUpdate ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the LIBOR Forward Curve
	 * 
	 * @return The LIBOR Forward Curve
	 */

	public org.drip.analytics.rates.ForwardCurve forwardCurve()
	{
		return (org.drip.analytics.rates.ForwardCurve) snapshot().qm (_lslForward,
			org.drip.analytics.definition.LatentStateStatic.FORWARD_QM_LIBOR_RATE);
	}

	/**
	 * Retrieve the LIBOR Forward Curve Increment Span
	 * 
	 * @return The LIBOR Forward Curve Increment Span
	 */

	public org.drip.spline.grid.Span forwardCurveIncrement()
	{
		return increment().span (_lslForward,
			org.drip.analytics.definition.LatentStateStatic.FORWARD_QM_LIBOR_RATE);
	}

	/**
	 * Retrieve the Instantaneous Continuously Compounded Forward Curve Increment Span
	 * 
	 * @return The Instantaneous Continuously Compounded Forward Curve Increment Span
	 */

	public org.drip.spline.grid.Span continuousForwardRateIncrement()
	{
		return increment().span (_lslForward,
			org.drip.analytics.definition.LatentStateStatic.FORWARD_QM_CONTINUOUSLY_COMPOUNDED_FORWARD_RATE);
	}

	/**
	 * Retrieve the Instantaneous Effective Annual Forward Rate Span
	 * 
	 * @return The Instantaneous Effective Annual Forward Rate Span
	 */

	public org.drip.spline.grid.Span instantaneousEffectiveForwardRate()
	{
		return increment().span (_lslForward,
			org.drip.analytics.definition.LatentStateStatic.FORWARD_QM_INSTANTANEOUS_EFFECTIVE_FORWARD_RATE);
	}

	/**
	 * Retrieve the Instantaneous Nominal Annual Forward Rate Span
	 * 
	 * @return The Instantaneous Nominal Annual Forward Rate Span
	 */

	public org.drip.spline.grid.Span instantaneousNominalForwardRate()
	{
		return increment().span (_lslForward,
			org.drip.analytics.definition.LatentStateStatic.FORWARD_QM_INSTANTANEOUS_NOMINAL_FORWARD_RATE);
	}

	/**
	 * Retrieve the Discount Factor Curve
	 * 
	 * @return The Discount Factor Curve
	 */

	public org.drip.analytics.rates.DiscountCurve discountCurve()
	{
		return (org.drip.analytics.rates.DiscountCurve) snapshot().qm (_lslFunding,
			org.drip.analytics.definition.LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR);
	}

	/**
	 * Retrieve the Discount Factor Discount Curve Increment
	 * 
	 * @return The Discount Factor Discount Curve Increment
	 */

	public org.drip.spline.grid.Span discountCurveIncrement()
	{
		return increment().span (_lslFunding,
			org.drip.analytics.definition.LatentStateStatic.DISCOUNT_QM_DISCOUNT_FACTOR);
	}

	/**
	 * Retrieve the Spot Rate Discount Curve Increment
	 * 
	 * @return The Spot Rate Discount Curve Increment
	 */

	public org.drip.spline.grid.Span spotRateIncrement()
	{
		return increment().span (_lslFunding,
			org.drip.analytics.definition.LatentStateStatic.DISCOUNT_QM_ZERO_RATE);
	}

	/**
	 * Retrieve the Log-normal LIBOR Volatility Instance
	 * 
	 * @return The Log-normal LIBOR Volatility Instance
	 */

	public org.drip.dynamics.lmm.LognormalLIBORVolatility lognormalLIBORVolatility()
	{
		return _llv;
	}
}
