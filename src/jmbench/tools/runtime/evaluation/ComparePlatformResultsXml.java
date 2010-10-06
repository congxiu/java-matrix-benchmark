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

package jmbench.tools.runtime.evaluation;

import jmbench.impl.MatrixLibrary;
import jmbench.tools.runtime.OperationResults;
import jmbench.tools.runtime.RuntimeEvaluationMetrics;
import pja.util.UtilXmlSerialization;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Compares results that are contained in two different results directory.  Typically this will be used to compare
 * results from the same library on different platforms or from different versions.  Results are saved to the
 * "plots" directory.
 *
 * @author Peter Abeles
 */
public class ComparePlatformResultsXml {

    // should it display results to the screen
    private static boolean displayResults = true;

    // only plot results more than this size
    private int minMatrixSize = 0;
    // it will only plot results which are of this size or less
    private int maxMatrixSize = 0;

    // which results it will plot
    List<Platform> platforms = new ArrayList<Platform>();

    /**
     * Adds a new platform to compare.
     *
     * @param resultsDir Where the results are stored.
     * @param library Which library in the results directory is being examined.
     * @param plotName What should the plot be named.
     */
    public void addPlatform( String resultsDir , MatrixLibrary library , String plotName )  {
        Platform p = new Platform();
        p.resultsDir = resultsDir;
        p.library = library;
        p.plotName = plotName;

        platforms.add(p);
    }

    /**
     * Plot the results.
     *
     * @param whichMetric The metric which will be plotted. See {@link RuntimeEvaluationMetrics}.
     */
    public void plot(int whichMetric) {
        // map containing all the results
        Map<String,RuntimePlotData> plotMap = new HashMap<String,RuntimePlotData>();

        loadResults(plotMap,whichMetric);

        // save the output to the current directory
        File outputDir = new File(".");

        createPlots(minMatrixSize,maxMatrixSize,outputDir, plotMap);
    }

    private void loadResults(Map<String,RuntimePlotData> plotMap , int whichMetric ) {

        // give each library its own unique plot line
        int plotType = 0;

        for( Platform p : platforms ) {

            String platformResultsDir = p.resultsDir+"/"+p.library.getLibraryDirName();

            File platformDir = new File(platformResultsDir);

            File[] files = platformDir.listFiles();

            for( File f : files ) {
                String fileName = f.getName();

                if( fileName.contains(".xml")) {
                    // extract the operation name
                    String stripName = fileName.substring(0,fileName.length()-4);

                    OperationResults r = UtilXmlSerialization.deserializeXml(f.getAbsolutePath());

                    // result the plot data or grab it if it already exists
                    RuntimePlotData l;
                    if( plotMap.containsKey(stripName) ) {
                        l = plotMap.get(stripName);
                    } else {
                        l = new RuntimePlotData(null);
                        l.plotName = stripName;
                        plotMap.put(stripName,l);
                    }

                    // set the matrix array to be the one with the most results
                    if( l.matrixSize == null || l.matrixSize.length < r.getMatDimen().length ) {
                        l.matrixSize = r.getMatDimen();
                    }

                    // extract the results for a specific metric
                    double results[] = new double[ l.matrixSize.length ];
                    for( int i = 0; i < results.length; i++ ) {
                        RuntimeEvaluationMetrics m = r.metrics.length <= i ? null : r.getMetrics()[i];
                        if( m != null )
                            results[i] = m.getMetric(whichMetric);
                        else
                            results[i] = Double.NaN;
                    }

                    l.addLibrary(p.plotName,results,plotType);
                }
            }

            plotType++;
        }
    }

    /**
     * Creates relative runtime and absolute runtime plots.  Relative runtime plots are displayed and both types
     * are saved to disk as pdf files.
     */
    public static void createPlots( int minMatrixSize , int maxMatrixSize , File outputDirectory ,
                                    Map<String,RuntimePlotData> plotMap ) {
        for( String key : plotMap.keySet() ) {
            RuntimePlotData plotData = plotMap.get(key);

            PlotRuntimeResultsXml.truncatePlotData(minMatrixSize,maxMatrixSize,plotData);

            String fileNameRel = outputDirectory.getPath()+"/plots/relative/"+key;
            String fileNameAbs = outputDirectory.getPath()+"/plots/absolute/"+key;

            RuntimeResultPlotter.Reference refType = RuntimeResultPlotter.Reference.MAX;

            RuntimeResultPlotter.relativePlots(plotData, refType,null,fileNameRel,plotData.plotName,true,displayResults);
            RuntimeResultPlotter.absolutePlots(plotData, fileNameAbs,plotData.plotName,true,false);
        }
    }

    private static class Platform
    {
        String resultsDir;
        MatrixLibrary library;
        public String plotName;
    }

    public static void main( String args[] ) {
        ComparePlatformResultsXml app = new ComparePlatformResultsXml();

        app.addPlatform("results/PentiumM_2010_08",MatrixLibrary.EJML,"Pentium-M");
        app.addPlatform("results/Q9400_2010_08",MatrixLibrary.EJML,"Q9400");
        app.addPlatform("results/runtime_xeon_2010_08",MatrixLibrary.EJML,"Xeon");

//        app.addPlatform("results/PentiumM_2010_08",MatrixLibrary.OJALGO,"Pentium-M");
//        app.addPlatform("results/Q9400_2010_08",MatrixLibrary.OJALGO,"Q9400");
//        app.addPlatform("results/runtime_xeon_2010_08",MatrixLibrary.OJALGO,"Xeon");

        app.plot(RuntimeEvaluationMetrics.METRIC_MIN);
    }
}
