package mtsar.processors.worker;

import mtsar.api.Task;
import mtsar.api.Worker;
import mtsar.processors.Processor;
import mtsar.processors.WorkerRanker;

public class RandomRanker extends Processor implements WorkerRanker {
    @Override
    public double estimatePerformance(Worker worker) {
        return Math.random();
    }

    @Override
    public double estimatePerformance(Worker worker, Task task) {
        return Math.random();
    }
}
