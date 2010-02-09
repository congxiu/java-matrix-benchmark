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
import jmbench.tools.runtime.generator.*;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Peter Abeles
 */
public class FactoryRuntimeEvaluationCase {
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

        alg = factory.invertSymmPosDef();
        if( config.invertSymmPosDef && alg != null ) ret.add( createInvertSymmPosDef(alg));

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

        InputOutputGenerator generator = new MultGenerator();

        int matDimen[] = createDimenList(config.minMatrixSize, config.maxMatrixSize);

        return new RuntimeEvaluationCase("Mult c=a*b","mult",matDimen,alg,generator);
    }

    public RuntimeEvaluationCase createMatrixAdd( AlgorithmInterface alg ) {

        InputOutputGenerator generator = new AddGenerator();

        int matDimen[] = createDimenList(config.minMatrixSize, config.maxMatrixSize);

        return new RuntimeEvaluationCase("Add c=a+b","add",matDimen,alg,generator);
    }

    public RuntimeEvaluationCase createTranspose( AlgorithmInterface alg ) {

        InputOutputGenerator generator = new TransposeGenerator();

        int matDimen[] = createDimenList(config.minMatrixSize, config.maxMatrixSize);

        return new RuntimeEvaluationCase("Transpose b=a^T","tran",matDimen,alg,generator);
    }

    public RuntimeEvaluationCase createScale( AlgorithmInterface alg ) {

        InputOutputGenerator generator = new ScaleGenerator();

        int matDimen[] = createDimenList(config.minMatrixSize, config.maxMatrixSize);

        return new RuntimeEvaluationCase("Scale b=alpha*a","scale",matDimen,alg,generator);
    }

    public RuntimeEvaluationCase createDeterminant( AlgorithmInterface alg ) {

        InputOutputGenerator generator = new DeterminantGenerator();

        int matDimen[] = createDimenList(config.minMatrixSize, config.maxMatrixSize);

        return new RuntimeEvaluationCase("Determinant","det",matDimen,alg,generator);
    }

    public RuntimeEvaluationCase createInvert( AlgorithmInterface alg ) {

        InputOutputGenerator generator = new InvertGenerator();

        int matDimen[] = createDimenList(config.minMatrixSize, config.maxMatrixSize);

        return new RuntimeEvaluationCase("Invert b=inv(a)","inv",matDimen,alg,generator);
    }

    public RuntimeEvaluationCase createInvertSymmPosDef( AlgorithmInterface alg ) {

        InputOutputGenerator generator = new InvertSymmPosDefGenerator();

        int matDimen[] = createDimenList(config.minMatrixSize, config.maxMatrixSize);

        return new RuntimeEvaluationCase("Invert b=invSymmPosDef(a)","invSymmPosDef",matDimen,alg,generator);
    }

    public RuntimeEvaluationCase createSVD( AlgorithmInterface alg ) {

        InputOutputGenerator generator = new SvdGenerator();

        int matDimen[] = createDimenList(config.minMatrixSize, config.maxMatrixSize);

        return new RuntimeEvaluationCase("SVD","svd",matDimen,alg,generator);
    }

    public RuntimeEvaluationCase createCholesky( AlgorithmInterface alg ) {

        InputOutputGenerator generator = new CholeskyGenerator();

        int matDimen[] = createDimenList(config.minMatrixSize, config.maxMatrixSize);

        return new RuntimeEvaluationCase("Cholesky","cholesky",matDimen,alg,generator);
    }

    public RuntimeEvaluationCase createMultTranA( AlgorithmInterface alg ) {

        InputOutputGenerator generator = new MultTranAGenerator();

        int matDimen[] = createDimenList(config.minMatrixSize, config.maxMatrixSize);

        return new RuntimeEvaluationCase("Mult c=a^t * b","multTranA",matDimen,alg,generator);
    }

    /**
     * The number of unknowns matches the number of equations.
     */
    public RuntimeEvaluationCase createSolveEq( AlgorithmInterface alg ) {

        InputOutputGenerator generator = new SolveEqGenerator();

        int matDimen[] = createDimenList(config.minMatrixSize, config.maxMatrixSize);

        return new RuntimeEvaluationCase("Solve m=n","solveEq",matDimen,alg,generator);
    }

    /**
     * See how well it can solve an overdetermined system.
     */
    public RuntimeEvaluationCase createSolveOver( AlgorithmInterface alg ) {

        InputOutputGenerator generator = new SolveOverGenerator();

        int matDimen[] = createDimenList(config.minMatrixSize, config.maxMatrixSize);

        return new RuntimeEvaluationCase("Solve m>n","solveOver",matDimen,alg,generator);
    }

    public RuntimeEvaluationCase createQR( AlgorithmInterface alg ) {

        InputOutputGenerator generator = new QrGenerator();

        int matDimen[] = createDimenList(config.minMatrixSize, config.maxMatrixSize);

        return new RuntimeEvaluationCase("QR","QR",matDimen,alg,generator);
    }

    public RuntimeEvaluationCase createLU( AlgorithmInterface alg ) {

        InputOutputGenerator generator = new LuGenerator();

        int matDimen[] = createDimenList(config.minMatrixSize, config.maxMatrixSize);

        return new RuntimeEvaluationCase("LU","LU",matDimen,alg,generator);
    }

    public RuntimeEvaluationCase createEigSymm( AlgorithmInterface alg ) {

        InputOutputGenerator generator = new EigSymmGenerator();
        int matDimen[] = createDimenList(config.minMatrixSize, config.maxMatrixSize);

        return new RuntimeEvaluationCase("Eigen for Symm Matrices","EigSymm",matDimen,alg,generator);
    }

    private static int[] createDimenList( int min , int max ) {
        List<Integer> a = new ArrayList<Integer>();

        int val = min;

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
