
package org.drip.state.identifier;

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
 * LatentStateLabel is the interface that contains the labels inside the sub-stretch of the alternate state.
 *  The functionality its derivations implement provide fully qualified label names and their matches.
 *
 * @author Lakshmi Krishnamurthy
 */

public interface LatentStateLabel {

	/**
	 * Retrieve the Fully Qualified Name
	 * 
	 * @return The Fully Qualified Name
	 */

	public abstract java.lang.String fullyQualifiedName();

	/**
	 * Indicate whether this Label matches the supplied.
	 * 
	 * @param lslOther The Supplied Label
	 * 
	 * @return TRUE => The Supplied Label matches this.
	 */

	public abstract boolean match (
		final LatentStateLabel lslOther);
}
