package ager;

import java.util.Random;
import unknown.Tag;

public class StoredItemRule {
	final int type;
	final double p;
	final int becomes;
	final int takesDamage;
	final int breakDmg;

	public StoredItemRule(int type, double p, int becomes, int takesDamage, int breakDmg) {
		this.type = type;
		this.p = p;
		this.becomes = becomes;
		this.takesDamage = takesDamage;
		this.breakDmg = breakDmg;
	}
	
	public void run(int x, int y, int z, MCMap m, Random r, Rule.ApplicationCache ac) {
		Tag t = m.getTileEntity(x, y, z);
		if (t == null) { return; }
		Tag itemL = t.findTagByName("Items");
		if (itemL == null) { return; }
		Tag[] items = (Tag[]) itemL.getValue();
		for (int i = 0; i < items.length; i++) {
			Tag item = items[i];
			Tag tag = item.findTagByName("tag");
			if (tag != null) {
				Tag ench = tag.findTagByName("ench");
				if (ench != null && ((Tag[]) ench.getValue()).length != 0) {
					//System.out.println("Skipping enchanted item!");
					continue; 
				}
			}
			if (item.findTagByName("id").getValue().equals(Short.valueOf((short) type)) && r.nextDouble() < p) {
				if (takesDamage > 0) {
					int newDmg = ((Short) item.findTagByName("Damage").getValue()) + takesDamage;
					if (newDmg >= breakDmg) {
						itemL.removeTag(i);
						items = (Tag[]) itemL.getValue();
						i--;
					} else {
						item.findTagByName("Damage").setValue(Short.valueOf((short) newDmg));
					}
				} else {
					if (becomes == -1) {
						itemL.removeTag(i);
						items = (Tag[]) itemL.getValue();
						i--;
					} else {
						item.findTagByName("id").setValue(Short.valueOf((short) becomes));
					}
				}
			}
		}
	}
}
