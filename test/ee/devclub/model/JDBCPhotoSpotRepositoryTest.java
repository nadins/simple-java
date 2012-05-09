package ee.devclub.model;

import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JDBCPhotoSpotRepositoryTest {
    JDBCPhotoSpotRepository repository = new JDBCPhotoSpotRepository();

    @Before
    public void setUp() throws Exception {
        repository.dataSource = mock(DataSource.class, RETURNS_DEEP_STUBS);
    }

    @Test
    public void spotFieldsAreCorrectlyMappedToDBColumns() throws Exception {
        ResultSet rs = repository.dataSource.getConnection().prepareStatement("select * from PhotoSpot").executeQuery();
        when(rs.next()).thenReturn(true, false);

        when(rs.getString("name")).thenReturn("Kohtuotsa");
        when(rs.getString("description")).thenReturn("Mega place!");
        when(rs.getFloat("latitude")).thenReturn(59.437755f);
        when(rs.getFloat("longitude")).thenReturn(24.74209f);

        List<PhotoSpot> spots = repository.getAllSpots();

        assertEquals(1, spots.size());
        PhotoSpot spot = spots.get(0);

        assertThat(spot.name, is("Kohtuotsa"));
        assertThat(spot.description, is("Mega place!"));
        assertThat(spot.location, is(new Location(59.437755f, 24.74209f)));

        verify(repository.dataSource.getConnection()).close();
    }

    @Test
    public void manySpotFieldsCanBeLoaded() throws Exception {
        ResultSet rs = repository.dataSource.getConnection().prepareStatement("select * from PhotoSpot").executeQuery();
        when(rs.next()).thenReturn(true, true, true, false);
        assertThat(repository.getAllSpots().size(), is(3));
    }
}