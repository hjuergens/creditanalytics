
package org.drip.sample.cross;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.CaseInsensitiveTreeMap;
import org.drip.analytics.support.CompositePeriodBuilder;
import org.drip.function.R1ToR1.FlatUnivariate;
import org.drip.param.creator.ScenarioForwardCurveBuilder;
import org.drip.param.market.CurveSurfaceQuoteSet;
import org.drip.param.period.ComposableFloatingUnitSetting;
import org.drip.param.period.CompositePeriodSetting;
import org.drip.param.period.FixingSetting;
import org.drip.param.valuation.CashSettleParams;
import org.drip.param.valuation.CollateralizationParams;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.fx.ComponentPair;
import org.drip.product.params.CurrencyPair;
import org.drip.product.rates.FloatFloatComponent;
import org.drip.product.rates.Stream;
import org.drip.quant.common.FormatUtil;
import org.drip.quant.common.NumberUtil;
import org.drip.service.api.CreditAnalytics;
import org.drip.state.creator.DiscountCurveBuilder;
import org.drip.state.identifier.FXLabel;
import org.drip.state.identifier.ForwardLabel;
import org.drip.state.identifier.FundingLabel;
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
 * FloatFloatFloatFloat demonstrates the construction, the usage, and the eventual valuation of the Cross
 *  Currency Basis Swap built out of a pair of float-float swaps.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FloatFloatFloatFloat {

	private static final FloatFloatComponent MakeFloatFloatSwap (
		final JulianDate dtEffective,
		final boolean bFXMTM,
		final String strPayCurrency,
		final String strCouponCurrency,
		final String strMaturityTenor,
		final int iTenorInMonthsReference,
		final int iTenorInMonthsDerived)
		throws Exception
	{
		ComposableFloatingUnitSetting cfusReference = new ComposableFloatingUnitSetting (
			iTenorInMonthsReference + "M",
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
			null,
			ForwardLabel.Create (
				strCouponCurrency,
				iTenorInMonthsReference + "M"
			),
			CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
			0.
		);

		ComposableFloatingUnitSetting cfusDerived = new ComposableFloatingUnitSetting (
			iTenorInMonthsDerived + "M",
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
			null,
			ForwardLabel.Create (
				strCouponCurrency,
				iTenorInMonthsDerived + "M"
			),
			CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
			0.
		);

		CompositePeriodSetting cpsReference = new CompositePeriodSetting (
			12 / iTenorInMonthsReference,
			iTenorInMonthsReference + "M",
			strPayCurrency,
			null,
			-1.,
			null,
			null,
			bFXMTM ? null : new FixingSetting (
				FixingSetting.FIXING_PRESET_STATIC,
				null,
				dtEffective.julian()
			),
			null
		);

		CompositePeriodSetting cpsDerived = new CompositePeriodSetting (
			12 / iTenorInMonthsDerived,
			iTenorInMonthsDerived + "M",
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

		List<Double> lsReferenceStreamEdgeDate = CompositePeriodBuilder.RegularEdgeDates (
			dtEffective,
			iTenorInMonthsReference + "M",
			strMaturityTenor,
			null
		);

		List<Double> lsDerivedStreamEdgeDate = CompositePeriodBuilder.RegularEdgeDates (
			dtEffective,
			iTenorInMonthsDerived + "M",
			strMaturityTenor,
			null
		);

		Stream referenceStream = new Stream (
			CompositePeriodBuilder.FloatingCompositeUnit (
				lsReferenceStreamEdgeDate,
				cpsReference,
				cfusReference
			)
		);

		Stream derivedStream = new Stream (
			CompositePeriodBuilder.FloatingCompositeUnit (
				lsDerivedStreamEdgeDate,
				cpsDerived,
				cfusDerived
			)
		);

		CashSettleParams csp = new CashSettleParams (
			0,
			strPayCurrency,
			0
		);

		return new FloatFloatComponent (
			referenceStream,
			derivedStream,
			csp
		);
	}

	@Test(dataProvider = "mainparam", dataProviderClass = org.drip.sample.TestNGDataProvider.class)
	public static void main (
		final String[] astrArgs)
		throws Exception
	{
		String strReferenceCurrency = "USD";
		String strDerivedCurrency = "EUR";

		double dblReference3MForwardRate = 0.00750;
		double dblReference6MForwardRate = 0.01000;
		double dblDerived3MForwardRate = 0.00375;
		double dblDerived6MForwardRate = 0.00625;
		double dblReferenceFundingRate = 0.02;
		double dblReferenceDerivedFXRate = 1. / 1.28;

		double dblReference3MForwardVol = 0.3;
		double dblReference6MForwardVol = 0.3;
		double dblDerived3MForwardVol = 0.3;
		double dblDerived6MForwardVol = 0.3;
		double dblReferenceFundingVol = 0.3;
		double dblReferenceDerivedFXVol = 0.3;

		double dblReference3MForwardFundingCorr = 0.15;
		double dblReference6MForwardFundingCorr = 0.15;
		double dblDerived3MForwardFundingCorr = 0.15;
		double dblDerived6MForwardFundingCorr = 0.15;

		double dblReference3MForwardFXCorr = 0.15;
		double dblReference6MForwardFXCorr = 0.15;
		double dblDerived3MForwardFXCorr = 0.15;
		double dblDerived6MForwardFXCorr = 0.15;

		double dblFundingFXCorr = 0.15;

		/*
		 * Initialize the Credit Analytics Library
		 */

		CreditAnalytics.Init ("");

		JulianDate dtToday = org.drip.analytics.date.DateUtil.Today();

		ValuationParams valParams = new ValuationParams (
			dtToday,
			dtToday,
			strReferenceCurrency
		);

		ForwardLabel fri3MReference = ForwardLabel.Create (
			strReferenceCurrency,
			"3M"
		);

		ForwardLabel fri6MReference = ForwardLabel.Create (
			strReferenceCurrency,
			"6M"
		);

		ForwardLabel fri3MDerived = ForwardLabel.Create (
			strDerivedCurrency,
			"3M"
		);

		ForwardLabel fri6MDerived = ForwardLabel.Create (
			strDerivedCurrency,
			"6M"
		);

		FundingLabel fundingLabelReference = FundingLabel.Standard (strReferenceCurrency);

		FXLabel fxLabel = FXLabel.Standard (CurrencyPair.FromCode (strReferenceCurrency + "/" + strDerivedCurrency));

		FloatFloatComponent floatFloatReference = MakeFloatFloatSwap (
			dtToday,
			false,
			strReferenceCurrency,
			strReferenceCurrency,
			"2Y",
			6,
			3
		);

		floatFloatReference.setPrimaryCode (
			"FLOAT::FLOAT::" + strReferenceCurrency + "::" + strReferenceCurrency + "_3M::" + strReferenceCurrency + "_6M::2Y"
		);

		FloatFloatComponent floatFloatDerivedMTM = MakeFloatFloatSwap (
			dtToday,
			true,
			strReferenceCurrency,
			strDerivedCurrency,
			"2Y",
			6,
			3
		);

		floatFloatDerivedMTM.setPrimaryCode (
			"FLOAT::FLOAT::MTM::" + strReferenceCurrency + "::" + strDerivedCurrency + "_3M::" + strDerivedCurrency + "_6M::2Y"
		);

		ComponentPair cpMTM = new ComponentPair (
			"FFFF_MTM",
			floatFloatReference,
			floatFloatDerivedMTM,
			null
		);

		FloatFloatComponent floatFloatDerivedNonMTM = MakeFloatFloatSwap (
			dtToday,
			false,
			strReferenceCurrency,
			strDerivedCurrency,
			"2Y",
			6,
			3
		);

		floatFloatDerivedNonMTM.setPrimaryCode (
			"FLOAT::FLOAT::NONMTM::" + strReferenceCurrency + "::" + strDerivedCurrency + "_3M::" + strDerivedCurrency + "_6M::2Y"
		);

		ComponentPair cpNonMTM = new ComponentPair (
			"FFFF_NonMTM",
			floatFloatReference,
			floatFloatDerivedNonMTM,
			null
		);

		CurveSurfaceQuoteSet mktParams = new CurveSurfaceQuoteSet();

		mktParams.setFixing (
			dtToday,
			fxLabel,
			dblReferenceDerivedFXRate
		);

		mktParams.setForwardCurve (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
				dtToday,
				fri3MReference,
				dblReference3MForwardRate,
				new CollateralizationParams (
					"OVERNIGHT_INDEX",
					strReferenceCurrency
				)
			)
		);

		mktParams.setForwardCurve (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
				dtToday,
				fri6MReference,
				dblReference6MForwardRate,
				new CollateralizationParams (
					"OVERNIGHT_INDEX",
					strReferenceCurrency
				)
			)
		);

		mktParams.setForwardCurve (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
				dtToday,
				fri3MDerived,
				dblDerived3MForwardRate,
				new CollateralizationParams (
					"OVERNIGHT_INDEX",
					strReferenceCurrency
				)
			)
		);

		mktParams.setForwardCurve (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
				dtToday,
				fri6MDerived,
				dblDerived6MForwardRate,
				new CollateralizationParams (
					"OVERNIGHT_INDEX",
					strReferenceCurrency
				)
			)
		);

		mktParams.setFundingCurve (
			DiscountCurveBuilder.CreateFromFlatRate (
				dtToday,
				strReferenceCurrency,
				new CollateralizationParams (
					"OVERNIGHT_INDEX",
					strReferenceCurrency
				),
				dblReferenceFundingRate
			)
		);

		mktParams.setFXCurve (
			fxLabel,
			new FlatUnivariate (dblReferenceDerivedFXRate)
		);

		mktParams.setForwardCurveVolSurface (
			fri3MReference,
			new FlatUnivariate (dblReference3MForwardVol)
		);

		mktParams.setForwardCurveVolSurface (
			fri6MReference,
			new FlatUnivariate (dblReference6MForwardVol)
		);

		mktParams.setForwardCurveVolSurface (
			fri3MDerived,
			new FlatUnivariate (dblDerived3MForwardVol)
		);

		mktParams.setForwardCurveVolSurface (
			fri6MDerived,
			new FlatUnivariate (dblDerived6MForwardVol)
		);

		mktParams.setFundingCurveVolSurface (
			fundingLabelReference,
			new FlatUnivariate (dblReferenceFundingVol)
		);

		mktParams.setFXCurveVolSurface (
			fxLabel,
			new FlatUnivariate (dblReferenceDerivedFXVol)
		);

		mktParams.setForwardFundingCorrSurface (
			fri3MReference,
			fundingLabelReference,
			new FlatUnivariate (dblReference3MForwardFundingCorr)
		);

		mktParams.setForwardFundingCorrSurface (
			fri6MReference,
			fundingLabelReference,
			new FlatUnivariate (dblReference6MForwardFundingCorr)
		);

		mktParams.setForwardFundingCorrSurface (
			fri3MDerived,
			fundingLabelReference,
			new FlatUnivariate (dblDerived3MForwardFundingCorr)
		);

		mktParams.setForwardFundingCorrSurface (
			fri6MDerived,
			fundingLabelReference,
			new FlatUnivariate (dblDerived6MForwardFundingCorr)
		);

		mktParams.setForwardFXCorrSurface (
			fri3MReference,
			fxLabel,
			new FlatUnivariate (dblReference3MForwardFXCorr)
		);

		mktParams.setForwardFXCorrSurface (
			fri6MReference,
			fxLabel,
			new FlatUnivariate (dblReference6MForwardFXCorr)
		);

		mktParams.setForwardFXCorrSurface (
			fri3MDerived,
			fxLabel,
			new FlatUnivariate (dblDerived3MForwardFXCorr)
		);

		mktParams.setForwardFXCorrSurface (
			fri6MDerived,
			fxLabel,
			new FlatUnivariate (dblDerived6MForwardFXCorr)
		);

		mktParams.setFundingFXCorrSurface (
			fundingLabelReference,
			fxLabel,
			new FlatUnivariate (dblFundingFXCorr)
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
