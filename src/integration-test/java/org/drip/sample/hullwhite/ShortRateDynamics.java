
package org.drip.sample.hullwhite;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.dynamics.hullwhite.SingleFactorStateEvolver;
import org.drip.function.R1ToR1.FlatUnivariate;
import org.drip.quant.common.FormatUtil;
import org.drip.sequence.random.BoxMullerGaussian;
import org.drip.service.api.CreditAnalytics;
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
 * ShortRateDynamics demonstrates the Construction and Usage of the Hull-White 1F Model Dynamics for the
 *  Evolution of the Short Rate.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ShortRateDynamics {

	private static final SingleFactorStateEvolver HullWhiteEvolver (
		final String strCurrency,
		final double dblSigma,
		final double dblA,
		final double dblStartingForwardRate)
		throws Exception
	{
		return new SingleFactorStateEvolver (
			FundingLabel.Standard (strCurrency),
			dblSigma,
			dblA,
			new FlatUnivariate (dblStartingForwardRate),
			new BoxMullerGaussian (
				0.,
				1.
			)
		);
	}

	private static final void ShortRateEvolution (
		final SingleFactorStateEvolver hw,
		final JulianDate dtSpot,
		final String strCurrency,
		final String strViewTenor,
		final double dblStartingShortRate)
		throws Exception
	{
		int iDayStep = 2;
		JulianDate dtView = dtSpot;
		double dblShortRate = dblStartingShortRate;

		double dblSpotDate = dtSpot.julian();

		double dblEndDate = dtSpot.addTenor (strViewTenor).julian();

		System.out.println ("\n\n\t|------------------------------------------------------||");

		System.out.println ("\t|                                                      ||");

		System.out.println ("\t|    Hull-White Evolution Run                          ||");

		System.out.println ("\t|    ------------------------                          ||");

		System.out.println ("\t|                                                      ||");

		System.out.println ("\t|    L->R:                                             ||");

		System.out.println ("\t|        Date                                          ||");

		System.out.println ("\t|        Short Rate (%)                                ||");

		System.out.println ("\t|        Short Rate - Change (%)                       ||");

		System.out.println ("\t|        Alpha (%)                                     ||");

		System.out.println ("\t|        Theta (%)                                     ||");

		System.out.println ("\t|------------------------------------------------------||");

		while (dtView.julian() < dblEndDate) {
			double dblViewDate = dtView.julian();

			double dblAlpha = hw.alpha (
				dblSpotDate,
				dblViewDate
			);

			double dblTheta = hw.theta (
				dblSpotDate,
				dblViewDate
			);

			double dblShortRateIncrement = hw.shortRateIncrement (
				dblSpotDate,
				dblViewDate,
				dblShortRate,
				iDayStep / 365.25
			);

			dblShortRate += dblShortRateIncrement;

			System.out.println ("\t| [" + dtView + "] = " +
				FormatUtil.FormatDouble (dblShortRate, 1, 2, 100.) + "% | " +
				FormatUtil.FormatDouble (dblShortRateIncrement, 1, 2, 100.) + "% | " +
				FormatUtil.FormatDouble (dblAlpha, 1, 4, 100.) + "% | " +
				FormatUtil.FormatDouble (dblTheta, 1, 4, 100.) + "% || "
			);

			dtView = dtView.addBusDays (
				iDayStep,
				strCurrency
			);
		}

		System.out.println ("\t|------------------------------------------------------||");
	}

	@Test(dataProvider = "mainparam", dataProviderClass = org.drip.sample.TestNGDataProvider.class)
	public static void main (
		final String[] astrArgs)
		throws Exception
	{
		CreditAnalytics.Init ("");

		JulianDate dtSpot = DateUtil.Today();

		String strCurrency = "USD";
		double dblStartingShortRate = 0.05;
		double dblSigma = 0.05;
		double dblA = 1.;

		SingleFactorStateEvolver hw = HullWhiteEvolver (
			strCurrency,
			dblSigma,
			dblA,
			dblStartingShortRate
		);

		ShortRateEvolution (
			hw,
			dtSpot,
			strCurrency,
			"4M",
			dblStartingShortRate
		);
	}
}
