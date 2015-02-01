package uk.co.flax.luwak;

import java.util.*;

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

public class Matches<T extends QueryMatch> implements Iterable<DocumentMatches<T>> {

    private final Map<String, DocumentMatches<T>> matches;
    private final List<MatchError> errors;

    private final long queryBuildTime;
    private final long searchTime;
    private final int queriesRun;
    private final int batchSize;

    protected final String slowlog;

    public Matches(Map<String, DocumentMatches<T>> matches, List<MatchError> errors,
                   long queryBuildTime, long searchTime, int queriesRun, int batchSize, String slowlog) {
        this.matches = Collections.unmodifiableMap(matches);
        this.errors = Collections.unmodifiableList(errors);
        this.queryBuildTime = queryBuildTime;
        this.searchTime = searchTime;
        this.queriesRun = queriesRun;
        this.batchSize = batchSize;
        this.slowlog = slowlog;
    }

    @Override
    public Iterator<DocumentMatches<T>> iterator() {
        return matches.values().iterator();
    }

    /**
     * Returns the QueryMatch for the given query, or null if it did not match
     * @param queryId the query id
     */
    public T matches(String docId, String queryId) {
        DocumentMatches<T> docMatches = matches.get(docId);
        if (docMatches == null)
            return null;

        for (T match : docMatches) {
            if (match.getQueryId().equals(queryId))
                return match;
        }

        return null;
    }

    /**
     * @return all matches for a particular document
     */
    public Collection<T> getMatches(String docId) {
        DocumentMatches docMatches = matches.get(docId);
        if (docMatches == null)
            return null;
        return docMatches.getMatches();
    }

    /**
     * @return the number of queries that matched
     */
    public int getMatchCount() {
        return matches.size();
    }

    /**
     * @return how long (in ms) it took to build the Presearcher query for the matcher run
     */
    public long getQueryBuildTime() {
        return queryBuildTime;
    }

    /**
     * @return how long (in ms) it took to run the selected queries
     */
    public long getSearchTime() {
        return searchTime;
    }

    /**
     * @return the number of queries passed to this CandidateMatcher during the matcher run
     */
    public int getQueriesRun() {
        return queriesRun;
    }

    /**
     * @return the number of documents in the batch
     */
    public int getBatchSize() {
        return batchSize;
    }

    /**
     * @return a List of any MatchErrors created during the matcher run
     */
    public List<MatchError> getErrors() {
        return errors;
    }

    /**
     * Return the slow log for this match run.
     *
     * The slow log contains a list of all queries that took longer than the slow log
     * limit to run.
     *
     * @return the slow log
     */
    public String getSlowLog() {
        return slowlog;
    }

}
