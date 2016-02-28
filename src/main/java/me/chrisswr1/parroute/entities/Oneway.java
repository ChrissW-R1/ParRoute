package me.chrisswr1.parroute.entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

/**
 * the {@link Oneway} status / direction of a {@link Way}
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public enum Oneway
{
	/**
	 * no {@link Oneway} ({@link Way} is passable in both directions)
	 * 
	 * @since 0.0.1
	 */
	NO,
	/**
	 * the {@link Oneway} direction is the same as the {@link WayNode}s are set
	 * 
	 * @since 0.0.1
	 */
	INDICATED,
	/**
	 * the {@link Oneway} direction is the opposite one of the {@link Way}
	 * 
	 * @since 0.0.1
	 */
	OPPOSITE,
	/**
	 * defines, that the status depends on the time
	 * 
	 * @since 0.0.1
	 */
	REVERSIBLE;
	
	/**
	 * the {@link Logger} of this class
	 * 
	 * @since 0.0.1
	 */
	public static final Logger LOGGER = LogManager.getLogger(Oneway.class);
	
	/**
	 * gives the defined {@link Oneway} status of a {@link Way}
	 * 
	 * @since 0.0.1
	 * 		
	 * @param way the {@link Way} to get the status of
	 * @return the {@link Oneway} status of {@code way}
	 */
	public static Oneway get(Way way)
	{
		Oneway.LOGGER.trace("Parsing oneway tag of " + way + ".");
		
		for (Tag tag : way.getTags())
		{
			if (tag.getKey().equals("oneway"))
			{
				String value = tag.getValue();
				Oneway.LOGGER.trace(way + ": oneway value is '" + value + "'.");
				
				try
				{
					Oneway.LOGGER.trace(way + ": Try parsing oneway value '" + value + "' as a number.");
					double num = Double.parseDouble(value);
					
					Oneway.LOGGER.trace(way + ": oneway value '" + value + "' is a number.");
					
					if (num < 0)
					{
						Oneway.LOGGER.trace(way + ": Found negative number for oneway value '" + value + "'. Returning " + OPPOSITE + ".");
						return OPPOSITE;
					}
					if (num > 0)
					{
						Oneway.LOGGER.trace(way + ": Found positive number for oneway value '" + value + "'. Returning " + INDICATED + ".");
						return Oneway.INDICATED;
					}
					
					return NO;
				}
				catch (NumberFormatException e)
				{
					Oneway.LOGGER.trace(way + ": oneway value of " + way + " is not a number.", e);
				}
				
				Oneway.LOGGER.trace(way + ": Parsing text of " + value + ".");
				if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true"))
				{
					return INDICATED;
				}
				if (value.isEmpty() || value.equalsIgnoreCase("no") || value.equalsIgnoreCase("false"))
				{
					return NO;
				}
				if (value.equalsIgnoreCase("reverse"))
				{
					return Oneway.OPPOSITE;
				}
				if (value.equalsIgnoreCase("reversible"))
				{
					return REVERSIBLE;
				}
				
				Oneway.LOGGER.warn(way + ": Found oneway tag, but couldn't parse it: " + value);
			}
		}
		
		Oneway.LOGGER.trace(way + ": No oneway tag found. Returning " + NO + ".");
		return NO;
	}
}