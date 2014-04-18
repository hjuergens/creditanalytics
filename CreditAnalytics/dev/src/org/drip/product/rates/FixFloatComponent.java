
package org.drip.product.rates;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
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
 * FixFloatComponent contains the implementation of the Fix-Float Index Basis Swap product
 *  contract/valuation details. It is made off one Reference Fixed stream and one Derived floating stream.
 *  It exports the following functionality:
 *  - Standard/Custom Constructor for the IRSComponent
 *  - Dates: Effective, Maturity, Coupon dates and Product settlement Parameters
 *  - Coupon/Notional Outstanding as well as schedules
 *  - Retrieve the constituent floating streams
 *  - Market Parameters: Discount, Forward, Credit, Treasury, EDSF Curves
 *  - Cash Flow Periods: Coupon flows and (Optionally) Loss Flows
 *  - Valuation: Named Measure Generation
 *  - Calibration: The codes and constraints generation
 *  - Jacobians: Quote/DF and PV/DF micro-Jacobian generation
 *  - Serialization into and de-serialization out of byte arrays
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FixFloatComponent extends org.drip.product.definition.RatesComponent {
	private java.lang.String _strCode = "";
	private org.drip.product.rates.FixedStream _fixReference = null;
	private org.drip.product.rates.FloatingStream _floatDerived = null;

	@Override protected org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> calibMeasures (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.PricerParams pricerParams,
		final org.drip.param.definition.ComponentMarketParams mktParams,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams)
	{
		return null;
	}

	/**
	 * Construct the FixFloatComponent from the Reference Fixed and the Derived Floating Streams.
	 * 
	 * @param fixReference The Reference Fixed Stream
	 * @param floatDerived The Derived Floating Stream
	 * 
	 * @throws java.lang.Exception Thrown if the inputs are invalid
	 */

	public FixFloatComponent (
		final org.drip.product.rates.FixedStream fixReference,
		final org.drip.product.rates.FloatingStream floatDerived)
		throws java.lang.Exception
	{
		if (null == (_fixReference = fixReference) || null == (_floatDerived = floatDerived))
			throw new java.lang.Exception ("FixFloatComponent ctr: Invalid Inputs");
	}

	/**
	 * De-serialize the FixFloatComponent from the byte array
	 * 
	 * @param ab Byte Array
	 * 
	 * @throws java.lang.Exception Thrown if the FixFloatComponent cannot be de-serialized from the byte
	 *  array
	 */

	public FixFloatComponent (
		final byte[] ab)
		throws java.lang.Exception
	{
		if (null == ab || 0 == ab.length)
			throw new java.lang.Exception ("FixFloatComponent de-serializer: Invalid input Byte array");

		java.lang.String strRawString = new java.lang.String (ab);

		if (null == strRawString || strRawString.isEmpty())
			throw new java.lang.Exception ("FixFloatComponent de-serializer: Empty state");

		java.lang.String strSerializedFixedFloatComponent = strRawString.substring (0, strRawString.indexOf
			(getObjectTrailer()));

		if (null == strSerializedFixedFloatComponent || strSerializedFixedFloatComponent.isEmpty())
			throw new java.lang.Exception ("FixFloatComponent de-serializer: Cannot locate state");

		java.lang.String[] astrField = org.drip.quant.common.StringUtil.Split
			(strSerializedFixedFloatComponent, getFieldDelimiter());

		if (null == astrField || 3 > astrField.length)
			throw new java.lang.Exception ("FixFloatComponent de-serializer: Invalid reqd field set");

		// double dblVersion = new java.lang.Double (astrField[0]).doubleValue();

		if (null == astrField[1] || astrField[1].isEmpty())
			throw new java.lang.Exception
				("FixFloatComponent de-serializer: Cannot locate visible floating stream");

		if (org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[1]))
			_fixReference = null;
		else
			_fixReference = new org.drip.product.rates.FixedStream (astrField[1].getBytes());

		if (null == astrField[2] || astrField[2].isEmpty())
			throw new java.lang.Exception
				("FixFloatComponent de-serializer: Cannot locate work-out floating stream");

		if (org.drip.service.stream.Serializer.NULL_SER_STRING.equalsIgnoreCase (astrField[2]))
			_floatDerived = null;
		else
			_floatDerived = new org.drip.product.rates.FloatingStream (astrField[2].getBytes());
	}

	@Override public void setPrimaryCode (
		final java.lang.String strCode)
	{
		_strCode = strCode;
	}

	@Override public java.lang.String getPrimaryCode()
	{
		return _strCode;
	}

	@Override public java.lang.String componentName()
	{
		return "IBS=" + getMaturityDate();
	}

	@Override public java.lang.String getTreasuryCurveName()
	{
		return "";
	}

	@Override public java.lang.String getEDSFCurveName()
	{
		return "";
	}

	@Override public double getInitialNotional()
		throws java.lang.Exception
	{
		return _fixReference.getInitialNotional();
	}

	@Override public double getNotional (
		final double dblDate)
		throws java.lang.Exception
	{
		return _fixReference.getNotional (dblDate);
	}

	@Override public double getNotional (
		final double dblDate1,
		final double dblDate2)
		throws java.lang.Exception
	{
		return _fixReference.getNotional (dblDate1, dblDate2);
	}

	@Override public boolean setCurves (
		final java.lang.String strIR,
		final java.lang.String strIRTSY,
		final java.lang.String strCC)
	{
		return _fixReference.setCurves (strIR, strIRTSY, strCC) && _floatDerived.setCurves (strIR, strIRTSY,
			strCC);
	}

	@Override public double getCoupon (
		final double dblValue,
		final org.drip.param.definition.ComponentMarketParams mktParams)
		throws java.lang.Exception
	{
		return _fixReference.getCoupon (dblValue, mktParams);
	}

	@Override public java.lang.String getIRCurveName()
	{
		return _fixReference.getIRCurveName();
	}

	@Override public java.lang.String[] getForwardCurveName()
	{
		return _floatDerived.getForwardCurveName();
	}

	@Override public java.lang.String creditCurveName()
	{
		return "";
	}

	/**
	 * Retrieve the Reference Stream
	 * 
	 * @return The Reference Stream
	 */

	public org.drip.product.rates.FixedStream getReferenceStream()
	{
		return _fixReference;
	}

	/**
	 * Retrieve the Derived Stream
	 * 
	 * @return The Derived Stream
	 */

	public org.drip.product.rates.FloatingStream getDerivedStream()
	{
		return _floatDerived;
	}

	@Override public org.drip.analytics.date.JulianDate getEffectiveDate()
	{
		org.drip.analytics.date.JulianDate dtFloatReferenceEffective = _fixReference.getEffectiveDate();

		org.drip.analytics.date.JulianDate dtFloatDerivedEffective = _floatDerived.getEffectiveDate();

		if (null == dtFloatReferenceEffective || null == dtFloatDerivedEffective) return null;

		return dtFloatReferenceEffective.getJulian() < dtFloatDerivedEffective.getJulian() ?
			dtFloatReferenceEffective : dtFloatDerivedEffective;
	}

	@Override public org.drip.analytics.date.JulianDate getMaturityDate()
	{
		org.drip.analytics.date.JulianDate dtFloatReferenceMaturity = _fixReference.getMaturityDate();

		org.drip.analytics.date.JulianDate dtFloatDerivedMaturity = _floatDerived.getMaturityDate();

		if (null == dtFloatReferenceMaturity || null == dtFloatDerivedMaturity) return null;

		return dtFloatReferenceMaturity.getJulian() > dtFloatDerivedMaturity.getJulian() ?
			dtFloatReferenceMaturity : dtFloatDerivedMaturity;
	}

	@Override public org.drip.analytics.date.JulianDate getFirstCouponDate()
	{
		org.drip.analytics.date.JulianDate dtFloatReferenceFirstCoupon = _fixReference.getFirstCouponDate();

		org.drip.analytics.date.JulianDate dtFloatDerivedFirstCoupon = _floatDerived.getFirstCouponDate();

		if (null == dtFloatReferenceFirstCoupon || null == dtFloatDerivedFirstCoupon) return null;

		return dtFloatReferenceFirstCoupon.getJulian() < dtFloatDerivedFirstCoupon.getJulian() ?
			dtFloatReferenceFirstCoupon : dtFloatDerivedFirstCoupon;
	}

	@Override public java.util.List<org.drip.analytics.period.CashflowPeriod> getCashFlowPeriod()
	{
		return org.drip.analytics.support.AnalyticsHelper.MergePeriodLists
			(_fixReference.getCashFlowPeriod(), _floatDerived.getCashFlowPeriod());
	}

	@Override public org.drip.param.valuation.CashSettleParams getCashSettleParams()
	{
		return _fixReference.getCashSettleParams();
	}

	@Override public org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> value (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.PricerParams pricerParams,
		final org.drip.param.definition.ComponentMarketParams mktParams,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams)
	{
		long lStart = System.nanoTime();

		org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> mapFixedReferenceStreamResult =
			_fixReference.value (valParams, pricerParams, mktParams, quotingParams);

		org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> mapFloatDerivedStreamResult =
			_floatDerived.value (valParams, pricerParams, mktParams, quotingParams);

		if (null == mapFixedReferenceStreamResult || 0 == mapFixedReferenceStreamResult.size() || null ==
			mapFloatDerivedStreamResult || 0 == mapFloatDerivedStreamResult.size())
			return null;

		org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> mapResult = new
			org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>();

		mapResult.put ("ReferenceAccrued01", mapFixedReferenceStreamResult.get ("Accrued01"));

		mapResult.put ("ReferenceAccrued", mapFixedReferenceStreamResult.get ("FloatAccrued"));

		double dblReferenceCleanDV01 = mapFixedReferenceStreamResult.get ("CleanDV01");

		mapResult.put ("ReferenceCleanDV01", dblReferenceCleanDV01);

		double dblReferenceCleanPV = mapFixedReferenceStreamResult.get ("CleanPV");

		mapResult.put ("ReferenceCleanPV", dblReferenceCleanPV);

		mapResult.put ("ReferenceDirtyDV01", mapFixedReferenceStreamResult.get ("DirtyDV01"));

		double dblReferenceDirtyPV = mapFixedReferenceStreamResult.get ("DirtyPV");

		mapResult.put ("ReferenceDirtyPV", dblReferenceDirtyPV);

		mapResult.put ("ReferenceDV01", mapFixedReferenceStreamResult.get ("DV01"));

		mapResult.put ("ReferenceFixing01", mapFixedReferenceStreamResult.get ("Fixing01"));

		double dblReferencePV = mapFixedReferenceStreamResult.get ("PV");

		mapResult.put ("ReferencePV", dblReferencePV);

		mapResult.put ("ReferenceResetDate", mapFixedReferenceStreamResult.get ("ResetDate"));

		mapResult.put ("ReferenceResetRate", mapFixedReferenceStreamResult.get ("ResetRate"));

		mapResult.put ("DerivedAccrued01", mapFloatDerivedStreamResult.get ("Accrued01"));

		mapResult.put ("DerivedAccrued", mapFloatDerivedStreamResult.get ("FloatAccrued"));

		double dblDerivedCleanDV01 = mapFloatDerivedStreamResult.get ("CleanDV01");

		mapResult.put ("DerivedCleanDV01", dblDerivedCleanDV01);

		double dblDerivedCleanPV = mapFloatDerivedStreamResult.get ("CleanPV");

		mapResult.put ("DerivedCleanPV", dblDerivedCleanPV);

		mapResult.put ("DerivedDirtyDV01", mapFloatDerivedStreamResult.get ("DirtyDV01"));

		double dblDerivedDirtyPV = mapFloatDerivedStreamResult.get ("DirtyPV");

		mapResult.put ("DerivedDirtyPV", dblDerivedDirtyPV);

		mapResult.put ("DerivedDV01", mapFloatDerivedStreamResult.get ("DV01"));

		mapResult.put ("DerivedFixing01", mapFloatDerivedStreamResult.get ("Fixing01"));

		double dblDerivedPV = mapFloatDerivedStreamResult.get ("PV");

		mapResult.put ("DerivedPV", dblDerivedPV);

		mapResult.put ("DerivedResetDate", mapFloatDerivedStreamResult.get ("ResetDate"));

		mapResult.put ("DerivedResetRate", mapFloatDerivedStreamResult.get ("ResetRate"));

		double dblCleanPV = dblReferenceCleanPV + dblDerivedCleanPV;

		mapResult.put ("CleanPV", dblCleanPV);

		mapResult.put ("DirtyPV", dblDerivedCleanPV + dblDerivedDirtyPV);

		mapResult.put ("PV", dblReferencePV + dblDerivedPV);

		mapResult.put ("Upfront", mapFixedReferenceStreamResult.get ("Upfront") +
			mapFloatDerivedStreamResult.get ("Upfront"));

		mapResult.put ("ReferenceParBasisSpread", -1. * (dblReferenceCleanPV + dblDerivedCleanPV) /
			dblReferenceCleanDV01);

		mapResult.put ("DerivedParBasisSpread", -1. * (dblReferenceCleanPV + dblDerivedCleanPV) /
			dblDerivedCleanDV01);

		mapResult.put ("ParSwapRate", -1. * dblDerivedCleanPV / dblReferenceCleanDV01);

		double dblValueNotional = java.lang.Double.NaN;

		try {
			dblValueNotional = getNotional (valParams.valueDate());
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		try {
			if (org.drip.quant.common.NumberUtil.IsValid (dblValueNotional)) {
				double dblCleanPrice = 100. * (1. + (dblCleanPV / getInitialNotional() / dblValueNotional));

				mapResult.put ("Price", dblCleanPrice);

				mapResult.put ("CleanPrice", dblCleanPrice);
			}
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		mapResult.put ("CalcTime", (System.nanoTime() - lStart) * 1.e-09);

		return mapResult;
	}

	@Override public java.util.Set<java.lang.String> getMeasureNames()
	{
		java.util.Set<java.lang.String> setstrMeasureNames = new java.util.TreeSet<java.lang.String>();

		setstrMeasureNames.add ("CalcTime");

		setstrMeasureNames.add ("CleanPrice");

		setstrMeasureNames.add ("CleanPV");

		setstrMeasureNames.add ("DerivedAccrued01");

		setstrMeasureNames.add ("DerivedAccrued");

		setstrMeasureNames.add ("DerivedCleanDV01");

		setstrMeasureNames.add ("DerivedCleanPV");

		setstrMeasureNames.add ("DerivedDirtyDV01");

		setstrMeasureNames.add ("DerivedDirtyPV");

		setstrMeasureNames.add ("DerivedDV01");

		setstrMeasureNames.add ("DerivedFixing01");

		setstrMeasureNames.add ("DerivedParBasisSpread");

		setstrMeasureNames.add ("DerivedPV");

		setstrMeasureNames.add ("DerivedResetDate");

		setstrMeasureNames.add ("DerivedResetRate");

		setstrMeasureNames.add ("DirtyPV");

		setstrMeasureNames.add ("Price");

		setstrMeasureNames.add ("PV");

		setstrMeasureNames.add ("ReferenceAccrued01");

		setstrMeasureNames.add ("ReferenceAccrued");

		setstrMeasureNames.add ("ReferenceCleanDV01");

		setstrMeasureNames.add ("ReferenceCleanPV");

		setstrMeasureNames.add ("ReferenceDirtyDV01");

		setstrMeasureNames.add ("ReferenceDirtyPV");

		setstrMeasureNames.add ("ReferenceDV01");

		setstrMeasureNames.add ("ReferenceFixing01");

		setstrMeasureNames.add ("ReferenceParBasisSpread");

		setstrMeasureNames.add ("ReferencePV");

		setstrMeasureNames.add ("ReferenceResetDate");

		setstrMeasureNames.add ("ReferenceResetRate");

		setstrMeasureNames.add ("Upfront");

		return setstrMeasureNames;
	}

	@Override public org.drip.quant.calculus.WengertJacobian jackDDirtyPVDManifestMeasure (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.PricerParams pricerParams,
		final org.drip.param.definition.ComponentMarketParams mktParams,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams)
	{
		return null;
	}

	@Override public org.drip.quant.calculus.WengertJacobian calcQuoteDFMicroJack (
		final java.lang.String strQuote,
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.PricerParams pricerParams,
		final org.drip.param.definition.ComponentMarketParams mktParams,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams)
	{
		return null;
	}

	@Override public org.drip.state.estimator.PredictorResponseWeightConstraint generateCalibPRLC (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.PricerParams pricerParams,
		final org.drip.param.definition.ComponentMarketParams mktParams,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams,
		final org.drip.state.representation.LatentStateMetricMeasure lsmm)
	{
		if (null == valParams || valParams.valueDate() >= getMaturityDate().getJulian() || null == lsmm ||
			null == mktParams)
			return null;

		java.lang.String strQuantificationMetric = lsmm.getQuantificationMetric();

		if (null == strQuantificationMetric) return null;

		if (!org.drip.analytics.rates.DiscountCurve.QUANTIFICATION_METRIC_FORWARD_RATE.equalsIgnoreCase
			(strQuantificationMetric) &&
				!org.drip.analytics.rates.DiscountCurve.QUANTIFICATION_METRIC_DISCOUNT_FACTOR.equalsIgnoreCase
					(strQuantificationMetric))
			return null;

		java.lang.String[] astrManifestMeasure = lsmm.getManifestMeasures();

		org.drip.state.estimator.PredictorResponseWeightConstraint prwc = _floatDerived.generateCalibPRLC
			(valParams, pricerParams, mktParams, quotingParams, lsmm);

		if (null == prwc) return null;

		org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> mapReferenceValue =
			_fixReference.value (valParams, pricerParams, mktParams, quotingParams);

		if (org.drip.quant.common.StringUtil.MatchInStringArray ("DerivedParBasisSpread",
			astrManifestMeasure, false))
			return null == mapReferenceValue || !mapReferenceValue.containsKey ("CleanPV") ||
				!prwc.updateValue (-1. * mapReferenceValue.get ("CleanPV")) ? null : prwc;

		if (org.drip.quant.common.StringUtil.MatchInStringArray ("ReferenceParBasisSpread",
			astrManifestMeasure, false))
			return null == mapReferenceValue || !mapReferenceValue.containsKey ("CleanPV") ||
				!mapReferenceValue.containsKey ("CleanDV01") || !prwc.updateValue (-1. *
					mapReferenceValue.get ("CleanPV") - (mapReferenceValue.get ("CleanDV01") * 10000. *
						lsmm.getMeasureQuoteValue())) || !prwc.updateDValueDManifestMeasure
							("ReferenceParBasisSpread", -10000. * mapReferenceValue.get ("CleanDV01")) ? null
								: prwc;

		if (org.drip.quant.common.StringUtil.MatchInStringArray ("SwapRate", astrManifestMeasure, false))
			return null == mapReferenceValue || !mapReferenceValue.containsKey ("CleanDV01") ||
				!prwc.updateValue (-1. * mapReferenceValue.get ("CleanDV01") * 10000. *
					lsmm.getMeasureQuoteValue()) || !prwc.updateDValueDManifestMeasure ("SwapRate", -10000. *
						mapReferenceValue.get ("CleanDV01")) ? null : prwc;

		return null;
	}

	@Override public java.lang.String getFieldDelimiter()
	{
		return "{";
	}

	@Override public java.lang.String getObjectTrailer()
	{
		return "^";
	}

	@Override public byte[] serialize()
	{
		java.lang.StringBuffer sb = new java.lang.StringBuffer();

		sb.append (org.drip.service.stream.Serializer.VERSION + getFieldDelimiter());

		if (null == _fixReference)
			sb.append (org.drip.service.stream.Serializer.NULL_SER_STRING + getFieldDelimiter());
		else
			sb.append (new java.lang.String (_fixReference.serialize()) + getFieldDelimiter());

		if (null == _floatDerived)
			sb.append (org.drip.service.stream.Serializer.NULL_SER_STRING + getFieldDelimiter());
		else
			sb.append (new java.lang.String (_floatDerived.serialize()));

		return sb.append (getObjectTrailer()).toString().getBytes();
	}

	@Override public org.drip.service.stream.Serializer deserialize (
		final byte[] ab)
	{
		try {
			return new FixFloatComponent (ab);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}