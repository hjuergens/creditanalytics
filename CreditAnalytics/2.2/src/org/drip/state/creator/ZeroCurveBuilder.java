
package org.drip.state.creator;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
 * Copyright (C) 2012 Lakshmi Krishnamurthy
 * 
 * This file is part of CreditAnalytics, a free-software/open-source library for fixed income analysts and
 * 		developers - http://www.credit-trader.org
 * 
 * CreditAnalytics is a free, full featured, fixed income credit analytics library, developed with a special
 * 		focus towards the needs of the bonds and credit products community.
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
 * This class contains the baseline zero curve builder object. It contains static functions that build
 * 		zero curves from cash flows, discount curves, and other input curves/instruments.
 *
 * @author Lakshmi Krishnamurthy
 */

/**
 * This class contains the builder functions that construct the zero curve instance. It contains static
 *  functions that build different types of zero curve from 2 major types of inputs:
 *  - From a source discount curve, a set of coupon periods, and the Zero Bump
 *  - From a serialized byte stream of the Zero curve instance
 *
 * @author Lakshmi Krishnamurthy
 */

public class ZeroCurveBuilder {

	/**
	 * ZeroCurve constructor from period, work-out, settle, and quoting parameters
	 * 
	 * @param iFreqZC Zero Curve Frequency
	 * @param strDCZC Zero Curve Day Count
	 * @param strCalendarZC Zero Curve Calendar
	 * @param bApplyEOMAdjZC Zero Coupon EOM Adjustment Flag
	 * @param lsCouponPeriod List of bond coupon periods
	 * @param dblWorkoutDate Work-out date
	 * @param dblCashPayDate Cash-Pay Date
	 * @param dc Discount Curve
	 * @param quotingParams Quoting Parameters
	 * @param dblZCBump DC Bump
	 * 
	 * @throws The new Zero Curve instance
	 */

	public static final org.drip.analytics.rates.ZeroCurve CreateZeroCurve (
		final int iFreqZC,
		final java.lang.String strDCZC,
		final java.lang.String strCalendarZC,
		final boolean bApplyEOMAdjZC,
		final java.util.List<org.drip.analytics.period.CashflowPeriod> lsCouponPeriod,
		final double dblWorkoutDate,
		final double dblCashPayDate,
		final org.drip.analytics.rates.DiscountCurve dc,
		final org.drip.param.valuation.QuotingParams quotingParams,
		final double dblZCBump)
	{
		try {
			return new org.drip.state.curve.DerivedZeroRate (iFreqZC, strDCZC, strCalendarZC,
				bApplyEOMAdjZC, lsCouponPeriod, dblWorkoutDate, dblCashPayDate, dc, quotingParams, dblZCBump);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

	return null;
	}

	/**
	 * Create a Zero curve instance from the byte array
	 * 
	 * @param ab Byte Array
	 * 
	 * @return Zero Curve Instance
	 */

	public static final org.drip.analytics.rates.ZeroCurve FromByteArray (
		final byte[] ab)
	{
		if (null == ab || 0 == ab.length) return null;

		try {
			return new org.drip.state.curve.DerivedZeroRate (ab);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
