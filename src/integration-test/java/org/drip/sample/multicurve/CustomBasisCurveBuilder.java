
package org.drip.sample.multicurve;

import org.drip.analytics.date.*;
import org.drip.analytics.rates.BasisCurve;
import org.drip.param.creator.ScenarioBasisCurveBuilder;
import org.drip.param.valuation.CollateralizationParams;
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
 * CustomBasisCurveBuilder contains the org.drip.sample demonstrating the full functionality behind creating highly
 * 	customized spline based Basis curves.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CustomBasisCurveBuilder {
	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		/*
		 * Initialize the Credit Analytics Library
		 */

		CreditAnalytics.Init ("");

		JulianDate dtToday = DateUtil.Today();

		String[] astrTenor = new String[] {
			"1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "11Y", "12Y", "15Y", "20Y", "25Y", "30Y"
		};

		double[] adblBasis = new double[] {
			0.00186,    //  1Y
			0.00127,    //  2Y
			0.00097,    //  3Y
			0.00080,    //  4Y
			0.00067,    //  5Y
			0.00058,    //  6Y
			0.00051,    //  7Y
			0.00046,    //  8Y
			0.00042,    //  9Y
			0.00038,    // 10Y
			0.00035,    // 11Y
			0.00033,    // 12Y
			0.00028,    // 15Y
			0.00022,    // 20Y
			0.00020,    // 25Y
			0.00018     // 30Y
		};

		BasisCurve bcCubicPolynomial = ScenarioBasisCurveBuilder.CubicPolynomialBasisCurve (
			"USD3M6MBasis_CubicPolynomial",
			dtToday,
			ForwardLabel.Create (
				"USD",
				"6M"
			),
			ForwardLabel.Create (
				"USD",
				"3M"
			),
			false,
			new CollateralizationParams (
				"OVERNIGHT",
				"USD"
			),
			astrTenor,
			adblBasis
		);

		BasisCurve bcQuinticPolynomial = ScenarioBasisCurveBuilder.QuarticPolynomialBasisCurve (
			"USD3M6MBasis_QuinticPolynomial",
			dtToday,
			ForwardLabel.Create (
				"USD",
				"6M"
			),
			ForwardLabel.Create (
				"USD",
				"3M"
			),
			false,
			new CollateralizationParams (
				"OVERNIGHT",
				"USD"
			),
			astrTenor,
			adblBasis
		);

		BasisCurve bcKaklisPandelis = ScenarioBasisCurveBuilder.KaklisPandelisBasisCurve (
			"USD3M6MBasis_KaklisPandelis",
			dtToday,
			ForwardLabel.Create (
				"USD",
				"6M"
			),
			ForwardLabel.Create (
				"USD",
				"3M"
			),
			false,
			new CollateralizationParams (
				"OVERNIGHT",
				"USD"
			),
			astrTenor,
			adblBasis
		);

		BasisCurve bcKLKHyperbolic = ScenarioBasisCurveBuilder.KLKHyperbolicBasisCurve (
			"USD3M6MBasis_KLKHyperbolic",
			dtToday,
			ForwardLabel.Create (
				"USD",
				"6M"
			),
			ForwardLabel.Create (
				"USD",
				"3M"
			),
			false,
			new CollateralizationParams (
				"OVERNIGHT",
				"USD"
			),
			astrTenor,
			adblBasis,
			1.
		);

		BasisCurve bcKLKRationalLinear = ScenarioBasisCurveBuilder.KLKRationalLinearBasisCurve (
			"USD3M6MBasis_KLKRationalLinear",
			dtToday,
			ForwardLabel.Create (
				"USD",
				"6M"
			),
			ForwardLabel.Create (
				"USD",
				"3M"
			),
			false,
			new CollateralizationParams (
				"OVERNIGHT",
				"USD"
			),
			astrTenor,
			adblBasis,
			0.1
		);

		BasisCurve bcKLKRationalQuadratic = ScenarioBasisCurveBuilder.KLKRationalLinearBasisCurve (
			"USD3M6MBasis_KLKRationalQuadratic",
			dtToday,
			ForwardLabel.Create (
				"USD",
				"6M"
			),
			ForwardLabel.Create (
				"USD",
				"3M"
			),
			false,
			new CollateralizationParams (
				"OVERNIGHT",
				"USD"
			),
			astrTenor,
			adblBasis,
			2.
		);

		System.out.println ("\tPrinting the Basis Node Values in Order (Left -> Right):");

		System.out.println ("\t\tCalculated Cubic Polynomial Basis (%)");

		System.out.println ("\t\tCalculated Quintic Polynomial Basis (%)");

		System.out.println ("\t\tCalculated Kaklis Pandelis Basis (%)");

		System.out.println ("\t\tCalculated KLK Hyperbolic Basis (%)");

		System.out.println ("\t\tCalculated KLK Rational Linear Basis (%)");

		System.out.println ("\t\tCalculated KLK Rational Quadratic Basis (%)");

		System.out.println ("\t\tInput Quote (bp)");

		System.out.println ("\t-------------------------------------------------------------");

		System.out.println ("\t-------------------------------------------------------------");

		for (int i = 0; i < adblBasis.length; ++i)
			System.out.println ("\t" + astrTenor[i] + " => " +
				FormatUtil.FormatDouble (bcCubicPolynomial.basis (astrTenor[i]), 1, 2, 10000.) + " | " +
				FormatUtil.FormatDouble (bcQuinticPolynomial.basis (astrTenor[i]), 1, 2, 10000.) + " | " +
				FormatUtil.FormatDouble (bcKaklisPandelis.basis (astrTenor[i]), 1, 2, 10000.) + " | " +
				FormatUtil.FormatDouble (bcKLKHyperbolic.basis (astrTenor[i]), 1, 2, 10000.) + " | " +
				FormatUtil.FormatDouble (bcKLKRationalLinear.basis (astrTenor[i]), 1, 2, 10000.) + " | " +
				FormatUtil.FormatDouble (bcKLKRationalQuadratic.basis (astrTenor[i]), 1, 2, 10000.) + " | " +
				FormatUtil.FormatDouble (adblBasis[i], 1, 2, 10000.)
			);

		System.out.println ("\n\t|----------------------------------------------------------------------------|");

		System.out.println ("\t|  DATE    =>  CUBIC | QUINTIC  | KAKPAND | KLKHYPER | KLKRATLNR | KLKRATQUA |");

		System.out.println ("\t|----------------------------------------------------------------------------|\n");

		for (int i = 3; i < 30; ++i) {
			JulianDate dt = dtToday.addTenor (i + "Y");

			System.out.println ("\t" + dt + " => " +
				FormatUtil.FormatDouble (bcCubicPolynomial.basis (dt), 1, 2, 10000.) + "  |  " +
				FormatUtil.FormatDouble (bcQuinticPolynomial.basis (dt), 1, 2, 10000.) + "   |  " +
				FormatUtil.FormatDouble (bcKaklisPandelis.basis (dt), 1, 2, 10000.) + "  |  " +
				FormatUtil.FormatDouble (bcKLKHyperbolic.basis (dt), 1, 2, 10000.) + "   |  " +
				FormatUtil.FormatDouble (bcKLKRationalLinear.basis (dt), 1, 2, 10000.) + "    |  " +
				FormatUtil.FormatDouble (bcKLKRationalQuadratic.basis (dt), 1, 2, 10000.) + "    |  "
			);
		}

		System.out.println ("\n\t|----------------------------------------------------------------------------|");
	}
}
