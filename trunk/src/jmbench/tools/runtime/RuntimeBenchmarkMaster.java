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

import jmbench.interfaces.LibraryAlgorithmFactory;
import jmbench.tools.EvaluationTarget;
import jmbench.tools.SystemInfo;
import pja.util.UtilXmlSerialization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


/**
 * <p>
 * RuntimeBenchmarkMaster runs a series of benchmark tests against a set of linear algebra libraries.
 * These libraries are evaluated using {@link RuntimeBenchmarkLibrary}.  The results are
 * put into a directory that is in the results directory whose name is set to the integer value
 * returned by System.currentTimeMillis().  In addition information on the system the test was run on
 * and error logs are all saved.
 * </p>
 * 
 * @author Peter Abeles
 */
public class RuntimeBenchmarkMaster {

    // where should the results be saved to
    private String directorySave;

    public RuntimeBenchmarkMaster() {
        directorySave = "results/"+System.currentTimeMillis();
    }

    public RuntimeBenchmarkMaster( String directory ) {
        this.directorySave = directory;
    }

    /**
     * Perform the benchmark tests against all the different algortihms
     */
    public void performBenchmark( RuntimeBenchmarkConfig config ) {

        saveSystemInfo(config);

        long startTime = System.currentTimeMillis();

        processLibraries(config.getTargets(),config);

        System.out.println("Total processing time = "+(System.currentTimeMillis()-startTime)/1000.0);
    }

    private void processLibraries( List<EvaluationTarget> libs, RuntimeBenchmarkConfig config ) {


        for( EvaluationTarget desc : libs ) {

            String libOutputDir = directorySave+"/"+desc.getLib().getDirName();

            // save the description so that where this came from can be easily extracted
            try {
                String outputFile = libOutputDir+".xml";
                UtilXmlSerialization.serializeXml(desc,outputFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            // run the benchmark
            LibraryAlgorithmFactory l = desc.loadAlgorithmFactory();

            RuntimeBenchmarkLibrary benchmark = new RuntimeBenchmarkLibrary(libOutputDir,l,
                    desc.getJarFiles(),desc.getLib(),
                    config);

            try {
                benchmark.performBenchmark();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Collects information of the system that this is being run on.  Allows for a better understanding
     * of the results.  Not all relevant information can be gathered since this is java.
     *
     * The benchmark config is also saved here.
     */
    private void saveSystemInfo(RuntimeBenchmarkConfig config) {
        SystemInfo info = new SystemInfo();
        info.grabCurrentInfo();

        File dir = new File(directorySave);
        if( !dir.exists() ) {
            if( !dir.mkdir() ) {
                throw new IllegalArgumentException("Can't make directories to save results.");
            }
        }

        try {
            UtilXmlSerialization.serializeXml(info,directorySave+"/info.xml");
            UtilXmlSerialization.serializeXml(config,directorySave+"/config.xml");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main( String args[] ) throws IOException, InterruptedException {
        RuntimeBenchmarkMaster master = new RuntimeBenchmarkMaster();

        if( args.length > 0 ) {
            System.out.println("Loading config from xml...");
            RuntimeBenchmarkConfig config = UtilXmlSerialization.deserializeXml(args[0]);
            if( config == null )
                throw new IllegalArgumentException("No config file found!");

            master.performBenchmark(config);
        } else {
            master.performBenchmark(RuntimeBenchmarkConfig.createAllConfig());
        }
    }
}
