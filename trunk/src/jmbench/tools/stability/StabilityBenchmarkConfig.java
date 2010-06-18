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

package jmbench.tools.stability;

import jmbench.impl.MatrixLibrary;
import jmbench.impl.stability.*;
import jmbench.tools.EvaluationTarget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Peter Abeles
 */
public class StabilityBenchmarkConfig implements Serializable {

    public static EvaluationTarget ejml = new EvaluationTarget( MatrixLibrary.EJML, EjmlStabilityFactory.class.getName());
    public static EvaluationTarget jama = new EvaluationTarget( MatrixLibrary.JAMA, JamaStabilityFactory.class.getName());
    public static EvaluationTarget ojalgo = new EvaluationTarget( MatrixLibrary.OJALGO, OjAlgoStabilityFactory.class.getName());
    public static EvaluationTarget commons = new EvaluationTarget( MatrixLibrary.CM, McBrStabilityFactory.class.getName());
    public static EvaluationTarget colt = new EvaluationTarget( MatrixLibrary.COLT, ColtStabilityFactory.class.getName());
    public static EvaluationTarget mtj = new EvaluationTarget( MatrixLibrary.MTJ, MtjStabilityFactory.class.getName());
    public static EvaluationTarget pcolt = new EvaluationTarget( MatrixLibrary.PCOLT, ParallelColtStabilityFactory.class.getName());
    public static EvaluationTarget ujmp = new EvaluationTarget( MatrixLibrary.UJMP, UjmpStabilityFactory.class.getName());
    public static EvaluationTarget jblas = new EvaluationTarget( MatrixLibrary.JBLAS, JBlasStabilityFactory.class.getName());


    public long randomSeed;
    public long maxProcessingTime;

    // how much memory it will add to the operation requested memory
    public long baseMemory;
    // how much operation requested memory will be scaled
    public long scaleMemory;

    // at which point is the error considered too large
    public double breakingPoint;

    // the number of trials for determining the breaking point
    // is this factor less than the base number of trials
    public int overFlowFactor;

    // size of matrices in each size group
    public int smallSizeMin;
    public int smallSizeMax;

    public int mediumSizeMin;
    public int mediumSizeMax;

    public int largeSizeMin;
    public int largeSizeMax;

    // number of trials it will perform for solve benchmarks
    public int trialsSmallSolve;
    public int trialsMediumSolve;
    public int trialsLargeSolve;

    // number of trials for SVD benchmarks
    public int trialsSmallSvd;
    public int trialsMediumSvd;
    public int trialsLargeSvd;

    // which libraries are to be evaluated
    public List<EvaluationTarget> targets = new ArrayList<EvaluationTarget>();

    public static StabilityBenchmarkConfig createDefault() {
        StabilityBenchmarkConfig config = new StabilityBenchmarkConfig();

        config.randomSeed = 0xdeadbeef;//new Random().nextLong();
        config.maxProcessingTime = 15*60*1000;

        config.baseMemory = 20;
        config.scaleMemory = 2;

        config.breakingPoint = 0.001;

        int off = 4;
        config.overFlowFactor = off;

        config.smallSizeMin = 2;
        config.smallSizeMax = 20;

        config.mediumSizeMin = 100;
        config.mediumSizeMax = 200;

        config.largeSizeMin = 500;
        config.largeSizeMax = 600;

        config.trialsSmallSolve = 1000*off;
        config.trialsMediumSolve = 50*off;
        config.trialsLargeSolve = 12*off;

        config.trialsSmallSvd = 600*off;
        config.trialsMediumSvd = 18*off;
        config.trialsLargeSvd = 7*off;

        config.targets.add(ejml);
        config.targets.add(jama);
        config.targets.add(ojalgo);
        config.targets.add(commons);
        config.targets.add(colt);
        config.targets.add(mtj);
        config.targets.add(pcolt);
        config.targets.add(ujmp);
        config.targets.add(jblas);

        return config;
    }

    public long getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }

    public long getMaxProcessingTime() {
        return maxProcessingTime;
    }

    public void setMaxProcessingTime(long maxProcessingTime) {
        this.maxProcessingTime = maxProcessingTime;
    }

    public int getSmallSizeMin() {
        return smallSizeMin;
    }

    public void setSmallSizeMin(int smallSizeMin) {
        this.smallSizeMin = smallSizeMin;
    }

    public int getSmallSizeMax() {
        return smallSizeMax;
    }

    public void setSmallSizeMax(int smallSizeMax) {
        this.smallSizeMax = smallSizeMax;
    }

    public int getMediumSizeMin() {
        return mediumSizeMin;
    }

    public void setMediumSizeMin(int mediumSizeMin) {
        this.mediumSizeMin = mediumSizeMin;
    }

    public int getMediumSizeMax() {
        return mediumSizeMax;
    }

    public void setMediumSizeMax(int mediumSizeMax) {
        this.mediumSizeMax = mediumSizeMax;
    }

    public int getLargeSizeMin() {
        return largeSizeMin;
    }

    public void setLargeSizeMin(int largeSizeMin) {
        this.largeSizeMin = largeSizeMin;
    }

    public int getLargeSizeMax() {
        return largeSizeMax;
    }

    public void setLargeSizeMax(int largeSizeMax) {
        this.largeSizeMax = largeSizeMax;
    }

    public int getTrialsSmallSolve() {
        return trialsSmallSolve;
    }

    public void setTrialsSmallSolve(int trialsSmallSolve) {
        this.trialsSmallSolve = trialsSmallSolve;
    }

    public int getTrialsMediumSolve() {
        return trialsMediumSolve;
    }

    public void setTrialsMediumSolve(int trialsMediumSolve) {
        this.trialsMediumSolve = trialsMediumSolve;
    }

    public int getTrialsLargeSolve() {
        return trialsLargeSolve;
    }

    public void setTrialsLargeSolve(int trialsLargeSolve) {
        this.trialsLargeSolve = trialsLargeSolve;
    }

    public List<EvaluationTarget> getTargets() {
        return targets;
    }

    public void setTargets(List<EvaluationTarget> targets) {
        this.targets = targets;
    }

    public double getBreakingPoint() {
        return breakingPoint;
    }

    public void setBreakingPoint(double breakingPoint) {
        this.breakingPoint = breakingPoint;
    }

    public int getTrialsSmallSvd() {
        return trialsSmallSvd;
    }

    public void setTrialsSmallSvd(int trialsSmallSvd) {
        this.trialsSmallSvd = trialsSmallSvd;
    }

    public int getTrialsMediumSvd() {
        return trialsMediumSvd;
    }

    public void setTrialsMediumSvd(int trialsMediumSvd) {
        this.trialsMediumSvd = trialsMediumSvd;
    }

    public int getTrialsLargeSvd() {
        return trialsLargeSvd;
    }

    public void setTrialsLargeSvd(int trialsLargeSvd) {
        this.trialsLargeSvd = trialsLargeSvd;
    }

    public int getOverFlowFactor() {
        return overFlowFactor;
    }

    public void setOverFlowFactor(int overFlowFactor) {
        this.overFlowFactor = overFlowFactor;
    }

    public long getBaseMemory() {
        return baseMemory;
    }

    public void setBaseMemory(long baseMemory) {
        this.baseMemory = baseMemory;
    }

    public long getScaleMemory() {
        return scaleMemory;
    }

    public void setScaleMemory(long scaleMemory) {
        this.scaleMemory = scaleMemory;
    }
}
