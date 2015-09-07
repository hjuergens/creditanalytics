
package org.drip.quant.fourier;

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
 * RotationCountPhaseTracker implements the standard technique to preserve the trajectory along the principal
 * 	branch in multi-valued complex operations. This is most common in Fourier inversion quadrature runs.
 * 
 * @author Lakshmi Krishnamurthy
 */

public class RotationCountPhaseTracker {

	/**
	 * APPLY_NONE => Do not Apply Rotation Count
	 */

	public static final int APPLY_NONE = 0;

	/**
	 * APPLY_BACKWARD => Decrement Rotation Count
	 */

	public static final int APPLY_BACKWARD = 1;

	/**
	 * APPLY_FORWARD => Increment Rotation Count
	 */

	public static final int APPLY_FORWARD = 2;

	private int _iRotationDirection = APPLY_NONE;
	private double _dblPreviousPhase = java.lang.Double.NaN;

	/**
	 * Empty RotationCountPhaseTracker constructor - Initialize to "NO ROTATION COUNT"
	 */

	public RotationCountPhaseTracker()
	{
		_iRotationDirection = APPLY_NONE;
	}

	/**
	 * Set the Direction on which the rotation count is to be applied
	 * 
	 * @param iRotationDirection The Rotation Direction
	 * 
	 * @return TRUE => Successfully set
	 */

	public boolean setDirection (
		final int iRotationDirection)
	{
		_iRotationDirection = iRotationDirection;
		return true;
	}

	/**
	 * Get the Direction on which the rotation count is to be applied
	 * 
	 * @return The Rotation Direction
	 */

	public int getDirection()
	{
		return _iRotationDirection;
	}

	/**
	 * Set the Previous Phase
	 * 
	 * @param dblPreviousPhase The Previous Phase
	 * 
	 * @return TRUE => Previous Phase Successfully set
	 */

	public boolean setPreviousPhase (
		final double dblPreviousPhase)
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblPreviousPhase)) return false;

		_dblPreviousPhase = dblPreviousPhase;
		return true;
	}

	/**
	 * Get the Previous Phase
	 * 
	 * @return The Previous Phase
	 */

	public double getPreviousPhase()
	{
		return _dblPreviousPhase;
	}

	/**
	 * Apply the Rotation Count Adjustment in accordance with the direction, (optionally) record the previous
	 * 	phase.
	 * 
	 * @param dblCurrentPhase The Phase to be Updated
	 * @param bApply TRUE => Record the Previous Phase
	 * 
	 * @return The Updated Phase
	 * 
	 * @throws java.lang.Exception Thrown if the Operation cannot be performed
	 */

	public double updateAndApply (
		final double dblCurrentPhase,
		final boolean bApply)
		throws java.lang.Exception
	{
		if (!org.drip.quant.common.NumberUtil.IsValid (dblCurrentPhase))
			throw new java.lang.Exception ("RotationCountPhaseTracker::updateAndApply => Invalid Inputs");

		double dblUpdatedPhase = dblCurrentPhase;

		if (APPLY_FORWARD == _iRotationDirection) {
			while (dblUpdatedPhase < _dblPreviousPhase)
				dblUpdatedPhase += 2. * java.lang.Math.PI;
		} else if (APPLY_BACKWARD == _iRotationDirection) {
			while (dblUpdatedPhase > _dblPreviousPhase)
				dblUpdatedPhase -= 2. * java.lang.Math.PI;
		} else if (APPLY_NONE != _iRotationDirection)
			throw new java.lang.Exception ("RotationCountPhaseTracker::updateAndApply => Invalid State");

		if (bApply) _dblPreviousPhase = dblUpdatedPhase;

		return dblUpdatedPhase;
	}
}
