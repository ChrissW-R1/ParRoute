package me.chrisswr1.parroute.util;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.GeneralDirectPosition;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.Position;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * defines an utility class, which extends the functions of the
 * <a href="http://www.geotools.org/">GeoTools</a> library
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class GeoUtils
{
	/**
	 * the {@link Logger} of this class
	 * 
	 * @since 0.0.1
	 */
	public static final Logger						LOGGER	= LogManager.getLogger(GeoUtils.class);
															
	/**
	 * the default {@link CoordinateReferenceSystem} of the OpenStreetMap
	 * database
	 * 
	 * @since 0.0.1
	 */
	public static final CoordinateReferenceSystem	OSM_CRS;
													
	static
	{
		CoordinateReferenceSystem crs;
		try
		{
			crs = CRS.decode("EPSG:4326");
		}
		catch (FactoryException e)
		{
			crs = DefaultGeographicCRS.WGS84;
		}
		OSM_CRS = crs;
	}
	
	/**
	 * private standard constructor, to prevent initialization
	 * 
	 * @since 0.0.1
	 */
	private GeoUtils()
	{
	}
	
	/**
	 * transform a {@link DirectPosition} into another
	 * {@link CoordinateReferenceSystem}
	 * 
	 * @since 0.0.1
	 * 		
	 * @param srcPos the {@link DirectPosition} to transform
	 * @param destCrs the {@link CoordinateReferenceSystem} to transform the
	 *            <code>srcPos</code> into
	 * @return the transformed {@link DirectPosition} or <code>srcPos</code> if
	 *         it is already referenced in <code>destCrs</code>
	 * @throws TransformException if the {@link DirectPosition} could not
	 *             transformed into <code>destCrs</code>
	 */
	public static DirectPosition transform(DirectPosition srcPos, CoordinateReferenceSystem destCrs)
	throws TransformException
	{
		try
		{
			GeoUtils.LOGGER.debug("Get source CRS.");
			CoordinateReferenceSystem srcCrs = srcPos.getCoordinateReferenceSystem();
			
			if (srcCrs == destCrs || srcPos.getDimension() < 2)
			{
				GeoUtils.LOGGER.debug("Source and target CRS are the same. Return source position.");
				
				return srcPos;
			}
			
			DirectPosition2D pos2D = new DirectPosition2D(srcCrs, srcPos.getOrdinate(0), srcPos.getOrdinate(1));
			
			GeoUtils.LOGGER.debug("Search for transformation model.");
			MathTransform trans = CRS.findMathTransform(srcCrs, destCrs);
			GeoUtils.LOGGER.debug("Transform the source position to the target CRS.");
			DirectPosition destPos = trans.transform(pos2D, null);
			
			GeoUtils.LOGGER.debug("Add ordinates of third and higher dimension.");
			for (int i = 2; i < srcPos.getDimension(); i++)
			{
				destPos.setOrdinate(i, srcPos.getOrdinate(i));
			}
			
			return destPos;
		}
		catch (NullPointerException | FactoryException e)
		{
			String msg = e.getMessage();
			GeoUtils.LOGGER.error(msg, e);
			throw new TransformException(msg, e);
		}
	}
	
	/**
	 * converts a {@link Position} to a JTS {@link Coordinate}
	 * 
	 * @since 0.0.1
	 * 		
	 * @param position the {@link Position} to convert
	 * @return the {@link Coordinate} representation of <code>position</code>
	 */
	public static Coordinate getJtsCoordinate(Position position)
	{
		DirectPosition pos = position.getDirectPosition();
		
		if (pos.getDimension() >= 3)
		{
			GeoUtils.LOGGER.debug("Create new 3D JTS coordinate.");
			
			return new Coordinate(pos.getOrdinate(0), pos.getOrdinate(1), pos.getOrdinate(2));
		}
		else
		{
			GeoUtils.LOGGER.debug("Create new 2D JTS coordinate.");
			
			return new Coordinate(pos.getOrdinate(0), pos.getOrdinate(1));
		}
	}
	
	/**
	 * gives the distance between two {@link Position}s
	 * 
	 * @since 0.0.1
	 * 		
	 * @param pos1 the first {@link Position}
	 * @param pos2 the second {@link Position}
	 * @return the distance between {@code pos1} and {@code pos2}
	 */
	public static double getDistance(Position pos1, Position pos2)
	{
		DirectPosition dPos1 = pos1.getDirectPosition();
		GeodeticCalculator gc = new GeodeticCalculator(dPos1.getCoordinateReferenceSystem());
		
		try
		{
			gc.setStartingPosition(pos1);
			gc.setDestinationPosition(pos2);
		}
		catch (TransformException e)
		{
			DirectPosition dPos2 = pos2.getDirectPosition();
			
			double sum = 0;
			for (int i = 0; i < Math.min(dPos1.getDimension(), dPos2.getDimension()); i++)
			{
				sum += Math.pow(dPos2.getOrdinate(i) - dPos1.getOrdinate(i), 2);
			}
			
			return Math.sqrt(sum);
		}
		
		return gc.getOrthodromicDistance();
	}
	
	/**
	 * creates a {@link Position} from the coordinates of a {@link Node}
	 * 
	 * @since 0.0.1
	 * 
	 * @param node the {@link Node} to convert
	 * @return the generated {@link Position}
	 */
	public static Position toPosition(Node node)
	{
		GeneralDirectPosition pos = new GeneralDirectPosition(GeoUtils.OSM_CRS);
		pos.setOrdinate(0, node.getLatitude());
		pos.setOrdinate(1, node.getLongitude());
		
		try
		{
			Map<String, Object> metaTags = node.getMetaTags();
			if (metaTags.containsKey("ele"))
			{
				long id = node.getId();
				
				GeoUtils.LOGGER.debug("Found ele tag with third ordinate on node " + id + ".");
				
				double ordinate = Double.parseDouble((String)metaTags.get("ele"));
				pos.setOrdinate(2, ordinate);
				
				GeoUtils.LOGGER.debug("Added thrid ordinate " + ordinate + " to node " + id + ".");
			}
		}
		catch (ClassCastException | NumberFormatException e)
		{
			GeoUtils.LOGGER.warn("Couldn't parse the third ordinate from ele tag!", e);
		}
		
		return pos;
	}
}