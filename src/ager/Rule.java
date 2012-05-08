package ager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Rule {
	public static class ApplicationCache {
		int type;
		int knownX, knownY, knownZ;
		IntPt3Stack connectedBlob;
		int cbKnownX, cbKnownY, cbKnownZ;
		int[] cbTypes;
		public int getType(int x, int y, int z, MCMap map) {
			if (x == knownX && y == knownY && z == knownZ) { return type; }
			type = map.getBlockType(x, y, z);
			knownX = x;
			knownY = y;
			knownZ = z;
			return type;
		}
		
		public void clear() {
			knownX = Integer.MIN_VALUE;
			knownY = Integer.MIN_VALUE;
			knownZ = Integer.MIN_VALUE;
			connectedBlob = null;
			cbTypes = null;
		}
		
		public IntPt3Stack getConnectedBlob(int x, int y, int z, int[] types) {
			if (x == cbKnownX && y == cbKnownY && z == cbKnownZ && (types == null || (cbTypes != null && Arrays.equals(cbTypes, types)))) {
				return connectedBlob;
			}
			return null;
		}
	}
	
	public final ArrayList<Condition> conditions = new ArrayList<Condition>();
	public final ArrayList<ProbabilityModifier> modifiers = new ArrayList<ProbabilityModifier>();
	public final ArrayList<Outcome> outcomes = new ArrayList<Outcome>();
	public double baseProbability = 1.0;
	public String description;
	public boolean recurseDownwardsOnSuccess = false;
		
	public Rule desc(String d) { description = d; return this; }
	public Rule p(double p) { baseProbability = p; return this; }
	public Rule when(Condition c) { conditions.add(c); return this; }
	public Rule moreLikelyWhen(ProbabilityModifier m) { modifiers.add(m); return this; }
	public Rule then(Outcome o) { outcomes.add(o); return this; }
	public Rule recurseDownwardsOnSuccess() { recurseDownwardsOnSuccess = true; return this; }
	
	public boolean apply(int x, int y, int z, MCMap map, Random r, ApplicationCache ac) {
		for (Condition c : conditions) {
			if (!c.test(x, y, z, map, ac)) { return false; }
		}
		double p = baseProbability;
		for (ProbabilityModifier m : modifiers) {
			p += m.check.get(x, y, z, map, ac) * m.pPerCheckValue;
		}
		
		if (r.nextDouble() > p) {
			return false;
		}
		
		for (Outcome o : outcomes) { if (!o.perform(x, y, z, map, r, ac)) { return false; } }
		if (recurseDownwardsOnSuccess && y > 0) {
			//System.out.println("rec");
			apply(x, y - 1, z, map, r, ac);
		}
		return true;
	}
	
	public static interface Condition {
		public boolean test(int x, int y, int z, MCMap map, ApplicationCache ac);
		public Check check();
	}
	public static class MinimumCondition implements Condition {
		public final Check check;
		public final int minValue;

		public MinimumCondition(Check check, int minValue) {
			this.check = check;
			this.minValue = minValue;
		}

		@Override
		public boolean test(int x, int y, int z, MCMap map, ApplicationCache ac) {
			return check.get(x, y, z, map, ac) >= minValue;
		}
		
		@Override
		public Check check() { return check; }
	}
	public static class MaximumCondition implements Condition {
		public final Check check;
		public final int maxValue;

		public MaximumCondition(Check check, int maxValue) {
			this.check = check;
			this.maxValue = maxValue;
		}

		@Override
		public boolean test(int x, int y, int z, MCMap map, ApplicationCache ac) {
			return check.get(x, y, z, map, ac) <= maxValue;
		}
		
		@Override
		public Check check() { return check; }
	}
	public static class ProbabilityModifier {
		public final Check check;
		public double pPerCheckValue;

		public ProbabilityModifier(Check check, double pPerCheckValue) {
			this.check = check;
			this.pPerCheckValue = pPerCheckValue;
		}
	}
	public static interface Check {
		public int get(int x, int y, int z, MCMap map, ApplicationCache ac);
	}
	public static interface Outcome {
		public boolean perform(int x, int y, int z, MCMap map, Random r, ApplicationCache ac);
	}
	
	public static class Is implements Check {
		final int type;

		public Is(int type) {
			this.type = type;
		}
		
		@Override
		public int get(int x, int y, int z, MCMap map, ApplicationCache ac) {
			return ac.getType(x, y, z, map) == type ? 1 : 0;
		}
	}
	
	public static Condition is(int type) { return new MinimumCondition(new Is(type), 1); }
	
	public static class HasData implements Check {
		final int data;

		public HasData(int data) {
			this.data = data;
		}
		
		@Override
		public int get(int x, int y, int z, MCMap map, ApplicationCache ac) {
			return map.getData(x, y, z) == data ? 1 : 0;
		}
	}
	
	public static Condition hasData(int data) { return new MinimumCondition(new HasData(data), 1); }
		
	public static class SkyExposed implements Check {
		@Override
		public int get(int x, int y, int z, MCMap map, ApplicationCache ac) {
			return map.getSkyLight(x, y, z) == 15 ? 1 : 0;
		}
	}
	
	public static Condition skyExposed() { return new MinimumCondition(new SkyExposed(), 1); }
	public static ProbabilityModifier skyExposed(double p) { return new ProbabilityModifier(new SkyExposed(), p); }
	
	public static class Below implements Check {
		final int type;

		public Below(int type) {
			this.type = type;
		}
		
		@Override
		public int get(int x, int y, int z, MCMap map, ApplicationCache ac) {
			return map.getBlockType(x, y + 1, z) == type ? 1 : 0;
		}
	}
	
	public static Condition below(int type) { return new MinimumCondition(new Below(type), 1); }
	public static ProbabilityModifier below(double p, int type) { return new ProbabilityModifier(new Below(type), p); }
	
	public static class Above implements Check {
		final int type;

		public Above(int type) {
			this.type = type;
		}
		
		@Override
		public int get(int x, int y, int z, MCMap map, ApplicationCache ac) {
			return map.getBlockType(x, y - 1, z) == type ? 1 : 0;
		}
	}
	
	public static Condition above(int type) { return new MinimumCondition(new Above(type), 1); }
	public static ProbabilityModifier above(double p, int type) { return new ProbabilityModifier(new Above(type), p); }
	
	public static class AdjacentTo implements Check {
		final int type;

		public AdjacentTo(int type) {
			this.type = type;
		}
		
		@Override
		public int get(int x, int y, int z, MCMap map, ApplicationCache ac) {
			return
					(map.getBlockType(x - 1, y, z) == type ? 1 : 0) +
					(map.getBlockType(x + 1, y, z) == type ? 1 : 0) +
					(map.getBlockType(x, y - 1, z) == type ? 1 : 0) +
					(map.getBlockType(x, y + 1, z) == type ? 1 : 0) +
					(map.getBlockType(x, y, z - 1) == type ? 1 : 0) +
					(map.getBlockType(x, y, z + 1) == type ? 1 : 0);
		}
	}
	
	public static Condition nextTo(int type) { return new MinimumCondition(new AdjacentTo(type), 1); }
	public static Condition notNextTo(int type) { return new MaximumCondition(new AdjacentTo(type), 0); }
	public static Condition nextToAtLeast(int n, int type) { return new MinimumCondition(new AdjacentTo(type), n); }
	public static Condition nextToNoMoreThan(int n, int type) { return new MaximumCondition(new AdjacentTo(type), n); }
	
	public static class Touching implements Check {
		final int type;

		public Touching(int type) {
			this.type = type;
		}
		
		@Override
		public int get(int x, int y, int z, MCMap map, ApplicationCache ac) {
			int sum = 0;
			for (int dx = -1; dx < 2; dx++) { for (int dy = -1; dy < 2; dy++) { for (int dz = -1; dz < 2; dz++) {
				if (dx != 0 && dy != 0 && dz != 0) { continue; }
				if (map.getBlockType(x + dx, y + dy, z + dz) == type) { sum++; }
			}}}
			return sum;
		}
	}
	
	public static Condition touching(int type) { return new MinimumCondition(new Touching(type), 1); }
	public static Condition notTouching(int type) { return new MaximumCondition(new Touching(type), 0); }
	public static Condition touchingAtLeast(int n, int type) { return new MinimumCondition(new Touching(type), n); }
	public static Condition touchingNoMoreThan(int n, int type) { return new MaximumCondition(new Touching(type), n); }
	
	public static ProbabilityModifier touching(double p, int type) { return new ProbabilityModifier(new Touching(type), p); }
	
	public static class Vicinity implements Check {
		final int type;
		final int dist;

		public Vicinity(int type, int dist) {
			this.type = type;
			this.dist = dist;
		}
		
		@Override
		public int get(int x, int y, int z, MCMap map, ApplicationCache ac) {
			int sum = 0;
			for (int dx = -dist; dx < dist + 1; dx++) { for (int dy = -dist; dy < dist + 1; dy++) { for (int dz = -dist; dz < dist + 1; dz++) {
				if (dx != 0 && dy != 0 && dz != 0) { continue; }
				if (map.getBlockType(x + dx, y + dy, z + dz) == type) { sum++; }
			}}}
			return sum;
		}
	}
	
	public static Condition withinDistOf(int dist, int type) { return new MinimumCondition(new Vicinity(type, dist), 1); }
	public static Condition notWithinDistOf(int dist, int type) { return new MaximumCondition(new Vicinity(type, dist), 0); }
	public static Condition atLeastNWithinVicinity(int n, int dist, int type) { return new MinimumCondition(new Vicinity(type, dist), n); }
	public static Condition noMoreThanNWithinVicinity(int n, int dist, int type) { return new MaximumCondition(new Vicinity(type, dist), n); }
	
	public static ProbabilityModifier inVicinityOf(double p, int dist, int type) { return new ProbabilityModifier(new Vicinity(type, dist), p); }
	
	public static class SupportFrom implements Check {
		int dx, dy, dz;

		public SupportFrom(int dx, int dy, int dz) {
			this.dx = dx;
			this.dy = dy;
			this.dz = dz;
		}
		
		@Override
		public int get(int x, int y, int z, MCMap map, ApplicationCache ac) {
			return Rules.providesSupport[map.getBlockType(x + dx, y + dy, z + dz) + 1] ? 1 : 0;
		}
	}
	
	public static Condition noSupportFrom(int dx, int dy, int dz) { return new MaximumCondition(new SupportFrom(dx, dy, dz), 0); }
	
	public static class Become implements Outcome {
		final int type;

		public Become(int type) {
			this.type = type;
		}
		
		@Override
		public boolean perform(int x, int y, int z, MCMap map, Random r, ApplicationCache ac) {
			map.setBlockType((byte) type, x, y, z);
			if (type == Types.Air) {
				map.setSkyLight((byte) map.getSkyLight(x, y + 1, z), x, y, z);
				map.healBlockLight(x, y, z);
				map.healSkyLight(x, y, z); // ?
			}
			return true;
		}
	}
	
	public static Outcome become(int type) { return new Become(type); }
	
	public static class Fall implements Outcome {
		@Override
		public boolean perform(int x, int y, int z, MCMap map, Random r, ApplicationCache ac) {
			fall(x, y, z, map, map.getBlockType(x, y, z));
			return true;
		}
	}
	
	public static Outcome fall() { return new Fall(); }
	
	public static void fall(int x, int y, int z, MCMap map, int becomes) {
		// how deep can we go?
		int fallY = y;
		while (Rules.fallThru[map.getBlockType(x, --fallY, z) + 1]) {}
		fallY++;
		if (fallY < y) {
			if (becomes != Types.Air) { map.setBlockType((byte) becomes, x, fallY, z); }
			map.setBlockType((byte) Types.Air, x, y, z);
			map.healBlockLight(x, y, z);
			if (becomes == Types.Air) {
				map.setSkyLight((byte) map.getSkyLight(x, y + 1, z), x, fallY, z);
			} else {
				map.setSkyLight((byte) map.getSkyLight(x, y + 1, z), x, fallY + 1, z);
			}
		}
	}
	
	public static class SlideDown implements Outcome {
		final int minDistance;

		public SlideDown(int minDistance) {
			this.minDistance = minDistance;
		}
		
		@Override
		public boolean perform(int x, int y, int z, MCMap map, Random r, ApplicationCache ac) {
			//System.out.println("slideDown " + x + "/" + y + "/" + z);
			int bestDx = 0;
			int bestDz = 0;
			int bestDistance = 0;
			for (int dx = -1; dx < 2; dx++) { for (int dz = -1; dz < 2; dz++) {
				if (dx == 0 && dz == 0) { continue; }
				if (dx != 0 && dz != 0) { continue; }
				int dist = -1;
				while (Rules.fallThru[map.getBlockType(x + dx, y - ++dist, z + dz) + 1]) {}
				dist--;
				if (dist >= minDistance && dist > bestDistance) {
					bestDx = dx;
					bestDz = dz;
					bestDistance = dist;
				}
			}}
			//System.out.println(bestDistance + " " + bestDx + " " + bestDz);
			if (bestDistance > 0) {
				map.setBlockType((byte) map.getBlockType(x, y, z), x + bestDx, y - bestDistance, z + bestDz);
				map.setBlockType((byte) Types.Air, x, y, z);
				map.healBlockLight(x, y, z);
				// Light?
				return true;
			}
			return false;
		}
	}
	
	public static Outcome slideDown(int minDistance) { return new SlideDown(minDistance); }
		
	public static class IsConnectedBlobOf implements Check {
		final int[] types;

		public IsConnectedBlobOf(int[] types) {
			this.types = types;
			Arrays.sort(types);
		}
		
		@Override
		public int get(int x, int y, int z, MCMap map, ApplicationCache ac) {
			IntPt3Stack b = ac.getConnectedBlob(x, y, z, types);
			if (b != null) { return 1; }
			if (Arrays.binarySearch(types, ac.getType(x, y, z, map)) < 0) { return 0; }
			if (map.getPartOfBlob(x, y, z)) { return 0; } // Already evaluated this blob.
			/*System.out.println("Blob found.");
			System.out.println("Blob initial block is " + ac.getType(x, y, z, map));*/
			IntPt3Stack q = new IntPt3Stack(10);
			IntPt3Stack blob = new IntPt3Stack(10);
			q.push(x, y, z);
			blob.push(x, y, z);
			map.setPartOfBlob(x, y, z, true);
			while (!q.isEmpty()) {
				q.pop();
				for (int dy = -1; dy < 2; dy++) {
					int ny = q.y + dy;
					if (ny < 0 || ny >= 256) { continue; }
					for (int dx = -1; dx < 2; dx++) {
						int nx = q.x + dx;
						for (int dz = -1; dz < 2; dz++) {
							if (dy != 0 && dx != 0 && dz != 0) { continue; }
							if (dy == 0 && dx == 0 && dz == 0) { continue; }
							int nz = q.z + dz;
							if (Arrays.binarySearch(types, map.getBlockType(nx, ny, nz)) >= 0 && !map.getPartOfBlob(nx, ny, nz)) {
								//System.out.println(map.getBlockType(nx, ny, nz));
								blob.push(nx, ny, nz);
								q.push(nx, ny, nz);
								map.setPartOfBlob(nx, ny, nz, true);
							}
						}
					}
				}
			}
			ac.connectedBlob = blob;
			ac.cbTypes = types;
			ac.cbKnownX = x;
			ac.cbKnownY = y;
			ac.cbKnownZ = z;
			/*System.out.println(blob.size());
			for (int i = 0; i < blob.size(); i++) {
				blob.get(i);
				System.out.print(map.getBlockType(blob.x, blob.y, blob.z) + " ");
			}
			System.out.println();*/
			return 1;
		}
	}
	
	public static Condition isConnectedBlobOf(int... types) { return new MinimumCondition(new IsConnectedBlobOf(types), 1); }
	
	public static class ConnectedBlobTypeCount implements Check {
		final int type;

		public ConnectedBlobTypeCount(int type) {
			this.type = type;
		}
		
		@Override
		public int get(int x, int y, int z, MCMap map, ApplicationCache ac) {
			IntPt3Stack blob = ac.getConnectedBlob(x, y, z, null);
			if (blob == null) { return 0; }
			int count = 0;
			for (int i = 0; i < blob.size(); i++) {
				blob.get(i);
				if (map.getBlockType(blob.x, blob.y, blob.z) == type) {
					count++;
				}
			}
			return count;
		}
	}
	
	public static Condition connectedBlobContains(int type) { return new MinimumCondition(new ConnectedBlobTypeCount(type), 1); }
	public static Condition connectedBlobDoesNotContain(int type) { return new MaximumCondition(new ConnectedBlobTypeCount(type), 0); }
	
	public static class ApplyIndividually implements Outcome {
		final Rule rule;

		public ApplyIndividually(Rule rule) {
			this.rule = rule;
		}
		
		@Override
		public boolean perform(int x, int y, int z, MCMap map, Random r, ApplicationCache ac) {
			IntPt3Stack blob = ac.getConnectedBlob(x, y, z, null);
			if (blob == null) { return false; }
			for (int i = 0; i < blob.size(); i++) {
				blob.get(i);
				rule.apply(blob.x, blob.y, blob.z, map, r, ac);
			}
			
			return true;
		}
	}
	
	public static Outcome applyIndividually(Rule r) { return new ApplyIndividually(r); }
	
	public static class ApplyCollectively implements Outcome {
		final Rule rule;

		public ApplyCollectively(Rule rule) {
			this.rule = rule;
		}
		
		@Override
		public boolean perform(int x, int y, int z, MCMap map, Random r, ApplicationCache ac) {
			IntPt3Stack blob = ac.getConnectedBlob(x, y, z, null);
			if (blob == null) { return false; }
			if (r.nextDouble() > rule.baseProbability) { return false; }
			for (int i = 0; i < blob.size(); i++) {
				blob.get(i);
				for (Outcome o : rule.outcomes) {
					o.perform(blob.x, blob.y, blob.z, map, r, ac);
				}
			}
			
			return true;
		}
	}
	
	public static Outcome applyCollectively(Rule r) { return new ApplyCollectively(r); }
	
	public static class ApplyNearby implements Outcome {
		final Rule rule;
		final int dist;

		public ApplyNearby(Rule rule, int dist) {
			this.rule = rule;
			this.dist = dist;
		}
		
		@Override
		public boolean perform(int x, int y, int z, MCMap map, Random r, ApplicationCache ac) {
			for (int dx = -dist; dx < dist + 1; dx++) { for (int dy = -dist; dy < dist + 1; dy++) { for (int dz = -dist; dz < dist + 1; dz++) {
				rule.apply(x + dx, y + dy, z + dz, map, r, ac);
			}}}
			
			return true;
		}
	}
	
	public static Outcome applyNearby(int dist, Rule r) { return new ApplyNearby(r, dist); }
}
