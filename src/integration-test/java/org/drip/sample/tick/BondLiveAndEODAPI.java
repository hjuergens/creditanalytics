
package org.drip.sample.tick;

/*
 * Credit Product Analytics API
 */

import java.util.*;

import org.drip.analytics.cashflow.*;
import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.analytics.daycount.Convention;
import org.drip.analytics.definition.*;
import org.drip.analytics.output.ExerciseInfo;
import org.drip.analytics.rates.ExplicitBootDiscountCurve;
import org.drip.analytics.support.CaseInsensitiveTreeMap;
import org.drip.param.market.CurveSurfaceQuoteSet;
import org.drip.param.pricer.CreditPricerParams;
import org.drip.param.valuation.*;
import org.drip.product.definition.*;
import org.drip.param.creator.*;
import org.drip.quant.common.FormatUtil;
import org.drip.service.api.CreditAnalytics;
import org.drip.state.creator.*;

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
 * BondLiveAndEODAPI contains the comprehensive org.drip.sample class demonstrating the usage of the EOD and Live
 *  Curve Bond API functions.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class BondLiveAndEODAPI {
	private static final String FIELD_SEPARATOR = "    ";

	/*
	 * Sample demonstrating the calculation of the bond's EOD yield measures from price
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final void BondEODMeasuresAPISample()
		throws Exception
	{
		String strISIN = "008686AA5"; // Amortizer

		/* 
		 * Sample for EOD measure in, measure out, as in <<MeasureOut>>From<<MeasureIn>>, where MeasureIn
		 * 		is one of
		 * 			- Price
		 * 			- TSY Spread
		 * 			- Yield
		 * 		and measure in is one of
		 * 			- Convexity
		 * 			- Credit Basis
		 * 			- Duration
		 * 			- G Spread
		 * 			- I Spread
		 * 			- OAS
		 * 			- Par Asset Swap Spread
		 * 			- Price
		 * 			- TSY Spread
		 * 			- Yield
		 * 			- ZSpread
		 * 
		 * Please see the FI javadoc for the API details.
		 */

		System.out.println ("EOD Yield From Price: " + FormatUtil.FormatDouble
			(CreditAnalytics.BondEODYieldFromPrice (strISIN, DateUtil.CreateFromYMD (2011, 12, 16), 1.), 2, 3, 100.));
	}

	/*
	 * Sample demonstrating the calculation of the bond's full EOD measures from price, TSY spread, or yield
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final void BondEODSample()
	{
		JulianDate dtEOD = DateUtil.CreateFromYMD (2012, 1, 13);

		String strISIN = "US78490FUS63"; // Short dated floater 9/15/2012
		// String strISIN = "US78442GGV23"; // Long dated floater
		// String strISIN = "US44180Y2046"; // Plain old fixed coupon

		CaseInsensitiveTreeMap<Double> mapPriceMeasures = CreditAnalytics.BondEODMeasuresFromPrice (strISIN, dtEOD, 1.);

		System.out.println ("\n--------------\nPrice Measures\n--------------");

		for (Map.Entry<String, Double> me : mapPriceMeasures.entrySet())
			System.out.println (me.getKey() + "=" + me.getValue());

		CaseInsensitiveTreeMap<Double> mapTSYSpreadMeasures = CreditAnalytics.BondEODMeasuresFromTSYSpread (strISIN, dtEOD, 0.0486);

		System.out.println ("\n---------------\nSpread Measures\n---------------");

		for (Map.Entry<String, Double> me : mapTSYSpreadMeasures.entrySet())
			System.out.println (me.getKey() + "=" + me.getValue());

		CaseInsensitiveTreeMap<Double> mapYieldMeasures = CreditAnalytics.BondEODMeasuresFromYield (strISIN, dtEOD, 0.0749);

		System.out.println ("\n--------------\nYield Measures\n--------------");

		for (Map.Entry<String, Double> me : mapYieldMeasures.entrySet())
			System.out.println (me.getKey() + "=" + me.getValue());
	}

	/*
	 * Sample demonstrating the calculation of analytics for the set of bonds associated with the ticker
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	public static final void BondTickerAPISample()
		throws Exception
	{
		int iNumBonds = 0;
		String strTicker = "SLMA";

		JulianDate dtToday = DateUtil.Today();

		ExplicitBootDiscountCurve dc = DiscountCurveBuilder.CreateFromFlatRate (dtToday, "USD", null, 0.05);

		ExplicitBootDiscountCurve dcTSY = DiscountCurveBuilder.CreateFromFlatRate (dtToday, "USD", null, 0.04);

		ExplicitBootCreditCurve cc = CreditCurveBuilder.FromFlatHazard (dtToday.julian(), "CC", "USD", 0.02, 0.4);

		CurveSurfaceQuoteSet mktParams = MarketParamsBuilder.Credit (dc, cc);

		List<String> lsstrISIN = CreditAnalytics.GetISINsForTicker (strTicker);

		System.out.println ("Dumping: ISIN, FLOAT/FIXED, Bond, Yield, Z Spread, Opt Adj Spread, TSY Spread, Credit Basis, Credit Price");

		System.out.println ("---------------------------------------------------------------------------------------------------------");

		for (String strISIN : lsstrISIN) {
			Bond bond = CreditAnalytics.GetBond (strISIN);

			if (null != bond && !bond.variableCoupon() && !bond.exercised() && !bond.defaulted() &&
				bond.maturityDate().julian() > dtToday.julian()) {
				double dblZSpreadFromPrice = Double.NaN;
				double dblOASpreadFromPrice = Double.NaN;

				double dblYieldFromPrice = CreditAnalytics.BondYieldFromPrice (strISIN, dtToday, dc, 1.);

				if (!CreditAnalytics.IsBondFloater (strISIN)) {
					dblZSpreadFromPrice = CreditAnalytics.BondZSpreadFromPrice (strISIN, dtToday, dc, 1.);

					dblOASpreadFromPrice = CreditAnalytics.BondOASFromPrice (strISIN, dtToday, dc, 1.);
				}

				double dblTSYSpreadFromPrice = CreditAnalytics.BondTSYSpreadFromPrice (strISIN, dtToday, dc, dcTSY, 1.);

				double dblCreditBasisFromPrice = CreditAnalytics.BondCreditBasisFromPrice (strISIN, dtToday, dc, cc, 1.);

				double dblPECSFromPrice = CreditAnalytics.BondCreditBasisFromPrice (strISIN, dtToday, dc, cc, 1.);

				double dblBondCreditPrice = CreditAnalytics.BondCreditPrice (strISIN, dtToday, dc, cc);

				++iNumBonds;

				System.out.println (strISIN + FIELD_SEPARATOR +
					(bond.isFloater() ? "FLOAT   " : "FIXED   ") + bond.ticker() + FIELD_SEPARATOR +
					FormatUtil.FormatDouble (bond.couponMetrics (dtToday.julian(), null, mktParams).rate(), 2, 3, 100.) + FIELD_SEPARATOR +
					bond.maturityDate() + FIELD_SEPARATOR +
					FormatUtil.FormatDouble (dblYieldFromPrice, 2, 3, 100.) + FIELD_SEPARATOR +
					FormatUtil.FormatDouble (dblZSpreadFromPrice, 1, 3, 100.) + FIELD_SEPARATOR +
					FormatUtil.FormatDouble (dblOASpreadFromPrice, 1, 3, 100.) + FIELD_SEPARATOR +
					FormatUtil.FormatDouble (dblTSYSpreadFromPrice, 1, 3, 100.) + FIELD_SEPARATOR +
					FormatUtil.FormatDouble (dblCreditBasisFromPrice, 1, 3, 100.) + FIELD_SEPARATOR +
					FormatUtil.FormatDouble (dblPECSFromPrice, 1, 3, 100.) + FIELD_SEPARATOR +
					FormatUtil.FormatDouble (dblBondCreditPrice, 1, 3, 100.)
				);
			}
		}

		System.out.println ("Processed " + iNumBonds + " " + strTicker + " bonds!");

		for (String strISIN : lsstrISIN) {
			Bond bond = CreditAnalytics.GetBond (strISIN);

			System.out.println (
				strISIN + FIELD_SEPARATOR + bond.ticker() + FIELD_SEPARATOR +
				FormatUtil.FormatDouble (bond.couponMetrics (DateUtil.Today().julian(), null, null).rate(), 2, 3, 100.) + FIELD_SEPARATOR +
				bond.maturityDate() + FIELD_SEPARATOR +
				FormatUtil.FormatDouble (CreditAnalytics.GetBondDoubleField (strISIN, "OutstandingAmount"), 10, 0, 1.)
			);
		}

		/*
		 * Calculate the bucketed outstanding notional for all the bonds of this ticker
		 */

		JulianDate[] adtAscending = new JulianDate[5];

		adtAscending[0] = dtToday.addYears (3);

		adtAscending[1] = dtToday.addYears (5);

		adtAscending[2] = dtToday.addYears (10);

		adtAscending[3] = dtToday.addYears (30);

		adtAscending[4] = dtToday.addYears (60);

		Map<JulianDate, Double> mapOutstandingNotional = CreditAnalytics.GetIssuerAggregateOutstandingNotional (dtToday, strTicker, adtAscending);

		for (Map.Entry<JulianDate, Double> me : mapOutstandingNotional.entrySet())
			System.out.println ("[" + DateUtil.Today() + "=>" + me.getKey() + "] = " + me.getValue());
	}

	/*
	 * Sample demonstrating the usage of the (full set of) bond analytics API. Also shows the usage of the
	 * 		bond loss and coupon flow functionality
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final void BondAPISample()
		throws Exception
	{
		Set<String> setstrTickers = CreditAnalytics.GetAvailableTickers();

		for (String strTicker : setstrTickers)
			System.out.println (strTicker);

		List<String> lsstrISIN = CreditAnalytics.GetISINsForTicker ("DB");

		for (String strBondISIN : lsstrISIN)
			System.out.println (strBondISIN);

		String strISIN = "US78490FUS63"; // Short-dated floater maturing at 9/15/2012
		// String strISIN = "US760677FD19"; // Amortizer
		double dblZTMFromPrice = Double.NaN;
		double dblOASTMFromPrice = Double.NaN;
		double dblZSpreadFromPrice = Double.NaN;
		double dblOASpreadFromPrice = Double.NaN;
		double dblZSpreadFromTSYSpread = Double.NaN;
		double dblOASpreadFromTSYSpread = Double.NaN;

		ValuationCustomizationParams quotingParams = new ValuationCustomizationParams ("30/360", 2, true,
			null, "USD", false, null, null);

		Bond bond = CreditAnalytics.GetBond (strISIN);

		JulianDate dtToday = DateUtil.Today();

		ExplicitBootDiscountCurve dc = DiscountCurveBuilder.CreateFromFlatRate (dtToday, "USD", null, 0.03);

		ExplicitBootDiscountCurve dcTSY = DiscountCurveBuilder.CreateFromFlatRate (dtToday, "USD", null, 0.04);

		ExplicitBootCreditCurve cc = CreditCurveBuilder.FromFlatHazard (dtToday.julian(), "CC", "USD",
			0.02, 0.);

		ValuationParams valParams = ValuationParams.Spot (
			dtToday,
			0,
			"",
			Convention.DATE_ROLL_ACTUAL
		);

		CreditPricerParams pricerParams = CreditPricerParams.Standard();

		CurveSurfaceQuoteSet mktParams = MarketParamsBuilder.Credit (dc, cc);

		WorkoutInfo wi = CreditAnalytics.BondWorkoutInfoFromPrice (strISIN, dtToday, dc, 1.);

		double dblYieldFromPrice = CreditAnalytics.BondYieldFromPrice (strISIN, dtToday, dc, 1.);

		double dblYTMFromPrice = CreditAnalytics.BondYTMFromPrice (strISIN, valParams, dc, 1., quotingParams);

		if (!CreditAnalytics.IsBondFloater (strISIN)) {
			dblZSpreadFromPrice = CreditAnalytics.BondZSpreadFromPrice (strISIN, dtToday, dc, 1.);

			dblZTMFromPrice = CreditAnalytics.BondZTMFromPrice (strISIN, valParams, dc, 1., quotingParams);

			dblOASpreadFromPrice = CreditAnalytics.BondOASFromPrice (strISIN, dtToday, dc, 1.);

			dblOASTMFromPrice = CreditAnalytics.BondZTMFromPrice (strISIN, valParams, dc, 1., quotingParams);
		}

		double dblISpreadFromPrice = CreditAnalytics.BondISpreadFromPrice (strISIN, dtToday, dc, 1.);

		double dblITMFromPrice = CreditAnalytics.BondITMFromPrice (strISIN, valParams, dc, 1., quotingParams);

		double dblDiscountMarginFromPrice = CreditAnalytics.BondDiscountMarginFromPrice (strISIN, dtToday, dc, 1.);

		double dblDiscountMarginTMFromPrice = CreditAnalytics.BondDiscountMarginTMFromPrice (strISIN, valParams, dc, 1., quotingParams);

		double dblTSYSpreadFromPrice = CreditAnalytics.BondTSYSpreadFromPrice (strISIN, dtToday, dc, dcTSY, 1.);

		double dblTSYTMFromPrice = CreditAnalytics.BondTSYTMFromPrice (strISIN, valParams, dc, dcTSY, 1., quotingParams);

		double dblGSpreadFromPrice = CreditAnalytics.BondGSpreadFromPrice (strISIN, dtToday, dc, dcTSY, 1.);

		double dblGTMFromPrice = CreditAnalytics.BondGTMFromPrice (strISIN, valParams, dc, dcTSY, 1., quotingParams);

		double dblCreditBasisFromPrice = CreditAnalytics.BondCreditBasisFromPrice (strISIN, dtToday, dc, cc, 1.);

		double dblCreditBasisTMFromPrice = CreditAnalytics.BondCreditBasisTMFromPrice (strISIN, valParams, dc, cc, 1., quotingParams);

		double dblPECSFromPrice = CreditAnalytics.BondPECSFromPrice (strISIN, dtToday, dc, cc, 0.5);

		double dblPECSTMFromPrice = CreditAnalytics.BondPECSTMFromPrice (strISIN, valParams, dc, cc, 0.5, quotingParams);

		double dblPriceFromTSYSpread = CreditAnalytics.BondPriceFromTSYSpread (strISIN, dtToday, dc, dcTSY, 0.0271);

		double dblYieldFromTSYSpread = CreditAnalytics.BondYieldFromTSYSpread (strISIN, dtToday, dcTSY, 0.0271);

		if (!CreditAnalytics.IsBondFloater (strISIN)) {
			dblZSpreadFromTSYSpread = CreditAnalytics.BondZSpreadFromTSYSpread (strISIN, dtToday, dc, dcTSY, 0.0271);

			dblOASpreadFromTSYSpread = CreditAnalytics.BondOASFromTSYSpread (strISIN, dtToday, dc, dcTSY, 0.0271);
		}

		double dblISpreadFromTSYSpread = CreditAnalytics.BondISpreadFromTSYSpread (strISIN, dtToday, dc, dcTSY, 0.0271);

		double dblDiscountMarginFromTSYSpread = CreditAnalytics.BondDiscountMarginFromTSYSpread (strISIN, dtToday, dc, dcTSY, 0.0271);

		double dblGSpreadFromTSYSpread = CreditAnalytics.BondGSpreadFromTSYSpread (strISIN, dtToday, dc, dcTSY, 0.0271);

		double dblCreditBasisFromTSYSpread = CreditAnalytics.BondCreditBasisFromTSYSpread (strISIN, dtToday, dc, dcTSY, cc, 0.0271);

		double dblPECSFromTSYSpread = CreditAnalytics.BondCreditBasisFromTSYSpread (strISIN, dtToday, dc, dcTSY, cc, 0.0971);

		double dblBondCreditPrice = CreditAnalytics.BondCreditPrice (strISIN, valParams, dc, cc, quotingParams);

		JulianDate dtPreviousCoupon = CreditAnalytics.PreviousCouponDate (strISIN, dtToday);

		JulianDate dtCurrentCoupon = CreditAnalytics.NextCouponDate (strISIN, dtToday);

		JulianDate dtNextCoupon = CreditAnalytics.NextCouponDate (strISIN, dtToday);

		JulianDate dtEffective = CreditAnalytics.EffectiveDate (strISIN);

		JulianDate dtMaturity = CreditAnalytics.MaturityDate (strISIN);

		boolean bInFirstPeriod = CreditAnalytics.InFirstPeriod (strISIN, dtToday.julian());

		boolean bInLastPeriod = CreditAnalytics.InLastPeriod (strISIN, dtToday.julian());

		ExerciseInfo nei = CreditAnalytics.NextExerciseInfo (strISIN, dtToday);

		System.out.println (strISIN + "    " + bond.ticker() + " " + FormatUtil.FormatDouble (bond.couponMetrics
			(valParams.valueDate(), valParams, mktParams).rate(), 2, 3, 100.) + " " + bond.maturityDate());

		System.out.println ("Work-out date From Price: " + new JulianDate (wi.date()));

		System.out.println ("Work-out factor From Price: " + wi.factor());

		System.out.println ("Work-out Yield From Price: " + FormatUtil.FormatDouble (wi.yield(), 2, 3, 100.));

		System.out.println ("Work-out Type for Price: " + org.drip.analytics.support.AnalyticsHelper.WorkoutTypeToString (wi.type()));

		System.out.println ("Yield From Price: " + FormatUtil.FormatDouble (dblYieldFromPrice, 2, 3, 100.) + " / " + FormatUtil.FormatDouble (dblYTMFromPrice, 2, 3, 100.));

		System.out.println ("Z Spread From Price: " + (int) (10000. * dblZSpreadFromPrice) + " / " + (int) (10000. * dblZTMFromPrice));

		System.out.println ("Option Adj Spread From Price: " + (int) (10000. * dblOASpreadFromPrice) + " / " + (int) (10000. * dblOASTMFromPrice));

		System.out.println ("I Spread From Price: " + (int) (10000. * dblISpreadFromPrice) + " / " + (int) (10000. * dblITMFromPrice));

		System.out.println ("Discount Margin From Price: " + (int) (10000. * dblDiscountMarginFromPrice) + " / " + (int) (10000. * dblDiscountMarginTMFromPrice));

		System.out.println ("TSY Spread From Price: " + (int) (10000. * dblTSYSpreadFromPrice) + " / " + (int) (10000. * dblTSYTMFromPrice));

		System.out.println ("G Spread From Price: " + (int) (10000. * dblGSpreadFromPrice) + " / " + (int) (10000. * dblGTMFromPrice));

		System.out.println ("Credit Basis From Price: " + (int) (10000. * dblCreditBasisFromPrice) + " / " + (int) (10000. * dblCreditBasisTMFromPrice));

		System.out.println ("PECS From Price: " + (int) (10000. * dblPECSFromPrice) + " / " + (int) (10000. * dblPECSTMFromPrice));

		System.out.println ("Price From TSY Spread: " + FormatUtil.FormatDouble (dblPriceFromTSYSpread, 2, 3, 100.));

		System.out.println ("Yield From TSY Spread: " + FormatUtil.FormatDouble (dblYieldFromTSYSpread, 2, 3, 100.));

		System.out.println ("Z Spread From TSY Spread: " + (int) (10000. * dblZSpreadFromTSYSpread));

		System.out.println ("OAS From TSY Spread: " + (int) (10000. * dblOASpreadFromTSYSpread));

		System.out.println ("I Spread From TSY Spread: " + (int) (10000. * dblISpreadFromTSYSpread));

		System.out.println ("Discount Margin From TSY Spread: " + (int) (10000. * dblDiscountMarginFromTSYSpread));

		System.out.println ("G Spread From TSY Spread: " + (int) (10000. * dblGSpreadFromTSYSpread));

		System.out.println ("Credit Basis From TSY Spread: " + (int) (10000. * dblCreditBasisFromTSYSpread));

		System.out.println ("PECS From TSY Spread: " + (int) (10000. * dblPECSFromTSYSpread));

		System.out.println ("Credit Risky Price: " + FormatUtil.FormatDouble (dblBondCreditPrice, 2, 3, 100.));

		System.out.println ("Valuation Date: " + DateUtil.Today());

		System.out.println ("Effective Date: " + dtEffective);

		System.out.println ("Maturity Date: " + dtMaturity);

		System.out.println ("Is Val Date in the first period? " + bInFirstPeriod);

		System.out.println ("Is Val Date in the last period? " + bInLastPeriod);

		System.out.println ("Previous Coupon Date: " + dtPreviousCoupon);

		System.out.println ("Current Coupon Date: " + dtCurrentCoupon);

		System.out.println ("Next Coupon Date: " + dtNextCoupon);

		System.out.println ("Next Exercise Date: " + new JulianDate (nei.date()));

		System.out.println ("Next Exercise Factor: " + nei.factor());

		System.out.println ("Next Exercise Type: " + org.drip.analytics.support.AnalyticsHelper.WorkoutTypeToString (nei.workoutType()));

		for (LossQuadratureMetrics dp : bond.lossFlow (valParams, pricerParams, mktParams))
			System.out.println (
				DateUtil.FromJulian (dp.start()) + FIELD_SEPARATOR +
				DateUtil.FromJulian (dp.end()) + FIELD_SEPARATOR +
				FormatUtil.FormatDouble (dp.effectiveNotional(), 1, 0, 1.) + FIELD_SEPARATOR +
				FormatUtil.FormatDouble (dp.effectiveRecovery(), 1, 2, 1.) + FIELD_SEPARATOR +
				FormatUtil.FormatDouble (dp.effectiveDF(), 1, 4, 1.)  + FIELD_SEPARATOR +
				FormatUtil.FormatDouble (dp.startSurvival(), 1, 4, 1.) + FIELD_SEPARATOR +
				FormatUtil.FormatDouble (dp.endSurvival(), 1, 4, 1.)
			);
	}

	public static final void main (
		final String astrArgs[])
		throws Exception
	{
		// String strConfig = "c:\\Lakshmi\\BondAnal\\Config.xml";

		String strConfig = "";

		if (!CreditAnalytics.Init (strConfig)) {
			System.out.println ("Cannot fully init FI!");

			System.exit (303);
		}

		BondEODMeasuresAPISample();

		BondEODSample();

		BondAPISample();
	}
}
