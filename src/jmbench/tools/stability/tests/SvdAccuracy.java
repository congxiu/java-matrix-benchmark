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
import org.ejml.data.SimpleMatrix;
import org.ejml.ops.MatrixFeatures;
import org.ejml.ops.RandomMatrices;

import java.util.Arrays;


/**
 * @author Peter Abeles
 */
public class SvdAccuracy extends StabilityTestBase {

    protected int minLength;
    protected int maxLength;

    private volatile DenseMatrix64F A;
    private volatile double sv[];

    public SvdAccuracy(long randomSeed, StabilityOperationInterface operation, 
                       int totalTrials, int minLength, int maxLength)
    {
        super(randomSeed, operation, totalTrials, 0);
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public SvdAccuracy(){}

    @Override
    public void performTest() {
        for( int i = 0; i < totalTrials; i++ ) {
//            System.out.print("Trial "+i+"  ");
            int m,n;

            m = rand.nextInt(maxLength-minLength)+minLength;
            n = minLength;
            if( m > minLength )
                n += rand.nextInt(m-minLength);

            int o = Math.min(m,n);
            int numS = rand.nextInt(o);

            while( numS == 0 ) {
                numS = rand.nextInt(o);
            }

            createMatrix(m,n,50,numS);

            evaluateSVD();

            saveResults();
        }
    }

    protected void createMatrix( int m, int n, double maxMag , int numS ) {
//        System.out.println("Matrix size = ("+m+" , "+n+" )");
        DenseMatrix64F U = RandomMatrices.createOrthogonal(m,m,rand);
        DenseMatrix64F V = RandomMatrices.createOrthogonal(n,n,rand);

        int o = Math.min(m,n);

        // randomly generate singular values and put into ascending order
        sv = new double[o];
        for( int i = 0; i < numS; i++ )
            sv[i] = -rand.nextDouble()*maxMag;

        Arrays.sort(sv);
        for( int i = 0; i < numS; i++ )
            sv[i] = -sv[i];

        A = SolverCommon.createMatrix(U,V,sv);
    }


    private void evaluateSVD() {
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

        SimpleMatrix U = SimpleMatrix.wrap(results[0]);
        SimpleMatrix S = SimpleMatrix.wrap(results[1]);
        SimpleMatrix V = SimpleMatrix.wrap(results[2]);

        if(MatrixFeatures.hasUncountable(U.getMatrix()) ||
                MatrixFeatures.hasUncountable(S.getMatrix()) ||
                MatrixFeatures.hasUncountable(V.getMatrix()) ) {
            reason = OutputError.UNCOUNTABLE;
            return;
        }

        DenseMatrix64F foundA = U.mult(S).mult(V.transpose()).getMatrix();

        foundResult = StabilityBenchmark.residualError(foundA,A);
    }

    @Override
    public String getTestName() {
        return "SVD Accuracy";
    }

    @Override
    public String getFileName() {
        return "SvdAccuracy";
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
