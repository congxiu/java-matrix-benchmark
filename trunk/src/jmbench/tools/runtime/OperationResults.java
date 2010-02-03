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

package jmbench.tools.runtime;

import jmbench.impl.MatrixLibrary;

import java.io.Serializable;


/**
 * The benchmark results performance trials.
 *
 * @author Peter Abeles
 */
public class OperationResults implements Serializable {
    public String opName;
    public MatrixLibrary library;
    public int matDimen[];
    public RuntimeEvaluationMetrics metrics[];

    public OperationResults( String opName , MatrixLibrary library ,
                             int matDimen[] ,  RuntimeEvaluationMetrics metrics[] )
    {
        this.opName = opName;
        this.library = library;

        this.metrics = metrics;
        this.matDimen = matDimen;
    }

    public OperationResults(){}

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public int[] getMatDimen() {
        return matDimen;
    }

    public void setMatDimen(int[] matDimen) {
        this.matDimen = matDimen;
    }

    public MatrixLibrary getLibrary() {
        return library;
    }

    public void setLibrary(MatrixLibrary library) {
        this.library = library;
    }

    public RuntimeEvaluationMetrics[] getMetrics() {
        return metrics;
    }

    public void setMetrics(RuntimeEvaluationMetrics[] metrics) {
        this.metrics = metrics;
    }
}
