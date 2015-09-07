
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
 * RdGeneralizedVector exposes the basic Properties of the Generalized R^d Vector Space.
 *
 * @author Lakshmi Krishnamurthy
 */

public interface RdGeneralizedVector extends org.drip.spaces.tensor.GeneralizedVector
{

	/**
	 * Retrieve the Dimension of the Space
	 *  
	 * @return The Dimension of the Space
	 */

	public abstract int dimension();

	/**
	 * Retrieve the Array of the Underlying R^1 Vector Spaces
	 * 
	 * @return The Array of the Underlying R^1 Vector Spaces
	 */

	public abstract org.drip.spaces.tensor.R1GeneralizedVector[] vectorSpaces();

	/**
	 * Validate the Input Instance
	 * 
	 * @param adblInstance The Input Instance
	 * 
	 * @return TRUE => Instance is a Valid Entry in the Space
	 */

	public abstract boolean validateInstance (
		final double[] adblInstance);

	/**
	 * Retrieve the Array of the Variate Left Edges
	 * 
	 * @return The Array of the Variate Left Edges
	 */

	public abstract double[] leftDimensionEdge();

	/**
	 * Retrieve the Array of the Variate Right Edges
	 * 
	 * @return The Array of the Variate Right Edges
	 */

	public abstract double[] rightDimensionEdge();
}
