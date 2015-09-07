
package org.drip.learning.bound;

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
 * EmpiricalLearnerLoss Function computes the Empirical Loss of a Learning Operation resulting from the Use
 *  of a Learning Function in Conjunction with the corresponding Empirical Realization.
 *  
 *  The References are:
 *  
 *  1) Lugosi, G. (2002): Pattern Classification and Learning Theory, in: L. Györ, editor,
 *   Principles of Non-parametric Learning, 5-62, Springer, Wien.
 * 
 *  2) Boucheron, S., G. Lugosi, and P. Massart (2003): Concentration Inequalities Using the Entropy Method,
 *   Annals of Probability, 31, 1583-1614.
 *
 * @author Lakshmi Krishnamurthy
 */

public class EmpiricalLearnerLoss extends org.drip.function.definition.R1ToR1 {
	private double _dblRealization = java.lang.Double.NaN;
	private org.drip.function.definition.R1ToR1 _learner = null;

	/**
	 * EmpiricalLearnerLoss Constructor
	 * 
	 * @param learner The Learning Function
	 * @param dblRealization The Empirical Outcome
	 * 
	 * @throws java.lang.Exception Thrown if the Inputs are Invalid
	 */

	public EmpiricalLearnerLoss (
		final org.drip.function.definition.R1ToR1 learner,
		final double dblRealization)
		throws java.lang.Exception
	{
		super (null);

		if (null == (_learner = learner) || !org.drip.quant.common.NumberUtil.IsValid (_dblRealization =
			dblRealization))
			throw new java.lang.Exception ("EmpiricalLearnerLoss ctr: Invalid Inputs");
	}

	/**
	 * Retrieve the Empirical Realization
	 * 
	 * @return The Empirical Realization
	 */

	public double empiricalRealization()
	{
		return _dblRealization;
	}

	/**
	 * Retrieve the Learning Function
	 * 
	 * @return The Learning Function
	 */

	public org.drip.function.definition.R1ToR1 learner()
	{
		return _learner;
	}

	/**
	 * Compute the Loss for the specified Variate
	 * 
	 * @param dblVariate The Variate
	 * 
	 * @return Loss for the specified Variate
	 * 
	 * @throws java.lang.Exception Thrown if the Loss cannot be computed
	 */

	public double loss (
		final double dblVariate)
		throws java.lang.Exception
	{
		return _dblRealization - _learner.evaluate (dblVariate);
	}

	@Override public double evaluate (
		final double dblVariate)
		throws java.lang.Exception
	{
		return loss (dblVariate);
	}
}
