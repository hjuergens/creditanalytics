
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
 * BondManager implements a container that holds the EOD and bond static information on a per issuer basis.
 * 	It exposes the following functionality:
 * 	- Retrieve the available tickers, and all the ISIN's per ticker.
 * 	- Load the full set of bond reference data, embedded option schedules, and amortization schedules.
 * 	- Load the full set of bond marks.
 * 	- Calculate the bond RV/Value measures for a ticker/full bond set, given the EOD and the appropriate
 * 		curves and market measures.
 * 	- Save the computed measures for a given EOD.
 * 	- (Optionally) Generate a Bond Creator File.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class BondManager {
	private static final boolean s_bBlog = false;
	private static final boolean s_bLoadEOS = true;

	private static org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double> s_mapBondMarks = new
		org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>();

	private static org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.product.credit.BondComponent>
		s_mapBonds = new
			org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.product.credit.BondComponent>();

	private static org.drip.analytics.support.CaseInsensitiveTreeMap<java.util.SortedMap<java.lang.Double,
		String>> s_mapTickerMatCUSIP = new
			org.drip.analytics.support.CaseInsensitiveTreeMap<java.util.SortedMap<java.lang.Double,
				String>>();

	private static final boolean AppendField (
		final StringBuilder sb,
		final double dblValue,
		final boolean bLast)
	{
		if (null == sb) return false;

		if (java.lang.Double.isNaN (dblValue))
			sb.append ("null");
		else
			sb.append (dblValue);

		if (bLast)
			sb.append (")");
		else
			sb.append (", ");

		return true;
	}

	private static final org.drip.product.params.EmbeddedOptionSchedule ExtractEOS (
		final java.sql.Statement stmt,
		final String strISIN,
		final double dblScheduleStart,
		final boolean bIsPut)
	{
		if (null == stmt || null == strISIN || strISIN.isEmpty() || java.lang.Double.isNaN
			(dblScheduleStart))
			return null;

		int i = 0;
		boolean bIsAmerican = false;
		String strCallOrPut = "C";

		if (bIsPut) strCallOrPut = "P";

		java.util.ArrayList<java.lang.Double> ldblCallDates = new java.util.ArrayList<java.lang.Double>();

		java.util.ArrayList<java.lang.Double> ldblCallFactors = new java.util.ArrayList<java.lang.Double>();

		try {
			java.sql.ResultSet rs = stmt.executeQuery
				("SELECT ExerciseStartDate, ExerciseFactor, EuroAmer FROM EOS where ISIN = '" + strISIN +
					"' and CallOrPut = '" + strCallOrPut + "' order by ExerciseStartDate");

			while (rs.next()) {
				ldblCallDates.add (org.drip.analytics.date.DateUtil.MakeJulianFromRSEntry (rs.getDate
					("ExerciseStartDate")).julian());

				ldblCallFactors.add (rs.getDouble ("ExerciseFactor"));

				if ("A".equalsIgnoreCase (rs.getString ("EuroAmer"))) bIsAmerican = true;
			}
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		if (0 == ldblCallDates.size() || 0 == ldblCallFactors.size()) return null;

		double[] adblEOSDate = new double[ldblCallDates.size()];

		for (double dblCallDate : ldblCallDates)
			adblEOSDate[i++] = dblCallDate;

		double[] adblEOSFactor = new double[ldblCallFactors.size()];

		i = 0;

		for (double dblCallFactor : ldblCallFactors)
			adblEOSFactor[i++] = dblCallFactor;

		if (bIsAmerican)
			return org.drip.product.params.EmbeddedOptionSchedule.FromAmerican (dblScheduleStart,
				adblEOSDate, adblEOSFactor, bIsPut, 30, false, java.lang.Double.NaN, "",
					java.lang.Double.NaN);

		try {
			return new org.drip.product.params.EmbeddedOptionSchedule (adblEOSDate, adblEOSFactor, bIsPut,
				30, false, java.lang.Double.NaN, "", java.lang.Double.NaN);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static final org.drip.product.params.EmbeddedOptionSchedule ExtractEOS2 (
		final java.sql.Statement stmt,
		final String strBondId,
		final double dblScheduleStart,
		final boolean bIsPut)
	{
		if (null == stmt || null == strBondId || strBondId.isEmpty() || java.lang.Double.isNaN
			(dblScheduleStart))
			return null;

		int i = 0;
		boolean bIsAmerican = false;
		String strCallOrPut = "C";

		if (bIsPut) strCallOrPut = "P";

		java.util.ArrayList<java.lang.Double> ldblCallDates = new java.util.ArrayList<java.lang.Double>();

		java.util.ArrayList<java.lang.Double> ldblCallFactors = new java.util.ArrayList<java.lang.Double>();

		try {
			java.sql.ResultSet rs = stmt.executeQuery
				("SELECT ExerciseStartDate, ExerciseFactor, EuroAmer FROM EOS where ISIN = '" + strBondId +
					"' and CallOrPut = '" + strCallOrPut + "' order by ExerciseStartDate");

			while (rs.next()) {
				ldblCallDates.add (org.drip.analytics.date.DateUtil.MakeJulianFromRSEntry (rs.getDate
					("ExerciseStartDate")).julian());

				ldblCallFactors.add (rs.getDouble ("ExerciseFactor"));

				if ("A".equalsIgnoreCase (rs.getString ("EuroAmer"))) bIsAmerican = true;
			}
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		if (0 == ldblCallDates.size() || 0 == ldblCallFactors.size()) return null;

		double[] adblEOSDate = new double[ldblCallDates.size()];

		for (double dblCallDate : ldblCallDates)
			adblEOSDate[i++] = dblCallDate;

		double[] adblEOSFactor = new double[ldblCallFactors.size()];

		i = 0;

		for (double dblCallFactor : ldblCallFactors)
			adblEOSFactor[i++] = dblCallFactor;

		if (bIsAmerican)
			return org.drip.product.params.EmbeddedOptionSchedule.FromAmerican (dblScheduleStart,
				adblEOSDate, adblEOSFactor, bIsPut,
					org.drip.product.params.EmbeddedOptionSchedule.CALL_NOTICE_PERIOD_DEFAULT, false,
						java.lang.Double.NaN, "", java.lang.Double.NaN);

		try {
			return new org.drip.product.params.EmbeddedOptionSchedule (adblEOSDate, adblEOSFactor, bIsPut,
				org.drip.product.params.EmbeddedOptionSchedule.CALL_NOTICE_PERIOD_DEFAULT, false,
					java.lang.Double.NaN, "", java.lang.Double.NaN);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static final org.drip.quant.common.Array2D ExtractAmortizationSchedule (
		final java.sql.Statement stmt,
		final String strBondId)
	{
		if (null == stmt || null == strBondId || strBondId.isEmpty()) return null;

		int i = 0;

		java.util.ArrayList<java.lang.Double> ldblAmortDates = new java.util.ArrayList<java.lang.Double>();

		java.util.ArrayList<java.lang.Double> ldblPrincipalPaydownFactors = new
			java.util.ArrayList<java.lang.Double>();

		try {
			java.sql.ResultSet rs = stmt.executeQuery ("select * from AmortizationSchedule where CUSIP = '" +
				strBondId + "' order by AmortDate");

			while (rs.next()) {
				ldblAmortDates.add (org.drip.analytics.date.DateUtil.MakeJulianFromRSEntry (rs.getDate
					("AmortDate")).julian());

				ldblPrincipalPaydownFactors.add (rs.getDouble ("PrincipalPaydown"));
			}
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		if (0 == ldblAmortDates.size() || 0 == ldblAmortDates.size()) return null;

		double[] adblDate = new double[ldblAmortDates.size()];

		for (double dblAmortDate : ldblAmortDates)
			adblDate[i++] = dblAmortDate;

		double[] adblPrincipalPaydownFactor = new double[ldblPrincipalPaydownFactors.size()];

		i = 0;

		for (double dblPrincipalPaydownFactor : ldblPrincipalPaydownFactors)
			adblPrincipalPaydownFactor[i++] = dblPrincipalPaydownFactor;

		return org.drip.quant.common.Array2D.FromXYDeltaArray (adblDate, adblPrincipalPaydownFactor, 1.);
	}

	/**
	 * Set the option schedule for all the bonds by extracting them from the database
	 * 
	 * @param stmt SQL Statement object representing the query to be executed
	 * 
	 * @return False indicates that the operation could not complete fully - true indicates that it was
	 *  successful
	 */

	public static final boolean SetEOS (
		final java.sql.Statement stmt)
	{
		if (null == stmt) return false;

		long lStart = System.nanoTime();

		for (java.util.Map.Entry<String, org.drip.product.credit.BondComponent> me :
			s_mapBonds.entrySet()) {
			if (null == me.getValue()) continue;

			org.drip.product.params.EmbeddedOptionSchedule eosCall = ExtractEOS (stmt,
				me.getValue().isin(), me.getValue().effectiveDate().julian(), false);

			if (null != eosCall) me.getValue().setEmbeddedCallSchedule (eosCall);

			org.drip.product.params.EmbeddedOptionSchedule eosPut = ExtractEOS (stmt,
				me.getValue().isin(), me.getValue().effectiveDate().julian(), true);

			if (null != eosPut) me.getValue().setEmbeddedPutSchedule (eosPut);
		}

		System.out.println ("EOS set in " + (System.nanoTime() - lStart) * 1.e-09 + " sec\n");

		return true;
	}

	/**
	 * Calculate the full set of calculable bond measures given the bond, the valuation parameters, and the
	 *  prices. Optionally, depending on the setting, it also displays formatted runs.
	 *  
	 * @param strBondDescription String describing the bond
	 * @param bond Bond object
	 * @param valParams ValuationParams
	 * @param mpc MarkerParamsContainer containing all the curves
	 * @param dblBidPrice Double representing the bid price
	 * @param dblAskPrice Double representing the ask price
	 * 
	 * @return The BondOutput object
	 */

	public static org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.output.BondRVMeasures>
		CalcBondMeasures
			(final String strBondDescription,
			final org.drip.product.definition.Bond bond,
			final org.drip.param.valuation.ValuationParams valParams,
			final org.drip.param.definition.ScenarioMarketParams mpc,
			final double dblBidPrice,
			final double dblAskPrice)
	{
		if (null == strBondDescription || strBondDescription.isEmpty() || null == bond || null == valParams
			|| null == mpc || java.lang.Double.isNaN (dblBidPrice) || java.lang.Double.isNaN (dblAskPrice))
			return null;

		org.drip.param.market.CurveSurfaceQuoteSet mktParams = mpc.scenarioMarketParams (bond, "Base");

		if (null == mktParams) return null;

		org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.output.BondRVMeasures> mapBMRV =
			new org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.output.BondRVMeasures>();

		org.drip.param.valuation.WorkoutInfo wiAsk = bond.exerciseYieldFromPrice (valParams, mktParams, null,
			dblAskPrice);

		if (null != wiAsk)
			mapBMRV.put ("ASK", bond.standardMeasures (valParams, null, mktParams, null, wiAsk,
				dblAskPrice));

		org.drip.param.valuation.WorkoutInfo wiBid = bond.exerciseYieldFromPrice (valParams, mktParams, null,
			dblBidPrice);

		if (null != wiBid)
			mapBMRV.put ("BID", bond.standardMeasures (valParams, null, mktParams, null, wiBid,
				dblBidPrice));

		return mapBMRV;
	}

	/**
	 * Calculate the full set of calculable bond measures given the CUSIP, the valuation parameters, and the
	 *  prices. Optionally, depending on the setting, it also displays formatted runs.
	 *  
	 * @param strCUSIPIn Bond CUSIP/ISIN
	 * @param mpc MarkerParamsContainer containing all the curves
	 * @param dt Valuation Date
	 * @param dblBidPrice Bid Price
	 * @param dblAskPrice Ask Price
	 * 
	 * @return The BondOutput
	 */

	public static org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.output.BondRVMeasures>
		CalcBondAnalyticsFromPrice (
			final String strCUSIPIn,
			final org.drip.param.definition.ScenarioMarketParams mpc,
			final org.drip.analytics.date.JulianDate dt,
			final double dblBidPrice,
			final double dblAskPrice)
	{
		if (null == strCUSIPIn || strCUSIPIn.isEmpty() || null == mpc || null == dt || java.lang.Double.isNaN
			(dblBidPrice) || java.lang.Double.isNaN (dblAskPrice))
			return null;

		org.drip.param.valuation.ValuationParams valParams = null;

		try {
			valParams = new org.drip.param.valuation.ValuationParams (dt, dt.addBusDays (3, "USD"), "USD");
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		org.drip.product.definition.Bond bond = s_mapBonds.get (strCUSIPIn);

		if (null != bond && bond.maturityDate().julian() > dt.julian()) {
	        java.text.DecimalFormat df2p = new java.text.DecimalFormat ("#00.000");

			try {
				String strRunName = bond.ticker() + "  " + df2p.format (100. * bond.couponMetrics
					(valParams.valueDate(), valParams, null).rate()) + " " +
						(org.drip.analytics.date.DateUtil.Year (bond.maturityDate().julian()) - 2000);

				if (bond.isFloater()) {
					if (s_bBlog)
						System.out.println ("Setting rate for index " + bond.rateIndex() + " and date " +
							bond.periodFixingDate (valParams.valueDate()));

					mpc.addFixing (bond.periodFixingDate (valParams.valueDate()), bond.forwardLabel().get
						(0), 0.0042);
				}

				return CalcBondMeasures (strRunName, bond, valParams, mpc, 0.01 * dblBidPrice, 0.01 *
					dblAskPrice);
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * Calculate the full set of bond measures for all available bonds given the same bid and ask prices.
	 *  Depending upon the setting, may also generate the runs.
	 * 
	 * @param mpc org.drip.param.market.MarketParamSet containing the curves
	 * @param dt Valuation date
	 * @param dblBidPrice Bid Price
	 * @param dblAskPrice Ask Price
	 * 
	 * @return Number of bonds successfully processed (excludes matured bonds)
	 */

	public static int CalcFullBondAnalytics (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final org.drip.analytics.date.JulianDate dt,
		final double dblBidPrice,
		final double dblAskPrice)
	{
		if (null == mpc || null == dt || java.lang.Double.isNaN (dblBidPrice) || java.lang.Double.isNaN
			(dblAskPrice)) {
			System.out.println ("Bad params into CalcFullBondAnalytics");

			return 0;
		}

		int iNumProcessed = 0;
		org.drip.param.valuation.ValuationParams valParams = null;

		try {
			valParams = new org.drip.param.valuation.ValuationParams (dt, dt.addBusDays (3, "USD"), "USD");
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return 0;
		}

		java.text.DecimalFormat df2_3p = new java.text.DecimalFormat ("#00.000");

		for (java.util.Map.Entry<String, org.drip.product.credit.BondComponent> me :
			s_mapBonds.entrySet()) {
			org.drip.product.definition.Bond bond = me.getValue();

			if (bond.maturityDate().julian() > dt.julian()) {
				String strRunTicker = bond.ticker();

				for (int i = bond.ticker().length() - 1; i < 6; ++i)
					strRunTicker += " ";

				try {
					String strRunName = strRunTicker + "  " + df2_3p.format (100. *
						bond.couponMetrics (valParams.valueDate(), valParams, null).rate()) + " " +
							(org.drip.analytics.date.DateUtil.Year (bond.maturityDate().julian()) - 2000);

					if (bond.isFloater())
						mpc.addFixing (bond.periodFixingDate (valParams.valueDate()),
							bond.forwardLabel().get (0), 0.0042);

					CalcBondMeasures (strRunName, bond, valParams, mpc, 0.01 * dblBidPrice, 0.01 *
						dblAskPrice);

					++iNumProcessed;
				} catch (java.lang.Exception e) {
					e.printStackTrace();
				}
			}
		}

		return iNumProcessed;
	}

	/**
	 * Calculate the complete set of bond measures for all the bonds from their closing bid/ask prices.
	 * 
	 * @param mpc org.drip.param.market.MarketParamSet containing the curves
	 * @param dt Valuation Date
	 * 
	 * @return Number of bonds successfully processed (excludes matured bonds and bonds for which closing
	 *  prices are not available).
	 */

	public static int FullBondMarketAnalytics (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final org.drip.analytics.date.JulianDate dt)
	{
		if (null == mpc || null == dt) {
			System.out.println ("Bad params into FullBondMarketAnalytics");

			return 0;
		}

		int iNumProcessed = 0;
		int iNumMarksUnavailable = 0;

        java.text.DecimalFormat df2_3p = new java.text.DecimalFormat ("#00.000");

		for (java.util.Map.Entry<String, org.drip.product.credit.BondComponent> me :
			s_mapBonds.entrySet()) {
			org.drip.product.definition.Bond bond = me.getValue();

			if (bond.maturityDate().julian() > dt.julian()) {
				String strRunTicker = bond.ticker();

				for (int i = bond.ticker().length() - 1; i < 6; ++i)
					strRunTicker += " ";

				try {
					String strRunName = strRunTicker + "  " + df2_3p.format (100. *
						bond.couponMetrics (dt.julian(), null, null).rate()) + " " +
							(org.drip.analytics.date.DateUtil.Year (bond.maturityDate().julian()) - 2000);

					if (!s_mapBondMarks.containsKey (bond.isin()) && !s_mapBondMarks.containsKey
						(bond.cusip())) {
						if (s_bBlog) System.out.println ("No price entry found for " + strRunTicker);

						++iNumMarksUnavailable;
						continue;
					}

					org.drip.param.valuation.ValuationParams valParams = new
						org.drip.param.valuation.ValuationParams (dt, dt.addBusDays (3, "USD"), "USD");

					if (bond.isFloater())
						mpc.addFixing (bond.periodFixingDate (valParams.valueDate()), bond.forwardLabel().get
							(0), 0.0042);

					double dblMidPrice = java.lang.Double.NaN;

					if (s_mapBondMarks.containsKey (bond.isin()))
						dblMidPrice = s_mapBondMarks.get (bond.isin());
					else
						dblMidPrice = s_mapBondMarks.get (bond.cusip());

					if (!java.lang.Double.isNaN (dblMidPrice)) {
						CalcBondMeasures (strRunName, bond, valParams, mpc, 0.01 * (dblMidPrice - 0.25), 0.01
							* (dblMidPrice + 0.25));

						++iNumProcessed;
					}
				} catch (java.lang.Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (0 != iNumMarksUnavailable)
			System.out.println ("Marks unavailable for " + iNumMarksUnavailable + " bonds!");

		return iNumProcessed;
	}

	/**
	 * Retrieve the mid marks (price/spreads) for the given ISIN/CUSIP and the valuation date
	 * 
	 * @param strCUSIP ISIN/CUSIP string
	 * @param dt Valuation Date
	 * @param stmt SQL Statement object representing the connection
	 * 
	 * @return Double representing the mid marks
	 * 
	 * @throws java.lang.Exception Thrown if there is an input error or a database failure
	 */

	public static final double GetMidMarksForCUSIP (
		final String strCUSIP,
		final org.drip.analytics.date.JulianDate dt,
		final java.sql.Statement stmt)
		throws java.lang.Exception
	{
		if (null == strCUSIP || strCUSIP.isEmpty() || null == dt || null == stmt)
			throw new java.lang.Exception ("Bad inputs into BondManager.GetMidMarksForCUSIP");

		double dblMidPrice = java.lang.Double.NaN;

		java.sql.ResultSet rs = stmt.executeQuery ("SELECT * FROM BondMarks where ID = '" + strCUSIP +
			"' and MARKDATE = '" + dt.toOracleDate() + "'");

		while (rs.next())
			dblMidPrice = rs.getDouble ("MidPrice");

		return dblMidPrice;
	}

	/**
	 * Load all the mid bond marks for the given EOD
	 * 
	 * @param dt EOD date
	 * @param stmt SQL Statement object representing the query
	 * 
	 * @return Success (true) or failure
	 */

	public static final boolean LoadMidBondMarks (
		final org.drip.analytics.date.JulianDate dt,
		final java.sql.Statement stmt) {
		if (null == dt || null == stmt) return false;

		try {
			java.sql.ResultSet rs = stmt.executeQuery
				("SELECT ID, MidPrice FROM BondMarks where MARKDATE = '" + dt.toOracleDate() + "'");

			while (rs.next())
				s_mapBondMarks.put (rs.getString ("ID"), rs.getDouble ("MidPrice"));

			return true;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * Build a bond from the input result set
	 * 
	 * @param rs ResultSet containing the list of queried bonds
	 * @param mpc org.drip.param.market.MarketParamSet containing the closing curves
	 * 
	 * @return Bond object
	 */

	public static final org.drip.product.credit.BondComponent BuildBondFromResultSet (
		final java.sql.ResultSet rs,
		final org.drip.param.definition.ScenarioMarketParams mpc)
	{
		if (null == rs) return null;

		org.drip.product.creator.BondProductBuilder bpb = null;

		try {
			if (null == (bpb = org.drip.product.creator.BondProductBuilder.CreateFromResultSet (rs, mpc)))
				return null;
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return null;
		}

		org.drip.product.params.IdentifierSet idParams = bpb.getIdentifierParams();

		if (null == idParams) return null;

		if (s_bBlog) System.out.println ("Working on making " + idParams.cusip());

		org.drip.product.params.BondStream periodParams = bpb.getPeriodGenParams();

		if (null == periodParams) return null;

		org.drip.product.params.CouponSetting cpnParams = bpb.getCouponParams();

		if (null == cpnParams) return null;

		org.drip.product.params.NotionalSetting notlParams = bpb.getNotionalParams();

		if (null == notlParams) return null;

		org.drip.product.params.FloaterSetting fltParams = bpb.getFloaterParams();

		org.drip.product.params.QuoteConvention mktConv = bpb.getMarketConvention();

		if (null == mktConv) return null;

		org.drip.product.params.CreditSetting crValParams = bpb.getCRValuationParams();

		if (null == crValParams) return null;

		org.drip.product.params.TerminationSetting cfteParams = bpb.getCFTEParams();

		if (null == cfteParams) return null;

		org.drip.product.credit.BondComponent bond = new org.drip.product.credit.BondComponent();

		bond.setIdentifierSet (idParams);

		bond.setCouponSetting (cpnParams);

		bond.setNotionalSetting (notlParams);

		bond.setFloaterSetting (fltParams);

		bond.setStream (periodParams);

		bond.setMarketConvention (mktConv);

		bond.setCreditSetting (crValParams);

		bond.setTerminationSetting (cfteParams);

		return bond;
	}

	/**
	 * Load the bond object using its ID
	 * 
	 * @param mpc org.drip.param.market.MarketParamSet containing the closing curves
	 * @param stmt SQL Statement object representing the query
	 * @param strBondId ISIN/CUSIP/other identifiable bond IDs
	 * @param dblScheduleStart Schedule start date - needed for bonds with embedded American options, so that
	 *  the discretized schedules can start from this date (usually the Valuation date)
	 * 
	 * @return Bond object
	 */

	public static final org.drip.product.definition.Bond LoadFromBondId (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final java.sql.Statement stmt,
		final String strBondId,
		final double dblScheduleStart)
	{
		if (null == stmt) return null;

		try {
			java.sql.ResultSet rs = stmt.executeQuery ("SELECT * FROM BondValData where ISIN = '" + strBondId
				+ "'");

			while (rs.next()) {
				org.drip.product.credit.BondComponent bond = BuildBondFromResultSet (rs, mpc);

				if (null == bond) {
					System.out.println ("Cannot build bond from ISIN " + strBondId + "!");

					return null;
				}

				double dblFirstExDate = bond.marketConvention().settleDate
					(org.drip.param.valuation.ValuationParams.Spot (new org.drip.analytics.date.JulianDate
						(dblScheduleStart), 0, "", org.drip.analytics.daycount.Convention.DATE_ROLL_ACTUAL));

				bond.setEmbeddedCallSchedule (ExtractEOS2 (stmt, strBondId, dblFirstExDate, false));

				bond.setEmbeddedPutSchedule (ExtractEOS2 (stmt, strBondId, dblFirstExDate, true));

				org.drip.quant.common.Array2D fsEOS = ExtractAmortizationSchedule (stmt,
					strBondId);

				if (null != fsEOS)
					bond.setNotionalSetting (new org.drip.product.params.NotionalSetting (1.,
						bond.principalCurrency(), fsEOS,
							org.drip.product.params.NotionalSetting.PERIOD_AMORT_AT_START, false));

				return bond;
			}

			rs = stmt.executeQuery ("SELECT * FROM BondValData where CUSIP = '" + strBondId + "'");

			while (rs.next()) {
				org.drip.product.credit.BondComponent bond = BuildBondFromResultSet (rs, mpc);

				if (null == bond) {
					System.out.println ("Cannot build bond from CUSIP " + strBondId + "!");

					return null;
				}

				bond.setEmbeddedCallSchedule (ExtractEOS2 (stmt, strBondId, dblScheduleStart, false));

				bond.setEmbeddedPutSchedule (ExtractEOS2 (stmt, strBondId, dblScheduleStart, true));

				org.drip.quant.common.Array2D fsEOS = ExtractAmortizationSchedule (stmt, strBondId);

				if (null != fsEOS)
					bond.setNotionalSetting (new org.drip.product.params.NotionalSetting (1.,
						bond.principalCurrency(), fsEOS,
							org.drip.product.params.NotionalSetting.PERIOD_AMORT_AT_START, false));

				return bond;
			}
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Load the reference data corresponding to the input bond ID
	 * 
	 * @param stmt SQL Statement representing the query
	 * @param strBondId Bond ID
	 * 
	 * @return Bond object
	 */

	public static final org.drip.product.creator.BondRefDataBuilder LoadBondRefData (
		final java.sql.Statement stmt,
		final String strBondId)
	{
		if (null == stmt) return null;

		try {
			java.sql.ResultSet rs = stmt.executeQuery ("SELECT * FROM BondRefData where ISIN = '" + strBondId
				+ "'");

			while (rs.next()) {
				org.drip.product.creator.BondRefDataBuilder brdb =
					org.drip.product.creator.BondRefDataBuilder.CreateFromResultSet (rs);

				if (null == brdb) {
					System.out.println ("Cannot build brdb for ISIN " + strBondId + "!");

					return null;
				}

				return brdb;
			}

			rs = stmt.executeQuery ("SELECT * FROM BondRefData where CUSIP = '" + strBondId + "'");

			while (rs.next()) {
				org.drip.product.creator.BondRefDataBuilder brdb =
					org.drip.product.creator.BondRefDataBuilder.CreateFromResultSet (rs);

				if (null == brdb) {
					System.out.println ("Cannot build brdb for CUSIP " + strBondId + "!");

					return null;
				}

				return brdb;
			}
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Get all the available tickers from the database
	 * 
	 * @param stmt SQL Statement object representing the executable query
	 * 
	 * @return Set of the tickers
	 */

	public static final java.util.Set<String> GetAvailableTickers (
		final java.sql.Statement stmt)
	{
		if (null == stmt) return null;

		try {
			java.util.Set<String> setstrTickers = new java.util.HashSet<String>();

			java.sql.ResultSet rs = stmt.executeQuery
				("SELECT distinct Ticker FROM BondValData order by Ticker");

			while (rs.next())
				setstrTickers.add (rs.getString ("Ticker"));

			return setstrTickers;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve all the ISINs for the given ticker
	 * 
	 * @param stmt SQL Statement object representing the executable query
	 * @param strTicker String representing the ticker
	 * 
	 * @return Set of ISINs
	 */

	public static final java.util.List<String> GetISINsForTicker (
		final java.sql.Statement stmt,
		final String strTicker)
	{
		if (null == stmt || null == strTicker || strTicker.isEmpty()) return null;

		try {
			java.util.List<String> lsstrISIN = new java.util.ArrayList<String>();

			java.sql.ResultSet rs = stmt.executeQuery ("SELECT ISIN FROM BondValData where Ticker = '" +
				strTicker + "' order by Maturity");

			while (rs.next())
				lsstrISIN.add (rs.getString ("ISIN"));

			return lsstrISIN;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Create all the bonds, and loads them onto the memory
	 * 
	 * @param mpc MarketParamasContainer containing the closing curves
	 * @param stmt SQL Statement object representing the executable query
	 * 
	 * @return Number of bonds loaded/added
	 */

	public static final int CommitBondsToMem (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final java.sql.Statement stmt)
	{
		if (null == mpc || null == stmt) return 0;

		int iNumBonds = 0;
		int iNumFloaters = 0;

		long lStart = System.nanoTime();

		try {
			java.sql.ResultSet rs = stmt.executeQuery
				("SELECT * FROM BondValData order by Ticker, Maturity");

			while (rs.next()) {
				org.drip.product.credit.BondComponent bond = BuildBondFromResultSet (rs, mpc);

				if (null == bond || bond.marketConvention().firstSettleDate() >=
					bond.maturityDate().julian())
					continue;

				s_mapBonds.put (bond.identifierSet().cusip(), bond);

				s_mapBonds.put (bond.identifierSet().isin(), bond);

				java.util.SortedMap<java.lang.Double, String> mapMatBond = s_mapTickerMatCUSIP.get
					(bond.identifierSet().ticker());

				if (null == mapMatBond)
					mapMatBond = new java.util.TreeMap<java.lang.Double, String>();

				mapMatBond.put (bond.maturityDate().julian(), bond.identifierSet().cusip());

				s_mapTickerMatCUSIP.put (bond.identifierSet().ticker(), mapMatBond);

				++iNumBonds;

				if (bond.isFloater()) ++iNumFloaters;

				if (s_bBlog)
					System.out.println ("Loaded Bond[" + iNumBonds + "] = " + bond.identifierSet().ticker() +
						" " + bond.maturityDate());
			}
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		System.out.println ("Loaded " + iNumBonds + " bonds in " + (System.nanoTime() - lStart) * 1.e-09 +
			" sec with " + iNumFloaters + " FRNs!");

		if (s_bLoadEOS) SetEOS (stmt);

		return iNumBonds;
	}

	/**
	 * Calculate the bond measures corresponding to the bonds in the ticker from the given price
	 * 
	 * @param strTicker Ticker string
	 * @param mpc MarketParamasContainer containing the closing curves
	 * @param dt Valuation date
	 * @param dblBidPrice Bid Price
	 * @param dblAskPrice Ask Price
	 * 
	 * @return True indicates success, false indicates failure
	 */

	public static final boolean CalcMeasuresForTicker (
		final String strTicker,
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final org.drip.analytics.date.JulianDate dt,
		final double dblBidPrice,
		final double dblAskPrice)
	{
		if (null == strTicker || strTicker.isEmpty() || null == mpc || null == dt || java.lang.Double.isNaN
			(dblBidPrice) || java.lang.Double.isNaN (dblAskPrice)) {
			System.out.println ("Bad params into BondManager.CalcMeasuresForTicker");

			return false;
		}

		int iNumBonds = 0;
		int iNumProcessed = 0;

		long lTestStart = System.nanoTime();

		java.util.SortedMap<java.lang.Double, String> mapMatCUSIP = s_mapTickerMatCUSIP.get
			(strTicker);

		if (null != mapMatCUSIP) {
			for (java.util.SortedMap.Entry<java.lang.Double, String> me : mapMatCUSIP.entrySet()) {
				++iNumBonds;

				if (null != CalcBondAnalyticsFromPrice (me.getValue(), mpc, dt, dblBidPrice, dblAskPrice))
					++iNumProcessed;
			}
		}

		System.out.println (iNumProcessed + " out of " + iNumBonds + " for " + strTicker + " took " +
			(System.nanoTime() - lTestStart) * 1.e-06 + " msec\n");

		return true;
	}

	/**
	 * Calculate the bond measures corresponding to the bonds in the ticker from their market prices
	 * 
	 * @param strTicker Ticker string
	 * @param mpc MarketParamasContainer containing the closing curves
	 * @param dt Valuation date
	 * 
	 * @return True indicates success, false indicates failure
	 */

	public static final boolean CalcMarketMeasuresForTicker (
		final String strTicker,
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final org.drip.analytics.date.JulianDate dt)
	{
		if (null == strTicker || strTicker.isEmpty() || null == mpc || null == dt) {
			System.out.println ("Bad params into BondManager.CalcMarketMeasuresForTicker");

			return false;
		}

		long lTestStart = System.nanoTime();

		java.util.SortedMap<java.lang.Double, String> mapMatCUSIP = s_mapTickerMatCUSIP.get
			(strTicker);

		if (null != mapMatCUSIP) {
			for (java.util.SortedMap.Entry<java.lang.Double, String> me : mapMatCUSIP.entrySet()) {
				if (null == me || null == me.getValue() || null == s_mapBonds.get (me.getValue())) continue;

				org.drip.product.definition.Bond bond = s_mapBonds.get (me.getValue());

				if (!s_mapBondMarks.containsKey (bond.isin()) && !s_mapBondMarks.containsKey (bond.cusip()))
				{
					if (s_bBlog) System.out.println ("No price entry found for " + bond.cusip());

					continue;
				}

				double dblMidPrice = java.lang.Double.NaN;

				if (s_mapBondMarks.containsKey (bond.isin()))
					dblMidPrice = s_mapBondMarks.get (bond.isin());
				else
					dblMidPrice = s_mapBondMarks.get (bond.cusip());

				if (!java.lang.Double.isNaN (dblMidPrice))
					CalcBondAnalyticsFromPrice (me.getValue(), mpc, dt, dblMidPrice - 0.25, dblMidPrice +
						0.25);
			}
		}

		System.out.println ("Runs for " + strTicker + " bonds took " + (System.nanoTime() - lTestStart) *
			1.e-06 + " milli-sec\n");

		return true;
	}

	/**
	 * Calculate the bond measures for the given bond and price, and loads them onto the DB
	 * 
	 * @param stmt SQL Statement object representing the executable query
	 * @param bond Input bond
	 * @param valParams ValuationParams containing the EOD
	 * @param mpc org.drip.param.market.MarketParamSet containing the EOD curves
	 * @param dblPrice Double price
	 * 
	 * @return Success (true), failure (false)
	 */

	public static final boolean CalcAndLoadBondMeasuresFromPrice (
		final java.sql.Statement stmt,
		final org.drip.product.definition.Bond bond,
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final double dblPrice)
	{
		if (null == stmt || null == bond || null == valParams || null == mpc || java.lang.Double.isNaN
			(dblPrice)) {
			System.out.println ("Bad inputs into BondManager.CalcAndLoadBondMeasuresFromPrice");

			return false;
		}

		String strOracleEOD = "";
		double dblZSpread = java.lang.Double.NaN;
		double dblGSpread = java.lang.Double.NaN;
		double dblISpread = java.lang.Double.NaN;
		double dblTSYSpread = java.lang.Double.NaN;
		double dblASWSpread = java.lang.Double.NaN;
		double dblCreditBasis = java.lang.Double.NaN;
		org.drip.param.valuation.WorkoutInfo wi = null;

		try {
			strOracleEOD = (new org.drip.analytics.date.JulianDate (valParams.valueDate())).toOracleDate();

			stmt.executeQuery ("delete from BondHist where ISIN = '" + bond.isin() + "' and EOD = '" +
				strOracleEOD + "'");

			stmt.executeQuery ("delete from BondHist where CUSIP = '" + bond.cusip() + "' and EOD = '" +
				strOracleEOD + "'");
		} catch (java.lang.Exception e) {
			System.out.println (e.getMessage() + "; " + bond.name() + " for price=" + dblPrice);

			e.printStackTrace();

			return false;
		}

		StringBuilder sbSQLInsertBondClose = new StringBuilder();

		sbSQLInsertBondClose.append ("insert into BondHist values('").append (bond.isin()).append
			("', '").append (bond.cusip()).append ("', '").append (strOracleEOD).append ("', ").append
				(dblPrice).append (", ");

		try {
			wi = bond.exerciseYieldFromPrice (valParams, mpc.scenarioMarketParams (bond, "Base"), null,
				dblPrice);
		} catch (java.lang.Exception e) {
			System.out.println (e.getMessage() + "; " + bond.name() + " for price=" + dblPrice);

			e.printStackTrace();

			return false;
		}

		try {
			dblZSpread = bond.zspreadFromPrice (valParams, mpc.scenarioMarketParams (bond, "Base"), null,
				wi.date(), wi.factor(), dblPrice);

			dblGSpread = bond.gSpreadFromPrice (valParams, mpc.scenarioMarketParams (bond, "Base"), null,
				wi.date(), wi.factor(), dblPrice);

			dblISpread = bond.gSpreadFromPrice (valParams, mpc.scenarioMarketParams (bond, "Base"), null,
				wi.date(), wi.factor(), dblPrice);

			dblTSYSpread = bond.gSpreadFromPrice (valParams, mpc.scenarioMarketParams (bond, "Base"), null,
				wi.date(), wi.factor(), dblPrice);

			dblASWSpread = bond.aswFromPrice (valParams, mpc.scenarioMarketParams (bond, "Base"), null,
				wi.date(), wi.factor(), dblPrice);

			dblCreditBasis = bond.creditBasisFromPrice (valParams, mpc.scenarioMarketParams (bond, "Base"),
				null, wi.date(), wi.factor(), dblPrice);

			AppendField (sbSQLInsertBondClose, wi.yield(), false);

			if (java.lang.Double.isNaN (wi.date()))
				sbSQLInsertBondClose.append ("'', ");
			else
				sbSQLInsertBondClose.append ("'" + (new org.drip.analytics.date.JulianDate
					(wi.date()).toOracleDate()) + "', ");
		} catch (java.lang.Exception e) {
			System.out.println (e.getMessage() + "; " + bond.name() + " for price=" + dblPrice);

			return false;
		}

		AppendField (sbSQLInsertBondClose, wi.factor(), false);

		AppendField (sbSQLInsertBondClose, dblZSpread, false);

		AppendField (sbSQLInsertBondClose, dblGSpread, false);

		AppendField (sbSQLInsertBondClose, dblISpread, false);

		AppendField (sbSQLInsertBondClose, dblTSYSpread, false);

		AppendField (sbSQLInsertBondClose, dblASWSpread, false);

		AppendField (sbSQLInsertBondClose, dblCreditBasis, true);

		if (s_bBlog) System.out.println (sbSQLInsertBondClose.toString());

		try {
			stmt.executeQuery (sbSQLInsertBondClose.toString());

			return true;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Calculate and saves the measures for all the bonds form their market prices for a given EOD
	 * 
	 * @param stmt SQL Statement object representing the executable query
	 * @param dtEOD EOD Date
	 * 
	 * @return Integer representing the number of bonds calculated and saved
	 */

	public static int SaveBondCalcMeasures (
		final java.sql.Statement stmt,
		final org.drip.analytics.date.JulianDate dtEOD)
	{
		if (null == stmt || null == dtEOD) {
			System.out.println ("Bad inputs into BondManager.SaveBondCalcMeasures");

			return 0;
		}

		int iNumProcessed = 0;
		int iNumMarksUnavailable = 0;
		org.drip.param.valuation.ValuationParams valParams = null;

		try {
			valParams = new org.drip.param.valuation.ValuationParams (dtEOD, dtEOD.addBusDays (3, "USD"),
				"USD");
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return 0;
		}

		org.drip.param.definition.ScenarioMarketParams mpc =
			org.drip.param.creator.MarketParamsBuilder.CreateMarketParams();

		RatesManager.LoadFullIRCurves (mpc, stmt, dtEOD);

		CDSManager.LoadFullCreditCurves (mpc, stmt, dtEOD);

		for (java.util.Map.Entry<String, org.drip.product.credit.BondComponent> me :
			s_mapBonds.entrySet()) {
			org.drip.product.definition.Bond bond = me.getValue();

			if (bond.maturityDate().julian() > dtEOD.julian()) {
				if (!s_mapBondMarks.containsKey (bond.isin()) && !s_mapBondMarks.containsKey (bond.cusip()))
				{
					if (s_bBlog) System.out.println ("No price entry found for " + bond.name());

					++iNumMarksUnavailable;
					continue;
				}

				try {
					if (bond.isFloater())
						mpc.addFixing (bond.periodFixingDate (valParams.valueDate()), bond.forwardLabel().get
							(0), 0.0042);
				} catch (java.lang.Exception e) {
					e.printStackTrace();

					continue;
				}

				double dblMidPrice = java.lang.Double.NaN;

				if (s_mapBondMarks.containsKey (bond.isin()))
					dblMidPrice = 0.01 * s_mapBondMarks.get (bond.isin());
				else
					dblMidPrice = 0.01 * s_mapBondMarks.get (bond.cusip());

				CalcAndLoadBondMeasuresFromPrice (stmt, bond, valParams, mpc, dblMidPrice);

				++iNumProcessed;
			}
		}

		if (0 != iNumMarksUnavailable)
			System.out.println ("Marks unavailable for " + iNumMarksUnavailable + " bonds!");

		return iNumProcessed;
	}

	/**
	 * Calculate and saves the measures for all the bonds from their market prices for all EODs between a
	 * 	given pair of dates
	 * 
	 * @param mpc MarketParams containing the curves
	 * @param stmt SQL Statement object representing the executable query
	 * @param dtEODStart EOD start
	 * @param dtEODFinish EOD finish
	 * 
	 * @return Success - true, failure - false
	 */

	public static final boolean CalcAndLoadBondClosingMeasures (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final java.sql.Statement stmt,
		final org.drip.analytics.date.JulianDate dtEODStart,
		final org.drip.analytics.date.JulianDate dtEODFinish)
	{
		if (null == mpc || null == stmt || null == dtEODStart || null == dtEODFinish) return false;

		java.util.Set<org.drip.analytics.date.JulianDate> setEOD = new
			java.util.HashSet<org.drip.analytics.date.JulianDate>();

		if (0 == CommitBondsToMem (mpc, stmt)) return false;

		java.sql.ResultSet rsEOD = null;

		try {
			rsEOD = stmt.executeQuery ("select distinct MARKDATE from BondMarks where MARKDATE >= '" +
				dtEODStart.toOracleDate() + "' and MARKDATE <= '" + dtEODFinish.toOracleDate() +
					"' order by MARKDATE asc");

			while (null != rsEOD && rsEOD.next())
				setEOD.add (org.drip.analytics.date.DateUtil.MakeJulianFromRSEntry (rsEOD.getDate
					("MARKDATE")));
		} catch (java.lang.Exception e) {
			e.printStackTrace();

			return false;
		}

		for (org.drip.analytics.date.JulianDate dtEOD : setEOD) {
			s_mapBondMarks.clear();

			LoadMidBondMarks (dtEOD, stmt);

			SaveBondCalcMeasures (stmt, dtEOD);
		}

		return true;
	}
	
	/**
	 * Generate the bond creator file
	 * 
	 * @param mpc MarketParamasContainer containing the closing curves
	 * @param stmt SQL Statement object representing the executable query
	 * 
	 * @return Whether the Bond Creator file succeeded
	 */

	public static final boolean GenerateBondCreatorFile (
		final org.drip.param.definition.ScenarioMarketParams mpc,
		final java.sql.Statement stmt)
	{
		if (null == mpc || null == stmt) {
			System.out.println ("Invalid inputs into GenerateBondCreatorFile!");

			return false;
		}

		try {
			java.io.BufferedWriter bw = new java.io.BufferedWriter (new java.io.FileWriter
				("c:\\Lakshmi\\java\\BondSetStaticCreator.java"));

			bw.write ("\npackage org.drip.service.env;\n\n");

			bw.write
				("/*\n * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-\n */\n");

			bw.write ("\n/*\n *    GENERATED on " + new java.util.Date().toString() +
				" ---- PLEASE DO NOT MODIFY\n */\n");

			bw.write ("\n/*!\n * Copyright (C) 2011 Lakshmi Krishnamurthy\n *\n");

			bw.write
				(" * This file is part of CreditAnalytics, a free-software/open-source library for fixed ");

			bw.write (" income analysts and \n");

			bw.write (" * 		developers - http://www.credit-trader.org/\n *\n");

			bw.write
				(" * CreditAnalytics is a free, full featured, fixed income credit analytics library, ");

			bw.write (" developed with a special focus\n");

			bw.write (" * 		towards the needs of the bonds and credit products community.\n *\n");

			bw.write (" *  Licensed under the Apache License, Version 2.0 (the \"License\");\n");

			bw.write (" *   	you may not use this file except in compliance with the License.\n");

			bw.write (" *\n *  You may obtain a copy of the License at\n");

			bw.write (" *  	http://www.apache.org/licenses/LICENSE-2.0\n *\n");

			bw.write (" *  Unless required by applicable law or agreed to in writing, software\n");

			bw.write (" *  	distributed under the License is distributed on an \"AS IS\" BASIS,\n");

			bw.write (" *  	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");

			bw.write (" *  \n *  See the License for the specific language governing permissions and\n");

			bw.write (" *  	limitations under the License.\n */\n\n");

			bw.write ("class BondSetStaticCreator {\n");

			bw.write ("\tpublic BondSetStaticCreator() {\n");

			bw.write ("\t}\n");

			bw.write ("\n\tprivate static final org.drip.param.product.BondTSYParams CreateTSYParams ");

			bw.write ("(final String\n\t\tstrBmkPrimary, final ");

			bw.write ("String strIRTSY, final String strIREDSF) {\n");

			bw.write ("\t\torg.drip.param.product.BondTSYParams tsyParams = new ");

			bw.write ("org.drip.param.product.BondTSYParams (new\n\t\t\t");

			bw.write ("org.drip.param.product.TsyBmkSet (strBmkPrimary, null), strIRTSY, strIREDSF);\n\n");

			bw.write ("\t\tif (!tsyParams.validate()) return null;\n\n");

			bw.write ("\t\treturn tsyParams;\n");

			bw.write ("\t}\n");

			bw.write ("\n\tprivate static final org.drip.param.product.BondCouponParams CreateCouponParams");

			bw.write (" (final\n\t\torg.drip.param.product.FactorSchedule fs, final ");

			bw.write ("String strCouponType, final double\n");

			bw.write ("\t\t\tdblCoupon) {\n");

			bw.write ("\t\torg.drip.param.product.BondCouponParams cpnParams = new ");

			bw.write ("org.drip.param.product.BondCouponParams (fs,\n");

			bw.write ("\t\t\tstrCouponType, dblCoupon);\n\n");

			bw.write ("\t\tif (!cpnParams.validate()) return null;\n\n");

			bw.write ("\t\treturn cpnParams;\n");

			bw.write ("\t}\n");

			bw.write ("\n\tprivate static final org.drip.param.product.BondNotionalParams ");

			bw.write ("CreateNotionalParams");

			bw.write (" (final\n\t\torg.drip.param.product.FactorSchedule fs, final double dblNotional) ");

			bw.write ("{\n\t\torg.drip.param.product.BondNotionalParams notlParams = new ");

			bw.write ("org.drip.param.product.BondNotionalParams\n");

			bw.write ("\t\t\t(fs, dblNotional);\n\n");

			bw.write ("\t\tif (!notlParams.validate()) return null;\n\n");

			bw.write ("\t\treturn notlParams;\n");

			bw.write ("\t}\n");

			bw.write ("\n\tprivate static final org.drip.param.product.BondFloaterParams ");

			bw.write ("CreateFloaterParams (final\n\t\tString strRateIndex, ");

			bw.write ("final String strFloatDayCount");

			bw.write (", final double dblFloatSpread,\n\t\t\tfinal double dblCurrentCoupon) {\n");

			bw.write ("\t\torg.drip.param.product.BondFloaterParams fltParams = new ");

			bw.write ("org.drip.param.product.BondFloaterParams\n");

			bw.write ("\t\t\t(strRateIndex, strFloatDayCount, dblFloatSpread, dblCurrentCoupon);\n\n");

			bw.write ("\t\tif (!fltParams.validate()) return null;\n\n");

			bw.write ("\t\treturn fltParams;\n");

			bw.write ("\t}\n");

			bw.write ("\n\tprivate static final org.drip.param.product.BondCurrencyParams ");

			bw.write ("CreateCurrencyParams (final\n\t\tString strTradeCurrency, ");

			bw.write ("final String strCouponCurrency, final String\n");

			bw.write ("\t\t\tstrRedemptionCurrency) {\n");

			bw.write ("\t\torg.drip.param.product.BondCurrencyParams ccyParams = new ");

			bw.write ("org.drip.param.product.BondCurrencyParams\n");

			bw.write ("\t\t\t(strTradeCurrency, strCouponCurrency, strRedemptionCurrency);\n\n");

			bw.write ("\t\tif (!ccyParams.validate()) return null;\n\n");

			bw.write ("\t\treturn ccyParams;\n");

			bw.write ("\t}\n");

			bw.write ("\n\tprivate static final org.drip.param.product.BondIdentifierParams ");

			bw.write ("CreateIdentifierParams (final\n\t\tString strISIN, final String");

			bw.write (" strCUSIP, final String strBondID, final\n");

			bw.write ("\t\t\tString strTicker) {\n");

			bw.write ("\t\torg.drip.param.product.BondIdentifierParams idParams = new \n");

			bw.write ("\t\t\torg.drip.param.product.BondIdentifierParams (strISIN, strCUSIP, strBondID, ");

			bw.write ("strTicker);\n\n\t\tif (!idParams.validate()) return null;\n\n");

			bw.write ("\t\treturn idParams;\n");

			bw.write ("\t}\n");

			bw.write ("\n\tprivate static final org.drip.param.product.BondIRValuationParams ");

			bw.write ("CreateIRValuationParams (final\n\t\tString strIR, final String");

			bw.write (" strQuoteConv, final String\n");

			bw.write ("\t\t\tstrCalculationType, final double");

			bw.write (" dblFirstSettle, final double dblRedemptionValue, final int\n");

			bw.write ("\t\t\t\tiSettleLag, final String strSettleCalendar, final int ");

			bw.write ("iSettleAdjustMode) {\n");

			bw.write ("\t\torg.drip.param.product.BondIRValuationParams irValParams = new \n");

			bw.write ("\t\t\torg.drip.param.product.BondIRValuationParams (strIR, strQuoteConv, ");

			bw.write ("strCalculationType,\n");

			bw.write ("\t\t\t\tdblFirstSettle, dblRedemptionValue, iSettleLag, strSettleCalendar, ");

			bw.write ("iSettleAdjustMode);\n\n\t\tif (!irValParams.validate()) return null;\n\n");

			bw.write ("\t\treturn irValParams;\n");

			bw.write ("\t}\n");

			bw.write ("\n\tprivate static final org.drip.param.product.CompCRValParams ");

			bw.write ("CreateCRValParams (final int iDefPayLag,\n");

			bw.write ("\t\tfinal double dblRecovery, final boolean bUseCurveRec, final String");

			bw.write ("strCC, final boolean\n\t\t\tbAccrOnDefault) {\n");

			bw.write ("\t\torg.drip.param.product.CompCRValParams crValParams = new ");

			bw.write ("org.drip.param.product.CompCRValParams\n");

			bw.write ("\t\t\t(iDefPayLag, dblRecovery, bUseCurveRec, strCC, bAccrOnDefault);\n\n");

			bw.write ("\t\tif (!crValParams.validate()) return null;\n\n");

			bw.write ("\t\treturn crValParams;\n");

			bw.write ("\t}\n");

			bw.write ("\n\tprivate static final org.drip.param.product.BondCFTerminationEvent ");

			bw.write ("CreateCFTEParams (final boolean\n");

			bw.write ("\t\tbIsPerpetual, final boolean bIsDefaulted, final boolean bHasBeenExercised) {\n");

			bw.write ("\t\torg.drip.param.product.BondCFTerminationEvent cfteParams = new\n");

			bw.write ("\t\t\torg.drip.param.product.BondCFTerminationEvent ");

			bw.write ("(bIsPerpetual, bIsDefaulted, bHasBeenExercised);\n\n");

			bw.write ("\t\tif (!cfteParams.validate()) return null;\n\n");

			bw.write ("\t\treturn cfteParams;\n");

			bw.write ("\t}\n");

			bw.write ("\n\tprivate static final org.drip.param.product.BondFixedPeriodGenerationParams ");

			bw.write ("CreatePeriodGenParams (final\n");

			bw.write ("\t\tdouble dblEffective, final String strDC, final int iFreq, final\n");

			bw.write ("\t\t\tjava.util.List<org.drip.analytics.period.Period> lPeriods) {\n");

			bw.write ("\t\torg.drip.param.product.BondFixedPeriodGenerationParams periodParams = new\n");

			bw.write ("\t\t\torg.drip.param.product.BondFixedPeriodGenerationParams ");

			bw.write ("(dblEffective, strDC, iFreq, lPeriods);\n\n");

			bw.write ("\t\tif (!periodParams.validate()) return null;\n\n");

			bw.write ("\t\treturn periodParams;\n");

			bw.write ("\t}\n");

			bw.write ("\n\tpublic static final ");

			bw.write ("org.drip.analytics.support.CaseInsensitiveMap<org.drip.product.credit.Bond> CreateBondSetStatic\n");

			bw.write ("\t\t(final org.drip.param.market.MarketParamSet mpc) {\n");

			bw.write ("\t\torg.drip.analytics.support.CaseInsensitiveMap<org.drip.product.credit.Bond> mapBondCache = ");

			bw.write ("new\n\t\t\torg.drip.analytics.support.CaseInsensitiveMap<org.drip.product.credit.Bond>();\n\n");

			java.sql.ResultSet rs = stmt.executeQuery
				("SELECT * FROM BondValData order by Ticker, Maturity");

			int i = 0;

			while (rs.next()) {
				org.drip.product.credit.BondComponent bond = BuildBondFromResultSet (rs, mpc);

				if (null == bond || bond.marketConvention().firstSettleDate() >=
					bond.maturityDate().julian())
					continue;

				bw.write ("\t\torg.drip.product.credit.Bond bond" + bond.isin() +
					" = new org.drip.product.credit.Bond();\n\n");

				if (null != bond.treasuryBenchmark()) {
					String strPrimaryBmk = "";

					if (null != bond.treasuryBenchmark()) strPrimaryBmk = bond.treasuryBenchmark().primary();

					bw.write ("\t\tbond" + bond.isin() + ".setTSYParams (CreateTSYParams (\"" +
						strPrimaryBmk + "\", \"NONE\", \"NONE\"));\n\n");
				}

				bw.write ("\t\tmapBondCache.add (\"" + bond.isin() + "\", bond" + bond.isin() + ");\n\n");

				bw.write ("\t\tmapBondCache.add (\"" + bond.cusip() + "\", bond" + bond.isin() + ");\n\n");

				System.out.println (i++);
			}

			bw.write ("\t\treturn mapBondCache;\n");

			bw.write ("\t}\n");

			bw.write ("}\n");

			bw.close();

			return true;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static final void main (
		final String[] astrArgs)
	{
		org.drip.param.definition.ScenarioMarketParams mpc =
			org.drip.param.creator.MarketParamsBuilder.CreateMarketParams();

		java.sql.Statement stmt = org.drip.service.env.EnvManager.InitEnv
			("c:\\Lakshmi\\BondAnal\\Config.xml");

		RatesManager.LoadFullIRCurves (mpc, stmt, org.drip.analytics.date.DateUtil.CreateFromYMD (2010, 12,
			3));

		GenerateBondCreatorFile (mpc, stmt);
	}
}
