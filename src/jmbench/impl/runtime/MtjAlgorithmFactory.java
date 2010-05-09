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

package jmbench.impl.runtime;

import jmbench.impl.MatrixLibrary;
import jmbench.interfaces.AlgorithmInterface;
import jmbench.interfaces.ConfigureLibrary;
import jmbench.interfaces.RuntimePerformanceFactory;
import jmbench.tools.runtime.generator.ScaleGenerator;
import no.uib.cipr.matrix.*;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;


/**
 * @author Peter Abeles
 */
public class MtjAlgorithmFactory implements RuntimePerformanceFactory {

    private static abstract class MyInterface implements AlgorithmInterface
    {
        @Override
        public String getName() {
            return MatrixLibrary.MTJ.getVersionName();
        }
    }

    @Override
    public ConfigureLibrary configure() {
        return null;
    }

    @Override
    public AlgorithmInterface chol() {
        return new Chol();
    }

    public static class Chol extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseMatrix matA = convertToMtj(inputs[0]);

            DenseCholesky cholesky = new DenseCholesky(matA.numRows(),false);
            LowerSPDDenseMatrix uspd = new LowerSPDDenseMatrix(matA);

            LowerTriangDenseMatrix L = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                // the input matrix is over written
                uspd.set(matA);
                if( !cholesky.factor(uspd).isSPD() ) {
                    throw new RuntimeException("Is not SPD");
                }

                L = cholesky.getL();
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = mtjToEjml(L);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface lu() {
        return new LU();
    }

    public static class LU extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseMatrix matA = convertToMtj(inputs[0]);

            DenseLU lu = new DenseLU(matA.numRows(),matA.numColumns());
            DenseMatrix tmp = new DenseMatrix(matA);

            LowerTriangDenseMatrix L = null;
            UpperTriangDenseMatrix U = null;
            int pivots[] = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                // the input matrix is over written
                tmp.set(matA);
                lu.factor(tmp);

                L = lu.getL();
                U = lu.getU();
                pivots = lu.getPivots();
            }

            long elapsedTime = System.currentTimeMillis()-prev;

            // I believe that MTJ is generating some buggy row pivots since they go outside
            // the matrix bounds

            outputs[0] = mtjToEjml(L);
            outputs[1] = mtjToEjml(U);
//            outputs[2] = SpecializedOps.pivotMatrix(null, pivots, pivots.length);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface svd() {
        return new MySVD();
    }

    public static class MySVD extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseMatrix matA = convertToMtj(inputs[0]);

            no.uib.cipr.matrix.SVD svd = new no.uib.cipr.matrix.SVD(matA.numRows(),matA.numColumns());
            DenseMatrix tmp = new DenseMatrix(matA);

            DenseMatrix U = null;
            double[] S = null;
            DenseMatrix Vt = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                try {
                    // the input matrix is over written
                    tmp.set(matA);
                    SVD s = svd.factor(tmp);
                    U = s.getU();
                    S = s.getS();
                    Vt = s.getVt();
                } catch (NotConvergedException e) {
                    throw new RuntimeException(e);
                }
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = mtjToEjml(U);
            outputs[1] = CommonOps.diag(S);
            outputs[2] = mtjToEjml(Vt);
            CommonOps.transpose(outputs[2]);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface eigSymm() {
        return new Eig();
    }

    public static class Eig extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseMatrix matA = convertToMtj(inputs[0]);

            no.uib.cipr.matrix.EVD eig = new no.uib.cipr.matrix.EVD(matA.numRows());
            DenseMatrix tmp = new DenseMatrix(matA);

            DenseMatrix V = null;
            double []D = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                try {
                    // the input matrix is over written
                    tmp.set(matA);
                    EVD e = eig.factor(tmp);
                    V = e.getRightEigenvectors();
                    D = e.getRealEigenvalues();
                } catch (NotConvergedException e) {
                    throw new RuntimeException(e);
                }
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = CommonOps.diag(D);
            outputs[1] = mtjToEjml(V);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface qr() {
        return new QR();
    }

    public static class QR extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseMatrix matA = convertToMtj(inputs[0]);

            no.uib.cipr.matrix.QR qr = new no.uib.cipr.matrix.QR(matA.numRows(),matA.numColumns());
            DenseMatrix tmp = new DenseMatrix(matA);

            DenseMatrix Q = null;
            UpperTriangDenseMatrix R = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                // the input matrix is over written
                tmp.set(matA);
                qr.factor(tmp);

                Q = qr.getQ();
                R = qr.getR();
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = mtjToEjml(Q);
            outputs[1] = mtjToEjml(R);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface det() {
        return null;
    }

    @Override
    public AlgorithmInterface invert() {
        return new Inv();
    }

    public static class Inv extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseMatrix matA = convertToMtj(inputs[0]);

            DenseMatrix I = Matrices.identity(matA.numColumns());
            DenseMatrix inv = new DenseMatrix(matA.numColumns(),matA.numColumns());

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                matA.solve(I,inv);
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = mtjToEjml(inv);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface invertSymmPosDef() {
        return new InvSymmPosDef();
    }

    public static class InvSymmPosDef extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseMatrix matA = convertToMtj(inputs[0]);

            DenseCholesky cholesky = new DenseCholesky(matA.numRows(),false);
            LowerSPDDenseMatrix uspd = new LowerSPDDenseMatrix(matA);

            DenseMatrix result = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                // the input matrix is over written
                uspd.set(matA);
                if( !cholesky.factor(uspd).isSPD() ) {
                    throw new RuntimeException("Is not SPD");
                }

                result = cholesky.solve(Matrices.identity(matA.numColumns()));
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = mtjToEjml(result);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface add() {
        return new Add();
    }

    public static class Add extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseMatrix matA = convertToMtj(inputs[0]);
            DenseMatrix matB = convertToMtj(inputs[1]);

            DenseMatrix result = new DenseMatrix(matA.numRows(),matB.numColumns());

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                // in-place operator
                result.set(matA);
                result.add(matB);
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = mtjToEjml(result);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface mult() {
        return new Mult();
    }

    public static class Mult extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseMatrix matA = convertToMtj(inputs[0]);
            DenseMatrix matB = convertToMtj(inputs[1]);

            DenseMatrix result = new DenseMatrix(matA.numRows(),matB.numColumns());

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                matA.mult(matB,result);
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = mtjToEjml(result);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface multTransA() {
        return new MulTranA();
    }

    public static class MulTranA extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseMatrix matA = convertToMtj(inputs[0]);
            DenseMatrix matB = convertToMtj(inputs[1]);

            DenseMatrix result = new DenseMatrix(matA.numColumns(),matB.numColumns());

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                matA.transAmult(matB,result);
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = mtjToEjml(result);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface scale() {
        return new Scale();
    }

    public static class Scale extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseMatrix matA = convertToMtj(inputs[0]);

            DenseMatrix mod = new DenseMatrix(matA.numRows(),matA.numColumns());

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                // in-place operator
                mod.set(matA);
                mod.scale(ScaleGenerator.SCALE);
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = mtjToEjml(mod);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface solveExact() {
        return new Solve();
    }

    @Override
    public AlgorithmInterface solveOver() {
        return new Solve();
    }

    public static class Solve extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseMatrix matA = convertToMtj(inputs[0]);
            DenseMatrix matB = convertToMtj(inputs[1]);

            DenseMatrix result = new DenseMatrix(matA.numColumns(),matB.numColumns());

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                matA.solve(matB,result);
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = mtjToEjml(result);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface transpose() {
        return new Transpose();
    }

    public static class Transpose extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseMatrix matA = convertToMtj(inputs[0]);
            DenseMatrix result = new DenseMatrix(matA.numColumns(),matA.numRows());

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                matA.transpose(result);
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = mtjToEjml(result);
            return elapsedTime;
        }
    }
    
    /**
     * Converts a DenseMatrix64F in EML into a DenseMatrix in MTJ
     *
     * @param orig A DenseMatrix64F in EML
     * @return A DenseMatrix in MTJ
     */
    public static DenseMatrix convertToMtj( DenseMatrix64F orig )
    {
        DenseMatrix ret = new DenseMatrix(orig.getNumRows(),orig.getNumCols());

        // MTJ's format is the transpose of this format
        DenseMatrix64F temp = new DenseMatrix64F();
        temp.numRows = orig.numCols;
        temp.numCols = orig.numRows;
        temp.data = ret.getData();

        CommonOps.transpose(orig,temp);

        return ret;
    }

    public static DenseMatrix64F mtjToEjml( AbstractMatrix orig )
    {
        if( orig == null )
            return null;

        DenseMatrix64F ret = new DenseMatrix64F(orig.numRows(),orig.numColumns());

        for( int i = 0; i < ret.numRows; i++ ) {
            for( int j = 0; j < ret.numCols; j++ ) {
                ret.set(i,j,orig.get(i,j));
            }
        }

        return ret;
    }
}