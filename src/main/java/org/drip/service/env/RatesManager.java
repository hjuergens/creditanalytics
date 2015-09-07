
package org.drip.service.env;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
 * Copyright (C) 2012 Lakshmi Krishnamurthy
 * Copyright (C) 2011 Lakshmi Krishnamurthy
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
 * RatesManager manages the creation/loading of rates curves of different kinds for a given EOD.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class RatesManager {
	private static final boolean s_bBlog = false;
	private static final boolean s_bLoadIRCurves = true;
	private static final boolean s_bLoadTSYCurves = true;
	private static final boolean s_bLoadStaticCurves = false;

	/**
	 * Retrieve all the IR curves of the type for a given EOD
	 * 
	 * @param stmt SQL Statement representing the executable query
	 * @param dtEOD EOD Date
	 * @param strInstrSetType Instrument set type string
	 * 
	 * @return Set of the IR curve names
	 */

	public static final java.util.Set<String> GetIRCurves (
		final java.sql.Statement stmt,
		final org.drip.analytics.date.JulianDate dtEOD,
		final String strInstrSetType)
	{
		if (null == stmt || null == dtEOD || null == strInstrSetType || strInstrSetType.isEmpty())
			return null;

		try {
			java.util.Set<String> setIRCurves = new java.util.HashSet<String>();

			java.sql.ResultSet rsIRCurves = stmt.executeQuery
				("select distinct Currency from IR_EOD where EOD = '" + dtEOD.toOracleDate() +
					"' and Type = '" + strInstrSetType + "'");

			while (null != rsIRCurves && rsIRCurves.next())
				setIRCurves.add (rsIRCurves.getString ("Currency"));

			return setIRCurves;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve all the IR curves of any type for a given EOD
	 * 
	 * @param stmt SQL Statement representing the executable query
	 * @param dtEOD EOD Date
	 * 
	 * @return Set of the IR curve names
	 */

	public static final java.util.Set<String> GetAvailableEODIRCurveNames (
		final java.sql.Statement stmt,
		final org.drip.analytics.date.JulianDate dtEOD)
	{
		if (null == stmt || null == dtEOD) return null;

		try {
			java.util.Set<String> setIRCurves = new java.util.HashSet<String>();

			java.sql.ResultSet rsIRCurves = stmt.executeQuery
				("select distinct Currency from IR_EOD where EOD = '" + dtEOD.toOracleDate() +
					"' order by Currency");

			while (null != rsIRCurves && rsIRCurves.next())
				setIRCurves.add (rsIRCurves.getString ("Currency"));

			return setIRCurves;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Load the entire set of IR curves of every type for a given EOD onto the MPC
	 * 
	 * @param mpc org.drip.param.definition.MarketParams onto which the curves are to be loaded
	 * @param stmt SQL Statement representing the executable query
	 * @param dtEOD EOD Date
	 * 
	 * @return Success (true) or failure (false)
	 */

	public static final boolean LoadFullIRCurves (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final java.sql.Statement stmt,
		final org.drip.analytics.date.JulianDate dtEOD)
	{
		if (null == stmt || null == dtEOD || null == mpc) return false;

		boolean bAllCurvesLoaded = true;

		if (s_bLoadIRCurves) {
			java.util.Set<String> setIRCurves = GetIRCurves (stmt, dtEOD, "swap");

			if (null == setIRCurves || 0 == setIRCurves.size())
				System.out.println ("Cannot locate an IR Curve to load");
			else {
				for (String strIRCurve : setIRCurves) {
					if (null == strIRCurve || strIRCurve.isEmpty()) {
						bAllCurvesLoaded = false;
						continue;
					}

					if (s_bLoadStaticCurves) {
						if (!StaticBACurves.setDC (mpc, dtEOD, strIRCurve)) {
							bAllCurvesLoaded = false;

							if (s_bBlog)
								System.out.println ("Cannot find IR Static Curve " + strIRCurve +
									" to load!");
						}
					} else {
						if (!EODCurves.BuildIREODCurve (mpc, stmt, dtEOD, strIRCurve)) {
							bAllCurvesLoaded = false;

							if (s_bBlog)
								System.out.println ("Cannot find IR EOD Curve " + strIRCurve + " to load!");
						}
					}
				}
			}
		}

		if (s_bLoadTSYCurves) {
			java.util.Set<String> setTSYCurves = GetIRCurves (stmt, dtEOD, "government");

			if (null == setTSYCurves || 0 == setTSYCurves.size())
				System.out.println ("Cannot locate a TSY Curve to load");
			else {
				for (String strTSYCurve : setTSYCurves) {
					if (null == strTSYCurve || strTSYCurve.isEmpty()) {
						bAllCurvesLoaded = false;

						continue;
					}

					if (s_bLoadStaticCurves) {
						if (!StaticBACurves.BuildTSYCurve (mpc, dtEOD, strTSYCurve)) {
							bAllCurvesLoaded = false;

							if (s_bBlog)
								System.out.println ("Cannot find TSY Static Curve " + strTSYCurve +
									" to load!");
						}
					} else {
						if (!EODCurves.BuildTSYEODCurve (mpc, stmt, dtEOD, strTSYCurve)) {
							bAllCurvesLoaded = false;

							if (s_bBlog)
								System.out.println ("Cannot find TSY EOD Curve " + strTSYCurve +
									" to load!");
						}
					}
				}
			}
		}

		return bAllCurvesLoaded;
	}

	public static final void main (
		final String[] astrArgs)
		throws java.lang.Exception
	{
		org.drip.analytics.support.AnalyticsHelper.Init();

		org.drip.param.definition.ScenarioMarketParams mpc =
			org.drip.param.creator.MarketParamsBuilder.CreateMarketParams();

		java.sql.Statement stmt = org.drip.service.env.EnvManager.InitEnv
			("c:\\Lakshmi\\BondAnal\\Config.xml");

		LoadFullIRCurves (mpc, stmt, org.drip.analytics.date.DateUtil.CreateFromYMD (2011, 6, 30));
	}
}
