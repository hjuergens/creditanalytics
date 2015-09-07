
package org.drip.state.curve;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
 * Copyright (C) 2012 Lakshmi Krishnamurthy
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
 * DerivedZeroRate implements the delegated ZeroCurve functionality. Beyond discount factor/zero rate
 * 	computation at specific cash pay nodes, all other functions are delegated to the embedded discount curve.
 *
 * @author Lakshmi Krishnamurthy
 */

public class DerivedZeroRate extends org.drip.analytics.rates.ZeroCurve {
	private org.drip.analytics.rates.DiscountCurve _dc = null;
	private org.drip.spline.stretch.MultiSegmentSequence _mssDF = null;
	private org.drip.spline.stretch.MultiSegmentSequence _mssZeroRate = null;

	private void updateMapEntries (
		final double dblDate,
		final int iFreq,
		final String strDC,
		final boolean bApplyCpnEOMAdj,
		final String strCalendar,
		final double dblZCBump,
		final java.util.Map<org.drip.analytics.date.JulianDate, java.lang.Double> mapDF,
		final java.util.Map<org.drip.analytics.date.JulianDate, java.lang.Double> mapZeroRate)
		throws java.lang.Exception
	{
		double dblYearFraction = org.drip.analytics.daycount.Convention.YearFraction (epoch().julian(),
			dblDate, strDC, bApplyCpnEOMAdj, null, strCalendar);

		if (!org.drip.quant.common.NumberUtil.IsValid (dblYearFraction) || 0. > dblYearFraction) return;

		org.drip.analytics.date.JulianDate dt = new org.drip.analytics.date.JulianDate (dblDate);

		if (0. == dblYearFraction) {
			mapDF.put (dt, 1.);

			mapZeroRate.put (dt, 0.);

			return;
		}

		double dblBumpedZeroRate = org.drip.analytics.support.AnalyticsHelper.DF2Yield (iFreq, _dc.df
			(dblDate), dblYearFraction) + dblZCBump;

		mapDF.put (dt, org.drip.analytics.support.AnalyticsHelper.Yield2DF (iFreq, dblBumpedZeroRate,
			dblYearFraction));

		mapZeroRate.put (dt, dblBumpedZeroRate);
	}

	/**
	 * DerivedZeroRate constructor from period, work-out, settle, and quoting parameters
	 * 
	 * @param iFreqZC Zero Curve Frequency
	 * @param strDCZC Zero Curve Day Count
	 * @param strCalendarZC Zero Curve Calendar
	 * @param bApplyEOMAdjZC Zero Coupon EOM Adjustment Flag
	 * @param lsCouponPeriod List of bond coupon periods
	 * @param dblWorkoutDate Work-out date
	 * @param dblCashPayDate Cash-Pay Date
	 * @param dc Discount Curve
	 * @param vcp Valuation Customization Parameters
	 * @param dblZCBump DC Bump
	 * @param scbc Segment Custom Builder Control Parameters
	 * 
	 * @throws java.lang.Exception
	 */

	public DerivedZeroRate (
		final int iFreqZC,
		final String strDCZC,
		final String strCalendarZC,
		final boolean bApplyEOMAdjZC,
		final java.util.List<org.drip.analytics.cashflow.CompositePeriod> lsCouponPeriod,
		final double dblWorkoutDate,
		final double dblCashPayDate,
		final org.drip.analytics.rates.DiscountCurve dc,
		final org.drip.param.valuation.ValuationCustomizationParams vcp,
		final double dblZCBump,
		final org.drip.spline.params.SegmentCustomBuilderControl scbc)
		throws java.lang.Exception
	{
		super (dc.epoch().julian(), dc.currency(), null == vcp ? null : vcp.coreCollateralizationParams());

		if (null == (_dc = dc) || null == lsCouponPeriod || 2 > lsCouponPeriod.size() ||
			!org.drip.quant.common.NumberUtil.IsValid (dblWorkoutDate) ||
				!org.drip.quant.common.NumberUtil.IsValid (dblCashPayDate) ||
					!org.drip.quant.common.NumberUtil.IsValid (dblZCBump) || null == scbc)
			throw new java.lang.Exception ("DerivedZeroRate ctr => Invalid date parameters!");

		int iFreq = 0 == iFreqZC ? 2 : iFreqZC;
		boolean bApplyCpnEOMAdj = bApplyEOMAdjZC;
		String strCalendar = strCalendarZC;

		String strDC = null == strDCZC || strDCZC.isEmpty() ? "30/360" : strDCZC;

		if (null != vcp) {
			strDC = vcp.yieldDayCount();

			iFreq = vcp.yieldFreq();

			bApplyCpnEOMAdj = vcp.applyYieldEOMAdj();

			strCalendar = vcp.yieldCalendar();
		}

		java.util.Map<org.drip.analytics.date.JulianDate, java.lang.Double> mapDF = new
			java.util.TreeMap<org.drip.analytics.date.JulianDate, java.lang.Double>();

		java.util.Map<org.drip.analytics.date.JulianDate, java.lang.Double> mapZeroRate = new
			java.util.TreeMap<org.drip.analytics.date.JulianDate, java.lang.Double>();

		for (org.drip.analytics.cashflow.CompositePeriod period : lsCouponPeriod)
			updateMapEntries (period.payDate(), iFreq, strDC, bApplyCpnEOMAdj, strCalendar, dblZCBump, mapDF,
				mapZeroRate);

		updateMapEntries (dblWorkoutDate, iFreq, strDC, bApplyCpnEOMAdj, strCalendar, dblZCBump, mapDF,
			mapZeroRate);

		updateMapEntries (dblCashPayDate, iFreq, strDC, bApplyCpnEOMAdj, strCalendar, dblZCBump, mapDF,
			mapZeroRate);

		int iNumNode = mapDF.size();

		int iNode = 0;
		double[] adblDF = new double[iNumNode];
		double[] adblDate = new double[iNumNode];
		double[] adblZeroRate = new double[iNumNode];

		for (java.util.Map.Entry<org.drip.analytics.date.JulianDate, java.lang.Double> me :
			mapDF.entrySet()) {
			org.drip.analytics.date.JulianDate dt = me.getKey();

			adblDF[iNode] = me.getValue();

			adblDate[iNode] = dt.julian();

			adblZeroRate[iNode++] = mapZeroRate.get (dt);
		}

		org.drip.spline.params.SegmentCustomBuilderControl[] aSCBC = new
			org.drip.spline.params.SegmentCustomBuilderControl[adblDF.length - 1]; 

		for (int i = 0; i < adblDF.length - 1; ++i)
			aSCBC[i] = scbc;

		_mssDF = org.drip.spline.stretch.MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator
			("DF_STRETCH", adblDate, adblDF, aSCBC, null,
				org.drip.spline.stretch.BoundarySettings.NaturalStandard(),
					org.drip.spline.stretch.MultiSegmentSequence.CALIBRATE);

		_mssZeroRate = org.drip.spline.stretch.MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator
			("ZERO_RATE_STRETCH", adblDate, adblZeroRate, aSCBC, null,
				org.drip.spline.stretch.BoundarySettings.NaturalStandard(),
					org.drip.spline.stretch.MultiSegmentSequence.CALIBRATE);
	}

	@Override public double df (
		final double dblDate)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblDate))
			throw new java.lang.Exception ("DerivedZeroCurve::df => got NaN for date");

		if (dblDate <= epoch().julian()) return 1.;

		return _mssDF.responseValue (dblDate);
	}

	@Override public org.drip.analytics.rates.ForwardRateEstimator forwardRateEstimator (
		final double dblDate,
		final org.drip.state.identifier.ForwardLabel fri)
	{
		return _dc.forwardRateEstimator (dblDate, fri);
	}

	@Override public org.drip.quant.calculus.WengertJacobian jackDDFDManifestMeasure (
		final double dblDate,
		final String strManifestMeasure)
	{
		return _dc.jackDDFDManifestMeasure (dblDate, strManifestMeasure);
	}

	@Override public double zeroRate (
		final double dblDate)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblDate))
			throw new java.lang.Exception ("DerivedZeroCurve::zeroRate => Invalid Date");

		if (dblDate <= epoch().julian()) return 1.;

		return _mssZeroRate.responseValue (dblDate);
	}

	@Override public String latentStateQuantificationMetric()
	{
		return org.drip.analytics.definition.LatentStateStatic.DISCOUNT_QM_ZERO_RATE;
	}

	@Override public org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> manifestMeasure (
		final String strInstr)
	{
		return _dc.manifestMeasure (strInstr);
	}

	@Override public org.drip.product.definition.CalibratableFixedIncomeComponent[] calibComp()
	{
		return _dc.calibComp();
	}

	@Override public org.drip.state.identifier.LatentStateLabel label()
	{
		return _dc.label();
	}

	@Override public org.drip.analytics.definition.Curve parallelShiftManifestMeasure (
		final String strManifestMeasure,
		final double dblShift)
	{
		return null;
	}

	@Override public org.drip.analytics.definition.Curve shiftManifestMeasure (
		final int iSpanIndex,
		final String strManifestMeasure,
		final double dblShift)
	{
		return null;
	}

	@Override public org.drip.analytics.definition.Curve customTweakManifestMeasure (
		final String strManifestMeasure,
		final org.drip.param.definition.ResponseValueTweakParams mmtp)
	{
		return null;
	}

	@Override public org.drip.analytics.date.JulianDate epoch()
	{
		return _dc.epoch();
	}

	@Override public boolean setCCIS (
		final org.drip.analytics.input.CurveConstructionInputSet ccis)
	{
		 return _dc.setCCIS (ccis);
	}

	@Override public org.drip.analytics.rates.DiscountCurve parallelShiftQuantificationMetric (
		final double dblShift)
	{
		return (org.drip.analytics.rates.DiscountCurve) _dc.parallelShiftQuantificationMetric (dblShift);
	}

	@Override public org.drip.analytics.definition.Curve customTweakQuantificationMetric (
		final org.drip.param.definition.ResponseValueTweakParams rvtp)
	{
		return (org.drip.analytics.rates.DiscountCurve) _dc.customTweakQuantificationMetric (rvtp);
	}

	@Override public String currency()
	{
		return _dc.currency();
	}

	@Override public org.drip.param.valuation.CollateralizationParams collateralParams()
	{
		return _dc.collateralParams();
	}

	@Override public double effectiveDF (
		final double dblDate1,
		final double dblDate2)
		throws java.lang.Exception
	{
		return _dc.effectiveDF (dblDate1, dblDate2);
	}

	@Override public double effectiveDF (
		final org.drip.analytics.date.JulianDate dt1,
		final org.drip.analytics.date.JulianDate dt2)
		throws java.lang.Exception
	{
		return _dc.effectiveDF (dt1, dt2);
	}

	@Override public double effectiveDF (
		final String strTenor1,
		final String strTenor2)
		throws java.lang.Exception
	{
		return _dc.effectiveDF (strTenor1, strTenor2);
	}

	@Override public double forward (
		final double dblDate1,
		final double dblDate2)
		throws java.lang.Exception
	{
		return _dc.forward (dblDate1, dblDate2);
	}

	@Override public double forward (
		final String strTenor1,
		final String strTenor2)
		throws java.lang.Exception
	{
		return _dc.forward (strTenor1, strTenor2);
	}

	@Override public double zero (
		final double dblDate)
		throws java.lang.Exception
	{
		return _dc.zero (dblDate);
	}

	@Override public double zero (
		final String strTenor)
		throws java.lang.Exception
	{
		return _dc.zero (strTenor);
	}
}
