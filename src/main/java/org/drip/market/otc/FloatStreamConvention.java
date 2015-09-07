
package org.drip.market.otc;

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
 * FloatStreamConvention contains the details of the Floating Stream of an OTC IBOR/Overnight fix-float Swap
 *  Contract.
 *
 * @author Lakshmi Krishnamurthy
 */

public class FloatStreamConvention {
	private String _strCompositePeriodTenor = "";
	private org.drip.state.identifier.ForwardLabel _forwardLabel = null;

	/**
	 * FloatStreamConvention Constructor
	 * 
	 * @param forwardLabel The Forward Label
	 * @param strCompositePeriodTenor Composite Period Tenor
	 * 
	 * @throws java.lang.Exception Thrown if Inputs are invalid
	 */

	public FloatStreamConvention (
		final org.drip.state.identifier.ForwardLabel forwardLabel,
		final String strCompositePeriodTenor)
		throws java.lang.Exception
	{
		if (null == (_forwardLabel = forwardLabel) || null == (_strCompositePeriodTenor =
			strCompositePeriodTenor) || _strCompositePeriodTenor.isEmpty())
			throw new java.lang.Exception ("FloatStreamConvention ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the Forward Label
	 * 
	 * @return The Forward Label
	 */

	public org.drip.state.identifier.ForwardLabel floaterIndex()
	{
		return _forwardLabel;
	}

	/**
	 * Retrieve the Composite Period Tenor
	 * 
	 * @return The Composite Period Tenor
	 */

	public String compositePeriodTenor()
	{
		return _strCompositePeriodTenor;
	}

	/**
	 * Create a Floating Stream Instance
	 * 
	 * @param dtEffective Effective Date
	 * @param strMaturityTenor Maturity Tenor
	 * @param dblBasis Basis
	 * @param dblNotional Notional
	 * 
	 * @return The Fixed Stream Instance
	 */

	public org.drip.product.rates.Stream createStream (
		final org.drip.analytics.date.JulianDate dtEffective,
		final String strMaturityTenor,
		final double dblBasis,
		final double dblNotional)
	{
		boolean bOvernight = _forwardLabel.overnight();

		try {
			org.drip.param.period.ComposableFloatingUnitSetting cfus = new
				org.drip.param.period.ComposableFloatingUnitSetting (bOvernight ? "ON" :
					_forwardLabel.tenor(), bOvernight ?
						org.drip.analytics.support.CompositePeriodBuilder.EDGE_DATE_SEQUENCE_OVERNIGHT :
							org.drip.analytics.support.CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
								null, _forwardLabel,
									org.drip.analytics.support.CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
				dblBasis);

			org.drip.param.period.CompositePeriodSetting cps = new
				org.drip.param.period.CompositePeriodSetting (bOvernight ? 360 :
					org.drip.analytics.support.AnalyticsHelper.TenorToFreq (_strCompositePeriodTenor),
						_strCompositePeriodTenor, _forwardLabel.currency(), null, dblNotional, null, null,
							null, null);

			java.util.List<java.lang.Double> lsEdgeDate = bOvernight ? 
				org.drip.analytics.support.CompositePeriodBuilder.OvernightEdgeDates (dtEffective,
					dtEffective.addTenor (strMaturityTenor), null):
						org.drip.analytics.support.CompositePeriodBuilder.RegularEdgeDates (dtEffective,
							_strCompositePeriodTenor, strMaturityTenor, null);

			return new org.drip.product.rates.Stream
				(org.drip.analytics.support.CompositePeriodBuilder.FloatingCompositeUnit (lsEdgeDate, cps,
					cfus));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override public String toString()
	{
		return "[FLOAT: " + _forwardLabel.fullyQualifiedName() + " | " + _strCompositePeriodTenor + "]";
	}
}
