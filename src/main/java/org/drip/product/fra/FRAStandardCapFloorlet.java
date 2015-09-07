
package org.drip.product.fra;

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
 * FRAStandardCapFloorlet implements the Standard FRA Caplet and Floorlet.
 *
 * @author Lakshmi Krishnamurthy
 */

public class FRAStandardCapFloorlet extends org.drip.product.option.FixedIncomeOptionComponent {
	private boolean _bIsCaplet = false;
	private org.drip.product.fra.FRAStandardComponent _fra = null;
	private org.drip.pricer.option.FokkerPlanckGenerator _fpg = null;

	/**
	 * FRAStandardCapFloorlet constructor
	 * 
	 * @param fra The Underlying FRA Standard Component
	 * @param strManifestMeasure Measure of the Underlying Component
	 * @param bIsCaplet Is the FRA Option a Caplet? TRUE => YES
	 * @param dblStrike Strike of the Underlying Component's Measure
	 * @param dblNotional Option Notional
	 * @param ltds Last Trading Date Setting
	 * @param strDayCount Day Count Convention
	 * @param strCalendar Holiday Calendar
	 * @param fpg The Fokker Planck Pricer Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public FRAStandardCapFloorlet (
		final org.drip.product.fra.FRAStandardComponent fra,
		final String strManifestMeasure,
		final boolean bIsCaplet,
		final double dblStrike,
		final double dblNotional,
		final org.drip.product.params.LastTradingDateSetting ltds,
		final String strDayCount,
		final String strCalendar,
		final org.drip.pricer.option.FokkerPlanckGenerator fpg)
		throws java.lang.Exception
	{
		super (fra, strManifestMeasure, dblStrike, dblNotional, ltds, strDayCount, strCalendar);

		if (null == (_fpg = fpg))
			throw new java.lang.Exception ("FRAStandardCapFloorlet ctr: Invalid Option Pricer");

		_fra = fra;
		_bIsCaplet = bIsCaplet;
	}

	/**
	 * Retrieve the Underlying FRA Instance
	 * 
	 * @return The FRA Instance
	 */

	public org.drip.product.fra.FRAStandardComponent fra()
	{
		return _fra;
	}

	/**
	 * Indicate whether this a Caplet/Floorlet
	 * 
	 * @return TRUE => This is a Caplet
	 */

	public boolean isCaplet()
	{
		return _bIsCaplet;
	}

	/**
	 * Retrieve the Underlying Pricer Instance
	 * 
	 * @return The Pricer Instance
	 */

	public org.drip.pricer.option.FokkerPlanckGenerator pricer()
	{
		return _fpg;
	}

	/**
	 * Generate the Standard FRA Caplet/Floorlet Measures from the Integrated Surface Variance
	 * 
	 * @param valParams The Valuation Parameters
	 * @param pricerParams The Pricer Parameters
	 * @param csqs The Market Parameters
	 * @param quotingParams The Quoting Parameters
	 * @param dblIntegratedSurfaceVariance The Integrated Surface Variance
	 * 
	 * @return The Standard FRA Caplet/Floorlet Measures
	 */

	public org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> valueFromSurfaceVariance (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.CreditPricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams,
		final double dblIntegratedSurfaceVariance)
	{
		if (null == valParams || null == csqs || !org.drip.quant.common.NumberUtil.IsValid
			(dblIntegratedSurfaceVariance))
			return null;

		org.drip.analytics.rates.DiscountCurve dcFunding = csqs.fundingCurve
			(org.drip.state.identifier.FundingLabel.Standard (_fra.payCurrency()));

		if (null == dcFunding) return null;

		double dblValueDate = valParams.valueDate();

		org.drip.product.params.LastTradingDateSetting ltds = lastTradingDateSetting();

		try {
			if (null != ltds && dblValueDate >= ltds.lastTradingDate (_fra.effectiveDate().julian(),
				calendar()))
				return null;
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		long lStart = System.nanoTime();

		double dblExerciseDate = exerciseDate().julian();

		org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> mapFRAOutput = _fra.value
			(valParams, pricerParams, csqs, quotingParams);

		String strManifestMeasure = manifestMeasure();

		if (null == mapFRAOutput || !mapFRAOutput.containsKey (strManifestMeasure)) return null;

		double dblFRADV01 = mapFRAOutput.get ("DV01");

		double dblATMManifestMeasure = mapFRAOutput.get (strManifestMeasure);

		if (!org.drip.quant.common.NumberUtil.IsValid (dblATMManifestMeasure) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblFRADV01))
			return null;

		try {
			double dblStrike = strike();

			double dblNotional = notional();

			double dblMoneynessFactor = dblATMManifestMeasure / dblStrike;
			double dblManifestMeasurePriceTransformer = java.lang.Double.NaN;
			double dblManifestMeasureIntrinsic = _bIsCaplet ? dblATMManifestMeasure - dblStrike : dblStrike -
				dblATMManifestMeasure;

			if (strManifestMeasure.equalsIgnoreCase ("Price") || strManifestMeasure.equalsIgnoreCase ("PV"))
				dblManifestMeasurePriceTransformer = dcFunding.df (dblExerciseDate);
			else if (strManifestMeasure.equalsIgnoreCase ("ForwardRate") ||
				strManifestMeasure.equalsIgnoreCase ("ParForward") || strManifestMeasure.equalsIgnoreCase
					("ParForwardRate") || strManifestMeasure.equalsIgnoreCase ("QuantoAdjustedParForward") ||
						strManifestMeasure.equalsIgnoreCase ("Rate"))
				dblManifestMeasurePriceTransformer = 10000. * dblFRADV01;

			if (!org.drip.quant.common.NumberUtil.IsValid (dblManifestMeasurePriceTransformer)) return null;

			org.drip.pricer.option.Greeks optGreek = _fpg.greeks (dblValueDate, dblExerciseDate, dblStrike,
				dcFunding, dblATMManifestMeasure, !_bIsCaplet, true, dblIntegratedSurfaceVariance);

			org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> mapResult = new
				org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>();

			double dblForwardIntrinsic = optGreek.expectedPayoff();

			double dblForwardATMIntrinsic = optGreek.expectedATMPayoff();

			double dblSpotPrice = dblForwardIntrinsic * dblManifestMeasurePriceTransformer;

			mapResult.put ("ATMFRA", dblATMManifestMeasure);

			mapResult.put ("ATMPrice", dblForwardATMIntrinsic * dblManifestMeasurePriceTransformer);

			mapResult.put ("CalcTime", (System.nanoTime() - lStart) * 1.e-09);

			mapResult.put ("Charm", optGreek.charm() * dblManifestMeasurePriceTransformer);

			mapResult.put ("Color", optGreek.color() * dblManifestMeasurePriceTransformer);

			mapResult.put ("Delta", optGreek.delta() * dblManifestMeasurePriceTransformer);

			mapResult.put ("EffectiveVolatility", optGreek.effectiveVolatility());

			mapResult.put ("ExpectedATMPayoff", optGreek.expectedATMPayoff());

			mapResult.put ("ExpectedPayoff", optGreek.expectedPayoff());

			mapResult.put ("ForwardATMIntrinsic", dblForwardATMIntrinsic);

			mapResult.put ("ForwardIntrinsic", dblForwardIntrinsic);

			mapResult.put ("Gamma", optGreek.gamma() * dblManifestMeasurePriceTransformer);

			mapResult.put ("IntegratedSurfaceVariance", dblIntegratedSurfaceVariance);

			mapResult.put ("ManifestMeasureIntrinsic", dblManifestMeasureIntrinsic);

			mapResult.put ("ManifestMeasureIntrinsicValue", dblManifestMeasureIntrinsic *
				dblManifestMeasurePriceTransformer);

			mapResult.put ("MoneynessFactor", dblMoneynessFactor);

			mapResult.put ("Price", dblSpotPrice);

			mapResult.put ("Prob1", optGreek.prob1());

			mapResult.put ("Prob2", optGreek.prob2());

			mapResult.put ("PV", dblSpotPrice * dblNotional);

			mapResult.put ("Rho", optGreek.rho() * dblManifestMeasurePriceTransformer);

			mapResult.put ("Speed", optGreek.speed() * dblManifestMeasurePriceTransformer);

			mapResult.put ("SpotPrice", dblSpotPrice);

			mapResult.put ("Theta", optGreek.theta() * dblManifestMeasurePriceTransformer);

			mapResult.put ("Ultima", optGreek.ultima() * dblManifestMeasurePriceTransformer);

			mapResult.put ("Upfront", dblSpotPrice);

			mapResult.put ("Vanna", optGreek.vanna() * dblManifestMeasurePriceTransformer);

			mapResult.put ("Vega", optGreek.vega() * dblManifestMeasurePriceTransformer);

			mapResult.put ("Veta", optGreek.veta() * dblManifestMeasurePriceTransformer);

			mapResult.put ("Vomma", optGreek.vomma() * dblManifestMeasurePriceTransformer);

			return mapResult;
		} catch (java.lang.Exception e) {
			// e.printStackTrace();
		}

		return null;
	}

	@Override public org.drip.analytics.support.CaseInsensitiveTreeMap<String> couponCurrency()
	{
		return _fra.couponCurrency();
	}

	@Override public String payCurrency()
	{
		return _fra.payCurrency();
	}

	@Override public String principalCurrency()
	{
		return _fra.principalCurrency();
	}

	@Override public org.drip.state.identifier.FundingLabel fundingLabel()
	{
		return _fra.fundingLabel();
	}

	@Override public org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> value (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.CreditPricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams)
	{
		if (null == valParams || null == csqs) return null;

		try {
			return valueFromSurfaceVariance (valParams, pricerParams, csqs, quotingParams,
				org.drip.analytics.support.OptionHelper.IntegratedSurfaceVariance
					(csqs.forwardCurveVolSurface (_fra.forwardLabel().get ("DERIVED")),
						valParams.valueDate(), exerciseDate().julian()));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public java.util.Set<String> measureNames()
	{
		java.util.Set<String> setstrMeasureNames = new java.util.TreeSet<String>();

		setstrMeasureNames.add ("ATMFRA");

		setstrMeasureNames.add ("ATMPrice");

		setstrMeasureNames.add ("CalcTime");

		setstrMeasureNames.add ("Charm");

		setstrMeasureNames.add ("Color");

		setstrMeasureNames.add ("Delta");

		setstrMeasureNames.add ("EffectiveVolatility");

		setstrMeasureNames.add ("ExpectedATMPayoff");

		setstrMeasureNames.add ("ExpectedPayoff");

		setstrMeasureNames.add ("ForwardATMIntrinsic");

		setstrMeasureNames.add ("ForwardIntrinsic");

		setstrMeasureNames.add ("Gamma");

		setstrMeasureNames.add ("IntegratedSurfaceVariance");

		setstrMeasureNames.add ("ManifestMeasureIntrinsic");

		setstrMeasureNames.add ("ManifestMeasureIntrinsicValue");

		setstrMeasureNames.add ("MoneynessFactor");

		setstrMeasureNames.add ("Price");

		setstrMeasureNames.add ("Prob1");

		setstrMeasureNames.add ("Prob2");

		setstrMeasureNames.add ("PV");

		setstrMeasureNames.add ("Rho");

		setstrMeasureNames.add ("Speed");

		setstrMeasureNames.add ("SpotPrice");

		setstrMeasureNames.add ("Theta");

		setstrMeasureNames.add ("Ultima");

		setstrMeasureNames.add ("Upfront");

		setstrMeasureNames.add ("Vanna");

		setstrMeasureNames.add ("Vega");

		setstrMeasureNames.add ("Veta");

		setstrMeasureNames.add ("Vomma");

		return setstrMeasureNames;
	}

	/**
	 * Compute the Caplet/Floorlet Price from the Inputs
	 * 
	 * @param valParams The Valuation Parameters
	 * @param pricerParams Pricer Parameters
	 * @param csqs The Market Parameters
	 * @param quotingParams The Quoting Parameters
	 * @param dblVolatility The FRA Volatility
	 * 
	 * @return The Caplet/Floorlet Price
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double price (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.CreditPricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams,
		final double dblVolatility)
		throws java.lang.Exception
	{
		if (null == valParams || null == csqs || !org.drip.quant.common.NumberUtil.IsValid (dblVolatility))
			throw new java.lang.Exception ("FRAStandardCapFloorlet::price => Invalid Inputs");

		org.drip.analytics.rates.DiscountCurve dcFunding = csqs.fundingCurve
			(org.drip.state.identifier.FundingLabel.Standard (_fra.payCurrency()));

		if (null == dcFunding)
			throw new java.lang.Exception ("FRAStandardCapFloorlet::price => Invalid Inputs");

		double dblExerciseDate = exerciseDate().julian();

		org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> mapFRAOutput = _fra.value
			(valParams, pricerParams, csqs, quotingParams);

		String strManifestMeasure = manifestMeasure();

		if (null == mapFRAOutput || !mapFRAOutput.containsKey (strManifestMeasure))
			throw new java.lang.Exception ("FRAStandardCapFloorlet::price => No ATM Metric");

		double dblManifestMeasurePriceTransformer = java.lang.Double.NaN;

		if (strManifestMeasure.equalsIgnoreCase ("Price") || strManifestMeasure.equalsIgnoreCase ("PV"))
			dblManifestMeasurePriceTransformer = dcFunding.df (dblExerciseDate);
		else if (strManifestMeasure.equalsIgnoreCase ("ForwardRate") ||
			strManifestMeasure.equalsIgnoreCase ("ParForward") || strManifestMeasure.equalsIgnoreCase
				("ParForwardRate") || strManifestMeasure.equalsIgnoreCase ("QuantoAdjustedParForward") ||
					strManifestMeasure.equalsIgnoreCase ("Rate")) {
			if (!mapFRAOutput.containsKey ("DV01"))
				throw new java.lang.Exception ("FRAStandardCapFloorlet::price => No FRA DV01");

			dblManifestMeasurePriceTransformer = 10000. * mapFRAOutput.get ("DV01");
		}

		if (!org.drip.quant.common.NumberUtil.IsValid (dblManifestMeasurePriceTransformer))
			throw new java.lang.Exception
				("FRAStandardCapFloorlet::price => No Manifest Measure Price Transformer");

		return dblManifestMeasurePriceTransformer * _fpg.payoff (valParams.valueDate(), dblExerciseDate,
			strike(), dcFunding, mapFRAOutput.get (strManifestMeasure), !_bIsCaplet, true, dblVolatility,
				false);
	}

	/**
	 * Imply the Flat Caplet/Floorlet Volatility from the Market Manifest Measure
	 * 
	 * @param valParams The Valuation Parameters
	 * @param pricerParams Pricer Parameters
	 * @param csqs The Market Parameters
	 * @param quotingParams The Quoting Parameters
	 * @param strCalibMeasure The Calibration Measure
	 * @param dblCalibValue The Calibration Value
	 * 
	 * @return The Implied Caplet/Floorlet Volatility
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public double implyVolatility (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.CreditPricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams,
		final String strCalibMeasure,
		final double dblCalibValue)
		throws java.lang.Exception
	{
		if (null == valParams || null == strCalibMeasure || strCalibMeasure.isEmpty() || null == csqs ||
			!org.drip.quant.common.NumberUtil.IsValid (dblCalibValue))
			throw new java.lang.Exception ("FRAStandardCapFloorlet::implyVolatility => Invalid Inputs");

		final double dblStrike = strike();

		final double dblValueDate = valParams.valueDate();

		final double dblExerciseDate = exerciseDate().julian();

		final org.drip.analytics.rates.DiscountCurve dcFunding = csqs.fundingCurve
			(org.drip.state.identifier.FundingLabel.Standard (_fra.payCurrency()));

		org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> mapFRAOutput = _fra.value
			(valParams, pricerParams, csqs, quotingParams);

		String strManifestMeasure = manifestMeasure();

		if (null == mapFRAOutput || !mapFRAOutput.containsKey (strManifestMeasure))
			throw new java.lang.Exception ("FRAStandardCapFloorlet::implyVolatility => No ATM Metric");

		final double dblATMManifestMeasure = mapFRAOutput.get (strManifestMeasure);

		double dblManifestMeasurePriceTransformer = java.lang.Double.NaN;

		if (strManifestMeasure.equalsIgnoreCase ("Price") || strManifestMeasure.equalsIgnoreCase ("PV"))
			dblManifestMeasurePriceTransformer = dcFunding.df (dblExerciseDate);
		else if (strManifestMeasure.equalsIgnoreCase ("ForwardRate") ||
			strManifestMeasure.equalsIgnoreCase ("ParForward") || strManifestMeasure.equalsIgnoreCase
				("ParForwardRate") || strManifestMeasure.equalsIgnoreCase ("QuantoAdjustedParForward") ||
					strManifestMeasure.equalsIgnoreCase ("Rate")) {
			if (!mapFRAOutput.containsKey ("DV01"))
				throw new java.lang.Exception ("FRAStandardCapFloorlet::implyVolatility => No DV01");

			dblManifestMeasurePriceTransformer = 10000. * mapFRAOutput.get ("DV01");
		}

		final double dblManifestMeasurePriceTransformerCalib = dblManifestMeasurePriceTransformer;

		if (!org.drip.quant.common.NumberUtil.IsValid (dblManifestMeasurePriceTransformer))
			throw new java.lang.Exception ("FRAStandardCapFloorlet::implyVolatility => No Transformer");

		org.drip.function.definition.R1ToR1 funcVolPricer = new org.drip.function.definition.R1ToR1 (null) {
			@Override public double evaluate (
				final double dblVolatility)
				throws java.lang.Exception
			{
				if ("Price".equals (strCalibMeasure))
					return dblManifestMeasurePriceTransformerCalib * _fpg.payoff (dblValueDate,
						dblExerciseDate, dblStrike, dcFunding, dblATMManifestMeasure, !_bIsCaplet, true,
							dblVolatility, false);

				if ("ATMPrice".equals (strCalibMeasure))
					return dblManifestMeasurePriceTransformerCalib * _fpg.payoff (dblValueDate,
						dblExerciseDate, dblATMManifestMeasure, dcFunding, dblATMManifestMeasure,
							!_bIsCaplet, true, dblVolatility, false);

				java.util.Map<String, java.lang.Double> mapOutput = valueFromSurfaceVariance
					(valParams, pricerParams, csqs, quotingParams, dblVolatility * dblVolatility *
						(dblExerciseDate - dblValueDate) / 365.25);

				if (null == mapOutput || !mapOutput.containsKey (strCalibMeasure))
					throw new java.lang.Exception
						("FRAStandardCapFloorlet::implyVolatility => Cannot generate Calibration Measure");

				return mapOutput.get (strCalibMeasure);

			}
		};

		org.drip.function.solverR1ToR1.FixedPointFinderOutput fpfo = (new
			org.drip.function.solverR1ToR1.FixedPointFinderBrent (dblCalibValue, funcVolPricer,
				false)).findRoot();

		if (null == fpfo || !fpfo.containsRoot())
			throw new java.lang.Exception
				("FRAStandardCapFloorlet::implyVolatility => Cannot calibrate the Vol");

		return fpfo.getRoot();
	}
}
