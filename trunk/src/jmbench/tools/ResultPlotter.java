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
import jmbench.tools.stability.StabilityTrialResults;

import java.util.List;


/**
 * Generates different plots of the results.
 *
 * @author Peter Abeles
 */
public class ResultPlotter {

    public static void overflowPlots( List<StabilityTrialResults> data )
    {

    }

    public static void relativePlots( List<OperationResults> data ,
                                      MatrixLibrary refLib ,
                                      String fileName ,
                                      int whichMetric ,
                                      boolean savePDF ,
                                      boolean showWindow )
    {
        OperationResults refResults = findReferenceResults(data, refLib);

        if( refResults == null ) {
            System.out.println("Can't find reference.");
            return;
        }

        String opName = refResults.getOpName();
        OperationsVersusSizePlot splot = new OperationsVersusSizePlot(opName,"Relative Performance");

        splot.setLogScale(true,true);
        splot.setRange(0.1,20);
//        splot.setRange(0.01,20);

        EvaluationMetrics []opsRef = refResults.getMetrics();

        double results[] = new double[ opsRef.length ];

        if( fileName == null ) {
            fileName = opName;
        }

        int maxIndexRef = 0;
        for( ; maxIndexRef < opsRef.length; maxIndexRef++ ) {
            if( opsRef[maxIndexRef] == null )
                break;
        }

        int matDimen[] = new int[ maxIndexRef ];
        for( int i = 0; i < maxIndexRef; i++ ){
            matDimen[i] = refResults.getMatDimen()[i];
        }

        for( OperationResults ops : data ) {
            EvaluationMetrics []metrics = ops.metrics;

            for( int i = 0; i < maxIndexRef; i++ ) {
                if( opsRef[i] != null && metrics[i] != null ) {
                    results[i] = metrics[i].getMetric(whichMetric)/opsRef[i].getMetric(whichMetric);
                } else {
                    results[i] = Double.NaN;
                }
            }

            splot.addResults(matDimen,results,ops.getLibrary().getPlotName(),maxIndexRef);
        }
        
        if( savePDF )
            splot.savePDF(fileName+".pdf",500,350);
        if( showWindow )
            splot.displayWindow(500, 350);
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
}
