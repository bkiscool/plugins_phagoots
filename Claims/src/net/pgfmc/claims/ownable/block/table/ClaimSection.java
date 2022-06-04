package net.pgfmc.claims.ownable.block.table;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;

import com.sk89q.worldguard.util.collect.LongHash;

import net.pgfmc.claims.ownable.block.Claim;
import net.pgfmc.claims.ownable.block.table.ClaimsLogic.Range;
import net.pgfmc.core.util.Vector4;

/**
 * Stores claims in a certain region.
 * @see ClaimsTable
 * 
 * Containes additional features that make claims work.
 * (this is where the bounds caclculations are done)
 * 
 * @author CrimsonDart
 * @since 1.4.1
 */
public class ClaimSection {
	
	/* Claim Section size*/
	public static int CSS  = 256;
	
	protected long key;
	protected int w;
	
	private Map<Neighbor, ClaimSection> neighbors = new EnumMap<Neighbor, ClaimSection>(Neighbor.class);
	private Set<Claim> claims = new HashSet<>();
	
	public ClaimSection(long key, int w) {
		this.key = key;
	}
	
	/**
	 * 
	 * @return all claims stored in this Section.
	 */
	public Set<Claim> getAllClaims() {
		return claims;
	}
	
	public static ArrayList<Claim> getClaims(ClaimSection cs, Vector4 v, Range r) {
		
		
		ArrayList<Claim> claims = new ArrayList<>();
		
		if (cs == null) return claims;
		if (cs.claims.size() == 0) return claims;
		
		for (Claim c : cs.claims) {
			if (ClaimsLogic.isInRange(c, v, r)) {
				
				claims.add(c);
			}
		}
		
		return claims;
		
	}
	
	public static Claim getRelevantClaim(ClaimSection cs, Vector4 v, Range r) {
		if (cs == null) return null;
		if (cs.claims.size() == 0) return null;
		
		for (Claim c : cs.claims) {
			if (ClaimsLogic.isInRange(c, v, r)) {
				return c;
			}
		}
		return null;
	}
	
	public Claim getClosestClaim(Vector4 v, Range r) {
		
		Claim ob = getRelevantClaim(this, v, r);
		if (ob != null) {
			return ob;
		}
		
		int claimRange = r.getRange();
		
		int xBound = v.x()%CSS;
		int zBound = v.z()%CSS;
		
		if (xBound < claimRange) {
			
			ob = getRelevantClaim(getNeighbor(Neighbor.LEFT), v, r);
			if (ob != null) return ob;
			
			if (zBound < claimRange) {
				
				ob = getRelevantClaim(getNeighbor(Neighbor.DOWN), v, r);
				if (ob != null) return ob;
				
				else {
					ob = getRelevantClaim(getNeighbor(Neighbor.DOWNLEFT), v, r);
					if (ob != null) return ob;
				}
				
				return null;
			} else if (zBound > CSS - claimRange) {
				
				ob = getRelevantClaim(getNeighbor(Neighbor.UP), v, r);
				if (ob != null) return ob;
				
				else {
					ob = getRelevantClaim(getNeighbor(Neighbor.UPLEFT), v, r);
					if (ob != null) return ob;
				}
			}
			return null;
		} else if (xBound > CSS - claimRange) {
			
			ob = getRelevantClaim(getNeighbor(Neighbor.RIGHT), v, r);
			if (ob != null) return ob;
			
			if (zBound < claimRange) {
				
				ob = getRelevantClaim(getNeighbor(Neighbor.DOWN), v, r);
				if (ob != null) return ob;
				
				else {
					ob = getRelevantClaim(getNeighbor(Neighbor.DOWNRIGHT), v, r);
					if (ob != null) return ob;
				}
				
				return null;
			} else if (zBound > CSS - claimRange) {
				
				ob = getRelevantClaim(getNeighbor(Neighbor.UP), v, r);
				if (ob != null) return ob;
				
				else {
					ob = getRelevantClaim(getNeighbor(Neighbor.UPRIGHT), v, r);
					if (ob != null) return ob;
				}
			}
			return null;
		} else if (zBound < claimRange) { // move left
			
			ob = getRelevantClaim(getNeighbor(Neighbor.DOWN), v, r);
			if (ob != null) return ob;
			
		} else if (zBound > CSS - claimRange) {
			ob = getRelevantClaim(getNeighbor(Neighbor.UP), v, r);
			if (ob != null) return ob;
		}
		return null;
	}
	
	public ArrayList<Claim> getNearbyClaims(Vector4 v, Range r) {
		
		final ArrayList<Claim> ob = getClaims(this, v, r);
		
		
		
		int claimRange = r.getRange();
		int xBound = v.x()%CSS;
		int zBound = v.z()%CSS;
		
		if (xBound < claimRange) {
			
			getClaims(getNeighbor(Neighbor.LEFT), v, r).forEach(x -> ob.add(x));
			
			
			if (zBound < claimRange) {
				
				getClaims(getNeighbor(Neighbor.DOWN), v, r).forEach(x -> ob.add(x));
				getClaims(getNeighbor(Neighbor.DOWNLEFT), v, r).forEach(x -> ob.add(x));
			} else if (zBound > CSS - claimRange) {
				getClaims(getNeighbor(Neighbor.UP), v, r).forEach(x -> ob.add(x));
				getClaims(getNeighbor(Neighbor.UPLEFT), v, r).forEach(x -> ob.add(x));
			}
		} else if (xBound > CSS - claimRange) {
			
			getClaims(getNeighbor(Neighbor.RIGHT), v, r).forEach(x -> ob.add(x));
			if (zBound < claimRange) {
				getClaims(getNeighbor(Neighbor.DOWN), v, r).forEach(x -> ob.add(x));
				getClaims(getNeighbor(Neighbor.DOWNRIGHT), v, r).forEach(x -> ob.add(x));
			} else if (zBound > CSS - claimRange) {
				getClaims(getNeighbor(Neighbor.UP), v, r).forEach(x -> ob.add(x));
				getClaims(getNeighbor(Neighbor.UPRIGHT), v, r).forEach(x -> ob.add(x));
			}
		} else if (zBound < claimRange) { // move left
			getClaims(getNeighbor(Neighbor.DOWN), v, r).forEach(x -> ob.add(x));
		} else if (zBound > CSS - claimRange) {
			getClaims(getNeighbor(Neighbor.UP), v, r).forEach(x -> ob.add(x));
		}
		return ob;
	}
	
	
	
	
	
	
	
	public Claim getOwnable(Vector4 v) {
		if (claims.size() == 0) return null;
		
		for (Claim c : claims) {
			if (c.getLocation().equals(v)) {
				return c;
			}
		}
		return null;
	}
	
	public void put(Claim ob) {
		System.out.println("pushed pt. 2");
		claims.add(ob);
	}
	
	public void remove(Claim ob) {
		claims.remove(ob);
	}
	
	public ClaimSection getNeighbor(Neighbor n) {
		
		Bukkit.getLogger().warning("getting neighbor at " + n.toString());
		
		ClaimSection cs = neighbors.get(n);
		if (cs != null) {
			Bukkit.getLogger().warning("Neighbor referenced.");
			return cs;
		}
		
		cs = ClaimsTable.getSection(LongHash.msw(key) + n.x(), LongHash.lsw(key) + n.z(), w);
		neighbors.put(n, cs);
		return cs;
	}
}
