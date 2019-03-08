package trafficsimulator.simulator.statistics.dto;

import lombok.Data;

/**
 * Changeset object which is used by statistics to add 
 * those values to {@link VehicleStatistics} and {@link SimulationStepStatistics}
 * @author Petr Svana
 */

@Data
public class VehicleChangeset
{
  private final Double vehicleCO2;
  private final Double vehicleCO;
  private final Double vehicleHC;
  private final Double vehiclePMx;
  private final Double vehicleNOx;
  private final Double vehicleFuelConsumption;  
  private final Double waitingTime;
}
