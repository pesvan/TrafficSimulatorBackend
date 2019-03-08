package trafficsimulator.simulator.interfaces.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import trafficsimulator.simulator.interfaces.StatisticsControl;
import trafficsimulator.simulator.output.dto.JsonStatistics;
import trafficsimulator.simulator.output.dto.JsonStepStatistics;
import trafficsimulator.simulator.output.dto.JsonVehicleStatistics;
import trafficsimulator.simulator.statistics.dto.SimulationStepStatistics;
import trafficsimulator.simulator.statistics.dto.VehicleChangeset;
import trafficsimulator.simulator.statistics.dto.VehicleStatistics;

/**
 * @author z003ru0y
 *
 */
public class StatisticsControlImpl implements StatisticsControl
{
  private double simulationTime;
  private double simulationStepLength;
  private int totalVehiclesAdded;

  private Map<String, VehicleStatistics> vehicles;
  private List<SimulationStepStatistics> simSteps;

  /**
   * @param simulationStepLength simulation step length
   */
  public StatisticsControlImpl(double simulationStepLength)
  {
    this.simulationTime = 0;
    this.simulationStepLength = simulationStepLength;
    this.totalVehiclesAdded = 0;

    this.vehicles = new HashMap<>();
    this.simSteps = new ArrayList<>();
  }

  @Override
  public void setSimulationTime(double simulationTime)
  {
    this.simulationTime = simulationTime;
    simSteps.add(new SimulationStepStatistics(simulationTime));
  }

  @Override
  public void addVehicle(String id)
  {
    vehicles.put(id, new VehicleStatistics(id));
    totalVehiclesAdded++;
  }

  @Override
  public void updateVehicle(String id, VehicleChangeset changes)
  {
    vehicles.get(id).update(changes);
    simSteps.get(simSteps.size()-1).update(changes);
  }

  @Override
  public JsonStatistics generateStatistics()
  {
    List<JsonVehicleStatistics> vehicles = this.vehicles.values().stream()
      .map(veh -> veh.generateVehicleResult())
      .collect(Collectors.toList());
    
    List<JsonStepStatistics> steps = this.simSteps.stream()
      .map(step -> step.generateStepResult())
      .collect(Collectors.toList());

    double totalCO2 =
      vehicles.stream().mapToDouble(JsonVehicleStatistics::getVehicleTotalCO2).sum();
    double totalCO =
      vehicles.stream().mapToDouble(JsonVehicleStatistics::getVehicleTotalCO).sum();

    double totalHC =
      vehicles.stream().mapToDouble(JsonVehicleStatistics::getVehicleTotalHC).sum();
    double totalPMx =
      vehicles.stream().mapToDouble(JsonVehicleStatistics::getVehicleTotalPMx).sum();
    double totalNOx =
      vehicles.stream().mapToDouble(JsonVehicleStatistics::getVehicleTotalNOx).sum();
    double totalFuelConsumption =
      vehicles.stream().mapToDouble(JsonVehicleStatistics::getVehicleTotalFuelConsumption).sum();

    double averageVehicleTimeInSimulation =
      vehicles.stream().mapToDouble(JsonVehicleStatistics::getTimeInSimulation).average().getAsDouble();
    
    double averageVehicleWaitingTime =
      vehicles.stream().mapToDouble(JsonVehicleStatistics::getVehicleWaitingTime).average().getAsDouble();
    
    int[] vehiclesInTime = steps.stream().mapToInt(step -> step.getVehicleCount()).toArray();
   // double[] vehiclesWaitingInTime = steps.stream().mapToDouble(step -> step.).toArray();
    double[] COInTime = steps.stream().mapToDouble(step -> step.getStepTotalCO()).toArray();
    double[] CO2InTime = steps.stream().mapToDouble(step -> step.getStepTotalCO2()).toArray();
    double[] NOxInTime = steps.stream().mapToDouble(step -> step.getStepTotalNOx()).toArray();
    double[] PMxInTime = steps.stream().mapToDouble(step -> step.getStepTotalPMx()).toArray();
    double[] HCInTime = steps.stream().mapToDouble(step -> step.getStepTotalHC()).toArray();
    double[] FuelInTime = steps.stream().mapToDouble(step -> step.getStepTotalFuelConsumption()).toArray();
    
    double[] simSteps = steps.stream().mapToDouble(step -> step.getSimTime()).toArray();
    

    return new JsonStatistics(vehicles, steps, simulationTime, simulationStepLength, totalVehiclesAdded,
      totalCO2, totalCO, totalHC, totalPMx, totalNOx, totalFuelConsumption,
      averageVehicleTimeInSimulation, averageVehicleWaitingTime, simSteps, vehiclesInTime, COInTime, CO2InTime,
      NOxInTime, PMxInTime, HCInTime, FuelInTime);
  }

}
