
package org.drip.analytics.daycount;

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
 * This class holds the applicable adjustments for a given date pair. It exposes the following functionality:
 * 	- Static Methods for creating 30/360, 30/365, and EOMA Date Adjustments
 * 	- Export Anterior and Posterior EOM Adjustments
 *
 * @author Lakshmi Krishnamurthy
 */

public class DateEOMAdjustment {
	private int _iD1Adj = 0;
	private int _iD2Adj = 0;

	/**
	 * Construct a DateEOMAdjustment instance for the 30/365 day count
	 * 
	 * @param dblStart Start Date
	 * @param dblEnd End Date
	 * @param bApplyEOMAdj TRUE => Apply EOM Adjustment
	 * 
	 * @return DateEOMAdjustment instance
	 */

	public static final DateEOMAdjustment MakeDEOMA30_365 (
		final double dblStart,
		final double dblEnd,
		final boolean bApplyEOMAdj)
	{
		DateEOMAdjustment dm = new DateEOMAdjustment();

		if (!bApplyEOMAdj) return dm;

		if (!org.drip.quant.common.NumberUtil.IsValid (dblStart) || !org.drip.quant.common.NumberUtil.IsValid
			(dblEnd))
			return null;

		try {
			if (org.drip.analytics.date.DateUtil.FEBRUARY == org.drip.analytics.date.DateUtil.Month
				(dblStart) && org.drip.analytics.date.DateUtil.IsEOM (dblStart) &&
					org.drip.analytics.date.DateUtil.FEBRUARY == org.drip.analytics.date.DateUtil.Month
						(dblEnd) && org.drip.analytics.date.DateUtil.IsEOM (dblEnd))
				dm._iD2Adj = (28 == org.drip.analytics.date.DateUtil.DaysInMonth
					(org.drip.analytics.date.DateUtil.Month (dblEnd),
						org.drip.analytics.date.DateUtil.Year (dblEnd)) ? 2 : 1);

			if (org.drip.analytics.date.DateUtil.FEBRUARY == org.drip.analytics.date.DateUtil.Month
				(dblStart) && org.drip.analytics.date.DateUtil.IsEOM (dblStart))
				dm._iD1Adj = (28 == org.drip.analytics.date.DateUtil.DaysInMonth
					(org.drip.analytics.date.DateUtil.Month (dblStart), org.drip.analytics.date.DateUtil.Year
						(dblStart)) ? 2 : 1);

			if (31 == org.drip.analytics.date.DateUtil.Day (dblEnd) + dm._iD2Adj && (30 ==
				org.drip.analytics.date.DateUtil.Day (dblStart) + dm._iD1Adj || 31 ==
					org.drip.analytics.date.DateUtil.Day (dblStart) + dm._iD1Adj))
				dm._iD2Adj -= 1;

			if (31 == org.drip.analytics.date.DateUtil.Day (dblStart) + dm._iD1Adj) dm._iD1Adj -= 1;

			return dm;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct a DateEOMAdjustment instance for the 30/360 day count
	 * 
	 * @param dblStart Start Date
	 * @param dblEnd End Date
	 * @param bApplyEOMAdj TRUE => Apply EOM Adjustment
	 * 
	 * @return DateEOMAdjustment instance
	 */

	public static final DateEOMAdjustment MakeDEOMA30_360 (
		final double dblStart,
		final double dblEnd,
		final boolean bApplyEOMAdj)
	{
		DateEOMAdjustment dm = new DateEOMAdjustment();

		if (!bApplyEOMAdj) return dm;

		if (!org.drip.quant.common.NumberUtil.IsValid (dblStart) || !org.drip.quant.common.NumberUtil.IsValid
			(dblEnd))
			return null;

		try {
			if (31 == org.drip.analytics.date.DateUtil.Day (dblStart)) dm._iD1Adj -= 1;

			if (!org.drip.analytics.date.DateUtil.IsLeapYear (dblStart)) {
				if (org.drip.analytics.date.DateUtil.FEBRUARY == org.drip.analytics.date.DateUtil.Month
					(dblStart) && 28 == org.drip.analytics.date.DateUtil.Day (dblStart))
					dm._iD1Adj += 2;
			} else {
				if (org.drip.analytics.date.DateUtil.FEBRUARY == org.drip.analytics.date.DateUtil.Month
					(dblStart) && 29 == org.drip.analytics.date.DateUtil.Day (dblStart))
					dm._iD1Adj += 1;
			}

			if (31 == org.drip.analytics.date.DateUtil.Day (dblEnd) && (30 ==
				org.drip.analytics.date.DateUtil.Day (dblStart) || 31 ==
					org.drip.analytics.date.DateUtil.Day (dblStart)))
				dm._iD2Adj -= 1;

			return dm;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct a DateEOMAdjustment instance for all other day counts
	 * 
	 * @param dblStart Start Date
	 * @param dblEnd End Date
	 * @param bApplyEOMAdj TRUE => Apply EOM Adjustment
	 * 
	 * @return DateEOMAdjustment instance
	 */

	public static final DateEOMAdjustment MakeDEOMA (
		final double dblStart,
		final double dblEnd,
		final boolean bApplyEOMAdj)
	{
		DateEOMAdjustment dm = new DateEOMAdjustment();

		if (!bApplyEOMAdj) return dm;

		if (!org.drip.quant.common.NumberUtil.IsValid (dblStart) || !org.drip.quant.common.NumberUtil.IsValid
			(dblEnd))
			return null;

		try {
			if (bApplyEOMAdj) {
				if (org.drip.analytics.date.DateUtil.IsEOM (dblStart))
					dm._iD1Adj = 30 - org.drip.analytics.date.DateUtil.Day (dblStart);

				if (org.drip.analytics.date.DateUtil.IsEOM (dblEnd) &&
					(org.drip.analytics.date.DateUtil.FEBRUARY != org.drip.analytics.date.DateUtil.Month
						(dblEnd)))
					dm._iD2Adj = 30 - org.drip.analytics.date.DateUtil.Day (dblEnd);
			}

			return dm;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct a DateEOMAdjustment instance for the 30E/360 day-count
	 * 
	 * @param dblStart Start Date
	 * @param dblEnd End Date
	 * @param bApplyEOMAdj TRUE => Apply EOM Adjustment
	 * 
	 * @return DateEOMAdjustment instance
	 */

	public static final DateEOMAdjustment MakeDEOMA30E_360 (
		final double dblStart,
		final double dblEnd,
		final boolean bApplyEOMAdj)
	{
		DateEOMAdjustment dm = new DateEOMAdjustment();

		if (!bApplyEOMAdj) return dm;

		if (!org.drip.quant.common.NumberUtil.IsValid (dblStart) || !org.drip.quant.common.NumberUtil.IsValid
			(dblEnd))
			return null;

		try {
			if (bApplyEOMAdj) {
				if (31 == org.drip.analytics.date.DateUtil.Day (dblStart)) dm._iD1Adj = -1;

				if (31 == org.drip.analytics.date.DateUtil.Day (dblEnd)) dm._iD2Adj = -1;
			}

			return dm;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct a DateEOMAdjustment instance for the 30E/360 ISDA day-count
	 * 
	 * @param dblStart Start Date
	 * @param dblEnd End Date
	 * @param bApplyEOMAdj TRUE => Apply EOM Adjustment
	 * 
	 * @return DateEOMAdjustment instance
	 */

	public static final DateEOMAdjustment MakeDEOMA30E_360_ISDA (
		final double dblStart,
		final double dblEnd,
		final boolean bApplyEOMAdj)
	{
		DateEOMAdjustment dm = new DateEOMAdjustment();

		if (!bApplyEOMAdj) return dm;

		if (!org.drip.quant.common.NumberUtil.IsValid (dblStart) || !org.drip.quant.common.NumberUtil.IsValid
			(dblEnd))
			return null;

		try {
			if (bApplyEOMAdj) {
				if (org.drip.analytics.date.DateUtil.IsEOM (dblStart))
					dm._iD1Adj = 30 - org.drip.analytics.date.DateUtil.Day (dblStart);

				if (org.drip.analytics.date.DateUtil.IsEOM (dblEnd))
					dm._iD2Adj = 30 - org.drip.analytics.date.DateUtil.Day (dblEnd);
			}

			return dm;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct a DateEOMAdjustment instance for the 30E+/360 ISDA day-count
	 * 
	 * @param dblStart Start Date
	 * @param dblEnd End Date
	 * @param bApplyEOMAdj TRUE => Apply EOM Adjustment
	 * 
	 * @return DateEOMAdjustment instance
	 */

	public static final DateEOMAdjustment MakeDEOMA30EPLUS_360_ISDA (
		final double dblStart,
		final double dblEnd,
		final boolean bApplyEOMAdj)
	{
		DateEOMAdjustment dm = new DateEOMAdjustment();

		if (!bApplyEOMAdj) return dm;

		if (!org.drip.quant.common.NumberUtil.IsValid (dblStart) || !org.drip.quant.common.NumberUtil.IsValid
			(dblEnd))
			return null;

		try {
			if (bApplyEOMAdj) {
				if (31 == org.drip.analytics.date.DateUtil.Day (dblStart)) dm._iD1Adj = -1;

				if (31 == org.drip.analytics.date.DateUtil.Day (dblStart)) dm._iD2Adj = +1;
			}

			return dm;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve the Anterior Date Adjustment
	 * 
	 * @return The Anterior Date Adjustment
	 */

	public int anterior()
	{
		return _iD1Adj;
	}

	/**
	 * Retrieve the Posterior Date Adjustment
	 * 
	 * @return The Posterior Date Adjustment
	 */

	public int posterior()
	{
		return _iD2Adj;
	}
}
