package me.chrisswr1.parroute.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;

/**
 * provides methods to use JOSM as a visual debugger
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class JosmDebugger
{
	/**
	 * the {@link Logger} of this class
	 * 
	 * @since 0.0.1
	 */
	public static final Logger LOGGER = LogManager.getLogger(JosmDebugger.class);
	
	/**
	 * private standard constructor, to prevent initialization
	 * 
	 * @since 0.0.1
	 */
	private JosmDebugger()
	{
	}
	
	/**
	 * shows a {@link Node} in JOSM
	 * 
	 * @since 0.0.1
	 * 		
	 * @param node the {@link Node} to show on JOSM
	 */
	private static void showNode(Node node)
	{
		try
		{
			double lat = node.getLatitude();
			double lng = node.getLongitude();
			double diff = 0.0000001;
			URLConnection connection = (new URL("http://127.0.0.1:8111/load_and_zoom?left=" + (lng - diff) + "&top=" + (lat + diff) + "&right=" + (lng + diff) + "&bottom=" + (lat - diff) + "&select=node" + node.getId())).openConnection();
			connection.connect();
			if (connection instanceof HttpURLConnection)
			{
				HttpURLConnection httpConnection = (HttpURLConnection)connection;
				httpConnection.getResponseCode();
			}
			else
			{
				JosmDebugger.LOGGER.error("No HTTP endpoint found on JOSM!");
			}
			IOUtils.close(connection);
		}
		catch (MalformedURLException e)
		{
			JosmDebugger.LOGGER.error("Defined a malformed URL!", e);
		}
		catch (IOException e)
		{
			JosmDebugger.LOGGER.error("No HTTP endpoint found on JOSM!", e);
		}
	}
	
	/**
	 * shows an {@link Entity} in JOSM
	 * 
	 * @since 0.0.1
	 * 		
	 * @param entity the {@link Entity} to show
	 */
	public static void showEntity(Entity entity)
	{
		if (entity instanceof Node)
		{
			JosmDebugger.showNode((Node)entity);
			return;
		}
	}
}