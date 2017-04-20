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

package mtsar.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import mtsar.util.StreamUtils;
import org.inferred.freebuilder.FreeBuilder;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

@FreeBuilder
@XmlRootElement
@JsonDeserialize(builder = AnswerAggregation.Builder.class)
public interface AnswerAggregation {
    String TYPE_DEFAULT = "aggregation";
    String TYPE_EMPTY = "empty";

    static AnswerAggregation empty(Task task) {
        return new Builder().setType(TYPE_EMPTY).setTask(task).build();
    }

    @JsonProperty
    String getType();

    @JsonProperty
    Task getTask();

    @JsonProperty
    List<String> getAnswers();

    @JsonProperty
    List<Double> getConfidences();

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
    class Builder extends AnswerAggregation_Builder {
        public Builder() {
            setType(TYPE_DEFAULT);
        }

        public AnswerAggregation build() {
            while (getConfidences().size() < getAnswers().size()) addConfidences(0.0);
            return super.build();
        }

        public Builder addAnswers(Map<String, Double> confidenceMap, boolean ignoreZeros) {
            confidenceMap.entrySet().stream()
                    .filter(e -> !ignoreZeros || e.getValue() > 0)
                    .sorted(StreamUtils.comparingDouble(Map.Entry::getValue))
                    .forEach(e -> {
                        addConfidences(e.getValue());
                        addAnswers(e.getKey());
                    });
            return this;
        }

    }
}
