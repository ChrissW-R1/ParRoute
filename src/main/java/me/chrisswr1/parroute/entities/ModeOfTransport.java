package me.chrisswr1.parroute.entities;

import java.util.LinkedHashSet;
import java.util.Set;

import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

/**
 * defines modes of transport for access restrictions
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public enum ModeOfTransport
{
	/**
	 * land-based transportation mode
	 * 
	 * @since 0.0.1
	 */
	LAND(null, null),
	/**
	 * any transportation over roads (incl. footways)
	 * 
	 * @since 0.0.1
	 */
	ROAD(null, LAND),
	/**
	 * moving without a vehicle
	 * 
	 * @since 0.0.1
	 */
	NO_VEHICLE(null, ROAD),
	/**
	 * pedestrians
	 * 
	 * @since 0.0.1
	 */
	FOOT("foot", NO_VEHICLE),
	/**
	 * inline skates
	 * 
	 * @since 0.0.1
	 */
	INLINE_SKATES("inline_skates", NO_VEHICLE),
	/**
	 * ice skates
	 * 
	 * @since 0.0.1
	 */
	ICE_SKATES("ice_skates", NO_VEHICLE),
	/**
	 * skiing
	 * 
	 * @since 0.0.1
	 */
	SKI("ski", NO_VEHICLE),
	/**
	 * cross-county skiing
	 * 
	 * @since 0.0.1
	 */
	SKI_NORDIC("ski:nordic", SKI),
	/**
	 * downhill skiing
	 * 
	 * @since 0.0.1
	 */
	SKI_ALPINE("ski:alpine", SKI),
	/**
	 * telemark (free-heel) skiing
	 * 
	 * @since 0.0.1
	 */
	SKI_TELEMARK("ski:telemark", SKI),
	/**
	 * horse riders
	 * 
	 * @since 0.0.1
	 */
	HORSE("horse", NO_VEHICLE),
	/**
	 * category: any vehicle
	 * 
	 * @since 0.0.1
	 */
	VEHICLE("vehicle", ROAD),
	/**
	 * non-motorized vehicle
	 * 
	 * @since 0.0.1
	 */
	NON_MOTORIZED_VEHICLE(null, VEHICLE),
	/**
	 * single-tracked non-motorized vehicle
	 * 
	 * @since 0.0.1
	 */
	SINGLETRACKED_NON_MOTORIZED_VEHICLE(null, NON_MOTORIZED_VEHICLE),
	/**
	 * cyclists
	 * 
	 * @since 0.0.1
	 */
	BICYCLE("bicycle", SINGLETRACKED_NON_MOTORIZED_VEHICLE),
	/**
	 * double-tracked non-motorized vehicle
	 * 
	 * @since 0.0.1
	 */
	DOUBLETRACKED_NON_MOTORIZED_VEHICLE(null, NON_MOTORIZED_VEHICLE),
	/**
	 * horse(s) + carriage
	 * 
	 * @since 0.0.1
	 */
	CARRIAGE("carriage", DOUBLETRACKED_NON_MOTORIZED_VEHICLE),
	/**
	 * needs to be towed by another vehicle
	 * 
	 * @since 0.0.1
	 */
	TRAILER("trailer", DOUBLETRACKED_NON_MOTORIZED_VEHICLE),
	/**
	 * a travel trailer, also known as caravan
	 * 
	 * @since 0.0.1
	 */
	CARAVAN("caravan", TRAILER),
	/**
	 * category: any motorized vehicle
	 * 
	 * @since 0.0.1
	 */
	MOTOR_VEHICLE("motor_vehicle", VEHICLE),
	/**
	 * single-tracked motorized vehicle
	 * 
	 * @since 0.0.1
	 */
	SINGLETRACKED_MOTOR_VEHICLE(null, MOTOR_VEHICLE),
	/**
	 * a two-wheeled motor vehicle, allowed to drive on motorways
	 * 
	 * @since 0.0.1
	 */
	MOTORCYCLE("motorcycle", SINGLETRACKED_MOTOR_VEHICLE),
	/**
	 * motorized bicycles with a speed restriction
	 * 
	 * @since 0.0.1
	 */
	MOPED("moped", SINGLETRACKED_MOTOR_VEHICLE),
	/**
	 * "low performance moped", usually with a maximum design speed of 25 km/h
	 * 
	 * @since 0.0.1
	 */
	MOFA("mofa", SINGLETRACKED_MOTOR_VEHICLE),
	/**
	 * double-tracked motorized vehicle
	 * 
	 * @since 0.0.1
	 */
	DOUBLETRACK_MOTOR_VEHICLE(null, MOTOR_VEHICLE),
	/**
	 * automobiles/cars
	 * 
	 * @since 0.0.1
	 */
	MOTORCAR("motorcar", DOUBLETRACK_MOTOR_VEHICLE),
	/**
	 * a motorhome
	 * 
	 * @since 0.0.1
	 */
	MOTORHOME("motorhome", MOTORCAR),
	/**
	 * a bus that is not acting as a public transport bus service
	 * 
	 * @since 0.0.1
	 */
	TOURIST_BUS("tourist_bus", DOUBLETRACK_MOTOR_VEHICLE),
	/**
	 * light commercial vehicles
	 * 
	 * @since 0.0.1
	 */
	GOODS("goods", DOUBLETRACK_MOTOR_VEHICLE),
	/**
	 * heavy goods vehicle
	 * 
	 * @since 0.0.1
	 */
	HGV("hgv", DOUBLETRACK_MOTOR_VEHICLE),
	/**
	 * EuroCombi
	 * 
	 * @since 0.0.1
	 */
	BDOUBLE("bdouble", DOUBLETRACK_MOTOR_VEHICLE),
	/**
	 * agricultural motor vehicles
	 * 
	 * @since 0.0.1
	 */
	AGRICULTURAL("agricultural", DOUBLETRACK_MOTOR_VEHICLE),
	/**
	 * a.k.a. Quad (bike)
	 * 
	 * @since 0.0.1
	 */
	ATV("atv", DOUBLETRACK_MOTOR_VEHICLE),
	/**
	 * a snowmobile (with a chain drive)
	 * 
	 * @since 0.0.1
	 */
	SNOWMOBILE("snowmobile", DOUBLETRACK_MOTOR_VEHICLE),
	/**
	 * category: any rail-based transportation mode
	 * 
	 * @since 0.0.1
	 */
	RAIL(null, LAND),
	/**
	 * trains
	 * 
	 * @since 0.0.1
	 */
	TRAIN("train", RAIL),
	/**
	 * category: any water-based transportation mode
	 * 
	 * @since 0.0.1
	 */
	WATER(null, null),
	/**
	 * covers small boats and pleasure crafts, including yachts
	 * 
	 * @since 0.0.1
	 */
	BOAT("boat", WATER),
	/**
	 * boats and yachts using motor
	 * 
	 * @since 0.0.1
	 */
	MOTORBOAT("motorboat", BOAT),
	/**
	 * boats and yachts using sails
	 * 
	 * @since 0.0.1
	 */
	SAILBOAT("sailboat", BOAT),
	/**
	 * boats without sail or motor, such as small dinghies, canoes, kayaks, etc.
	 * 
	 * @since 0.0.1
	 */
	CANOE("canoe", BOAT),
	/**
	 * covers fishing vessels of any size
	 * 
	 * @since 0.0.1
	 */
	FISHING_VESSEL("fishing_vessel", WATER),
	/**
	 * covers commercial vessels of any size and any trade
	 * 
	 * @since 0.0.1
	 */
	SHIP("ship", WATER),
	/**
	 * ships carrying passengers, either in route (ferries, etc.) or as cruise
	 * 
	 * @since 0.0.1
	 */
	PASSENGER("passenger", SHIP),
	/**
	 * any type of cargo ship
	 * 
	 * @since 0.0.1
	 */
	CARGO("cargo", SHIP),
	/**
	 * covers all dry bulk cargo
	 * 
	 * @since 0.0.1
	 */
	BULK("bulk", CARGO),
	/**
	 * covers all wet bulk cargo, including compressed gas
	 * 
	 * @since 0.0.1
	 */
	TANKER("tanker", CARGO),
	/**
	 * compressed or liquefied gas
	 * 
	 * @since 0.0.1
	 */
	TANKER_GAS("tanker:gas", TANKER),
	/**
	 * crude oil and oil products
	 * 
	 * @since 0.0.1
	 */
	TANKER_OIL("tanker:oil", TANKER),
	/**
	 * all other products in tanks
	 * 
	 * @since 0.0.1
	 */
	TANKER_CHEMICAL("tanker:chemical", TANKER),
	/**
	 * special coverage for single hull
	 * 
	 * @since 0.0.1
	 */
	TANKER_SINGLEHULL("tanker:singlehull", TANKER),
	/**
	 * collective tag for general cargo
	 * 
	 * @since 0.0.1
	 */
	CONTAINER("container", CARGO),
	/**
	 * dangerous cargo covered by the IMDG code
	 * 
	 * @since 0.0.1
	 */
	IMDG("imdg", CARGO),
	/**
	 * International Ship and Port Facility Security Regulation.
	 * 
	 * @since 0.0.1
	 */
	ISPS("isps", SHIP),
	/**
	 * air-based transportation
	 * 
	 * @since 0.0.1
	 */
	AVIATION(null, null),
	// by use
	/**
	 * public service vehicle
	 * 
	 * @since 0.0.1
	 */
	PSV("psv", DOUBLETRACK_MOTOR_VEHICLE),
	/**
	 * a bus acting as a public service vehicle
	 * 
	 * @since 0.0.1
	 */
	BUS("bus", PSV),
	/**
	 * taxi
	 * 
	 * @since 0.0.1
	 */
	TAXI("taxi", PSV);
	
	private final String			key;
	private final ModeOfTransport	parent;
									
	/**
	 * constructor, with all given attributes
	 * 
	 * @since 0.0.1
	 * 		
	 * @param key the {@link Tag} key
	 * @param parent the super category
	 */
	private ModeOfTransport(String key, ModeOfTransport parent)
	{
		this.key = key;
		this.parent = parent;
	}
	
	/**
	 * gives all {@link Tag} keys, which apply to this mode
	 * 
	 * @since 0.0.1
	 * 		
	 * @return a {@link Set} with all keys, starting with the most restrictive
	 */
	public Set<String> keys()
	{
		Set<String> res = new LinkedHashSet<>();
		
		if (this.key != null && ! (this.key.isEmpty()))
		{
			res.add(this.key);
		}
		
		if (this.parent != null)
		{
			res.addAll(this.parent.keys());
		}
		
		return res;
	}
	
	/**
	 * checks if this mode is also in a specific category
	 * 
	 * @since 0.0.1
	 * 		
	 * @param mode the category to check for
	 * @return <code>true</code> if this is also a {@code mode},
	 *         <code>false</code> otherwise
	 */
	public boolean is(ModeOfTransport mode)
	{
		if (this == mode)
		{
			return true;
		}
		
		if (this.parent == null)
		{
			return false;
		}
		
		return this.parent.is(mode);
	}
}