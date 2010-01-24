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

package jmbench.tools;

import java.util.ArrayList;
import java.util.List;


/**
 * The raw performance results from all the trials.
 *
 * @author Peter Abeles
 */
@SuppressWarnings({"unchecked"})
public class RawPerformanceResults {
    List<Double> algResults[];

    public RawPerformanceResults( int numAlgs ) {
        algResults = (List<Double>[])new List[ numAlgs ];

        for( int i = 0; i < algResults.length; i++ ) {
            algResults[i] = new ArrayList<Double>();
        }
    }

    public void addResults( int algIndex , List<Double> results ) {
        algResults[algIndex].addAll(results);
    }

    
}
