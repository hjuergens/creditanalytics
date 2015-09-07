
package org.drip.sample.option;

import java.util.*;

import org.drip.analytics.date.*;
import org.drip.analytics.definition.TermStructure;
import org.drip.analytics.rates.DiscountCurve;
import org.drip.function.R1ToR1.FlatUnivariate;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.valuation.*;
import org.drip.pricer.option.BlackScholesAlgorithm;
import org.drip.product.creator.*;
import org.drip.product.definition.CalibratableFixedIncomeComponent;
import org.drip.product.option.EuropeanCallPut;
import org.drip.product.rates.*;
import org.drip.quant.common.FormatUtil;
import org.drip.service.api.CreditAnalytics;
import org.drip.state.identifier.ForwardLabel;

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
 * ATMTermStructure contains an illustration of the Calibration and Extraction of the Deterministic ATM
 * 	Price and Volatility Term Structures. This does not deal with Local Volatility Surfaces.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class ATMTermStructure {

	private static final FixFloatComponent OTCIRS (
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

	/*
	 * Construct the Array of Deposit Instruments from the given set of parameters
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final CalibratableFixedIncomeComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate dtEffective,
		final int[] aiDay,
		final int iNumFutures,
		final String strCurrency)
		throws Exception
	{
		CalibratableFixedIncomeComponent[] aCalibComp = new CalibratableFixedIncomeComponent[aiDay.length + iNumFutures];

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
			iNumFutures,
			strCurrency
		);

		for (int i = aiDay.length; i < aiDay.length + iNumFutures; ++i)
			aCalibComp[i] = aEDF[i - aiDay.length];

		return aCalibComp;
	}

	/*
	 * Construct the Array of Swap Instruments from the given set of parameters
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final FixFloatComponent[] SwapInstrumentsFromMaturityTenor (
		final JulianDate dtSpot,
		final String strCurrency,
		final String[] astrMaturityTenor,
		final double[] adblCoupon)
		throws Exception
	{
		FixFloatComponent[] aIRS = new FixFloatComponent[astrMaturityTenor.length];

		for (int i = 0; i < astrMaturityTenor.length; ++i) {
			FixFloatComponent irs = OTCIRS (
				dtSpot,
				strCurrency,
				astrMaturityTenor[i],
				adblCoupon[i]
			);

			irs.setPrimaryCode ("IRS." + astrMaturityTenor[i] + "." + strCurrency);

			aIRS[i] = irs;
		}

		return aIRS;
	}

	/*
	 * Construct the discount curve using the following steps:
	 * 	- Construct the array of cash instruments and their quotes.
	 * 	- Construct the array of swap instruments and their quotes.
	 * 	- Construct a shape preserving and smoothing KLK Hyperbolic Spline from the cash/swap instruments.
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

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
				1, 2, 3, 7, 14, 21, 30, 60
			},
			0,
			strCurrency
		);

		double[] adblDepositQuote = new double[] {
			0.01200, 0.01200, 0.01200, 0.01450, 0.01550, 0.01600, 0.01660, 0.01850
		};

		String[] astrDepositManifestMeasure = new String[] {
			"ForwardRate", "ForwardRate", "ForwardRate", "ForwardRate", "ForwardRate", "ForwardRate", "ForwardRate", "ForwardRate"
		};

		/*
		 * Construct the array of Swap instruments and their quotes.
		 */

		double[] adblSwapQuote = new double[] {
			0.02604,    //  4Y
			0.02808,    //  5Y
			0.02983,    //  6Y
			0.03136,    //  7Y
			0.03268,    //  8Y
			0.03383,    //  9Y
			0.03488,    // 10Y
			0.03583,    // 11Y
			0.03668,    // 12Y
			0.03833,    // 15Y
			0.03854,    // 20Y
			0.03672,    // 25Y
			0.03510,    // 30Y
			0.03266,    // 40Y
			0.03145     // 50Y
		};

		String[] astrSwapManifestMeasure = new String[] {
			"SwapRate",    //  4Y
			"SwapRate",    //  5Y
			"SwapRate",    //  6Y
			"SwapRate",    //  7Y
			"SwapRate",    //  8Y
			"SwapRate",    //  9Y
			"SwapRate",    // 10Y
			"SwapRate",    // 11Y
			"SwapRate",    // 12Y
			"SwapRate",    // 15Y
			"SwapRate",    // 20Y
			"SwapRate",    // 25Y
			"SwapRate",    // 30Y
			"SwapRate",    // 40Y
			"SwapRate"     // 50Y
		};

		CalibratableFixedIncomeComponent[] aSwapComp = SwapInstrumentsFromMaturityTenor (
			dtSpot,
			strCurrency,
			new String[] {
				"4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "11Y", "12Y", "15Y", "20Y", "25Y", "30Y", "40Y", "50Y"
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
			true
		);
	}

	private static final double ATMCall (
		final JulianDate dtMaturity,
		final ValuationParams valParams,
		final DiscountCurve dc,
		final double dblVolatility,
		final String strMeasure)
		throws Exception
	{
		Map<String, Double> mapOptionCalc = new EuropeanCallPut (dtMaturity, 1.).value (
			valParams,
			1.,
			false,
			dc,
			new FlatUnivariate (dblVolatility),
			new BlackScholesAlgorithm()
		);

		return mapOptionCalc.get (strMeasure);
	}

	private static final void InputNodeReplicator (
		final TermStructure ts,
		final String[] astrMaturityTenor,
		final double[] dblNodeInput)
		throws Exception
	{
		System.out.println ("\n\t" + ts.label());

		System.out.println ("\n\t|--------------------------|");

		System.out.println ("\t| TNR =>   CALC  |  INPUT  |");

		System.out.println ("\t|--------------------------|");

		for (int i = 0; i < astrMaturityTenor.length; ++i)
			System.out.println ("\t| " + astrMaturityTenor[i] + " => " +
				FormatUtil.FormatDouble (ts.node (astrMaturityTenor[i]), 2, 2, 100.) + "% | " +
				FormatUtil.FormatDouble (dblNodeInput[i], 2, 2, 100.) + "% |");

		System.out.println ("\t|--------------------------|");
	}

	private static final void OffGrid (
		final String strHeader,
		final String[] astrLabel,
		final TermStructure[] aTS,
		final String[] astrMaturityTenor)
		throws Exception
	{
		System.out.println ("\n\n\t\t" + strHeader + "\n");

		System.out.print ("\t| TNR =>");

		for (int i = 0; i < aTS.length; ++i)
			System.out.print (" " + astrLabel[i] + " | ");

		System.out.println ("\n");

		for (int i = 0; i < astrMaturityTenor.length; ++i) {
			System.out.print ("\t| " + astrMaturityTenor[i] + " =>");

			for (int j = 0; j < aTS.length; ++j)
				System.out.print ("  " + FormatUtil.FormatDouble (aTS[j].node (astrMaturityTenor[i]), 2, 2, 100.) + "%   | ");

			System.out.print ("\n");
		}

		System.out.println ("\n");
	}

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		/*
		 * Initialize the Credit Analytics Library
		 */

		CreditAnalytics.Init ("");

		JulianDate dtToday = DateUtil.Today();

		ValuationParams valParams = new ValuationParams (
			dtToday,
			dtToday,
			"USD"
		);

		/*
		 * Construct the Discount Curve using its instruments and quotes
		 */

		DiscountCurve dc = MakeDC (
			dtToday,
			"USD"
		);

		String[] astrMaturityTenor = new String[] {
			"06M", "01Y", "02Y", "03Y", "04Y", "05Y", "07Y", "10Y", "15Y", "20Y"
		};
		double[] adblVolatility = new double[] {
			0.20, 0.23, 0.27, 0.30, 0.33, 0.35, 0.34, 0.29, 0.26, 0.19
		};
		double[] adblCallPrice = new double[adblVolatility.length];
		double[] adblImpliedCallVolatility = new double[adblVolatility.length];

		for (int i = 0; i < astrMaturityTenor.length; ++i) {
			adblCallPrice[i] = ATMCall (
				dtToday.addTenor (astrMaturityTenor[i]),
				valParams,
				dc,
				adblVolatility[i],
				"CallPrice");

			adblImpliedCallVolatility[i] = ATMCall (
				dtToday.addTenor (astrMaturityTenor[i]),
				valParams,
				dc,
				adblVolatility[i],
				"ImpliedCallVolatility");
		}

		TermStructure tsCallPriceCubicPoly = ScenarioTermStructureBuilder.CubicPolynomialTermStructure (
			"CUBIC_POLY_CALLPRICE_TERMSTRUCTURE",
			dtToday,
			"USD",
			null,
			astrMaturityTenor,
			adblCallPrice
		);

		TermStructure tsCallPriceQuarticPoly = ScenarioTermStructureBuilder.QuarticPolynomialTermStructure (
			"QUARTIC_POLY_CALLPRICE_TERMSTRUCTURE",
			dtToday,
			"USD",
			null,
			astrMaturityTenor,
			adblCallPrice
		);

		TermStructure tsCallPriceKaklisPandelis = ScenarioTermStructureBuilder.KaklisPandelisTermStructure (
			"KAKLIS_PANDELIS_CALLPRICE_TERMSTRUCTURE",
			dtToday,
			"USD",
			null,
			astrMaturityTenor,
			adblCallPrice
		);

		TermStructure tsCallPriceKLKHyperbolic = ScenarioTermStructureBuilder.KLKHyperbolicTermStructure (
			"KLK_HYPERBOLIC_CALLPRICE_TERMSTRUCTURE",
			dtToday,
			"USD",
			null,
			astrMaturityTenor,
			adblCallPrice,
			1.
		);

		TermStructure tsCallPriceKLKRationalLinear = ScenarioTermStructureBuilder.KLKRationalLinearTermStructure (
			"KLK_RATIONAL_LINEAR_CALLPRICE_TERMSTRUCTURE",
			dtToday,
			"USD",
			null,
			astrMaturityTenor,
			adblCallPrice,
			1.
		);

		TermStructure tsCallPriceKLKRationalQuadratic = ScenarioTermStructureBuilder.KLKRationalQuadraticTermStructure (
			"KLK_RATIONAL_QUADRATIC_CALLPRICE_TERMSTRUCTURE",
			dtToday,
			"USD",
			null,
			astrMaturityTenor,
			adblCallPrice,
			0.0001
		);

		InputNodeReplicator (
			tsCallPriceCubicPoly,
			astrMaturityTenor,
			adblCallPrice
		);

		TermStructure tsCallVolatilityCubicPoly = ScenarioTermStructureBuilder.CubicPolynomialTermStructure (
			"CUBIC_POLY_CALLVOL_TERMSTRUCTURE",
			dtToday,
			"USD",
			null,
			astrMaturityTenor,
			adblImpliedCallVolatility
		);

		TermStructure tsCallVolatilityQuarticPoly = ScenarioTermStructureBuilder.QuarticPolynomialTermStructure (
			"QUARTIC_POLY_CALLVOL_TERMSTRUCTURE",
			dtToday,
			"USD",
			null,
			astrMaturityTenor,
			adblImpliedCallVolatility
		);

		TermStructure tsCallVolatilityKaklisPandelis = ScenarioTermStructureBuilder.KaklisPandelisTermStructure (
			"KAKLIS_PANDELIS_CALLVOL_TERMSTRUCTURE",
			dtToday,
			"USD",
			null,
			astrMaturityTenor,
			adblImpliedCallVolatility
		);

		TermStructure tsCallVolatilityKLKHyperbolic = ScenarioTermStructureBuilder.KLKHyperbolicTermStructure (
			"KLK_HYPERBOLIC_CALLVOL_TERMSTRUCTURE",
			dtToday,
			"USD",
			null,
			astrMaturityTenor,
			adblImpliedCallVolatility,
			1.
		);

		TermStructure tsCallVolatilityKLKRationalLinear = ScenarioTermStructureBuilder.KLKRationalLinearTermStructure (
			"KLK_RATIONAL_LINEAR_CALLVOL_TERMSTRUCTURE",
			dtToday,
			"USD",
			null,
			astrMaturityTenor,
			adblImpliedCallVolatility,
			1.
		);

		TermStructure tsCallVolatilityKLKRationalQuadratic = ScenarioTermStructureBuilder.KLKRationalQuadraticTermStructure (
			"KLK_RATIONAL_QUADRATIC_CALLVOL_TERMSTRUCTURE",
			dtToday,
			"USD",
			null,
			astrMaturityTenor,
			adblImpliedCallVolatility,
			0.0001
		);

		InputNodeReplicator (
			tsCallVolatilityCubicPoly,
			astrMaturityTenor,
			adblImpliedCallVolatility
		);

		String[] astrOffGridTenor = new String[] {
			"03M", "09M", "18M", "30Y", "42M", "54M", "06Y", "09Y", "12Y", "18Y", "25Y"
		};

		OffGrid (
			"ATM_CALLPRICE_TERM_STRUCTURE",
			new String[] {
				"Cubic Poly", "Quart Poly", "KaklisPand", "KLKHyperbl", "KLKRatlLin", "KLKRatlQua"
			},
			new TermStructure[] {
				tsCallPriceCubicPoly,
				tsCallPriceQuarticPoly,
				tsCallPriceKaklisPandelis,
				tsCallPriceKLKHyperbolic,
				tsCallPriceKLKRationalLinear,
				tsCallPriceKLKRationalQuadratic
			},
			astrOffGridTenor
		);

		OffGrid (
			"ATM_CALLVOL_TERM_STRUCTURE",
			new String[] {
				"Cubic Poly", "Quart Poly", "KaklisPand", "KLKHyperbl", "KLKRatlLin", "KLKRatlQua"
			},
			new TermStructure[] {
				tsCallVolatilityCubicPoly,
				tsCallVolatilityQuarticPoly,
				tsCallVolatilityKaklisPandelis,
				tsCallVolatilityKLKHyperbolic,
				tsCallVolatilityKLKRationalLinear,
				tsCallVolatilityKLKRationalQuadratic
			},
			astrOffGridTenor
		);
	}
}
