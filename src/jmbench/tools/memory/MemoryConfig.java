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

import jmbench.impl.MatrixLibrary;
import jmbench.impl.memory.*;
import jmbench.tools.EvaluationTarget;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Peter Abeles
 */
public class MemoryConfig {

    public static EvaluationTarget ejml = new EvaluationTarget( MatrixLibrary.EJML, EjmlMemoryFactory.class.getName());
    public static EvaluationTarget sejml = new EvaluationTarget( MatrixLibrary.SEJML, SejmlMemoryFactory.class.getName());
    public static EvaluationTarget jama = new EvaluationTarget( MatrixLibrary.JAMA, JamaMemoryFactory.class.getName());
    public static EvaluationTarget ojalgo = new EvaluationTarget( MatrixLibrary.OJALGO, OjAlgoMemoryFactory.class.getName());
    public static EvaluationTarget commons = new EvaluationTarget( MatrixLibrary.CM, CommonsMathMemoryFactory.class.getName());
    public static EvaluationTarget colt = new EvaluationTarget( MatrixLibrary.COLT, ColtMemoryFactory.class.getName());
    public static EvaluationTarget mtj = new EvaluationTarget( MatrixLibrary.MTJ, MtjMemoryFactory.class.getName());
    public static EvaluationTarget pcolt = new EvaluationTarget( MatrixLibrary.PCOLT, PColtMemoryFactory.class.getName());
    public static EvaluationTarget ujmp = new EvaluationTarget( MatrixLibrary.UJMP, UjmpMemoryFactory.class.getName());
    public static EvaluationTarget jblas = new EvaluationTarget( MatrixLibrary.JBLAS, JBlasMemoryFactory.class.getName());

    // random seed
    public long seed;

    // list of all the libraries being tested
    public List<EvaluationTarget> libraries;

    // maximum amount of time it allows for any test
    public long maxTestTimeMilli;

    // number of times it repeats an operation within a test
    public int numCycles;

    // how many times should it repeat each test
    public int numTrials;

    // desired accuracy of the result
    public long accuracy;

    // number of rows/cols of tested matrices
    public int matrixSizeSmall;
    public int matrixSizeLarge;

    // ops that it tessts
    public boolean mult;
    public boolean add;
    public boolean solveLinear;
    public boolean solveLS;
    public boolean svd;
    public boolean eig;


    public static MemoryConfig createDefault() {
        MemoryConfig ret = new MemoryConfig();

        ret.libraries = new ArrayList<EvaluationTarget>();
        ret.libraries.add( ejml);
        ret.libraries.add( jama );
        ret.libraries.add( commons );
        ret.libraries.add( colt );
        ret.libraries.add( jblas );
        ret.libraries.add( mtj );
        ret.libraries.add( ojalgo );
        ret.libraries.add( sejml);
        ret.libraries.add( pcolt );
        ret.libraries.add( ujmp );

        ret.seed = 234234;

        ret.maxTestTimeMilli = 5*60*1000;
        ret.numCycles = 1;
        ret.numTrials = 1;
        ret.accuracy = 2;
        ret.matrixSizeSmall = 1000;
        ret.matrixSizeLarge = 1500;

        ret.mult = true;
        ret.add = true;
        ret.solveLinear = true;
        ret.solveLS = true;
        ret.svd = true;
        ret.eig = true;


        return ret;
    }
}
