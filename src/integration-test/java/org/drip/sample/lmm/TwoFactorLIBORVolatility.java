
package org.drip.sample.lmm;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.analytics.definition.MarketSurface;
import org.drip.analytics.rates.DiscountCurve;
import org.drip.analytics.rates.ForwardCurve;
import org.drip.dynamics.lmm.LognormalLIBORVolatility;
import org.drip.market.otc.FixedFloatSwapConvention;
import org.drip.market.otc.IBORFixedFloatContainer;
import org.drip.param.creator.ScenarioDiscountCurveBuilder;
import org.drip.param.creator.ScenarioMarketSurfaceBuilder;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.definition.CalibratableFixedIncomeComponent;
import org.drip.product.rates.FixFloatComponent;
import org.drip.quant.common.FormatUtil;
import org.drip.sequence.random.BoxMullerGaussian;
import org.drip.sequence.random.PrincipalFactorSequenceGenerator;
import org.drip.sequence.random.UnivariateSequenceGenerator;
import org.drip.service.api.CreditAnalytics;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.SegmentCustomBuilderControl;
import org.drip.spline.params.SegmentInelasticDesignControl;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
import org.drip.state.identifier.ForwardLabel;
import org.testng.annotations.Test;

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
 * TwoFactorLIBORVolatility demonstrates the Construction and Usage of the 2 Factor LIBOR Forward Rate
 *  Volatility. The References are:
 * 
 *  1) Goldys, B., M. Musiela, and D. Sondermann (1994): Log-normality of Rates and Term Structure Models,
 *  	The University of New South Wales.
 * 
 *  2) Musiela, M. (1994): Nominal Annual Rates and Log-normal Volatility Structure, The University of New
 *   	South Wales.
 * 
 * 	3) Brace, A., D. Gatarek, and M. Musiela (1997): The Market Model of Interest Rate Dynamics, Mathematical
 * 		Finance 7 (2), 127-155.
 *
 * @author Lakshmi Krishnamurthy
 */

public class TwoFactorLIBORVolatility {

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

	private static final MarketSurface FactorFlatVolatilitySurface (
		final JulianDate dtStart,
		final String strCurrency,
		final String[] astrMaturityTenor,
		final double[] adblFactorFlatVolatility,
		final double[] adblTermStructureLoading)
		throws Exception
	{
		int iNumNode = astrMaturityTenor.length + 1;
		double[] adblDate = new double[iNumNode];
		double[][] aadblVolatility = new double[iNumNode][iNumNode];

		for (int i = 0; i < iNumNode; ++i)
			adblDate[i] = 0 == i ? adblDate[i] = dtStart.julian() : dtStart.addTenor (astrMaturityTenor[i - 1]).julian();

		for (int i = 0; i < iNumNode; ++i) {
			for (int j = 0; j < iNumNode; ++j)
				aadblVolatility[i][j] =
					0 == i || 0 == j ?
					adblFactorFlatVolatility[0] :
					adblTermStructureLoading[i - 1] * adblFactorFlatVolatility[j - 1];
		}

		return ScenarioMarketSurfaceBuilder.CustomSplineWireSurface (
			"VIEW_TARGET_VOLATILITY_SURFACE",
			dtStart,
			strCurrency,
			null,
			adblDate,
			adblDate,
			aadblVolatility,
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (2),
				SegmentInelasticDesignControl.Create (
					0,
					2
				),
				null,
				null
			),
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (4),
				SegmentInelasticDesignControl.Create (
					2,
					2
				),
				null,
				null
			)
		);
	}

	private static final void DisplayVolArray (
		final String strTenor,
		final double[] adblVol)
	{
		String strDump = "\t | " + strTenor + " =>  ";

		for (int i = 0; i < adblVol.length; ++i)
			strDump += FormatUtil.FormatDouble (adblVol[i], 1, 2, 100.) + "% | ";

		System.out.println (strDump);
	}

	@Test(dataProvider = "mainparam", dataProviderClass = org.drip.sample.TestNGDataProvider.class)
	public static void main (
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

		ForwardLabel forwardLabel = ForwardLabel.Create (
			strCurrency,
			strFRATenor
		);

		DiscountCurve dc = MakeDC (
			dtSpot,
			strCurrency
		);

		ForwardCurve fcNative = dc.nativeForwardCurve (strFRATenor);

		String[] astrMaturityTenor = new String[] {
			 "3M",
			 "6M",
			"12M",
			"18M",
			"24M",
			"30M",
			 "3Y",
			 "4Y",
			 "5Y",
			 "7Y",
			 "9Y",
			"11Y"
		};

		double[] adblFlatTermStructure = new double[] {
			1.00000000, //  "3M",
			1.00000000, //  "6M",
			0.99168448, // "12M",
			1.00388389, // "18M",
			1.00388389, // "24M",
			1.07602593, // "30M",
			1.07602593, //  "3Y",
			1.04727642, //  "4Y",
			1.02727799, //  "5Y",
			0.96660430, //  "7Y",
			0.93012459, //  "9Y",
			0.81425256  // "11Y"
		};

		double[] adblFlatVolFactor1 = new double[] {
			0.09481393, //  "3M",
			0.08498925, //  "6M",
			0.22939966, // "12M",
			0.19166872, // "18M",
			0.08232925, // "24M",
			0.18548202, // "30M",
			0.13817885, //  "3Y",
			0.08562258, //  "4Y",
			0.14547123, //  "5Y",
			0.08869328, //  "7Y",
			0.04121240, //  "9Y",
			0.15206796  // "11Y"
		};

		double[] adblFlatVolFactor2 = new double[] {
			 0.12146092, //  "3M",
			 0.05117321, //  "6M",
			 0.09100802, // "12M",
			 0.02876211, // "18M",
			 0.01172983, // "24M",
			 0.00047705, // "30M",
			-0.01160086, //  "3Y",
			-0.04673283, //  "4Y",
			-0.04181446, //  "5Y",
			-0.05459175, //  "7Y",
			-0.03631021, //  "9Y",
			-0.16626765  // "11Y"
		};

		MarketSurface mktSurf1 = FactorFlatVolatilitySurface (
			dtSpot,
			strCurrency,
			astrMaturityTenor,
			adblFlatVolFactor1,
			adblFlatTermStructure
		);

		MarketSurface mktSurf2 = FactorFlatVolatilitySurface (
			dtSpot,
			strCurrency,
			astrMaturityTenor,
			adblFlatVolFactor2,
			adblFlatTermStructure
		);

		LognormalLIBORVolatility llv = new LognormalLIBORVolatility (
			dtSpot.julian(),
			forwardLabel,
			new MarketSurface[] {
				mktSurf1,
				mktSurf2
			},
			new PrincipalFactorSequenceGenerator (
				new UnivariateSequenceGenerator[] {
					new BoxMullerGaussian (
						0.,
						1.
					),
					new BoxMullerGaussian (
						0.,
						1.
					)
				},
				new double[][] {
					{1.0, 0.1},
					{0.1, 1.0},
				},
				2
			)
		);

		String[] astrForwardTenor = {
			"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y"
		};

		System.out.println ("\n\t |------------------------------|");

		System.out.println ("\t |  CONTINUOUS FORWARD RATE VOL |");

		System.out.println ("\t |------------------------------|");

		for (String strForwardTenor : astrForwardTenor)
			DisplayVolArray (
				strForwardTenor,
				llv.continuousForwardVolatility (
					dtSpot.addTenor (strForwardTenor).julian(),
					fcNative
				)
			);

		System.out.println ("\t |------------------------------|");
	}
}
