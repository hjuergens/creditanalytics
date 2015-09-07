
package org.drip.regression.fixedpointfinder;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
 * Copyright (C) 2012 Lakshmi Krishnamurthy
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
 * BracketingRegressorSet implements regression run for the Primitive Bracketing Fixed Point Search Method.
 *  It implements the following 4 primitive bracketing schemes: Bisection, False Position, Quadratic, and
 *  Inverse Quadratic.
 *
 * @author Lakshmi Krishnamurthy
 */

public class BracketingRegressorSet implements org.drip.regression.core.RegressorSet {
	private org.drip.function.definition.R1ToR1 _of = null;
	private String _strRegressionScenario = "org.drip.math.solver1D.FixedPointFinderPrimitive";

	private java.util.List<org.drip.regression.core.UnitRegressor> _setRegressors = new
		java.util.ArrayList<org.drip.regression.core.UnitRegressor>();

	public BracketingRegressorSet()
	{
		_of = new org.drip.function.definition.R1ToR1 (null)
		{
			public double evaluate (
				final double dblVariate)
				throws java.lang.Exception
			{
				if (java.lang.Double.isNaN (dblVariate))
					throw new java.lang.Exception
						("FixedPointFinderRegressorOF.evalTarget => Invalid variate!");

				/* return java.lang.Math.cos (dblVariate) - dblVariate * dblVariate * dblVariate;

				return dblVariate * dblVariate * dblVariate - 3. * dblVariate * dblVariate + 2. *
					dblVariate;

				return dblVariate * dblVariate * dblVariate + 4. * dblVariate + 4.;

				return 32. * dblVariate * dblVariate * dblVariate * dblVariate * dblVariate * dblVariate
					- 48. * dblVariate * dblVariate * dblVariate * dblVariate + 18. * dblVariate *
						dblVariate - 1.; */

				return 1. + 3. * dblVariate - 2. * java.lang.Math.sin (dblVariate);
			}

			@Override public double integrate (
				final double dblBegin,
				final double dblEnd)
				throws java.lang.Exception
			{
				return org.drip.quant.calculus.R1ToR1Integrator.Boole (this, dblBegin, dblEnd);
			}
		};
	}

	@Override public boolean setupRegressors()
	{
		try {
			_setRegressors.add (new org.drip.regression.core.UnitRegressionExecutor
				("BisectionFixedPointFinder", _strRegressionScenario)
			{
				org.drip.function.solverR1ToR1.FixedPointFinderOutput fpfopBisect = null;
				org.drip.function.solverR1ToR1.FixedPointFinderBracketing fpfbBisect = null;

				@Override public boolean preRegression()
				{
					try {
						fpfbBisect = new org.drip.function.solverR1ToR1.FixedPointFinderBracketing (0., _of, null,
							org.drip.function.solverR1ToR1.VariateIteratorPrimitive.BISECTION, true);

						return true;
					} catch (java.lang.Exception e) {
						e.printStackTrace();
					}

					return false;
				}

				@Override public boolean execRegression()
				{
					if (null == (fpfopBisect = fpfbBisect.findRoot())) return false;

					return true;
				}

				@Override public boolean postRegression (
					final org.drip.regression.core.RegressionRunDetail rnvd)
				{
					rnvd.set ("FixedPoint", "" + fpfopBisect.getRoot());

					return true;
				}
			});

			_setRegressors.add (new org.drip.regression.core.UnitRegressionExecutor
				("FalsePositionFixedPointFinder", _strRegressionScenario)
			{
				org.drip.function.solverR1ToR1.FixedPointFinderOutput fpfopFalsePosition = null;
				org.drip.function.solverR1ToR1.FixedPointFinderBracketing fpfbFalsePosition = null;

				@Override public boolean preRegression()
				{
					try {
						fpfbFalsePosition = new org.drip.function.solverR1ToR1.FixedPointFinderBracketing (0., _of,
							null, org.drip.function.solverR1ToR1.VariateIteratorPrimitive.FALSE_POSITION, true);

						return true;
					} catch (java.lang.Exception e) {
						e.printStackTrace();
					}

					return false;
				}

				@Override public boolean execRegression()
				{
					if (null == (fpfopFalsePosition = fpfbFalsePosition.findRoot())) return false;

					return true;
				}

				@Override public boolean postRegression (
					final org.drip.regression.core.RegressionRunDetail rnvd)
				{
					rnvd.set ("FixedPoint", "" + fpfopFalsePosition.getRoot());

					return true;
				}
			});

			_setRegressors.add (new org.drip.regression.core.UnitRegressionExecutor
				("QuadraticFixedPointFinder", _strRegressionScenario) {
				org.drip.function.solverR1ToR1.FixedPointFinderOutput fpfopQuadratic = null;
				org.drip.function.solverR1ToR1.FixedPointFinderBracketing fpfbQuadratic = null;

				@Override public boolean preRegression()
				{
					try {
						fpfbQuadratic = new org.drip.function.solverR1ToR1.FixedPointFinderBracketing (0., _of,
							null, org.drip.function.solverR1ToR1.VariateIteratorPrimitive.QUADRATIC_INTERPOLATION,
								true);

						return true;
					} catch (java.lang.Exception e) {
						e.printStackTrace();
					}

					return false;
				}

				@Override public boolean execRegression()
				{
					if (null == (fpfopQuadratic = fpfbQuadratic.findRoot())) return false;

					return true;
				}

				@Override public boolean postRegression (
					final org.drip.regression.core.RegressionRunDetail rnvd)
				{
					rnvd.set ("FixedPoint", "" + fpfopQuadratic.getRoot());

					return true;
				}
			});

			_setRegressors.add (new org.drip.regression.core.UnitRegressionExecutor
				("InverseQuadraticFixedPointFinder", _strRegressionScenario)
			{
				org.drip.function.solverR1ToR1.FixedPointFinderOutput fpfopInverseQuadratic = null;
				org.drip.function.solverR1ToR1.FixedPointFinderBracketing fpfbInverseQuadratic = null;

				@Override public boolean preRegression()
				{
					try {
						fpfbInverseQuadratic = new org.drip.function.solverR1ToR1.FixedPointFinderBracketing (0.,
							_of, null,
								org.drip.function.solverR1ToR1.VariateIteratorPrimitive.INVERSE_QUADRATIC_INTERPOLATION,
							true);

						return true;
					} catch (java.lang.Exception e) {
						e.printStackTrace();
					}

					return false;
				}

				@Override public boolean execRegression()
				{
					if (null == (fpfopInverseQuadratic = fpfbInverseQuadratic.findRoot())) return false;

					return true;
				}

				@Override public boolean postRegression (
					final org.drip.regression.core.RegressionRunDetail rnvd)
				{
					rnvd.set ("FixedPoint", "" + fpfopInverseQuadratic.getRoot());

					return true;
				}
			});

			_setRegressors.add (new org.drip.regression.core.UnitRegressionExecutor
				("RidderFixedPointFinder", _strRegressionScenario)
			{
				org.drip.function.solverR1ToR1.FixedPointFinderOutput fpfopRidder = null;
				org.drip.function.solverR1ToR1.FixedPointFinderBracketing fpfbRidder = null;

				@Override public boolean preRegression()
				{
					try {
						fpfbRidder = new org.drip.function.solverR1ToR1.FixedPointFinderBracketing (0., _of, null,
							org.drip.function.solverR1ToR1.VariateIteratorPrimitive.RIDDER, true);

						return true;
					} catch (java.lang.Exception e) {
						e.printStackTrace();
					}

					return false;
				}

				@Override public boolean execRegression()
				{
					if (null == (fpfopRidder = fpfbRidder.findRoot())) return false;

					return true;
				}

				@Override public boolean postRegression (
					final org.drip.regression.core.RegressionRunDetail rnvd) {
					rnvd.set ("FixedPoint", "" + fpfopRidder.getRoot());

					return true;
				}
			});
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override public java.util.List<org.drip.regression.core.UnitRegressor> getRegressorSet()
	{
		return _setRegressors;
	}

	@Override public String getSetName()
	{
		return _strRegressionScenario;
	}
}
