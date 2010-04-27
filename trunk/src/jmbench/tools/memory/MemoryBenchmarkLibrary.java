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

import jmbench.impl.MatrixLibrary;
import jmbench.impl.memory.EjmlMemoryFactory;
import jmbench.interfaces.MemoryFactory;
import jmbench.interfaces.MemoryProcessorInterface;
import jmbench.tools.BenchmarkTools;
import jmbench.tools.EvaluatorSlave;
import pja.util.UtilXmlSerialization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * @author Peter Abeles
 */
public class MemoryBenchmarkLibrary {

    Random rand;

    MatrixLibrary libInfo;
    MemoryConfig config;

    BenchmarkTools tool = new BenchmarkTools();

    List<Task> activeTasks = new ArrayList<Task>();

    String directorySave;

    private PrintStream logStream = System.err;

    public MemoryBenchmarkLibrary( MemoryConfig config ,
                                   MemoryFactory factory ,
                                   List<String> jarNames ,
                                   String directorySave ) {
        this.rand = new Random(config.seed);
        this.config = config;
        this.directorySave = directorySave;
        this.libInfo = factory.getLibraryInfo();

        tool.setJars(jarNames);
        tool.setVerbose(false);

        String libraryName = factory.getLibraryInfo().getPlotName();

        if( config.add )
            addOperation(config, factory.add(), "add", libraryName, 3 , config.matrixSizeLarge);

        if( config.mult )
            addOperation(config, factory.mult(), "mult", libraryName, 3 , config.matrixSizeLarge);

        if( config.solveLinear )
            addOperation(config, factory.solveEq(), "solveLinear", libraryName, 3 , config.matrixSizeLarge);

        if( config.solveLS )
            addOperation(config, factory.solveLS(), "solveLS", libraryName,3 , config.matrixSizeLarge);

        if( config.svd )
            addOperation(config, factory.svd(), "svd", libraryName,3 , config.matrixSizeSmall);

        if( config.eig )
            addOperation(config, factory.eig(), "eig", libraryName,3 , config.matrixSizeSmall);

        if( directorySave != null ) {
            setupOutputDirectory();
            setupLog();
        }
    }

    private void addOperation(MemoryConfig config, MemoryProcessorInterface op,
                              String opName , String libraryName ,
                              int scale , int matrixSize ) {
        if( op != null ) {
            activeTasks.add( new Task(op,
                    opName,libraryName,config.maxTestTimeMilli,scale,matrixSize));
        }
    }

    public void process() {
        while( !activeTasks.isEmpty() ) {
            Task task = activeTasks.get( rand.nextInt(activeTasks.size()));

            System.out.println(libInfo.getVersionName()+" operation "+task.results.nameOp);

            long mem = findMinimumMemory(task);

            boolean remove = false;

            if( mem > 0 ) {
                task.results.results.add(mem);
                System.out.println("  memory: "+mem);
                if( task.results.results.size() >= config.numTrials ) {
                    remove = true;
                }
            } else {
                remove = true;
            }

            saveResults(task.results);

            if( remove ) {
                activeTasks.remove(task);
            }
        }
    }

    private void setupOutputDirectory() {
        File d = new File(directorySave);
        if( !d.exists() ) {
            if( !d.mkdirs() ) {
                throw new IllegalArgumentException("Failed to make output directory");
            }
        } else if( !d.isDirectory())
            throw new IllegalArgumentException("The output directory already exists and is not a directory");
    }

    /**
     * Sets out a file for recording errors.
     */
    private void setupLog() {
        try {
            logStream = new PrintStream(directorySave+"/log.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // For debugging purposes output the slave's classpath
        logStream.println("Current directory = "+new File(".").getAbsolutePath());
        logStream.println("Classpath:");
        logStream.println(tool.getClassPath());
    }

    private void saveResults( MemoryResults results  ) {
        if( directorySave == null )
            return;

        try {
            UtilXmlSerialization.serializeXml(results,directorySave+"/"+results.nameOp+".xml");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public long findMinimumMemory( Task task )
    {
        // lower bound for memory
        long lowerMem=0;
        // upper bound for memory
        long upperMem=Long.MAX_VALUE;
        // what is currently being tested
        long testPoint = task.matrixSize*task.memoryScale;

        int numFroze = 0;


        while( upperMem - lowerMem > config.accuracy ) {
            System.out.print("*");
            tool.setFrozenDefaultTime(task.timeout);
            tool.setOverrideMemory(testPoint);

            MemoryTest test = new MemoryTest();
            test.setup(task.op,config.numCycles,task.matrixSize);
            test.setRandomSeed(config.seed);

            EvaluatorSlave.Results results = tool.runTest(test);

            if( results == null ) {
                System.out.println("Returned NULL");
                return -1;
            }

            if( results.failed == null ) {
//                System.out.println("  Result: "+testPoint);
                upperMem = testPoint;
                MemoryTest.Results r = (MemoryTest.Results)results.getResults().get(0);

                // now that it knows how long it should take adjust the time out time
                if( r.elapsedTime*3 < task.timeout) {
                    task.timeout = r.elapsedTime*3;
                }

            } else if( results.failed == EvaluatorSlave.FailReason.OUT_OF_MEMORY ) {
//                System.out.println("  Ran out of memory: "+testPoint);
                lowerMem = testPoint;
            } else if( results.failed == EvaluatorSlave.FailReason.FROZEN ) {
//                System.out.println("  Froze: "+testPoint);
                lowerMem = testPoint;
                numFroze++;
                // if it hasn't found an upper limit and keeps on timing out
                // the max time is probably too sort
                if( upperMem == Long.MAX_VALUE && numFroze >= 2) {
                    task.results.error = results.failed;
                    System.out.println("FROZEN FATAL ERROR");
                    return -1;
                }
            } else {
                System.out.println("FATAL ERROR: "+results.failed);
                task.results.error = results.failed;
                task.results.errorMessage = results.detailedError;
                return -1;
            }

            if( upperMem == Long.MAX_VALUE ) {
                testPoint *= 2;
            } else {
                testPoint = (upperMem+lowerMem)/2;
            }
        }

        return (upperMem+lowerMem)/2;
    }

    private static class Task {
        MemoryProcessorInterface op;
        MemoryResults results;

        long timeout;

        int memoryScale;

        int matrixSize;

        public Task(MemoryProcessorInterface op ,
                    String nameOp , String nameLibrary ,
                    long time ,
                    int memoryScale ,
                    int matrixSize ) {
            this.op = op;
            this.timeout = time;
            results = new MemoryResults();
            results.nameOp = nameOp;
            results.nameLibrary = nameLibrary;
            this.memoryScale = memoryScale;
            this.matrixSize = matrixSize;
        }
    }

    public static void main( String []args ) {
        MemoryConfig config = MemoryConfig.createDefault();

        MemoryBenchmarkLibrary benchmark = new MemoryBenchmarkLibrary(config,
                new EjmlMemoryFactory(),null,null);

        benchmark.process();
    }
}
