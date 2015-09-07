
package tester.functional;

import org.testng.annotations.Test;

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
 * ProductTestSuite tests more-or-less the full suite of the product valuation functionality exposed in
 * 	CreditAnalytics API. The following variants are tested.
 * 	- Full suite of products - rates, credit and FX, both components and baskets.
 * 	- Base flat/tenor bumped scenario tests.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class ProductTestSuite {
	/*
	 * Test Mode
	 */

	public static final int TM_BASE = 0;
	public static final int TM_IR_UP01 = 1;
	public static final int TM_IR_DN01 = 2;
	public static final int TM_IR_SET_UP01 = 4;
	public static final int TM_IR_SET_DN01 = 8;
	public static final int TM_IR_TENOR_UP01 = 16;
	public static final int TM_IR_TENOR_DN01 = 32;
	public static final int TM_CC_UP01 = 64;
	public static final int TM_CC_DN01 = 128;
	public static final int TM_CC_SET_UP01 = 256;
	public static final int TM_CC_SET_DN01 = 512;
	public static final int TM_CC_TENOR_UP01 = 1024;
	public static final int TM_CC_TENOR_DN01 = 2048;
	public static final int TM_RR_UP01 = 4096;
	public static final int TM_RR_DN01 = 8192;
	public static final int TM_RR_SET_UP01 = 16384;
	public static final int TM_RR_SET_DN01 = 32768;
	public static final int TM_TSY_UP01 = 65536;
	public static final int TM_TSY_DN01 = 131072;
	public static final int TM_TSY_SET_UP01 = 262144;
	public static final int TM_TSY_SET_DN01 = 524288;
	public static final int TM_TSY_TENOR_UP01 = 1048576;
	public static final int TM_TSY_TENOR_DN01 = 2097152;

	/*
	 * Test Detail
	 */

	public static final int TD_SUCCESS_FAILURE = 0;
	public static final int TD_BRIEF = 1;
	public static final int TD_DETAILED = 2;

	/*
	 * Test Product
	 */

	public static final int TP_NONE = 0;
	public static final int TP_CASH = 1;
	public static final int TP_EDF = 2;
	public static final int TP_IRS = 4;
	public static final int TP_CDS = 8;
	public static final int TP_FIXED_BOND = 16;
	public static final int TP_CDX = 32;
	public static final int TP_BASKET_BOND = 64;

	private static final org.drip.product.credit.BondComponent CreateSimpleBond (
		final String strName,
		final org.drip.analytics.date.JulianDate dtEffective,
		final org.drip.analytics.date.JulianDate dtMaturity,
		final double dblCoupon,
		final String strCurrency,
		final String strCC,
		final String strTicker)
		throws java.lang.Exception
	{
		org.drip.product.credit.BondComponent simpleBond = new org.drip.product.credit.BondComponent();

		org.drip.product.params.IdentifierSet idParams = new
			org.drip.product.params.IdentifierSet (strName, strName, strName, strTicker);

		if (!idParams.validate()) {
			System.out.println ("ID params for " + strName + " could not be validated!");

			return null;
		}

		simpleBond.setIdentifierSet (idParams);

		org.drip.product.params.CouponSetting cpnParams = new org.drip.product.params.CouponSetting
			(null, "", dblCoupon, java.lang.Double.NaN, java.lang.Double.NaN);

		if (!cpnParams.validate()) {
			System.out.println ("Cpn params for " + strName + " could not be validated!");

			return null;
		}

		simpleBond.setCouponSetting (cpnParams);

		simpleBond.setFloaterSetting (null);

		org.drip.product.params.QuoteConvention mktConv = new org.drip.product.params.QuoteConvention (null,
			"", dtEffective.julian(), 1., 0, strCurrency,
				org.drip.analytics.daycount.Convention.DATE_ROLL_MODIFIED_FOLLOWING);

		if (!mktConv.validate()) {
			System.out.println ("IR Val params for " + strName + " could not be validated!");

			return null;
		}

		simpleBond.setMarketConvention (mktConv);

		org.drip.product.params.CreditSetting crValParams = new
			org.drip.product.params.CreditSetting (30, 0., true, strCC, true);

		if (!crValParams.validate()) {
			System.out.println ("CR Val params for " + strName + " could not be validated!");

			return null;
		}

		simpleBond.setCreditSetting (crValParams);

		org.drip.product.params.TerminationSetting cfteParams = new
			org.drip.product.params.TerminationSetting (false, false, false);

		if (!cfteParams.validate()) {
			System.out.println ("CFTE params for " + strName + " could not be validated!");

			return null;
		}

		simpleBond.setTerminationSetting (cfteParams);

		org.drip.product.params.BondStream periodParams = org.drip.product.params.BondStream.Create
			(dtMaturity.julian(), dtEffective.julian(), java.lang.Double.NaN, java.lang.Double.NaN,
				java.lang.Double.NaN, 2, dblCoupon, "30/360", "30/360", null, null, null, null, null, null,
					null, null, "", false, strCurrency, strCurrency, null,
						org.drip.state.identifier.CreditLabel.Standard ("IBM"));

		simpleBond.setStream (periodParams);

		org.drip.product.params.NotionalSetting notlParams = new org.drip.product.params.NotionalSetting
			(100., strCurrency, null, org.drip.product.params.NotionalSetting.PERIOD_AMORT_AT_START, false);

		if (!notlParams.validate()) {
			System.out.println ("Notional params for " + strName + " could not be validated!");

			return null;
		}

		simpleBond.setNotionalSetting (notlParams);

		return simpleBond;
	}

	private static final org.drip.product.credit.BondComponent CreateTSYBond (
		final String strName,
		final double dblCoupon,
		final org.drip.analytics.date.JulianDate dt,
		int iNumYears)
		throws java.lang.Exception
	{
		org.drip.product.credit.BondComponent bondTSY = new org.drip.product.credit.BondComponent();

		org.drip.product.params.IdentifierSet idParams = new
			org.drip.product.params.IdentifierSet (strName, strName, strName, "UST");

		if (!idParams.validate()) {
			System.out.println ("ID params for " + strName + " could not be validated!");

			return null;
		}

		bondTSY.setIdentifierSet (idParams);

		org.drip.product.params.CouponSetting cpnParams = new org.drip.product.params.CouponSetting
			(null, "", dblCoupon, java.lang.Double.NaN, java.lang.Double.NaN);

		if (!cpnParams.validate()) {
			System.out.println ("Cpn params for " + strName + " could not be validated!");

			return null;
		}

		bondTSY.setCouponSetting (cpnParams);

		bondTSY.setFloaterSetting (null);

		org.drip.product.params.QuoteConvention mktConv = new
			org.drip.product.params.QuoteConvention (null, "", dt.julian(), 1., 3, "USD",
				org.drip.analytics.daycount.Convention.DATE_ROLL_MODIFIED_FOLLOWING);

		if (!mktConv.validate()) {
			System.out.println ("Valuation params for " + strName + " could not be validated!");

			return null;
		}

		bondTSY.setMarketConvention (mktConv);

		org.drip.product.params.CreditSetting crValParams = new org.drip.product.params.CreditSetting (30,
			java.lang.Double.NaN, false, "", true);

		if (!crValParams.validate()) {
			System.out.println ("CR Val params for " + strName + " could not be validated!");

			return null;
		}

		bondTSY.setCreditSetting (crValParams);

		org.drip.product.params.TerminationSetting cfteParams = new
			org.drip.product.params.TerminationSetting (false, false, false);

		if (!cfteParams.validate()) {
			System.out.println ("CFTE params for " + strName + " could not be validated!");

			return null;
		}

		bondTSY.setTerminationSetting (cfteParams);

		org.drip.product.params.BondStream periodParams = org.drip.product.params.BondStream.Create
			(dt.addYears (iNumYears).julian(), dt.julian(), java.lang.Double.NaN, dt.julian(), dt.julian(),
				2, dblCoupon, "30/360", "30/360", null, null, null, null, null, null, null, null, "", false,
					"USD", "USD", null, org.drip.state.identifier.CreditLabel.Standard ("IBM"));

		bondTSY.setStream (periodParams);

		org.drip.product.params.NotionalSetting notlParams = new org.drip.product.params.NotionalSetting
			(100., "USD", null, org.drip.product.params.NotionalSetting.PERIOD_AMORT_AT_START, false);

		if (!notlParams.validate()) {
			System.out.println ("Notional params for " + strName + " could not be validated!");

			return null;
		}

		bondTSY.setNotionalSetting (notlParams);

		return bondTSY;
	}

	private static final boolean addTSYToMPC (
		final org.drip.param.definition.ScenarioMarketParams mpc)
	{
		try {
			org.drip.param.definition.ProductQuote cq2YON =
				org.drip.param.creator.QuoteBuilder.CreateProductQuote();

			cq2YON.addQuote ("Yield", org.drip.param.creator.QuoteBuilder.CreateQuote ("mid", 0.02,
				java.lang.Double.NaN), true);

			mpc.addTSYQuote ("2YON", cq2YON);

			org.drip.param.definition.ProductQuote cq3YON =
				org.drip.param.creator.QuoteBuilder.CreateProductQuote();

			cq3YON.addQuote ("Yield", org.drip.param.creator.QuoteBuilder.CreateQuote ("mid", 0.025,
				java.lang.Double.NaN), true);

			mpc.addTSYQuote ("3YON", cq3YON);

			org.drip.param.definition.ProductQuote cq5YON =
				org.drip.param.creator.QuoteBuilder.CreateProductQuote();

			cq5YON.addQuote ("Yield", org.drip.param.creator.QuoteBuilder.CreateQuote ("mid", 0.03,
				java.lang.Double.NaN), true);

			mpc.addTSYQuote ("5YON", cq5YON);

			org.drip.param.definition.ProductQuote cq7YON =
				org.drip.param.creator.QuoteBuilder.CreateProductQuote();

			cq7YON.addQuote ("Yield", org.drip.param.creator.QuoteBuilder.CreateQuote ("mid", 0.0325,
				java.lang.Double.NaN), true);

			mpc.addTSYQuote ("7YON", cq7YON);

			org.drip.param.definition.ProductQuote cq10YON =
				org.drip.param.creator.QuoteBuilder.CreateProductQuote();

			cq10YON.addQuote ("Yield", org.drip.param.creator.QuoteBuilder.CreateQuote ("mid", 0.0375,
				java.lang.Double.NaN), true);

			mpc.addTSYQuote ("10YON", cq10YON);

			org.drip.param.definition.ProductQuote cq30YON =
				org.drip.param.creator.QuoteBuilder.CreateProductQuote();

			cq30YON.addQuote ("Yield", org.drip.param.creator.QuoteBuilder.CreateQuote ("mid", 0.04,
				java.lang.Double.NaN), true);

			mpc.addTSYQuote ("30YON", cq30YON);

			org.drip.param.definition.ProductQuote cqBRA_5_00_21 =
				org.drip.param.creator.QuoteBuilder.CreateProductQuote();

			org.drip.param.definition.Quote qPxBRA_5_00_21 = org.drip.param.creator.QuoteBuilder.CreateQuote
				("bid", 0.900, java.lang.Double.NaN);

			qPxBRA_5_00_21.setSide ("mid", 0.900, java.lang.Double.NaN);

			qPxBRA_5_00_21.setSide ("ask", 0.900, java.lang.Double.NaN);

			cqBRA_5_00_21.addQuote ("Price", qPxBRA_5_00_21, true);

			mpc.addComponentQuote ("BRA_5.00_21", cqBRA_5_00_21);

			org.drip.param.definition.ProductQuote cqTESTCDS =
				org.drip.param.creator.QuoteBuilder.CreateProductQuote();

			org.drip.param.definition.Quote qTESTCDS = org.drip.param.creator.QuoteBuilder.CreateQuote
				("mid", 101., java.lang.Double.NaN);

			cqTESTCDS.addQuote ("CleanPrice", qTESTCDS, true);

			mpc.addComponentQuote ("TESTCDS", cqTESTCDS);
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}

	private static final void BuildTSYCurve (
		final org.drip.analytics.date.JulianDate dt,
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final int iTestMode,
		final int iTestDetail)
		throws java.lang.Exception
	{
		System.out.println ("--------\nStarting TSY DC tests\n--------");

		int NUM_TSY_CALIB_INSTR = 6;
		double adblRate[] = new double[NUM_TSY_CALIB_INSTR];
		double adblCompCalibValue[] = new double[NUM_TSY_CALIB_INSTR];
		String astrCalibMeasure[] = new String[NUM_TSY_CALIB_INSTR];
		org.drip.product.definition.CalibratableFixedIncomeComponent aCompCalib[] = new
			org.drip.product.definition.CalibratableFixedIncomeComponent[NUM_TSY_CALIB_INSTR];
		adblCompCalibValue[0] = .0200;
		adblCompCalibValue[1] = .0250;
		adblCompCalibValue[2] = .0300;
		adblCompCalibValue[3] = .0325;
		adblCompCalibValue[4] = .0375;
		adblCompCalibValue[5] = .0400;

		long lStart = System.nanoTime();

		aCompCalib[0] = CreateTSYBond ("USD2YON", 0.02, dt, 2);

		aCompCalib[1] = CreateTSYBond ("USD3YON", 0.025, dt, 3);

		aCompCalib[2] = CreateTSYBond ("USD5YON", 0.03, dt, 5);

		aCompCalib[3] = CreateTSYBond ("USD7YON", 0.0325, dt, 7);

		aCompCalib[4] = CreateTSYBond ("USD10YON", 0.0375, dt, 10);

		aCompCalib[5] = CreateTSYBond ("USD30YON", 0.04, dt, 30);

		for (int i = 0; i < NUM_TSY_CALIB_INSTR; ++i) {
			adblRate[i] = 0.02;
			astrCalibMeasure[i] = "Yield";
		}

		org.drip.param.definition.ScenarioDiscountCurve irscUSDTSY =
			org.drip.param.creator.ScenarioDiscountCurveBuilder.FromIRCSG ("USDTSY",
				org.drip.state.creator.DiscountCurveBuilder.BOOTSTRAP_MODE_CONSTANT_FORWARD, aCompCalib);

		irscUSDTSY.cookScenarioDC (new org.drip.param.valuation.ValuationParams (dt, dt, "USD"), null,
			astrCalibMeasure, adblCompCalibValue, 0.0001, mpc.fixings(), null, 15);

		System.out.println ("TSYDC Cook in: " + (System.nanoTime() - lStart) * 1.e-09 + " sec");

		mpc.addScenarioDiscountCurve ("USDTSY", irscUSDTSY);

		org.drip.analytics.rates.DiscountCurve dcBaseTSY = mpc.scenarioMarketParams (aCompCalib[0],
			"Base").fundingCurve (org.drip.state.identifier.FundingLabel.Standard
				(aCompCalib[0].payCurrency()));

		if (TD_SUCCESS_FAILURE == iTestDetail)
			System.out.println ("Base TSY DC build: " + (null == dcBaseTSY ? "Failure" : "Success"));
		else if (TD_BRIEF == iTestDetail)
			System.out.println ("Base TSY: " + dcBaseTSY.toString());
		else if (TD_DETAILED == iTestDetail) {
			System.out.println ("Base TSY DC build: " + (null == dcBaseTSY ? "Failure" : "Success"));

			System.out.println ("\n\n------------------\nTesting Base TSY DC Curve\n--------\n");

			for (int i = 0; i < aCompCalib.length; ++i) {
				System.out.println ("TSYRate[" + i + "] = " + dcBaseTSY.zero
					(aCompCalib[i].maturityDate().julian()));

				System.out.println (astrCalibMeasure[i] + "[" + i + "] = " + aCompCalib[i].measureValue
					(new org.drip.param.valuation.ValuationParams (dt, dt, "USD"), null,
						org.drip.param.creator.MarketParamsBuilder.Create (dcBaseTSY, null, null,
							null, null, null, mpc.fixings()), null, astrCalibMeasure[i]));
			}
		}

		if (0 != (TM_TSY_UP01 & iTestMode)) {
			org.drip.analytics.rates.DiscountCurve dcBumpUp = mpc.scenarioMarketParams (aCompCalib[0],
				"FlatIRBumpUp").fundingCurve (org.drip.state.identifier.FundingLabel.Standard
					(aCompCalib[0].payCurrency()));

			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bump Up TSY DC build: " + (null == dcBumpUp ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bump Up TSY Curve: " + dcBumpUp.toString());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("Bump Up TSY DC build: " + (null == dcBumpUp ? "Failure" : "Success"));

				System.out.println ("Bump Up TSY DC build: " + dcBumpUp.toString());

				System.out.println
					("\n\n----------\nMeasures for Parallel Bump Up TSY DC Curve\n------\n");

				for (int i = 0; i < aCompCalib.length; ++i)
					System.out.println (astrCalibMeasure[i] + "[" + i + "] = " + aCompCalib[i].measureValue
						(new org.drip.param.valuation.ValuationParams (dt, dt, "USD"), null,
							org.drip.param.creator.MarketParamsBuilder.Create (dcBumpUp, null, null, null,
								null, null, mpc.fixings()), null, astrCalibMeasure[i]));
			}
		}

		if (0 != (TM_TSY_DN01 & iTestMode)) {
			org.drip.analytics.rates.DiscountCurve dcBumpDn = mpc.scenarioMarketParams (aCompCalib[0],
				"FlatIRBumpDn").fundingCurve (org.drip.state.identifier.FundingLabel.Standard
					(aCompCalib[0].payCurrency()));

			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bump Dn TSY DC build: " + (null == dcBumpDn ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bump Dn TSY DC: " + dcBumpDn.toString());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("\n\n---------\nMeasures for Parallel Bump Dn TSY Curve\n------\n");

				for (int i = 0; i < aCompCalib.length; ++i)
					System.out.println (astrCalibMeasure[i] + "[" + i + "] = " + aCompCalib[i].measureValue
						(new org.drip.param.valuation.ValuationParams (dt, dt, "USD"), null,
							org.drip.param.creator.MarketParamsBuilder.Create (dcBumpDn, null, null, null,
								null, null, mpc.fixings()), null, astrCalibMeasure[i]));
			}
		}

		if (0 != (TM_TSY_TENOR_UP01 & iTestMode)) {
			org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.param.market.CurveSurfaceQuoteSet>
				mapCSQSDCUp = mpc.fundingTenorMarketParams (aCompCalib[0], true);

			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Tenor Bump Up TSY build: " + (null == mapCSQSDCUp ? "Failure" :
					"Success"));
			else if (TD_BRIEF == iTestDetail)
				for (java.util.Map.Entry<String, org.drip.param.market.CurveSurfaceQuoteSet>
					meCSQS : mapCSQSDCUp.entrySet())
					System.out.println (meCSQS.getKey() + meCSQS.getValue().fundingCurve
						(org.drip.state.identifier.FundingLabel.Standard
							(aCompCalib[0].payCurrency())).toString());
			else if (TD_DETAILED == iTestDetail) {
				for (java.util.Map.Entry<String, org.drip.param.market.CurveSurfaceQuoteSet>
					meCSQS : mapCSQSDCUp.entrySet()) {
					System.out.println ("\n\n------\nMeasures for Tenor TSY Bump Up\n--------\n");

					for (int i = 0; i < aCompCalib.length; ++i)
						System.out.println ("Tenor: " + meCSQS.getKey() + "; " + astrCalibMeasure[i] + "[" +
							aCompCalib[i].primaryCode() + "] = " + aCompCalib[i].measureValue (new
								org.drip.param.valuation.ValuationParams (dt, dt, "USD"), null,
									org.drip.param.creator.MarketParamsBuilder.Create
										(meCSQS.getValue().fundingCurve
											(org.drip.state.identifier.FundingLabel.Standard
												(aCompCalib[i].payCurrency())), null, null, null, null, null,
													mpc.fixings()), null, astrCalibMeasure[i]));
				}
			}
		}

		if (0 != (TM_TSY_TENOR_DN01 & iTestMode)) {
			org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.param.market.CurveSurfaceQuoteSet>
				mapCSQSDCDn = mpc.fundingTenorMarketParams (aCompCalib[0], true);

			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Tenor Bump Dn TSY build: " + (null == mapCSQSDCDn ? "Failure" :
					"Success"));
			else if (TD_BRIEF == iTestDetail) {
				for (java.util.Map.Entry<String, org.drip.param.market.CurveSurfaceQuoteSet>
					meCSQS : mapCSQSDCDn.entrySet())
					System.out.println (meCSQS.getKey() + meCSQS.getValue().toString());
			} else if (TD_DETAILED == iTestDetail) {
				for (java.util.Map.Entry<String, org.drip.param.market.CurveSurfaceQuoteSet>
					meCSQS : mapCSQSDCDn.entrySet()) {
					System.out.println ("\n\n------\nMeasures for Tenor TSY Bump Dn\n--------\n");

					for (int i = 0; i < aCompCalib.length; ++i)
						System.out.println ("Tenor: " + meCSQS.getKey() + "; " + astrCalibMeasure[i] + "[" +
							aCompCalib[i].primaryCode() + "] = " + aCompCalib[i].measureValue (new
								org.drip.param.valuation.ValuationParams (dt, dt, "USD"), null,
									org.drip.param.creator.MarketParamsBuilder.Create
										(meCSQS.getValue().fundingCurve
											(org.drip.state.identifier.FundingLabel.Standard
												(aCompCalib[i].payCurrency())), null, null, null, null, null,
													mpc.fixings()), null, astrCalibMeasure[i]));
				}
			}
		}
	}

	private static void testDC (
		org.drip.param.definition.ScenarioMarketParams mpc,
		final org.drip.analytics.date.JulianDate dt,
		final int iTestMode,
		final int iTestDetail)
		throws java.lang.Exception
	{
		System.out.println ("--------\nStarting DC tests\n--------");

		int NUM_DC_INSTR = 30;
		double adblDate[] = new double[NUM_DC_INSTR];
		double adblRate[] = new double[NUM_DC_INSTR];
		double adblCompCalibValue[] = new double[NUM_DC_INSTR];
		String astrCalibMeasure[] = new String[NUM_DC_INSTR];
		org.drip.product.definition.CalibratableFixedIncomeComponent aCompCalib[] = new
			org.drip.product.definition.CalibratableFixedIncomeComponent[NUM_DC_INSTR];

		// First 7 instruments - cash calibration

		adblDate[0] = dt.addDays (3).julian(); // ON

		adblDate[1] = dt.addDays (4).julian(); // 1D (TN)

		adblDate[2] = dt.addDays (9).julian(); // 1W

		adblDate[3] = dt.addDays (16).julian(); // 2W

		adblDate[4] = dt.addDays (32).julian(); // 1M

		adblDate[5] = dt.addDays (62).julian(); // 2M

		adblDate[6] = dt.addDays (92).julian(); // 3M

		adblCompCalibValue[0] = .0013;
		adblCompCalibValue[1] = .0017;
		adblCompCalibValue[2] = .0017;
		adblCompCalibValue[3] = .0018;
		adblCompCalibValue[4] = .0020;
		adblCompCalibValue[5] = .0023;
		adblCompCalibValue[6] = .0026;

		for (int i = 0; i < 7; ++i) {
			adblRate[i] = 0.02;
			astrCalibMeasure[i] = "Rate";

			aCompCalib[i] = org.drip.product.creator.SingleStreamComponentBuilder.Deposit (dt.addDays (2),
				new org.drip.analytics.date.JulianDate (adblDate[i]),
					org.drip.state.identifier.ForwardLabel.Create ("USD", "3M"));
		}

		// Next 8 instruments - EDF calibration

		org.drip.analytics.date.JulianDate dtEDFStart = dt;
		adblCompCalibValue[7] = .0027;
		adblCompCalibValue[8] = .0032;
		adblCompCalibValue[9] = .0041;
		adblCompCalibValue[10] = .0054;
		adblCompCalibValue[11] = .0077;
		adblCompCalibValue[12] = .0104;
		adblCompCalibValue[13] = .0134;
		adblCompCalibValue[14] = .0160;

		org.drip.product.definition.CalibratableFixedIncomeComponent[] aEDF =
			org.drip.product.creator.SingleStreamComponentBuilder.FuturesPack (dt, 8, "USD");

		for (int i = 0; i < 8; ++i) {
			adblRate[i + 7] = 0.02;
			aCompCalib[i + 7] = aEDF[i];
			astrCalibMeasure[i + 7] = "Rate";

			adblDate[i + 7] = dtEDFStart.addDays ((i + 1) * 91).julian();
		}

		// Final 15 instruments - IRS calibration

		adblDate[15] = dt.addDays ((int)(365.25 * 4 + 2)).julian(); // 4Y

		adblDate[16] = dt.addDays ((int)(365.25 * 5 + 2)).julian(); // 5Y

		adblDate[17] = dt.addDays ((int)(365.25 * 6 + 2)).julian(); // 6Y

		adblDate[18] = dt.addDays ((int)(365.25 * 7 + 2)).julian(); // 7Y

		adblDate[19] = dt.addDays ((int)(365.25 * 8 + 2)).julian(); // 8Y

		adblDate[20] = dt.addDays ((int)(365.25 * 9 + 2)).julian(); // 9Y

		adblDate[21] = dt.addDays ((int)(365.25 * 10 + 2)).julian(); // 10Y

		adblDate[22] = dt.addDays ((int)(365.25 * 11 + 2)).julian(); // 11Y

		adblDate[23] = dt.addDays ((int)(365.25 * 12 + 2)).julian(); // 12Y

		adblDate[24] = dt.addDays ((int)(365.25 * 15 + 2)).julian(); // 15Y

		adblDate[25] = dt.addDays ((int)(365.25 * 20 + 2)).julian(); // 20Y

		adblDate[26] = dt.addDays ((int)(365.25 * 25 + 2)).julian(); // 25Y

		adblDate[27] = dt.addDays ((int)(365.25 * 30 + 2)).julian(); // 30Y

		adblDate[28] = dt.addDays ((int)(365.25 * 40 + 2)).julian(); // 40Y

		adblDate[29] = dt.addDays ((int)(365.25 * 50 + 2)).julian(); // 50Y

		adblCompCalibValue[15] = .0166;
		adblCompCalibValue[16] = .0206;
		adblCompCalibValue[17] = .0241;
		adblCompCalibValue[18] = .0269;
		adblCompCalibValue[19] = .0292;
		adblCompCalibValue[20] = .0311;
		adblCompCalibValue[21] = .0326;
		adblCompCalibValue[22] = .0340;
		adblCompCalibValue[23] = .0351;
		adblCompCalibValue[24] = .0375;
		adblCompCalibValue[25] = .0393;
		adblCompCalibValue[26] = .0402;
		adblCompCalibValue[27] = .0407;
		adblCompCalibValue[28] = .0409;
		adblCompCalibValue[29] = .0409;

		org.drip.param.period.UnitCouponAccrualSetting ucasFixed = new
			org.drip.param.period.UnitCouponAccrualSetting (2, "Act/360", false, "Act/360", false, "USD",
				true, org.drip.analytics.support.CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC);

		org.drip.param.period.ComposableFloatingUnitSetting cfusFloating = new
			org.drip.param.period.ComposableFloatingUnitSetting ("3M",
				org.drip.analytics.support.CompositePeriodBuilder.EDGE_DATE_SEQUENCE_SINGLE, null,
					org.drip.state.identifier.ForwardLabel.Standard ("USD-3M"),
						org.drip.analytics.support.CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE, 0.);

		org.drip.param.period.ComposableFixedUnitSetting cfusFixed = new
			org.drip.param.period.ComposableFixedUnitSetting ("6M",
				org.drip.analytics.support.CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR, null, 0., 0.,
					"USD");

		org.drip.param.period.CompositePeriodSetting cpsFloating = new
			org.drip.param.period.CompositePeriodSetting (4, "3M", "USD", null, -1., null, null, null, null);

		org.drip.param.period.CompositePeriodSetting cpsFixed = new
			org.drip.param.period.CompositePeriodSetting (2, "6M", "USD", null, 1., null, null, null, null);

		for (int i = 0; i < 15; ++i) {
			adblRate[i + 15] = 0.02;
			adblRate[i + 15] = 0.05;
			astrCalibMeasure[i + 15] = "Rate";

			java.util.List<java.lang.Double> lsFixedStreamEdgeDate =
				org.drip.analytics.support.CompositePeriodBuilder.BackwardEdgeDates (dt, new
					org.drip.analytics.date.JulianDate (adblDate[i + 15]), "6M", null,
						org.drip.analytics.support.CompositePeriodBuilder.SHORT_STUB);

			java.util.List<java.lang.Double> lsFloatingStreamEdgeDate =
				org.drip.analytics.support.CompositePeriodBuilder.BackwardEdgeDates (dt, new
					org.drip.analytics.date.JulianDate (adblDate[i + 15]), "3M", null,
						org.drip.analytics.support.CompositePeriodBuilder.SHORT_STUB);

			org.drip.product.rates.Stream floatingStream = new org.drip.product.rates.Stream
				(org.drip.analytics.support.CompositePeriodBuilder.FloatingCompositeUnit
					(lsFloatingStreamEdgeDate, cpsFloating, cfusFloating));

			org.drip.product.rates.Stream fixedStream = new org.drip.product.rates.Stream
				(org.drip.analytics.support.CompositePeriodBuilder.FixedCompositeUnit (lsFixedStreamEdgeDate,
					cpsFixed, ucasFixed, cfusFixed));

			aCompCalib[i + 15] = new org.drip.product.rates.FixFloatComponent (fixedStream, floatingStream,
				null);
		}

		mpc.addFixing (dt.addDays (2), org.drip.state.identifier.ForwardLabel.Standard ("USD-LIBOR-6M"),
			0.0042);

		long lStart = System.nanoTime();

		org.drip.param.definition.ScenarioDiscountCurve irscUSD =
			org.drip.param.creator.ScenarioDiscountCurveBuilder.FromIRCSG ("USD",
				org.drip.state.creator.DiscountCurveBuilder.BOOTSTRAP_MODE_CONSTANT_FORWARD, aCompCalib);

		irscUSD.cookScenarioDC (new org.drip.param.valuation.ValuationParams (dt, dt, "USD"), null,
			astrCalibMeasure, adblCompCalibValue, 0.0001, mpc.fixings(), null, 15);

		System.out.println ("DC Cook in: " + (System.nanoTime() - lStart) * 1.e-09 + " sec");

		mpc.addScenarioDiscountCurve ("USD", irscUSD);

		addTSYToMPC (mpc);

		org.drip.analytics.rates.DiscountCurve dcBase = mpc.scenarioMarketParams (aCompCalib[0],
			"Base").fundingCurve (org.drip.state.identifier.FundingLabel.Standard
				(aCompCalib[0].payCurrency()));

		if (TD_SUCCESS_FAILURE == iTestDetail)
			System.out.println ("Base DC build: " + (null == dcBase ? "Failure" : "Success"));
		else if (TD_BRIEF == iTestDetail)
			System.out.println ("Base: " + dcBase.toString());
		else if (TD_DETAILED == iTestDetail) {
			System.out.println ("Base DC build: " + (null == dcBase ? "Failure" : "Success"));

			System.out.println ("\n\n------------------\nTesting Base DC Curve\n--------\n");

			for (int i = 0; i < aCompCalib.length; ++i)
				System.out.println (astrCalibMeasure[i] + "[" + i + "] = " + aCompCalib[i].measureValue (new
					org.drip.param.valuation.ValuationParams (dt, dt, "USD"), null,
						org.drip.param.creator.MarketParamsBuilder.Create (dcBase, null, null, null, null,
							null, mpc.fixings()), null, astrCalibMeasure[i]));
		}

		if (0 != (TM_IR_UP01 & iTestMode)) {
			org.drip.analytics.rates.DiscountCurve dcBumpUp = mpc.scenarioMarketParams (aCompCalib[0],
				"FlatIRBumpUp").fundingCurve (org.drip.state.identifier.FundingLabel.Standard
					(aCompCalib[0].payCurrency()));

			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bump Up DC build: " + (null == dcBumpUp ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Base: " + dcBumpUp.toString());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("Bump Up DC build: " + (null == dcBumpUp ? "Failure" : "Success"));

				System.out.println ("Base: " + dcBumpUp.toString());

				System.out.println ("\n\n----------\nMeasures for Parallel Bump Up DC Curve\n------\n");

				for (int i = 0; i < aCompCalib.length; ++i)
					System.out.println (astrCalibMeasure[i] + "[" + i + "] = " + aCompCalib[i].measureValue
						(new org.drip.param.valuation.ValuationParams (dt, dt, "USD"), null,
							org.drip.param.creator.MarketParamsBuilder.Create (dcBumpUp, null, null, null,
								null, null, mpc.fixings()), null, astrCalibMeasure[i]));
			}
		}

		if (0 != (TM_IR_DN01 & iTestMode)) {
			org.drip.analytics.rates.DiscountCurve dcBumpDn = mpc.scenarioMarketParams (aCompCalib[0],
				"FlatIRBumpDn").fundingCurve (org.drip.state.identifier.FundingLabel.Standard
					(aCompCalib[0].payCurrency()));

			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bump Dn DC build: " + (null == dcBumpDn ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bump Dn DC: " + dcBumpDn.toString());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("\n\n---------\nMeasures for Parallel  Bump Dn IR Curve\n------\n");

				for (int i = 0; i < aCompCalib.length; ++i)
					System.out.println (astrCalibMeasure[i] + "[" + i + "] = " + aCompCalib[i].measureValue
						(new org.drip.param.valuation.ValuationParams (dt, dt, "USD"), null,
							org.drip.param.creator.MarketParamsBuilder.Create (dcBumpDn, null, null, null,
								null, null, mpc.fixings()), null, astrCalibMeasure[i]));
			}
		}

		if (0 != (TM_IR_TENOR_UP01 & iTestMode)) {
			org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.param.market.CurveSurfaceQuoteSet>
				mapCSQSDCUp = mpc.fundingTenorMarketParams (aCompCalib[0], true);

			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Tenor Bump Up DC build: " + (null == mapCSQSDCUp ? "Failure" :
					"Success"));
			else if (TD_BRIEF == iTestDetail)
				for (java.util.Map.Entry<String, org.drip.param.market.CurveSurfaceQuoteSet>
					meCSCQ : mapCSQSDCUp.entrySet())
					System.out.println (meCSCQ.getKey() + meCSCQ.getValue().fundingCurve
						(org.drip.state.identifier.FundingLabel.Standard
							(aCompCalib[0].payCurrency())).toString());
			else if (TD_DETAILED == iTestDetail) {
				for (java.util.Map.Entry<String, org.drip.param.market.CurveSurfaceQuoteSet>
				meCSCQ : mapCSQSDCUp.entrySet()) {
					System.out.println ("\n\n------\nMeasures for Tenor IR Bump Up\n--------\n");

					for (int i = 0; i < aCompCalib.length; ++i)
						System.out.println ("Tenor: " + meCSCQ.getKey() + "; " + astrCalibMeasure[i] + "[" +
							aCompCalib[i].primaryCode() + "] = " + aCompCalib[i].measureValue (new
								org.drip.param.valuation.ValuationParams (dt, dt, "USD"), null,
									org.drip.param.creator.MarketParamsBuilder.Create
										(meCSCQ.getValue().fundingCurve
											(org.drip.state.identifier.FundingLabel.Standard
												(aCompCalib[0].payCurrency())), null, null, null, null, null,
													mpc.fixings()), null, astrCalibMeasure[i]));
				}
			}
		}

		if (0 != (TM_IR_TENOR_DN01 & iTestMode)) {
			org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.param.market.CurveSurfaceQuoteSet>
				mapCSQSDCDn = mpc.fundingTenorMarketParams (aCompCalib[0], true);

			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Tenor Bump Dn DC build: " + (null == mapCSQSDCDn ? "Failure" :
					"Success"));
			else if (TD_BRIEF == iTestDetail) {
				for (java.util.Map.Entry<String, org.drip.param.market.CurveSurfaceQuoteSet>
					meCSQS : mapCSQSDCDn.entrySet())
					System.out.println (meCSQS.getKey() + meCSQS.getValue().toString());
			} else if (TD_DETAILED == iTestDetail) {
				for (java.util.Map.Entry<String, org.drip.param.market.CurveSurfaceQuoteSet>
					meCSQS : mapCSQSDCDn.entrySet()) {
					System.out.println ("\n\n------\nMeasures for Tenor IR Bump Dn\n--------\n");

					for (int i = 0; i < aCompCalib.length; ++i)
						System.out.println ("Tenor: " + meCSQS.getKey() + "; " + astrCalibMeasure[i] + "[" +
							aCompCalib[i].primaryCode() + "] = " + aCompCalib[i].measureValue (new
								org.drip.param.valuation.ValuationParams (dt, dt, "USD"), null,
									org.drip.param.creator.MarketParamsBuilder.Create
										(meCSQS.getValue().fundingCurve
											(org.drip.state.identifier.FundingLabel.Standard
												(aCompCalib[0].payCurrency())), null, null, null, null, null,
													mpc.fixings()), null, astrCalibMeasure[i]));
				}
			}
		}

		BuildTSYCurve (dt, mpc, iTestMode, iTestDetail);
	}

	public static void testCC (
		org.drip.param.definition.ScenarioMarketParams mpc,
		final org.drip.analytics.date.JulianDate dt,
		final int iTestMode,
		final int iTestDetail)
		throws java.lang.Exception
	{
		System.out.println ("--------\nStarting CC tests\n--------");

		boolean bSNACOn = true;
		double[] adblQuotesARG = new double[5];
		double[] adblQuotesBRA = new double[5];
		String[] astrCalibMeasure = new String[5];
		org.drip.product.definition.CalibratableFixedIncomeComponent[] aCDSARG = new
			org.drip.product.definition.CreditDefaultSwap[5];
		org.drip.product.definition.CalibratableFixedIncomeComponent[] aCDSBRA = new
			org.drip.product.definition.CreditDefaultSwap[5];

		for (int i = 0; i < 5; ++i) {
			adblQuotesARG[i] = 700.;
			adblQuotesBRA[i] = 100.;
			astrCalibMeasure[i] = "FairPremium";

			if (bSNACOn) {
				aCDSARG[i] = org.drip.product.creator.CDSBuilder.CreateSNAC (dt, (i + 1) + "Y", 0.07, "ARG");

				aCDSBRA[i] = org.drip.product.creator.CDSBuilder.CreateSNAC (dt, (i + 1) + "Y", 0.01, "BRA");
			} else {
				aCDSARG[i] = org.drip.product.creator.CDSBuilder.CreateCDS (dt, dt.addYears (i + 1), 0.07,
					"USD", 0.40, "ARG", "USD", true);

				aCDSBRA[i] = org.drip.product.creator.CDSBuilder.CreateCDS (dt, dt.addYears (i + 1), 0.01,
					"USD", 0.40, "BRA", "USD", true);
			}
		}

		org.drip.param.pricer.CreditPricerParams pricerParams = new org.drip.param.pricer.CreditPricerParams
			(7, null, false, org.drip.param.pricer.CreditPricerParams.PERIOD_DISCRETIZATION_DAY_STEP);

		org.drip.analytics.rates.DiscountCurve dc = mpc.scenarioMarketParams (aCDSBRA[0], "Base").fundingCurve
			(org.drip.state.identifier.FundingLabel.Standard (aCDSBRA[0].payCurrency()));

		org.drip.param.valuation.ValuationParams valParams = new org.drip.param.valuation.ValuationParams
			(dt, dt, "USD");

		long lStart = System.nanoTime();

		org.drip.param.definition.ScenarioCreditCurve ccscARG =
			org.drip.param.creator.CreditScenarioCurveBuilder.CreateCCSC (aCDSARG);

		ccscARG.cookScenarioCC ("ARG", valParams, dc, null, astrCalibMeasure, adblQuotesARG, 0.40, null,
			null, false, 63);

		org.drip.param.definition.ScenarioCreditCurve ccscBRA =
			org.drip.param.creator.CreditScenarioCurveBuilder.CreateCCSC (aCDSBRA);

		ccscBRA.cookScenarioCC ("BRA", valParams, dc, null, astrCalibMeasure, adblQuotesBRA, 0.40, null,
			null, false, 63);

		System.out.println ("All CC Cook in: " + (System.nanoTime() - lStart) * 1.e-09 + " sec");

		mpc.addScenarioCreditCurve ("ARG", ccscARG);

		mpc.addScenarioCreditCurve ("BRA", ccscBRA);

		org.drip.analytics.definition.CreditCurve ccBase = mpc.scenarioMarketParams (aCDSBRA[0],
			"Base").creditCurve (aCDSBRA[0].creditLabel());

		if (TD_SUCCESS_FAILURE == iTestDetail)
			System.out.println ("Base CC build: " + (null == ccBase ? "Failure" : "Success"));
		else if (TD_BRIEF == iTestDetail)
			System.out.println ("Base: " + ccBase.toString());
		else if (TD_DETAILED == iTestDetail) {
			for (int i = 0; i < aCDSBRA.length; ++i)
				System.out.println ("Base Fair premium = " + aCDSBRA[i].measureValue (valParams,
					pricerParams,
						org.drip.param.creator.MarketParamsBuilder.Create (dc,
							null, null, ccBase, null, null, null, null), null, "FairPremium"));
		}

		if (0 != (TM_CC_UP01 & iTestMode)) {
			org.drip.analytics.definition.CreditCurve ccBumpUp = mpc.scenarioMarketParams (aCDSBRA[0],
				"FlatCreditBumpUp").creditCurve (aCDSBRA[0].creditLabel());

			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CC Up01 build: " + (null == ccBumpUp ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CC Up01: " + ccBumpUp.toString());
			else if (TD_DETAILED == iTestDetail) {
				for (int i = 0; i < aCDSBRA.length; ++i)
					System.out.println ("Up01 Fair premium = " + aCDSBRA[i].measureValue (valParams,
						pricerParams,
							org.drip.param.creator.MarketParamsBuilder.Create
								(dc, null, null, ccBumpUp, null, null, null, null), null, "FairPremium"));
			}
		}

		if (0 != (TM_CC_DN01 & iTestMode)) {
			org.drip.analytics.definition.CreditCurve ccBumpDn = mpc.scenarioMarketParams (aCDSBRA[0],
				"FlatCreditBumpDn").creditCurve (aCDSBRA[0].creditLabel());

			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CC Dn01 build: " + (null == ccBumpDn ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CC Dn01: " + ccBumpDn.toString());
			else if (TD_DETAILED == iTestDetail) {
				for (int i = 0; i < aCDSBRA.length; ++i)
					System.out.println ("Dn01 Fair premium = " + aCDSBRA[i].measureValue (valParams,
						pricerParams,
							org.drip.param.creator.MarketParamsBuilder.Create
								(dc, null, null, ccBumpDn, null, null, null, null), null, "FairPremium"));
			}
		}

		if (0 != (TM_RR_UP01 & iTestMode)) {
			org.drip.analytics.definition.CreditCurve ccRecoveryUp = mpc.scenarioMarketParams (aCDSBRA[0],
				"RRBumpUp").creditCurve (aCDSBRA[0].creditLabel());

			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CC RR Up01 build: " + (null == ccRecoveryUp ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CC RR Up01: " + ccRecoveryUp.toString());
			else if (TD_DETAILED == iTestDetail) {
				for (int i = 0; i < aCDSBRA.length; ++i)
					System.out.println ("RR Up01 Fair premium = " + aCDSBRA[i].measureValue (valParams,
						pricerParams,
							org.drip.param.creator.MarketParamsBuilder.Create
								(dc, null, null, ccRecoveryUp, null, null, null, null), null, "FairPremium"));
			}
		}

		if (0 != (TM_RR_DN01 & iTestMode)) {
			org.drip.analytics.definition.CreditCurve ccRecoveryDn = mpc.scenarioMarketParams (aCDSBRA[0],
				"RRBumpDn").creditCurve (aCDSBRA[0].creditLabel());

			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CC RR Dn01 build: " + (null == ccRecoveryDn ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CC RR Dn01: " + ccRecoveryDn.toString());
			else if (TD_DETAILED == iTestDetail) {
				for (int i = 0; i < aCDSBRA.length; ++i)
					System.out.println ("RR Dn01 Fair premium = " + aCDSBRA[i].measureValue (valParams,
						pricerParams,
							org.drip.param.creator.MarketParamsBuilder.Create
								(dc, null, null, ccRecoveryDn, null, null, null, null), null,
									"FairPremium"));
			}
		}

		if (0 != (TM_CC_TENOR_UP01 & iTestMode)) {
			org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.param.market.CurveSurfaceQuoteSet>
				mapCSQSCCUp = mpc.creditTenorMarketParams (aCDSBRA[0], true);

			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Tenor Bump Up CC build: " + (null == mapCSQSCCUp ? "Failure" :
					"Success"));
			else if (TD_BRIEF == iTestDetail) {
				for (java.util.Map.Entry<String, org.drip.param.market.CurveSurfaceQuoteSet>
					meCSQS : mapCSQSCCUp.entrySet())
					System.out.println (meCSQS.getKey() + ": " + meCSQS.getValue().toString());
			} else if (TD_DETAILED == iTestDetail) {
				for (java.util.Map.Entry<String, org.drip.param.market.CurveSurfaceQuoteSet>
					meCSQS : mapCSQSCCUp.entrySet()) {
					System.out.println ("\n\n------\nMeasures for Tenor CC Bump Up\n--------\n");

					for (int i = 0; i < aCDSBRA.length; ++i)
						System.out.println ("Tenor: " + meCSQS.getKey() + "; " + astrCalibMeasure[i] + "[" +
							aCDSBRA[i].primaryCode() + "] = " + aCDSBRA[i].measureValue (new
								org.drip.param.valuation.ValuationParams (dt, dt, "USD"), pricerParams,
								meCSQS.getValue(), null, astrCalibMeasure[i]));
				}
			}
		}

		if (0 != (TM_CC_TENOR_DN01 & iTestMode)) {
			org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.param.market.CurveSurfaceQuoteSet>
				mapCSQSCCDn = mpc.creditTenorMarketParams (aCDSBRA[0], false);

			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Tenor Bump Dn CC build: " + (null == mapCSQSCCDn ? "Failure" :
					"Success"));
			else if (TD_BRIEF == iTestDetail) {
				for (java.util.Map.Entry<String, org.drip.param.market.CurveSurfaceQuoteSet>
					meCSQS : mapCSQSCCDn.entrySet())
					System.out.println (meCSQS.getKey() + ": " + meCSQS.getValue().toString());
			} else if (TD_DETAILED == iTestDetail) {
				for (java.util.Map.Entry<String, org.drip.param.market.CurveSurfaceQuoteSet>
					meCSQS : mapCSQSCCDn.entrySet()) {
					System.out.println ("\n\n------\nMeasures for Tenor CC Bump Dn\n--------\n");

					for (int i = 0; i < aCDSBRA.length; ++i)
						System.out.println ("Tenor: " + meCSQS.getKey() + "; " + astrCalibMeasure[i] + "[" +
							aCDSBRA[i].primaryCode() + "] = " + aCDSBRA[i].measureValue (new
								org.drip.param.valuation.ValuationParams (dt, dt, "USD"), pricerParams,
									meCSQS.getValue(), null, astrCalibMeasure[i]));
				}
			}
		}
	}

	private static void testCash (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final org.drip.analytics.date.JulianDate dt,
		final int iTestMode,
		final int iTestDetail)
		throws java.lang.Exception
	{
		org.drip.product.definition.CalibratableFixedIncomeComponent cash =
			org.drip.product.creator.SingleStreamComponentBuilder.Deposit (dt.addDays (2), dt.addDays (10),
				org.drip.state.identifier.ForwardLabel.Create ("USD", "3M"));

		org.drip.analytics.output.ComponentMeasures cashOut = cash.measures (new
			org.drip.param.valuation.ValuationParams (dt, dt, "USD"), null, mpc, null);

		System.out.println ("Cash calcs in " + cashOut.calcTime() + " sec");

		if (TD_SUCCESS_FAILURE == iTestDetail)
			System.out.println ("Cash Base test: " + (null == cashOut.baseMeasures() ? "Failure" :
				"Success"));
		else if (TD_BRIEF == iTestDetail)
			System.out.println ("Cash measures generated: " + cashOut.baseMeasures().entrySet());
		else if (TD_DETAILED == iTestDetail) {
			System.out.println ("----\nDisplaying Base Cash measures\n----");

			for (java.util.Map.Entry<String, java.lang.Double> me :
				cashOut.baseMeasures().entrySet())
				System.out.println (me.getKey() + ": " + me.getValue().toString());
		}

		if (0 != (TM_IR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Cash FlatIRDelta test: " + (null == cashOut.flatIRDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Flat IR Delta Cash measures generated: " +
					cashOut.flatIRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Flat IR Delta Cash measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					cashOut.flatIRDeltaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_IR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Cash FlatIRGamma test: " + (null == cashOut.flatIRGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Flat IR Gamma Cash measures generated: " +
					cashOut.flatIRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Flat IR Gamma Cash measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					cashOut.flatIRGammaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_IR_TENOR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Cash TenorIRDelta test: " + (null == cashOut.tenorIRDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Cash Tenor IR Delta measures generated for: " +
					cashOut.tenorIRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Cash Tenor IR Delta measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenorMeasure :
						cashOut.tenorIRDeltaMeasures().entrySet())
					for (java.util.Map.Entry<String, java.lang.Double> me :
						meTenorMeasure.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_IR_TENOR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Cash TenorIRGamma test: " + (null == cashOut.tenorIRGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Cash Tenor IR Gamma measures generated for: " +
					cashOut.tenorIRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Cash Tenor IR Gamma measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenorMeasure :
						cashOut.tenorIRGammaMeasures().entrySet())
					for (java.util.Map.Entry<String, java.lang.Double> me :
						meTenorMeasure.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
			}
		}
	}

	private static void testEDF (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final org.drip.analytics.date.JulianDate dt,
		final int iTestMode,
		final int iTestDetail)
		throws java.lang.Exception
	{
		org.drip.product.definition.CalibratableFixedIncomeComponent[] aEDF =
			org.drip.product.creator.SingleStreamComponentBuilder.FuturesPack (dt, 1, "USD");
		
		org.drip.product.definition.CalibratableFixedIncomeComponent edf = aEDF[0];

		org.drip.analytics.output.ComponentMeasures edfOut = edf.measures (new
			org.drip.param.valuation.ValuationParams (dt, dt, "USD"), null, mpc, null);

		System.out.println ("EDF calcs in " + edfOut.calcTime() + " sec");

		if (TD_SUCCESS_FAILURE == iTestDetail)
			System.out.println ("EDF Base test: " + (null == edfOut.baseMeasures() ? "Failure" : "Success"));
		else if (TD_BRIEF == iTestDetail)
			System.out.println ("EDF measures generated: " + edfOut.baseMeasures().entrySet());
		else if (TD_DETAILED == iTestDetail) {
			System.out.println ("----\nDisplaying Base EDF DC measures\n----");

			for (java.util.Map.Entry<String, java.lang.Double> me :
				edfOut.baseMeasures().entrySet())
				System.out.println (me.getKey() + ": " + me.getValue().toString());
		}

		if (0 != (TM_IR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("EDF FlatIRDelta test: " + (null == edfOut.flatIRDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Flat IR Delta EDF measures generated: " +
					edfOut.flatIRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Flat IR Delta EDF measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					edfOut.flatIRDeltaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_IR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("EDF FlatIRGamma test: " + (null == edfOut.flatIRGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Flat IR Gamma EDF measures generated: " +
					edfOut.flatIRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Flat IR Gamma EDF measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					edfOut.flatIRGammaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_IR_TENOR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("EDF TenorIRDelta test: " + (null == edfOut.tenorIRDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("EDF Tenor IR Delta measures generated for: " +
					edfOut.tenorIRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying EDF Tenor IR Delta measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenorMeasure :
						edfOut.tenorIRDeltaMeasures().entrySet())
					for (java.util.Map.Entry<String, java.lang.Double> me :
						meTenorMeasure.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_IR_TENOR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("EDF TenorIRGamma test: " + (null == edfOut.tenorIRGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("EDF Tenor IR Gamma measures generated for: " +
					edfOut.tenorIRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying EDF Tenor IR Gamma measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenorMeasure :
						edfOut.tenorIRGammaMeasures().entrySet())
					for (java.util.Map.Entry<String, java.lang.Double> me :
						meTenorMeasure.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
			}
		}
	}

	private static void testIRS (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final org.drip.analytics.date.JulianDate dt,
		final int iTestMode,
		final int iTestDetail)
		throws java.lang.Exception
	{
		org.drip.param.period.UnitCouponAccrualSetting ucasFixed = new
			org.drip.param.period.UnitCouponAccrualSetting (2, "Act/360", false, "Act/360", false, "USD",
				true, org.drip.analytics.support.CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC);

		org.drip.param.period.ComposableFloatingUnitSetting cfusFloating = new
			org.drip.param.period.ComposableFloatingUnitSetting ("3M",
				org.drip.analytics.support.CompositePeriodBuilder.EDGE_DATE_SEQUENCE_SINGLE, null,
					org.drip.state.identifier.ForwardLabel.Standard ("USD-3M"),
						org.drip.analytics.support.CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE, 0.);

		org.drip.param.period.ComposableFixedUnitSetting cfusFixed = new
			org.drip.param.period.ComposableFixedUnitSetting ("6M",
				org.drip.analytics.support.CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR, null, 0., 0.,
					"USD");

		org.drip.param.period.CompositePeriodSetting cpsFloating = new
			org.drip.param.period.CompositePeriodSetting (4, "3M", "USD", null, -1., null, null, null, null);

		org.drip.param.period.CompositePeriodSetting cpsFixed = new
			org.drip.param.period.CompositePeriodSetting (2, "6M", "USD", null, 1., null, null, null, null);

		java.util.List<java.lang.Double> lsFixedStreamEdgeDate =
			org.drip.analytics.support.CompositePeriodBuilder.BackwardEdgeDates (dt.addDays (2), dt.addDays
				((int)(365.25 * 9 + 2)), "6M", null,
					org.drip.analytics.support.CompositePeriodBuilder.SHORT_STUB);

		java.util.List<java.lang.Double> lsFloatingStreamEdgeDate =
			org.drip.analytics.support.CompositePeriodBuilder.BackwardEdgeDates (dt.addDays (2), dt.addDays
				((int)(365.25 * 9 + 2)), "3M", null,
					org.drip.analytics.support.CompositePeriodBuilder.SHORT_STUB);

		org.drip.product.rates.Stream floatingStream = new org.drip.product.rates.Stream
			(org.drip.analytics.support.CompositePeriodBuilder.FloatingCompositeUnit
				(lsFloatingStreamEdgeDate, cpsFloating, cfusFloating));

		org.drip.product.rates.Stream fixedStream = new org.drip.product.rates.Stream
			(org.drip.analytics.support.CompositePeriodBuilder.FixedCompositeUnit (lsFixedStreamEdgeDate,
				cpsFixed, ucasFixed, cfusFixed));

		org.drip.product.definition.CalibratableFixedIncomeComponent irs = new
			org.drip.product.rates.FixFloatComponent (fixedStream, floatingStream, null);

		org.drip.analytics.output.ComponentMeasures irsOut = irs.measures (new
			org.drip.param.valuation.ValuationParams (dt, dt, "USD"), null, mpc, null);

		System.out.println ("IRS calcs in " + irsOut.calcTime() + " sec");

		if (TD_SUCCESS_FAILURE == iTestDetail)
			System.out.println ("IRS Base test: " + (null == irsOut.baseMeasures() ? "Failure" : "Success"));
		else if (TD_BRIEF == iTestDetail)
			System.out.println ("IRS measures generated: " + irsOut.baseMeasures().entrySet());
		else if (TD_DETAILED == iTestDetail) {
			System.out.println ("----\nDisplaying Base IRS DC measures\n----");

			for (java.util.Map.Entry<String, java.lang.Double> me :
				irsOut.baseMeasures().entrySet())
				System.out.println (me.getKey() + ": " + me.getValue().toString());
		}

		if (0 != (TM_IR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("IRS FlatIRDelta test: " + (null == irsOut.flatIRDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Flat IR Delta IRS measures generated: " +
					irsOut.flatIRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Flat IR Delta IRS measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					irsOut.flatIRDeltaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_IR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("IRS FlatIRGamma test: " + (null == irsOut.flatIRGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Flat IR Gamma IRS measures generated: " +
					irsOut.flatIRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Flat IR Gamma IRS measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					irsOut.flatIRGammaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_IR_TENOR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("IRS TenorIRDelta test: " + (null == irsOut.tenorIRDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Tenor IR Delta IRS measures generated for: " +
					irsOut.tenorIRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Tenor IR Delta IRS measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenorMeasure :
						irsOut.tenorIRDeltaMeasures().entrySet())
					for (java.util.Map.Entry<String, java.lang.Double> me :
						meTenorMeasure.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_IR_TENOR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("IRS TenorIRGamma test: " + (null == irsOut.tenorIRGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Tenor IR Gamma IRS measures generated for: " +
					irsOut.tenorIRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Tenor IR Gamma IRS measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenorMeasure :
						irsOut.tenorIRGammaMeasures().entrySet())
					for (java.util.Map.Entry<String, java.lang.Double> me :
						meTenorMeasure.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
			}
		}
	}

	private static void testIRComponent (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final org.drip.analytics.date.JulianDate dt,
		final int iTestMode,
		final int iTestDetail,
		final int iTestProduct)
		throws java.lang.Exception
	{
		System.out.println ("--------\nStarting IR Components tests\n--------");

		if (0 != (TP_CASH & iTestProduct)) testCash (mpc, dt, iTestMode, iTestDetail);

		if (0 != (TP_EDF & iTestProduct)) testEDF (mpc, dt, iTestMode, iTestDetail);

		if (0 != (TP_IRS & iTestProduct)) testIRS (mpc, dt, iTestMode, iTestDetail);
	}

	private static void testCDS (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final org.drip.analytics.date.JulianDate dt,
		final int iTestMode,
		final int iTestDetail)
		throws java.lang.Exception
	{
		org.drip.product.definition.CreditDefaultSwap cds = org.drip.product.creator.CDSBuilder.CreateCDS
			(dt, dt.addYears (5), 0.01, "USD", 0.40, "BRA", "USD", true);

		org.drip.analytics.output.ComponentMeasures cdsOut = cds.measures (new
			org.drip.param.valuation.ValuationParams (dt, dt, "USD"),
				org.drip.param.pricer.CreditPricerParams.Standard(), mpc, null);

		System.out.println ("CDS calcs in " + cdsOut.calcTime() + " sec");

		if (TD_SUCCESS_FAILURE == iTestDetail)
			System.out.println ("CDS Base test: " + (null == cdsOut.baseMeasures() ? "Failure" : "Success"));
		else if (TD_BRIEF == iTestDetail)
			System.out.println ("CDS measures generated: " + cdsOut.baseMeasures().entrySet());
		else if (TD_DETAILED == iTestDetail) {
			System.out.println ("----\nDisplaying Base CDS IR measures\n----");

			for (java.util.Map.Entry<String, java.lang.Double> me :
				cdsOut.baseMeasures().entrySet())
				System.out.println (me.getKey() + ": " + me.getValue().toString());
		}

		if (0 != (TM_IR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDS FlatIRDelta test: " + (null == cdsOut.flatIRDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Flat IR Delta CDS measures generated: " +
					cdsOut.flatIRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Flat IR Delta CDS measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					cdsOut.flatIRDeltaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_IR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDS FlatIRGamma test: " + (null == cdsOut.flatIRGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Flat IR Gamma CDS measures generated: " +
					cdsOut.flatIRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Flat IR Gamma CDS measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					cdsOut.flatIRGammaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_CC_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDS FlatCreditDelta test: " + (null == cdsOut.flatCreditDeltaMeasures()
					? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Flat Credit Delta CDS measures generated: " +
					cdsOut.flatCreditDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Flat Credit Delta CDS measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					cdsOut.flatCreditDeltaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_CC_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDS FlatCreditGamma test: " + (null == cdsOut.flatCreditGammaMeasures()
					? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Flat Credit Gamma CDS measures generated: " +
					cdsOut.flatCreditGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Flat Credit Gamma CDS measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					cdsOut.flatCreditGammaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_RR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDS FlatRRDelta test: " + (null == cdsOut.flatRRDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Flat RR Delta CDS measures generated: " +
					cdsOut.flatRRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Flat RR Delta CDS measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					cdsOut.flatRRDeltaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_RR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDS FlatRRGamma test: " + (null == cdsOut.flatRRGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Flat RR Gamma CDS measures generated: " +
					cdsOut.flatRRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Flat RR Gamma CDS measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					cdsOut.flatRRGammaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_IR_TENOR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDS TenorIRDelta test: " + (null == cdsOut.tenorIRDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Tenor IR Delta CDS measures generated for: " +
					cdsOut.tenorIRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Tenor IR Delta CDS measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenorMeasure :
						cdsOut.tenorIRDeltaMeasures().entrySet()) {
					for (java.util.Map.Entry<String, java.lang.Double> me :
						meTenorMeasure.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_IR_TENOR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDS TenorIRGamma test: " + (null == cdsOut.tenorIRGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Tenor IR Gamma CDS measures generated for: " +
					cdsOut.tenorIRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Tenor IR Gamma CDS measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenorMeasure :
						cdsOut.tenorIRGammaMeasures().entrySet()) {
					for (java.util.Map.Entry<String, java.lang.Double> me :
						meTenorMeasure.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_CC_TENOR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDS TenorCreditDelta test: " + (null ==
					cdsOut.tenorCreditDeltaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Tenor Credit Delta CDS measures generated for: " +
					cdsOut.tenorCreditDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Tenor Credit Delta CDS measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenorMeasure :
						cdsOut.tenorCreditDeltaMeasures().entrySet()) {
					for (java.util.Map.Entry<String, java.lang.Double> me :
						meTenorMeasure.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_CC_TENOR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDS TenorCreditGamma test: " + (null ==
					cdsOut.tenorCreditGammaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Tenor Credit Gamma CDS measures generated for: " +
					cdsOut.tenorCreditGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println
					("----\nDisplaying Tenor Credit Gamma CDS measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenorMeasure :
						cdsOut.tenorCreditGammaMeasures().entrySet()) {
					for (java.util.Map.Entry<String, java.lang.Double> me :
						meTenorMeasure.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}
	}

	private static void testFixedCouponBond (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final org.drip.analytics.date.JulianDate dt,
		final int iTestMode,
		final int iTestDetail)
		throws java.lang.Exception
	{
		double adblAmericanCallDates[] = new double[5];
		double adblAmericanCallFactors[] = new double[5];

		org.drip.product.credit.BondComponent bond = CreateSimpleBond ("BRA_5.00_21", dt.subtractDays (365),
			dt.addYears (10), 0.05, "USD", "BRA", "BRA");

		for (int i = 0; i < 5; ++i) {
			adblAmericanCallDates[i] = dt.addYears (i + 5).julian();

			adblAmericanCallFactors[i] = 1.;
		}

		/* bond.setEmbeddedCallSchedule (org.drip.product.params.EmbeddedOptionSchedule.fromAmerican
			(org.drip.analytics.date.JulianDate.CreateFromYMD (2015, 6, 20).julian(),
				adblAmericanCallDates, adblAmericanCallFactors, false, 30, false, java.lang.Double.NaN, "",
					java.lang.Double.NaN)); */

		org.drip.analytics.output.ComponentMeasures bondOut = bond.measures (new
			org.drip.param.valuation.ValuationParams (dt, dt, "USD"),
				org.drip.param.pricer.CreditPricerParams.Standard(), mpc, null);

		System.out.println ("Fixed Cpn Bond calcs in " + bondOut.calcTime() + " sec");

		if (TD_SUCCESS_FAILURE == iTestDetail)
			System.out.println ("Fixed Cpn Bond Base test: " + (null == bondOut.baseMeasures() ? "Failure" :
				"Success"));
		else if (TD_BRIEF == iTestDetail)
			System.out.println ("Fixed Cpn Bond measures generated: " + bondOut.baseMeasures().entrySet());
		else if (TD_DETAILED == iTestDetail) {
			System.out.println ("----\nDisplaying Fixed Cpn Bond Base measures\n----");

			for (java.util.Map.Entry<String, java.lang.Double> me :
				bondOut.baseMeasures().entrySet())
				System.out.println (me.getKey() + ": " + me.getValue().toString());
		}

		if (0 != (TM_IR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Fixed Cpn Bond FlatIRDelta test: " + (null ==
					bondOut.flatIRDeltaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Fixed Cpn Bond Flat IR Delta measures generated: " +
					bondOut.flatIRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Fixed Cpn Bond Flat IR Delta measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					bondOut.flatIRDeltaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_IR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Fixed Cpn Bond FlatIRGamma test: " + (null ==
					bondOut.flatIRGammaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Fixed Cpn Bond Flat IR Gamma measures generated: " +
					bondOut.flatIRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Fixed Cpn Bond Flat IR Gamma measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					bondOut.flatIRGammaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_CC_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Fixed Cpn Bond FlatCreditDelta test: " + (null ==
					bondOut.flatCreditDeltaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Fixed Cpn Bond Flat Credit Delta measures generated: " +
					bondOut.flatCreditDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Fixed Cpn Bond Flat Credit Delta measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					bondOut.flatCreditDeltaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_CC_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Fixed Cpn Bond FlatCreditGamma test: " + (null ==
					bondOut.flatCreditGammaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Fixed Cpn Bond Flat Credit Gamma measures generated: " +
					bondOut.flatCreditGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Fixed Cpn Bond Flat Credit Gamma measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					bondOut.flatCreditGammaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_RR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Fixed Cpn Bond FlatRRDelta test: " + (null ==
					bondOut.flatRRDeltaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Fixed Cpn Bond Flat RR Delta measures generated: " +
					bondOut.flatRRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Fixed Cpn Bond Flat RR Delta measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					bondOut.flatRRDeltaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_RR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Fixed Cpn Bond FlatRRGamma test: " + (null ==
					bondOut.flatRRGammaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Fixed Cpn Bond Flat RR Gamma measures generated: " +
					bondOut.flatRRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Fixed Cpn Bond Flat RR Gamma measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					bondOut.flatRRGammaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_IR_TENOR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Fixed Cpn Bond TenorIRDelta test: " + (null ==
					bondOut.tenorIRDeltaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Fixed Cpn Bond Tenor IR Delta measures generated for: " +
					bondOut.tenorIRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Fixed Cpn Bond Tenor IR Delta measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenorMeasure :
						bondOut.tenorIRDeltaMeasures().entrySet()) {
					System.out.println
						("\t----\n\tDisplaying Fixed Cpn Bond Tenor IR Delta measures\n\t----");

					for (java.util.Map.Entry<String, java.lang.Double> me :
						meTenorMeasure.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_IR_TENOR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Fixed Cpn Bond TenorIRGamma test: " + (null ==
					bondOut.tenorIRGammaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Fixed Cpn Bond Tenor IR Gamma measures generated for: " +
					bondOut.tenorIRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Fixed Cpn Bond Tenor IR Gamma measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenorMeasure :
						bondOut.tenorIRGammaMeasures().entrySet()) {
					System.out.println
						("\t----\n\tDisplaying Fixed Cpn Bond Tenor IR Gamma measures\n\t----");

					for (java.util.Map.Entry<String, java.lang.Double> me :
						meTenorMeasure.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_CC_TENOR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Fixed Cpn Bond TenorCreditDelta test: " + (null ==
					bondOut.tenorCreditDeltaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println
					("Fixed Cpn Bond Tenor Credit Delta measures generated for: " +
						bondOut.tenorCreditDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Tenor Credit Delta Fixed Cpn Bond measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenorMeasure :
						bondOut.tenorCreditDeltaMeasures().entrySet()) {
					System.out.println
						("\t----\n\tDisplaying Fixed Cpn Bond Tenor Credit Delta measures\n\t----");

					for (java.util.Map.Entry<String, java.lang.Double> me :
						meTenorMeasure.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_CC_TENOR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Fixed Cpn Bond TenorCreditGamma test: " + (null ==
					bondOut.tenorCreditGammaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Fixed Cpn Bond Tenor Credit Gamma measures generated for: " +
					bondOut.tenorCreditGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Fixed Cpn Bond Tenor Credit Gamma measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenorMeasure :
						bondOut.tenorCreditGammaMeasures().entrySet()) {
					System.out.println
						("\t----\n\tDisplaying Fixed Cpn Bond Tenor Credit Gamma measures\n\t----");

					for (java.util.Map.Entry<String, java.lang.Double> me :
						meTenorMeasure.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}
	}

	private static void testCreditComponent (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final org.drip.analytics.date.JulianDate dt,
		final int iTestMode,
		final int iTestDetail,
		final int iTestProduct)
		throws java.lang.Exception
	{
		System.out.println ("--------\nStarting Credit Components tests\n--------");

		if (0 != (TP_CDS & iTestProduct)) testCDS (mpc, dt, iTestMode, iTestDetail);

		if (0 != (TP_FIXED_BOND & iTestProduct)) testFixedCouponBond (mpc, dt, iTestMode, iTestDetail);
	}

	private static void testBasketDefaultSwap (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final org.drip.analytics.date.JulianDate dt,
		final int iTestMode,
		final int iTestDetail)
		throws java.lang.Exception
	{
		org.drip.product.definition.BasketProduct cdx = org.drip.product.creator.CDSBasketBuilder.MakeCDX
			(dt, dt.addYears (5), 0.05, "USD", new String[] {"ARG", "BRA"}, "CDX_Test");

		org.drip.param.valuation.ValuationParams valParams = new org.drip.param.valuation.ValuationParams
			(dt, dt, "USD");

		org.drip.param.pricer.CreditPricerParams pricerParams =
			org.drip.param.pricer.CreditPricerParams.Standard();

		org.drip.analytics.output.BasketMeasures cdxOp = cdx.measures (valParams, pricerParams, mpc,
			null);

		System.out.println ("CDX calcs in " + cdxOp.calcTime() + " sec");

		if (TD_SUCCESS_FAILURE == iTestDetail)
			System.out.println ("CDX Base test: " + (null == cdxOp.baseMeasures() ? "Failure" : "Success"));
		else if (TD_BRIEF == iTestDetail)
			System.out.println ("CDX measures generated: " + cdxOp.baseMeasures().entrySet());
		else if (TD_DETAILED == iTestDetail) {
			System.out.println ("----\nDisplaying CDX Base measures\n----");

			for (java.util.Map.Entry<String, java.lang.Double> me :
				cdxOp.baseMeasures().entrySet())
				System.out.println (me.getKey() + ": " + me.getValue().toString());
		}

		if (0 != (TM_IR_SET_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDX FlatIRDelta test: " + (null == cdxOp.flatIRDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CDX Flat IR Delta measures generated: " +
					cdxOp.flatIRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying CDX Flat IR Delta measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					cdxOp.flatIRDeltaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_IR_SET_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDX FlatIRGamma test: " + (null == cdxOp.flatIRGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CDX Flat IR Gamma measures generated: " +
					cdxOp.flatIRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying CDX Flat IR Gamma measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					cdxOp.flatIRGammaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_CC_SET_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDX FlatCreditDelta test: " + (null == cdxOp.flatCreditDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CDX Flat Credit Delta measures generated: " +
					cdxOp.flatCreditDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying CDX Flat Credit Delta measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					cdxOp.flatCreditDeltaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_CC_SET_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDX FlatCreditGamma test: " + (null == cdxOp.flatCreditGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CDX Flat Credit Gamma measures generated: " +
					cdxOp.flatCreditGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying CDX Flat Credit Gamma measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					cdxOp.flatCreditGammaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_RR_SET_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDX FlatRRDelta test: " + (null == cdxOp.flatRRDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CDX Flat RR Delta measures generated: " +
					cdxOp.flatRRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying CDX Flat RR Delta measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					cdxOp.flatRRDeltaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_RR_SET_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDX FlatRRGamma test: " + (null == cdxOp.flatRRGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CDX Flat RR Gamma measures generated: " +
					cdxOp.flatRRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying CDX Flat RR Gamma measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					cdxOp.flatRRGammaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_IR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDX IRDelta test: " + (null == cdxOp.componentIRDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CDX IRDelta measures generated for curves: " +
					cdxOp.componentIRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying CDX IRDelta measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meIR :
						cdxOp.componentIRDeltaMeasures().entrySet()) {
					System.out.println ("\tMeasures shown are for bumped IR Curve: " + meIR.getKey());
	
					for (java.util.Map.Entry<String, java.lang.Double> me :
						meIR.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_IR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDX IRGamma test: " + (null == cdxOp.componentIRGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CDX IRGamma measures generated for curves: " +
					cdxOp.componentIRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying CDX IRGamma measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meIR :
						cdxOp.componentIRGammaMeasures().entrySet()) {
					System.out.println ("\tMeasures shown are for bumped IR Curve: " + meIR.getKey());

					for (java.util.Map.Entry<String, java.lang.Double> me :
						meIR.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_CC_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDX CreditDelta test: " + (null == cdxOp.componentCreditDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CDX CreditDelta measures generated for curves: " +
					cdxOp.componentCreditDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying CDX CreditDelta measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meCredit :
						cdxOp.componentCreditDeltaMeasures().entrySet()) {
					System.out.println ("\tMeasures shown are for bumped Credit Curve: " +
						meCredit.getKey());

					for (java.util.Map.Entry<String, java.lang.Double> me :
						meCredit.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_CC_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDX CreditGamma test: " + (null == cdxOp.componentCreditGammaMeasures()
					? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CDX CreditGamma measures generated for curves: " +
					cdxOp.componentCreditGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying CDX CreditGamma measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meCredit :
						cdxOp.componentCreditGammaMeasures().entrySet()) {
					System.out.println ("\tMeasures shown are for bumped Credit Curve: " +
							meCredit.getKey());

					for (java.util.Map.Entry<String, java.lang.Double> me :
						meCredit.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_RR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDX RRDelta test: " + (null == cdxOp.componentRRDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CDX RRDelta measures generated for curves: " +
					cdxOp.componentRRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying CDX RRDelta measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meRR :
						cdxOp.componentRRDeltaMeasures().entrySet()) {
					System.out.println ("\tMeasures shown are for bumped RR Curve: " + meRR.getKey());

					for (java.util.Map.Entry<String, java.lang.Double> me :
						meRR.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_RR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDX RRGamma test: " + (null == cdxOp.componentRRGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CDX RRGamma measures generated for curves: " +
					cdxOp.componentRRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying CDX RRGamma measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meRR :
						cdxOp.componentRRGammaMeasures().entrySet()) {
					System.out.println ("\tMeasures shown are for bumped RR Curve: " + meRR.getKey());

					for (java.util.Map.Entry<String, java.lang.Double> me :
						meRR.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_IR_TENOR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDX IRTenorDelta test: " + (null ==
					cdxOp.componentTenorIRDeltaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CDX IRTenorDelta measures generated for curves: " +
					cdxOp.componentTenorIRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying CDX IRTenorDelta measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>>>
						mmIRTenor : cdxOp.componentTenorIRDeltaMeasures().entrySet()) {
					for (java.util.Map.Entry<String,
						org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenor :
							mmIRTenor.getValue().entrySet()) {
						System.out.println ("\tDelta measures shown are for IRCurve=" + mmIRTenor.getKey() +
							" and Tenor=" + meTenor.getKey());

						for (java.util.Map.Entry<String, java.lang.Double> me :
							meTenor.getValue().entrySet())
							System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
					}
				}
			}
		}

		if (0 != (TM_IR_TENOR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDX IRTenorGamma test: " + (null ==
					cdxOp.componentTenorIRGammaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CDX IRTenorGamma measures generated for curves: " +
					cdxOp.componentTenorIRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying CDX IRTenorGamma measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>>>
						mmIRTenor : cdxOp.componentTenorIRGammaMeasures().entrySet()) {
					for (java.util.Map.Entry<String,
						org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenor :
							mmIRTenor.getValue().entrySet()) {
						System.out.println ("\tGamma measures shown are for IRCurve=" + mmIRTenor.getKey() +
							" and Tenor=" + meTenor.getKey());

						for (java.util.Map.Entry<String, java.lang.Double> me :
							meTenor.getValue().entrySet())
							System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
					}
				}
			}
		}

		if (0 != (TM_CC_TENOR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDX CreditTenorDelta test: " + (null ==
					cdxOp.componentTenorCreditDeltaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CDX CreditTenorDelta measures generated for curves: " +
					cdxOp.componentTenorCreditDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying CDX CreditTenorDelta measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>>>
						mmCreditTenor : cdxOp.componentTenorCreditDeltaMeasures().entrySet()) {
					for (java.util.Map.Entry<String,
						org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenor :
							mmCreditTenor.getValue().entrySet()) {
						System.out.println ("\tDelta measures shown are for Credit=" + mmCreditTenor.getKey()
							+ " and Tenor=" + meTenor.getKey());

						for (java.util.Map.Entry<String, java.lang.Double> me :
							meTenor.getValue().entrySet())
							System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
					}
				}
			}
		}

		if (0 != (TM_CC_TENOR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDX CreditTenorGamma test: " + (null ==
					cdxOp.componentTenorCreditGammaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("CDX CreditTenorGamma measures generated for curves: " +
					cdxOp.componentTenorCreditGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying CDX CreditTenorGamma measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>>>
						mmCreditTenor : cdxOp.componentTenorCreditGammaMeasures().entrySet()) {
					for (java.util.Map.Entry<String,
						org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenor :
							mmCreditTenor.getValue().entrySet()) {
						System.out.println ("\tGamma measures shown are for Credit=" + mmCreditTenor.getKey()
							+ " and Tenor=" + meTenor.getKey());

						for (java.util.Map.Entry<String, java.lang.Double> me :
							meTenor.getValue().entrySet())
							System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
					}
				}
			}
		}
	}

	private static void testBasketBond (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final org.drip.analytics.date.JulianDate dt,
		final int iTestMode,
		final int iTestDetail)
		throws java.lang.Exception
	{
		double dblStart = dt.julian();

		double[] adblDate = new double[3];
		double[] adblPutDate = new double[3];
		double[] adblCallDate = new double[3];
		double[] adblPutFactor = new double[3];
		double[] adblCallFactor = new double[3];
		double[] adblCouponFactor = new double[3];
		double[] adblNotionalFactor = new double[3];
		adblPutFactor[0] = 0.80;
		adblPutFactor[1] = 0.90;
		adblPutFactor[2] = 1.00;
		adblCallFactor[0] = 1.20;
		adblCallFactor[1] = 1.10;
		adblCallFactor[2] = 1.00;
		adblPutDate[0] = dblStart + 30.;
		adblPutDate[1] = dblStart + 396.;
		adblPutDate[2] = dblStart + 761.;
		adblCallDate[0] = dblStart + 1126.;
		adblCallDate[1] = dblStart + 1492.;
		adblCallDate[2] = dblStart + 1857.;

		for (int i = 0; i < 3; ++i) {
			adblCouponFactor[i] = 1 - 0.1 * i;
			adblNotionalFactor[i] = 1 - 0.05 * i;
			adblDate[i] = dblStart + 365. * (i + 1);
		}

		org.drip.param.market.LatentStateFixingsContainer lsfc = new
			org.drip.param.market.LatentStateFixingsContainer();

		lsfc.add (org.drip.analytics.date.DateUtil.Today().addDays (2),
			org.drip.state.identifier.ForwardLabel.Standard ("USD-6M"), 0.0402);

		org.drip.product.params.BondStream bpgp = org.drip.product.params.BondStream.Create (dblStart +
			3653., dblStart, dblStart + 3653., dblStart + 182., dblStart, 2, 0.01, "30/360", "30/360", null,
				null, null, null, null, null, null, null, "IGNORE", false, "USD", "USD",
					org.drip.state.identifier.ForwardLabel.Standard ("USD-6M"),
						org.drip.state.identifier.CreditLabel.Standard ("IBM"));

		org.drip.product.credit.BondComponent bond = new org.drip.product.credit.BondComponent();

		if (!bond.setTreasuryBenchmark (new org.drip.product.params.TreasuryBenchmarks ("USD5YON", new
			String[] {"USD3YON", "USD7YON"}))) {
			System.out.println ("Cannot initialize bond TSY params!");

			System.exit (126);
		}

		if (!bond.setCouponSetting (new org.drip.product.params.CouponSetting
			(org.drip.quant.common.Array2D.FromArray (adblDate, adblCouponFactor), "FLOATER", 0.01,
				java.lang.Double.NaN, java.lang.Double.NaN))) {
			System.out.println ("Cannot initialize bond Coupon params!");

			System.exit (127);
		}

		if (!bond.setNotionalSetting (new org.drip.product.params.NotionalSetting (1, "USD",
			org.drip.quant.common.Array2D.FromArray (adblDate, adblNotionalFactor),
				org.drip.product.params.NotionalSetting.PERIOD_AMORT_AT_START, false))) {
			System.out.println ("Cannot initialize bond Notional params!");

			System.exit (128);
		}

		if (!bond.setFloaterSetting (new org.drip.product.params.FloaterSetting ("USD-LIBOR-6M", "30/360",
			0.01, java.lang.Double.NaN))) {
			System.out.println ("Cannot initialize bond Floater params!");

			System.exit (129);
		}

		if (!bond.setFixings (lsfc)) {
			System.out.println ("Cannot initialize bond Fixings!");

			System.exit (130);
		}

		if (!bond.setIdentifierSet (new org.drip.product.params.IdentifierSet ("US07942381EZ",
			"07942381E", "IBM-US07942381EZ", "IBM"))) {
			System.out.println ("Cannot initialize bond Identifier params!");

			System.exit (132);
		}

		if (!bond.setMarketConvention (new org.drip.product.params.QuoteConvention (new
			org.drip.param.valuation.ValuationCustomizationParams ("30/360", 2, true, null, "DKK", false,
				null, null), "REGULAR", dblStart + 2, 1., 3, "USD",
					org.drip.analytics.daycount.Convention.DATE_ROLL_FOLLOWING))) {
			System.out.println ("Cannot initialize bond Valuation params!");

			System.exit (133);
		}

		if (!bond.setCreditSetting (new org.drip.product.params.CreditSetting (30, java.lang.Double.NaN,
			true, "IBMSUB", false))) {
			System.out.println ("Cannot initialize bond Credit Valuation params!");

			System.exit (134);
		}

		if (!bond.setTerminationSetting (new org.drip.product.params.TerminationSetting (false, false,
			false))) {
			System.out.println ("Cannot initialize bond CFTE params!");

			System.exit (135);
		}

		if (!bond.setStream (bpgp)) {
			System.out.println ("Cannot initialize bond Period Generation params!");

			System.exit (136);
		}

		bond.setEmbeddedPutSchedule (org.drip.product.params.EmbeddedOptionSchedule.FromAmerican (dblStart,
			adblPutDate, adblPutFactor, true, 30, false, java.lang.Double.NaN, "CRAP",
				java.lang.Double.NaN));

		bond.setEmbeddedCallSchedule (org.drip.product.params.EmbeddedOptionSchedule.FromAmerican (dblStart,
			adblCallDate, adblCallFactor, false, 30, false, java.lang.Double.NaN, "CRAP",
				java.lang.Double.NaN));

		org.drip.product.definition.BasketProduct bb =
			org.drip.product.creator.BondBasketBuilder.CreateBondBasket ("BASKETBOND", new
				org.drip.product.credit.BondComponent[] {bond, bond}, new double[] {0.7, 1.3});

		org.drip.param.valuation.ValuationParams valParams = new org.drip.param.valuation.ValuationParams
			(dt, dt, "USD");

		org.drip.param.pricer.CreditPricerParams pricerParams = org.drip.param.pricer.CreditPricerParams.Standard();

		org.drip.analytics.output.BasketMeasures bbOp = bb.measures (valParams, pricerParams, mpc, null);

		System.out.println ("Bond Basket calcs in " + bbOp.calcTime() + " sec");

		if (TD_SUCCESS_FAILURE == iTestDetail)
			System.out.println ("Bond Basket Base test: " + (null == bbOp.baseMeasures() ? "Failure" :
				"Success"));
		else if (TD_BRIEF == iTestDetail)
			System.out.println ("Bond Basket measures generated: " + bbOp.baseMeasures().entrySet());
		else if (TD_DETAILED == iTestDetail) {
			System.out.println ("----\nDisplaying Bond Basket Base measures\n----");

			for (java.util.Map.Entry<String, java.lang.Double> me : bbOp.baseMeasures().entrySet())
				System.out.println (me.getKey() + ": " + me.getValue().toString());
		}

		if (0 != (TM_IR_SET_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bond Basket FlatIRDelta test: " + (null == bbOp.flatIRDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bond Basket Flat IR Delta measures generated: " +
					bbOp.flatIRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Bond Basket Flat IR Delta measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					bbOp.flatIRDeltaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_IR_SET_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bond Basket FlatIRGamma test: " + (null == bbOp.flatIRGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bond Basket Flat IR Gamma measures generated: " +
					bbOp.flatIRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Bond Basket Flat IR Gamma measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					bbOp.flatIRGammaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_CC_SET_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bond Basket FlatCreditDelta test: " + (null ==
					bbOp.flatCreditDeltaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bond Basket Flat Credit Delta measures generated: " +
					bbOp.flatCreditDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Bond Basket Flat Credit Delta measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					bbOp.flatCreditDeltaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_CC_SET_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bond Basket FlatCreditGamma test: " + (null ==
					bbOp.flatCreditGammaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bond Basket Flat Credit Gamma measures generated: " +
					bbOp.flatCreditGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Bond Basket Flat Credit Gamma measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					bbOp.flatCreditGammaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_RR_SET_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bond Basket FlatRRDelta test: " + (null == bbOp.flatRRDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bond Basket Flat RR Delta measures generated: " +
					bbOp.flatRRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Bond Basket Flat RR Delta measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					bbOp.flatRRDeltaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_RR_SET_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bond Basket FlatRRGamma test: " + (null == bbOp.flatRRGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bond Basket Flat RR Gamma measures generated: " +
					bbOp.flatRRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Bond Basket Flat RR Gamma measures\n----");

				for (java.util.Map.Entry<String, java.lang.Double> me :
					bbOp.flatRRGammaMeasures().entrySet())
					System.out.println (me.getKey() + ": " + me.getValue().toString());
			}
		}

		if (0 != (TM_IR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bond Basket IRDelta test: " + (null == bbOp.componentIRDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bond Basket IRDelta measures generated for curves: " +
					bbOp.componentIRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Bond Basket IRDelta measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meIR :
						bbOp.componentIRDeltaMeasures().entrySet()) {
					System.out.println ("\tMeasures shown are for bumped IR Curve: " + meIR.getKey());
	
					for (java.util.Map.Entry<String, java.lang.Double> me :
						meIR.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_IR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bond Basket IRGamma test: " + (null == bbOp.componentIRGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bond Basket IRGamma measures generated for curves: " +
					bbOp.componentIRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Bond Basket IRGamma measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meIR :
						bbOp.componentIRGammaMeasures().entrySet()) {
					System.out.println ("\tMeasures shown are for bumped IR Curve: " + meIR.getKey());

					for (java.util.Map.Entry<String, java.lang.Double> me :
						meIR.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_CC_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("CDX CreditDelta test: " + (null == bbOp.componentCreditDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bond Basket CreditDelta measures generated for curves: " +
					bbOp.componentCreditDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Bond Basket CreditDelta measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meCredit :
						bbOp.componentCreditDeltaMeasures().entrySet()) {
					System.out.println ("\tMeasures shown are for bumped Credit Curve: " +
						meCredit.getKey());

					for (java.util.Map.Entry<String, java.lang.Double> me :
						meCredit.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_CC_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bond Basket CreditGamma test: " + (null ==
					bbOp.componentCreditGammaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bond Basket CreditGamma measures generated for curves: " +
					bbOp.componentCreditGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Bond Basket CreditGamma measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meCredit :
						bbOp.componentCreditGammaMeasures().entrySet()) {
					System.out.println ("\tMeasures shown are for bumped Credit Curve: " +
						meCredit.getKey());

					for (java.util.Map.Entry<String, java.lang.Double> me :
						meCredit.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_RR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bond Basket RRDelta test: " + (null == bbOp.componentRRDeltaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bond Basket RRDelta measures generated for curves: " +
					bbOp.componentRRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Bond Basket RRDelta measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meRR :
						bbOp.componentRRDeltaMeasures().entrySet()) {
					System.out.println ("\tMeasures shown are for bumped RR Curve: " + meRR.getKey());

					for (java.util.Map.Entry<String, java.lang.Double> me :
						meRR.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_RR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bond Basket RRGamma test: " + (null == bbOp.componentRRGammaMeasures() ?
					"Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bond Basket RRGamma measures generated for curves: " +
					bbOp.componentRRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Bond Basket RRGamma measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meRR :
						bbOp.componentRRGammaMeasures().entrySet()) {
					System.out.println ("\tMeasures shown are for bumped RR Curve: " + meRR.getKey());

					for (java.util.Map.Entry<String, java.lang.Double> me :
						meRR.getValue().entrySet())
						System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
				}
			}
		}

		if (0 != (TM_IR_TENOR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bond Basket IRTenorDelta test: " + (null ==
					bbOp.componentTenorIRDeltaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bond Basket IRTenorDelta measures generated for curves: " +
					bbOp.componentTenorIRDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Bond Basket IRTenorDelta measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>>>
						mmIRTenor : bbOp.componentTenorIRDeltaMeasures().entrySet()) {
					for (java.util.Map.Entry<String,
						org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenor :
							mmIRTenor.getValue().entrySet()) {
						System.out.println ("\tDelta measures shown are for IRCurve=" + mmIRTenor.getKey() +
							" and Tenor=" + meTenor.getKey());

						for (java.util.Map.Entry<String, java.lang.Double> me :
							meTenor.getValue().entrySet())
							System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
					}
				}
			}
		}

		if (0 != (TM_IR_TENOR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bond Basket IRTenorGamma test: " + (null ==
					bbOp.componentTenorIRGammaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bond Basket IRTenorGamma measures generated for curves: " +
					bbOp.componentTenorIRGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Bond Basket IRTenorGamma measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>>>
						mmIRTenor : bbOp.componentTenorIRGammaMeasures().entrySet()) {
					for (java.util.Map.Entry<String,
						org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenor :
							mmIRTenor.getValue().entrySet()) {
						System.out.println ("\tGamma measures shown are for IRCurve=" + mmIRTenor.getKey() +
							" and Tenor=" + meTenor.getKey());

						for (java.util.Map.Entry<String, java.lang.Double> me :
							meTenor.getValue().entrySet())
							System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
					}
				}
			}
		}

		if (0 != (TM_CC_TENOR_UP01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bond Basket CreditTenorDelta test: " + (null ==
					bbOp.componentTenorCreditDeltaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bond Basket CreditTenorDelta measures generated for curves: " +
					bbOp.componentTenorCreditDeltaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Bond Basket CreditTenorDelta measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>>>
						mmCreditTenor : bbOp.componentTenorCreditDeltaMeasures().entrySet()) {
					for (java.util.Map.Entry<String,
						org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenor :
							mmCreditTenor.getValue().entrySet()) {
						System.out.println ("\tDelta measures shown are for Credit=" + mmCreditTenor.getKey()
							+ " and Tenor=" + meTenor.getKey());

						for (java.util.Map.Entry<String, java.lang.Double> me :
							meTenor.getValue().entrySet())
							System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
					}
				}
			}
		}

		if (0 != (TM_CC_TENOR_DN01 & iTestMode)) {
			if (TD_SUCCESS_FAILURE == iTestDetail)
				System.out.println ("Bond Basket CreditTenorGamma test: " + (null ==
					bbOp.componentTenorCreditGammaMeasures() ? "Failure" : "Success"));
			else if (TD_BRIEF == iTestDetail)
				System.out.println ("Bond Basket CreditTenorGamma measures generated for curves: " +
					bbOp.componentTenorCreditGammaMeasures().entrySet());
			else if (TD_DETAILED == iTestDetail) {
				System.out.println ("----\nDisplaying Bond Basket CreditTenorGamma measures\n----");

				for (java.util.Map.Entry<String,
					org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>>>
				mmCreditTenor : bbOp.componentTenorCreditGammaMeasures().entrySet()) {
					for (java.util.Map.Entry<String,
						org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>> meTenor :
							mmCreditTenor.getValue().entrySet()) {
						System.out.println ("\tGamma measures shown are for Credit=" + mmCreditTenor.getKey()
							+ " and Tenor=" + meTenor.getKey());

						for (java.util.Map.Entry<String, java.lang.Double> me :
							meTenor.getValue().entrySet())
							System.out.println ("\t\t" + me.getKey() + ": " + me.getValue().toString());
					}
				}
			}
		}
	}

	private static void testCreditBasketProduct (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final org.drip.analytics.date.JulianDate dt,
		final int iTestMode,
		final int iTestDetail,
		final int iTestProduct)
		throws java.lang.Exception
	{
		System.out.println ("--------\nStarting Basket Credit Products tests\n--------");

		if (0 != (TP_CDX & iTestProduct)) testBasketDefaultSwap (mpc, dt, iTestMode, iTestDetail);
	}

	private static void testBondBasketProduct (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final org.drip.analytics.date.JulianDate dt,
		final int iTestMode,
		final int iTestDetail,
		final int iTestProduct)
		throws java.lang.Exception
	{
		System.out.println ("--------\nStarting Basket Bond Products tests\n--------");

		if (0 != (TP_BASKET_BOND & iTestProduct)) testBasketBond (mpc, dt, iTestMode, iTestDetail);
	}

	private static final void AnalSim (
		final int iTestDetail,
		final int iTestProduct,
		final int iTestMode,
		final int iNumSimulations,
		final long lSleepTime)
		throws java.lang.Exception
	{
		org.drip.analytics.date.JulianDate dt = org.drip.analytics.date.DateUtil.Today();

		org.drip.param.definition.ScenarioMarketParams mpc =
			org.drip.param.creator.MarketParamsBuilder.CreateMarketParams();

		for (int i = 0; i < iNumSimulations; ++i) {
			long lTestStart = System.nanoTime();

			testDC (mpc, dt, iTestMode, iTestDetail);

			testIRComponent (mpc, dt, iTestMode, iTestDetail, iTestProduct);

			testCC (mpc, dt, 15, iTestDetail);

			testCreditComponent (mpc, dt, iTestMode, iTestDetail, iTestProduct);

			testCreditBasketProduct (mpc, dt, iTestMode, iTestDetail, iTestProduct);

			testBondBasketProduct (mpc, dt, iTestMode, iTestDetail, iTestProduct);

			System.out.println ("Sim # " + (i + 1) + " took " + (System.nanoTime() - lTestStart) * 1.e-09 +
				" sec\n\n");

			java.lang.Thread.sleep (lSleepTime);
		}
	}

	public static void main (
		final String astrArgs[])
		throws java.lang.Exception
	{
		org.drip.analytics.support.Logger.Init ("c:\\Lakshmi\\BondAnal\\Config.xml");

		org.drip.analytics.daycount.Convention.Init ("c:\\Lakshmi\\BondAnal\\Config.xml");

		int iNumRuns = 1;
		long lSleepTime = 1000L;
		int iTestDetail = TD_SUCCESS_FAILURE;
		int iTestProduct = TP_FIXED_BOND;
		// int iTestProduct = TP_FIXED_BOND | TP_CDS | TP_BASKET_BOND;
		// int iTestProduct = TP_CASH | TP_EDF | TP_IRS | TP_CDS | TP_FIXED_BOND | TP_CDX | TP_BASKET_BOND;
		int iTestMode = TM_BASE;
		/* int iTestMode = TM_IR_UP01 | TM_IR_DN01 | TM_IR_SET_UP01 | TM_IR_SET_DN01 | TM_IR_TENOR_UP01 |
			TM_IR_TENOR_DN01 | TM_CC_UP01 | TM_CC_DN01 | TM_CC_SET_UP01 | TM_CC_SET_DN01 | TM_CC_TENOR_UP01 |
				TM_CC_TENOR_DN01 | TM_RR_UP01 | TM_RR_DN01 | TM_RR_SET_UP01 | TM_RR_SET_DN01 | TM_TSY_UP01 |
					TM_TSY_DN01 | TM_TSY_SET_UP01 | TM_TSY_SET_DN01 | TM_TSY_TENOR_UP01 | TM_TSY_TENOR_DN01; */

		if (0 < astrArgs.length) {
			iNumRuns = java.lang.Integer.valueOf (astrArgs[0]);

			if (1 < astrArgs.length) lSleepTime = new java.lang.Long (astrArgs[0]);
		}

		AnalSim (iTestDetail, iTestProduct, iTestMode, iNumRuns, lSleepTime);
	}
}
