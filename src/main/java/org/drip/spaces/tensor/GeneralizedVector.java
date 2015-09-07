
package org.drip.spaces.tensor;

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
 * GeneralizedVector exposes the basic Properties of the General Vector Space.
 *
 * @author Lakshmi Krishnamurthy
 */

public interface GeneralizedVector {

	/**
	 * Retrieve the Left Edge
	 * 
	 * @return The Left Edge
	 */

	public abstract double leftEdge();

	/**
	 * Retrieve the Right Edge
	 * 
	 * @return The Right Edge
	 */

	public abstract double rightEdge();

	/**
	 * Retrieve the Cardinality of the Vector Space
	 * 
	 * @return Cardinality of the Vector Space
	 */

	public abstract org.drip.spaces.tensor.Cardinality cardinality();

	/**
	 * Compare against the "Other" Generalized Vector Space
	 * 
	 * @param gvsOther The "Other" Generalized Vector Space
	 * 
	 * @return TRUE => The "Other" Generalized Vector Space matches this
	 */

	public abstract boolean match (
		final org.drip.spaces.tensor.GeneralizedVector gvsOther);

	/**
	 * Indicate if the "Other" Generalized Vector Space is a Subset of "this"
	 * 
	 * @param gvsOther The "Other" Generalized Vector Space
	 * 
	 * @return TRUE => The "Other" Generalized Vector Space is a Subset of this
	 */

	public abstract boolean subset (
		final org.drip.spaces.tensor.GeneralizedVector gvsOther);

	/**
	 * Indicate if the Predictor Variate Space is bounded from the Left and the Right
	 * 
	 * @return The Predictor Variate Space is bounded from the Left and the Right
	 */

	public abstract boolean isPredictorBounded();

	/**
	 * Retrieve the "Hyper" Volume of the Vector Space
	 * 
	 * @return The "Hyper" Volume of the Vector Space
	 * 
	 * @throws java.lang.Exception Thrown if the Hyper Volume cannot be computed
	 */

	public abstract double hyperVolume()
		throws java.lang.Exception;
}
