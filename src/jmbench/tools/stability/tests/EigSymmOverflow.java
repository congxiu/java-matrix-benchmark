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
import jmbench.tools.stability.StabilityBenchmark;
import jmbench.tools.stability.StabilityTestBase;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.ejml.ops.MatrixFeatures;
import org.ejml.ops.RandomMatrices;


/**
 * @author Peter Abeles
 */
public class EigSymmOverflow extends StabilityTestBase
        implements BreakingPointBinarySearch.Processor
{
    protected boolean overflow;
    protected int minLength;
    protected int maxLength;

    protected volatile DenseMatrix64F A;
    protected volatile DenseMatrix64F Ascaled;
    protected volatile DenseMatrix64F L;
    protected volatile DenseMatrix64F R;
    protected volatile double scaling;

    protected volatile BreakingPointBinarySearch search;

    public EigSymmOverflow(long randomSeed, StabilityOperationInterface operation,
                       int totalTrials, double breakingPoint, int minLength, int maxLength,
                       boolean overflow)
    {
        super(randomSeed, operation, totalTrials, breakingPoint);
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.overflow = overflow;
    }

    public EigSymmOverflow(){}

    @Override
    public void performTest() {
        if( overflow ) {
            scaling = 10.0;
        } else {
            scaling = 0.1;
        }

        search = new BreakingPointBinarySearch(this);

        for( int i = 0; i < totalTrials; i++ ) {
            int m;

            m = rand.nextInt(maxLength-minLength)+minLength;

            createMatrix(m);

            breakEig();

            saveResults();
        }
    }

    protected void createMatrix( int m ) {
        A = RandomMatrices.createSymmetric(m,-1,1,rand);
        Ascaled = new DenseMatrix64F(m,m);

        L = new DenseMatrix64F(m,m);
        R = new DenseMatrix64F(m,m);
    }

    private void breakEig() {

        reason = Reason.FINISHED;
        int where = search.findCriticalPoint(-1,findMaxPow(scaling));
        foundResult = Math.pow(scaling,where);
    }

    @Override
    public boolean check( int testPoint ) {
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
            reason = Reason.UNEXPECTED_EXCEPTION;
            return false;
        }

        if( results == null ) {
            reason = Reason.GRACEFULL_FAILURE;
            return false;
        }

        DenseMatrix64F D = results[0];
        DenseMatrix64F V = results[1];

        if(MatrixFeatures.hasUncountable(D) ||
                MatrixFeatures.hasUncountable(V)) {
            reason = Reason.UNCOUNTABLE;
            return false;
        }

        CommonOps.mult(Ascaled,V,L);
        CommonOps.mult(V,D,R);

        double error = StabilityBenchmark.residualError(L,R);

        if( error > breakingPoint ) {
            reason = Reason.LARGE_ERROR;
            return false;
        }

        return true;
    }

    @Override
    public String getTestName() {
        if( overflow )
            return "Eigenvalue Symmetric Overflow";
        else
            return "Eigenvalue Symmetric Underflow";
    }

    @Override
    public String getFileName() {
        if( overflow )
            return "EigSymmOverflow";
        else
            return "EigSymmUnderflow";
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
}
