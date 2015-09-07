
package org.drip.sample.collateral;

import java.util.Map;

import org.drip.analytics.date.*;
import org.drip.analytics.rates.DiscountCurve;
import org.drip.analytics.support.CaseInsensitiveTreeMap;
import org.drip.function.R1ToR1.*;
import org.drip.function.definition.R1ToR1;
import org.drip.param.creator.MarketParamsBuilder;
import org.drip.param.market.CurveSurfaceQuoteSet;
import org.drip.param.valuation.*;
import org.drip.product.fx.DomesticCollateralizedForeignForward;
import org.drip.product.params.CurrencyPair;
import org.drip.service.api.CreditAnalytics;
import org.drip.state.creator.DiscountCurveBuilder;
import org.drip.state.curve.ForeignCollateralizedDiscountCurve;
import org.drip.state.identifier.FXLabel;

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
 * DomesticCollateralForeignForex demonstrates the construction and the usage of Domestic Currency
 * 	Collateralized Foreign Pay-out FX forward product, and the generation of its measures.
 *  
 * @author Lakshmi Krishnamurthy
 */

public class DomesticCollateralForeignForex {
	public static final void main (
		final String[] astrArgs)
		throws Exception
	{
		/*
		 * Initialize the Credit Analytics Library
		 */

		CreditAnalytics.Init ("");

		JulianDate dtToday = DateUtil.Today();

		String strDomesticCurrency = "USD";
		String strForeignCurrency = "EUR";
		String strMaturity = "1Y";
		double dblFXFwdStrike = 0.984;
		double dblForeignCollateralRate = 0.02;
		double dblCollateralizedFXRate = 0.01;
		double dblForeignRatesVolatility = 0.30;
		double dblFXVolatility = 0.10;
		double dblFXForeignRatesCorrelation = 0.20;

		CurrencyPair cp = CurrencyPair.FromCode (strForeignCurrency + "/" + strDomesticCurrency);

		DiscountCurve dcCcyDomesticCollatDomestic = DiscountCurveBuilder.CreateFromFlatRate (
			dtToday,
			strForeignCurrency,
			new CollateralizationParams (
				"OVERNIGHT_INDEX",
				strForeignCurrency
			),
			dblForeignCollateralRate
		);

		R1ToR1 auFX = new ExponentialDecay (
			dtToday.julian(),
			dblCollateralizedFXRate / 365.25
		);

		DiscountCurve dcCcyForeignCollatDomestic = new ForeignCollateralizedDiscountCurve (
			strForeignCurrency,
			dcCcyDomesticCollatDomestic,
			auFX,
			new FlatUnivariate (dblForeignRatesVolatility),
			new FlatUnivariate (dblFXVolatility),
			new FlatUnivariate (dblFXForeignRatesCorrelation)
		);

		CurveSurfaceQuoteSet mktParams = MarketParamsBuilder.Create (
			null,
			null,
			null,
			null,
			null,
			null,
			null
		);

		mktParams.setPayCurrencyCollateralCurrencyCurve (
			strForeignCurrency,
			strDomesticCurrency,
			dcCcyForeignCollatDomestic
		);

		mktParams.setPayCurrencyCollateralCurrencyCurve (
			strDomesticCurrency,
			strDomesticCurrency,
			dcCcyDomesticCollatDomestic
		);

		mktParams.setFXCurve (
			FXLabel.Standard (cp),
			auFX
		);

		DomesticCollateralizedForeignForward dcff = new DomesticCollateralizedForeignForward (
			cp,
			dblFXFwdStrike,
			dtToday.addTenor (strMaturity)
		);

		CaseInsensitiveTreeMap<Double> mapDCFF = dcff.value (
			new ValuationParams (
				dtToday,
				dtToday,
				strDomesticCurrency
			),
			null,
			mktParams,
			null
		);

		for (Map.Entry<String, Double> me : mapDCFF.entrySet())
			System.out.println ("\t" + me.getKey() + " => " + me.getValue());
	}
}
