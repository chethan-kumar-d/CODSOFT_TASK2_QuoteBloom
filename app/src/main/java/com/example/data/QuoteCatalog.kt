package com.example.data

object QuoteCatalog {
    /**
     * Complete anthology of 380 curated quotes across 5 categories (76 per category).
     * All original 65 quote IDs (mot_01..13, lrn_01..13, cnf_01..13, knd_01..13, lif_01..13)
     * are strictly preserved for 100% backward compatibility with saved favourites and records.
     */
    val quotes: List<Quote> = buildList {
        addAll(MotivationCatalog.quotes)
        addAll(LearningCatalog.quotes)
        addAll(ConfidenceCatalog.quotes)
        addAll(KindnessCatalog.quotes)
        addAll(LifeCatalog.quotes)
    }
}
