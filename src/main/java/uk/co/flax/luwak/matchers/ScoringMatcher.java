package uk.co.flax.luwak.matchers;

import java.io.IOException;

import org.apache.lucene.search.Scorer;
import uk.co.flax.luwak.DocumentBatch;
import uk.co.flax.luwak.MatcherFactory;

/**
 * Copyright (c) 2014 Lemur Consulting Ltd.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * A Matcher that reports the scores of queries run against its InputDocument
 */
public class ScoringMatcher extends CollectingMatcher<ScoringMatch> {

    public ScoringMatcher(DocumentBatch docs) {
        super(docs);
    }

    @Override
    protected ScoringMatch doMatch(String queryId, int doc, Scorer scorer) throws IOException {
        float score = scorer.score();
        if (score > 0)
            return new ScoringMatch(queryId, docs.resolveDocId(doc), score);
        return null;
    }

    @Override
    public ScoringMatch resolve(ScoringMatch match1, ScoringMatch match2) {
        return match1.getScore() < match2.getScore() ? match2 : match1;
    }

    /**
     * A MatcherFactory for ScoringMatcher objects
     */
    public static final MatcherFactory<ScoringMatch> FACTORY = new MatcherFactory<ScoringMatch>() {
        @Override
        public ScoringMatcher createMatcher(DocumentBatch docs) {
            return new ScoringMatcher(docs);
        }
    };

}
