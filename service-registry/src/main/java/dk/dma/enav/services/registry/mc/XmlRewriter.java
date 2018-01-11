/* Copyright (c) 2011 Danish Maritime Authority.
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
package dk.dma.enav.services.registry.mc;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.jcip.annotations.ThreadSafe;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

/**
 *
 * The Xml sent from the service registry uses namespaces incorrectly, this confuses the JaxB xml parsing, and causes
 * some of the fields in the constructed objects to be null, especially with newer JDKs (1.8u91+) which are less lenient.
 * This class uses Dom4J to manipulate the XML, to produce XML which is correctly namespaced so it can be processed by JaxB
 *
 * @author Klaus Groenbaek
 *         Created 07/03/17.
 */
@Slf4j
@ThreadSafe
public class XmlRewriter {

    /**
     * producedBy and providedBy are of type VendorInfo which is defined in ServiceBaseTypesSchema.xsd, the elements of this
     * type is not namespaced since the elementFormDefault attribute is not set for this xsd.
     * ServiceSpecificationSchema.xsd has elementFormDefault="qualified" so element from type define there must belong to the namespace
     */
    private static final List<String> REWRITE_PARENT_NODE_ONLY = Lists.newArrayList("producedBy", "providedBy");

    public String correctNamespaceIssues(String xml) {

        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new StringReader(xml));

            Element rootElement = document.getRootElement();
            if (!rootElement.getQualifiedName().equals("ServiceInstanceSchema:serviceInstance")) {
                // if the document changes we do not modify it
                return xml;
            }

            Namespace namespace = rootElement.getNamespace();
            @SuppressWarnings("unchecked")
            List<Element> list = rootElement.selectNodes("./*");

            for (Element element : list) {
                element.setQName(new QName(element.getName(), namespace));
                if (!REWRITE_PARENT_NODE_ONLY.contains(element.getName())) {
                    // we should rewrite the children because the type comes from ServiceInstanceSchema.xsd and the element must be namespaced
                    addNamespace(element, namespace);
                }
            }
            OutputFormat format = OutputFormat.createPrettyPrint();
            StringWriter stringWriter = new StringWriter();
            XMLWriter writer = new XMLWriter(stringWriter, format );
            writer.write(document);
            return stringWriter.toString();
        } catch (DocumentException | IOException e) {
            log.error("Unable to rewrite xml\n " + xml + "\n", e);
        }
        return xml;
    }
    private  void addNamespace(Element element, Namespace namespace) {
        @SuppressWarnings("unchecked") List<Element> children = element.selectNodes("./*");
        for (Element child : children) {
            child.setQName(new QName(child.getName(), namespace));
            addNamespace(child, namespace);
        }
    }
}
