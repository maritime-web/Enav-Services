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
package dk.dma.enav.services.s124;

import org.geotools.xml.Parser;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DataSetMapperTest {
    private SimpleFeature fc;

    @Before
    public void setUp() throws Exception {
        InputStream testDoc = getClass().getClassLoader().getResourceAsStream("S124-test-dummy.xml");
        org.geotools.xml.Configuration config = new S124Configuration(S124XSD.getInstance());

        Parser parser = new Parser(config);
        parser.setStrict(true);

        fc = (SimpleFeature) parser.parse(testDoc);
    }

    @Test
    public void shouldMapTitle() {
        S124View s124View = new DataSetMapper().toViewType(fc);

        assertThat(s124View.getTitle(), is("Denmark. Kattegat. Randers Fiord. Light buoy replaced."));
    }

    @Test
    public void shouldMapAreaHeading() {
        S124View s124View = new DataSetMapper().toViewType(fc);

        assertThat(s124View.getAreaHeading(), is("Kattegat - Kattegat - Randers Fiord"));
    }

    @Test
    public void geoToolsGml() throws JAXBException, ParserConfigurationException, SAXException, IOException {

/*
        GML gml = new GML(GML.Version.GML3);
        gml.setCoordinateReferenceSystem(DefaultGeographicCRS.WGS84);
*/

//        InputStream testDoc = getClass().getClassLoader().getResourceAsStream("gmltest.xml");
        InputStream testDoc = getClass().getClassLoader().getResourceAsStream("S124-test-dummy.xml");


/*
        try (SimpleFeatureIterator simpleFeatureIterator = gml.decodeFeatureIterator(testDoc)) {
            while (simpleFeatureIterator.hasNext()) {
                SimpleFeature feature = simpleFeatureIterator.next();
                System.out.println(feature);
                System.out.println(feature.getID());
            }
        }
*/

//        org.geotools.xml.Configuration configuration = new org.geotools.gml3.v3_2.GMLConfiguration(true);

        String namespace = "http://www.iho.int/S124/gml/cs0/0.1";
        String schemaLocation = getClass().getClassLoader().getResource("schema/S124.xsd").toString();
        ApplicationSchemaXSD xsd = new ApplicationSchemaXSD(namespace, schemaLocation);
        org.geotools.xml.Configuration config = new S124Configuration(xsd);

        Parser parser = new Parser(config);
        parser.setStrict(true);

        SimpleFeature fc = (SimpleFeature) parser.parse(testDoc);
        System.out.println(fc);

        System.out.println(fc.getID());
        System.out.println(fc.getAttributeCount());
/*
        System.out.println(fc.getDefaultGeometry());
        System.out.println(fc.getType());
        fc.getAttributes().forEach(a -> {
            if (a != null) {
                System.out.println(a.getClass());
                System.out.println(a);
            }

        });
*/

        System.out.println("Preamble Start");
        Object imember = fc.getAttribute("imember");
        System.out.println(imember.getClass().getSimpleName());
        System.out.println(imember);
        System.out.println("Preamble End");


/*
        System.out.println(fc.getAttribute("geometry"));
        StringWriter output = new StringWriter();
        GeoJSON.write(fc.getAttribute("geometry"), output);
        System.out.println(output);
        GeometryVo geometry = JtsConverter.fromJts((Geometry) fc.getAttribute("geometry"));
        System.out.println(fc.getAttribute("warningInformation"));
*/
/*
        JAXBElement<NavigationalWarningFeaturePartType> navigationalWarningFeaturePart = createNavigationalWarningFeaturePart();
        navigationalWarningFeaturePart.getValue().getGeometry().forEach(geom -> {

            StringWriter writer = new StringWriter();
            try {
                Marshaller marshaller = JAXBContext.newInstance("_int.iho.s124.gml.cs0._0").createMarshaller();
                marshaller.marshal(geom.getSurfaceProperty().getAbstractSurface(), writer);
                System.out.println(writer);
                ByteArrayInputStream bis = new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8));
                SimpleFeatureCollection simpleFeatureCollection = gml.decodeFeatureCollection(bis);

            } catch (JAXBException | SAXException | ParserConfigurationException | IOException e) {
                e.printStackTrace();
            }


        });
*/


    }

/*
    private FeatureVo toGeoJson(PointCurveSurface pointCurveSurface) {
        FeatureVo res = new FeatureVo();
        SurfacePropertyType surfaceProperty = pointCurveSurface.getSurfaceProperty();
        AbstractSurfaceType value = surfaceProperty.getAbstractSurface().getValue();
        if (value instanceof _int.iho.s100gml._1.SurfaceType) {
            _int.iho.s100gml._1.SurfaceType surfaceValue = (_int.iho.s100gml._1.SurfaceType) value;
            surfaceValue.getPatches().getAbstractSurfacePatch().forEach(p -> {

                AbstractSurfacePatchType patchType = p.getValue();

                if (patchType instanceof PolygonPatchType) {

                    PolygonPatchType polyPatch = ((PolygonPatchType) patchType);
//                    polyPatch.getExterior()

                } else {
                    System.out.println(patchType.getClass().getSimpleName());
                }
            });
        } else {
            System.out.println(value.getClass().getSimpleName());
        }
        String id = value.getId();
//        value.getClass()
        return null;
    }

    private JAXBElement<NavigationalWarningFeaturePartType> createNavigationalWarningFeaturePart() throws JAXBException {
        */
/* GML *//*

        ObjectFactory fac = new ObjectFactory();

        Double[] coords = {55.843, 11.965, 55.828, 11.869, 55.892, 11.837, 55.915, 11.863, 55.897,
                11.936, 55.858, 11.965, 55.843, 11.965, 55.843, 11.965, 55.843, 11.965, 55.843, 11.965,
                55.843, 11.965, 55.843, 11.965, 55.843, 11.965};

        DirectPositionListType directPositionListType = fac.createDirectPositionListType();
        directPositionListType.getValue().addAll(Arrays.asList(coords));

        LinearRingType linearRingType = fac.createLinearRingType();
        linearRingType.setPosList(directPositionListType);

        JAXBElement<LinearRingType> linearRing = fac.createLinearRing(linearRingType);

        AbstractRingPropertyType abstractRingPropertyType = fac.createAbstractRingPropertyType();
        abstractRingPropertyType.setAbstractRing(linearRing);

        PolygonPatchType polygonPatchType = fac.createPolygonPatchType();
        polygonPatchType.setExterior(abstractRingPropertyType);

        JAXBElement<PolygonPatchType> polygonPatch = fac.createPolygonPatch(polygonPatchType);

        SurfacePatchArrayPropertyType surfacePatchArrayPropertyType = fac.createSurfacePatchArrayPropertyType();
        surfacePatchArrayPropertyType.getAbstractSurfacePatch().add(polygonPatch);

        SurfaceType surfaceType = fac.createSurfaceType();
        surfaceType.setPatches(surfacePatchArrayPropertyType);


        */
/* S100 GML *//*

        _int.iho.s100gml._1.ObjectFactory s100GmlFac = new _int.iho.s100gml._1.ObjectFactory();


        _int.iho.s100gml._1.SurfaceType surfaceType_s100 = s100GmlFac.createSurfaceType();
        surfaceType_s100.setId("12345surface");
        surfaceType_s100.setPatches(surfacePatchArrayPropertyType);

        JAXBElement<_int.iho.s100gml._1.SurfaceType> surface = s100GmlFac.createSurface(surfaceType_s100);

        _int.iho.s100gml._1.SurfacePropertyType surfacePropertyType1 = s100GmlFac.createSurfacePropertyType();
        surfacePropertyType1.setAbstractSurface(surface);

        */
/* S124 *//*

        _int.iho.s124.gml.cs0._0.ObjectFactory s124Fac = new _int.iho.s124.gml.cs0._0.ObjectFactory();

        PointCurveSurface pointCurveSurface = s124Fac.createPointCurveSurface();
        pointCurveSurface.setSurfaceProperty(surfacePropertyType1);

        NavigationalWarningFeaturePartType navigationalWarningFeaturePartType = s124Fac.createNavigationalWarningFeaturePartType();
        navigationalWarningFeaturePartType.getGeometry().add(pointCurveSurface);

        JAXBElement<NavigationalWarningFeaturePartType> navigationalWarningFeaturePart = s124Fac.createNavigationalWarningFeaturePart(navigationalWarningFeaturePartType);

        Marshaller marshaller = JAXBContext.newInstance("_int.iho.s124.gml.cs0._0").createMarshaller();

        StringWriter writer = new StringWriter();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(navigationalWarningFeaturePart, writer);

        System.out.println(writer.toString());

        return navigationalWarningFeaturePart;
    }
*/
}
