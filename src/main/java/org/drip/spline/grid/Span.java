
package org.drip.spline.grid;

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
 * Span is the interface that exposes the functionality behind the collection of Stretches that may be
 *  overlapping or non-overlapping. It exposes the following stubs:
 *  - Retrieve the Left/Right Span Edge.
 *  - Indicate if the specified Label is part of the Merge State at the specified Predictor Ordinate.
 *  - Compute the Response from the containing Stretches.
 *  - Add a Stretch to the Span.
 *  - Retrieve the first Stretch that contains the Predictor Ordinate.
 *  - Retrieve the Stretch by Name.
 *  - Calculate the Response Derivative to the Quote at the specified Ordinate.
 *  - Display the Span Edge Coordinates.
 *
 * @author Lakshmi Krishnamurthy
 */

public interface Span {

	/**
	 * Retrieve the Left Span Edge
	 * 
	 * @return The Left Span Edge
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public abstract double left()
		throws java.lang.Exception;

	/**
	 * Retrieve the Right Span Edge
	 * 
	 * @return The Left Span Edge
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public abstract double right()
		throws java.lang.Exception;

	/**
	 * Indicate if the specified Label is part of the Merge State at the specified Predictor Ordinate
	 * 
	 * @param dblPredictorOrdinate The Predictor Ordinate
	 * @param lsl Merge State Label
	 * 
	 * @return TRUE => The specified Label is part of the Merge State at the specified Predictor Ordinate
	 */

	public abstract boolean isMergeState (
		final double dblPredictorOrdinate,
		final org.drip.state.identifier.LatentStateLabel lsl);

	/**
	 * Compute the Response from the containing Stretches
	 * 
	 * @param dblPredictorOrdinate The Predictor Ordinate
	 * 
	 * @return The Response
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public abstract double calcResponseValue (
		final double dblPredictorOrdinate)
		throws java.lang.Exception;

	/**
	 * Compute the Response Value Derivative from the containing Stretches
	 * 
	 * @param dblPredictorOrdinate The Predictor Ordinate
	 * @param iOrder Order of the Derivative to be calculated
	 * 
	 * @return The Response Value Derivative
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public abstract double calcResponseValueDerivative (
		final double dblPredictorOrdinate,
		final int iOrder)
		throws java.lang.Exception;

	/**
	 * Add a Stretch to the Span
	 * 
	 * @param mss Stretch to be added
	 * 
	 * @return TRUE => Stretch added successfully
	 */

	public abstract boolean addStretch (
		final org.drip.spline.stretch.MultiSegmentSequence mss);

	/**
	 * Retrieve the first Stretch that contains the Predictor Ordinate
	 * 
	 * @param dblPredictorOrdinate The Predictor Ordinate
	 * 
	 * @return The containing Stretch
	 */

	public abstract org.drip.spline.stretch.MultiSegmentSequence getContainingStretch (
		final double dblPredictorOrdinate);

	/**
	 * Retrieve the Stretch by Name
	 * 
	 * @param strName The Stretch Name
	 * 
	 * @return The Stretch
	 */

	public abstract org.drip.spline.stretch.MultiSegmentSequence getStretch (
		final String strName);

	/**
	 * Calculate the Response Derivative to the Manifest Measure at the specified Ordinate
	 * 
	 * @param strManifestMeasure Manifest Measure whose Sensitivity is sought
	 * @param dblPredictorOrdinate Predictor Ordinate
	 * @param iOrder Order of Derivative desired
	 * 
	 * @return Jacobian of the Response Derivative to the Manifest Measure at the Ordinate
	 */

	public abstract org.drip.quant.calculus.WengertJacobian jackDResponseDManifestMeasure (
		final String strManifestMeasure,
		final double dblPredictorOrdinate,
		final int iOrder);

	/**
	 * Check if the Predictor Ordinate is in the Stretch Range
	 * 
	 * @param dblPredictorOrdinate Predictor Ordinate
	 * 
	 * @return TRUE => Predictor Ordinate is in the Range
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are invalid
	 */

	public abstract boolean in (
		final double dblPredictorOrdinate)
		throws java.lang.Exception;

	/**
	 * Display the Span Edge Coordinates
	 */

	public String displayString();
}
