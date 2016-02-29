package me.chrisswr1.parroute.entities;

import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

import me.chrisswr1.parroute.DataHandler;
import me.chrisswr1.parroute.util.StringUtils;

/**
 * a {@link Relation} from type {@link RelationType#RESTRICTION}
 * 
 * @version 0.0.1
 * @author ChrissW-R1
 * @since 0.0.1
 */
public class RestrictionRelation
{
	/**
	 * the super {@link Relation}
	 * 
	 * @since 0.0.1
	 */
	private final Relation rel;
	
	/**
	 * constructor, with given super {@link Relation}
	 * 
	 * @since 0.0.1
	 * 
	 * @param rel the super {@link Relation}
	 * @throws IllegalArgumentException if the type of the given
	 *             {@link Relation} is not {@link RelationType#RESTRICTION}
	 */
	public RestrictionRelation(Relation rel)
	throws IllegalArgumentException
	{
		if (DataHandler.getRelType(rel) != RelationType.RESTRICTION)
		{
			throw new IllegalArgumentException(rel + " is not from type restriction!");
		}
		
		this.rel = rel;
	}
	
	/**
	 * gives the super {@link Relation}
	 * 
	 * @since 0.0.1
	 * 		
	 * @return the super {@link Relation}
	 */
	public Relation getRel()
	{
		return this.rel;
	}
	
	/**
	 * gives the {@link RelationMember} with the &quot;from&quot; role
	 * 
	 * @since 0.0.1
	 * 
	 * @return the {@link RelationMember}, which have the role &quot;from&quot;
	 */
	public RelationMember getFrom()
	{
		RelationMember res = null;
		String fromKey = "from";
		
		for (RelationMember member : this.getRel().getMembers())
		{
			String role = member.getMemberRole();
			
			if (role.equals(fromKey))
			{
				return member;
			}
			
			if (role.equalsIgnoreCase(fromKey))
			{
				res = member;
				continue;
			}
			
			if (res == null && StringUtils.containsIgnoreCase(role, fromKey))
			{
				res = member;
			}
		}
		
		return res;
	}
	
	/**
	 * gives the {@link RelationMember} with the &quot;via&quot; role
	 * 
	 * @since 0.0.1
	 * 
	 * @return the {@link RelationMember}, which have the role &quot;via&quot;
	 */
	public RelationMember getVia()
	{
		RelationMember res = null;
		String viaKey = "via";
		
		for (RelationMember member : this.getRel().getMembers())
		{
			String role = member.getMemberRole();
			
			if (role.equals(viaKey))
			{
				return member;
			}
			
			if (role.equalsIgnoreCase(viaKey))
			{
				res = member;
				continue;
			}
			
			if (res == null && StringUtils.containsIgnoreCase(role, viaKey))
			{
				res = member;
			}
		}
		
		return res;
	}
	
	/**
	 * gives the {@link RelationMember} with the &quot;to&quot; role
	 * 
	 * @since 0.0.1
	 * 
	 * @return the {@link RelationMember}, which have the role &quot;to&quot;
	 */
	public RelationMember getTo()
	{
		RelationMember res = null;
		String toKey = "to";
		
		for (RelationMember member : this.getRel().getMembers())
		{
			String role = member.getMemberRole();
			
			if (role.equals(toKey))
			{
				return member;
			}
			
			if (role.equalsIgnoreCase(toKey))
			{
				res = member;
				continue;
			}
			
			if (res == null && StringUtils.containsIgnoreCase(role, toKey))
			{
				res = member;
			}
		}
		
		return res;
	}
	
	/**
	 * checks if the {@link Relation} is a commandment (restriction{@link Tag}
	 * starts with &quot;only&quot;)
	 * 
	 * @since 0.0.1
	 * 
	 * @return <code>true</code> if the super {@link Relation} is a commandment,
	 *         <code>false</code> otherwise
	 */
	public boolean isCommandment()
	{
		return DataHandler.getTagValue(this.getRel(), "restriction").toLowerCase().startsWith("only");
	}
}