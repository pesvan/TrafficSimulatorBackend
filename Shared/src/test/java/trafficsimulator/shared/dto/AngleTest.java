package trafficsimulator.shared.dto;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;



public class AngleTest
{

    @RunWith(MockitoJUnitRunner.class)
    public static class addToAngleMethod
    {
        @Test
        public void basic_arithmetic_test() throws Exception
        {
            // PREPARE
            Angle angle1 = new Angle(20);
            Angle angle2 = new Angle(50);
            // EXECUTE
            angle1.addToAngle(50);
            angle2.addToAngle(309);
            // VERIFY
            assertThat("20 + 50 = 70", angle1.getValue(), is(equalTo(70)));
            assertThat("50 + 309 = 359", angle2.getValue(), is(equalTo(359)));
        }

        @Test
        public void overflow_test() throws Exception
        {
            // PREPARE
            Angle angle1 = new Angle(0);
            // EXECUTE
            angle1.addToAngle(400);
            // VERIFY
            assertThat("400 - 360 = 40", angle1.getValue(), is(equalTo(40)));
        }

        @Test(expected = IllegalArgumentException.class)
        public void negative_addition_throws_exception() throws Exception
        {
            Angle angle1 = new Angle(6);
            angle1.addToAngle(-5);
        }

    }


}
