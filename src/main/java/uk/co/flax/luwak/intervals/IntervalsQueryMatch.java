package uk.co.flax.luwak.intervals;

import java.util.*;

import org.apache.lucene.search.intervals.Interval;
import uk.co.flax.luwak.QueryMatch;

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
 * QueryMatch object that contains the hit positions of a matching Query
 *
 * If the Query does not support interval iteration (eg, if it gets re-written to
 * a Filter), then no hits will be reported, but an IntervalsQueryMatch will still
 * be returned from an IntervalsMatcher to indicate a match.
 */
public class IntervalsQueryMatch extends QueryMatch {

    private static final Map<String, List<Hit>> EMPTYMAP = new HashMap<>();

    private final Map<String, List<Hit>> hits;

    /**
     * Create a new QueryMatch object for a query
     *
     * @param queryId the ID of the query
     */
    public IntervalsQueryMatch(String queryId, String docId, Map<String, List<Hit>> hits) {
        super(queryId, docId);
        this.hits = new TreeMap<>(hits);
    }

    /**
     * @return the fields in which matches have been found
     */
    public Set<String> getFields() {
        return hits.keySet();
    }

    /**
     * Get the hits for a specific field
     * @param field the field
     * @return the Hits found in this field
     */
    public Collection<Hit> getHits(String field) {
        return Collections.unmodifiableCollection(hits.get(field));
    }

    /**
     * @return the total number of hits for the query
     */
    public int getHitCount() {
        return hits.size();
    }

    public static IntervalsQueryMatch merge(String queryId, String docId, IntervalsQueryMatch... matches) {
        IntervalsQueryMatch newMatch = new IntervalsQueryMatch(queryId, docId, EMPTYMAP);
        for (IntervalsQueryMatch match : matches) {
            assert newMatch.getDocId().equals(match.getDocId());
            for (String field : match.getFields()) {
                if (!newMatch.hits.containsKey(field))
                    newMatch.hits.put(field, new ArrayList<Hit>());
                newMatch.hits.get(field).addAll(match.getHits(field));
            }
        }
        return newMatch;
    }

    /**
     * Represents an individual hit
     */
    public static class Hit implements Comparable<Hit> {

        /** The start position */
        public final int startPosition;

        /** The start offset */
        public final int startOffset;

        /** The end positions */
        public final int endPosition;

        /** The end offset */
        public final int endOffset;

        public Hit(int startPosition, int startOffset, int endPosition, int endOffset) {
            this.startPosition = startPosition;
            this.startOffset = startOffset;
            this.endPosition = endPosition;
            this.endOffset = endOffset;
        }

        public Hit(Interval interval) {
            this(interval.begin, interval.offsetBegin, interval.end, interval.offsetEnd);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Hit))
                return false;
            Hit other = (Hit) obj;
            return this.startOffset == other.startOffset &&
                    this.endOffset == other.endOffset &&
                    this.startPosition == other.startPosition &&
                    this.endPosition == other.endPosition;
        }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "%d(%d)->%d(%d)", startPosition, startOffset, endPosition, endOffset);
        }

        @Override
        public int compareTo(Hit other) {
            if (this.startPosition != other.startPosition)
                return Integer.compare(this.startPosition, other.startPosition);
            return Integer.compare(this.endPosition, other.endPosition);
        }
    }
}
