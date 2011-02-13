/*
 * Copyright (c) 2009-2011, Peter Abeles. All Rights Reserved.
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
import jmbench.impl.runtime.*;
import jmbench.tools.EvaluationTarget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Provides a centralized way to specify what and how the library is run.  Individual libraries,
 * operations, and the random seed can all be changed from here.
 *
 * @author Peter Abeles
 */
public class RuntimeBenchmarkConfig implements Serializable {

    public static final EvaluationTarget ejml = new EvaluationTarget( MatrixLibrary.EJML, EjmlAlgorithmFactory.class.getName());
    public static final EvaluationTarget sejml = new EvaluationTarget( MatrixLibrary.SEJML, SejmlAlgorithmFactory.class.getName());
    public static final EvaluationTarget jama = new EvaluationTarget( MatrixLibrary.JAMA, JamaAlgorithmFactory.class.getName());
    public static final EvaluationTarget ojalgo = new EvaluationTarget( MatrixLibrary.OJALGO, OjAlgoAlgorithmFactory.class.getName());
    public static final EvaluationTarget commons = new EvaluationTarget( MatrixLibrary.CM, CommonsMathAlgorithmFactory.class.getName());
    public static final EvaluationTarget colt = new EvaluationTarget( MatrixLibrary.COLT, ColtAlgorithmFactory.class.getName());
    public static final EvaluationTarget mtj = new EvaluationTarget( MatrixLibrary.MTJ, MtjAlgorithmFactory.class.getName());
    public static final EvaluationTarget pcolt = new EvaluationTarget( MatrixLibrary.PCOLT, PColtAlgorithmFactory.class.getName());
    public static final EvaluationTarget ujmp = new EvaluationTarget( MatrixLibrary.UJMP, UjmpAlgorithmFactory.class.getName());
    public static final EvaluationTarget ujmp_j = new EvaluationTarget( MatrixLibrary.UJMP_JAVA , UjmpJavaAlgorithmFactory.class.getName());
    public static final EvaluationTarget jblas = new EvaluationTarget( MatrixLibrary.JBLAS, JBlasAlgorithmFactory.class.getName());

    // random seed used to create matrices used as inputs
    public long seed;

    // randomize the order that the benchmarks are done in
    public boolean randizeOrder;

    // all the functions that will be evaluated
    public boolean chol;
    public boolean lu;
    public boolean svd;
    public boolean qr;
    public boolean eigSymm;
    public boolean det;
    public boolean invert;
    public boolean invertSymmPosDef;
    public boolean add;
    public boolean mult;
    public boolean multTransB;
    public boolean scale;
    public boolean solveExact;
    public boolean solveOver;
    public boolean transposeSquare;
    public boolean transposeTall;
    public boolean transposeWide;

    // which libraries are to be evaluated
    public List<EvaluationTarget> targets;

    // how many performance trials should it run in a block
    public int numBlockTrials;
    // it will stop processing a matrix size if this number of trials has been exceeded
    public int maxTrials;
    // the minimum amount of time each trials should last for
    public int trialTime;
    // the maximum amount of time a trial can last for
    public int maxTrialTime;

    // specifies a fixed amount of memory that is to be allocated to the slave.
    // if set to zero then the memory will be dynamically allocated
    // memory here is in megabytes
    public int memoryTrial;

    // if memory is dynamically allocated this specifies how much is allocated
    public int memorySlaveBase;
    public int memorySlaveScale;

    // largest size matrix it can process
    public int maxMatrixSize;
    // the smallest matrix size it will process
    public int minMatrixSize;

    // should it perform a sanity check on the operations it tests
    // this requires more memory and time, but can make sure the operation is
    // really doing what it should be doing.
    public boolean sanityCheck;

    /**
     * This config will process everything
     *
     * @return The config.
     */
    public static RuntimeBenchmarkConfig createAllConfig() {
        RuntimeBenchmarkConfig config = new RuntimeBenchmarkConfig();

        config.seed = 0xDEADBEEF;//new Random().nextLong();
        config.numBlockTrials = 5;
        config.maxTrials = 25;
        config.trialTime = 3000;
        config.maxTrialTime = 600000;
        config.memoryTrial = 0;
        config.memorySlaveBase = 20;
        config.memorySlaveScale = 1;
        config.randizeOrder = true;
        config.maxMatrixSize = 10000;
        config.minMatrixSize = 2;
        config.sanityCheck = false;

//        config.chol = true;
//        config.lu = true;
//        config.qr = true;
//        config.transposeTall = true;
//        config.transposeWide = true;
        
        config.svd = true;
        config.eigSymm = true;
        config.det = true;
        config.invert = true;
        config.invertSymmPosDef = true;
        config.add = true;
        config.mult = true;
        config.multTransB = true;
        config.scale = true;
        config.solveExact = true;
        config.solveOver = true;
        config.transposeSquare = true;

        config.targets = new ArrayList<EvaluationTarget>();

        config.targets.add(ejml);
//        config.targets.add(sejml);
        config.targets.add(jama);
        config.targets.add(ojalgo);
        config.targets.add(commons);
        config.targets.add(colt);
        config.targets.add(pcolt);
        config.targets.add(mtj);
        config.targets.add(ujmp_j);
        config.targets.add(ujmp);
        config.targets.add(jblas);

        return config;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public boolean isChol() {
        return chol;
    }

    public void setChol(boolean chol) {
        this.chol = chol;
    }

    public boolean isLu() {
        return lu;
    }

    public void setLu(boolean lu) {
        this.lu = lu;
    }

    public boolean isSvd() {
        return svd;
    }

    public void setSvd(boolean svd) {
        this.svd = svd;
    }

    public boolean isQr() {
        return qr;
    }

    public void setQr(boolean qr) {
        this.qr = qr;
    }

    public boolean isEigSymm() {
        return eigSymm;
    }

    public void setEigSymm(boolean eigSymm) {
        this.eigSymm = eigSymm;
    }

    public boolean isDet() {
        return det;
    }

    public void setDet(boolean det) {
        this.det = det;
    }

    public boolean isInvert() {
        return invert;
    }

    public void setInvert(boolean invert) {
        this.invert = invert;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public boolean isMult() {
        return mult;
    }

    public void setMult(boolean mult) {
        this.mult = mult;
    }

    public boolean isMultTransB() {
        return multTransB;
    }

    public void setMultTransB(boolean multTransB) {
        this.multTransB = multTransB;
    }

    public boolean isScale() {
        return scale;
    }

    public void setScale(boolean scale) {
        this.scale = scale;
    }

    public boolean isSolveExact() {
        return solveExact;
    }

    public void setSolveExact(boolean solveExact) {
        this.solveExact = solveExact;
    }

    public boolean isSolveOver() {
        return solveOver;
    }

    public void setSolveOver(boolean solveOver) {
        this.solveOver = solveOver;
    }

    public boolean isTransposeSquare() {
        return transposeSquare;
    }

    public void setTransposeSquare(boolean transposeSquare) {
        this.transposeSquare = transposeSquare;
    }

    public boolean isTransposeTall() {
        return transposeTall;
    }

    public void setTransposeTall(boolean transposeTall) {
        this.transposeTall = transposeTall;
    }

    public boolean isTransposeWide() {
        return transposeWide;
    }

    public void setTransposeWide(boolean transposeWide) {
        this.transposeWide = transposeWide;
    }

    public List<EvaluationTarget> getTargets() {
        return targets;
    }

    public void setTargets(List<EvaluationTarget> targets) {
        this.targets = targets;
    }

    public boolean isRandizeOrder() {
        return randizeOrder;
    }

    public void setRandizeOrder(boolean randizeOrder) {
        this.randizeOrder = randizeOrder;
    }

    public int getNumBlockTrials() {
        return numBlockTrials;
    }

    public void setNumBlockTrials(int numBlockTrials) {
        this.numBlockTrials = numBlockTrials;
    }

    public int getMaxTrials() {
        return maxTrials;
    }

    public void setMaxTrials(int maxTrials) {
        this.maxTrials = maxTrials;
    }

    public int getTrialTime() {
        return trialTime;
    }

    public void setTrialTime(int trialTime) {
        this.trialTime = trialTime;
    }

    public int getMemorySlaveBase() {
        return memorySlaveBase;
    }

    public void setMemorySlaveBase(int memorySlaveBase) {
        this.memorySlaveBase = memorySlaveBase;
    }

    public int getMemorySlaveScale() {
        return memorySlaveScale;
    }

    public void setMemorySlaveScale(int memorySlaveScale) {
        this.memorySlaveScale = memorySlaveScale;
    }

    public int getMaxMatrixSize() {
        return maxMatrixSize;
    }

    public void setMaxMatrixSize(int maxMatrixSize) {
        this.maxMatrixSize = maxMatrixSize;
    }

    public int getMinMatrixSize() {
        return minMatrixSize;
    }

    public void setMinMatrixSize(int minMatrixSize) {
        this.minMatrixSize = minMatrixSize;
    }

    public boolean isInvertSymmPosDef() {
        return invertSymmPosDef;
    }

    public void setInvertSymmPosDef(boolean invertSymmPosDef) {
        this.invertSymmPosDef = invertSymmPosDef;
    }

    public int getMaxTrialTime() {
        return maxTrialTime;
    }

    public void setMaxTrialTime(int maxTrialTime) {
        this.maxTrialTime = maxTrialTime;
    }

    public int getMemoryTrial() {
        return memoryTrial;
    }

    public void setMemoryTrial(int memoryTrial) {
        this.memoryTrial = memoryTrial;
    }

    public boolean isSanityCheck() {
        return sanityCheck;
    }

    public void setSanityCheck(boolean sanityCheck) {
        this.sanityCheck = sanityCheck;
    }
}
