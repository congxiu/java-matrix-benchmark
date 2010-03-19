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

package jmbench.tools.runtime.evaluation;


/**
 * Data structure that contains information about a runtime performance plot.
 *
 * @author Peter Abeles
 */
public class RuntimePlotData {

    public int matrixSize[];

    public double[][] results;

    public String labels[];

    public int[] plotLineType;

    public RuntimePlotData( int matrixSize[] , int numLibraries ) {
        this.matrixSize = matrixSize;

        results = new double[ numLibraries ][];
        for( int i = 0; i < numLibraries; i++ ) {
            results[i] = new double[ matrixSize.length ];
            for( int j = 0; j < matrixSize.length; j++ ) {
                results[i][j] = Double.NaN;
            }
        }

        labels = new String[ numLibraries ];
        plotLineType = new int[ numLibraries ];
    }

    public int findLibrary(String refLib) {
        for( int i = 0; i < labels.length; i++ ) {
            if( refLib.compareTo(labels[i]) == 0 )
                return i;
        }

        return -1;
    }
}
