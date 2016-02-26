package me.chrisswr1.parroute.io;

import java.io.IOException;
import java.util.Map;

import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

/**
 * defines a handler to get OSM data
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public interface DataHandler
{
	/**
	 * requests all {@link Way}s on which {@code node} is a part from
	 * 
	 * @since 0.0.1
	 * 		
	 * @param node the {@link Node} to get the {@link Way}s from
	 * @return the {@link Way}s which contains {@code node}
	 * @throws IOException if the connection couldn't established or the
	 *             response couldn't parsed
	 * 			
	 * @see <a href=
	 *      "http://wiki.openstreetmap.org/wiki/API_v0.6#Ways_for_node:_GET_.2Fapi.2F0.6.2Fnode.2F.23id.2Fways">
	 *      API v0.6 – OpenStreetMap Wiki</a>
	 */
	public Map<? extends Long, ? extends Way> getWaysOfNode(Node node)
	throws IOException;
}