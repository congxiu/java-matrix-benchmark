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
import jmbench.impl.runtime.*;
import jmbench.tools.EvaluationTarget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Provides a centralized way to specify what and how the library is run.  Individual libraries,
 * operations, and the random seed can all be changed from here.
 *
 * @author Peter Abeles
 */
public class RuntimeBenchmarkConfig implements Serializable {

    public static EvaluationTarget ejml = new EvaluationTarget( MatrixLibrary.EJML, EjmlAlgorithmFactory.class.getName());
    public static EvaluationTarget sejml = new EvaluationTarget( MatrixLibrary.SEJML, SejmlAlgorithmFactory.class.getName());
    public static EvaluationTarget jama = new EvaluationTarget( MatrixLibrary.JAMA, JamaAlgorithmFactory.class.getName());
    public static EvaluationTarget ojalgo = new EvaluationTarget( MatrixLibrary.OJALGO, OjAlgoAlgorithmFactory.class.getName());
    public static EvaluationTarget commons = new EvaluationTarget( MatrixLibrary.CM, McBrAlgorithmFactory.class.getName());
    public static EvaluationTarget colt = new EvaluationTarget( MatrixLibrary.COLT, ColtAlgorithmFactory.class.getName());
    public static EvaluationTarget mtj = new EvaluationTarget( MatrixLibrary.MTJ, MtjAlgorithmFactory.class.getName());
    public static EvaluationTarget jsci = new EvaluationTarget( MatrixLibrary.JSCIENCE, JScienceAlgorithmFactory.class.getName());
    public static EvaluationTarget pcolt = new EvaluationTarget( MatrixLibrary.PCOLT, PColtAlgorithmFactory.class.getName());
    public static EvaluationTarget ujmp = new EvaluationTarget( MatrixLibrary.UJMP, UjmpAlgorithmFactory.class.getName());

    // random seed used to create matrices used as inputs
    public long seed;

    // randomize the order that the benchmarks are done in
    public boolean randizeOrder;

    // all the functions that will be benchmarked
    public boolean chol;
    public boolean lu;
    public boolean svd;
    public boolean qr;
    public boolean eigSymm;
    public boolean det;
    public boolean invert;
    public boolean add;
    public boolean mult;
    public boolean multTransA;
    public boolean scale;
    public boolean solveExact;
    public boolean solveOver;
    public boolean transpose;

    // which libraries are to be evaluated
    public List<EvaluationTarget> targets;

    // how many performance trials should it run in a block
    public int numBlockTrials; // 5
    // a block is a set of trials performed in a single instance of a spawned VM
    // This is the number of times a block is spawned to evaluate the same set of input parameters
    public int numBlocks; // 5
    // how long should each trial last for
    public int trialTime;// 3000

    public int memorySlaveBase;// 10
    public int memorySlaveScale;// 8

    /**
     * This config will process everything
     *
     * @return The config.
     */
    public static RuntimeBenchmarkConfig createAllConfig() {
        RuntimeBenchmarkConfig config = new RuntimeBenchmarkConfig();

        config.seed = new Random().nextLong();
        config.numBlockTrials = 5;
        config.numBlocks = 5;
        config.trialTime = 3000;
        config.memorySlaveBase = 10;
        config.memorySlaveScale = 8;
        config.randizeOrder = true;

        config.chol = true;
        config.lu = true;
        config.svd = true;
        config.qr = true;
        config.eigSymm = true;
        config.det = true;
        config.invert = true;
        config.add = true;
        config.mult = true;
        config.multTransA = true;
        config.scale = true;
        config.solveExact = true;
        config.solveOver = true;
        config.transpose = true;

        config.targets = new ArrayList<EvaluationTarget>();

        config.targets.add(ejml);
//        config.targets.add(sejml);
        config.targets.add(jama);
        config.targets.add(ojalgo);
        config.targets.add(commons);
        config.targets.add(colt);
        config.targets.add(pcolt);
        config.targets.add(mtj);
        config.targets.add(jsci);
        config.targets.add(ujmp);

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

    public boolean isMultTransA() {
        return multTransA;
    }

    public void setMultTransA(boolean multTransA) {
        this.multTransA = multTransA;
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

    public boolean isTranspose() {
        return transpose;
    }

    public void setTranspose(boolean transpose) {
        this.transpose = transpose;
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

    public int getNumBlocks() {
        return numBlocks;
    }

    public void setNumBlocks(int numBlocks) {
        this.numBlocks = numBlocks;
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
}
