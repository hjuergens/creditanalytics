
package org.drip.sample.sequence;

import org.drip.measure.continuous.R1;
import org.drip.measure.continuous.R1Lebesgue;
import org.drip.quant.common.FormatUtil;
import org.drip.sequence.metrics.BoundedSequenceAgnosticMetrics;
import org.drip.sequence.metrics.SingleSequenceAgnosticMetrics;
import org.drip.sequence.random.BoundedUniform;
import org.drip.sequence.random.UnivariateSequenceGenerator;
import org.drip.service.api.CreditAnalytics;
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
 * IIDSequenceSumBound demonstrates the Computation of the Different Probabilistic Bounds for Sums of i.i.d.
 * 	Random Sequences.
 *
 * @author Lakshmi Krishnamurthy
 */

public class IIDSequenceSumBound {

	private static final void Head (
		final String strHeader)
	{
		System.out.println();

		System.out.println ("\t|---------------------------------------------------------------------------------------|");

		System.out.println (strHeader);

		System.out.println ("\t|---------------------------------------------------------------------------------------|");

		System.out.println ("\t|   SAMPLE  ||               <-               TOLERANCES               ->               |");

		System.out.println ("\t|---------------------------------------------------------------------------------------|");
	}

	private static final void WeakLawBounds (
		final UnivariateSequenceGenerator iidsg,
		final R1 dist,
		final int[] aiSampleSize,
		final double[] adblTolerance)
		throws Exception
	{
		for (int iSampleSize : aiSampleSize) {
			SingleSequenceAgnosticMetrics ssamDist = iidsg.sequence (
				iSampleSize,
				dist
			);

			String strDump = "\t| " + FormatUtil.FormatDouble (iSampleSize, 8, 0, 1) + " => ";

			for (double dblTolerance : adblTolerance)
				strDump += FormatUtil.FormatDouble (ssamDist.weakLawAverageBounds (dblTolerance).upper(), 1, 9, 1.) + " | ";

			System.out.println (strDump);
		}
	}

	private static final void ChernoffHoeffdingBounds (
		final UnivariateSequenceGenerator iidsg,
		final R1 dist,
		final double dblSupport,
		final int[] aiSampleSize,
		final double[] adblTolerance)
		throws Exception
	{
		for (int iSampleSize : aiSampleSize) {
			BoundedSequenceAgnosticMetrics ssamDist = (BoundedSequenceAgnosticMetrics) iidsg.sequence (
				iSampleSize,
				dist
			);

			String strDump = "\t| " + FormatUtil.FormatDouble (iSampleSize, 8, 0, 1) + " => ";

			for (double dblTolerance : adblTolerance)
				strDump += FormatUtil.FormatDouble (ssamDist.chernoffHoeffdingAverageBounds (dblTolerance).upper(), 1, 9, 1.) + " | ";

			System.out.println (strDump);
		}
	}

	private static final void BennettBounds (
		final UnivariateSequenceGenerator iidsg,
		final R1 dist,
		final double dblSupport,
		final int[] aiSampleSize,
		final double[] adblTolerance)
		throws Exception
	{
		for (int iSampleSize : aiSampleSize) {
			BoundedSequenceAgnosticMetrics ssamDist = (BoundedSequenceAgnosticMetrics) iidsg.sequence (
				iSampleSize,
				dist
			);

			String strDump = "\t| " + FormatUtil.FormatDouble (iSampleSize, 8, 0, 1) + " => ";

			for (double dblTolerance : adblTolerance)
				strDump += FormatUtil.FormatDouble (ssamDist.bennettAverageBounds (dblTolerance).upper(), 1, 9, 1.) + " | ";

			System.out.println (strDump);
		}
	}

	private static final void BernsteinBounds (
		final UnivariateSequenceGenerator iidsg,
		final R1 dist,
		final double dblSupport,
		final int[] aiSampleSize,
		final double[] adblTolerance)
		throws Exception
	{
		for (int iSampleSize : aiSampleSize) {
			BoundedSequenceAgnosticMetrics ssamDist = (BoundedSequenceAgnosticMetrics) iidsg.sequence (
				iSampleSize,
				dist
			);

			String strDump = "\t| " + FormatUtil.FormatDouble (iSampleSize, 8, 0, 1) + " => ";

			for (double dblTolerance : adblTolerance)
				strDump += FormatUtil.FormatDouble (ssamDist.bernsteinAverageBounds (dblTolerance).upper(), 1, 9, 1.) + " | ";

			System.out.println (strDump);
		}
	}

	@Test(dataProvider = "mainparam", dataProviderClass = org.drip.sample.TestNGDataProvider.class)
	public static void main (
		final String[] args)
		throws Exception
	{
		CreditAnalytics.Init ("");

		BoundedUniform uniformRandom = new BoundedUniform (
			0.,
			1.
		);

		R1Lebesgue uniformDistribution = new R1Lebesgue (
			0.,
			1.
		);

		int[] aiSampleSize = new int[] {
			50, 500, 5000, 50000, 500000, 5000000, 50000000
		};

		double[] adblTolerance = new double[] {
			0.01, 0.03, 0.05, 0.07, 0.10
		};

		Head ("\t|         WEAK LAW OF LARGE NUMBERS     -      METRICS FROM UNDERLYING GENERATOR        |");

		WeakLawBounds (
			uniformRandom,
			uniformDistribution,
			aiSampleSize,
			adblTolerance
		);

		System.out.println ("\t|---------------------------------------------------------------------------------------|");

		Head ("\t|        WEAK LAW OF LARGE NUMBERS      -     METRICS FROM EMPIRICAL DISTRIBUTION       |");

		WeakLawBounds (
			uniformRandom,
			null,
			aiSampleSize,
			adblTolerance
		);

		System.out.println ("\t|---------------------------------------------------------------------------------------|");

		Head ("\t|         CHERNOFF-HOEFFDING BOUNDS      -     METRICS FROM UNDERLYING GENERATOR        |");

		ChernoffHoeffdingBounds (
			uniformRandom,
			uniformDistribution,
			uniformRandom.upperBound() - uniformRandom.lowerBound(),
			aiSampleSize,
			adblTolerance
		);

		System.out.println ("\t|---------------------------------------------------------------------------------------|");

		Head ("\t|         CHERNOFF-HOEFFDING BOUNDS      -     METRICS FROM EMPIRICAL DISTRIBUTION      |");

		ChernoffHoeffdingBounds (
			uniformRandom,
			null,
			uniformRandom.upperBound() - uniformRandom.lowerBound(),
			aiSampleSize,
			adblTolerance
		);

		System.out.println ("\t|---------------------------------------------------------------------------------------|");

		Head ("\t|              BENNETT BOUNDS      -      METRICS FROM UNDERLYING GENERATOR             |");

		BennettBounds (
			uniformRandom,
			uniformDistribution,
			uniformRandom.upperBound() - uniformRandom.lowerBound(),
			aiSampleSize,
			adblTolerance
		);

		System.out.println ("\t|---------------------------------------------------------------------------------------|");

		Head ("\t|              BENNETT BOUNDS      -      METRICS FROM EMPIRICAL DISTRIBUTION           |");

		BennettBounds (
			uniformRandom,
			null,
			uniformRandom.upperBound() - uniformRandom.lowerBound(),
			aiSampleSize,
			adblTolerance
		);

		System.out.println ("\t|---------------------------------------------------------------------------------------|");

		Head ("\t|             BERNSTEIN BOUNDS      -      METRICS FROM UNDERLYING GENERATOR            |");

		BernsteinBounds (
			uniformRandom,
			uniformDistribution,
			uniformRandom.upperBound() - uniformRandom.lowerBound(),
			aiSampleSize,
			adblTolerance
		);

		System.out.println ("\t|---------------------------------------------------------------------------------------|");

		Head ("\t|            BERNSTEIN BOUNDS      -      METRICS FROM EMPIRICAL DISTRIBUTION           |");

		BernsteinBounds (
			uniformRandom,
			uniformDistribution,
			uniformRandom.upperBound() - uniformRandom.lowerBound(),
			aiSampleSize,
			adblTolerance
		);

		System.out.println ("\t|---------------------------------------------------------------------------------------|");
	}
}
