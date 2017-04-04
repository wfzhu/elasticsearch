/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.search.suggest;

import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitTests;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion.Entry.Option;
import org.elasticsearch.test.ESTestCase;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.elasticsearch.common.xcontent.XContentHelper.toXContent;
import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertToXContentEquivalent;

public class CompletionSuggestionOptionTests extends ESTestCase {

    public static Option createTestItem() {
        Text text = new Text(randomAlphaOfLengthBetween(5, 15));
        int docId = randomInt();
        int numberOfContexts = randomIntBetween(0, 3);
        Map<String, Set<CharSequence>> contexts = new HashMap<>();
        for (int i = 0; i < numberOfContexts; i++) {
            int numberOfValues = randomIntBetween(0, 3);
            Set<CharSequence> values = new HashSet<>();
            for (int v = 0; v < numberOfValues; v++) {
                values.add(randomAlphaOfLengthBetween(5, 15));
            }
            contexts.put(randomAlphaOfLengthBetween(5, 15), values);
        }
        SearchHit hit = null;
        float score = randomFloat();
        if (randomBoolean()) {
            hit = SearchHitTests.createTestItem(false);
            score = hit.getScore();
        }
        Option option = new CompletionSuggestion.Entry.Option(docId, text, score, contexts);
        option.setHit(hit);
        return option;
    }

    public void testFromXContent() throws IOException {
        Option option = createTestItem();
        XContentType xContentType = randomFrom(XContentType.values());
        boolean humanReadable = randomBoolean();
        BytesReference originalBytes = toXContent(option, xContentType, humanReadable);
        if (randomBoolean()) {
            try (XContentParser parser = createParser(xContentType.xContent(), originalBytes)) {
                originalBytes = shuffleXContent(parser, randomBoolean()).bytes();
            }
        }
        Option parsed;
        try (XContentParser parser = createParser(xContentType.xContent(), originalBytes)) {
            parsed = Option.fromXContent(parser);
            assertNull(parser.nextToken());
        }
        assertEquals(option.getText(), parsed.getText());
        assertEquals(option.getHighlighted(), parsed.getHighlighted());
        assertEquals(option.getScore(), parsed.getScore(), Float.MIN_VALUE);
        assertEquals(option.collateMatch(), parsed.collateMatch());
        assertEquals(option.getContexts(), parsed.getContexts());
        assertToXContentEquivalent(originalBytes, toXContent(parsed, xContentType, humanReadable), xContentType);
    }

    public void testToXContent() throws IOException {
        Map<String, Set<CharSequence>> contexts = Collections.singletonMap("key", Collections.singleton("value"));
        CompletionSuggestion.Entry.Option option = new CompletionSuggestion.Entry.Option(1, new Text("someText"), 1.3f, contexts);
        BytesReference xContent = toXContent(option, XContentType.JSON, randomBoolean());
        assertEquals("{\"text\":\"someText\",\"score\":1.3,\"contexts\":{\"key\":[\"value\"]}}"
                   , xContent.utf8ToString());
    }
}
