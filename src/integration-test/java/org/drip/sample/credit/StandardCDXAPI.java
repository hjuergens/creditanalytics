
package org.drip.sample.credit;

/*
 * Generic imports
 */

import java.util.*;

import org.drip.analytics.date.*;
import org.drip.analytics.support.CaseInsensitiveTreeMap;
import org.drip.product.definition.*;
import org.drip.service.api.CreditAnalytics;
import org.drip.service.env.StandardCDXManager;

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
 * StandardCDXAPI contains a demo of the CDS basket API Sample. It shows the following:
 * 	- Construct the CDX.NA.IG 5Y Series 17 index by name and series.
 * 	- Construct the on-the-run CDX.NA.IG 5Y Series index.
 * 	- List all the built-in CDX's - their names and descriptions.
 *  - Construct the on-the run CDX.EM 5Y corresponding to T - 1Y.
 *  - Construct the on-the run ITRAXX.ENERGY 5Y corresponding to T - 7Y.
 *  - Retrieve the full set of date/index series set for ITRAXX.ENERGY.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class StandardCDXAPI {

	/*
	 * Sample demonstrating the creation/usage of the CDX API
	 * 
	 *  	USE WITH CARE: This org.drip.sample ignores errors and does not handle exceptions.
	 */

	private static final void BasketCDSAPISample()
	{
		JulianDate dtToday = DateUtil.CreateFromYMD (
			2013,
			DateUtil.MAY,
			10
		);

		/*
		 * Construct the CDX.NA.IG 5Y Series 17 index by name and series
		 */

		BasketProduct bpCDX = CreditAnalytics.MakeCDX (
			"CDX.NA.IG",
			17,
			"5Y"
		);

		/*
		 * Construct the on-the-run CDX.NA.IG 5Y Series index
		 */

		BasketProduct bpCDXOTR = CreditAnalytics.MakeCDX (
			"CDX.NA.IG",
			dtToday,
			"5Y"
		);

		/*
		 * List of all the built-in CDX names
		 */

		Set<String> setstrCDXNames = StandardCDXManager.GetCDXNames();

		/*
		 * Descriptions of all the built-in CDX names
		 */

		CaseInsensitiveTreeMap<String> mapCDXDescr = StandardCDXManager.GetCDXDescriptions();

		/*
		 * Construct the on-the run CDX.EM 5Y corresponding to T - 1Y
		 */

		BasketProduct bpPresetOTR = StandardCDXManager.GetOnTheRun (
			"CDX.EM",
			dtToday.subtractTenor ("1Y"),
			"5Y"
		);

		/*
		 * Construct the on-the run ITRAXX.ENERGY 5Y corresponding to T - 7Y
		 */

		BasketProduct bpPreLoadedOTR = StandardCDXManager.GetOnTheRun (
			"ITRAXX.ENERGY",
			dtToday.subtractTenor ("7Y"),
			"5Y"
		);

		/*
		 * Retrieve the full set of date/index series set for ITRAXX.ENERGY
		 */

		Map<JulianDate, Integer> mapCDXSeries = StandardCDXManager.GetCDXSeriesMap ("ITRAXX.ENERGY");

		System.out.println (bpCDX.name() + ": " + bpCDX.effective() + "=>" + bpCDX.maturity());

		System.out.println (bpCDXOTR.name() + ": " + bpCDXOTR.effective() + "=>" + bpCDXOTR.maturity());

		int i = 0;

		for (String strCDX : setstrCDXNames)
			System.out.println ("CDX[" + i++ + "]: " + strCDX);

		for (Map.Entry<String, String> meCDXDescr : mapCDXDescr.entrySet())
			System.out.println ("[" + meCDXDescr.getKey() + "]: " + meCDXDescr.getValue());

		System.out.println (bpPresetOTR.name() + ": " + bpPresetOTR.effective() + "=>" + bpPresetOTR.maturity());

		System.out.println (bpPreLoadedOTR.name() + ": " + bpPreLoadedOTR.effective() + "=>" + bpPreLoadedOTR.maturity());

		for (Map.Entry<JulianDate, Integer> me : mapCDXSeries.entrySet())
			System.out.println ("ITRAXX.ENERGY[" + me.getValue() + "]: " + me.getKey());
	}

	public static final void main (
		final String astrArgs[])
		throws Exception
	{
		// String strConfig = "c:\\Lakshmi\\BondAnal\\Config.xml";

		String strConfig = "";

		CreditAnalytics.Init (strConfig);

		BasketCDSAPISample();
	}
}
