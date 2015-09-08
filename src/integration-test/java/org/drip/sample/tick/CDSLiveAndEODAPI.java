
package org.drip.sample.tick;

/*
 * Generic imports
 */

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.analytics.definition.CreditCurve;
import org.drip.analytics.support.CaseInsensitiveTreeMap;
import org.drip.product.creator.CDSBuilder;
import org.drip.product.definition.CalibratableFixedIncomeComponent;
import org.drip.product.definition.CreditDefaultSwap;
import org.drip.service.api.CreditAnalytics;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Set;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
 * Copyright (C) 2012 Lakshmi Krishnamurthy
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
 * CDSLiveAndEODAPI is a fairly comprehensive org.drip.sample demo'ing the usage of the EOD and Live CDS Curve API
 * 	functions. It demonstrates the following:
 * 	- Retrieves all the CDS curves available for the given EOD.
 * 	- Retrieves the calibrated credit curve from the CDS instruments for the given CDS curve name, IR curve
 * 		name, and EOD. Also shows the 10Y survival probability and hazard rate.
 * 	- Displays the CDS quotes used to construct the closing credit curve.
 * 	- Loads all available credit curves for the given curve ID built from CDS instruments between 2 dates and
 * 		displays the corresponding 5Y quote.
 * 	- Calculate and display the EOD CDS measures for a spot starting CDS based off of a specific credit
 * 		curve.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CDSLiveAndEODAPI {

	/*
	 * Sample API demonstrating the creation/usage of the credit curve API
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final void CreditCurveEODAPISample()
		throws Exception
	{
		JulianDate dt = DateUtil.CreateFromYMD (2011, 7, 21);

		/*
		 * Retrieves all the CDS curves available for the given EOD
		 */

		Set<String> setstrCDSCurves = CreditAnalytics.GetEODCDSCurveNames (dt);

		for (String strCDSCurveName : setstrCDSCurves)
			System.out.println (strCDSCurveName);

		/*
		 * Retrieves the calibrated credit curve from the CDS instruments for the given CDS curve name,
		 * 		IR curve name, and EOD. Also shows the 10Y survival probability and hazard rate.
		 */

		CreditCurve ccEOD = CreditAnalytics.LoadEODCDSCreditCurve ("813796", "USD", dt);

		JulianDate dt10Y = DateUtil.Today().addYears (10);

		System.out.println ("CCFromEOD[" + dt10Y.toString() + "]; Survival=" + ccEOD.survival ("10Y") +
			"; Hazard=" + ccEOD.hazard ("10Y"));

		/*
		 * Displays the CDS quotes used to construct the closing credit curve
		 */

		CalibratableFixedIncomeComponent[] aCompCDS = ccEOD.calibComp();

		for (int i = 0; i < aCompCDS.length; ++i)
			System.out.println (aCompCDS[i].primaryCode() + " => " + (ccEOD.manifestMeasure
				(aCompCDS[i].primaryCode()).get ("FairPremium")));

		/*
		 * Loads all available credit curves for the given curve ID built from CDS instruments between 2 dates
		 */

		Map<JulianDate, CreditCurve> mapCC = CreditAnalytics.LoadEODCDSCreditCurves ("813796", "USD",
			DateUtil.CreateFromYMD (2011, 7, 14), dt);

		/*
		 * Displays their 5Y CDS quote
		 */

		for (Map.Entry<JulianDate, CreditCurve> meCC : mapCC.entrySet()) {
			JulianDate dtME = meCC.getKey();

			CreditCurve ccCOB = meCC.getValue();

			System.out.println (dtME + "[CDS.5Y] => " + (ccCOB.manifestMeasure ("CDS.5Y").get ("Rate")));
		}
	}

	/*
	 * Sample demonstrating the calculation of the CDS EOD measures from price
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final void CDSEODMeasuresAPISample()
	{
		JulianDate dtEOD = DateUtil.CreateFromYMD (2011, 7, 21); // EOD

		/*
		 * Create a spot starting CDS based off of a specific credit curve
		 */

		CreditDefaultSwap cds = CDSBuilder.CreateSNAC (DateUtil.Today(), "5Y", 0.1, "813796");

		/*
		 * Calculate the EOD CDS measures
		 */

		CaseInsensitiveTreeMap<Double> mapEODCDSMeasures = CreditAnalytics.GetEODCDSMeasures (cds, dtEOD);

		/*
		 * Display the EOD CDS measures
		 */

		for (Map.Entry<String, Double> me : mapEODCDSMeasures.entrySet())
			System.out.println (me.getKey() + " => " + me.getValue());
	}

	@Test(dataProvider = "mainparam", dataProviderClass = org.drip.sample.TestNGDataProvider.class)
	public static void main (
		final String astrArgs[])
		throws Exception
	{
		// String strConfig = "c:\\Lakshmi\\BondAnal\\Config.xml";

		String strConfig = "";

		if (!CreditAnalytics.Init (strConfig)) {
			System.out.println ("Cannot fully init FI!");

			System.exit (305);
		}

		CreditCurveEODAPISample();

		CDSEODMeasuresAPISample();
	}
}
