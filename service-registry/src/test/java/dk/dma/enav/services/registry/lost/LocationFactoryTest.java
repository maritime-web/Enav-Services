package dk.dma.enav.services.registry.lost;

import ietf.lost1.Location;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

/**
 * Created by Steen on 25-05-2016.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LocationFactoryTest {
    @Mock(answer = Answers.RETURNS_MOCKS)
    private Geodetic2DFactory geodetic2DFactory;
    @InjectMocks
    private LocationFactory cut;

    @Test
    public void shouldUseGeodetic2DAsLocationProfile() throws Exception {
        Location location = cut.createLocation(11.0001234, 22.0005678);

        assertThat(location, hasProperty("profile", equalTo("geodetic-2d")));
    }


}