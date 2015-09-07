
package org.drip.market.exchange;

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
 * FuturesOptionsContainer holds the short term futures options contracts.
 *
 * @author Lakshmi Krishnamurthy
 */

public class FuturesOptionsContainer {
	private static final java.util.Map<String, org.drip.market.exchange.FuturesOptions>
		_mapFuturesOptions = new
			java.util.TreeMap<String, org.drip.market.exchange.FuturesOptions>();

	/**
	 * Initialize the Overnight Index Container with the Overnight Indexes
	 * 
	 * @return TRUE => The Overnight Index Container successfully initialized with the indexes
	 */

	public static final boolean Init()
	{
		try {
			org.drip.product.params.LastTradingDateSetting ltdsMidCurveQuarterly = new
				org.drip.product.params.LastTradingDateSetting
					(org.drip.product.params.LastTradingDateSetting.MID_CURVE_OPTION_QUARTERLY, "",
						java.lang.Double.NaN);

			org.drip.product.params.LastTradingDateSetting ltdsMidCurve1M = new
				org.drip.product.params.LastTradingDateSetting
					(org.drip.product.params.LastTradingDateSetting.MID_CURVE_OPTION, "1M",
						java.lang.Double.NaN);

			org.drip.product.params.LastTradingDateSetting ltdsMidCurve2M = new
				org.drip.product.params.LastTradingDateSetting
					(org.drip.product.params.LastTradingDateSetting.MID_CURVE_OPTION, "2M",
						java.lang.Double.NaN);

			org.drip.product.params.LastTradingDateSetting ltdsMidCurve1Y = new
				org.drip.product.params.LastTradingDateSetting
					(org.drip.product.params.LastTradingDateSetting.MID_CURVE_OPTION, "1Y",
						java.lang.Double.NaN);

			org.drip.product.params.LastTradingDateSetting ltdsMidCurve2Y = new
				org.drip.product.params.LastTradingDateSetting
					(org.drip.product.params.LastTradingDateSetting.MID_CURVE_OPTION, "2Y",
						java.lang.Double.NaN);

			org.drip.product.params.LastTradingDateSetting[] s_aLTDSMidCurveAll = new
				org.drip.product.params.LastTradingDateSetting[] {ltdsMidCurveQuarterly, ltdsMidCurve1M,
					ltdsMidCurve2M, ltdsMidCurve1Y, ltdsMidCurve2Y, new
						org.drip.product.params.LastTradingDateSetting
							(org.drip.product.params.LastTradingDateSetting.MID_CURVE_OPTION, "4Y",
								java.lang.Double.NaN)};

			org.drip.market.exchange.FuturesOptions foCHFLIBOR3M_MARGIN = new
				org.drip.market.exchange.FuturesOptions ("CHF-LIBOR-3M", "MARGIN");

			foCHFLIBOR3M_MARGIN.setLDTS ("LIFFE", new org.drip.product.params.LastTradingDateSetting[]
				{ltdsMidCurveQuarterly});

			_mapFuturesOptions.put ("CHF-LIBOR-3M|MARGIN", foCHFLIBOR3M_MARGIN);

			org.drip.market.exchange.FuturesOptions foGBPLIBOR3M_MARGIN = new
				org.drip.market.exchange.FuturesOptions ("GBP-LIBOR-3M", "MARGIN");

			foGBPLIBOR3M_MARGIN.setLDTS ("LIFFE", new org.drip.product.params.LastTradingDateSetting[]
				{ltdsMidCurveQuarterly, ltdsMidCurve1M, ltdsMidCurve2M, ltdsMidCurve2Y});

			_mapFuturesOptions.put ("GBP-LIBOR-3M|MARGIN", foGBPLIBOR3M_MARGIN);

			org.drip.market.exchange.FuturesOptions foEUREURIBOR3M_MARGIN = new
				org.drip.market.exchange.FuturesOptions ("EUR-EURIBOR-3M", "MARGIN");

			foEUREURIBOR3M_MARGIN.setLDTS ("EUREX", new org.drip.product.params.LastTradingDateSetting[]
				{ltdsMidCurveQuarterly, ltdsMidCurve1Y});

			foEUREURIBOR3M_MARGIN.setLDTS ("LIFFE", new org.drip.product.params.LastTradingDateSetting[]
				{ltdsMidCurveQuarterly, ltdsMidCurve1M, ltdsMidCurve2M, ltdsMidCurve2Y});

			_mapFuturesOptions.put ("EUR-EURIBOR-3M|MARGIN", foEUREURIBOR3M_MARGIN);

			org.drip.market.exchange.FuturesOptions foJPYLIBOR3M_PREMIUM = new
				org.drip.market.exchange.FuturesOptions ("JPY-LIBOR-3M", "PREMIUM");

			foJPYLIBOR3M_PREMIUM.setLDTS ("SGX", s_aLTDSMidCurveAll);

			_mapFuturesOptions.put ("JPY-LIBOR-3M|PREMIUM", foJPYLIBOR3M_PREMIUM);

			org.drip.market.exchange.FuturesOptions foJPYTIBOR3M_PREMIUM = new
				org.drip.market.exchange.FuturesOptions ("JPY-TIBOR-3M", "PREMIUM");

			foJPYTIBOR3M_PREMIUM.setLDTS ("SGX", s_aLTDSMidCurveAll);

			_mapFuturesOptions.put ("JPY-TIBOR-3M|PREMIUM", foJPYTIBOR3M_PREMIUM);

			org.drip.market.exchange.FuturesOptions foUSDLIBOR1M_PREMIUM = new
				org.drip.market.exchange.FuturesOptions ("USD-LIBOR-1M", "PREMIUM");

			foUSDLIBOR1M_PREMIUM.setLDTS ("CME", s_aLTDSMidCurveAll);

			_mapFuturesOptions.put ("USD-LIBOR-1M|PREMIUM", foUSDLIBOR1M_PREMIUM);

			org.drip.market.exchange.FuturesOptions foUSDLIBOR3M_MARGIN = new
				org.drip.market.exchange.FuturesOptions ("USD-LIBOR-3M", "MARGIN");

			foUSDLIBOR3M_MARGIN.setLDTS ("LIFFE", new org.drip.product.params.LastTradingDateSetting[]
				{ltdsMidCurveQuarterly, ltdsMidCurve1M, ltdsMidCurve2M});

			_mapFuturesOptions.put ("USD-LIBOR-3M|MARGIN", foUSDLIBOR3M_MARGIN);

			org.drip.market.exchange.FuturesOptions foUSDLIBOR3M_PREMIUM = new
				org.drip.market.exchange.FuturesOptions ("USD-LIBOR-3M", "PREMIUM");

			foUSDLIBOR3M_PREMIUM.setLDTS ("CME", s_aLTDSMidCurveAll);

			foUSDLIBOR3M_PREMIUM.setLDTS ("SGX", s_aLTDSMidCurveAll);

			_mapFuturesOptions.put ("USD-LIBOR-3M|PREMIUM", foUSDLIBOR3M_PREMIUM);
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}

	/**
	 * Retrieve the FuturesOptions Exchange Info
	 * 
	 * @param strFullyQualifiedName Fully Qualified Name
	 * @param strTradingMode Trading Mode
	 * 
	 * @return The FuturesOptions Exchange Info
	 */

	public static final org.drip.market.exchange.FuturesOptions ExchangeInfo (
		final String strFullyQualifiedName,
		final String strTradingMode)
	{
		if (null == strFullyQualifiedName || strFullyQualifiedName.isEmpty() || null == strTradingMode ||
			strTradingMode.isEmpty() || !_mapFuturesOptions.containsKey (strFullyQualifiedName + "|" +
				strTradingMode))
			return null;

		String strFuturesOptionsKey = strFullyQualifiedName + "|" + strTradingMode;

		return !_mapFuturesOptions.containsKey (strFuturesOptionsKey) ? null : _mapFuturesOptions.get
			(strFuturesOptionsKey);
	}
}
