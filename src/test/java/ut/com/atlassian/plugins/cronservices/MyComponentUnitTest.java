package ut.com.atlassian.plugins.cronservices;

import org.junit.Test;
import com.atlassian.plugins.cronservices.MyPluginComponent;
import com.atlassian.plugins.cronservices.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}