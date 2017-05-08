package dk.dma.enav.services.registry;

import dk.dma.enav.services.registry.api.EnavServiceRegister;
import dk.dma.enav.services.registry.api.InstanceMetadata;
import dk.dma.enav.services.registry.api.TechnicalDesignId;
import dk.dma.enav.services.registry.api.VendorInfo;

import java.util.Collections;
import java.util.List;

public class StaticServiceRegistry implements EnavServiceRegister {
    @Override
    public List<InstanceMetadata> getServiceInstances(TechnicalDesignId id, String wktLocationFilter) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public List<InstanceMetadata> getServiceInstances(List<String> instanceIds) {
        InstanceMetadata res = new InstanceMetadata("NWNM", "1.0", 1L);
        res
                .setDescription("ArcticWeb specific service registry providing access to the NW-NM service")
                .setName("NWNM Service Endpoint")
                .setProducedBy(new VendorInfo("DMA"))
                .setProvidedBy(new VendorInfo("DMA"))
                .setUrl("https://niord-dma.e-navigation.net/");
        return Collections.singletonList(res);
    }
}
