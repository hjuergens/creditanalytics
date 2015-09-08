
package org.drip.sample.fra;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.analytics.rates.DiscountCurve;
import org.drip.analytics.rates.ForwardCurve;
import org.drip.function.R1ToR1.FlatUnivariate;
import org.drip.function.definition.R1ToR1;
import org.drip.param.creator.MarketParamsBuilder;
import org.drip.param.market.CurveSurfaceQuoteSet;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.fra.FRAMarketComponent;
import org.drip.quant.common.FormatUtil;
import org.drip.sample.forward.IBOR6MQuarticPolyVanilla;
import org.drip.sample.forward.OvernightIndexCurve;
import org.drip.service.api.CreditAnalytics;
import org.drip.spline.basis.ExponentialTensionSetParams;
import org.drip.spline.params.SegmentCustomBuilderControl;
import org.drip.spline.params.SegmentInelasticDesignControl;
import org.drip.spline.stretch.BoundarySettings;
import org.drip.spline.stretch.MultiSegmentSequence;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
import org.drip.state.identifier.ForwardLabel;
import org.drip.state.identifier.FundingLabel;
import org.testng.annotations.Test;

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
 * FRAMktAnalysis contains an analysis of the correlation and volatility impact on the Market FRA.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FRAMktAnalysis {
	static class FRAMktConvexityCorrection {
		double _dblParMktFwd = Double.NaN;
		double _dblParStdFwd = Double.NaN;
		double _dblConvexityCorrection = Double.NaN;

		FRAMktConvexityCorrection (
			final double dblParMktFwd,
			final double dblParStdFwd,
			final double dblConvexityCorrection)
		{
			_dblParMktFwd = dblParMktFwd;
			_dblParStdFwd = dblParStdFwd;
			_dblConvexityCorrection = dblConvexityCorrection;
		}
	}

	private static final R1ToR1 ATMLogNormalVolTermStructure (
		final JulianDate dtEpoch,
		final String[] astrTenor,
		final double[] adblATMLogNormalVolTSIn)
		throws Exception
	{
		double[] adblTime = new double[astrTenor.length + 1];
		double[] adblATMLogNormalVolTS = new double[adblATMLogNormalVolTSIn.length + 1];
		SegmentCustomBuilderControl[] aSCBC = new SegmentCustomBuilderControl[astrTenor.length];

		SegmentCustomBuilderControl scbc = new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_HYPERBOLIC_TENSION,
			new ExponentialTensionSetParams (1.),
			SegmentInelasticDesignControl.Create (
				2,
				2
			),
			null,
			null
		);

		for (int i = 0; i < adblTime.length; ++i) {
			if (0 != i) aSCBC[i - 1] = scbc;

			adblTime[i] = 0 == i ? dtEpoch.julian(): dtEpoch.addTenor (astrTenor[i - 1]).julian();

			adblATMLogNormalVolTS[i] = 0 == i ? adblATMLogNormalVolTSIn[0] : adblATMLogNormalVolTSIn[i - 1];
		}

		return MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
			"LOG_NORMAL_ATM_VOL_STRETCH",
			adblTime,
			adblATMLogNormalVolTS,
			aSCBC,
			null,
			BoundarySettings.NaturalStandard(),
			MultiSegmentSequence.CALIBRATE
		).toAU();
	}

	public static final FRAMktConvexityCorrection FRAMktMetric (
		final JulianDate dtValue,
		final DiscountCurve dcEONIA,
		final ForwardCurve fcEURIBOR6M,
		final String strForwardStartTenor,
		final R1ToR1 auEONIAVolTS,
		final R1ToR1 auEURIBOR6MVolTS,
		final double dblEONIAEURIBOR6MCorrelation)
		throws Exception
	{
		String strTenor = "6M";
		String strCurrency = "USD";

		ForwardLabel fri = ForwardLabel.Create (
			strCurrency,
			strTenor
		);

		FundingLabel fundingLabel = FundingLabel.Standard (strCurrency);

		JulianDate dtForwardStart = dtValue.addTenor (strForwardStartTenor);

		FRAMarketComponent fra = SingleStreamComponentBuilder.FRAMarket (
			dtForwardStart,
			fri,
			0.006
		);

		CurveSurfaceQuoteSet mktParams = MarketParamsBuilder.Create (
			dcEONIA,
			fcEURIBOR6M,
			null,
			null,
			null,
			null,
			null,
			null
		);

		ValuationParams valParams = new ValuationParams (
			dtValue,
			dtValue,
			strCurrency
		);

		mktParams.setForwardCurveVolSurface (
			fri,
			auEURIBOR6MVolTS
		);

		mktParams.setFundingCurveVolSurface (
			fundingLabel,
			auEONIAVolTS
		);

		mktParams.setForwardFundingCorrSurface (
			fri,
			fundingLabel,
			new FlatUnivariate (dblEONIAEURIBOR6MCorrelation)
		);

		Map<String, Double> mapFRAOutput = fra.value (
			valParams,
			null,
			mktParams,
			null
		);

		return new FRAMktConvexityCorrection (
			mapFRAOutput.get ("shiftedlognormalparmarketfra"),
			mapFRAOutput.get ("parstandardfra"),
			mapFRAOutput.get ("shiftedlognormalconvexitycorrection")
		);
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

		String strTenor = "6M";
		String strCurrency = "USD";

		JulianDate dtToday = DateUtil.Today().addTenor ("0D");

		DiscountCurve dcEONIA = OvernightIndexCurve.MakeDC (
			dtToday,
			strCurrency
		);

		ForwardCurve fcEURIBOR6M = IBOR6MQuarticPolyVanilla.Make6MForward (
			dtToday,
			strCurrency,
			strTenor
		);

		String[] astrForwardStartTenor = {
			"6M", "1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y"
		};

		double dblEONIAEURIBOR6MCorrelation = 0.8;

		R1ToR1 auATMVolTS = ATMLogNormalVolTermStructure (
			dtToday,
			astrForwardStartTenor,
			new double[] {
				0.5946, // 6M
				0.5311,	// 1Y
				0.3307,	// 2Y
				0.2929,	// 3Y
				0.2433,	// 4Y
				0.2013,	// 5Y
				0.1855,	// 6Y
				0.1789,	// 7Y
				0.1655,	// 8Y
				0.1574	// 9Y
			}
		);

		R1ToR1 auEONIAVolTS = auATMVolTS;
		R1ToR1 auEURIBOR6MVolTS = auATMVolTS;

		System.out.println ("\t---------------------------------");

		System.out.println ("\t---------------------------------");

		System.out.println ("\t---------------------------------");

		System.out.println ("\t---------------------------------");

		System.out.println ("\tTNR =>   MKT   |   STD   |  CONV ");

		System.out.println ("\t---------------------------------");

		for (String strForwardStartTenor : astrForwardStartTenor) {
			FRAMktConvexityCorrection fraMktMetric = FRAMktMetric (
				dtToday,
				dcEONIA,
				fcEURIBOR6M,
				strForwardStartTenor,
				auEONIAVolTS,
				auEURIBOR6MVolTS,
				dblEONIAEURIBOR6MCorrelation
			);

			System.out.println (
				"\t " + strForwardStartTenor + " => " +
				FormatUtil.FormatDouble (fraMktMetric._dblParMktFwd, 1, 3, 100.) + "% | " +
				FormatUtil.FormatDouble (fraMktMetric._dblParStdFwd, 1, 3, 100.) + "% | " +
				FormatUtil.FormatDouble (fraMktMetric._dblConvexityCorrection, 1, 2, 10000.)
			);
		}
	}
}
