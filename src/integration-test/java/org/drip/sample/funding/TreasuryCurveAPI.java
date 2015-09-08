
package org.drip.sample.funding;

/*
 * Credit Product imports
 */

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.analytics.rates.DiscountCurve;
import org.drip.param.creator.MarketParamsBuilder;
import org.drip.param.creator.ScenarioDiscountCurveBuilder;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.creator.BondBuilder;
import org.drip.product.definition.Bond;
import org.drip.quant.common.FormatUtil;
import org.drip.service.api.CreditAnalytics;
import org.drip.state.creator.DiscountCurveBuilder;
import org.testng.annotations.Test;

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
 * TreasuryCurveAPI contains a demo of construction and usage of the treasury discount curve from government
 * 	bond inputs. It shows the following:
 * 	- Create on-the-run TSY bond set.
 * 	- Calibrate a discount curve off of the on-the-run yields and calculate the implied zeroes and DF's.
 * 	- Price an off-the-run TSY.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class TreasuryCurveAPI {

	/*
	 * Sample demonstrating creation of simple fixed coupon treasury bond
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final Bond CreateTSYBond (
		final String strName,
		final double dblCoupon,
		final JulianDate dt,
		int iNumYears)
		throws Exception
	{
		return BondBuilder.CreateSimpleFixed (	// Simple Fixed Rate Bond
			strName,					// Name
			"USD",						// Fictitious Treasury Curve Name
			"",							// Empty Credit Curve
			dblCoupon,					// Bond Coupon
			2, 							// Frequency
			"Act/Act",					// Day Count
			dt, 						// Effective
			dt.addYears (iNumYears),	// Maturity
			null,						// Principal Schedule
			null
		);
	}

	/*
	 * Sample demonstrating creation of a set of the on-the-run treasury bonds
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final Bond[] CreateOnTheRunTSYBondSet (
		final JulianDate dt,
		final String[] astrTSYBondName,
		final int[] aiMaturityYear,
		final double[] adblCoupon)
		throws Exception
	{
		Bond aTSYBond[] = new Bond[astrTSYBondName.length];

		for (int i = 0; i < astrTSYBondName.length; ++i)
			aTSYBond[i] = CreateTSYBond (
				astrTSYBondName[i],
				adblCoupon[i],
				dt,
				aiMaturityYear[i]
			);

		return aTSYBond;
	}

	/*
	 * Sample demonstrating building of the treasury discount curve based off the on-the run instruments and their yields
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final DiscountCurve BuildOnTheRunTSYDiscountCurve (
		final JulianDate dt,
		final Bond[] aTSYBond,
		final double[] adblCalibYield)
		throws Exception
	{
		String astrCalibMeasure[] = new String[aTSYBond.length];

		for (int i = 0; i < aTSYBond.length; ++i)
			astrCalibMeasure[i] = "Yield";

		return ScenarioDiscountCurveBuilder.NonlinearBuild (
			dt,
			"USD",
			DiscountCurveBuilder.BOOTSTRAP_MODE_CONSTANT_FORWARD,
			aTSYBond,
			adblCalibYield,
			astrCalibMeasure,
			null
		);
	}

	/*
	 * Sample demonstrating calculation of the yields of the input on the run treasury instruments
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final double[] GetOnTheRunYield (
		final JulianDate dt,
		final DiscountCurve dcTSY,
		final Bond[] aTSYBond)
		throws Exception
	{
		double adblYield[] = new double[aTSYBond.length];

		for (int i = 0; i < aTSYBond.length; ++i) {
			double dblPrice = aTSYBond[i].priceFromBumpedDC (
				new ValuationParams (
					DateUtil.Today(),
					DateUtil.Today(),
					"USD"
				),
				MarketParamsBuilder.Discount (dcTSY),
				aTSYBond[i].maturityDate().julian(),
				1.,
				0.
			);

			System.out.println ("\tPrice[" + aTSYBond[i].name() + "]: " +
				FormatUtil.FormatDouble (dblPrice, 2, 3, 100.));

			double dblYield = aTSYBond[i].yieldFromPrice (
				new ValuationParams (
					DateUtil.Today(),
					DateUtil.Today(),
					"USD"
				),
				MarketParamsBuilder.Discount (dcTSY),
				null,
				dblPrice
			);

			System.out.println ("\tYield[" + aTSYBond[i].name() + "]: " +
				FormatUtil.FormatDouble (dblYield, 1, 3, 100.));
		}

		return adblYield;
	}

	/*
	 * This org.drip.sample illustrates the construction and validation of the Treasury Curve API. It demonstrates the
	 * 	following:
	 * 	- Create the on-the-run treasury bonds.
	 * 	- Create the on-the-run treasury discount curve from the treasury bonds.
	 * 	- Compare the implied and the input yields for the on-the-run's.
	 * 	- Calculate the yield of an off-the-run instrument off of the on-the-run yield discount curve and
	 * 		cross verify it with the price.
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final void TreasuryCurveSample()
		throws Exception
	{
		/*
		 * Initialize the Credit Analytics Library
		 */

		String strConfig = "";

		CreditAnalytics.Init (strConfig);

		/*
		 * Define name, maturity, coupon, and the market yield of the input on-the-run treasuries  
		 */

		final String[] astrTSYName = new String[] {"TSY2YON", "TSY3YON", "TSY5YON", "TSY7YON", "TSY10YON", "TSY30YON"};
		final int[] aiMaturityYear = new int[] {2, 3, 5, 7, 10, 30};
		final double[] adblCoupon = new double[] {0.0200, 0.0250, 0.0300, 0.0325, 0.0375, 0.0400};
		final double[] adblCalibYield = new double[] {0.0200, 0.0250, 0.0300, 0.0325, 0.0375, 0.0400};

		/*
		 * Create the on-the-run treasury bonds
		 */

		long lTime = System.nanoTime();

		Bond[] aTSYBond = CreateOnTheRunTSYBondSet (
			DateUtil.Today(),
			astrTSYName,
			aiMaturityYear,
			adblCoupon
		);

		/*
		 * Create the on-the-run treasury discount curve
		 */

		DiscountCurve dcTSY = BuildOnTheRunTSYDiscountCurve (
			DateUtil.Today(),
			aTSYBond,
			adblCalibYield
		);

		/*
		 * Compare the implied discount rate and input yields - in general they DO NOT match 
		 */

		for (int i = 0; i < astrTSYName.length; ++i) {
			String strTenor = aiMaturityYear[i] + "Y";

			System.out.println ("Zero[" + strTenor + "]: " + dcTSY.zero (strTenor) +
				"; Yield[" + strTenor + "]: " + adblCalibYield[i]);
		}

		System.out.println ("\n----\n");

		double[] adblYield = GetOnTheRunYield (
			DateUtil.Today(),
			dcTSY,
			aTSYBond
		);

		/*
		 * Compare the implied and the input yields for the on-the-run's - they DO match 
		 */

		for (int i = 0; i < astrTSYName.length; ++i) {
			String strTenor = aiMaturityYear[i] + "Y";

			System.out.println ("CalcYield[" + strTenor + "]: " + adblYield[i] + "; Input[" + strTenor + "]: " + adblCalibYield[i]);
		}

		/*
		 * Finally calculate the yield of an off-the-run instrument off of the on-the-run yield discount curve 
		 */

		/*
		 * Construct off-the-run
		 */

		int iOffTheRunMaturityYears = 10;

		Bond bondOffTheRun = BondBuilder.CreateSimpleFixed (	// Simple Fixed Rate Bond
			"USD" + iOffTheRunMaturityYears + "YOFF",
			"USD",
			"",
			0.0375,
			2,
			"Act/Act",
			DateUtil.Today(),
			DateUtil.Today().addYears (iOffTheRunMaturityYears),	// off-the-run
			null,
			null
		);

		/*
		 * Calculate price for off-the-run
		 */

		double dblPrice = bondOffTheRun.priceFromBumpedDC (
			new ValuationParams (
				DateUtil.Today(),
				DateUtil.Today(),
				"USD"
			),
			MarketParamsBuilder.Discount (dcTSY),
			bondOffTheRun.maturityDate().julian(),
			1.,
			0.
		);

		System.out.println ("\nOff-The-Run Price[" + iOffTheRunMaturityYears + "Y]: " + dblPrice);

		/*
		 * Calculate yield for off-the-run
		 */

		double dblYieldOffTheRun = bondOffTheRun.yieldFromPrice (
			new ValuationParams (
				DateUtil.Today(),
				DateUtil.Today(),
				"USD"
			),
			MarketParamsBuilder.Discount (dcTSY),
			null,
			dblPrice
		);

		System.out.println ("\nOff-The-Run Yield[" + iOffTheRunMaturityYears + "Y]: " + dblYieldOffTheRun);

		System.out.println ("\tTime => " + (System.nanoTime() - lTime) * 1.e-06 + " ms");
	}

	@Test(dataProvider = "mainparam", dataProviderClass = org.drip.sample.TestNGDataProvider.class)
	public static void main (
		final String[] astrArgs)
		throws Exception
	{
		TreasuryCurveSample();
	}
}
