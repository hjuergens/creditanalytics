
package org.drip.sample.stretch;

import org.drip.quant.common.FormatUtil;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.grid.OverlappingStretchSpan;
import org.drip.spline.grid.Span;
import org.drip.spline.multidimensional.WireSurfaceStretch;
import org.drip.spline.params.SegmentCustomBuilderControl;
import org.drip.spline.params.SegmentInelasticDesignControl;
import org.drip.spline.stretch.BoundarySettings;
import org.drip.spline.stretch.MultiSegmentSequence;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
import org.testng.annotations.Test;

import java.util.TreeMap;

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
 * Surface2D demonstrates the Surface 2D Stretch Construction and usage API.
 *
 * @author Lakshmi Krishnamurthy
 */

public class Surface2D {

	@Test(dataProvider = "mainparam", dataProviderClass = org.drip.sample.TestNGDataProvider.class)
	public static void main (
		final String[] astrArgs)
		throws Exception
	{
		double[] adblATMFactor = new double[] {
			0.8, 0.9, 1.0, 1.1, 1.2
		};
		double[] adblTTE = new double[] {
			1., 2., 3., 4., 5.
		};

		double[][] aadblImpliedVolatility = new double[][] {
			{0.44, 0.38, 0.33, 0.27, 0.25},
			{0.41, 0.34, 0.30, 0.22, 0.27},
			{0.36, 0.31, 0.28, 0.30, 0.37},
			{0.38, 0.31, 0.34, 0.40, 0.47},
			{0.43, 0.46, 0.48, 0.52, 0.57}
		};

		SegmentCustomBuilderControl scbcSpan = new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (4),
			SegmentInelasticDesignControl.Create (
				2,
				2
			),
			null,
			null
		);

		TreeMap<Double, Span> mapSpan = new TreeMap<Double, Span>();

		SegmentCustomBuilderControl[] aSCBCSpan = new SegmentCustomBuilderControl[adblATMFactor.length - 1];

		for (int i = 0; i < aSCBCSpan.length; ++i)
			aSCBCSpan[i] = scbcSpan;

		for (int i = 0; i < adblATMFactor.length; ++i)
			mapSpan.put (adblATMFactor[i], new OverlappingStretchSpan (
				MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
					"Stretch@" + adblTTE + "@" + org.drip.quant.common.StringUtil.GUID(),
					adblTTE,
					aadblImpliedVolatility[i],
					aSCBCSpan,
					null,
					BoundarySettings.NaturalStandard(),
					MultiSegmentSequence.CALIBRATE
				)
			)
		);

		SegmentCustomBuilderControl scbcSurface = new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (4),
			SegmentInelasticDesignControl.Create (
				2,
				2
			),
			null,
			null
		);

		WireSurfaceStretch ss = new WireSurfaceStretch (
			"SurfaceStretch@" + org.drip.quant.common.StringUtil.GUID(),
			scbcSurface,
			mapSpan
		);

		System.out.println ("\n\t|------------------------------------------------------------|");

		System.out.println ("\t|----------------- INPUT  SURFACE  RECOVERY -----------------|");

		System.out.print ("\t|------------------------------------------------------------|\n\t|  ATM/TTE  =>");

		for (double dblTTE : adblTTE)
			System.out.print ("   " + FormatUtil.FormatDouble (dblTTE, 1, 2, 1.) + " ");

		System.out.println ("  |\n\t|------------------------------------------------------------|");

		for (double dblATMFactor : adblATMFactor) {
			System.out.print ("\t|  " + FormatUtil.FormatDouble (dblATMFactor, 1, 2, 1.) + "    =>");

			for (double dblTTE : adblTTE)
				System.out.print ("  " +
					FormatUtil.FormatDouble (ss.responseValue (
						dblATMFactor,
						dblTTE
					), 2, 2, 100.) + "%");

			System.out.print ("  |\n");
		}

		System.out.println ("\t|------------------------------------------------------------|");

		adblATMFactor = new double[] {
			0.850, 0.925, 1.000, 1.075, 1.15
		};
		adblTTE = new double[] {
			1.50, 2.25, 3., 3.75, 4.50
		};

		System.out.println ("\n\t|------------------------------------------------------------|");

		System.out.println ("\t|------------- IN-SURFACE RESPONSE CALCULATION --------------|");

		System.out.print ("\t|------------------------------------------------------------|\n\t|  ATM/TTE  =>");

		for (double dblTTE : adblTTE)
			System.out.print ("   " + FormatUtil.FormatDouble (dblTTE, 1, 2, 1.) + " ");

		System.out.println ("  |\n\t|------------------------------------------------------------|");

		for (double dblATMFactor : adblATMFactor) {
			System.out.print ("\t|  " + FormatUtil.FormatDouble (dblATMFactor, 1, 2, 1.) + "    =>");

			for (double dblTTE : adblTTE)
				System.out.print ("  " +
					FormatUtil.FormatDouble (
						ss.responseValue (
							dblATMFactor,
							dblTTE
						), 2, 2, 100.) + "%");

			System.out.print ("  |\n");
		}

		System.out.println ("\t|------------------------------------------------------------|");

		adblATMFactor = new double[] {
			0.70, 0.85, 1.00, 1.15, 1.30
		};
		adblTTE = new double[] {
			0.50, 1.75, 3.00, 4.25, 5.50
		};

		System.out.println ("\n\t|------------------------------------------------------------|");

		System.out.println ("\t|------------- OFF-SURFACE RESPONSE CALCULATION -------------|");

		System.out.print ("\t|------------------------------------------------------------|\n\t|  ATM/TTE  =>");

		for (double dblTTE : adblTTE)
			System.out.print ("   " + FormatUtil.FormatDouble (dblTTE, 1, 2, 1.) + " ");

		System.out.println ("  |\n\t|------------------------------------------------------------|");

		for (double dblATMFactor : adblATMFactor) {
			System.out.print ("\t|  " + FormatUtil.FormatDouble (dblATMFactor, 1, 2, 1.) + "    =>");

			for (double dblTTE : adblTTE)
				System.out.print ("  " + FormatUtil.FormatDouble (
					ss.responseValue (
						dblATMFactor,
						dblTTE
					), 2, 2, 100.) + "%");

			System.out.print ("  |\n");
		}

		System.out.println ("\t|------------------------------------------------------------|");
	}
}
