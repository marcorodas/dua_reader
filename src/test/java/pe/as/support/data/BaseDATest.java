package pe.as.support.data;

import org.junit.Before;
import pe.as.support.App;

public abstract class BaseDATest {
    @Before
    public void setUp() {
        App.configDB();
    }
}
