
package org.drip.spaces.instance;

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
 * ValidatedRdContinuous holds the Validated R^d Continuous Vector Instance Sequence and the Corresponding
 *  Generalized Vector Space Type.
 * 
 * The Reference we've used is:
 * 
 * 	- Carl, B., and I. Stephani (1990): Entropy, Compactness, and Approximation of Operators, Cambridge
 * 		University Press, Cambridge UK.
 *
 * @author Lakshmi Krishnamurthy
 */

public class ValidatedRdContinuous extends org.drip.spaces.instance.ValidatedRd {

	/**
	 * ValidatedRdContinuous Constructor
	 * 
	 * @param cvRd The Continuous R^d Tensor Space Type
	 * @param aadblInstance The Data Instance
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public ValidatedRdContinuous (
		final org.drip.spaces.tensor.RdContinuousVector cvRd,
		final double[][] aadblInstance)
		throws java.lang.Exception
	{
		super (cvRd, aadblInstance);
	}

	@Override public org.drip.spaces.tensor.RdContinuousVector tensorSpaceType()
	{
		return (org.drip.spaces.tensor.RdContinuousVector) super.tensorSpaceType();
	}
}
