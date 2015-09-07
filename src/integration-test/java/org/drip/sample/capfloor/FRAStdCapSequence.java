
package org.drip.sample.capfloor;

import java.util.*;

import org.drip.analytics.date.*;
import org.drip.analytics.rates.*;
import org.drip.analytics.support.CompositePeriodBuilder;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteSet;
import org.drip.param.period.*;
import org.drip.param.valuation.ValuationParams;
import org.drip.pricer.option.BlackScholesAlgorithm;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.definition.CalibratableFixedIncomeComponent;
import org.drip.product.fra.FRAStandardCapFloor;
import org.drip.product.params.LastTradingDateSetting;
import org.drip.product.rates.*;
import org.drip.quant.common.FormatUtil;
import org.drip.service.api.CreditAnalytics;
import org.drip.state.identifier.ForwardLabel;

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
 * FRAStdCapSequence demonstrates the Product Creation, Market Parameters Construction, and Valuation of a
 * 	Sequence of Standard FRA Caps. The Marks and the Valuation References are sourced from:
 * 
 * 	- Brace, A., D. Gatarek, and M. Musiela (1997): The Market Model of Interest Rate Dynamics, Mathematical
 * 		Finance 7 (2), 127-155.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FRAStdCapSequence {

	private static final FixFloatComponent OTCFixFloat (
		final JulianDate dtSpot,
		final String strCurrency,
		final String strMaturityTenor,
		final double dblCoupon)
	{
		FixedFloatSwapConvention ffConv = IBORFixedFloatContainer.ConventionFromJurisdiction (
			strCurrency,
			"ALL",
			strMaturityTenor,
			"MAIN"
		);

		return ffConv.createFixFloatComponent (
			dtSpot,
			strMaturityTenor,
			dblCoupon,
			0.,
			1.
		);
	}

	private static final CalibratableFixedIncomeComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate dtEffective,
		final int[] aiDay,
		final int iNumFuture,
		final String strCurrency)
		throws Exception
	{
		CalibratableFixedIncomeComponent[] aCalibComp = new CalibratableFixedIncomeComponent[aiDay.length + iNumFuture];

		for (int i = 0; i < aiDay.length; ++i)
			aCalibComp[i] = SingleStreamComponentBuilder.Deposit (
				dtEffective,
				dtEffective.addBusDays (
					aiDay[i],
					strCurrency
				),
				ForwardLabel.Create (
					strCurrency,
					"3M"
				)
			);

		CalibratableFixedIncomeComponent[] aEDF = SingleStreamComponentBuilder.FuturesPack (
			dtEffective,
			iNumFuture,
			strCurrency
		);

		for (int i = aiDay.length; i < aiDay.length + iNumFuture; ++i)
			aCalibComp[i] = aEDF[i - aiDay.length];

		return aCalibComp;
	}

	private static final FixFloatComponent[] SwapInstrumentsFromMaturityTenor (
		final JulianDate dtSpot,
		final String strCurrency,
		final String[] astrMaturityTenor,
		final double[] adblCoupon)
		throws Exception
	{
		FixFloatComponent[] aIRS = new FixFloatComponent[astrMaturityTenor.length];

		for (int i = 0; i < astrMaturityTenor.length; ++i)
			aIRS[i] = OTCFixFloat (
				dtSpot,
				strCurrency,
				astrMaturityTenor[i],
				adblCoupon[i]
			);

		return aIRS;
	}

	private static final DiscountCurve MakeDC (
		final JulianDate dtSpot,
		final String strCurrency)
		throws Exception
	{
		/*
		 * Construct the array of Deposit instruments and their quotes.
		 */

		CalibratableFixedIncomeComponent[] aDepositComp = DepositInstrumentsFromMaturityDays (
			dtSpot,
			new int[] {
				30,
				60,
				91,
				182,
				273
			},
			0,
			strCurrency
		);

		double[] adblDepositQuote = new double[] {
			0.0668750,	//  30D
			0.0675000,	//  60D
			0.0678125,	//  91D
			0.0712500,	// 182D
			0.0750000	// 273D
		};

		String[] astrDepositManifestMeasure = new String[] {
			"ForwardRate", //  30D
			"ForwardRate", //  60D
			"ForwardRate", //  91D
			"ForwardRate", // 182D
			"ForwardRate"  // 273D
		};

		/*
		 * Construct the array of Swap instruments and their quotes.
		 */

		double[] adblSwapQuote = new double[] {
			0.08265,    //  2Y
			0.08550,    //  3Y
			0.08655,    //  4Y
			0.08770,    //  5Y
			0.08910,    //  7Y
			0.08920     // 10Y
		};

		String[] astrSwapManifestMeasure = new String[] {
			"SwapRate",    //  2Y
			"SwapRate",    //  3Y
			"SwapRate",    //  4Y
			"SwapRate",    //  5Y
			"SwapRate",    //  7Y
			"SwapRate"     // 10Y
		};

		CalibratableFixedIncomeComponent[] aSwapComp = SwapInstrumentsFromMaturityTenor (
			dtSpot,
			strCurrency,
			new String[] {
				"2Y",
				"3Y",
				"4Y",
				"5Y",
				"7Y",
				"10Y"
			},
			adblSwapQuote
		);

		/*
		 * Construct a shape preserving and smoothing KLK Hyperbolic Spline from the cash/swap instruments.
		 */

		return ScenarioDiscountCurveBuilder.CubicKLKHyperbolicDFRateShapePreserver (
			"KLK_HYPERBOLIC_SHAPE_TEMPLATE",
			new ValuationParams (
				dtSpot,
				dtSpot,
				strCurrency
			),
			aDepositComp,
			adblDepositQuote,
			astrDepositManifestMeasure,
			aSwapComp,
			adblSwapQuote,
			astrSwapManifestMeasure,
			false
		);
	}

	private static final FRAStandardCapFloor MakeCap (
		final JulianDate dtEffective,
		final ForwardLabel fri,
		final String strMaturityTenor,
		final String strManifestMeasure,
		final double dblStrike)
		throws Exception
	{
		ComposableFloatingUnitSetting cfus = new ComposableFloatingUnitSetting (
			fri.tenor(),
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_SINGLE,
			null,
			fri,
			CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
			0.
		);

		CompositePeriodSetting cps = new CompositePeriodSetting (
			4,
			fri.tenor(),
			fri.currency(),
			null,
			1.,
			null,
			null,
			null,
			null
		);

		Stream floatStream = new Stream (
			CompositePeriodBuilder.FloatingCompositeUnit (
				CompositePeriodBuilder.RegularEdgeDates (
					dtEffective.julian(),
					fri.tenor(),
					strMaturityTenor,
					null
				),
				cps,
				cfus
			)
		);

		return new FRAStandardCapFloor (
			"FRA_CAP",
			floatStream,
			strManifestMeasure,
			true,
			dblStrike,
			new LastTradingDateSetting (
				LastTradingDateSetting.MID_CURVE_OPTION_QUARTERLY,
				"",
				Double.NaN
			),
			null,
			new BlackScholesAlgorithm()
		);
	}

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		CreditAnalytics.Init ("");

		JulianDate dtSpot = DateUtil.CreateFromYMD (
			1995,
			DateUtil.FEBRUARY,
			3
		);

		String strFRATenor = "3M";
		String strCurrency = "GBP";
		String strManifestMeasure = "ParForward";

		ForwardLabel fri = ForwardLabel.Create (
			strCurrency,
			strFRATenor
		);

		DiscountCurve dc = MakeDC (
			dtSpot,
			strCurrency
		);

		ForwardCurve fcNative = dc.nativeForwardCurve (strFRATenor);

		ValuationParams valParams = new ValuationParams (
			dtSpot,
			dtSpot,
			strCurrency
		);

		CurveSurfaceQuoteSet mktParams = MarketParamsBuilder.Create (
			dc,
			fcNative,
			null,
			null,
			null,
			null,
			null,
			null
		);

		String[] astrMaturityTenor = new String[] {
			 "1Y",
			 "2Y",
			 "3Y",
			 "4Y",
			 "5Y",
			 "7Y",
			"10Y"
		};

		double[] adblATMStrike = new double[] {
			0.0788, //  "1Y",
			0.0839, // 	"2Y",
			0.0864, //  "3Y",
			0.0869, //  "4Y",
			0.0879, //  "5Y",
			0.0890, //  "7Y",
			0.0889  // "10Y"
		};

		double[] adblATMVol = new double[] {
			0.1550, //  "1Y",
			0.1775, // 	"2Y",
			0.1800, //  "3Y",
			0.1775, //  "4Y",
			0.1775, //  "5Y",
			0.1650, //  "7Y",
			0.1550  // "10Y"
		};

		Map<JulianDate, Double> mapDateVol = new TreeMap<JulianDate, Double>();

		for (int i = 0; i < astrMaturityTenor.length; ++i) {
			FRAStandardCapFloor cap = MakeCap (
				dtSpot,
				fri,
				astrMaturityTenor[i],
				strManifestMeasure,
				adblATMStrike[i]
			);

			Map<String, Double> mapCapStreamOutput = cap.stream().value (
				valParams,
				null,
				mktParams,
				null
			);

			double dblCapStreamFairPremium = mapCapStreamOutput.get ("FairPremium");

			FixFloatComponent swap = OTCFixFloat (
				dtSpot,
				strCurrency,
				astrMaturityTenor[i],
				0.
			);

			Map<String, Double> mapSwapOutput = swap.value (
				valParams,
				null,
				mktParams,
				null
			);

			double dblSwapRate = mapSwapOutput.get ("FairPremium");

			double dblCapPrice = cap.priceFromFlatVolatility (
				valParams,
				null,
				mktParams,
				null,
				adblATMVol[i]
			);

			cap.stripPiecewiseForwardVolatility (
				valParams,
				null,
				mktParams,
				null,
				adblATMVol[i],
				mapDateVol
			);

			System.out.println (
				"\tCap  " + cap.maturityDate() + " | " +
				FormatUtil.FormatDouble (dblCapStreamFairPremium, 1, 2, 100.) + "% |" +
				FormatUtil.FormatDouble (dblSwapRate, 1, 2, 100.) + "% |" +
				FormatUtil.FormatDouble (cap.strike(), 1, 2, 100.) + "% |" +
				FormatUtil.FormatDouble (adblATMVol[i], 2, 2, 100.) + "% |" +
				FormatUtil.FormatDouble (dblCapPrice, 1, 0, 10000.) + " ||"
			);
		}

		System.out.println ("\n\n\t---------------------------------------------------");

		System.out.println ("\t-----  CALIBRATED FORWARD VOLATILITY NODES --------");

		System.out.println ("\t---------------------------------------------------\n");

		for (Map.Entry<JulianDate, Double> me : mapDateVol.entrySet())
			System.out.println (
				"\t" +
				me.getKey() + " => " +
				FormatUtil.FormatDouble (me.getValue(), 2, 2, 100.) + "%  ||"
			);

		System.out.println ("\t---------------------------------------------------");

		System.out.println ("\t---------------------------------------------------------------");

		double[] adblATMPrice = new double[] {
			0.0027, //  "1Y",
			0.0100, // 	"2Y",
			0.0185, //  "3Y",
			0.0267, //  "4Y",
			0.0360, //  "5Y",
			0.0511, //  "7Y",
			0.0703  // "10Y"
		};

		for (int i = 0; i < astrMaturityTenor.length; ++i) {
			FRAStandardCapFloor cap = MakeCap (
				dtSpot,
				fri,
				astrMaturityTenor[i],
				strManifestMeasure,
				adblATMStrike[i]
			);

			double dblATMVolatility = cap.volatilityFromATMPrice (
				valParams,
				null,
				mktParams,
				null,
				adblATMPrice[i]
			);

			Map<String, Double> mapCapStreamOutput = cap.stream().value (
				valParams,
				null,
				mktParams,
				null
			);

			double dblCapStreamFairPremium = mapCapStreamOutput.get ("FairPremium");

			System.out.println (
				"\tCap ATM Volatility  " + cap.maturityDate() + " | " +
				FormatUtil.FormatDouble (adblATMPrice[i], 2, 2, 100.) + "% |" +
				FormatUtil.FormatDouble (dblCapStreamFairPremium, 2, 2, 100.) + "% |" +
				FormatUtil.FormatDouble (dblATMVolatility, 2, 2, 100.) + "% ||"
			);
		}

		System.out.println ("\t---------------------------------------------------------------");

		System.out.println ("\t---------------------------------------------------------------");
	}
}
