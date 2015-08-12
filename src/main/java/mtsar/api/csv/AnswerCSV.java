package mtsar.api.csv;

import mtsar.api.Answer;
import mtsar.api.Process;
import org.apache.commons.collections4.iterators.IteratorIterable;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

public final class AnswerCSV {
    public static final String[] HEADER = {"id", "tags", "process", "task_id", "worker_id", "answers", "datetime"};

    public static final CSVFormat FORMAT = CSVFormat.DEFAULT.withHeader(HEADER).withSkipHeaderRecord();

    public static Iterator<Answer> parse(Process process, Iterator<CSVRecord> records) {
        final Iterable<CSVRecord> iterable = () -> records;
        return StreamSupport.stream(iterable.spliterator(), false).map(row -> {
            final String id = row.isSet("id") ? row.get("id") : null;
            final String[] tags = row.isSet("tags") && !StringUtils.isEmpty(row.get("tags")) ? row.get("tags").split("\\|") : null;
            final String workerId = row.get("worker_id");
            final String taskId = row.get("task_id");
            final String[] answers = row.isSet("answers") && !StringUtils.isEmpty(row.get("answers")) ? row.get("answers").split("\\|") : null;
            final String datetime = row.isSet("datetime") ? row.get("datetime") : null;

            return Answer.builder().
                    setId(Integer.valueOf(id)).
                    setProcess(process.getId()).
                    setTags(tags).
                    setDateTime(new Timestamp(StringUtils.isEmpty(datetime) ? System.currentTimeMillis() : Long.valueOf(datetime) * 1000L)).
                    setWorkerId(Integer.valueOf(workerId)).
                    setTaskId(Integer.valueOf(taskId)).
                    setAnswers(answers).
                    build();
        }).iterator();
    }

    public static void write(List<Answer> answers, OutputStream output) throws IOException {
        try (final Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8)) {
            CSVFormat.DEFAULT.withHeader(HEADER).print(writer).printRecords(new IteratorIterable<>(
                    answers.stream().map(answer -> new String[]{
                            Integer.toString(answer.getId()),
                            String.join("|", answer.getTags()),
                            answer.getProcess(),
                            Integer.toString(answer.getTaskId()),
                            Integer.toString(answer.getWorkerId()),
                            String.join("|", answer.getAnswers()),
                            Long.toString(answer.getDateTime().toInstant().getEpochSecond())
                    }).iterator()
            ));
        }
    }
}