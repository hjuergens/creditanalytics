
package org.drip.dynamics.evolution;

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
 * LSQMCurveIncrement contains the Increment of the Evolving Term Structure of the Latent State
 *  Quantification Metrics.
 *
 * @author Lakshmi Krishnamurthy
 */

public class LSQMCurveIncrement {
	private java.util.Map<java.lang.String, java.util.Map<java.lang.String, org.drip.spline.grid.Span>>
		_mmIncrement = new java.util.HashMap<java.lang.String, java.util.Map<java.lang.String,
			org.drip.spline.grid.Span>>();

	/**
	 * Empty LSQMCurveIncrement Constructor
	 */

	public LSQMCurveIncrement()
	{
	}

	/**
	 * Retrieve the Latent State Labels
	 * 
	 * @return The Latent State Labels
	 */

	public java.util.Set<java.lang.String> latentStateLabel()
	{
		return _mmIncrement.keySet();
	}

	/**
	 * Indicate if Quantification Metrics are available for the specified Latent State
	 * 
	 * @param lsl The Latent State Label
	 * 
	 * @return TRUE => Quantification Metrics are available for the specified Latent State
	 */

	public boolean containsLatentState (
		final org.drip.state.identifier.LatentStateLabel lsl)
	{
		return null == lsl ? false : _mmIncrement.containsKey (lsl.fullyQualifiedName());
	}

	/**
	 * Indicate if the Value for the specified Quantification Metric is available
	 * 
	 * @param lsl The Latent State Label
	 * @param strQM The Quantification Metric
	 * 
	 * @return TRUE => The Requested Value is available
	 */

	public boolean containsQM (
		final org.drip.state.identifier.LatentStateLabel lsl,
		final java.lang.String strQM)
	{
		if (null == lsl || null == strQM || strQM.isEmpty()) return false;

		java.lang.String strLabel = lsl.fullyQualifiedName();

		return _mmIncrement.containsKey (strLabel) && _mmIncrement.get (strLabel).containsKey (strQM);
	}

	/**
	 * Set the LSQM Increment Span
	 * 
	 * @param lsl The Latent State Label
	 * @param strQM The Quantification Metric
	 * @param spanIncrement The Increment Span
	 * 
	 * @return TRUE => The LSQM Increment Span successfully set
	 */

	public boolean setQMSpan (
		final org.drip.state.identifier.LatentStateLabel lsl,
		final java.lang.String strQM,
		final org.drip.spline.grid.Span spanIncrement)
	{
		if (null == lsl || null == strQM || strQM.isEmpty() || null == spanIncrement) return false;

		java.lang.String strLabel = lsl.fullyQualifiedName();

		java.util.Map<java.lang.String, org.drip.spline.grid.Span> mapSpanIncrement =
			_mmIncrement.containsKey (strLabel) ? _mmIncrement.get (strLabel) : new
				java.util.HashMap<java.lang.String, org.drip.spline.grid.Span>();

		mapSpanIncrement.put (strQM, spanIncrement);

		_mmIncrement.put (strLabel, mapSpanIncrement);

		return true;
	}

	/**
	 * Retrieve the specified Latent State Quantification Metric Span Increment
	 * 
	 * @param lsl The Latent State Label
	 * @param strQM The Quantification Metric
	 * 
	 * @return The Latent State Quantification Metric Span Increment
	 */

	public org.drip.spline.grid.Span span (
		final org.drip.state.identifier.LatentStateLabel lsl,
		final java.lang.String strQM)
	{
		if (null == lsl || null == strQM || strQM.isEmpty()) return null;

		java.lang.String strLabel = lsl.fullyQualifiedName();

		java.util.Map<java.lang.String, org.drip.spline.grid.Span> mapSpanIncrement = _mmIncrement.get
			(strLabel);

		return mapSpanIncrement.containsKey (strQM) ? mapSpanIncrement.get (strQM) : null;
	}
}
