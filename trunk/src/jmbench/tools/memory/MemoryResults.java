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

package jmbench.tools.memory;

import jmbench.tools.EvaluatorSlave;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Peter Abeles
 */
public class MemoryResults implements Serializable {
    String nameLibrary;
    String nameOp;

    EvaluatorSlave.FailReason error;
    String errorMessage;

    List<Long> results = new ArrayList<Long>();

    public MemoryResults() {

    }

    public long getMinimumMemory() {
        long min = Long.MAX_VALUE;

        for( long l : results ) {
            if( l < min ) {
                min = l;
            }
        }

        return min;
    }

    public String getNameLibrary() {
        return nameLibrary;
    }

    public void setNameLibrary(String nameLibrary) {
        this.nameLibrary = nameLibrary;
    }

    public String getNameOp() {
        return nameOp;
    }

    public void setNameOp(String nameOp) {
        this.nameOp = nameOp;
    }

    public EvaluatorSlave.FailReason getError() {
        return error;
    }

    public void setError(EvaluatorSlave.FailReason error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<Long> getResults() {
        return results;
    }

    public void setResults(List<Long> results) {
        this.results = results;
    }
}
