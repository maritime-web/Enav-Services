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

import dk.dma.enav.services.registry.api.Error;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.registry.api.TechnicalDesignId;
import dk.dma.enav.services.registry.mc.model.Instance;
import org.efficiensea2.maritimecloud.serviceregistry.v1.CoverageArea;
import org.efficiensea2.maritimecloud.serviceregistry.v1.ServiceDesignReference;
import org.efficiensea2.maritimecloud.serviceregistry.v1.ServiceInstance;
import org.efficiensea2.maritimecloud.serviceregistry.v1.ServiceLevel;
import org.efficiensea2.maritimecloud.serviceregistry.v1.ServiceStatus;
import org.efficiensea2.maritimecloud.serviceregistry.v1.VendorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 *
 */
public class InstanceMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceMapper.class);

    private final InstanceXmlParser instanceXmlParser;

    @Inject
    InstanceMapper(InstanceXmlParser instanceXmlParser) {
        this.instanceXmlParser = instanceXmlParser;
    }

    InstanceMetadata toMetaData(Instance instance) {
        InstanceMetadata result = new InstanceMetadata(instance.getInstanceId(), instance.getVersion())
                .withInstanceName(instance.getName());

        ServiceInstance details = getDetails(instance);

        TechnicalDesignId technicalDesignId = createTechnicalDesignId(details.getImplementsServiceDesign());
        result
                .withUrl(details.getURL())
                .withTechnicalDesignId(technicalDesignId)
                .withDescription(details.getDescription())
                .withStatus(createStatus(details.getStatus()))
                .withAvailability(details.getOffersServiceLevel().getAvailability())
                .withProducedBy(createVendorInfo(details.getProducedBy()))
                .withProvidedBy(createVendorInfo(details.getProvidedBy()))
        ;
        try {
            result.withBoundary(toGeometryCollection(details));
        } catch (IllegalArgumentException e) {
            result.addError(new Error(e));
        }catch (NullPointerException e) {
            System.err.println("NullPointerException = " + e.getMessage() + " for url " + details.getURL());
        }

        List<Error> validationErrors = result.validate();
        result.addAllErrors(validationErrors);

        return result;
    }


    private ServiceInstance getDetails(Instance instance) {
        try {
            return instanceXmlParser.parseInstanceXml(instance.getInstanceAsXml());
        } catch (Exception e) {
            LOGGER.warn("Error parsing xml from instance:\n" + instance.toString(), e);
            ServiceInstance serviceInstance = new ServiceInstance();
            serviceInstance.setOffersServiceLevel(new ServiceLevel());
            serviceInstance.setCoversAreas(new ServiceInstance.CoversAreas());
            serviceInstance.setImplementsServiceDesign(new ServiceDesignReference());
            serviceInstance.setProducedBy(new VendorInfo());
            serviceInstance.setProvidedBy(new VendorInfo());

            return serviceInstance;
        }
    }

    private dk.dma.enav.services.registry.api.VendorInfo createVendorInfo(VendorInfo vendorInfo) {
        dk.dma.enav.services.registry.api.VendorInfo res = new dk.dma.enav.services.registry.api.VendorInfo(vendorInfo.getId());
        res.withName(vendorInfo.getName())
                .withDescription(vendorInfo.getDescription())
                .withContactInfo(vendorInfo.getContactInfo())
                .withCommercial(vendorInfo.getIsCommercial())
        ;
        return res;
    }

    private String toGeometryCollection(ServiceInstance inst) {
        Stream<CoverageArea> coverageAreaStream = inst.getCoversAreas().getCoversArea().stream();
        return "GEOMETRYCOLLECTION(" + coverageAreaStream.map(CoverageArea::getGeometryAsWKT).collect(joining(", ")) + ")";
    }

    private TechnicalDesignId createTechnicalDesignId(ServiceDesignReference ref) {
        try {
            return new TechnicalDesignId(ref.getId(), ref.getVersion());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String createStatus(ServiceStatus status) {
        return status != null ? status.value() : "Unkown status";
    }
}
