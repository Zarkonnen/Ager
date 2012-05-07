package ager;

import java.util.ArrayList;
import java.util.Random;

public class Rule {
	public static class ApplicationCache {
		int type;
		int knownX, knownY, knownZ;
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
		
		for (Outcome o : outcomes) { if (!o.perform(x, y, z, map)) { return false; } }
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
		public boolean perform(int x, int y, int z, MCMap map);
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
	
	public static class Become implements Outcome {
		final int type;

		public Become(int type) {
			this.type = type;
		}
		
		@Override
		public boolean perform(int x, int y, int z, MCMap map) {
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
		public boolean perform(int x, int y, int z, MCMap map) {
			fall(x, y, z, map, map.getBlockType(x, y, z));
			return true;
		}
	}
	
	public static Outcome fall() { return new Fall(); }
	
	public static void fall(int x, int y, int z, MCMap map, int becomes) {
		// how deep can we go?
		int fallY = y;
		while (map.getBlockType(x, --fallY, z) == Types.Air) {}
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
		public boolean perform(int x, int y, int z, MCMap map) {
			//System.out.println("slideDown " + x + "/" + y + "/" + z);
			int bestDx = 0;
			int bestDz = 0;
			int bestDistance = 0;
			for (int dx = -1; dx < 2; dx++) { for (int dz = -1; dz < 2; dz++) {
				if (dx == 0 && dz == 0) { continue; }
				if (dx != 0 && dz != 0) { continue; }
				int dist = -1;
				while (map.getBlockType(x + dx, y - ++dist, z + dz) == Types.Air) {}
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
}
