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
import jmbench.interfaces.LibraryAlgorithmFactory;
import jmbench.interfaces.MatrixGenerator;
import jmbench.misc.PosDefSymGenerator;
import jmbench.misc.RandomMatrixGenerator;
import jmbench.misc.SymmMatrixGenerator;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Peter Abeles
 */
public class FactoryRuntimeEvaluationCase {
    private static long RAND_SEED = 0x37645;

    private static int MAX_MATRIX_SIZE = 2000;
    private static int MIN_MATRIX_SIZE = 2;

    LibraryAlgorithmFactory factory;

    RuntimeBenchmarkConfig config;

    public FactoryRuntimeEvaluationCase( LibraryAlgorithmFactory factory , RuntimeBenchmarkConfig config ) {
        this.factory = factory;
        this.config = config;
    }

    public List<RuntimeEvaluationCase> createCases() {

        List<RuntimeEvaluationCase> ret = new ArrayList<RuntimeEvaluationCase>();

        AlgorithmInterface alg;

        alg = factory.mult();
        if( config.mult && alg != null ) ret.add( createMatrixMult(alg,1));

        alg = factory.add();
        if( config.add && alg != null ) ret.add( createMatrixAdd(alg));

        alg = factory.transpose();
        if( config.transpose && alg != null ) ret.add( createTranspose(alg));

        alg = factory.scale();
        if( config.scale && alg != null ) ret.add( createScale(alg));

        alg = factory.det();
        if( config.det && alg != null ) ret.add( createDeterminant(alg));

        alg = factory.invert();
        if( config.invert && alg != null ) ret.add( createInvert(alg));

        alg = factory.svd();
        if( config.svd && alg != null ) ret.add( createSVD(alg));

        alg = factory.chol();
        if( config.chol && alg != null ) ret.add( createCholesky(alg));

        alg = factory.multTransA();
        if( config.multTransA && alg != null ) ret.add( createMultTranA(alg));

        alg = factory.solveExact();
        if( config.solveExact &&  alg != null ) ret.add( createSolveEq(alg));

        alg = factory.solveOver();
        if( config.solveOver && alg != null ) ret.add( createSolveOver(alg));

        alg = factory.qr();
        if( config.qr && alg != null ) ret.add( createQR(alg));

        alg = factory.lu();
        if( config.lu && alg != null ) ret.add( createLU(alg));

        alg = factory.eigSymm();
        if( config.eigSymm && alg != null ) ret.add( createEigSymm(alg));

        return ret;
    }


    public RuntimeEvaluationCase createMatrixMult( AlgorithmInterface alg , double scale ) {

        MatrixGenerator []generators = new MatrixGenerator[2];
        generators[0] = new RandomMatrixGenerator(RAND_SEED,scale,1);
        generators[1] = new RandomMatrixGenerator(RAND_SEED,1,scale);

        int matDimen[] = createDimenList(MAX_MATRIX_SIZE);

        return new RuntimeEvaluationCase("Mult c=a*b","mult",matDimen,alg,generators);
    }

    public RuntimeEvaluationCase createMatrixAdd( AlgorithmInterface alg ) {

        MatrixGenerator []generators = new MatrixGenerator[2];
        generators[0] = new RandomMatrixGenerator(RAND_SEED);
        generators[1] = new RandomMatrixGenerator(RAND_SEED);

        int matDimen[] = createDimenList(MAX_MATRIX_SIZE);

        return new RuntimeEvaluationCase("Add c=a+b","add",matDimen,alg,generators);
    }

    public RuntimeEvaluationCase createTranspose( AlgorithmInterface alg ) {

        MatrixGenerator []generators = new MatrixGenerator[1];
        generators[0] = new RandomMatrixGenerator(RAND_SEED,1,1);

        int matDimen[] = createDimenList(MAX_MATRIX_SIZE);

        return new RuntimeEvaluationCase("Transpose b=a^T","tran",matDimen,alg,generators);
    }

    public RuntimeEvaluationCase createScale( AlgorithmInterface alg ) {

        MatrixGenerator []generators = new MatrixGenerator[1];
        generators[0] = new RandomMatrixGenerator(RAND_SEED,1,1);

        int matDimen[] = createDimenList(MAX_MATRIX_SIZE);

        return new RuntimeEvaluationCase("Scale b=alpha*a","scale",matDimen,alg,generators);
    }

    public RuntimeEvaluationCase createDeterminant( AlgorithmInterface alg ) {

        MatrixGenerator []generators = new MatrixGenerator[1];
        generators[0] = new RandomMatrixGenerator(RAND_SEED);

        int matDimen[] = createDimenList(MAX_MATRIX_SIZE);

        return new RuntimeEvaluationCase("Determinant","det",matDimen,alg,generators);
    }

    public RuntimeEvaluationCase createInvert( AlgorithmInterface alg ) {

        MatrixGenerator []generators = new MatrixGenerator[1];
        generators[0] = new RandomMatrixGenerator(RAND_SEED);

        int matDimen[] = createDimenList(MAX_MATRIX_SIZE);

        return new RuntimeEvaluationCase("Invert b=inv(a)","inv",matDimen,alg,generators);
    }

    public RuntimeEvaluationCase createSVD( AlgorithmInterface alg ) {

        MatrixGenerator []generators = new MatrixGenerator[1];
        generators[0] = new RandomMatrixGenerator(RAND_SEED);

        int matDimen[] = createDimenList(MAX_MATRIX_SIZE);

        return new RuntimeEvaluationCase("SVD","svd",matDimen,alg,generators);
    }

    public RuntimeEvaluationCase createCholesky( AlgorithmInterface alg ) {

        MatrixGenerator []generators = new MatrixGenerator[1];
        generators[0] = new PosDefSymGenerator(RAND_SEED);

        int matDimen[] = createDimenList(MAX_MATRIX_SIZE);

        return new RuntimeEvaluationCase("Cholesky","cholesky",matDimen,alg,generators);
    }

    public RuntimeEvaluationCase createMultTranA( AlgorithmInterface alg ) {

        MatrixGenerator []generators = new MatrixGenerator[2];
        generators[0] = new RandomMatrixGenerator(RAND_SEED,1,1);
        generators[1] = new RandomMatrixGenerator(RAND_SEED,1,1);

        int matDimen[] = createDimenList(MAX_MATRIX_SIZE);

        return new RuntimeEvaluationCase("Mult c=a^t * b","multTranA",matDimen,alg,generators);
    }

    /**
     * The number of unknowns matches the number of equations.
     */
    public RuntimeEvaluationCase createSolveEq( AlgorithmInterface alg ) {

        MatrixGenerator []generators = new MatrixGenerator[2];
        generators[0] = new RandomMatrixGenerator(RAND_SEED,1,1);
        generators[1] = new RandomMatrixGenerator(RAND_SEED,1,2.0);

        int matDimen[] = createDimenList(MAX_MATRIX_SIZE);

        return new RuntimeEvaluationCase("Solve m=n","solveEq",matDimen,alg,generators);
    }

    /**
     * See how well it can solve an overdetermined system.
     */
    public RuntimeEvaluationCase createSolveOver( AlgorithmInterface alg ) {

        MatrixGenerator []generators = new MatrixGenerator[2];
        generators[0] = new RandomMatrixGenerator(RAND_SEED,3,1);
        generators[1] = new RandomMatrixGenerator(RAND_SEED,3,2.0);

        int matDimen[] = createDimenList(MAX_MATRIX_SIZE);

        return new RuntimeEvaluationCase("Solve m>n","solveOver",matDimen,alg,generators);
    }

    public RuntimeEvaluationCase createQR( AlgorithmInterface alg ) {

        MatrixGenerator []generators = new MatrixGenerator[1];
        generators[0] = new RandomMatrixGenerator(RAND_SEED,1,1);

        int matDimen[] = createDimenList(MAX_MATRIX_SIZE);

        return new RuntimeEvaluationCase("QR","QR",matDimen,alg,generators);
    }

    public RuntimeEvaluationCase createLU( AlgorithmInterface alg ) {

        MatrixGenerator []generators = new MatrixGenerator[1];
        generators[0] = new RandomMatrixGenerator(RAND_SEED,1,1);

        int matDimen[] = createDimenList(MAX_MATRIX_SIZE);

        return new RuntimeEvaluationCase("LU","LU",matDimen,alg,generators);
    }

    public RuntimeEvaluationCase createEigSymm( AlgorithmInterface alg ) {

        MatrixGenerator []generators = new MatrixGenerator[1];
        generators[0] = new SymmMatrixGenerator(RAND_SEED);

        int matDimen[] = createDimenList(MAX_MATRIX_SIZE);

        return new RuntimeEvaluationCase("Eigen for Symm Matrices","EigSymm",matDimen,alg,generators);
    }

    private static int[] createDimenList( int max ) {
        List<Integer> a = new ArrayList<Integer>();

        int val = MIN_MATRIX_SIZE;

        int dec = 1;

        while( (val / (dec*10)) >= 1 ) {
            dec *= 10;
        }

        a.add(val);

        while(  val < max ) {
            if( val < 5 ) {
                val++;
            } else {
                switch( val / dec ) {
                    case 1:
                        val = 2*dec;
                        break;

                    case 2:
                        val = 5*dec;
                        break;

                    case 5:
                        val = 10*dec;
                        dec *= 10;
                        break;
                }
            }

            a.add(val);
        }

        int ret[] = new int[ a.size() ];
        for( int i = 0; i < ret.length; i++ ) {
            ret[i] = a.get(i);
        }
        return ret;
    }

}
