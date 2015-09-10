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

package mtsar.api.sql;

import mtsar.api.Answer;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@UseStringTemplate3StatementLocator
@RegisterMapper(AnswerDAO.Mapper.class)
public interface AnswerDAO {
    String ANSWER_TYPE_ANSWER = "answer";
    String ANSWER_TYPE_DEFAULT = ANSWER_TYPE_ANSWER;

    @SqlQuery("select * from answers where process = :process")
    List<Answer> listForProcess(@Bind("process") String process);

    @SqlQuery("select * from answers where task_id = :taskId and process = :process")
    List<Answer> listForTask(@Bind("taskId") Integer taskId, @Bind("process") String process);

    @SqlQuery("select * from answers where worker_id = :workerId and process = :process")
    List<Answer> listForWorker(@Bind("workerId") Integer workerId, @Bind("process") String process);

    @SqlQuery("select * from answers where id = :id and process = :process limit 1")
    Answer find(@Bind("id") Integer id, @Bind("process") String process);

    @SqlQuery("select * from answers where process = :process and worker_id = :worker_id and task_id = :task_id limit 1")
    Answer findByWorkerAndTask(@Bind("process") String process, @Bind("worker_id") Integer workerId, @Bind("task_id") Integer taskId);

    @SqlQuery("insert into answers (process, datetime, tags, type, worker_id, task_id, answers) values (:process, coalesce(:dateTime, localtimestamp), cast(:tagsTextArray as text[]), cast(:type as answer_type), :workerId, :taskId, cast(:answersTextArray as text[])) returning id")
    int insert(@BindBean Answer a);

    @SqlBatch("insert into answers (id, process, datetime, tags, type, worker_id, task_id, answers) values (coalesce(:id, nextval('answers_id_seq')), :process, coalesce(:dateTime, localtimestamp), cast(:tagsTextArray as text[]), cast(:type as answer_type), :workerId, :taskId, cast(:answersTextArray as text[]))")
    @BatchChunkSize(1000)
    int[] insert(@BindBean Iterator<Answer> tasks);

    @SqlQuery("select count(*) from answers")
    int count();

    @SqlQuery("select count(*) from answers where process = :process")
    int count(@Bind("process") String process);

    @SqlUpdate("delete from answers where id = :id and process = :process")
    void delete(@Bind("id") Integer id, @Bind("process") String process);

    @SqlUpdate("delete from answers where process = :process")
    void deleteAll(@Bind("process") String process);

    @SqlUpdate("delete from answers")
    void deleteAll();

    @SqlUpdate("select setval('answers_id_seq', coalesce((select max(id) + 1 from answers), 1), false)")
    void resetSequence();

    void close();

    class Mapper implements ResultSetMapper<Answer> {
        public Answer map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return new Answer.Builder().
                    setId(r.getInt("id")).
                    setProcess(r.getString("process")).
                    setDateTime(r.getTimestamp("datetime")).
                    addAllTags(Arrays.asList((String[]) r.getArray("tags").getArray())).
                    setType(r.getString("type")).
                    setWorkerId(r.getInt("worker_id")).
                    setTaskId(r.getInt("task_id")).
                    addAllAnswers(Arrays.asList((String[]) r.getArray("answers").getArray())).
                    build();
        }
    }
}
