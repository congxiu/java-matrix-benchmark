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

package jmbench.tools.memory;

import jmbench.interfaces.MemoryFactory;
import jmbench.tools.EvaluationTarget;
import jmbench.tools.SystemInfo;
import pja.util.UtilXmlSerialization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


/**
 * @author Peter Abeles
 */
public class MemoryBenchmark {

    String directorySave;

    public MemoryBenchmark() {
        directorySave = "results/"+System.currentTimeMillis();
    }

    public MemoryBenchmark( String directory ) {
        this.directorySave = directory;
    }

    public void performBenchmark( MemoryConfig config ) {
        File dir = new File(directorySave);
        if( !dir.exists() ) {
            if( !dir.mkdir() ) {
                throw new IllegalArgumentException("Can't make directories to save results.");
            }
        }

        SystemInfo info = new SystemInfo();
        info.grabCurrentInfo();

        try {
            UtilXmlSerialization.serializeXml(info,directorySave+"/info.xml");
            UtilXmlSerialization.serializeXml(config,directorySave+"/config.xml");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        long startTime = System.currentTimeMillis();

        processLibraries(config.libraries,config);

        long stopTime = System.currentTimeMillis();

        System.out.println("Finished Benchmark");
        System.out.println("  elapsed time "+(stopTime-startTime)+" (ms)");
    }

    private void processLibraries( List<EvaluationTarget> libs, MemoryConfig config ) {

        saveLibraryDescriptions(libs);

        for(  EvaluationTarget desc : libs ) {
            // run the benchmark
            MemoryFactory l = desc.loadAlgorithmFactory();

            String libOutputDir = directorySave+"/"+l.getLibraryInfo().getLibraryDirName();

            MemoryBenchmarkLibrary bench = new MemoryBenchmarkLibrary(config,l,desc.getJarFiles(),libOutputDir);

            bench.process();

            System.out.println("Finished Library Benchmark");
            System.out.println();
        }
    }

    /**
     * Save the description so that where this came from can be easily extracted
     */
    private void saveLibraryDescriptions( List<EvaluationTarget> libs )
    {
        for( EvaluationTarget desc : libs ) {
            try {
                String outputFile = directorySave+"/"+desc.getLib().getLibraryDirName()+".xml";
                UtilXmlSerialization.serializeXml(desc,outputFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main( String args[] ) throws IOException, InterruptedException {
        MemoryBenchmark master = new MemoryBenchmark();

        if( args.length > 0 ) {
            System.out.println("Loading config from xml...");
            MemoryConfig config = UtilXmlSerialization.deserializeXml(args[0]);
            if( config == null )
                throw new IllegalArgumentException("No config file found!");

            master.performBenchmark(config);
        } else {
            master.performBenchmark(MemoryConfig.createDefault());
        }
    }
}