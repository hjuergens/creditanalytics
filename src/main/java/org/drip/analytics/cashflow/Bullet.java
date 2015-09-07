
package org.drip.analytics.cashflow;

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
 * Bullet holds the point realizations for the latent states relevant to terminal valuation of a bullet cash
 *  flow.
 *
 * @author Lakshmi Krishnamurthy
 */

public class Bullet {

	/*
	 * Date Fields
	 */

	private double _dblPayDate = java.lang.Double.NaN;
	private double _dblFXFixingDate = java.lang.Double.NaN;
	private double _dblTerminalDate = java.lang.Double.NaN;

	/*
	 * Period Latent State Identification Support Fields
	 */

	private String _strPayCurrency = "";
	private String _strCouponCurrency = "";
	private org.drip.state.identifier.CreditLabel _creditLabel = null;

	/*
	 * Period Cash Extensive Fields
	 */

	private double _dblBaseNotional = java.lang.Double.NaN;
	private org.drip.quant.common.Array2D _notlSchedule = null;

	private org.drip.analytics.output.ConvexityAdjustment convexityAdjustment (
		final double dblValueDate,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs)
	{
		org.drip.state.identifier.CreditLabel creditLabel = creditLabel();

		org.drip.state.identifier.FundingLabel fundingLabel = fundingLabel();

		org.drip.state.identifier.FXLabel fxLabel = fxLabel();

		org.drip.analytics.output.ConvexityAdjustment convAdj = new
			org.drip.analytics.output.ConvexityAdjustment();

		try {
			if (!convAdj.setCreditFunding (null != csqs ? java.lang.Math.exp
				(org.drip.analytics.support.OptionHelper.IntegratedCrossVolQuanto (csqs.creditCurveVolSurface
					(creditLabel), csqs.fundingCurveVolSurface (fundingLabel), csqs.creditFundingCorrSurface
						(creditLabel, fundingLabel), dblValueDate, _dblPayDate)) : 1.))
				return null;

			if (!convAdj.setCreditFX (null != csqs && isFXMTM() ? java.lang.Math.exp
				(org.drip.analytics.support.OptionHelper.IntegratedCrossVolQuanto (csqs.creditCurveVolSurface
					(creditLabel), csqs.fxCurveVolSurface (fxLabel), csqs.creditFXCorrSurface (creditLabel,
						fxLabel), dblValueDate, _dblPayDate)) : 1.))
				return null;

			if (!convAdj.setFundingFX (null != csqs && isFXMTM() ? java.lang.Math.exp
				(org.drip.analytics.support.OptionHelper.IntegratedCrossVolQuanto
					(csqs.fundingCurveVolSurface (fundingLabel), csqs.fxCurveVolSurface (fxLabel),
						csqs.fundingFXCorrSurface (fundingLabel, fxLabel), dblValueDate, _dblPayDate)) : 1.))
				return null;

			return convAdj;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct a Bullet instance from the specified parameters
	 * 
	 * @param dblTerminalDate Period End Date
	 * @param dblFXFixingDate The FX Fixing Date for non-MTM'ed Cash-flow
	 * @param dblBaseNotional Coupon Period Base Notional
	 * @param notlSchedule Coupon Period Notional Schedule
	 * @param strPayCurrency Pay Currency
	 * @param strCouponCurrency Coupon Currency
	 * @param creditLabel The Credit Label
	 * 
	 * @throws java.lang.Exception Thrown if the inputs are invalid
	 */

	public Bullet (
		final double dblTerminalDate,
		final double dblPayDate,
		final double dblFXFixingDate,
		final double dblBaseNotional,
		final org.drip.quant.common.Array2D notlSchedule,
		final String strPayCurrency,
		final String strCouponCurrency,
		final org.drip.state.identifier.CreditLabel creditLabel)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (_dblTerminalDate = dblTerminalDate) ||
			!org.drip.quant.common.NumberUtil.IsValid (_dblPayDate = dblPayDate) ||
				!org.drip.quant.common.NumberUtil.IsValid (_dblBaseNotional = dblBaseNotional) || null ==
					(_strPayCurrency = strPayCurrency) || _strPayCurrency.isEmpty() || null ==
						(_strCouponCurrency = strCouponCurrency) || _strCouponCurrency.isEmpty())
		throw new java.lang.Exception ("Bullet ctr: Invalid inputs");

		_creditLabel = creditLabel;
		_dblFXFixingDate = dblFXFixingDate;

		if (null == (_notlSchedule = notlSchedule))
			_notlSchedule = org.drip.quant.common.Array2D.BulletSchedule();
	}

	/**
	 * Return the Terminal Date
	 * 
	 * @return Terminal Date
	 */

	public double terminalDate()
	{
		return _dblTerminalDate;
	}

	/**
	 * Return the period Pay Date
	 * 
	 * @return Period Pay Date
	 */

	public double payDate()
	{
		return _dblPayDate;
	}

	/**
	 * Return the period FX Fixing Date
	 * 
	 * @return Period FX Fixing Date
	 */

	public double fxFixingDate()
	{
		return _dblFXFixingDate;
	}

	/**
	 * Coupon Period FX
	 * 
	 * @param csqs Market Parameters
	 * 
	 * @return The Period FX
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double fx (
		final org.drip.param.market.CurveSurfaceQuoteSet csqs)
		throws java.lang.Exception
	{
		org.drip.state.identifier.FXLabel fxLabel = fxLabel();

		if (null == fxLabel) return 1.;

		if (null == csqs) throw new java.lang.Exception ("Bullet::fx => Invalid Inputs");

		if (!isFXMTM()) return csqs.fixing (_dblFXFixingDate, fxLabel);

		org.drip.function.definition.R1ToR1 auFX = csqs.fxCurve (fxLabel);

		if (null == auFX)
			throw new java.lang.Exception ("Bullet::fx => No Curve for " + fxLabel.fullyQualifiedName());

		return auFX.evaluate (_dblPayDate);
	}

	/**
	 * Is this Cash Flow FX MTM'ed?
	 * 
	 * @return TRUE => FX MTM is on (i.e., FX is not driven by fixing)
	 */

	public boolean isFXMTM()
	{
		return !org.drip.quant.common.NumberUtil.IsValid (_dblFXFixingDate);
	}

	/**
	 * Retrieve the Pay Currency
	 * 
	 * @return The Pay Currency
	 */

	public String payCurrency()
	{
		return _strPayCurrency;
	}

	/**
	 * Retrieve the Coupon Currency
	 * 
	 * @return The Coupon Currency
	 */

	public String couponCurrency()
	{
		return _strCouponCurrency;
	}

	/**
	 * Get the Base Notional
	 * 
	 * @return Base Notional
	 */

	public double baseNotional()
	{
		return _dblBaseNotional;
	}

	/**
	 * Get the Notional Schedule
	 * 
	 * @return Notional Schedule
	 */

	public org.drip.quant.common.Array2D notionalSchedule()
	{
		return _notlSchedule;
	}

	/**
	 * Notional Corresponding to the specified Date
	 * 
	 * @param dblDate The Specified Date
	 * 
	 * @return The Corresponding to the specified Date
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double notional (
		final double dblDate)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblDate))
			throw new java.lang.Exception ("Bullet::notional => Invalid Inputs");

		return _dblBaseNotional * (null == _notlSchedule ? 1. : _notlSchedule.y (dblDate));
	}

	/**
	 * Notional Aggregated over the specified Dates
	 * 
	 * @param dblDate1 The Date #1
	 * @param dblDate2 The Date #2
	 * 
	 * @return The Notional Aggregated over the specified Dates
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double notional (
		final double dblDate1,
		final double dblDate2)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblDate1) || !org.drip.quant.common.NumberUtil.IsValid
			(dblDate2))
			throw new java.lang.Exception ("Bullet::notional => Invalid Dates");

		return _dblBaseNotional * (null == _notlSchedule ? 1. : _notlSchedule.y (dblDate1, dblDate2));
	}

	/**
	 * Return the Collateral Label
	 * 
	 * @return The Collateral Label
	 */

	public org.drip.state.identifier.CollateralLabel collateralLabel()
	{
		return org.drip.state.identifier.CollateralLabel.Standard (_strPayCurrency);
	}

	/**
	 * Return the Credit Label
	 * 
	 * @return The Credit Label
	 */

	public org.drip.state.identifier.CreditLabel creditLabel()
	{
		return _creditLabel;
	}

	/**
	 * Return the Funding Label
	 * 
	 * @return The Funding Label
	 */

	public org.drip.state.identifier.FundingLabel fundingLabel()
	{
		return org.drip.state.identifier.FundingLabel.Standard (_strPayCurrency);
	}

	/**
	 * Return the FX Label
	 * 
	 * @return The FX Label
	 */

	public org.drip.state.identifier.FXLabel fxLabel()
	{
		return _strPayCurrency.equalsIgnoreCase (_strCouponCurrency) ? null :
			org.drip.state.identifier.FXLabel.Standard (_strPayCurrency + "/" + _strCouponCurrency);
	}

	/**
	 * Compute the Metrics at the Specified Valuation Date
	 * 
	 * @param dblValueDate Valuation Date
	 * @param csqs The Market Curve Surface/Quote Set
	 * 
	 * @return The Metrics at the specified Valuation Date
	 */

	public org.drip.analytics.output.BulletMetrics metrics (
		final double dblValueDate,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblValueDate)) return null;

		double dblDF = 1.;
		double dblSurvival = 1.;

		org.drip.state.identifier.FXLabel fxLabel = fxLabel();

		org.drip.state.identifier.CreditLabel creditLabel = creditLabel();

		org.drip.state.identifier.FundingLabel fundingLabel = fundingLabel();

		org.drip.analytics.definition.CreditCurve cc = null == csqs ? null : csqs.creditCurve (creditLabel);

		org.drip.analytics.rates.DiscountCurve dcFunding = null == csqs ? null : csqs.fundingCurve
			(fundingLabel);

		try {
			double dblFX = fx (csqs);

			if (null != dcFunding) dblDF = dcFunding.df (_dblPayDate);

			if (null != cc) dblSurvival = cc.survival (_dblPayDate);

			return new org.drip.analytics.output.BulletMetrics (_dblTerminalDate, _dblPayDate, notional
				(_dblTerminalDate), dblSurvival, dblDF, dblFX, convexityAdjustment (dblValueDate, csqs),
					creditLabel, fundingLabel, fxLabel);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Funding Predictor/Response Constraint
	 * 
	 * @param dblValueDate The Valuation Date
	 * @param csqs The Market Curve Surface/Quote Set
	 * @param pqs Product Quote Set
	 * 
	 * @return The Funding Predictor/Response Constraint
	 */

	public org.drip.state.estimator.PredictorResponseWeightConstraint fundingPRWC (
		final double dblValueDate,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.product.calib.ProductQuoteSet pqs)
	{
		if (null == pqs) return null;

		double dblPV = 0.;

		org.drip.state.estimator.PredictorResponseWeightConstraint prwc = new
			org.drip.state.estimator.PredictorResponseWeightConstraint();

		org.drip.analytics.output.BulletMetrics bm = metrics (dblValueDate, csqs);

		if (null == bm) return null;

		java.util.Map<java.lang.Double, java.lang.Double> mapDiscountFactorLoading =
			bm.discountFactorFundingLoading (pqs.fundingLabel());

		if (null != mapDiscountFactorLoading && 0 != mapDiscountFactorLoading.size()) {
			for (java.util.Map.Entry<java.lang.Double, java.lang.Double> meDiscountFactorLoading :
				mapDiscountFactorLoading.entrySet()) {
				double dblDateAnchor = meDiscountFactorLoading.getKey();

				double dblDiscountFactorFundingLoading = meDiscountFactorLoading.getValue();

				if (!prwc.addPredictorResponseWeight (dblDateAnchor, dblDiscountFactorFundingLoading))
					return null;

				if (!prwc.addDResponseWeightDManifestMeasure ("PV", dblDateAnchor,
					dblDiscountFactorFundingLoading))
					return null;
			}
		} else
			dblPV -= bm.annuity();

		if (!prwc.updateValue (dblPV)) return null;

		if (!prwc.updateDValueDManifestMeasure ("PV", 1.)) return null;

		return prwc;
	}
}
