package ager;

import java.util.Random;
import unknown.Tag;

public class StoredItemRule {
	final int type;
	final double p;
	final int becomes;

	public StoredItemRule(int type, double p, int becomes) {
		this.type = type;
		this.p = p;
		this.becomes = becomes;
	}
	
	public void run(int x, int y, int z, MCMap m, Random r, Rule.ApplicationCache ac) {
		Tag t = m.getTileEntity(x, y, z);
		if (t == null) { return; }
		Tag itemL = t.findTagByName("Items");
		if (itemL == null) { return; }
		Tag[] items = (Tag[]) itemL.getValue();
		for (int i = 0; i < items.length; i++) {
			Tag item = items[i];
			if (item.findTagByName("id").getValue().equals(Short.valueOf((short) type)) && r.nextDouble() < p) {
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
