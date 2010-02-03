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

package jmbench.tools;

import jmbench.impl.MatrixLibrary;
import jmbench.plots.OperationsVersusSizePlot;
import jmbench.tools.runtime.OperationResults;
import jmbench.tools.runtime.RuntimeEvaluationMetrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Generates different plots of the results.
 *
 * @author Peter Abeles
 */
public class ResultPlotter {

    public static void relativePlots( List<OperationResults> data ,
                                      Reference referenceType ,
                                      MatrixLibrary refLib ,
                                      String fileName ,
                                      int whichMetric ,
                                      boolean savePDF ,
                                      boolean showWindow )
    {
        OperationResults refResults = null;
        if( refLib != null ) {
            refResults = findReferenceResults(data, refLib);

            if( refResults == null ) {
                System.out.println("Can't find reference.");
                return;
            }
        }

        String opName = data.get(0).getOpName();
        OperationsVersusSizePlot splot = new OperationsVersusSizePlot(opName,"Relative Performance");

        splot.setLogScale(true,true);
        splot.setRange(0.1,20);
//        splot.setRange(0.01,20);

        int numMatrixSizes = getNumMatrices(data);

        double results[] = new double[ numMatrixSizes ];
        double refValue[] = new double[ numMatrixSizes ];
        int matDimen[] = new int[ numMatrixSizes ];

        if( fileName == null ) {
            fileName = opName;
        }

        for( int i = 0; i <numMatrixSizes; i++ ) {
            refValue[i] = getReferenceValue(data,refResults,i,whichMetric,referenceType);
        }

        for( int i = 0; i < numMatrixSizes; i++ ){
            matDimen[i] = getMatrixSize(data,i);
        }

        for( OperationResults ops : data ) {
            RuntimeEvaluationMetrics[]metrics = ops.metrics;

            for( int i = 0; i < numMatrixSizes; i++ ) {
                if( !Double.isNaN(refValue[i]) && metrics[i] != null ) {
                    results[i] = metrics[i].getMetric(whichMetric)/refValue[i];
                } else {
                    results[i] = Double.NaN;
                }
            }

            splot.addResults(matDimen,results,ops.getLibrary().getPlotName(),numMatrixSizes,
                    ops.getLibrary().getPlotLineType());
        }
        
        if( savePDF )
            splot.savePDF(fileName+".pdf",500,350);
        if( showWindow )
            splot.displayWindow(500, 350);
    }

    private static int getNumMatrices( List<OperationResults> data ) {
        int max = 0;

        for( OperationResults d : data ) {
            int sizes[] = d.getMatDimen();

            if( sizes.length > max )
                max = sizes.length;
        }

        return max;
    }

    private static int getMatrixSize( List<OperationResults> data , int index)
    {
        for( OperationResults d : data ) {
            int sizes[] = d.getMatDimen();

            if( sizes.length >= index ) {
                return sizes[index];
            }
        }

        throw new RuntimeException("Couldnt find a match");
    }

    private static double getReferenceValue( List<OperationResults> data ,
                                             OperationResults refLib ,
                                             int matrixSize ,
                                             int whichMetric ,
                                             Reference referenceType )
    {
        if( referenceType == Reference.LIBRARY ) {
            RuntimeEvaluationMetrics[]opsRef = refLib.getMetrics();

            if( matrixSize >= opsRef.length || opsRef[matrixSize] == null ) {
                return Double.NaN;
            }

            return opsRef[matrixSize].getMetric(whichMetric);
        }

        List<Double> results = new ArrayList<Double>();

        for( OperationResults d : data ) {
            RuntimeEvaluationMetrics[]opsRef = d.getMetrics();

            if( matrixSize >= opsRef.length || opsRef[matrixSize] == null ) {
                continue;
            }

            results.add( opsRef[matrixSize].getMetric(whichMetric) );
        }

        if( results.size() == 0 )
            return Double.NaN;

        switch( referenceType ) {
            case MEAN:
                double total = 0;
                for( double d : results )
                    total += d;
                return total / results.size();

            case MEDIAN:
                Collections.sort(results);
                return results.get(results.size()/2);
        }

        throw new RuntimeException("Unknown reference type");
    }

    private static OperationResults findReferenceResults(List<OperationResults> data,
                                                         MatrixLibrary refLib) {
        OperationResults refResults = null;
        // find the results it is in reference to
        for( OperationResults o : data ) {
            if( o.getLibrary() == refLib ) {
                refResults = o;
                break;
            }
        }

        return refResults;
    }

    public static enum Reference
    {
        LIBRARY,
        MEAN,
        MEDIAN
    }
}
