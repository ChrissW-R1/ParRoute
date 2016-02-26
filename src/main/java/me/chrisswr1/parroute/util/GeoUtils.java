package me.chrisswr1.parroute.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.coordinate.Position;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

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
	public static final Logger LOGGER = LogManager.getLogger(GeoUtils.class);
	
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
	 * @since 0.1
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
	 * @since 0.1
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
}