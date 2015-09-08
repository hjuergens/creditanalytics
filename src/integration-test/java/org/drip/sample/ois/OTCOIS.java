
package org.drip.sample.ois;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.rates.DiscountCurve;
import org.drip.function.R1ToR1.QuadraticRationalShapeControl;
import org.drip.market.otc.FixedFloatSwapConvention;
import org.drip.market.otc.OvernightFixedFloatContainer;
import org.drip.param.creator.MarketParamsBuilder;
import org.drip.param.creator.ScenarioDiscountCurveBuilder;
import org.drip.param.market.CurveSurfaceQuoteSet;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.definition.CalibratableFixedIncomeComponent;
import org.drip.product.rates.FixFloatComponent;
import org.drip.product.rates.SingleStreamComponent;
import org.drip.quant.common.FormatUtil;
import org.drip.service.api.CreditAnalytics;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.ResponseScalingShapeControl;
import org.drip.spline.params.SegmentCustomBuilderControl;
import org.drip.spline.params.SegmentInelasticDesignControl;
import org.drip.spline.stretch.BoundarySettings;
import org.drip.spline.stretch.MultiSegmentSequence;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
import org.drip.state.estimator.LatentStateStretchBuilder;
import org.drip.state.identifier.ForwardLabel;
import org.drip.state.inference.LatentStateStretchSpec;
import org.drip.state.inference.LinearLatentStateCalibrator;
import org.testng.annotations.Test;

import java.util.Map;

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
 * OTCOIS contains the Curve Construction and Valuation Functionality of the OTC OIS.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class OTCOIS {

	private static final FixFloatComponent OTCOISFixFloat (
		final JulianDate dtSpot,
		final String strCurrency,
		final String strMaturityTenor,
		final double dblCoupon)
	{
		FixedFloatSwapConvention ffConv = OvernightFixedFloatContainer.ConventionFromJurisdiction (
			strCurrency
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

	private static final SingleStreamComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate dtEffective,
		final String strCurrency,
		final int[] aiDay)
		throws Exception
	{
		SingleStreamComponent[] aDeposit = new SingleStreamComponent[aiDay.length];

		for (int i = 0; i < aiDay.length; ++i)
			aDeposit[i] = SingleStreamComponentBuilder.Deposit (
				dtEffective,
				dtEffective.addBusDays (
					aiDay[i],
					strCurrency
				),
				ForwardLabel.Create (
					strCurrency,
					"ON"
				)
			);

		return aDeposit;
	}

	/*
	 * Construct the Array of Swap Instruments from the given set of parameters
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final FixFloatComponent[] OISFromMaturityTenor (
		final JulianDate dtSpot,
		final String strCurrency,
		final String[] astrMaturityTenor,
		final double[] adblCoupon)
		throws Exception
	{
		FixFloatComponent[] aOIS = new FixFloatComponent[astrMaturityTenor.length];

		for (int i = 0; i < astrMaturityTenor.length; ++i)
			aOIS[i] = OTCOISFixFloat (
				dtSpot,
				strCurrency,
				astrMaturityTenor[i],
				adblCoupon[i]
			);

		return aOIS;
	}

	/*
	 * Construct the Array of Swap Instruments from the given set of parameters
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final FixFloatComponent[] OISFuturesFromMaturityTenor (
		final JulianDate dtSpot,
		final String strCurrency,
		final String[] astrStartTenor,
		final String[] astrMaturityTenor,
		final double[] adblCoupon)
		throws Exception
	{
		FixFloatComponent[] aOISFutures = new FixFloatComponent[astrMaturityTenor.length];

		for (int i = 0; i < astrMaturityTenor.length; ++i)
			aOISFutures[i] = OTCOISFixFloat (
				dtSpot.addTenor (astrStartTenor[i]),
				strCurrency,
				astrMaturityTenor[i],
				adblCoupon[i]
			);

		return aOISFutures;
	}

	private static final void OTCOISRun (
		final JulianDate dtSpot,
		final String strCurrency,
		final String[] astrOTCMaturityTenor,
		final boolean bCalibMetricDisplay)
		throws Exception
	{
		if (bCalibMetricDisplay) {
			System.out.println ("\n\t----------------------------------------------------------------");

			System.out.println ("\t--------- DISCOUNT CURVE WITH OVERNIGHT INDEX ------------------");

			System.out.println ("\t----------------------------------------------------------------");
		}

		/*
		 * Construct the Array of Deposit Instruments and their Quotes from the given set of parameters
		 */

		SingleStreamComponent[] aDepositComp = DepositInstrumentsFromMaturityDays (
			dtSpot,
			strCurrency,
			new int[] {
				1, 2, 3
			}
		);

		double[] adblDepositQuote = new double[] {
			0.0004, 0.0004, 0.0004		 // Deposit
		};

		/*
		 * Construct the Deposit Instrument Set Stretch Builder
		 */

		LatentStateStretchSpec depositStretch = LatentStateStretchBuilder.ForwardFundingStretchSpec (
			"   DEPOSIT   ",
			aDepositComp,
			"ForwardRate",
			adblDepositQuote
		);

		/*
		 * Construct the Array of Short End OIS Instruments and their Quotes from the given set of parameters
		 */

		double[] adblShortEndOISQuote = new double[] {
			0.00070,    //   1W
			0.00069,    //   2W
			0.00078,    //   3W
			0.00074     //   1M
		};

		CalibratableFixedIncomeComponent[] aShortEndOISComp = OISFromMaturityTenor (
			dtSpot,
			strCurrency,
			new String[] {
				"1W", "2W", "3W", "1M"
			},
			adblShortEndOISQuote
		);

		/*
		 * Construct the Short End OIS Instrument Set Stretch Builder
		 */

		LatentStateStretchSpec oisShortEndStretch = LatentStateStretchBuilder.ForwardFundingStretchSpec (
			"SHORT END OIS",
			aShortEndOISComp,
			"SwapRate",
			adblShortEndOISQuote
		);

		/*
		 * Construct the Array of OIS Futures Instruments and their Quotes from the given set of parameters
		 */

		double[] adblOISFutureQuote = new double[] {
			 0.00046,    //   1M x 1M
			 0.00016,    //   2M x 1M
			-0.00007,    //   3M x 1M
			-0.00013,    //   4M x 1M
			-0.00014     //   5M x 1M
		};

		CalibratableFixedIncomeComponent[] aOISFutureComp = OISFuturesFromMaturityTenor (
			dtSpot,
			strCurrency,
			new String[] {
				"1M", "2M", "3M", "4M", "5M"
			},
			new String[] {
				"1M", "1M", "1M", "1M", "1M"
			},
			adblOISFutureQuote
		);

		/*
		 * Construct the OIS Future Instrument Set Stretch Builder
		 */

		LatentStateStretchSpec oisFutureStretch = LatentStateStretchBuilder.ForwardFundingStretchSpec (
			" OIS FUTURE  ",
			aOISFutureComp,
			"SwapRate",
			adblOISFutureQuote
		);

		/*
		 * Construct the Array of Long End OIS Instruments and their Quotes from the given set of parameters
		 */

		double[] adblLongEndOISQuote = new double[] {
			0.00002,    //  15M
			0.00008,    //  18M
			0.00021,    //  21M
			0.00036,    //   2Y
			0.00127,    //   3Y
			0.00274,    //   4Y
			0.00456,    //   5Y
			0.00647,    //   6Y
			0.00827,    //   7Y
			0.00996,    //   8Y
			0.01147,    //   9Y
			0.01280,    //  10Y
			0.01404,    //  11Y
			0.01516,    //  12Y
			0.01764,    //  15Y
			0.01939,    //  20Y
			0.02003,    //  25Y
			0.02038     //  30Y
		};

		CalibratableFixedIncomeComponent[] aLongEndOISComp = OISFromMaturityTenor (
			dtSpot,
			strCurrency,
			new String[] {
				"15M", "18M", "21M", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "11Y", "12Y", "15Y", "20Y", "25Y", "30Y"
			},
			adblLongEndOISQuote
		);

		/*
		 * Construct the Long End OIS Instrument Set Stretch Builder
		 */

		LatentStateStretchSpec oisLongEndStretch = LatentStateStretchBuilder.ForwardFundingStretchSpec (
			"LONG END OIS ",
			aLongEndOISComp,
			"SwapRate",
			adblLongEndOISQuote
		);

		LatentStateStretchSpec[] aStretchSpec = new LatentStateStretchSpec[] {
			depositStretch,
			oisShortEndStretch,
			oisFutureStretch,
			oisLongEndStretch
		};

		/*
		 * Set up the Linear Curve Calibrator using the following parameters:
		 * 	- Cubic Exponential Mixture Basis Spline Set
		 * 	- Ck = 2, Segment Curvature Penalty = 2
		 * 	- Quadratic Rational Shape Controller
		 * 	- Natural Boundary Setting
		 */

		LinearLatentStateCalibrator lcc = new LinearLatentStateCalibrator (
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (4),
				SegmentInelasticDesignControl.Create (
					2,
					2
				),
				new ResponseScalingShapeControl (
					true,
					new QuadraticRationalShapeControl (0.)
				),
				null
			),
			BoundarySettings.NaturalStandard(),
			MultiSegmentSequence.CALIBRATE,
			null,
			null
		);

		ValuationParams valParams = new ValuationParams (
			dtSpot,
			dtSpot,
			strCurrency
		);

		/*
		 * Construct the Shape Preserving Discount Curve by applying the linear curve calibrator to the array
		 *  of Deposit and Swap Stretches.
		 */

		DiscountCurve dc = ScenarioDiscountCurveBuilder.ShapePreservingDFBuild (
			lcc,
			aStretchSpec,
			valParams,
			null,
			null,
			null,
			1.
		);

		CurveSurfaceQuoteSet mktParams = MarketParamsBuilder.Create (
			dc,
			null,
			null,
			null,
			null,
			null,
			null
		);

		if (bCalibMetricDisplay) {

			/*
			 * Cross-Comparison of the Deposit Calibration Instrument "Rate" metric across the different curve
			 * 	construction methodologies.
			 */

			System.out.println ("\t----------------------------------------------------------------");

			System.out.println ("\t     DEPOSIT INSTRUMENTS CALIBRATION RECOVERY");

			System.out.println ("\t----------------------------------------------------------------");

			for (int i = 0; i < aDepositComp.length; ++i)
				System.out.println ("\t[" + aDepositComp[i].effectiveDate() + " => " + aDepositComp[i].maturityDate() + "] = " +
					FormatUtil.FormatDouble (aDepositComp[i].measureValue (valParams, null, mktParams, null, "Rate"), 1, 6, 1.) + " | " +
						FormatUtil.FormatDouble (adblDepositQuote[i], 1, 6, 1.));

			/*
			 * Cross-Comparison of the Short End OIS Calibration Instrument "Rate" metric across the different curve
			 * 	construction methodologies.
			 */

			System.out.println ("\n\t----------------------------------------------------------------");

			System.out.println ("\t     OIS SHORT END INSTRUMENTS CALIBRATION RECOVERY");

			System.out.println ("\t----------------------------------------------------------------");

			for (int i = 0; i < aShortEndOISComp.length; ++i) {
				Map<String, Double> mapShortEndOISComp = aShortEndOISComp[i].value (valParams, null, mktParams, null);

				System.out.println ("\t[" + aShortEndOISComp[i].effectiveDate() + " => " + aShortEndOISComp[i].maturityDate() + "] = " +
					FormatUtil.FormatDouble (mapShortEndOISComp.get ("CalibSwapRate"), 1, 6, 1.) + " | " +
					FormatUtil.FormatDouble (adblShortEndOISQuote[i], 1, 6, 1.) + " | " +
					FormatUtil.FormatDouble (mapShortEndOISComp.get ("FairPremium"), 1, 6, 1.)
				);
			}

			/*
			 * Cross-Comparison of the OIS Future Calibration Instrument "Rate" metric across the different curve
			 * 	construction methodologies.
			 */

			System.out.println ("\n\t----------------------------------------------------------------");

			System.out.println ("\t     OIS FUTURE INSTRUMENTS CALIBRATION RECOVERY");

			System.out.println ("\t----------------------------------------------------------------");

			for (int i = 0; i < aOISFutureComp.length; ++i) {
				Map<String, Double> mapOISFutureComp = aOISFutureComp[i].value (valParams, null, mktParams, null);

				System.out.println ("\t[" + aOISFutureComp[i].effectiveDate() + " => " + aOISFutureComp[i].maturityDate() + "] = " +
					FormatUtil.FormatDouble (mapOISFutureComp.get ("SwapRate"), 1, 6, 1.) + " | " +
					FormatUtil.FormatDouble (adblOISFutureQuote[i], 1, 6, 1.) + " | " +
					FormatUtil.FormatDouble (mapOISFutureComp.get ("FairPremium"), 1, 6, 1.)
				);
			}

			/*
			 * Cross-Comparison of the Long End OIS Calibration Instrument "Rate" metric across the different curve
			 * 	construction methodologies.
			 */

			System.out.println ("\n\t----------------------------------------------------------------");

			System.out.println ("\t     OIS LONG END INSTRUMENTS CALIBRATION RECOVERY");

			System.out.println ("\t----------------------------------------------------------------");

			for (int i = 0; i < aLongEndOISComp.length; ++i) {
				Map<String, Double> mapLongEndOISComp = aLongEndOISComp[i].value (valParams, null, mktParams, null);

				System.out.println ("\t[" + aLongEndOISComp[i].effectiveDate() + " => " + aLongEndOISComp[i].maturityDate() + "] = " +
					FormatUtil.FormatDouble (mapLongEndOISComp.get ("CalibSwapRate"), 1, 6, 1.) + " | " +
					FormatUtil.FormatDouble (adblLongEndOISQuote[i], 1, 6, 1.) + " | " +
					FormatUtil.FormatDouble (mapLongEndOISComp.get ("FairPremium"), 1, 6, 1.)
				);
			}

			System.out.println ("\t----------------------------------------------------------------");
		}

		System.out.print ("\t[" + strCurrency + "] = ");

		for (int i = 0; i < astrOTCMaturityTenor.length; ++i) {
			FixFloatComponent swap = OTCOISFixFloat (
				dtSpot,
				strCurrency,
				astrOTCMaturityTenor[i],
				0.
			);

			Map<String, Double> mapOutput = swap.value (
				valParams,
				null,
				mktParams,
				null
			);

			System.out.print (
				FormatUtil.FormatDouble (mapOutput.get ("SwapRate"), 1, 4, 100.) + "% (" +
				FormatUtil.FormatDouble (mapOutput.get ("FairPremium"), 1, 4, 100.) + "%) || "
			);
		}

		System.out.println();
	}

	@Test(dataProvider = "mainparam", dataProviderClass = org.drip.sample.TestNGDataProvider.class)
	public static void main (
		final String[] astrArgs)
		throws Exception
	{
		/*
		 * Initialize the Credit Analytics Library
		 */

		CreditAnalytics.Init ("");

		JulianDate dtToday = org.drip.analytics.date.DateUtil.Today();

		String[] astrOTCMaturityTenor = new String[] {
			"1Y", "3Y", "5Y", "7Y", "10Y"
		};

		OTCOISRun (dtToday, "AUD", astrOTCMaturityTenor, true);

		System.out.println ("\n\t--------------------------------------------------------------------------------------------------------------------------");

		System.out.println ("\t JURISDICTION       1Y      ||          3Y         ||          5Y         ||          7Y         ||         10Y         ||");

		System.out.println ("\t--------------------------------------------------------------------------------------------------------------------------");

		OTCOISRun (dtToday, "AUD", astrOTCMaturityTenor, false);

		OTCOISRun (dtToday, "CAD", astrOTCMaturityTenor, false);

		OTCOISRun (dtToday, "EUR", astrOTCMaturityTenor, false);

		OTCOISRun (dtToday, "GBP", astrOTCMaturityTenor, false);

		OTCOISRun (dtToday, "INR", astrOTCMaturityTenor, false);

		OTCOISRun (dtToday, "JPY", astrOTCMaturityTenor, false);

		OTCOISRun (dtToday, "SGD", astrOTCMaturityTenor, false);

		OTCOISRun (dtToday, "USD", astrOTCMaturityTenor, false);
	}
}
