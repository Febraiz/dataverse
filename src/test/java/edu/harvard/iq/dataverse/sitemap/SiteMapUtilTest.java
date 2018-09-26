package edu.harvard.iq.dataverse.sitemap;

import edu.harvard.iq.dataverse.Dataset;
import edu.harvard.iq.dataverse.DatasetVersion;
import edu.harvard.iq.dataverse.GlobalId;
import edu.harvard.iq.dataverse.harvest.client.HarvestingClient;
import edu.harvard.iq.dataverse.util.xml.XmlPrinter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class SiteMapUtilTest {

    @Test
    public void testUpdateSiteMap() throws IOException {

        List<Dataset> datasets = new ArrayList<>();

        Dataset published = new Dataset();
        String publishedPid = "doi:10.666/FAKE/published1";
        published.setGlobalId(new GlobalId(publishedPid));
        published.setPublicationDate(new Timestamp(new Date().getTime()));
        datasets.add(published);

        Dataset unpublished = new Dataset();
        String unpublishedPid = "doi:10.666/FAKE/unpublished1";
        unpublished.setGlobalId(new GlobalId(unpublishedPid));
        Timestamp nullPublicationDateToIndicateNotPublished = null;
        unpublished.setPublicationDate(nullPublicationDateToIndicateNotPublished);
        datasets.add(unpublished);

        Dataset harvested = new Dataset();
        String harvestedPid = "doi:10.666/FAKE/harvested1";
        harvested.setGlobalId(new GlobalId(harvestedPid));
        harvested.setPublicationDate(new Timestamp(new Date().getTime()));
        harvested.setHarvestedFrom(new HarvestingClient());
        datasets.add(harvested);

        Dataset deaccessioned = new Dataset();
        String deaccessionedPid = "doi:10.666/FAKE/harvested1";
        deaccessioned.setGlobalId(new GlobalId(deaccessionedPid));
        deaccessioned.setPublicationDate(new Timestamp(new Date().getTime()));
        List<DatasetVersion> datasetVersions = new ArrayList<>();
        DatasetVersion datasetVersion = new DatasetVersion();
        datasetVersion.setVersionState(DatasetVersion.VersionState.DEACCESSIONED);
        datasetVersions.add(datasetVersion);
        deaccessioned.setVersions(datasetVersions);
        datasets.add(deaccessioned);

        SiteMapUtil.updateSiteMap(datasets);

        File sitemapFile = new File("/tmp/sitemap.xml");
        String sitemapString = XmlPrinter.prettyPrintXml(new String(Files.readAllBytes(Paths.get(sitemapFile.getAbsolutePath()))));
        System.out.println("sitemap: " + sitemapString);

        assertTrue(sitemapString.contains(publishedPid));
        assertFalse(sitemapString.contains(unpublishedPid));
        assertFalse(sitemapString.contains(harvestedPid));
        assertFalse(sitemapString.contains(deaccessionedPid));

    }

}
