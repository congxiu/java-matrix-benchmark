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

import jmbench.tools.EvaluationTarget;
import jmbench.tools.runtime.OperationResults;
import jmbench.tools.runtime.RuntimeEvaluationMetrics;
import pja.util.UtilXmlSerialization;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Creates plots for all the results in a directory from raw xml files.  Plots are saved
 * int the plots directory.
 *
 * @author Peter Abeles
 */
public class PlotRuntimeResultsXml {

    File directory;

    // should it include native libraries while plotting results
    boolean plotNativeLibraries = true;

    // should it display results to the screen
    boolean displayResults = true;

    public PlotRuntimeResultsXml( String dir ) {
        directory = new File(dir);

        if( !directory.exists() ) {
            throw new IllegalArgumentException("Directory does not exist.");
        }

        if( !directory.isDirectory() ) {
            throw new IllegalArgumentException("Need to specify a directory.");
        }
    }

    @SuppressWarnings({"unchecked"})
    public void plot(int whichMetric) {
        String[] files = directory.list();

        Map<String, List> opMap = new HashMap<String,List>();

        for( String nameLevel0 : files ) {
            File level0 = new File(directory.getPath()+"/"+nameLevel0);

            if( level0.isDirectory() ) {
                // see if it should include this library in the results or not
                if( !checkIncludeLibrary(level0.getAbsolutePath()))
                    continue;

                String []files2 = level0.list();

                for( String name2 : files2 ) {
                    if( name2.contains(".xml") ) {

                        String stripName = name2.substring(0,name2.length()-4);
                        name2 = level0.getPath()+"/"+name2;

                        OperationResults r;
                        try {
                            r = UtilXmlSerialization.deserializeXml(name2);
                        } catch( ClassCastException e ) {
                            System.out.println("Couldn't deserialize "+name2);
                            continue;
                        }


                        List l;
                        if( opMap.containsKey(stripName) ) {
                            l = opMap.get(stripName);
                        } else {
                            l = new ArrayList();
                            opMap.put(stripName,l);
                        }
                        l.add(r);
                    }
                }
            }

        }

        for( String key : opMap.keySet() ) {
            List<OperationResults> l = opMap.get(key);

            RuntimePlotData plotData = convertToPlotData(l,whichMetric);

            String fileNameVar = directory.getPath()+"/plots/variability/"+key;
            String fileNameRel = directory.getPath()+"/plots/relative/"+key;
            String fileNameAbs = directory.getPath()+"/plots/absolute/"+key;

            RuntimeResultPlotter.Reference refType = RuntimeResultPlotter.Reference.MAX;
            RuntimeResultPlotter.variabilityPlots(l, fileNameVar,true,false);
            // TODO change key in the line below to plot name
            RuntimeResultPlotter.relativePlots(plotData, refType,null,fileNameRel,plotData.plotName,true,true);
            RuntimeResultPlotter.absolutePlots(plotData, fileNameAbs,plotData.plotName,true,false);
        }
    }

    private RuntimePlotData convertToPlotData( List<OperationResults> results , int whichMetric ) {
        OperationResults a = results.get(0);

        RuntimePlotData ret = new RuntimePlotData(a.matDimen,results.size());

        ret.plotName = a.getOpName();

        for( int i = 0; i < results.size(); i++ ) {
            a = results.get(i);
            ret.labels[i] = a.getLibrary().getPlotName();
            ret.plotLineType[i] = a.getLibrary().getPlotLineType();

            double r[] = ret.results[i];

            for( int j = 0; j < a.matDimen.length; j++ ) {
                RuntimeEvaluationMetrics m = a.getMetrics()[j];
                if( m != null )
                    r[j] = m.getMetric(whichMetric);
            }
        }

        return ret;
    }

    /**
     * Checks to see if the results from this library are being filters out or not
     *
     * @param pathDir Path to results directory
     * @return true if it should be included
     */
    private boolean checkIncludeLibrary(String pathDir) {
        EvaluationTarget target = UtilXmlSerialization.deserializeXml(pathDir+".xml");

        if( target == null ) {
            // no library info associated with this directory so its probably not a results directory
            return false;
        }

        return !(target.getLib().isNativeCode() && !plotNativeLibraries);
    }

    /**
     * Returns the path to the most recently modified directory in results.
     */
    public static String findMostRecentDirectory() {
        File baseDir = new File("results");

        if( !baseDir.exists() ) {
            throw new RuntimeException("Can't find results directory.  Try running one level up from it.");
        }

        File bestDir = null;
        long bestTime = 0;

        for( File f : baseDir.listFiles() ) {
            if( f.isDirectory() ) {
                long time = f.lastModified();
                if( time > bestTime ) {
                    bestDir = f;
                    bestTime = time;
                }
            }
        }

        if( bestDir == null ) {
            throw new RuntimeException("Can't find any directories in results/");
        }

        return bestDir.getPath();
    }

    public static void printHelp() {
        System.out.println("Creates plots from raw XML file results.  The plots can be generated from " +
                "different statistical metric and filtered based on library features.");
        System.out.println();
        System.out.println("--PlotNative=<true|false>    : Turns plotting results from native libraries on and off.");
        System.out.println("--Metric=<?>                 : Changes the metric that is plotted.");
        System.out.println("                             : MAX,MIN,STDEV,MEDIAN,MEAN");
        System.out.println("--Display=<true|false>       : If true some results will be displayed.");
        System.out.println();
        System.out.println("The last argument is the directory that contains the results.  If this is not specified");
        System.out.println("then the most recently modified directory is used.");
    }

    public static void main( String args[] ) {
        String inputDirectory = null;
        boolean plotNative = true;
        int metric = RuntimeEvaluationMetrics.METRIC_MAX;
        boolean displayResults = true;

        boolean failed = false;

        for( int i = 0; i < args.length; i++ ) {
            String splits[] = args[i].split("=");

            String flag = splits[0];

            if( flag.length() < 2 || flag.charAt(0) != '-' || flag.charAt(0) != '-') {
                inputDirectory = args[i];
                break;
            }

            flag = flag.substring(2);

            if( flag.compareTo("PlotNative") == 0 ) {
                if( splits.length != 2 ) {failed = true; break;}
                plotNative = Boolean.parseBoolean(splits[1]);
                System.out.println("PlotNative = "+plotNative);
            } else if( flag.compareTo("Metric") == 0 ) {
                if( splits.length != 2 ) {failed = true; break;}
                if( splits[1].compareToIgnoreCase("MAX") == 0 ) {
                    metric = RuntimeEvaluationMetrics.METRIC_MAX;
                } else if( splits[1].compareToIgnoreCase("MIN") == 0 ) {
                    metric = RuntimeEvaluationMetrics.METRIC_MIN;
                } else if( splits[1].compareToIgnoreCase("STDEV") == 0 ) {
                    metric = RuntimeEvaluationMetrics.METRIC_STDEV;
                } else if( splits[1].compareToIgnoreCase("MEDIAN") == 0 ) {
                    metric = RuntimeEvaluationMetrics.METRIC_MEDIAN;
                } else if( splits[1].compareToIgnoreCase("MEAN") == 0 ) {
                    metric = RuntimeEvaluationMetrics.METRIC_MEAN;
                } else {
                    throw new RuntimeException("Unknown metric: "+splits[1]);
                }
            } else if( flag.compareTo("Display") ==0 ) {
                if( splits.length != 2 ) {failed = true; break;}
                displayResults = Boolean.parseBoolean(splits[1]);
                System.out.println("Display = "+displayResults);
            } else {
                System.out.println("Unknown flag: "+flag);
                failed = true;
                break;
            }
        }

        if( failed ) {
            printHelp();
            throw new RuntimeException("Parsing arguments failed");
        }

        if( inputDirectory == null )
            inputDirectory = findMostRecentDirectory();

        System.out.println("Parsing "+inputDirectory);

        PlotRuntimeResultsXml p = new PlotRuntimeResultsXml(inputDirectory);

        p.plotNativeLibraries = plotNative;
        p.displayResults = displayResults;
        p.plot(metric);
    }
}
