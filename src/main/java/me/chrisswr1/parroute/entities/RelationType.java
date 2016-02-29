package me.chrisswr1.parroute.entities;

import org.openstreetmap.osmosis.core.domain.v0_6.Relation;

/**
 * defines the type of a {@link Relation}
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public enum RelationType
{
	/**
	 * an unknown type
	 * 
	 * @since 0.0.1
	 */
	UNKNOWN,
	/**
	 * type of turn restrictions
	 * 
	 * @since 0.0.1
	 */
	RESTRICTION;
}