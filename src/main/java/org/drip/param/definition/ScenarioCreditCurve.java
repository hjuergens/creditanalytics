
package org.drip.param.definition;

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
 * ScenarioCreditCurve abstract class exposes the bump parameters and the curves for the following credit
 *  curve scenarios:
 *  - Base, Flat Spread/Recovery bumps
 *	- Spread/Recovery Tenor bumped up/down credit curve sets keyed using the tenor.
 *	- NTP-based custom scenario curves.
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class ScenarioCreditCurve {

	/**
	 * CC Scenario Base
	 */

	public static final int CC_BASE = 0;

	/**
	 * CC Scenario Parallel Up
	 */

	public static final int CC_FLAT_UP = 1;

	/**
	 * CC Scenario Parallel Down
	 */

	public static final int CC_FLAT_DN = 2;

	/**
	 * CC Scenario Tenor Up
	 */

	public static final int CC_TENOR_UP = 4;

	/**
	 * CC Scenario Tenor Down
	 */

	public static final int CC_TENOR_DN = 8;

	/**
	 * CC Scenario Recovery Parallel Up
	 */

	public static final int CC_RR_FLAT_UP = 16;

	/**
	 * CC Scenario Recovery Parallel Down
	 */

	public static final int CC_RR_FLAT_DN = 32;

	/**
	 * Cook and save the credit curves corresponding to the scenario specified
	 * 
	 * @param strName Credit Curve Name
	 * @param valParams ValuationParams
	 * @param dc Base Discount Curve
	 * @param dcTSY Treasury Discount Curve
	 * @param astrCalibMeasure Matched array of Calibration measures
	 * @param adblQuote Matched array of Quotes
	 * @param dblRecovery Curve Recovery
	 * @param lsfc Latent State Fixings Container
	 * @param vcp Valuation Customization Parameters
	 * @param bFlat Whether the calibration is to a flat curve
	 * @param iScenario One of the values in the CC_ enum listed above. 
	 * 
	 * @return Success (true), failure (false)
	 */

	public abstract boolean cookScenarioCC (
		final java.lang.String strName,
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.analytics.rates.DiscountCurve dc,
		final org.drip.analytics.rates.DiscountCurve dcTSY,
		final java.lang.String[] astrCalibMeasure,
		final double[] adblQuote,
		final double dblRecovery,
		final org.drip.param.market.LatentStateFixingsContainer lsfc,
		final org.drip.param.valuation.ValuationCustomizationParams vcp,
		final boolean bFlat,
		final int iScenario);

	/**
	 * Cook the credit curve according to the desired tweak parameters
	 * 
	 * @param strName Scenario Credit Curve Name
	 * @param strCustomName Scenario Name
	 * @param valParams Valuation Parameters
	 * @param dc Discount Curve
	 * @param dcTSY TSY Discount Curve
	 * @param astrCalibMeasure Array of calibration measures
	 * @param adblQuote Double array of input quotes
	 * @param dblRecovery Recovery Rate
	 * @param lsfc Latent State Fixings Container
	 * @param vcp Valuation Customization Parameters
	 * @param bFlat Whether the calibration is flat
	 * @param rvtpDC Node Tweak Parameters for the Base Discount Curve
	 * @param rvtpTSY Node Tweak Parameters for the TSY Discount Curve
	 * @param rvtpCC Node Tweak Parameters for the Credit Curve
	 * 
	 * @return True => Credit Curve successfully created
	 */

	public abstract boolean cookCustomCC (
		final java.lang.String strName,
		final java.lang.String strCustomName,
		final org.drip.param.valuation.ValuationParams valParams,
		final org.drip.analytics.rates.DiscountCurve dc,
		final org.drip.analytics.rates.DiscountCurve dcTSY,
		final java.lang.String[] astrCalibMeasure,
		final double[] adblQuote,
		final double dblRecovery,
		final org.drip.param.market.LatentStateFixingsContainer lsfc,
		final org.drip.param.valuation.ValuationCustomizationParams vcp,
		final boolean bFlat,
		final org.drip.param.definition.ResponseValueTweakParams rvtpDC,
		final org.drip.param.definition.ResponseValueTweakParams rvtpTSY,
		final org.drip.param.definition.ResponseValueTweakParams rvtpCC);

	/**
	 * Return the base credit curve
	 * 
	 * @return The base credit curve
	 */

	public abstract org.drip.analytics.definition.CreditCurve base();

	/**
	 * Return the bump up credit curve
	 * 
	 * @return The Bumped up credit curve
	 */

	public abstract org.drip.analytics.definition.CreditCurve bumpUp();

	/**
	 * Return the bump down credit curve
	 * 
	 * @return The Bumped down credit curve
	 */

	public abstract org.drip.analytics.definition.CreditCurve bumpDown();

	/**
	 * Return the recovery bump up credit curve
	 * 
	 * @return The Recovery Bumped up credit curve
	 */

	public abstract org.drip.analytics.definition.CreditCurve bumpRecoveryUp();

	/**
	 * Return the recovery bump down credit curve
	 * 
	 * @return The Recovery Bumped Down credit curve
	 */

	public abstract org.drip.analytics.definition.CreditCurve bumpRecoveryDown();

	/**
	 * Return the tenor bump up credit curve map
	 * 
	 * @return The Tenor Bumped up credit curve Map
	 */

	public abstract
		org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.definition.CreditCurve>
			tenorBumpUp();

	/**
	 * Return the tenor bump down credit curve map
	 * 
	 * @return The Tenor Bumped Down credit curve Map
	 */

	public abstract
		org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.definition.CreditCurve>
			tenorBumpDown();
}
