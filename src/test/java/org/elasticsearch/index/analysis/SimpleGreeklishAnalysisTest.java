package org.elasticsearch.index.analysis;

import org.hamcrest.MatcherAssert;
import org.testng.annotations.Test;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.inject.Injector;
import org.elasticsearch.common.inject.ModulesBuilder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsModule;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.EnvironmentModule;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexNameModule;
import org.elasticsearch.index.settings.IndexSettingsModule;
import org.elasticsearch.indices.analysis.IndicesAnalysisModule;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;
import static org.hamcrest.Matchers.instanceOf;

public class SimpleGreeklishAnalysisTest {

    @Test
    public void testGreeklishAnalysis() {
        Index index = new Index("test");

        Settings indexSettings = settingsBuilder()
            .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT).build();

        Injector parentInjector = new ModulesBuilder()
            .add(new SettingsModule(indexSettings),
                 new EnvironmentModule(new Environment(indexSettings)),
                 new IndicesAnalysisModule()).createInjector();

        Injector injector = new ModulesBuilder()
            .add(new IndexSettingsModule(index, indexSettings),
                 new IndexNameModule(index),
                 new AnalysisModule(indexSettings,
                                    parentInjector.getInstance(IndicesAnalysisService.class))
                 .addProcessor(new GreeklishBinderProcessor()))
            .createChildInjector(parentInjector);

        AnalysisService analysisService = injector.getInstance(AnalysisService.class);

        TokenFilterFactory filterFactory = analysisService.tokenFilter("greeklish");
        MatcherAssert.assertThat(filterFactory, instanceOf(GreeklishTokenFilterFactory.class));
    }

}
