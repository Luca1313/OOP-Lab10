package it.unibo.oop.lab.workers02;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;


public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nThreads;

    public MultiThreadedSumMatrix(final int nThreads) {
        this.nThreads = nThreads;
    }

    private class Worker extends Thread {

        private final double[][] matrix;
        private final Dimension startPos;
        private final int nElem;
        private double res;

        Worker(final double[][] matrix, final Dimension startPos, final int nElem) {
            super();
            this.matrix = matrix;   this.startPos = startPos;
            this.nElem = nElem;
        }

        @Override
        public void run() {
            int x = (int) this.startPos.getWidth();
            int y = (int) this.startPos.getHeight();

            for (int count = 0;  y < this.matrix.length && count < this.nElem; count++,
                    x = x + 1 == (int) this.matrix[y].length ? 0 : x + 1, y = x == 0 ? y + 1 : y) {
                this.res += this.matrix[y][x];
            }
        }

        public double getRes() {
            return this.res;
        }
    }

    private void matrixIncrem(final double[][] matrix, final Dimension startDim, final int size) {
        int x = startDim.width;
        int y = startDim.height;

        for (int count = 0; count < size; count++) {
            x = x + 1 == (int) matrix[y].length ? 0 : x + 1;
            y = x == 0 ? y + 1 : y;
        }
        startDim.width = x;
        startDim.height = y;
    }

    @Override
    public double sum(final double[][] matrix) {
        final int nElemMatrix = matrix[0].length * matrix.length;
        int sizeOfEachThread = nElemMatrix % this.nThreads + nElemMatrix / this.nThreads;
        int count = 0;

        final List<Worker> workers = new ArrayList<>(this.nThreads);
        for (final Dimension start = new Dimension(0, 0); start.height < matrix.length; 
                matrixIncrem(matrix, start, sizeOfEachThread), count++) {
            sizeOfEachThread = count == this.nThreads - 1
                    ? nElemMatrix - count * sizeOfEachThread : sizeOfEachThread;
            workers.add(new Worker(matrix, new Dimension(start), 
                    sizeOfEachThread));
        }

        for (final Worker w: workers) {
            w.start();
        }

        double sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getRes();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }

}
