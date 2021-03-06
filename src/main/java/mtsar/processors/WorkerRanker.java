/*
 * Copyright 2015 Dmitry Ustalov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mtsar.processors;

import mtsar.api.Worker;
import mtsar.api.WorkerRanking;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public interface WorkerRanker {
    /**
     * Given a collection of workers, estimate their performance.
     *
     * @param workers workers.
     * @return Worker rankings.
     */
    @Nonnull
    Map<Integer, WorkerRanking> rank(@Nonnull Collection<Worker> workers);

    /**
     * Given a worker, a ranker returns either a worker ranking, or nothing.
     * This is an alias for the method accepting the worker collection.
     *
     * @param worker worker.
     * @return Worker ranking.
     */
    @Nonnull
    default Optional<WorkerRanking> rank(@Nonnull Worker worker) {
        final Map<Integer, WorkerRanking> rankings = rank(Collections.singletonList(worker));
        if (rankings.isEmpty()) return Optional.empty();
        return Optional.of(rankings.get(worker.getId()));
    }
}
