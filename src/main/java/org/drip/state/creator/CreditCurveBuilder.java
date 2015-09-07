
package org.drip.state.creator;

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
 * This class contains the builder functions that construct the credit curve (comprising both survival and
 * 	recovery) instance. It contains static functions that build different types of credit curve from 3 major
 *  types of inputs:
 * 	- From a variety of ordered credit-sensitive calibration instruments and their quotes
 *  - From an array of ordered survival probabilities
 *  - From a serialized byte stream of the credit curve instance
 *
 * @author Lakshmi Krishnamurthy
 */

public class CreditCurveBuilder {

	/**
	 * Create a CreditCurve instance from a single node hazard rate
	 * 
	 * @param dblStartDate Curve epoch date
	 * @param strName Credit Curve Name
	 * @param strCurrency Currency
	 * @param dblHazardRate Curve hazard rate
	 * @param dblRecovery Curve recovery
	 * 
	 * @return CreditCurve instance
	 */

	public static final org.drip.analytics.definition.ExplicitBootCreditCurve FromFlatHazard (
		final double dblStartDate,
		final String strName,
		final String strCurrency,
		final double dblHazardRate,
		final double dblRecovery)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblStartDate) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblHazardRate) ||
				!org.drip.quant.common.NumberUtil.IsValid (dblRecovery))
			return null;

		double[] adblHazard = new double[1];
		double[] adblRecovery = new double[1];
		double[] adblHazardDate = new double[1];
		double[] adblRecoveryDate = new double[1];
		adblHazard[0] = dblHazardRate;
		adblRecovery[0] = dblRecovery;
		adblHazardDate[0] = dblStartDate;
		adblRecoveryDate[0] = dblStartDate;

		try {
			return new org.drip.state.curve.ForwardHazardCreditCurve (dblStartDate,
				org.drip.state.identifier.CreditLabel.Standard (strName), strCurrency, adblHazard,
					adblHazardDate, adblRecovery, adblRecoveryDate, java.lang.Double.NaN);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Create a CreditCurve instance from the input array of survival probabilities
	 * 
	 * @param dblStartDate Start Date
	 * @param strName Credit Curve Name
	 * @param strCurrency Currency
	 * @param adblSurvivalDate Array of dates
	 * @param adblSurvivalProbability Array of survival probabilities
	 * @param dblRecovery Recovery
	 * 
	 * @return CreditCurve instance
	 */

	public static final org.drip.analytics.definition.ExplicitBootCreditCurve FromSurvival (
		final double dblStartDate,
		final String strName,
		final String strCurrency,
		final double[] adblSurvivalDate,
		final double[] adblSurvivalProbability,
		final double dblRecovery)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblRecovery)) return null;

		try {
			double dblSurvivalBegin = 1.;
			double dblPeriodBegin = dblStartDate;
			double[] adblHazard = new double[adblSurvivalProbability.length];
			double[] adblRecovery = new double[1];
			double[] adblRecoveryDate = new double[1];
			adblRecovery[0] = dblRecovery;
			adblRecoveryDate[0] = dblStartDate;

			for (int i = 0; i < adblSurvivalProbability.length; ++i) {
				if (!org.drip.quant.common.NumberUtil.IsValid (adblSurvivalProbability[i]) ||
					adblSurvivalDate[i] <= dblPeriodBegin || dblSurvivalBegin <= adblSurvivalProbability[i])
					return null;

				adblHazard[i] = 365.25 / (adblSurvivalDate[i] - dblPeriodBegin) * java.lang.Math.log
					(dblSurvivalBegin / adblSurvivalProbability[i]);

				dblPeriodBegin = adblSurvivalDate[i];
				dblSurvivalBegin = adblSurvivalProbability[i];
			}

			return new org.drip.state.curve.ForwardHazardCreditCurve (dblStartDate,
				org.drip.state.identifier.CreditLabel.Standard (strName), strCurrency, adblHazard,
					adblSurvivalDate, adblRecovery, adblRecoveryDate, java.lang.Double.NaN);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Create an instance of the CreditCurve object from a solitary hazard rate node
	 * 
	 * @param dblStartDate The Curve epoch date
	 * @param strName Credit Curve Name
	 * @param strCurrency Currency
	 * @param dblHazardRate The solo hazard rate
	 * @param dblHazardDate Date
	 * @param dblRecovery Recovery
	 * 
	 * @return CreditCurve instance
	 */

	public static final org.drip.analytics.definition.ExplicitBootCreditCurve FromHazardNode (
		final double dblStartDate,
		final String strName,
		final String strCurrency,
		final double dblHazardRate,
		final double dblHazardDate,
		final double dblRecovery)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblStartDate) ||
			!org.drip.quant.common.NumberUtil.IsValid (dblHazardRate) ||
				!org.drip.quant.common.NumberUtil.IsValid (dblHazardDate) ||
					!org.drip.quant.common.NumberUtil.IsValid (dblRecovery))
			return null;

		double[] adblHazard = new double[1];
		double[] adblRecovery = new double[1];
		double[] adblHazardDate = new double[1];
		double[] adblRecoveryDate = new double[1];
		adblHazard[0] = dblHazardRate;
		adblRecovery[0] = dblRecovery;
		adblHazardDate[0] = dblHazardDate;
		adblRecoveryDate[0] = dblStartDate;

		try {
			return new org.drip.state.curve.ForwardHazardCreditCurve (dblStartDate,
				org.drip.state.identifier.CreditLabel.Standard (strName), strCurrency, adblHazard,
					adblHazardDate, adblRecovery, adblRecoveryDate, java.lang.Double.NaN);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Create a credit curve from an array of dates and hazard rates
	 * 
	 * @param dtStart Curve epoch date
	 * @param strName Credit Curve Name
	 * @param strCurrency Currency
	 * @param adblDate Array of dates
	 * @param adblHazardRate Array of hazard rates
	 * @param dblRecovery Recovery
	 * 
	 * @return CreditCurve instance
	 */

	public static final org.drip.analytics.definition.ExplicitBootCreditCurve CreateCreditCurve (
		final org.drip.analytics.date.JulianDate dtStart,
		final String strName,
		final String strCurrency,
		final double[] adblDate,
		final double[] adblHazardRate,
		final double dblRecovery)
	{
		if (null == dtStart || null == adblHazardRate || null == adblDate || adblHazardRate.length !=
			adblDate.length || !org.drip.quant.common.NumberUtil.IsValid (dblRecovery))
			return null;

		try {
			double[] adblRecovery = new double[1];
			double[] adblRecoveryDate = new double[1];
			adblRecovery[0] = dblRecovery;

			adblRecoveryDate[0] = dtStart.julian();

			return new org.drip.state.curve.ForwardHazardCreditCurve (dtStart.julian(),
				org.drip.state.identifier.CreditLabel.Standard (strName), strCurrency, adblHazardRate,
					adblDate, adblRecovery, adblRecoveryDate, java.lang.Double.NaN);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Create a credit curve from hazard rate and recovery rate term structures
	 * 
	 * @param dblStart Curve Epoch date
	 * @param strName Credit Curve Name
	 * @param strCurrency Currency
	 * @param adblHazardRate Matched array of hazard rates
	 * @param adblHazardDate Matched array of hazard dates
	 * @param adblRecoveryRate Matched array of recovery rates
	 * @param adblRecoveryDate Matched array of recovery dates
	 * @param dblSpecificDefaultDate (Optional) Specific Default Date
	 * 
	 * @return CreditCurve instance
	 */

	public static final org.drip.analytics.definition.ExplicitBootCreditCurve CreateCreditCurve (
		final double dblStart,
		final String strName,
		final String strCurrency,
		final double adblHazardRate[],
		final double adblHazardDate[],
		final double[] adblRecoveryRate,
		final double[] adblRecoveryDate,
		final double dblSpecificDefaultDate)
	{
		try {
			return new org.drip.state.curve.ForwardHazardCreditCurve (dblStart,
				org.drip.state.identifier.CreditLabel.Standard (strName), strCurrency, adblHazardRate,
					adblHazardDate, adblRecoveryRate, adblRecoveryDate, dblSpecificDefaultDate);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
