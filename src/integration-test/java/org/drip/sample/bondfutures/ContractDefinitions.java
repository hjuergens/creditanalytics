
package org.drip.sample.bondfutures;

import org.drip.market.exchange.BondFuturesConventionContainer;
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
 * ContractDefinitions contains all the pre-fixed Definitions of the Bond Futures Contracts.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ContractDefinitions {

	private static final void DisplayBondFuturesInfo (
		String strCurrency,
		String strUnderlierType,
		String strUnderlierSubtype,
		String strMaturityTenor)
	{
		System.out.println ("--------------------------------------------------------------------------------------------------------\n");

		System.out.println ("\t" +
			BondFuturesConventionContainer.FromJurisdictionTypeMaturity (
				strCurrency,
				strUnderlierType,
				strUnderlierSubtype,
				strMaturityTenor
			)
		);
	}

	@Test(dataProvider = "mainparam", dataProviderClass = org.drip.sample.TestNGDataProvider.class)
	public static void main (
		final String[] args)
	{
		CreditAnalytics.Init ("");

		System.out.println();

		DisplayBondFuturesInfo ("AUD", "BANK", "BILLS", "3M");

		DisplayBondFuturesInfo ("AUD", "TREASURY", "BOND", "3Y");

		DisplayBondFuturesInfo ("AUD", "TREASURY", "BOND", "10Y");

		DisplayBondFuturesInfo ("EUR", "EURO", "SCHATZ", "2Y");

		DisplayBondFuturesInfo ("EUR", "EURO", "BOBL", "5Y");

		DisplayBondFuturesInfo ("EUR", "EURO", "BUND", "10Y");

		DisplayBondFuturesInfo ("EUR", "EURO", "BUXL", "30Y");

		DisplayBondFuturesInfo ("EUR", "TREASURY", "BONO", "10Y");

		DisplayBondFuturesInfo ("GBP", "SHORT", "GILT", "2Y");

		DisplayBondFuturesInfo ("GBP", "MEDIUM", "GILT", "5Y");

		DisplayBondFuturesInfo ("GBP", "LONG", "GILT", "10Y");

		DisplayBondFuturesInfo ("JPY", "TREASURY", "JGB", "5Y");

		DisplayBondFuturesInfo ("JPY", "TREASURY", "JGB", "10Y");

		DisplayBondFuturesInfo ("USD", "TREASURY", "NOTE", "2Y");

		DisplayBondFuturesInfo ("USD", "TREASURY", "NOTE", "3Y");

		DisplayBondFuturesInfo ("USD", "TREASURY", "NOTE", "5Y");

		DisplayBondFuturesInfo ("USD", "TREASURY", "NOTE", "10Y");

		DisplayBondFuturesInfo ("USD", "TREASURY", "BOND", "30Y");

		DisplayBondFuturesInfo ("USD", "TREASURY", "BOND", "ULTRA");

		System.out.println ("--------------------------------------------------------------------------------------------------------\n");
	}
}
