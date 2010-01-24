/*
 * Copyright (c) 2009-2010, Peter Abeles. All Rights Reserved.
 *
 * This file is part of JMatrixBenchmark.
 *
 * JMatrixBenchmark is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * JMatrixBenchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JMatrixBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 */

package jmbench.impl;

import jmbench.impl.runtime.SejmlAlgorithmFactory;
import org.junit.Test;


/**
 * @author Peter Abeles
 */
public class TestSejmlAlgorithmFactory {
    /**
     * Checks to see if all the functions don't modify their inputs.
     *
     * Right now this only really tests ejml since all the other ones work with a copy.
     */
    @Test
    public void testModInputs() {
        CheckModifyInputs.checkFactory( new SejmlAlgorithmFactory() );
    }
}