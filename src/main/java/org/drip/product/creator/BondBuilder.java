
package org.drip.product.creator;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
 * Copyright (C) 2012 Lakshmi Krishnamurthy
 * Copyright (C) 2011 Lakshmi Krishnamurthy
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
 * BondBuilder contains the suite of helper functions for creating simple fixed/floater bonds, user defined
 * 	bonds, optionally with custom cash flows and embedded option schedules (European or American). It also
 * 	constructs bonds by de-serializing the byte stream.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class BondBuilder {

	/**
	 * Custom Bond Type Simple Fixed
	 */

	public static final int BOND_TYPE_SIMPLE_FIXED = 0;

	/**
	 * Custom Bond Type Simple Floater
	 */

	public static final int BOND_TYPE_SIMPLE_FLOATER = 1;

	/**
	 * Custom Bond Type Simple From Cash flows
	 */

	public static final int BOND_TYPE_SIMPLE_FROM_CF = 2;

	private static final boolean s_bLog = false;

	/**
	 * Create the full generic bond object from the complete set of parameters
	 * 
	 * @param tsyParams Bond Treasury Parameters
	 * @param idParams Bond Identifier Parameters
	 * @param cpnParams Bond Coupon Parameters
	 * @param fltParams Bond Floater Parameters
	 * @param crValParams Bond Credit Valuation Parameters
	 * @param cfteParams Bond Cash-flow Termination Event Parameters
	 * @param periodParams Bond Period Generation Parameters
	 * @param notlParams Bond Notional Parameters
	 * 
	 * @return The Bond object
	 */

	public static final org.drip.product.credit.BondComponent CreateBondFromParams (
		final org.drip.product.params.TreasuryBenchmarks tsyParams,
		final org.drip.product.params.IdentifierSet idParams,
		final org.drip.product.params.CouponSetting cpnParams,
		final org.drip.product.params.FloaterSetting fltParams,
		final org.drip.product.params.QuoteConvention mktConv,
		final org.drip.product.params.CreditSetting crValParams,
		final org.drip.product.params.TerminationSetting cfteParams,
		final org.drip.product.params.BondStream periodParams,
		final org.drip.product.params.NotionalSetting notlParams)
	{
		if (null == idParams || !idParams.validate() || null == cpnParams || !cpnParams.validate() || (null
			!= fltParams && !fltParams.validate()) || null == mktConv || !mktConv.validate() || null ==
				crValParams || !crValParams.validate() || null == cfteParams || !cfteParams.validate() ||
					null == periodParams || null == notlParams || !notlParams.validate())
			return null;

		org.drip.product.credit.BondComponent bond = new org.drip.product.credit.BondComponent();

		bond.setTreasuryBenchmark (tsyParams);

		bond.setIdentifierSet (idParams);

		bond.setCouponSetting (cpnParams);

		bond.setFloaterSetting (fltParams);

		bond.setMarketConvention (mktConv);

		bond.setCreditSetting (crValParams);

		bond.setTerminationSetting (cfteParams);

		bond.setStream (periodParams);

		bond.setNotionalSetting (notlParams);

		return bond;
	}

	/**
	 * Creates a simple fixed bond from parameters
	 * 
	 * @param strName Bond Name
	 * @param strCurrency Bond Currency
	 * @param strCreditCurveName Credit Curve Name
	 * @param dblCoupon Bond Fixed Coupon
	 * @param iFreq Coupon Frequency
	 * @param strDayCount Bond Coupon Day count convention
	 * @param dtEffective Effective Date
	 * @param dtMaturity Maturity Date
	 * @param fsPrincipalOutstanding Outstanding Principal schedule
	 * @param fsCoupon Bond Coupon Schedule
	 * 
	 * @return The Bond Object
	 */

	public static final org.drip.product.credit.BondComponent CreateSimpleFixed (
		final java.lang.String strName,
		final java.lang.String strCurrency,
		final java.lang.String strCreditCurveName,
		final double dblCoupon,
		final int iFreq,
		final java.lang.String strDayCount,
		final org.drip.analytics.date.JulianDate dtEffective,
		final org.drip.analytics.date.JulianDate dtMaturity,
		final org.drip.quant.common.Array2D fsPrincipalOutstanding,
		final org.drip.quant.common.Array2D fsCoupon)
	{
		if (null == strName || strName.isEmpty() || null == strCurrency || strCurrency.isEmpty() || null ==
			dtEffective || null == dtMaturity || !org.drip.quant.common.NumberUtil.IsValid (dblCoupon))
			return null;

		return CreateBondFromParams (null, new org.drip.product.params.IdentifierSet (strName, strName,
			strName, strName), new org.drip.product.params.CouponSetting (fsCoupon, "", dblCoupon,
				java.lang.Double.NaN, java.lang.Double.NaN), null, new
					org.drip.product.params.QuoteConvention (null, "", dtEffective.julian(), 100., 0,
						strCurrency, org.drip.analytics.daycount.Convention.DATE_ROLL_ACTUAL), new
							org.drip.product.params.CreditSetting (30, java.lang.Double.NaN, true,
								strCreditCurveName, true), new org.drip.product.params.TerminationSetting
									(false, false, false), org.drip.product.params.BondStream.Create
										(dtMaturity.julian(), dtEffective.julian(), java.lang.Double.NaN,
											java.lang.Double.NaN, dtEffective.julian(), iFreq, dblCoupon,
												strDayCount, strDayCount, null, null, null, null, null, null,
													null, null, "", false, strCurrency, strCurrency, null,
														!org.drip.quant.common.StringUtil.IsEmpty
															(strCreditCurveName) ?
																org.drip.state.identifier.CreditLabel.Standard
				(strCreditCurveName) : null), new org.drip.product.params.NotionalSetting (100., strCurrency,
					fsPrincipalOutstanding, org.drip.product.params.NotionalSetting.PERIOD_AMORT_AT_START,
						false));
	}

	/**
	 * Create a simple floating rate bond
	 * 
	 * @param strName Bond Name
	 * @param strCurrency Bond Currency
	 * @param strRateIndex Floating Rate Index
	 * @param strCreditCurveName Credit Curve Name
	 * @param dblSpread Bond Floater Spread
	 * @param iFreq Coupon Frequency
	 * @param strDayCount Coupon Day Count Convention
	 * @param dtEffective Effective date
	 * @param dtMaturity Maturity Date
	 * @param fsPrincipalOutstanding Outstanding Principal Schedule
	 * @param fsCoupon Coupon Schedule
	 * 
	 * @return The Bond object
	 */

	public static final org.drip.product.credit.BondComponent CreateSimpleFloater (
		final java.lang.String strName,
		final java.lang.String strCurrency,
		final java.lang.String strRateIndex,
		final java.lang.String strCreditCurveName,
		final double dblSpread,
		final int iFreq,
		final java.lang.String strDayCount,
		final org.drip.analytics.date.JulianDate dtEffective,
		final org.drip.analytics.date.JulianDate dtMaturity,
		final org.drip.quant.common.Array2D fsPrincipalOutstanding,
		final org.drip.quant.common.Array2D fsCoupon)
	{
		if (null == strName || strName.isEmpty() || null == strCurrency || strCurrency.isEmpty() || null ==
			dtEffective || null == dtMaturity || !org.drip.quant.common.NumberUtil.IsValid (dblSpread))
			return null;

		return CreateBondFromParams (null, new org.drip.product.params.IdentifierSet (strName, strName,
			strName, strCurrency), new org.drip.product.params.CouponSetting (fsCoupon, "", dblSpread,
				java.lang.Double.NaN, java.lang.Double.NaN), new org.drip.product.params.FloaterSetting
					(strRateIndex, "", dblSpread, java.lang.Double.NaN), new
						org.drip.product.params.QuoteConvention (null, "", dtEffective.julian(), 100., 0,
							strCurrency, org.drip.analytics.daycount.Convention.DATE_ROLL_ACTUAL), new
								org.drip.product.params.CreditSetting (30, java.lang.Double.NaN, true,
									strCreditCurveName, true), new org.drip.product.params.TerminationSetting
										(false, false, false), org.drip.product.params.BondStream.Create
											(dtMaturity.julian(), dtEffective.julian(), java.lang.Double.NaN,
												java.lang.Double.NaN, dtEffective.julian(), iFreq, dblSpread,
													strDayCount, strDayCount, null, null, null, null, null,
														null, null, null, "", false, strCurrency,
															strCurrency,
																org.drip.state.identifier.ForwardLabel.Standard
			(strRateIndex), null == strCreditCurveName || strCreditCurveName.isEmpty() ? null :
				org.drip.state.identifier.CreditLabel.Standard (strCreditCurveName)), new
					org.drip.product.params.NotionalSetting (100., strCurrency, fsPrincipalOutstanding,
						org.drip.product.params.NotionalSetting.PERIOD_AMORT_AT_START, false));
	}

	/**
	 * Create a bond from custom/user-defined cash flows and coupon conventions
	 * 
	 * @param strName Bond Name
	 * @param dtEffective Effective Date
	 * @param strCurrency Bond Currency
	 * @param strCreditCurveName Credit Curve Name
	 * @param strDC Coupon Day Count Convention
	 * @param iFreq Coupon Frequency
	 * @param adt Array of dates
	 * @param adblCouponAmount Matching array of coupon amounts
	 * @param adblPrincipal Matching array of principal amounts
	 * @param bIsPrincipalPayDown Flag indicating whether principal is pay down or outstanding
	 * 
	 * @return The Bond object
	 */

	public static final org.drip.product.credit.BondComponent CreateBondFromCF (
		final java.lang.String strName,
		final org.drip.analytics.date.JulianDate dtEffective,
		final java.lang.String strCurrency,
		final java.lang.String strCreditCurveName,
		final java.lang.String strDC,
		final int iFreq,
		final org.drip.analytics.date.JulianDate[] adt,
		final double[] adblCouponAmount,
		final double[] adblPrincipal,
		final boolean bIsPrincipalPayDown)
	{
		if (null == adt || 0 == adt.length || null == adblCouponAmount || 0 == adblCouponAmount.length ||
			null == adblPrincipal || 0 == adblPrincipal.length || adt.length != adblCouponAmount.length ||
				adt.length != adblPrincipal.length || null == dtEffective || 0 == iFreq)
			return null;

		java.util.List<org.drip.analytics.cashflow.CompositePeriod> lsCouponPeriod = new
			java.util.ArrayList<org.drip.analytics.cashflow.CompositePeriod>();

		double dblTotalPrincipal = 0.;
		double[] adblDate = new double[adt.length];
		org.drip.product.params.BondStream bfpgp = null;
		double[] adblCouponFactor = new double[adt.length];
		double[] adblNormalizedPrincipal = new double[adt.length];
		double[] adblCurrentCumulativePayDown = new double[adt.length];

		double dblPeriodStart = dtEffective.julian();

		if (bIsPrincipalPayDown) {
			for (int i = 0; i < adt.length; ++i) {
				if (0 == i)
					adblCurrentCumulativePayDown[i] = adblPrincipal[i];
				else
					adblCurrentCumulativePayDown[i] = adblCurrentCumulativePayDown[i - 1] + adblPrincipal[i];

				dblTotalPrincipal += adblPrincipal[i];
			}
		}

		for (int i = 0; i < adt.length; ++i) {
			if (null == adt[i]) {
				System.out.println ("Bad date in BondBuilder.CreateBondFromCF; index " + i);

				return null;
			}

			adblDate[i] = adt[i].julian();

			adblCouponFactor[i] = adblCouponAmount[i] * iFreq;

			if (bIsPrincipalPayDown) {
				if (0. == dblTotalPrincipal)
					adblNormalizedPrincipal[i] = 1.;
				else
					adblNormalizedPrincipal[i] = 1. - (adblCurrentCumulativePayDown[i] / dblTotalPrincipal);

				if (s_bLog)
					System.out.println (adblPrincipal[i] + " | " + adblCurrentCumulativePayDown[i] + " | " +
						adblNormalizedPrincipal[i]);
			} else {
				adblNormalizedPrincipal[i] = adblPrincipal[i] / adblPrincipal[0];

				if (s_bLog) System.out.println (adblNormalizedPrincipal[i]);
			}

			try {
				java.util.List<org.drip.analytics.cashflow.ComposableUnitPeriod> lsCUP = new
					java.util.ArrayList<org.drip.analytics.cashflow.ComposableUnitPeriod>();

				lsCUP.add (new org.drip.analytics.cashflow.ComposableUnitFixedPeriod (dblPeriodStart,
					adblDate[i], new org.drip.param.period.UnitCouponAccrualSetting (iFreq, strDC, false,
						strDC, false, strCurrency, false,
							org.drip.analytics.support.CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC),
					new org.drip.param.period.ComposableFixedUnitSetting ((12 / iFreq) + "M",
						org.drip.analytics.support.CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR, null,
							adblCouponAmount[i], 0., strCurrency)));

				lsCouponPeriod.add (new org.drip.analytics.cashflow.CompositeFixedPeriod (new
					org.drip.param.period.CompositePeriodSetting (iFreq, (12 / iFreq) + "M", strCurrency,
						null, adblPrincipal[i], null, null, null, null), lsCUP));
			} catch (java.lang.Exception e) {
				e.printStackTrace();

				return null;
			}

			dblPeriodStart = adblDate[i];
		}

		try {
			bfpgp = new org.drip.product.params.BondStream (lsCouponPeriod, java.lang.Double.NaN,
				"MATURITY_TYPE_REGULAR");
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		return CreateBondFromParams (null, new org.drip.product.params.IdentifierSet (strName, strName,
			strName, strCurrency), new org.drip.product.params.CouponSetting
				(org.drip.quant.common.Array2D.FromArray (adblDate, adblCouponFactor), "", 1.,
					java.lang.Double.NaN, java.lang.Double.NaN), null, new
						org.drip.product.params.QuoteConvention (null, "", dtEffective.julian(), 100., 0,
							strCurrency, org.drip.analytics.daycount.Convention.DATE_ROLL_ACTUAL), new
								org.drip.product.params.CreditSetting (30, java.lang.Double.NaN, true,
									strCreditCurveName, true), new org.drip.product.params.TerminationSetting
										(false, false, false), bfpgp, new
											org.drip.product.params.NotionalSetting (100., strCurrency,
												org.drip.quant.common.Array2D.FromArray (adblDate,
													adblNormalizedPrincipal),
														org.drip.product.params.NotionalSetting.PERIOD_AMORT_AT_START,
			false));
	}
}
