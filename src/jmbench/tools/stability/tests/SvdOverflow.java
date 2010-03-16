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
import jmbench.tools.BenchmarkToolsMasterApp;
import jmbench.tools.OutputError;
import jmbench.tools.stability.StabilityBenchmark;
import jmbench.tools.stability.StabilityTestBase;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.SimpleMatrix;
import org.ejml.ops.CommonOps;
import org.ejml.ops.MatrixFeatures;
import org.ejml.ops.RandomMatrices;


/**
 * @author Peter Abeles
 */
public class SvdOverflow extends StabilityTestBase implements BreakingPointBinarySearch.Processor {

    // should it test overflow or underflow
    protected boolean overflow;

    protected int minLength;
    protected int maxLength;

    private volatile DenseMatrix64F A;
    private volatile DenseMatrix64F Ascaled;
    private volatile double sv[];
    private volatile double scaling;

    private volatile BreakingPointBinarySearch search;

    public SvdOverflow(long randomSeed, StabilityOperationInterface operation,
                       int totalTrials, double breakingPoint, int minLength, int maxLength,
                       boolean overflow)
    {
        super(randomSeed, operation, totalTrials, breakingPoint);
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.overflow = overflow;
    }

    public SvdOverflow(){}

    @Override
    public void performTest() {
        if( overflow )
            scaling = 10.0;
        else
            scaling = 0.1;

        search = new BreakingPointBinarySearch(this);

        for( int i = 0; i < totalTrials; i++ ) {
            int m,n;

            m = rand.nextInt(maxLength-minLength)+minLength;
            n = minLength;
            if( m > minLength )
                n += rand.nextInt(m-minLength);

            Ascaled = new DenseMatrix64F(m,n);

            createMatrix(m,n,1);

            breakSVD();

            saveResults();
        }
    }

    protected void createMatrix( int m, int n, double svMag ) {
//        System.out.println("Matrix size = ("+m+" , "+n+" )");
        DenseMatrix64F U = RandomMatrices.createOrthogonal(m,m,rand);
        DenseMatrix64F V = RandomMatrices.createOrthogonal(n,n,rand);

        int o = Math.min(m,n);

        // randomly generate singular values and put into ascending order
        sv = new double[o];
        for( int i = 0; i < o; i++ )
        // perturb it from being exactly svMag since that is a pathological case for some
        // algorithms and not common in real world scenarios
            sv[i] = svMag+rand.nextDouble()* BenchmarkToolsMasterApp.SMALL_PERTURBATION;

        A = SolverCommon.createMatrix(U,V,sv);
    }

    private void breakSVD() {

        reason = OutputError.NO_ERROR;
        int index = search.findCriticalPoint(-1,findMaxPow(scaling));
        foundResult = Math.pow(scaling,index);
    }

    @Override
    public String getTestName() {
        if( overflow)
            return "SVD Overflow";
        else
            return "SVD Underflow";
    }

    @Override
    public String getFileName() {
        if( overflow )
            return "SvdOverflow";
        else
            return "SvdUnderflow";
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

    public boolean isOverflow() {
        return overflow;
    }

    public void setOverflow(boolean overflow) {
        this.overflow = overflow;
    }

    @Override
    public boolean check(int testPoint) {
        double scale = Math.pow(scaling,testPoint);

        Ascaled.set(A);
        CommonOps.scale(scale,Ascaled);

        DenseMatrix64F inputs[] = new DenseMatrix64F[]{Ascaled};
        DenseMatrix64F results[];
        try {
            results = operation.process(inputs);
        } catch( Exception e ) {
            addUnexpectedException(e);
//                e.printStackTrace();
            reason = OutputError.UNEXPECTED_EXCEPTION;
            return false;
        }

        if( results == null ) {
            reason = OutputError.DETECTED_FAILURE;
            return false;
        }

        SimpleMatrix U = SimpleMatrix.wrap(results[0]);
        SimpleMatrix S = SimpleMatrix.wrap(results[1]);
        SimpleMatrix V = SimpleMatrix.wrap(results[2]);

        if(MatrixFeatures.hasUncountable(U.getMatrix()) ||
                MatrixFeatures.hasUncountable(S.getMatrix()) ||
                MatrixFeatures.hasUncountable(V.getMatrix()) ) {
            reason = OutputError.UNCOUNTABLE;
            return false;
        }

        DenseMatrix64F foundA = U.mult(S).mult(V.transpose()).getMatrix();

        double error = StabilityBenchmark.residualError(foundA,Ascaled);

        if( error > breakingPoint ) {
            reason = OutputError.LARGE_ERROR;
            return false;
        }

        return true;
    }
}