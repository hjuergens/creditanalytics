
package org.drip.state.identifier;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
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
 * GovvieLabel contains the Identifier Parameters referencing the Latent State of the named Sovereign Curve.
 *  Currently it only contains the Sovereign Name.
 *  
 * @author Lakshmi Krishnamurthy
 */

public class GovvieLabel implements org.drip.state.identifier.LatentStateLabel {
	private java.lang.String _strSovereign = "";

	/**
	 * Make a Standard Govvie Label from the Sovereign Entity
	 * 
	 * @param strSovereign The Sovereign Entity
	 * 
	 * @return The Govvie Label
	 */

	public static final GovvieLabel Standard (
		final java.lang.String strSovereign)
	{
		try {
			return new GovvieLabel (strSovereign);
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * GovvieLabel constructor
	 * 
	 * @param strSovereign The Sovereign Entity
	 * 
	 * @throws java.lang.Exception Thrown if the inputs are invalid
	 */

	private GovvieLabel (
		final java.lang.String strSovereign)
		throws java.lang.Exception
	{
		if (null == (_strSovereign = strSovereign) || _strSovereign.isEmpty())
			throw new java.lang.Exception ("GovvieLabel ctr: Invalid Inputs");
	}

	@Override public java.lang.String fullyQualifiedName()
	{
		return _strSovereign;
	}

	@Override public boolean match (
		final org.drip.state.identifier.LatentStateLabel lslOther)
	{
		return null == lslOther || !(lslOther instanceof org.drip.state.identifier.GovvieLabel) ? false :
			_strSovereign.equalsIgnoreCase (lslOther.fullyQualifiedName());
	}
}
