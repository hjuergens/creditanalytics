
package org.drip.product.fx;

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
 * ForeignCollateralizedDomesticForward contains the Foreign Currency Collateralized Domestic Payout FX
 * 	forward product contract details.
 *  
 * @author Lakshmi Krishnamurthy
 */

public class ForeignCollateralizedDomesticForward {
	private String _strCode = "";
	private double _dblMaturity = java.lang.Double.NaN;
	private org.drip.product.params.CurrencyPair _cp = null;
	private double _dblForexForwardStrike = java.lang.Double.NaN;

	/**
	 * Create an ForeignCollateralizedDomesticForward from the currency pair, the strike, and the maturity
	 * 	dates
	 * 
	 * @param cp Currency Pair
	 * @param dblForexForwardStrike Forex Forward Strike
	 * @param dtMaturity Maturity Date
	 * 
	 * @throws java.lang.Exception Thrown if the inputs are invalid
	 */

	public ForeignCollateralizedDomesticForward (
		final org.drip.product.params.CurrencyPair cp,
		final double dblForexForwardStrike,
		final org.drip.analytics.date.JulianDate dtMaturity)
		throws java.lang.Exception
	{
		if (null == (_cp = cp) || !org.drip.quant.common.NumberUtil.IsValid (_dblForexForwardStrike =
			dblForexForwardStrike) || null == dtMaturity)
			throw new java.lang.Exception ("ForeignCollateralizedDomesticForward ctr: Invalid Inputs");

		_dblMaturity = dtMaturity.julian();
	}

	public String getPrimaryCode()
	{
		return _strCode;
	}

	public void setPrimaryCode (
		final String strCode)
	{
		_strCode = strCode;
	}

	public String[] getSecondaryCode()
	{
		String strPrimaryCode = getPrimaryCode();

		int iNumTokens = 0;
		String astrCodeTokens[] = new String[2];

		java.util.StringTokenizer stCodeTokens = new java.util.StringTokenizer (strPrimaryCode, ".");

		while (stCodeTokens.hasMoreTokens())
			astrCodeTokens[iNumTokens++] = stCodeTokens.nextToken();

		System.out.println (astrCodeTokens[0]);

		return new String[] {astrCodeTokens[0]};
	}

	public org.drip.analytics.date.JulianDate getMaturityDate()
	{
		try {
			return new org.drip.analytics.date.JulianDate (_dblMaturity);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public org.drip.product.params.CurrencyPair getCcyPair()
	{
		return _cp;
	}

	public org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> value (
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.pricer.CreditPricerParams pricerParams,
		final org.drip.param.market.CurveSurfaceQuoteSet csqs,
		final org.drip.param.valuation.ValuationCustomizationParams quotingParams)
	{
		if (null == valParams || null == csqs) return null;

		long lStart = System.nanoTime();

		double dblValueDate = valParams.valueDate();

		if (dblValueDate > _dblMaturity) return null;

		org.drip.function.definition.R1ToR1 auFX = csqs.fxCurve
			(org.drip.state.identifier.FXLabel.Standard (_cp));

		if (null == auFX) return null;

		String strForeignCurrency = _cp.numCcy();

		org.drip.analytics.rates.DiscountCurve dcForeignCollateral =
			csqs.payCurrencyCollateralCurrencyCurve (strForeignCurrency, strForeignCurrency);

		if (null == dcForeignCollateral) return null;

		org.drip.analytics.rates.DiscountCurve dcDomesticCurrencyForeignCollateral =
			csqs.payCurrencyCollateralCurrencyCurve (_cp.denomCcy(), strForeignCurrency);

		if (null == dcDomesticCurrencyForeignCollateral) return null;

		double dblPrice = java.lang.Double.NaN;
		double dblSpotFX = java.lang.Double.NaN;
		double dblParForward = java.lang.Double.NaN;
		double dblForeignCollateralDF = java.lang.Double.NaN;
		double dblDomesticCurrencyForeignCollateralDF = java.lang.Double.NaN;

		try {
			dblPrice = (dblSpotFX = auFX.evaluate (dblValueDate)) * (dblForeignCollateralDF =
				dcForeignCollateral.df (_dblMaturity)) - ((dblDomesticCurrencyForeignCollateralDF =
					dcDomesticCurrencyForeignCollateral.df (_dblMaturity)) * _dblForexForwardStrike);

			dblParForward = dblSpotFX * dblForeignCollateralDF / dblDomesticCurrencyForeignCollateralDF;
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> mapResult = new
			org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>();

		mapResult.put ("CalcTime", (System.nanoTime() - lStart) * 1.e-09);

		mapResult.put ("DomesticCurrencyForeignCollateralDF", dblDomesticCurrencyForeignCollateralDF);

		mapResult.put ("ForeignCollateralDF", dblForeignCollateralDF);

		mapResult.put ("ParForward", dblParForward);

		mapResult.put ("Price", dblPrice);

		mapResult.put ("SpotFX", dblSpotFX);

		return mapResult;
	}

	public java.util.Set<String> getMeasureNames()
	{
		java.util.Set<String> setstrMeasureNames = new java.util.TreeSet<String>();

		setstrMeasureNames.add ("CalcTime");

		setstrMeasureNames.add ("DomesticCurrencyForeignCollateralDF");

		setstrMeasureNames.add ("ForeignCollateralDF");

		setstrMeasureNames.add ("ParForward");

		setstrMeasureNames.add ("Price");

		setstrMeasureNames.add ("SpotFX");

		return setstrMeasureNames;
	}
}
