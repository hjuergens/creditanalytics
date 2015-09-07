
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
 * RdAggregate exposes the basic Properties of the R^d as a Sectional Super-position of R^1 Vector Spaces.
 *
 * @author Lakshmi Krishnamurthy
 */

public abstract class RdAggregate implements org.drip.spaces.tensor.RdGeneralizedVector {
	private org.drip.spaces.tensor.R1GeneralizedVector[] _aR1GV = null;

	protected RdAggregate (
		final org.drip.spaces.tensor.R1GeneralizedVector[] aR1GV)
		throws java.lang.Exception
	{
		if (null == (_aR1GV = aR1GV)) throw new java.lang.Exception ("RdAggregate ctr: Invalid Inputs");

		int iDimension = _aR1GV.length;

		if (0 == iDimension) throw new java.lang.Exception ("RdAggregate ctr: Invalid Inputs");

		for (int i = 0; i < iDimension; ++i) {
			if (null == _aR1GV[i]) throw new java.lang.Exception ("RdAggregate ctr: Invalid Inputs");
		}
	}

	@Override public int dimension()
	{
		return _aR1GV.length;
	}

	@Override public org.drip.spaces.tensor.R1GeneralizedVector[] vectorSpaces()
	{
		return _aR1GV;
	}

	@Override public boolean validateInstance (
		final double[] adblInstance)
	{
		if (null == adblInstance) return false;

		int iDimension = _aR1GV.length;

		if (adblInstance.length != iDimension) return false;

		for (int i = 0; i < iDimension; ++i) {
			if (!_aR1GV[i].validateInstance (adblInstance[i])) return false;
		}

		return true;
	}

	@Override public boolean match (
		final org.drip.spaces.tensor.GeneralizedVector gvOther)
	{
		if (null == gvOther || !(gvOther instanceof RdAggregate)) return false;

		RdAggregate rdaOther = (RdAggregate) gvOther;

		int iDimensionOther = rdaOther.dimension();

		if (iDimensionOther != dimension()) return false;

		org.drip.spaces.tensor.R1GeneralizedVector[] aR1GVOther = rdaOther.vectorSpaces();

		for (int i = 0; i < iDimensionOther; ++i) {
			if (!aR1GVOther[i].match (_aR1GV[i])) return false;
		}

		return true;
	}

	@Override public boolean subset (
		final org.drip.spaces.tensor.GeneralizedVector gvOther)
	{
		if (null == gvOther || !(gvOther instanceof RdAggregate)) return false;

		int iDimensionOther = _aR1GV.length;
		RdAggregate rdaOther = (RdAggregate) gvOther;

		org.drip.spaces.tensor.R1GeneralizedVector[] aR1GVOther = rdaOther.vectorSpaces();

		for (int i = 0; i < iDimensionOther; ++i) {
			if (!aR1GVOther[i].match (_aR1GV[i])) return false;
		}

		return true;
	}

	@Override public boolean isPredictorBounded()
	{
		int iDimension = _aR1GV.length;

		for (int i = 0; i < iDimension; ++i) {
			if (!_aR1GV[i].isPredictorBounded()) return false;
		}

		return true;
	}
}
