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

import jmbench.interfaces.AlgorithmInterface;
import jmbench.interfaces.MatrixGenerator;

import java.io.Serializable;


/**
 * @author Peter Abeles
 */
public class RuntimeEvaluationCase implements Serializable {
    // the operation that this is evaluating
    private String opName;
    // the different matrix sizes that can be evalued
    private int dimens[];
    // list of algorithms it it can run
    private AlgorithmInterface alg;
    // what creates the matrices it processes
    private MatrixGenerator generators[];

    private volatile String fileName;

    private volatile RuntimeEvaluationTest theTest = new RuntimeEvaluationTest();

    public RuntimeEvaluationCase( String opName , String fileName , int dimens[] ,
                                  AlgorithmInterface alg ,
                                  MatrixGenerator generators[] )
    {
        this.opName = opName;
        this.dimens = dimens.clone();
        this.alg = alg;
        this.generators = generators.clone();
        this.fileName = fileName;
    }

    public RuntimeEvaluationCase(){}


    public RuntimeEvaluationTest createTest( int dimenIndex , long duration ) {
        theTest.setDimen(dimens[dimenIndex]);
        theTest.setAlg(alg);
        theTest.setGenerators(generators);
        theTest.setExpectedRuntime(duration);

        return theTest;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public int[] getDimens() {
        return dimens;
    }

    public void setDimens(int[] dimens) {
        this.dimens = dimens;
    }

    public AlgorithmInterface getAlg() {
        return alg;
    }

    public void setAlg(AlgorithmInterface alg) {
        this.alg = alg;
    }

    public MatrixGenerator[] getGenerators() {
        return generators;
    }

    public void setGenerators(MatrixGenerator[] generators) {
        this.generators = generators;
    }

    public String getFileName() {
        return fileName;
    }

}
