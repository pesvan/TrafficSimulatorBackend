package trafficsimulator.shared.dto;

import lombok.Data;
import trafficsimulator.shared.enumerators.VehicleTypeEnum;

/**
 * DTO VehicleType
 * @author z003ru0y
 *
 */
@Data
public class VehicleType 
{
	private final String name;
	private final double accel;
	private final double decel;		
	private final double length;	
	
	/**
	 * @param name name string enum
	 * @param accel acceleration of vehicle 
	 * @param decel deceleration of vehicle
	 * @param length length of vehicle
	 */
	public VehicleType(VehicleTypeEnum name, double accel, double decel, double length) 
	{
		this.name = name.getValue();
		this.accel = accel;
		this.decel = decel;
		this.length = length;
	}
}
