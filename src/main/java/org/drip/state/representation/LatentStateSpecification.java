
package org.drip.state.representation;

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
 * LatentStateSpecification holds the fields necessary to specify a complete Latent State. It includes the
 * 	Latent State Type, the Latent State Label, and the Latent State Quantification metric.
 *
 * @author Lakshmi Krishnamurthy
 */

public class LatentStateSpecification {
	private String _strLatentState = "";
	private org.drip.state.identifier.LatentStateLabel _label = null;
	private String _strLatentStateQuantificationMetric = "";

	/**
	 * LatentStateSpecification constructor
	 * 
	 * @param strLatentState The Latent State
	 * @param strLatentStateQuantificationMetric The Latent State Quantification Metric
	 * @param label The Specific Latent State Label
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public LatentStateSpecification (
		final String strLatentState,
		final String strLatentStateQuantificationMetric,
		final org.drip.state.identifier.LatentStateLabel label)
		throws java.lang.Exception
	{
		if (null == (_strLatentState = strLatentState) || _strLatentState.isEmpty() || null ==
			(_strLatentStateQuantificationMetric = strLatentStateQuantificationMetric) ||
				_strLatentStateQuantificationMetric.isEmpty() || null == (_label = label))
			throw new java.lang.Exception ("LatentStateSpecification ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the Latent State
	 * 
	 * @return The Latent State
	 */

	public String latentState()
	{
		return _strLatentState;
	}

	/**
	 * Retrieve the Latent State Label
	 * 
	 * @return The Latent State Label
	 */

	public org.drip.state.identifier.LatentStateLabel label()
	{
		return _label;
	}

	/**
	 * Retrieve the Latent State Quantification Metric
	 * 
	 * @return The Latent State Quantification Metric
	 */

	public String latentStateQuantificationMetric()
	{
		return _strLatentStateQuantificationMetric;
	}

	/**
	 * Does the Specified Latent State Specification Instance match the current one?
	 * 
	 * @param lssOther The "Other" Latent State Specification Instance
	 * 
	 * @return TRUE => Matches the Specified Latent State Specification Instance
	 */

	public boolean match (
		final LatentStateSpecification lssOther)
	{
		return null == lssOther ? false : _strLatentState.equalsIgnoreCase (lssOther.latentState()) &&
			_strLatentStateQuantificationMetric.equalsIgnoreCase (lssOther.latentStateQuantificationMetric())
				&& _label.match (lssOther.label());
	}

	/**
	 * Display the Latent State Details
	 * 
	 * @param strComment The Comment Prefix
	 */

	public void displayString (
		final String strComment)
	{
		System.out.println ("\t[LatentStateSpecification]: " + _strLatentState + " | " +
			_strLatentStateQuantificationMetric + " | " + _label.fullyQualifiedName());
	}
}
