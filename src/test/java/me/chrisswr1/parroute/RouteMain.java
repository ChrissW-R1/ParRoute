package me.chrisswr1.parroute;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.chrisswr1.parroute.io.OverpassHandler;

/**
 * tests the {@link Route} class with calculation in M&uuml;nster
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class RouteMain
{
	/**
	 * the {@link Logger} of this class
	 * 
	 * @since 0.0.1
	 */
	public static final Logger LOGGER = LogManager.getLogger(RouteMain.class);
	
	/**
	 * private standard constructor, to prevent initialization
	 * 
	 * @since 0.0.1
	 */
	private RouteMain()
	{
	}
	
	/**
	 * main method
	 * 
	 * @since 0.0.1
	 * 
	 * @param args arguments from the console
	 */
	public static void main(String[] args)
	{
		try
		{
			OverpassHandler handler = new OverpassHandler();
			Route route = new Route(handler, handler.getNode(962137765), handler.getNode(42907635));
			
			if (route.calc())
			{
				RouteMain.LOGGER.error("The following route was found: " + route);
			}
			else
			{
				RouteMain.LOGGER.error("No route was found between " + route.getStart().getId() + " and " + route.getDest().getId());
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}