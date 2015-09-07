
package org.drip.sample.cross;

import java.util.*;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.*;
import org.drip.function.R1ToR1.FlatUnivariate;
import org.drip.param.creator.ScenarioForwardCurveBuilder;
import org.drip.param.market.CurveSurfaceQuoteSet;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.fx.ComponentPair;
import org.drip.product.params.*;
import org.drip.product.rates.*;
import org.drip.quant.common.*;
import org.drip.service.api.CreditAnalytics;
import org.drip.state.creator.DiscountCurveBuilder;
import org.drip.state.identifier.*;

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
 * FixFloatFixFloat demonstrates the construction, the usage, and the eventual valuation of the Cross
 *  Currency Basis Swap built out of a pair of fix-float swaps.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FixFloatFixFloat {

	private static final FixFloatComponent MakeFixFloatSwap (
		final JulianDate dtEffective,
		final boolean bFXMTM,
		final String strPayCurrency,
		final String strCouponCurrency,
		final String strMaturityTenor,
		final int iTenorInMonths)
		throws Exception
	{
		UnitCouponAccrualSetting ucasFixed = new UnitCouponAccrualSetting (
			2,
			"Act/360",
			false,
			"Act/360",
			false,
			strCouponCurrency,
			false,
			CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
		);

		ComposableFloatingUnitSetting cfusFloating = new ComposableFloatingUnitSetting (
			iTenorInMonths + "M",
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
			null,
			ForwardLabel.Create (
				strCouponCurrency,
				iTenorInMonths + "M"
			),
			CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
			0.
		);

		ComposableFixedUnitSetting cfusFixed = new ComposableFixedUnitSetting (
			"6M",
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
			null,
			0.02,
			0.,
			strCouponCurrency
		);

		CompositePeriodSetting cpsFloating = new CompositePeriodSetting (
			12 / iTenorInMonths,
			iTenorInMonths + "M",
			strPayCurrency,
			null,
			-1.,
			null,
			null,
			null,
			null
		);

		CompositePeriodSetting cpsFixed = new CompositePeriodSetting (
			2,
			"6M",
			strPayCurrency,
			null,
			1.,
			null,
			null,
			bFXMTM ? null : new FixingSetting (
				FixingSetting.FIXING_PRESET_STATIC,
				null,
				dtEffective.julian()
			),
			null
		);

		List<Double> lsFloatingStreamEdgeDate = CompositePeriodBuilder.RegularEdgeDates (
			dtEffective,
			iTenorInMonths + "M",
			strMaturityTenor,
			null
		);

		List<Double> lsFixedStreamEdgeDate = CompositePeriodBuilder.RegularEdgeDates (
			dtEffective,
			"6M",
			strMaturityTenor,
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

		FixFloatComponent fixFloat = new FixFloatComponent (
			fixedStream,
			floatingStream,
			new CashSettleParams (
				0,
				strPayCurrency,
				0
			)
		);

		return fixFloat;
	}

	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		double dblUSD3MForwardRate = 0.0275;
		double dblEUR3MForwardRate = 0.0175;
		double dblUSDFundingRate = 0.03;
		double dblUSDEURFXRate = 1. / 1.34;

		double dblUSD3MForwardVol = 0.3;
		double dblEUR3MForwardVol = 0.3;
		double dblUSDFundingVol = 0.3;
		double dblUSDEURFXVol = 0.3;

		double dblUSD3MForwardUSDFundingCorr = 0.15;
		double dblEUR3MForwardUSDFundingCorr = 0.15;
		double dblEUR3MForwardUSDEURFXCorr = 0.15;
		double dblUSDFundingUSDEURFXCorr = 0.15;

		/*
		 * Initialize the Credit Analytics Library
		 */

		CreditAnalytics.Init ("");

		JulianDate dtToday = org.drip.analytics.date.DateUtil.Today();

		ValuationParams valParams = new ValuationParams (
			dtToday,
			dtToday,
			"USD"
		);

		ForwardLabel fri3MUSD = ForwardLabel.Create (
			"USD",
			"3M"
		);

		ForwardLabel fri3MEUR = ForwardLabel.Create (
			"EUR",
			"3M"
		);

		FundingLabel fundingLabelUSD = FundingLabel.Standard ("USD");

		FXLabel fxLabel = FXLabel.Standard (CurrencyPair.FromCode ("USD/EUR"));

		FixFloatComponent fixFloatUSD = MakeFixFloatSwap (
			dtToday,
			false,
			"USD",
			"USD",
			"2Y",
			3
		);

		FixFloatComponent fixFloatEURMTM = MakeFixFloatSwap (
			dtToday,
			true,
			"USD",
			"EUR",
			"2Y",
			3
		);

		ComponentPair cpMTM = new ComponentPair (
			"FFFF_MTM",
			fixFloatUSD,
			fixFloatEURMTM,
			null
		);

		FixFloatComponent fixFloatEURNonMTM = MakeFixFloatSwap (
			dtToday,
			false,
			"USD",
			"EUR",
			"2Y",
			3
		);

		ComponentPair cpNonMTM = new ComponentPair (
			"FFFF_Non_MTM",
			fixFloatUSD,
			fixFloatEURNonMTM,
			null
		);

		CurveSurfaceQuoteSet mktParams = new CurveSurfaceQuoteSet();

		mktParams.setFixing (
			dtToday,
			fxLabel,
			dblUSDEURFXRate
		);

		mktParams.setForwardCurve (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
				dtToday,
				fri3MUSD,
				dblUSD3MForwardRate,
				new CollateralizationParams (
					"OVERNIGHT_INDEX",
					"USD"
				)
			)
		);

		mktParams.setForwardCurve (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
				dtToday,
				fri3MEUR,
				dblEUR3MForwardRate,
				new CollateralizationParams (
					"OVERNIGHT_INDEX",
					"EUR"
				)
			)
		);

		mktParams.setFundingCurve (
			DiscountCurveBuilder.CreateFromFlatRate (
				dtToday,
				"USD",
				new CollateralizationParams (
					"OVERNIGHT_INDEX",
					"USD"
				),
				dblUSDFundingRate
			)
		);

		mktParams.setFXCurve (
			fxLabel,
			new FlatUnivariate (dblUSDEURFXRate)
		);

		mktParams.setForwardCurveVolSurface (
			fri3MUSD,
			new FlatUnivariate (dblUSD3MForwardVol)
		);

		mktParams.setForwardCurveVolSurface (
			fri3MEUR,
			new FlatUnivariate (dblEUR3MForwardVol)
		);

		mktParams.setFundingCurveVolSurface (
			fundingLabelUSD,
			new FlatUnivariate (dblUSDFundingVol)
		);

		mktParams.setFXCurveVolSurface (
			fxLabel,
			new FlatUnivariate (dblUSDEURFXVol)
		);

		mktParams.setForwardFundingCorrSurface (
			fri3MUSD,
			fundingLabelUSD,
			new FlatUnivariate (dblUSD3MForwardUSDFundingCorr)
		);

		mktParams.setForwardFundingCorrSurface (
			fri3MEUR,
			fundingLabelUSD,
			new FlatUnivariate (dblEUR3MForwardUSDFundingCorr)
		);

		mktParams.setForwardFXCorrSurface (
			fri3MEUR,
			fxLabel,
			new FlatUnivariate (dblEUR3MForwardUSDEURFXCorr)
		);

		mktParams.setFundingFXCorrSurface (
			fundingLabelUSD,
			fxLabel,
			new FlatUnivariate (dblUSDFundingUSDEURFXCorr)
		);

		CaseInsensitiveTreeMap<Double> mapMTMOutput = cpMTM.value (
			valParams,
			null,
			mktParams,
			null
		);

		CaseInsensitiveTreeMap<Double> mapNonMTMOutput = cpNonMTM.value (
			valParams,
			null,
			mktParams,
			null
		);

		for (Map.Entry<String, Double> me : mapMTMOutput.entrySet()) {
			String strKey = me.getKey();

			if (null != me.getValue() && null != mapNonMTMOutput.get (strKey)) {
				double dblMTMMeasure = me.getValue();

				double dblNonMTMMeasure = mapNonMTMOutput.get (strKey);

				String strReconcile = NumberUtil.WithinTolerance (
					dblMTMMeasure,
					dblNonMTMMeasure,
					1.e-08,
					1.e-04
				) ? "RECONCILES" : "DOES NOT RECONCILE";

				System.out.println ("\t" +
					FormatUtil.FormatDouble (dblMTMMeasure, 1, 8, 1.) + " | " +
					FormatUtil.FormatDouble (dblNonMTMMeasure, 1, 8, 1.) + " | " +
					strReconcile + " <= " + strKey);
			}
		}
	}
}
