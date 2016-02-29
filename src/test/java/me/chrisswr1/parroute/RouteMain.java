package me.chrisswr1.parroute;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlWriter;

import me.chrisswr1.parroute.io.OverpassReceiver;

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
		long startTime = System.currentTimeMillis();
		
		try
		{
			OverpassReceiver receiver = new OverpassReceiver();
			DataHandler handler = new DataHandler(receiver);
			receiver.setStore(handler);
			
			long start = 962137765;
			long dest = 42907635;
			
			Route route = new Route(handler, handler.getNode(start), handler.getNode(dest));
			
			RouteMain.LOGGER.debug("Start calculation of a route from " + route.getStart() + " to " + route.getDest() + ".");
			
			if (route.calc())
			{
				RouteMain.LOGGER.info("The following route was found: " + route);
			}
			else
			{
				RouteMain.LOGGER.info("No route was found between " + route.getStart().getId() + " and " + route.getDest().getId());
			}
			
			XmlWriter xmlWriter = new XmlWriter(new File("target/" + start + "_" + dest + ".osm"), CompressionMethod.None);
			for (EntityContainer container : handler.getContainers())
			{
				xmlWriter.process(container);
			}
			xmlWriter.complete();
		}
		catch (IOException e)
		{
			RouteMain.LOGGER.error("Couldn't get an entity from the data handler!", e);
		}
		
		RouteMain.LOGGER.info("Routing calculation finished after " + ((double) (System.currentTimeMillis() - startTime) / 60000) + " minutes.");
	}
}