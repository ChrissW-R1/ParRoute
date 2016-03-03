package me.chrisswr1.parroute;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlWriter;

import com.topografix.gpx.GpxType;
import com.topografix.gpx.ObjectFactory;
import com.topografix.gpx.RteType;
import com.topografix.gpx.WptType;

import me.chrisswr1.parroute.io.OsmFileFormat;
import me.chrisswr1.parroute.io.OsmFileReceiver;
import me.chrisswr1.parroute.util.GeoUtils;

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
			// OverpassReceiver receiver = new OverpassReceiver();
			OsmFileReceiver receiver = new OsmFileReceiver(new File("src/test/resources/kuhviertel.osm"), OsmFileFormat.XML, CompressionMethod.None);
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
			
			try
			{
				RteType gpxRoute = new RteType();
				for (Node node : route.getPath())
				{
					DirectPosition pos = GeoUtils.toPosition(node).getDirectPosition();
					CoordinateReferenceSystem gpxCrs;
					if (pos.getDimension() >= 3)
					{
						gpxCrs = DefaultGeographicCRS.WGS84_3D;
					}
					else
					{
						gpxCrs = DefaultGeographicCRS.WGS84;
					}
					
					try
					{
						GeoUtils.transform(pos, gpxCrs);
						
						WptType wayPoint = new WptType();
						wayPoint.setLat(new BigDecimal(pos.getOrdinate(0)));
						wayPoint.setLon(new BigDecimal(pos.getOrdinate(1)));
						
						gpxRoute.getRtept().add(wayPoint);
					}
					catch (TransformException e)
					{
						RouteMain.LOGGER.error("Couldn't transform " + pos + " to " + gpxCrs + "!", e);
					}
				}
				
				GpxType gpx = new GpxType();
				gpx.getRte().add(gpxRoute);
				
				ObjectFactory factory = new ObjectFactory();
				JAXBElement<GpxType> gpxElement = factory.createGpx(gpx);
				JAXBContext context = JAXBContext.newInstance(GpxType.class);
				Marshaller marshaller = context.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				
				File gpxFile = new File("target/" + start + "_" + dest + ".gpx");
				gpxFile.delete();
				marshaller.marshal(gpxElement, gpxFile);
			}
			catch (JAXBException e)
			{
				RouteMain.LOGGER.error("Couldn't export route to GPX file!", e);
			}
		}
		catch (IOException e)
		{
			RouteMain.LOGGER.error("Couldn't get an entity from the data handler!", e);
		}
		
		RouteMain.LOGGER.info("Routing calculation finished after " + ((double) (System.currentTimeMillis() - startTime) / 60000) + " minutes.");
	}
}