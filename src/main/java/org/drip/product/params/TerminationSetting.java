
package org.drip.product.params;

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
 * TerminationSetting class contains the current "liveness" state of the component, and, if inactive, how it
 *  entered that state. It exports serialization into and de-serialization out of byte arrays.
 *
 * @author Lakshmi Krishnamurthy
 */

public class TerminationSetting implements org.drip.product.params.Validatable {
	private boolean _bIsDefaulted = false;
	private boolean _bIsPerpetual = false;
	private boolean _bHasBeenExercised = false;

	/**
	 * Construct the TerminationSetting object from the perpetual flag, defaulted flag, and the has
	 * 	been exercised flag.
	 * 
	 * @param bIsPerpetual True (component is perpetual)
	 * @param bIsDefaulted True (component has defaulted)
	 * @param bHasBeenExercised True (component has been exercised)
	 */

	public TerminationSetting (
		final boolean bIsPerpetual,
		final boolean bIsDefaulted,
		final boolean bHasBeenExercised)
	{
		_bIsPerpetual = bIsPerpetual;
		_bIsDefaulted = bIsDefaulted;
		_bHasBeenExercised = bHasBeenExercised;
	}

	@Override public boolean validate()
	{
		return true;
	}

	/**
	 * Indicate if the contract has defaulted
	 * 
	 * @return TRUE => The Contract has defaulted
	 */

	public boolean defaulted()
	{
		return _bIsDefaulted;
	}

	/**
	 * Indicate if the contract is perpetual
	 * 
	 * @return TRUE => The Contract is Perpetual
	 */

	public boolean perpetual()
	{
		return _bIsPerpetual;
	}

	/**
	 * Indicate if the contract has been exercised
	 * 
	 * @return TRUE => The Contract has been exercised
	 */

	public boolean exercised()
	{
		return _bHasBeenExercised;
	}
}
