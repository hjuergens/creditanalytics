
package org.drip.service.api;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
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
 * InstrMetric contains the fields that hold the result of the PnL metric calculations.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class InstrMetric {
	private org.drip.service.api.ForwardRates _fwdMetric = null;
	private org.drip.service.api.ProductDailyPnL _pnlMetric = null;

	/**
	 * InstrMetric constructor
	 * 
	 * @param fwdMetric The Forward Rates Metric
	 * @param pnlMetric The Daily Carry/Roll PnL Metric
	 */

	public InstrMetric (
		final org.drip.service.api.ForwardRates fwdMetric,
		final org.drip.service.api.ProductDailyPnL pnlMetric)
		throws java.lang.Exception
	{
		if (null == (_fwdMetric = fwdMetric) || null == (_pnlMetric = pnlMetric))
			throw new java.lang.Exception ("InstrMetric ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the Forward Metric
	 * 
	 * @return The Forward Metric
	 */

	public org.drip.service.api.ForwardRates fwdMetric()
	{
		return _fwdMetric;
	}

	/**
	 * Retrieve the PnL Metric
	 * 
	 * @return The PnL Metric
	 */

	public org.drip.service.api.ProductDailyPnL pnlMetric()
	{
		return _pnlMetric;
	}

	/**
	 * Reduce the PnL/forward metrics to an array
	 * 
	 * @return The Array containing the PnL/forward metrics
	 */

	public double[] toArray()
	{
		double[] adblPnLMetric = _pnlMetric.toArray();

		double[] adblFwdMetric = _fwdMetric.toArray();

		int i = 0;
		double[] adblInstrMetric = new double[adblFwdMetric.length + adblPnLMetric.length];

		for (double dbl : adblPnLMetric)
			adblInstrMetric[i++] = dbl;

		for (double dbl : adblFwdMetric)
			adblInstrMetric[i++] = dbl;

		return adblInstrMetric;
	}

	@Override public java.lang.String toString()
	{
		java.lang.StringBuffer sb = new java.lang.StringBuffer();

		boolean bStart = true;

		for (double dbl : toArray()) {
			if (bStart)
				bStart = false;
			else
				sb.append (",");

			sb.append (dbl);
		}

		return sb.toString();
	}
}
