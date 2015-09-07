
package org.drip.product.rates;

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
 * SingleStreamComponent implements fixed income component that is based off of a single stream.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class SingleStreamComponent extends org.drip.product.definition.CalibratableFixedIncomeComponent {
	private String _strCode = "";
	private String _strName = "";
	private org.drip.product.rates.Stream _stream = null;
	private org.drip.param.valuation.CashSettleParams _csp = null;

	@Override protected org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> calibMeasures (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.CreditPricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams)
	{
		return null;
	}

	/**
	 * SingleStreamComponent constructor
	 * 
	 * @param strName The Component Name
	 * @param stream The Single Stream Instance
	 * @param csp Cash Settle Parameters Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public SingleStreamComponent (
		final String strName,
		final org.drip.product.rates.Stream stream,
		final org.drip.param.valuation.CashSettleParams csp)
		throws java.lang.Exception
	{
		if (null == (_strName = strName) || _strName.isEmpty() || null == (_stream = stream))
			throw new java.lang.Exception ("SingleStreamComponent ctr: Invalid Inputs");

		_csp = csp;
	}

	/**
	 * Retrieve the Stream Instance
	 * 
	 * @return The Stream Instance
	 */

	public org.drip.product.rates.Stream stream()
	{
		return _stream;
	}

	@Override public String name()
	{
		return _strName;
	}

	@Override public org.drip.analytics.support.CaseInsensitiveTreeMap<String> couponCurrency()
	{
		org.drip.analytics.support.CaseInsensitiveTreeMap<String> mapCouponCurrency = new
			org.drip.analytics.support.CaseInsensitiveTreeMap<String>();

		mapCouponCurrency.put (name(), _stream.couponCurrency());

		return mapCouponCurrency;
	}

	@Override public String payCurrency()
	{
		return _stream.payCurrency();
	}

	@Override public String principalCurrency()
	{
		return null;
	}

	@Override public
		org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.state.identifier.ForwardLabel>
			forwardLabel()
	{
		org.drip.state.identifier.ForwardLabel forwardLabel = _stream.forwardLabel();

		if (null == forwardLabel) return null;

		org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.state.identifier.ForwardLabel> mapFRI =
			new org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.state.identifier.ForwardLabel> ();

		mapFRI.put ("DERIVED", forwardLabel);

		return mapFRI;
	}

	@Override public org.drip.state.identifier.FundingLabel fundingLabel()
	{
		return _stream.fundingLabel();
	}

	@Override public org.drip.state.identifier.CreditLabel creditLabel()
	{
		return _stream.creditLabel();
	}

	@Override public org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.state.identifier.FXLabel>
		fxLabel()
	{
		org.drip.state.identifier.FXLabel fxLabel = _stream.fxLabel();

		if (null == fxLabel) return null;

		org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.state.identifier.FXLabel> mapFXLabel = new
			org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.state.identifier.FXLabel>();

		mapFXLabel.put (name(), fxLabel);

		return mapFXLabel;
	}

	@Override public double initialNotional()
		throws java.lang.Exception
	{
		return _stream.initialNotional();
	}

	@Override public double notional (
		final double dblDate)
		throws java.lang.Exception
	{
		return _stream.notional (dblDate);
	}

	@Override public double notional (
		final double dblDate1,
		final double dblDate2)
		throws java.lang.Exception
	{
		return _stream.notional (dblDate1, dblDate2);
	}

	@Override public org.drip.analytics.output.CompositePeriodCouponMetrics couponMetrics (
		final double dblAccrualEndDate,
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs)
	{
		return _stream.coupon (dblAccrualEndDate, valParams, csqs);
	}

	@Override public int freq()
	{
		return _stream.freq();
	}

	@Override public org.drip.analytics.date.JulianDate effectiveDate()
	{
		return _stream.effective();
	}

	@Override public org.drip.analytics.date.JulianDate maturityDate()
	{
		return _stream.maturity();
	}

	@Override public org.drip.analytics.date.JulianDate firstCouponDate()
	{
		return _stream.firstCouponDate();
	}

	@Override public java.util.List<org.drip.analytics.cashflow.CompositePeriod> couponPeriods()
	{
		return _stream.cashFlowPeriod();
	}

	@Override public org.drip.param.valuation.CashSettleParams cashSettleParams()
	{
		return _csp;
	}

	@Override public org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> value (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.CreditPricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams)
	{
		org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> mapResult = _stream.value
			(valParams, pricerParams, csqs, quotingParams);

		mapResult.put ("ForwardRate", mapResult.get ("Rate"));

		return mapResult;
	}

	@Override public java.util.Set<String> measureNames()
	{
		return null;
	}

	@Override public void setPrimaryCode (
		final String strCode)
	{
		_strCode = strCode;
	}

	@Override public String primaryCode()
	{
		return _strCode;
	}

	@Override public org.drip.quant.calculus.WengertJacobian jackDDirtyPVDManifestMeasure (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.CreditPricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams)
	{
		if (null == valParams || valParams.valueDate() >= maturityDate().julian() || null == csqs)
			return null;

		org.drip.analytics.rates.DiscountCurve dcFunding = csqs.fundingCurve (fundingLabel());

		if (null == dcFunding) return null;

		try {
			org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> mapMeasures = value
				(valParams, pricerParams, csqs, quotingParams);

			if (null == mapMeasures) return null;

			double dblEffectiveDate = effectiveDate().julian();

			double dblDFEffective = dcFunding.df (dblEffectiveDate);

			double dblDFMaturity = dcFunding.df (maturityDate().julian());

			org.drip.quant.calculus.WengertJacobian wjDFEffective = dcFunding.jackDDFDManifestMeasure
				(dblEffectiveDate, "Rate");

			org.drip.quant.calculus.WengertJacobian wjDFMaturity = dcFunding.jackDDFDManifestMeasure
				(maturityDate().julian(), "Rate");

			if (null == wjDFEffective || null == wjDFMaturity) return null;

			org.drip.quant.calculus.WengertJacobian wjPVDFMicroJack = new
				org.drip.quant.calculus.WengertJacobian (1, wjDFMaturity.numParameters());

			for (int i = 0; i < wjDFMaturity.numParameters(); ++i) {
				if (!wjPVDFMicroJack.accumulatePartialFirstDerivative (0, i, wjDFMaturity.firstDerivative (0,
					i) / dblDFEffective))
					return null;

				if (!wjPVDFMicroJack.accumulatePartialFirstDerivative (0, i, -wjDFEffective.firstDerivative
					(0, i) * dblDFMaturity / dblDFEffective / dblDFEffective))
					return null;
			}

			return wjPVDFMicroJack;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public org.drip.quant.calculus.WengertJacobian manifestMeasureDFMicroJack (
		final String strManifestMeasure,
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.CreditPricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams)
	{
		if (null == valParams || valParams.valueDate() >= maturityDate().julian() || null ==
			strManifestMeasure || strManifestMeasure.isEmpty() || null == csqs)
			return null;

		org.drip.analytics.rates.DiscountCurve dcFunding = csqs.fundingCurve (fundingLabel());

		if (null == dcFunding) return null;

		if ("Rate".equalsIgnoreCase (strManifestMeasure)) {
			double dblEffectiveDate = effectiveDate().julian();

			try {
				double dblDFEffective = dcFunding.df (dblEffectiveDate);

				double dblDFMaturity = dcFunding.df (maturityDate().julian());

				org.drip.quant.calculus.WengertJacobian wjDFEffective = dcFunding.jackDDFDManifestMeasure
					(dblEffectiveDate, "Rate");

				org.drip.quant.calculus.WengertJacobian wjDFMaturity = dcFunding.jackDDFDManifestMeasure
					(maturityDate().julian(), "Rate");

				if (null == wjDFEffective || null == wjDFMaturity) return null;

				org.drip.quant.calculus.WengertJacobian wjDFMicroJack = new
					org.drip.quant.calculus.WengertJacobian (1, wjDFMaturity.numParameters());

				for (int i = 0; i < wjDFMaturity.numParameters(); ++i) {
					if (!wjDFMicroJack.accumulatePartialFirstDerivative (0, i,
						wjDFMaturity.firstDerivative (0, i) / dblDFEffective))
						return null;

					if (!wjDFMicroJack.accumulatePartialFirstDerivative (0, i, -1. *
						wjDFEffective.firstDerivative (0, i) * dblDFMaturity / dblDFEffective /
							dblDFEffective))
						return null;
				}

				return wjDFMicroJack;
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override public org.drip.product.calib.ProductQuoteSet calibQuoteSet (
		final org.drip.state.representation.LatentStateSpecification[] aLSS)
	{
		try {
			return new org.drip.product.calib.FloatingStreamQuoteSet (aLSS);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public org.drip.state.estimator.PredictorResponseWeightConstraint forwardPRWC (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.CreditPricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams vcp,
		final org.drip.product.calib.ProductQuoteSet pqs)
	{
		return _stream.forwardPRWC (valParams, pricerParams, csqs, vcp, pqs);
	}

	@Override public org.drip.state.estimator.PredictorResponseWeightConstraint fundingPRWC (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.CreditPricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams vcp,
		final org.drip.product.calib.ProductQuoteSet pqs)
	{
		return _stream.fundingPRWC (valParams, pricerParams, csqs, vcp, pqs);
	}

	@Override public org.drip.state.estimator.PredictorResponseWeightConstraint fundingForwardPRWC (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.CreditPricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams vcp,
		final org.drip.product.calib.ProductQuoteSet pqs)
	{
		return _stream.fundingForwardPRWC (valParams, pricerParams, csqs, vcp, pqs);
	}
}
