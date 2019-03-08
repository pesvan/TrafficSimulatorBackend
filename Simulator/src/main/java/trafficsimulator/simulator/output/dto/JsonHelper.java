package trafficsimulator.simulator.output.dto;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import trafficsimulator.shared.dto.Coordinates;

public class JsonHelper
{
  private static DecimalFormat df = new DecimalFormat("#.####");

  public static JsonCoordinates roundCoordinates(Coordinates coordinates)
  {
    return new JsonCoordinates(
      roundDouble(coordinates.getX()),
      roundDouble(coordinates.getY()));
  }

  public static double roundDouble(double number)
  {
    df.setRoundingMode(RoundingMode.HALF_UP);
    String rounded = df.format(number).replace(',', '.');
    return Double.parseDouble(rounded);
  }
}
