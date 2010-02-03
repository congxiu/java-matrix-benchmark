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

package jmbench.tools.stability.tests;

import jmbench.interfaces.StabilityOperationInterface;
import jmbench.tools.OutputError;
import jmbench.tools.stability.StabilityBenchmark;
import jmbench.tools.stability.StabilityTestBase;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.ejml.ops.MatrixFeatures;
import org.ejml.ops.RandomMatrices;


/**
 * @author Peter Abeles
 */
public class EigSymmAccuracy extends StabilityTestBase {

    protected int minLength;
    protected int maxLength;

    protected volatile DenseMatrix64F A;
    protected volatile DenseMatrix64F L;
    protected volatile DenseMatrix64F R;

    public EigSymmAccuracy(long randomSeed, StabilityOperationInterface operation,
                           int totalTrials,
                           int minLength, int maxLength)
    {
        super(randomSeed, operation, totalTrials, 0);
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public EigSymmAccuracy() {}

    @Override
    public void performTest() {
        for( int i = 0; i < totalTrials; i++ ) {
//            System.out.print("Trial "+i+"  ");
            int m = rand.nextInt(maxLength-minLength)+minLength;

            createMatrix(m);
            evaluateEigenDecomposition();

            saveResults();
        }
    }

    protected void createMatrix( int m ) {
        A = RandomMatrices.createSymmetric(m,-1,1,rand);

        L = new DenseMatrix64F(m,m);
        R = new DenseMatrix64F(m,m);
    }


    private void evaluateEigenDecomposition() {
        reason = OutputError.NO_ERROR;
        foundResult = Double.NaN;

        DenseMatrix64F inputs[] = new DenseMatrix64F[]{A};
        DenseMatrix64F results[];
        try {
            results = operation.process(inputs);
        } catch( Exception e ) {
            addUnexpectedException(e);
//                e.printStackTrace();
            reason = OutputError.UNEXPECTED_EXCEPTION;
            return;
        }

        if( results == null ) {
            reason = OutputError.DETECTED_FAILURE;
            return;
        }

        DenseMatrix64F D = results[0];
        DenseMatrix64F V = results[1];

        if(MatrixFeatures.hasUncountable(D) ||
                MatrixFeatures.hasUncountable(V)) {
            reason = OutputError.UNCOUNTABLE;
            return;
        }

        CommonOps.mult(A,V,L);
        CommonOps.mult(V,D,R);

        foundResult = StabilityBenchmark.residualError(L,R);
    }

    @Override
    public String getTestName() {
        return "Eigen Value Symmetric Accuracy";
    }

    @Override
    public String getFileName() {
        return "EigSymmAccuracy";
    }

    @Override
    public long getInputMemorySize() {
        return 8*maxLength*maxLength*10;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}
