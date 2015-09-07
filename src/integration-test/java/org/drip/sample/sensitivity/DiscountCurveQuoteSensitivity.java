
package org.drip.sample.sensitivity;

import org.drip.analytics.date.*;
import org.drip.analytics.rates.*;
import org.drip.function.R1ToR1.QuadraticRationalShapeControl;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.*;
import org.drip.product.definition.*;
import org.drip.product.rates.*;
import org.drip.quant.calculus.WengertJacobian;
import org.drip.quant.common.FormatUtil;
import org.drip.service.api.CreditAnalytics;
import org.drip.spline.basis.*;
import org.drip.spline.params.*;
import org.drip.spline.stretch.*;
import org.drip.state.estimator.LatentStateStretchBuilder;
import org.drip.state.identifier.*;
import org.drip.state.inference.*;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
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
 * DiscountCurveQuoteSensitivity demonstrates the calculation of the discount curve sensitivity to the
 * 	calibration instrument quotes. It does the following:
 * 	- Construct the Array of Cash/Swap Instruments and their Quotes from the given set of parameters.
 * 	- Construct the Cash/Swap Instrument Set Stretch Builder.
 * 	- Set up the Linear Curve Calibrator using the following parameters:
 * 		- Cubic Exponential Mixture Basis Spline Set
 * 		- Ck = 2, Segment Curvature Penalty = 2
 * 		- Quadratic Rational Shape Controller
 * 		- Natural Boundary Setting
 * 	- Construct the Shape Preserving Discount Curve by applying the linear curve calibrator to the array
 * 		of Cash and Swap Stretches.
 * 	- Cross-Comparison of the Cash/Swap Calibration Instrument "Rate" metric across the different curve
 * 		construction methodologies.
 * 	- Display of the Cash Instrument Discount Factor Quote Jacobian Sensitivities.
 * 	- Display of the Swap Instrument Discount Factor Quote Jacobian Sensitivities.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class DiscountCurveQuoteSensitivity {

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

	private static final FixFloatComponent[] SwapInstrumentsFromMaturityTenor (
		final JulianDate dtSpot,
		final String strCurrency,
		final String[] astrMaturityTenor)
		throws Exception
	{
		FixFloatComponent[] aIRS = new FixFloatComponent[astrMaturityTenor.length];

		for (int i = 0; i < astrMaturityTenor.length; ++i) {
			FixFloatComponent irs = OTCIRS (
				dtSpot,
				strCurrency,
				astrMaturityTenor[i],
				0.
			);

			irs.setPrimaryCode ("IRS." + astrMaturityTenor[i] + "." + strCurrency);

			aIRS[i] = irs;
		}

		return aIRS;
	}

	private static final void TenorJack (
		final JulianDate dtStart,
		final String strTenor,
		final String strCurrency,
		final String strManifestMeasure,
		final DiscountCurve dc)
		throws Exception
	{
		CalibratableFixedIncomeComponent irsBespoke = OTCIRS (
			dtStart,
			strCurrency,
			strTenor,
			0.
		);

		WengertJacobian wjDFQuoteBespokeMat = dc.jackDDFDManifestMeasure (
			irsBespoke.maturityDate(),
			strManifestMeasure
		);

		System.out.println (strTenor + " => " + wjDFQuoteBespokeMat.displayString());
	}

	private static final void Forward6MRateJack (
		final JulianDate dtStart,
		final String strStartTenor,
		final String strManifestMeasure,
		final DiscountCurve dc)
	{
		JulianDate dtBegin = dtStart.addTenor (strStartTenor);

		WengertJacobian wjForwardRate = dc.jackDForwardDManifestMeasure (
			dtBegin,
			"6M",
			strManifestMeasure,
			0.5
		);

		System.out.println ("[" + dtBegin + " | 6M] => " + wjForwardRate.displayString());
	}

	/*
	 * This org.drip.sample demonstrates the calculation of the discount curve sensitivity to the calibration
	 * 	instrument quotes. It does the following:
	 * 	- Construct the Array of Cash/Swap Instruments and their Quotes from the given set of parameters.
	 * 	- Construct the Cash/Swap Instrument Set Stretch Builder.
	 * 	- Set up the Linear Curve Calibrator using the following parameters:
	 * 		- Cubic Exponential Mixture Basis Spline Set
	 * 		- Ck = 2, Segment Curvature Penalty = 2
	 * 		- Quadratic Rational Shape Controller
	 * 		- Natural Boundary Setting
	 * 	- Construct the Shape Preserving Discount Curve by applying the linear curve calibrator to the array
	 * 		of Cash and Swap Stretches.
	 * 	- Cross-Comparison of the Cash/Swap Calibration Instrument "Rate" metric across the different curve
	 * 		construction methodologies.
	 * 	- Display of the Cash Instrument Discount Factor Quote Jacobian Sensitivities.
	 * 	- Display of the Swap Instrument Discount Factor Quote Jacobian Sensitivities.
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final void DiscountCurveQuoteSensitivitySample (
		final JulianDate dtSpot,
		final String strCurrency)
		throws Exception
	{
		/*
		 * Construct the Array of DEPOSIT Instruments and their Quotes from the given set of parameters
		 */

		SingleStreamComponent[] aDepositComp = DepositInstrumentsFromMaturityDays (
			dtSpot,
			strCurrency,
			new int[] {
				1, 2, 7, 14, 30, 60
			}
		);

		double[] adblDepositQuote = new double[] {
			0.0013, 0.0017, 0.0017, 0.0018, 0.0020, 0.0023
		}; // Cash Rate

		/*
		 * Construct the Deposit Instrument Set Stretch Builder
		 */

		LatentStateStretchSpec depositStretch = LatentStateStretchBuilder.ForwardFundingStretchSpec (
			"DEPOSIT",
			aDepositComp,
			"ForwardRate",
			adblDepositQuote
		);

		/*
		 * Construct the Array of FUTURE Instruments and their Quotes from the given set of parameters
		 */

		SingleStreamComponent[] aEDFComp = SingleStreamComponentBuilder.FuturesPack (
			dtSpot,
			8,
			strCurrency
		);

		double[] adblEDFQuote = new double[] {
			0.0027, 0.0032, 0.0041, 0.0054, 0.0077, 0.0104, 0.0134, 0.0160
		};

		/*
		 * Construct the EDF Instrument Set Stretch Builder
		 */

		LatentStateStretchSpec edfStretch = LatentStateStretchBuilder.ForwardFundingStretchSpec (
			"EDF",
			aEDFComp,
			"ForwardRate",
			adblEDFQuote
		);

		/*
		 * Construct the Array of SWAP Instruments and their Quotes from the given set of parameters
		 */

		FixFloatComponent[] aSwapComp = SwapInstrumentsFromMaturityTenor (
			dtSpot,
			strCurrency,
			new String[] {
				"4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "11Y", "12Y", "15Y", "20Y", "25Y", "30Y", "40Y", "50Y"
			}
		);

		double[] adblSwapQuote = new double[] {
			0.0166, 0.0206, 0.0241, 0.0269, 0.0292, 0.0311, 0.0326, 0.0340, 0.0351, 0.0375, 0.0393, 0.0402, 0.0407, 0.0409, 0.0409
		};

		/*
		 * Construct the Swap Instrument Set Stretch Builder
		 */

		LatentStateStretchSpec swapStretch = LatentStateStretchBuilder.ForwardFundingStretchSpec (
			"SWAP",
			aSwapComp,
			"SwapRate",
			adblSwapQuote
		);

		LatentStateStretchSpec[] aStretchSpec = new LatentStateStretchSpec[] {
			depositStretch,
			edfStretch,
			swapStretch
		};

		/*
		 * Set up the Linear Curve Calibrator using the following Default Segment Control parameters:
		 * 	- Cubic Exponential Mixture Basis Spline Set
		 * 	- Ck = 2, Segment Curvature Penalty = 2
		 * 	- Quadratic Rational Shape Controller
		 * 	- Prior Quote Sensitivity Control with first derivative tail fade, with FADE ON
		 * 	- Natural Boundary Setting
		 */

		LinearLatentStateCalibrator lcc = new LinearLatentStateCalibrator (
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_HYPERBOLIC_TENSION,
				new ExponentialTensionSetParams (2.),
				SegmentInelasticDesignControl.Create (
					2,
					2
				),
				new ResponseScalingShapeControl (
					true,
					new QuadraticRationalShapeControl (0.)
				),
				new org.drip.spline.params.PreceedingManifestSensitivityControl (
					true,
					1,
					null
				)
			),
			BoundarySettings.NaturalStandard(),
			MultiSegmentSequence.CALIBRATE,
			null,
			null
		);

		/*
		 * Set up the DEPOSIT Segment Control parameters with the following details:
		 * 	- Cubic Exponential Mixture Basis Spline Set
		 * 	- Ck = 2, Segment Curvature Penalty = 2
		 * 	- Quadratic Rational Shape Controller
		 * 	- Prior Quote Sensitivity Control with first derivative tail fade, with FADE ON
		 * 	- Natural Boundary Setting
		 */

		lcc.setStretchSegmentBuilderControl (
			depositStretch.name(),
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_HYPERBOLIC_TENSION,
				new ExponentialTensionSetParams (2.),
				SegmentInelasticDesignControl.Create (
					2,
					2
				),
				new ResponseScalingShapeControl (
					true,
					new QuadraticRationalShapeControl (0.)
				),
				new org.drip.spline.params.PreceedingManifestSensitivityControl (
					true,
					1,
					null
				)
			)
		);

		/*
		 * Set up the FUTURE Segment Control parameters with the following details:
		 * 	- Cubic Exponential Mixture Basis Spline Set
		 * 	- Ck = 2, Segment Curvature Penalty = 2
		 * 	- Quadratic Rational Shape Controller
		 * 	- Prior Quote Sensitivity Control with first derivative tail fade, with FADE OFF, RETAIN ON
		 * 	- Natural Boundary Setting
		 */

		lcc.setStretchSegmentBuilderControl (
			edfStretch.name(),
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_HYPERBOLIC_TENSION,
				new ExponentialTensionSetParams (2.),
				SegmentInelasticDesignControl.Create (
					2,
					2
				),
				new ResponseScalingShapeControl (
					true,
					new QuadraticRationalShapeControl (0.)
				),
				new org.drip.spline.params.PreceedingManifestSensitivityControl (
					false,
					1,
					null
				)
			)
		);

		/*
		 * Set up the SWAP Segment Control parameters with the following details:
		 * 	- Cubic Exponential Mixture Basis Spline Set
		 * 	- Ck = 2, Segment Curvature Penalty = 2
		 * 	- Quadratic Rational Shape Controller
		 * 	- Prior Quote Sensitivity Control with first derivative tail fade, with FADE ON
		 * 	- Natural Boundary Setting
		 */

		lcc.setStretchSegmentBuilderControl (
			swapStretch.name(),
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_HYPERBOLIC_TENSION,
				new ExponentialTensionSetParams (2.),
				SegmentInelasticDesignControl.Create (
					2,
					2
				),
				new ResponseScalingShapeControl (
					true,
					new QuadraticRationalShapeControl (0.)
				),
				new org.drip.spline.params.PreceedingManifestSensitivityControl (
					true,
					1,
					null
				)
			)
		);

		ValuationParams valParams = new ValuationParams (
			dtSpot,
			dtSpot,
			strCurrency
		);

		/*
		 * Construct the Shape Preserving Discount Curve by applying the linear curve calibrator to the array
		 *  of Deposit, Futures, and Swap Stretches.
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

		/*
		 * Cross-Comparison of the DEPOSIT Calibration Instrument "Rate" metric across the different curve
		 * 	construction methodologies.
		 */

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     DEPOSIT INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int i = 0; i < aDepositComp.length; ++i)
			System.out.println ("\t[" + aDepositComp[i].maturityDate() + "] = " +
				FormatUtil.FormatDouble (aDepositComp[i].measureValue (valParams, null,
					MarketParamsBuilder.Create (dc, null, null, null, null, null, null),
						null, "Rate"), 1, 6, 1.) + " | " + FormatUtil.FormatDouble (adblDepositQuote[i], 1, 6, 1.));

		/*
		 * Cross-Comparison of the FUTURE Calibration Instrument "Rate" metric across the different curve
		 * 	construction methodologies.
		 */

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     FUTURE INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int i = 0; i < aEDFComp.length; ++i)
			System.out.println ("\t[" + aEDFComp[i].maturityDate() + "] = " +
				FormatUtil.FormatDouble (aEDFComp[i].measureValue (valParams, null,
					MarketParamsBuilder.Create (dc, null, null, null, null, null, null),
						null, "Rate"), 1, 6, 1.) + " | " + FormatUtil.FormatDouble (adblEDFQuote[i], 1, 6, 1.));

		/*
		 * Cross-Comparison of the SWAP Calibration Instrument "Rate" metric across the different curve
		 * 	construction methodologies.
		 */

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     SWAP INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int i = 0; i < aSwapComp.length; ++i)
			System.out.println ("\t[" + aSwapComp[i].maturityDate() + "] = " +
				FormatUtil.FormatDouble (aSwapComp[i].measureValue (valParams, null,
					MarketParamsBuilder.Create (dc, null, null, null, null, null, null),
						null, "CalibSwapRate"), 1, 6, 1.) + " | " + FormatUtil.FormatDouble (adblSwapQuote[i], 1, 6, 1.));

		/*
		 * Display of the DEPOSIT Instrument Discount Factor Quote Jacobian Sensitivities.
		 */

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     DEPOSIT MATURITY DISCOUNT FACTOR JACOBIAN");

		System.out.println ("\t----------------------------------------------------------------");

		for (int i = 0; i < aDepositComp.length; ++i) {
			org.drip.quant.calculus.WengertJacobian wj = dc.jackDDFDManifestMeasure (aDepositComp[i].maturityDate(), "PV");

			System.out.println (aDepositComp[i].maturityDate() + " => " + wj.displayString());
		}

		/*
		 * Display of the FUTURE Instrument Discount Factor Quote Jacobian Sensitivities.
		 */

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     FUTURE MATURITY DISCOUNT FACTOR JACOBIAN");

		System.out.println ("\t----------------------------------------------------------------");

		for (int i = 0; i < aEDFComp.length; ++i) {
			org.drip.quant.calculus.WengertJacobian wj = dc.jackDDFDManifestMeasure (
				aEDFComp[i].maturityDate(),
				"PV"
			);

			System.out.println (aEDFComp[i].maturityDate() + " => " + wj.displayString());
		}

		/*
		 * Display of the SWAP Instrument Discount Factor Quote Jacobian Sensitivities.
		 */

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     SWAP MATURITY DISCOUNT FACTOR JACOBIAN");

		System.out.println ("\t----------------------------------------------------------------");

		for (int i = 0; i < aSwapComp.length; ++i) {
			org.drip.quant.calculus.WengertJacobian wjDFQuote = dc.jackDDFDManifestMeasure (
				aSwapComp[i].maturityDate(),
				"PV"
			);

			System.out.println (aSwapComp[i].maturityDate() + " => " + wjDFQuote.displayString());
		}

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     COMPONENT-BY-COMPONENT QUOTE JACOBIAN");

		System.out.println ("\t----------------------------------------------------------------");

		WengertJacobian wj = dc.compJackDPVDManifestMeasure (dtSpot);

		System.out.println (wj.displayString());

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     BESPOKE 35Y SWAP QUOTE JACOBIAN");

		System.out.println ("\t----------------------------------------------------------------");

		CalibratableFixedIncomeComponent irs35Y = OTCIRS (
			dtSpot,
			strCurrency,
			"35Y",
			0.
		);

		WengertJacobian wjIRSBespokeQuoteJack = irs35Y.jackDDirtyPVDManifestMeasure (
			valParams,
			null,
			MarketParamsBuilder.Create (
				dc,
				null,
				null,
				null,
				null,
				null,
				null,
				null
			),
			null
		);

		System.out.println (wjIRSBespokeQuoteJack.displayString());

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     BESPOKE SWAP MATURITY QUOTE JACOBIAN");

		System.out.println ("\t----------------------------------------------------------------");

		TenorJack (dtSpot, "30Y", strCurrency, "PV", dc);

		TenorJack (dtSpot, "32Y", strCurrency, "PV", dc);

		TenorJack (dtSpot, "34Y", strCurrency, "PV", dc);

		TenorJack (dtSpot, "36Y", strCurrency, "PV", dc);

		TenorJack (dtSpot, "38Y", strCurrency, "PV", dc);

		TenorJack (dtSpot, "40Y", strCurrency, "PV", dc);

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     DISCOUNT CURVE IMPLIED 6M FORWARD RATE QUOTE JACOBIAN");

		System.out.println ("\t----------------------------------------------------------------");

		Forward6MRateJack (dtSpot, "1D", "PV", dc);

		Forward6MRateJack (dtSpot, "3M", "PV", dc);

		Forward6MRateJack (dtSpot, "6M", "PV", dc);

		Forward6MRateJack (dtSpot, "1Y", "PV", dc);

		Forward6MRateJack (dtSpot, "2Y", "PV", dc);

		Forward6MRateJack (dtSpot, "5Y", "PV", dc);
	}

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		/*
		 * Initialize the Credit Analytics Library
		 */

		CreditAnalytics.Init ("");

		String strCurrency = "USD";

		DiscountCurveQuoteSensitivitySample (
			DateUtil.Today(),
			strCurrency
		);
	}
}
