package trafficsimulator.shared.dto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CoordinatesTest
{
  @RunWith(MockitoJUnitRunner.class)
  public static class moveByDistanceTest
  {
    private static int distance = 250;

    @Test
    public void move_up_by_distance()
    {
      //PREPARE
      Angle upAngle = new Angle(180);
      Angle reverseAngle = new Angle(0);
      Coordinates coordinates = new Coordinates(0.0, 0.0);
      //EXECUTE
      Coordinates movedCoordinates = coordinates.moveByDistance(upAngle, distance);
      Coordinates originalCoordinates = movedCoordinates.moveByDistance(reverseAngle, distance);
      //VERIFY
      assertThat("X coordinate should stay the same", movedCoordinates.getX(), is(equalTo(0.0)));
      assertThat("Y coordinate should be moved by 250 ", movedCoordinates.getY(), is(equalTo(250.0)));
      assertThat("Application of reverse arguments on new coordinates should lead to original coordinates",
        coordinates, is(equalTo(originalCoordinates)));
    }

    @Test
    public void move_right_by_distance()
    {
      Angle rightAngle = new Angle(270);
      Angle reverseAngle = new Angle(90);
      Coordinates coordinates = new Coordinates(0.0, 0.0);
      //EXECUTE
      Coordinates movedCoordinates = coordinates.moveByDistance(rightAngle, distance);
      Coordinates originalCoordinates = movedCoordinates.moveByDistance(reverseAngle, distance);
      //VERIFY
      assertThat("X coordinate should be moved by 250", movedCoordinates.getX(), is(equalTo(250.0)));
      assertThat("Y coordinate should stay the same", movedCoordinates.getY(), is(equalTo(0.0)));
      assertThat("Application of reverse arguments on new coordinates should lead to original coordinates",
        coordinates, is(equalTo(originalCoordinates)));
    }

    @Test
    public void move_left_down_by_distance()
    {
      Angle leftTopAngle = new Angle(45);
      Angle reverseAngle = new Angle(225);
      Coordinates coordinates = new Coordinates(0.0, 0.0);
      //EXECUTE
      Coordinates movedCoordinates = coordinates.moveByDistance(leftTopAngle, distance);
      Coordinates originalCoordinates = movedCoordinates.moveByDistance(reverseAngle, distance);
      //VERIFY
      assertThat("X coordinate should be moved by -176.77669529663686", movedCoordinates.getX(),
        is(equalTo(-176.77669529663686)));
      assertThat("Y coordinate should be moved by -176.7766952966369 ", movedCoordinates.getY(),
        is(equalTo(-176.7766952966369)));
      assertThat("Application of reverse arguments on new coordinates should lead to original coordinates",
        coordinates, is(equalTo(originalCoordinates)));
    }
  }
}
