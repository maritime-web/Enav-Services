package dk.dma.enav.services.registry.mc;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

/**
 * Test for XML rewriting
 * Created by rob on 9/6/17.
 */
public class XmlRewriterTest {
    @Test
    public void shouldAddNamespaceQualifierToChildElement() throws Exception {
        String someXML = "<ServiceInstanceSchema:serviceInstance xmlns:ServiceInstanceSchema=\"http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd\">\n" +
                "    <name>Unit Test 0</name>\n" +
                "    <id>urn:mrn:mcl:service:instance:dma:nw-nm-test</id>\n" +
                "</ServiceInstanceSchema:serviceInstance>";

        XmlRewriter cut = new XmlRewriter();
        String res = cut.correctNamespaceIssues(someXML);

        assertThat(res, containsString("ServiceInstanceSchema:id"));
    }

    @Test
    public void shouldReturnUnmodifiedXMLWhenNoQualifierOnRootTag() throws Exception {
        String someXML = "<serviceInstance xmlns:ServiceInstanceSchema=\"http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd\">\n" +
                "    <name>Unit Test 1</name>\n" +
                "    <id>urn:mrn:mcl:service:instance:dma:nw-nm-test</id>\n" +
                "</serviceInstance>";

        XmlRewriter cut = new XmlRewriter();
        String res = cut.correctNamespaceIssues(someXML);

        assertThat(res, is(someXML));
    }

    @Test
    public void shouldReturnUnmodifiedXMLWhenRootTagIsUnknown() throws Exception {
        String someXML = "<t xmlns:ServiceInstanceSchema=\"http://efficiensea2.org/maritime-cloud/service-registry/v1/ServiceInstanceSchema.xsd\">\n" +
                "    <name>Unit Test 2</name>\n" +
                "    <id>urn:mrn:mcl:service:instance:dma:nw-nm-test</id>\n" +
                "</t>";

        XmlRewriter cut = new XmlRewriter();
        String res = cut.correctNamespaceIssues(someXML);

        assertThat(res, is(someXML));
    }

}

