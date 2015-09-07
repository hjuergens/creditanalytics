
package org.drip.analytics.input;

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
 * CurveConstructionInputSet interface contains the Parameters needed for the Curve Calibration/Estimation.
 *  It's methods expose access to the following:
 *  - Calibration Valuation Parameters
 *  - Calibration Quoting Parameters
 *  - Array of Calibration Instruments
 *  - Map of Calibration Quotes
 *  - Map of Calibration Measures
 *  - Double Map of the Date/Index Fixings
 *
 * @author Lakshmi Krishnamurthy
 */

public interface CurveConstructionInputSet {

	/**
	 * Retrieve the Valuation Parameter
	 * 
	 * @return The Valuation Parameter
	 */

	public abstract org.drip.param.valuation.ValuationParams valuationParameter();

	/**
	 * Retrieve the Market Parameters
	 * 
	 * @return The Market Parameters
	 */

	public abstract org.drip.param.market.CurveSurfaceQuoteSet marketParameters();

	/**
	 * Retrieve the Pricer Parameters
	 * 
	 * @return The Pricer Parameters
	 */

	public abstract org.drip.param.pricer.CreditPricerParams pricerParameter();

	/**
	 * Retrieve the Quoting Parameter
	 * 
	 * @return The Quoting Parameter
	 */

	public abstract org.drip.param.valuation.ValuationCustomizationParams quotingParameter();

	/**
	 * Retrieve the Array of the Calibration Components
	 * 
	 * @return The Array of the Calibration Components
	 */

	public abstract org.drip.product.definition.CalibratableFixedIncomeComponent[] components();

	/**
	 * Retrieve the Calibration Quote Map
	 * 
	 * @return The Calibration Quote Map
	 */

	public abstract
		org.drip.analytics.support.CaseInsensitiveTreeMap<org.drip.analytics.support.CaseInsensitiveTreeMap<java.lang.Double>>
			quoteMap();

	/**
	 * Retrieve the Map containing the array of the Calibration Measures
	 * 
	 * @return The Map containing the array of the Calibration Measures
	 */

	public abstract org.drip.analytics.support.CaseInsensitiveTreeMap<String[]> measures();

	/**
	 * Retrieve the Latent State Fixings Container
	 * 
	 * @return The Latent State Fixings Container
	 */

	public abstract org.drip.param.market.LatentStateFixingsContainer fixing();
}
