
package org.drip.sample.lmm;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.definition.MarketSurface;
import org.drip.analytics.rates.DiscountCurve;
import org.drip.analytics.rates.ForwardCurve;
import org.drip.dynamics.lmm.BGMPointUpdate;
import org.drip.dynamics.lmm.LognormalLIBORPointEvolver;
import org.drip.dynamics.lmm.LognormalLIBORVolatility;
import org.drip.param.creator.ScenarioForwardCurveBuilder;
import org.drip.param.creator.ScenarioMarketSurfaceBuilder;
import org.drip.quant.common.FormatUtil;
import org.drip.sequence.random.BoxMullerGaussian;
import org.drip.sequence.random.PrincipalFactorSequenceGenerator;
import org.drip.sequence.random.UnivariateSequenceGenerator;
import org.drip.service.api.CreditAnalytics;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.SegmentCustomBuilderControl;
import org.drip.spline.params.SegmentInelasticDesignControl;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
import org.drip.state.creator.DiscountCurveBuilder;
import org.drip.state.identifier.ForwardLabel;
import org.drip.state.identifier.FundingLabel;
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
 * PointAncillaryMetricsDynamics demonstrates the Construction and Usage of the Point LIBOR State Evolver,
 *  and the eventual Evolution of the related Ancillary bDiscount/Forward Latent State Quantification
 *   Metrics. The References are:
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

public class PointAncillaryMetricsDynamics {

	private static final MarketSurface FlatVolatilitySurface (
		final JulianDate dtStart,
		final String strCurrency,
		final double dblFlatVol)
		throws Exception
	{
		return ScenarioMarketSurfaceBuilder.CustomSplineWireSurface (
			"VIEW_TARGET_VOLATILITY_SURFACE",
			dtStart,
			strCurrency,
			null,
			new double[] {
				dtStart.julian(),
				dtStart.addYears (2).julian(),
				dtStart.addYears (4).julian(),
				dtStart.addYears (6).julian(),
				dtStart.addYears (8).julian(),
				dtStart.addYears (10).julian()
			},
			new double[] {
				dtStart.julian(),
				dtStart.addYears (2).julian(),
				dtStart.addYears (4).julian(),
				dtStart.addYears (6).julian(),
				dtStart.addYears (8).julian(),
				dtStart.addYears (10).julian()
			},
			new double[][] {
				{dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol},
				{dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol},
				{dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol},
				{dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol},
				{dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol},
				{dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol, dblFlatVol},
			},
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (4),
				SegmentInelasticDesignControl.Create (
					2,
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

	private static final LognormalLIBORVolatility LLVInstance (
		final double dblSpotDate,
		final ForwardLabel forwardLabel,
		final MarketSurface[] aMS,
		final double[][] aadblCorrelation,
		final int iNumFactor)
		throws Exception
	{
		UnivariateSequenceGenerator[] aUSG = new UnivariateSequenceGenerator[aMS.length];

		for (int i = 0; i < aUSG.length; ++i)
			aUSG[i] = new BoxMullerGaussian (0., 1.);

		return new LognormalLIBORVolatility (
			dblSpotDate,
			forwardLabel,
			aMS,
			new PrincipalFactorSequenceGenerator (
				aUSG,
				aadblCorrelation,
				iNumFactor
			)
		);
	}

	private static final void DisplayRunSnap (
		final BGMPointUpdate bgmRunSnap)
		throws Exception
	{
		System.out.println (
			"\t| [" + new JulianDate (bgmRunSnap.evolutionStartDate()) +
			" -> " + new JulianDate (bgmRunSnap.evolutionFinishDate()) +
			"]  => " + FormatUtil.FormatDouble (bgmRunSnap.libor(), 1, 2, 100.) +
			"% | " + FormatUtil.FormatDouble (bgmRunSnap.liborIncrement(), 2, 0, 10000.) +
			" | " + FormatUtil.FormatDouble (bgmRunSnap.instantaneousEffectiveForwardRate(), 1, 2, 100.) +
			"% | " + FormatUtil.FormatDouble (bgmRunSnap.instantaneousNominalForwardRate(), 1, 2, 100.) +
			"% | "
		);
	}

	@Test(dataProvider = "mainparam", dataProviderClass = org.drip.sample.TestNGDataProvider.class)
	public static void main (
		final String[] astrArgs)
		throws Exception
	{
		CreditAnalytics.Init ("");

		String strTenor = "3M";
		String strCurrency = "USD";
		double dblFlatVol1 = 0.35;
		double dblFlatVol2 = 0.42;
		double dblFlatVol3 = 0.27;
		double dblZeroRate = 0.02;
		double dblFlatForwardRate = 0.02;
		int iNumRun = 20;

		int[] aiNumFactor = {
			1, 2, 3
		};

		double[][] aadblCorrelation = new double[][] {
			{1.0, 0.1, 0.2},
			{0.1, 1.0, 0.2},
			{0.2, 0.1, 1.0}
		};

		ForwardLabel forwardLabel = ForwardLabel.Create (
			strCurrency,
			strTenor
		);

		FundingLabel fundingLabel = FundingLabel.Standard (
			strCurrency
		);

		JulianDate dtSpot = org.drip.analytics.date.DateUtil.Today();

		MarketSurface[] aMS = new MarketSurface[] {
			FlatVolatilitySurface (
				dtSpot,
				strCurrency,
				dblFlatVol1
			),
			FlatVolatilitySurface (
				dtSpot,
				strCurrency,
				dblFlatVol2
			),
			FlatVolatilitySurface (
				dtSpot,
				strCurrency,
				dblFlatVol3
			)
		};

		ForwardCurve fc = ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
			dtSpot,
			forwardLabel,
			dblFlatForwardRate,
			null
		);

		DiscountCurve dc = DiscountCurveBuilder.CreateFromFlatRate (
			dtSpot,
			strCurrency,
			null,
			dblZeroRate
		);

		double dblSpotDate = dtSpot.julian();

		double dblViewDate = dtSpot.addTenor ("1Y").julian();

		double dblViewTimeIncrement = 1. / 365.;

		for (int iNumFactor : aiNumFactor) {
			System.out.println ("\n\n\t|----------------------------------------------------------------------------------------------------------|");

			System.out.println ("\t|                                                                                                          |");

			System.out.println ("\t|                             LOG-NORMAL LIBOR EVOLVER                                                     |");

			System.out.println ("\t|                             ---------- ----- -------                                                     |");

			System.out.println ("\t|                                                                                                          |");

			System.out.println ("\t|       Num Factors: " + iNumFactor + "                                                                                     |");

			System.out.println ("\t|       Start Date                                                                                         |");

			System.out.println ("\t|       End Date                                                                                           |");

			System.out.println ("\t|       Adjacent Step LIBOR (%)                                                                            |");

			System.out.println ("\t|       Adjacent Step LIBOR Increment (bp)                                                                 |");

			System.out.println ("\t|       Adjacent Step Effective Annual Forward Rate (%)                                                    |");

			System.out.println ("\t|       Adjacent Step Nominal Annual Forward Rate (%)                                                      |");

			System.out.println ("\t|                                                                                                          |");

			System.out.println ("\t|----------------------------------------------------------------------------------------------------------|");

			LognormalLIBORPointEvolver lle = new LognormalLIBORPointEvolver (
				fundingLabel,
				forwardLabel,
				LLVInstance (
					dtSpot.julian(),
					forwardLabel,
					aMS,
					aadblCorrelation,
					iNumFactor
				),
				fc,
				dc
			);

			for (int iRun = 0; iRun < iNumRun; ++iRun)
				DisplayRunSnap (
					lle.evolve (
						dblSpotDate,
						dblViewDate,
						dblViewTimeIncrement,
						null
					)
				);

			System.out.println ("\t|----------------------------------------------------------------------------------------------------------|");
		}
	}
}
