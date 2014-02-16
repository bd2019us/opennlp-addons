/*
 * Copyright 2013 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package opennlp.addons.geoentitylinker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import opennlp.tools.entitylinker.EntityLinkerProperties;
import opennlp.tools.entitylinker.domain.BaseLink;
import opennlp.tools.entitylinker.domain.LinkedSpan;
import opennlp.tools.util.Span;

/**
 * Scores toponymns based on geographic point binning. Based on the heuristic
 * that docs are generally about a small amount of locations, so one can detect
 * outliers by finding those points that are not near the majority
 *
 */
public class GeoHashBinningScorer implements LinkedEntityScorer<CountryContext> {

  private final PointClustering CLUSTERER = new PointClustering();
  private int PRECISION = 4;

  @Override
  public void score(List<LinkedSpan> linkedSpans, String docText, Span[] sentenceSpans, EntityLinkerProperties properties, CountryContext additionalContext) {
     //Map<Double, Double> latLongs = new HashMap<Double, Double>();
    List<GazateerEntry> allGazEntries = new ArrayList<>();

    /**
     * collect all the gaz entry references
     */
    for (LinkedSpan<BaseLink> ls : linkedSpans) {
      for (BaseLink bl : ls.getLinkedEntries()) {
        if (bl instanceof GazateerEntry) {
          allGazEntries.add((GazateerEntry) bl);
        }
      }
    }
    /**
     * use the point clustering to score each hit
     */
    Map<String, List<GazateerEntry>> cluster = CLUSTERER.cluster(allGazEntries, PRECISION);
    CLUSTERER.scoreClusters(cluster);

  }

}
