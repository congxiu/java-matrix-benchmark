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

import jmbench.impl.MatrixLibrary;
import jmbench.impl.runtime.EjmlAlgorithmFactory;
import jmbench.interfaces.LibraryAlgorithmFactory;
import jmbench.tools.BenchmarkTools;
import jmbench.tools.EvaluationTest;
import jmbench.tools.EvaluatorSlave;
import jmbench.tools.TestResults;
import pja.util.UtilXmlSerialization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * <p>
 * RuntimeBenchmarkLibrary performs a series of runtime performance benchmark tests against a
 * specific linear algebra library.  These test are composed of typical matrix operations
 * that are performed against matrices of various sizes.  For each test a new process is spawned.
 * Spawning a new process improves the stability of the results.
 * </p>
 *
 * <p>
 * Definitions:<br>
 * <DL>
 * <DT> Case
 *   <DD> A case is a set of algorithms (that all perform the same operation) and matrix sizes.
 * <DT> Block
 *   <DD> A block is a set of identical trials that are performed in a new instance of the JavaVM.
 * <DT> Trial
 *   <DD> The operations per second is computed in a trial by running an algorithm for numerious cycles
 * with the same inputs.
 * </DL>
 * </p>
 *
 * <p>
 * Performance is defined by how many operations it can perform in a second, ops/sec.  For each algorithm
 * this is computed several times.  The raw results and computed metrics are all saved to a file.  To
 * ensure a statistically significant number of operations is performed, the number of computations performed
 * is dynamically adjusted so that the total time is approximately a predetermined length.
 * </p>
 *
 * <p>
 * For each block a new javavm is spawned.  To allow these tests to run on computers with less resources
 * the amount of memory allocated to the VM is dynamically computed based on the size of the input matrices.
 * </p>
 *
 *
 * @author Peter Abeles
 */
public class RuntimeBenchmarkLibrary {

    // used to randomize the order of processes
    private Random rand;

    // have a different random seed for each block
    private long randSeed[];

    // where the results be saved to
    private String directorySave;

    // used to write errors to
    private PrintStream logStream;

    // true if an evaluation case failed
    private boolean caseFailed;
    // is it too slow to continue testing
    private boolean tooSlow;

    private LibraryAlgorithmFactory library;

    private BenchmarkTools tools;

    private MatrixLibrary libraryType;

    private RuntimeBenchmarkConfig config;

    public RuntimeBenchmarkLibrary( String outputDir , LibraryAlgorithmFactory library  ,
                             List<String> jarNames , MatrixLibrary libraryType,
                             RuntimeBenchmarkConfig config )
    {
        this.config = config;


        this.directorySave = outputDir;

        File d = new File(directorySave);
        if( !d.exists() ) {
            if( !d.mkdir() ) {
                throw new IllegalArgumentException("Failed to make output directory");
            }
        } else if( !d.isDirectory())
            throw new IllegalArgumentException("The output directory already exists and is not a directory");
        
        this.library = library;

        // create the random seeds for each block
        this.randSeed = new long[ config.numBlocks ];
        this.rand = new Random(config.seed);

        for( int i = 0; i < this.randSeed.length; i++ ) {
            this.randSeed[i] = rand.nextLong();
        }

        tools = new BenchmarkTools(config.numBlockTrials,config.memorySlaveBase,config.memorySlaveScale,jarNames);
        tools.setFrozenScale(20);

        this.libraryType = libraryType;
    }

    /**
     * Perform the benchmark tests against all the different algortihms
     */
    public void performBenchmark() throws FileNotFoundException {
        setupLog();

        List<RuntimeEvaluationCase> cases = new FactoryRuntimeEvaluationCase(library,config).createCases();

        List<CaseState> states = createCaseList(cases);

        long startTime = System.currentTimeMillis();

        while(!states.isEmpty()) {
            // if random is true then select the next operation block that is to be benchmarked randomly
            int index = config.randizeOrder ? rand.nextInt( states.size() ) : 0;

            CaseState s = states.get(index);

            if( evaluateOneBlock(s)) {
                states.remove(index);
            }
        }

        System.out.println("Total processing time = "+(System.currentTimeMillis()-startTime)/1000.0);

        logStream.close();
    }

    /**
     * Check to see if there are any previously saved results.  if so skip the test. 
     */
    private List<CaseState> createCaseList(List<RuntimeEvaluationCase> cases) {
        List<CaseState> states = new ArrayList<CaseState>();

        for( RuntimeEvaluationCase c : cases ) {
            // see if the file already exists
            File f = new File(directorySave+"/"+c.getFileName()+".xml");

            if( f.exists() ) {
                // if it exists read it in and see if it finished
                OperationResults oldResults = UtilXmlSerialization.deserializeXml(f.getAbsolutePath());

                if( !oldResults.isComplete() ) {
                    System.out.println("DELETING OLD RESULTS: Found previously incomplete results for "+c.getOpName()+" deleting");
                    logStream.println("DELETING OLD RESULTS: Found previously incomplete results for "+c.getOpName()+" deleting");
                    if( !f.delete() )
                        throw new RuntimeException("Can't delete old unfinished results");
                     states.add( new CaseState(c));
                } else {
                    System.out.println("SKIPPING: Found previously completed results for "+c.getOpName());
                    logStream.println("SKIPPING: Found previously completed results for "+c.getOpName()+" skipping");
                }
            } else {
                states.add( new CaseState(c));
            }
        }
        return states;
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
        logStream.println(tools.getClassPath());
    }


    /**
     * Process a single block in one case.
     *
     * @param state the current state of an evaluation
     *
     * @throws java.io.FileNotFoundException
     * @return true if the case has finished
     */
    private boolean evaluateOneBlock( CaseState state ) throws FileNotFoundException {

        RuntimeEvaluationCase e = state.evalCase;
        int matDimen[] = e.getDimens();

        RuntimeEvaluationMetrics score[] = state.score;

        System.out.println("#### "+libraryType.getVersionName()+"  op "+e.getOpName()+"  Size "+matDimen[state.matrixIndex]+"  block "+state.blockIndex+"  ####");

        OperationResults r = computeResults(e, state.matrixIndex , randSeed[state.blockIndex] , score , state.results);

        if( r == null )
            return true;
        
        boolean done = tooSlow;

        // increment the number of blocks
        if( !done && ++state.blockIndex >= config.numBlocks ) {
            state.results.clear();
            state.blockIndex = 0;
            state.matrixIndex++;

            // see if its done processing all the matrices
            if( state.matrixIndex >= matDimen.length ) {
                done = true;
            }
        }

        // mark the this operation as being finished or not
        r.complete = done;

        // save the current state of the test
        UtilXmlSerialization.serializeXml(r,directorySave+"/"+e.getFileName()+".xml");

        return done;
    }

    /**
     * Computes the current results
     */
    private OperationResults computeResults( RuntimeEvaluationCase e , int matrixIndex ,
                                           long randSeed ,
                                           RuntimeEvaluationMetrics score[] , List<RuntimeResults> rawResults )
            throws FileNotFoundException {

        List<RuntimeResults> opsPerSecond = evaluateCase( e , randSeed , matrixIndex );

        if( caseFailed ) {
            System.out.println("      ---- ***** -----");
            System.out.println("Evaluation Case Failed ");
            System.out.println("      ---- ***** -----");
            return null;
        } else {
            rawResults.addAll(opsPerSecond);

            // save the results
            score[matrixIndex] = new RuntimeEvaluationMetrics(rawResults);
            OperationResults results = new OperationResults(e.getOpName(),
                    libraryType,e.getDimens(),score);

            return results;
        }
    }


    /**
     * Computes performance metrics for the specified case.
     *
     * @param indexDimen Which matrix size it should use.
     * @return The operations per second for this case.
     */
    @SuppressWarnings({"RedundantCast", "unchecked"})
    private List<RuntimeResults> evaluateCase( RuntimeEvaluationCase e , long seed , int indexDimen) {
        EvaluationTest test = e.createTest(indexDimen,config.trialTime,config.maxTrialTime);
        test.setRandomSeed(seed);

        int matrixSize = e.getDimens()[indexDimen];

        tooSlow = false;
        caseFailed = false;

        // try running the application a few times and see if its size increases
        for( int attempts = 0; attempts < 5; attempts++ ) {
            tools.setMemoryScale(config.memorySlaveScale*(1+attempts));

            EvaluatorSlave.Results r = tools.runTest(test);
//            EvaluatorSlave.Results r = tools.runTestNoSpawn(test);

            if( r == null ) {
                logStream.println("*** RunTest returned null: op = "+e.getOpName()+" matrix size = "+matrixSize);
                caseFailed = true;
                return null;
            } else if( r.failed == EvaluatorSlave.FailReason.OUT_OF_MEMORY ){
                System.out.println("  Not enough memory given to slave.");
                logStream.println("Not enough memory for op.  Attempt num "+attempts+"  op name = "+e.getOpName()+" matrix size = "+matrixSize);
                // have it run again, which will up the memory
            } else {
                if( r.failed != null ) {
                    if( r.failed == EvaluatorSlave.FailReason.TOO_SLOW ) {
                        logStream.println("    Case was too slow: op = "+e.getOpName()+" matrix size = "+matrixSize);
                        tooSlow = true;
                    } else {
                        logStream.println("    Case failed: reason = "+r.failed+" op = "+e.getOpName()+" matrix size = "+matrixSize);
                        if( r.detailedError != null ) {
                            logStream.println(r.detailedError);
                        }
                        caseFailed = true;
                    }
                }

                if( caseFailed ) {
                    return null;
                }

                return (List<RuntimeResults>)((List)r.results);
            }

        }

        // never had enough memory
        caseFailed = true;
        return null;
    }

    private static List<Double> convertToDoubleList( List<TestResults> l ){
        List<Double> ret = new ArrayList<Double>(l.size());

        for (TestResults aL : l) {

            double val = ((RuntimeResults) aL).getOpsPerSec();

            ret.add(val);
        }

        return ret;
    }

    public static class CaseState
    {
        RuntimeEvaluationCase evalCase;

        List<RuntimeResults> results = new ArrayList<RuntimeResults>();

        RuntimeEvaluationMetrics score[];

        int matrixIndex = 0;
        int blockIndex = 0;

        public CaseState( RuntimeEvaluationCase e ) {
            this.evalCase = e;
            this.score = new RuntimeEvaluationMetrics[ e.getDimens().length ];
        }
    }

    public static void main( String args[] ) throws IOException, InterruptedException {

        File f = new File("results/temp");

        if( !f.exists() )
            if( !f.mkdir() ) throw new RuntimeException("Crap");

        RuntimeBenchmarkConfig config = RuntimeBenchmarkConfig.createAllConfig();

        RuntimeBenchmarkLibrary master = new RuntimeBenchmarkLibrary("results/temp",new EjmlAlgorithmFactory(),
                null,MatrixLibrary.EJML,config);
        master.performBenchmark();
    }
}