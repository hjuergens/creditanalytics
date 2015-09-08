
package org.drip.sample.option;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.analytics.definition.MarketSurface;
import org.drip.param.creator.ScenarioMarketSurfaceBuilder;
import org.drip.param.valuation.CollateralizationParams;
import org.drip.quant.common.FormatUtil;
import org.drip.spline.basis.ExponentialTensionSetParams;
import org.drip.spline.basis.KaklisPandelisSetParams;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.SegmentCustomBuilderControl;
import org.drip.spline.params.SegmentInelasticDesignControl;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
import org.testng.annotations.Test;

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
 * CustomVolSurfaceBuilder contains an Comparison of the Construction of the Volatility Surface using
 * 	different Splining Techniques.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CustomVolSurfaceBuilder {
	private static final SegmentCustomBuilderControl CubicPolySCBC()
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (4),
			SegmentInelasticDesignControl.Create (
				2,
				2
			),
			null,
			null
		);
	}

	private static final SegmentCustomBuilderControl QuarticPolySCBC()
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (5),
			SegmentInelasticDesignControl.Create (
				2,
				2
			),
			null,
			null
		);
	}

	private static final SegmentCustomBuilderControl KaklisPandelisSCBC()
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_KAKLIS_PANDELIS,
			new KaklisPandelisSetParams (2),
			SegmentInelasticDesignControl.Create (
				2,
				2
			),
			null,
			null
		);
	}

	private static final SegmentCustomBuilderControl KLKHyperbolicSCBC(
		final double dblTension)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_HYPERBOLIC_TENSION,
			new ExponentialTensionSetParams (dblTension),
			SegmentInelasticDesignControl.Create (
				2,
				2
			),
			null,
			null
		);
	}

	private static final SegmentCustomBuilderControl KLKRationalLinearSCBC(
		final double dblTension)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_RATIONAL_LINEAR_TENSION,
			new ExponentialTensionSetParams (dblTension),
			SegmentInelasticDesignControl.Create (
				2,
				2
			),
			null,
			null
		);
	}

	private static final SegmentCustomBuilderControl KLKRationalQuadraticSCBC(
		final double dblTension)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_RATIONAL_QUADRATIC_TENSION,
			new ExponentialTensionSetParams (dblTension),
			SegmentInelasticDesignControl.Create (
				2,
				2
			),
			null,
			null
		);
	}

	private static final void EvaluateSplineSurface (
		final MarketSurface volSurface,
		final double[] adblStrikeATMFactor,
		final String[] astrMaturityTenor)
		throws Exception
	{
		System.out.println ("\t|------------------------------------------------------------|");

		System.out.print ("\t|------------------------------------------------------------|\n\t|  ATM/TTE  =>");

		for (String strMaturity : astrMaturityTenor)
			System.out.print ("    " + strMaturity + "  ");

		System.out.println ("  |\n\t|------------------------------------------------------------|");

		for (double dblStrike : adblStrikeATMFactor) {
			System.out.print ("\t|  " + FormatUtil.FormatDouble (dblStrike, 1, 2, 1.) + "    =>");

			for (String strMaturity : astrMaturityTenor)
				System.out.print ("  " + FormatUtil.FormatDouble (volSurface.node (dblStrike, strMaturity), 2, 2, 100.) + "%");

			System.out.print ("  |\n");
		}

		System.out.println ("\t|------------------------------------------------------------|");
	}

	@Test(dataProvider = "mainparam", dataProviderClass = org.drip.sample.TestNGDataProvider.class)
	public static void main (
		final String[] astrArgs)
		throws Exception
	{
		JulianDate dtStart = DateUtil.Today();

		double[] adblStrikeATMFactorCalib = new double[] {
			0.8, 0.9, 1.0, 1.1, 1.2
		};
		String[] astrMaturityTenorCalib = new String[] {
			"1Y", "2Y", "3Y", "4Y", "5Y"
		};

		double[][] aadblImpliedVolatility = new double[][] {
			{0.44, 0.38, 0.33, 0.27, 0.25},
			{0.41, 0.34, 0.30, 0.22, 0.27},
			{0.36, 0.31, 0.28, 0.30, 0.37},
			{0.38, 0.31, 0.34, 0.40, 0.47},
			{0.43, 0.46, 0.48, 0.52, 0.57}
		};

		double[] adblStrikeATMFactorCalc = new double[] {
			0.700, 0.850, 1.000, 1.150, 1.300
		};
		String[] astrMaturityTenorCalc = new String[] {
			"06M", "21M", "36M", "51M", "66M"
		};

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.CubicPolynomialWireSurface (
				"CUBIC_POLY_VOL_SURFACE",
				dtStart,
				"USD",
				new CollateralizationParams (
					"OVERNIGHT_INDEX",
					"USD"
				),
				adblStrikeATMFactorCalib,
				astrMaturityTenorCalib,
				aadblImpliedVolatility
			),
			adblStrikeATMFactorCalc,
			astrMaturityTenorCalc
		);

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.QuarticPolynomialWireSurface (
				"QUARTIC_POLY_VOL_SURFACE",
				dtStart,
				"USD",
				new CollateralizationParams (
					"OVERNIGHT_INDEX",
					"USD"
				),
				adblStrikeATMFactorCalib,
				astrMaturityTenorCalib,
				aadblImpliedVolatility
			),
			adblStrikeATMFactorCalc,
			astrMaturityTenorCalc
		);

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.KaklisPandelisWireSurface (
				"KAKLIS_PANDELIS_VOL_SURFACE",
				dtStart,
				"USD",
				new CollateralizationParams (
					"OVERNIGHT_INDEX",
					"USD"
				),
				adblStrikeATMFactorCalib,
				astrMaturityTenorCalib,
				aadblImpliedVolatility
			),
			adblStrikeATMFactorCalc,
			astrMaturityTenorCalc
		);

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.KLKHyperbolicWireSurface (
				"KLK_HYPERBOLIC_VOL_SURFACE",
				dtStart,
				"USD",
				new CollateralizationParams (
					"OVERNIGHT_INDEX",
					"USD"
				),
				adblStrikeATMFactorCalib,
				astrMaturityTenorCalib,
				aadblImpliedVolatility,
				1.
			),
			adblStrikeATMFactorCalc,
			astrMaturityTenorCalc
		);

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.KLKRationalLinearWireSurface (
				"KLK_RATIONAL_LINEAR_VOL_SURFACE",
				dtStart,
				"USD",
				new CollateralizationParams (
					"OVERNIGHT_INDEX",
					"USD"
				),
				adblStrikeATMFactorCalib,
				astrMaturityTenorCalib,
				aadblImpliedVolatility,
				1.
			),
			adblStrikeATMFactorCalc,
			astrMaturityTenorCalc
		);

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.KLKRationalQuadraticWireSurface (
				"KLK_RATIONAL_QUADRATIC_VOL_SURFACE",
				dtStart,
				"USD",
				new CollateralizationParams (
					"OVERNIGHT_INDEX",
					"USD"
				),
				adblStrikeATMFactorCalib,
				astrMaturityTenorCalib,
				aadblImpliedVolatility,
				1.
			),
			adblStrikeATMFactorCalc,
			astrMaturityTenorCalc
		);

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.CustomWireSurface (
				"CUBIC_WIRESPAN_QUARTIC_SURFACE_VOL_SURFACE",
				dtStart,
				"USD",
				new CollateralizationParams (
					"OVERNIGHT_INDEX",
					"USD"
				),
				adblStrikeATMFactorCalib,
				astrMaturityTenorCalib,
				aadblImpliedVolatility,
				CubicPolySCBC(),
				QuarticPolySCBC()
			),
			adblStrikeATMFactorCalc,
			astrMaturityTenorCalc
		);

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.CustomWireSurface (
				"KAKLISPANDELIS_WIRESPAN_KLKHYPERBOLIC_SURFACE_VOL_SURFACE",
				dtStart,
				"USD",
				new CollateralizationParams (
					"OVERNIGHT_INDEX",
					"USD"
				),
				adblStrikeATMFactorCalib,
				astrMaturityTenorCalib,
				aadblImpliedVolatility,
				KaklisPandelisSCBC(),
				KLKHyperbolicSCBC (2.)
			),
			adblStrikeATMFactorCalc,
			astrMaturityTenorCalc
		);

		EvaluateSplineSurface (
			ScenarioMarketSurfaceBuilder.CustomWireSurface (
				"KLKRATIONALLINEAR_WIRESPAN_KLKRATIONALQUADRATIC_SURFACE_VOL_SURFACE",
				dtStart,
				"USD",
				new CollateralizationParams (
					"OVERNIGHT_INDEX",
					"USD"
				),
				adblStrikeATMFactorCalib,
				astrMaturityTenorCalib,
				aadblImpliedVolatility,
				KLKRationalLinearSCBC (3.),
				KLKRationalQuadraticSCBC (1.)
			),
			adblStrikeATMFactorCalc,
			astrMaturityTenorCalc
		);
	}
}
