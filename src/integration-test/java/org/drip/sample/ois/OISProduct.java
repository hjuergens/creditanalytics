
package org.drip.sample.ois;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.rates.DiscountCurve;
import org.drip.analytics.support.AnalyticsHelper;
import org.drip.analytics.support.CompositePeriodBuilder;
import org.drip.function.R1ToR1.QuadraticRationalShapeControl;
import org.drip.param.creator.MarketParamsBuilder;
import org.drip.param.creator.ScenarioDiscountCurveBuilder;
import org.drip.param.market.CurveSurfaceQuoteSet;
import org.drip.param.period.ComposableFixedUnitSetting;
import org.drip.param.period.ComposableFloatingUnitSetting;
import org.drip.param.period.CompositePeriodSetting;
import org.drip.param.period.UnitCouponAccrualSetting;
import org.drip.param.valuation.CashSettleParams;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.definition.CalibratableFixedIncomeComponent;
import org.drip.product.rates.FixFloatComponent;
import org.drip.product.rates.SingleStreamComponent;
import org.drip.product.rates.Stream;
import org.drip.quant.common.FormatUtil;
import org.drip.quant.common.NumberUtil;
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

import java.util.List;
import java.util.Map;

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
 * OISProduct demonstrates the Construction, Usage, Coupon Extraction and Measure Generation for an OIS
 * 	Product Sample.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class OISProduct {

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
	 * Construct the Array of Overnight Index Instruments from the given set of parameters
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final FixFloatComponent[] OvernightIndexFromMaturityTenor (
		final JulianDate dtEffective,
		final String[] astrMaturityTenor,
		final double[] adblCoupon,
		final String strCurrency)
		throws Exception
	{
		FixFloatComponent[] aOIS = new FixFloatComponent[astrMaturityTenor.length];

		UnitCouponAccrualSetting ucasFixed = new UnitCouponAccrualSetting (
			2,
			"Act/360",
			false,
			"Act/360",
			false,
			strCurrency,
			false,
			CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
		);

		CashSettleParams csp = new CashSettleParams (
			0,
			strCurrency,
			0
		);

		for (int i = 0; i < astrMaturityTenor.length; ++i) {
			String strFixedTenor = AnalyticsHelper.LEFT_TENOR_LESSER == AnalyticsHelper.TenorCompare (
				astrMaturityTenor[i],
				"6M"
			) ? astrMaturityTenor[i] : "6M";

			String strFloatingTenor = AnalyticsHelper.LEFT_TENOR_LESSER == AnalyticsHelper.TenorCompare (
				astrMaturityTenor[i],
				"3M"
			) ? astrMaturityTenor[i] : "3M";

			ComposableFloatingUnitSetting cfusFloating = new ComposableFloatingUnitSetting (
				"ON",
				CompositePeriodBuilder.EDGE_DATE_SEQUENCE_OVERNIGHT,
				null,
				ForwardLabel.Create (
					strCurrency,
					"ON"
				),
				CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
				0.
			);

			ComposableFixedUnitSetting cfusFixed = new ComposableFixedUnitSetting (
				strFixedTenor,
				CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
				null,
				adblCoupon[i],
				0.,
				strCurrency
			);

			CompositePeriodSetting cpsFloating = new CompositePeriodSetting (
				4,
				strFloatingTenor,
				strCurrency,
				null,
				-1.,
				null,
				null,
				null,
				null
			);

			CompositePeriodSetting cpsFixed = new CompositePeriodSetting (
				2,
				strFixedTenor,
				strCurrency,
				null,
				1.,
				null,
				null,
				null,
				null
			);

			List<Double> lsFixedStreamEdgeDate = CompositePeriodBuilder.RegularEdgeDates (
				dtEffective,
				strFixedTenor,
				astrMaturityTenor[i],
				null
			);

			List<Double> lsFloatingStreamEdgeDate = CompositePeriodBuilder.RegularEdgeDates (
				dtEffective,
				strFloatingTenor,
				astrMaturityTenor[i],
				null
			);

			Stream floatingStream = new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					lsFloatingStreamEdgeDate,
					cpsFloating,
					cfusFloating
				)
			);

			Stream fixedStream = new Stream (
				CompositePeriodBuilder.FixedCompositeUnit (
					lsFixedStreamEdgeDate,
					cpsFixed,
					ucasFixed,
					cfusFixed
				)
			);

			FixFloatComponent ois = new FixFloatComponent (
				fixedStream,
				floatingStream,
				csp
			);

			ois.setPrimaryCode ("OIS." + astrMaturityTenor[i] + "." + strCurrency);

			aOIS[i] = ois;
		}

		return aOIS;
	}

	/*
	 * Construct the Array of Overnight Fund Instruments from the given set of parameters
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final FixFloatComponent[] OvernightFundFromMaturityTenor (
		final JulianDate dtEffective,
		final String[] astrMaturityTenor,
		final double[] adblCoupon,
		final String strCurrency)
		throws Exception
	{
		FixFloatComponent[] aOIS = new FixFloatComponent[astrMaturityTenor.length];

		UnitCouponAccrualSetting ucasFixed = new UnitCouponAccrualSetting (
			2,
			"Act/360",
			false,
			"Act/360",
			false,
			strCurrency,
			false,
			CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
		);

		CashSettleParams csp = new CashSettleParams (
			0,
			strCurrency,
			0
		);

		for (int i = 0; i < astrMaturityTenor.length; ++i) {
			String strFixedTenor = AnalyticsHelper.LEFT_TENOR_LESSER == AnalyticsHelper.TenorCompare (
				astrMaturityTenor[i],
				"6M"
			) ? astrMaturityTenor[i] : "6M";

			String strFloatingTenor = "ON";

			ComposableFloatingUnitSetting cfusFloating = new ComposableFloatingUnitSetting (
				"ON",
				CompositePeriodBuilder.EDGE_DATE_SEQUENCE_OVERNIGHT,
				null,
				ForwardLabel.Create (
					strCurrency,
					"ON"
				),
				CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
				0.
			);

			ComposableFixedUnitSetting cfusFixed = new ComposableFixedUnitSetting (
				strFixedTenor,
				CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
				null,
				adblCoupon[i],
				0.,
				strCurrency
			);

			CompositePeriodSetting cpsFloating = new CompositePeriodSetting (
				360,
				strFloatingTenor,
				strCurrency,
				null,
				-1.,
				null,
				null,
				null,
				null
			);

			CompositePeriodSetting cpsFixed = new CompositePeriodSetting (
				2,
				strFixedTenor,
				strCurrency,
				null,
				1.,
				null,
				null,
				null,
				null
			);

			List<Double> lsFixedStreamEdgeDate = CompositePeriodBuilder.RegularEdgeDates (
				dtEffective,
				strFixedTenor,
				astrMaturityTenor[i],
				null
			);

			List<Double> lsFloatingStreamEdgeDate = CompositePeriodBuilder.OvernightEdgeDates (
				dtEffective,
				dtEffective.addTenor (astrMaturityTenor[i]),
				null
			);

			Stream floatingStream = new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					lsFloatingStreamEdgeDate,
					cpsFloating,
					cfusFloating
				)
			);

			Stream fixedStream = new Stream (
				CompositePeriodBuilder.FixedCompositeUnit (
					lsFixedStreamEdgeDate,
					cpsFixed,
					ucasFixed,
					cfusFixed
				)
			);

			FixFloatComponent ois = new FixFloatComponent (
				fixedStream,
				floatingStream,
				csp
			);

			ois.setPrimaryCode ("OIS." + astrMaturityTenor[i] + "." + strCurrency);

			aOIS[i] = ois;
		}

		return aOIS;
	}

	/*
	 * Construct the Array of Overnight Index Future Instruments from the given set of parameters
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final FixFloatComponent[] OvernightIndexFutureFromMaturityTenor (
		final JulianDate dtSpot,
		final String[] astrStartTenor,
		final String[] astrMaturityTenor,
		final double[] adblCoupon,
		final String strCurrency)
		throws Exception
	{
		FixFloatComponent[] aOIS = new FixFloatComponent[astrStartTenor.length];

		CashSettleParams csp = new CashSettleParams (
			0,
			strCurrency,
			0
		);

		for (int i = 0; i < astrStartTenor.length; ++i) {
			JulianDate dtEffective = dtSpot.addTenor (astrStartTenor[i]);

			String strFixedTenor = AnalyticsHelper.LEFT_TENOR_LESSER == AnalyticsHelper.TenorCompare (
				astrMaturityTenor[i],
				"6M"
			) ? astrMaturityTenor[i] : "6M";

			String strFloatingTenor = AnalyticsHelper.LEFT_TENOR_LESSER == AnalyticsHelper.TenorCompare (
				astrMaturityTenor[i],
				"3M"
			) ? astrMaturityTenor[i] : "3M";

			ComposableFixedUnitSetting cfusFixed = new ComposableFixedUnitSetting (
				strFixedTenor,
				CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
				null,
				adblCoupon[i],
				0.,
				strCurrency
			);

			UnitCouponAccrualSetting ucasFixed = new UnitCouponAccrualSetting (
				2,
				"Act/360",
				false,
				"Act/360",
				false,
				strCurrency,
				false,
				CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
			);

			ComposableFloatingUnitSetting cfusFloating = new ComposableFloatingUnitSetting (
				"ON",
				CompositePeriodBuilder.EDGE_DATE_SEQUENCE_OVERNIGHT,
				null,
				ForwardLabel.Create (
					strCurrency,
					"ON"
				),
				CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
				0.
			);

			CompositePeriodSetting cpsFloating = new CompositePeriodSetting (
				4,
				strFloatingTenor,
				strCurrency,
				null,
				-1.,
				null,
				null,
				null,
				null
			);

			CompositePeriodSetting cpsFixed = new CompositePeriodSetting (
				2,
				strFixedTenor,
				strCurrency,
				null,
				1.,
				null,
				null,
				null,
				null
			);

			List<Double> lsFixedStreamEdgeDate = CompositePeriodBuilder.RegularEdgeDates (
				dtEffective,
				"6M",
				astrMaturityTenor[i],
				null
			);

			List<Double> lsFloatingStreamEdgeDate = CompositePeriodBuilder.RegularEdgeDates (
				dtEffective,
				"3M",
				astrMaturityTenor[i],
				null
			);

			Stream floatingStream = new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					lsFloatingStreamEdgeDate,
					cpsFloating,
					cfusFloating
				)
			);

			Stream fixedStream = new Stream (
				CompositePeriodBuilder.FixedCompositeUnit (
					lsFixedStreamEdgeDate,
					cpsFixed,
					ucasFixed,
					cfusFixed
				)
			);

			FixFloatComponent ois = new FixFloatComponent (
				fixedStream,
				floatingStream,
				csp
			);

			ois.setPrimaryCode ("OIS." + astrMaturityTenor[i] + "." + strCurrency);

			aOIS[i] = ois;
		}

		return aOIS;
	}

	/*
	 * Construct the Array of Overnight Fund Future Instruments from the given set of parameters
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final FixFloatComponent[] OvernightFundFutureFromMaturityTenor (
		final JulianDate dtSpot,
		final String[] astrStartTenor,
		final String[] astrMaturityTenor,
		final double[] adblCoupon,
		final String strCurrency)
		throws Exception
	{
		FixFloatComponent[] aOIS = new FixFloatComponent[astrStartTenor.length];

		CashSettleParams csp = new CashSettleParams (
			0,
			strCurrency,
			0
		);

		for (int i = 0; i < astrStartTenor.length; ++i) {
			JulianDate dtEffective = dtSpot.addTenor (astrStartTenor[i]);

			String strFixedTenor = AnalyticsHelper.LEFT_TENOR_LESSER == AnalyticsHelper.TenorCompare (
				astrMaturityTenor[i],
				"6M"
			) ? astrMaturityTenor[i] : "6M";

			ComposableFixedUnitSetting cfusFixed = new ComposableFixedUnitSetting (
				strFixedTenor,
				CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
				null,
				adblCoupon[i],
				0.,
				strCurrency
			);

			UnitCouponAccrualSetting ucasFixed = new UnitCouponAccrualSetting (
				2,
				"Act/360",
				false,
				"Act/360",
				false,
				strCurrency,
				false,
				CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
			);

			ComposableFloatingUnitSetting cfusFloating = new ComposableFloatingUnitSetting (
				"ON",
				CompositePeriodBuilder.EDGE_DATE_SEQUENCE_OVERNIGHT,
				null,
				ForwardLabel.Create (
					strCurrency,
					"ON"
				),
				CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
				0.
			);

			CompositePeriodSetting cpsFloating = new CompositePeriodSetting (
				4,
				"ON",
				strCurrency,
				null,
				-1.,
				null,
				null,
				null,
				null
			);

			CompositePeriodSetting cpsFixed = new CompositePeriodSetting (
				2,
				strFixedTenor,
				strCurrency,
				null,
				1.,
				null,
				null,
				null,
				null
			);

			List<Double> lsFixedStreamEdgeDate = CompositePeriodBuilder.RegularEdgeDates (
				dtEffective,
				"6M",
				astrMaturityTenor[i],
				null
			);

			List<Double> lsFloatingStreamEdgeDate = CompositePeriodBuilder.RegularEdgeDates (
				dtEffective,
				"3M",
				astrMaturityTenor[i],
				null
			);

			Stream floatingStream = new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					lsFloatingStreamEdgeDate,
					cpsFloating,
					cfusFloating
				)
			);

			Stream fixedStream = new Stream (
				CompositePeriodBuilder.FixedCompositeUnit (
					lsFixedStreamEdgeDate,
					cpsFixed,
					ucasFixed,
					cfusFixed
				)
			);

			FixFloatComponent ois = new FixFloatComponent (
				fixedStream,
				floatingStream,
				csp
			);

			ois.setPrimaryCode ("OIS." + astrMaturityTenor[i] + "." + strCurrency);

			aOIS[i] = ois;
		}

		return aOIS;
	}

	private static final DiscountCurve CustomOISCurveBuilderSample (
		final JulianDate dtToday,
		final String strHeaderComment,
		final String strCurrency,
		final boolean bOvernightIndex)
		throws Exception
	{
		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     " + strHeaderComment);

		System.out.println ("\t----------------------------------------------------------------");

		/*
		 * Construct the Array of Deposit Instruments and their Quotes from the given set of parameters
		 */

		SingleStreamComponent[] aDepositComp = DepositInstrumentsFromMaturityDays (
			dtToday,
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

		CalibratableFixedIncomeComponent[] aShortEndOISComp = bOvernightIndex ?
			OvernightIndexFromMaturityTenor (
				dtToday,
				new String[]
					{"1W", "2W", "3W", "1M"},
				adblShortEndOISQuote,
				strCurrency) :
			OvernightFundFromMaturityTenor (
				dtToday,
				new String[]
					{"1W", "2W", "3W", "1M"},
				adblShortEndOISQuote,
				strCurrency
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

		CalibratableFixedIncomeComponent[] aOISFutureComp = bOvernightIndex ?
			OvernightIndexFutureFromMaturityTenor (
				dtToday,
				new String[] {"1M", "2M", "3M", "4M", "5M"},
				new String[] {"1M", "1M", "1M", "1M", "1M"},
				adblOISFutureQuote,
				strCurrency
			) :
			OvernightFundFutureFromMaturityTenor (
				dtToday,
				new String[] {"1M", "2M", "3M", "4M", "5M"},
				new String[] {"1M", "1M", "1M", "1M", "1M"},
				adblOISFutureQuote,
				strCurrency
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

		CalibratableFixedIncomeComponent[] aLongEndOISComp = bOvernightIndex ?
			OvernightIndexFromMaturityTenor (
				dtToday,
				new String[]
					{"15M", "18M", "21M", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "11Y", "12Y", "15Y", "20Y", "25Y", "30Y"},
				adblLongEndOISQuote,
				strCurrency
			) : OvernightFundFromMaturityTenor (
				dtToday,
				new String[]
					{"15M", "18M", "21M", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "11Y", "12Y", "15Y", "20Y", "25Y", "30Y"},
				adblLongEndOISQuote,
				strCurrency
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
			null);

		/*
		 * Construct the Shape Preserving Discount Curve by applying the linear curve calibrator to the array
		 *  of Deposit and Swap Stretches.
		 */

		DiscountCurve dc = ScenarioDiscountCurveBuilder.ShapePreservingDFBuild (
			lcc,
			aStretchSpec,
			new ValuationParams (
				dtToday,
				dtToday,
				strCurrency
			),
			null,
			null,
			null,
			1.
		);

		/*
		 * Cross-Comparison of the Deposit Calibration Instrument "Rate" metric across the different curve
		 * 	construction methodologies.
		 */

		System.out.println ("\t----------------------------------------------------------------");

		System.out.println ("\t     DEPOSIT INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int i = 0; i < aDepositComp.length; ++i)
			System.out.println ("\t[" + aDepositComp[i].effectiveDate() + " => " + aDepositComp[i].maturityDate() + "] = " +
				FormatUtil.FormatDouble (aDepositComp[i].measureValue (new ValuationParams (dtToday, dtToday, strCurrency), null,
					MarketParamsBuilder.Create (dc, null, null, null, null, null, null),
						null, "Rate"), 1, 6, 1.) + " | " + FormatUtil.FormatDouble (adblDepositQuote[i], 1, 6, 1.));

		/*
		 * Cross-Comparison of the Short End OIS Calibration Instrument "Rate" metric across the different curve
		 * 	construction methodologies.
		 */

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     OIS SHORT END INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int i = 0; i < aShortEndOISComp.length; ++i)
			System.out.println ("\t[" + aShortEndOISComp[i].effectiveDate() + " => " + aShortEndOISComp[i].maturityDate() + "] = " +
				FormatUtil.FormatDouble (aShortEndOISComp[i].measureValue (new ValuationParams (dtToday, dtToday, strCurrency), null,
					MarketParamsBuilder.Create (dc, null, null, null, null, null, null),
						null, "CalibSwapRate"), 1, 6, 1.) + " | " + FormatUtil.FormatDouble (adblShortEndOISQuote[i], 1, 6, 1.) + " | " +
							FormatUtil.FormatDouble (aShortEndOISComp[i].measureValue (new ValuationParams (dtToday, dtToday, strCurrency), null,
								MarketParamsBuilder.Create (dc, null, null, null, null, null, null),
									null, "FairPremium"), 1, 6, 1.));

		/*
		 * Cross-Comparison of the OIS Future Calibration Instrument "Rate" metric across the different curve
		 * 	construction methodologies.
		 */

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     OIS FUTURE INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int i = 0; i < aOISFutureComp.length; ++i)
			System.out.println ("\t[" + aOISFutureComp[i].effectiveDate() + " => " + aOISFutureComp[i].maturityDate() + "] = " +
				FormatUtil.FormatDouble (aOISFutureComp[i].measureValue (new ValuationParams (dtToday, dtToday, strCurrency), null,
					MarketParamsBuilder.Create (dc, null, null, null, null, null, null),
						null, "SwapRate"), 1, 6, 1.) + " | " + FormatUtil.FormatDouble (adblOISFutureQuote[i], 1, 6, 1.) + " | " +
							FormatUtil.FormatDouble (aOISFutureComp[i].measureValue (new ValuationParams (dtToday, dtToday, strCurrency), null,
								MarketParamsBuilder.Create (dc, null, null, null, null, null, null),
									null, "FairPremium"), 1, 6, 1.));

		/*
		 * Cross-Comparison of the Long End OIS Calibration Instrument "Rate" metric across the different curve
		 * 	construction methodologies.
		 */

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     OIS LONG END INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int i = 0; i < aLongEndOISComp.length; ++i)
			System.out.println ("\t[" + aLongEndOISComp[i].effectiveDate() + " => " + aLongEndOISComp[i].maturityDate() + "] = " +
				FormatUtil.FormatDouble (aLongEndOISComp[i].measureValue (new ValuationParams (dtToday, dtToday, strCurrency), null,
					MarketParamsBuilder.Create (dc, null, null, null, null, null, null),
						null, "CalibSwapRate"), 1, 6, 1.) + " | " + FormatUtil.FormatDouble (adblLongEndOISQuote[i], 1, 6, 1.) + " | " +
							FormatUtil.FormatDouble (aLongEndOISComp[i].measureValue (new ValuationParams (dtToday, dtToday, strCurrency), null,
								MarketParamsBuilder.Create (dc, null, null, null, null, null, null),
									null, "FairPremium"), 1, 6, 1.));

		return dc;
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

		String strCurrency = "EUR";

		JulianDate dtToday = org.drip.analytics.date.DateUtil.Today().addTenor ("0D");

		DiscountCurve dcIndex = CustomOISCurveBuilderSample (
			dtToday,
			"---- DISCOUNT CURVE WITH OVERNIGHT INDEX ---",
			strCurrency,
			true
		);

		DiscountCurve dcFund = CustomOISCurveBuilderSample (
			dtToday,
			"---- DISCOUNT CURVE WITH OVERNIGHT FUND ---",
			strCurrency,
			false
		);

		JulianDate dtCustomOISStart = dtToday.subtractTenor ("2M");

		UnitCouponAccrualSetting ucasCustomFixed = new UnitCouponAccrualSetting (
			2,
			"Act/360",
			false,
			"Act/360",
			false,
			strCurrency,
			true,
			CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
		);

		ComposableFloatingUnitSetting cfusCustomFloating = new ComposableFloatingUnitSetting (
			"ON",
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_OVERNIGHT,
			null,
			ForwardLabel.Create (
				strCurrency,
				"ON"
			),
			CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
			0.
		);

		ComposableFixedUnitSetting cfusCustomFixed = new ComposableFixedUnitSetting (
			"6M",
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
			null,
			0.003,
			0.,
			strCurrency
		);

		CompositePeriodSetting cpsCustomFloating = new CompositePeriodSetting (
			4,
			"3M",
			strCurrency,
			null,
			-1.,
			null,
			null,
			null,
			null
		);

		CompositePeriodSetting cpsCustomFixed = new CompositePeriodSetting (
			2,
			"6M",
			strCurrency,
			null,
			1.,
			null,
			null,
			null,
			null
		);

		List<Double> lsCustomFixedStreamEdgeDate = CompositePeriodBuilder.RegularEdgeDates (
			dtCustomOISStart,
			"6M",
			"4M",
			null
		);

		List<Double> lsCustomFloatingStreamEdgeDate = CompositePeriodBuilder.RegularEdgeDates (
			dtCustomOISStart,
			"3M",
			"4M",
			null
		);

		Stream customFloatingStream = new Stream (
			CompositePeriodBuilder.FloatingCompositeUnit (
				lsCustomFloatingStreamEdgeDate,
				cpsCustomFloating,
				cfusCustomFloating
			)
		);

		Stream customFixedStream = new Stream (
			CompositePeriodBuilder.FixedCompositeUnit (
				lsCustomFixedStreamEdgeDate,
				cpsCustomFixed,
				ucasCustomFixed,
				cfusCustomFixed
			)
		);

		FixFloatComponent ois = new FixFloatComponent (
			customFixedStream,
			customFloatingStream,
			new CashSettleParams (
				0,
				strCurrency,
				0
			)
		);

		CurveSurfaceQuoteSet mktParamsIndex = MarketParamsBuilder.Create (
			dcIndex,
			null,
			null,
			null,
			null,
			null,
			null
		);

		CurveSurfaceQuoteSet mktParamsFund = MarketParamsBuilder.Create (
			dcFund,
			null,
			null,
			null,
			null,
			null,
			null
		);

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t----------------------------------------------------------------\n");

		Map<String, Double> mapOISOutputIndex = ois.value (
			new ValuationParams (
				dtToday,
				dtToday,
				strCurrency
			),
			null,
			mktParamsIndex,
			null
		);

		Map<String, Double> mapOISOutputFund = ois.value (
			new ValuationParams (
				dtToday,
				dtToday,
				strCurrency
			),
			null,
			mktParamsFund,
			null
		);

		for (Map.Entry<String, Double> me : mapOISOutputIndex.entrySet()) {
			String strKey = me.getKey();

			double dblIndexMeasure = me.getValue();

			double dblFundMeasure = mapOISOutputFund.get (strKey);

			String strReconcile = NumberUtil.WithinTolerance (
				dblIndexMeasure,
				dblFundMeasure,
				1.e-08,
				1.e-04) ?
			"RECONCILES" :
			"DOES NOT RECONCILE";

			System.out.println ("\t" +
				FormatUtil.FormatDouble (dblIndexMeasure, 1, 8, 1.) + " | " +
				FormatUtil.FormatDouble (dblFundMeasure, 1, 8, 1.) + " | " +
				strReconcile + " <= " + strKey
			);
		}
	}
}
