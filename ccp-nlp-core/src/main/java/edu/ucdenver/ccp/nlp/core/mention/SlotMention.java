package edu.ucdenver.ccp.nlp.core.mention;

import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;

/**
 * The slot mention represents a slot for a given frame. There are two subclasses of SlotMention, ComplexSlotMention
 * which has ClassMentions as slot fillers and PrimitiveSlotMention which has primitives (String, Integer, Boolean,
 * Float, etc.) as slot fillers.
 * 
 * @author Bill Baumgartner
 * 
 */
public abstract class SlotMention<E> extends Mention implements ISlotMention<E> {

	protected static Logger logger = Logger.getLogger(SlotMention.class);

//	protected Collection<E> slotValues;

	public SlotMention(String mentionName,IMentionTraversalTracker traversalTracker, Object... wrappedObjectPlusGlobalVars) {
		super(mentionName, traversalTracker, wrappedObjectPlusGlobalVars);
	}

	public SlotMention(IMentionTraversalTracker traversalTracker, Object... wrappedObjectPlusGlobalVars) {
		super(traversalTracker, wrappedObjectPlusGlobalVars);
	}
	
//	@Override
//	protected void initializeMention() {
//		slotValues = new ArrayList<E>();
//	}

//	/**
//	 * Get the slot values
//	 * 
//	 * @return
//	 */
//	public Collection<E> getSlotValues() {
//		return slotValues;
//	}

	/**
	 * If this slot mention is storing only a single slot filler, then the single slot filler is returned, otherwise it
	 * throws an exception if there are zero or more than one slot value from which to choose
	 * 
	 * @return
	 */
	public E getSingleSlotValue() throws SingleSlotFillerExpectedException {
		if (getSlotValues().size() == 1) {
			return Collections.list(Collections.enumeration(getSlotValues())).get(0);
		}
		throw new SingleSlotFillerExpectedException("Expected single slot filler for slot: " + getMentionName() + ", however this slot has "
				+ getSlotValues().size() + " fillers. [" + getSlotValues().toString()
				+ "] PrimitiveSlotMention.getOnlySlotValue() cannot return an appropriate value");
	}

	/**
	 * Counts the non-empty slot mentions in the input collection
	 * @param slotMentions
	 * @return
	 */
	public static <T extends SlotMention> int nonEmptySlotMentionCount(Collection<T> slotMentions) {
		int nonEmptySlotMentionCount = 0;
		for (T sm : slotMentions) {
			if (sm.getSlotValues().size()>0) {
				nonEmptySlotMentionCount++;
			}
		}
		return nonEmptySlotMentionCount;
	}

//	/**
//	 * Set the slot values
//	 * 
//	 * @param slotValues
//	 */
//	public void setSlotValues(Collection<E> slotValues) {
//		this.slotValues = slotValues;
//		if (hasWrappedMention) {
//			try {
//				setSlotValuesForWrappedMention(slotValues);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	protected abstract void setSlotValuesForWrappedMention(Collection<E> slotValues) throws Exception;
//
//	/**
//	 * Add a slot value to this slot mention
//	 * 
//	 * @param slotValue
//	 */
//	public void addSlotValue(E slotValue) {
//		this.slotValues.add(slotValue);
//		if (hasWrappedMention) {
//			try {
//				addSlotValueToWrappedMention(slotValue);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	protected abstract void addSlotValueToWrappedMention(E slotValue) throws Exception;
//
//	public void addSlotValues(Collection<E> slotValues) {
//		this.slotValues.addAll(slotValues);
//		if (hasWrappedMention) {
//			try {
//				addSlotValuesToWrappedMention(slotValues);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	protected abstract void addSlotValuesToWrappedMention(Collection<E> slotValues) throws Exception;
//
//	public void overwriteSlotValues(E slotValue) {
//		this.slotValues = new ArrayList<E>();
//		this.slotValues.add(slotValue);
//		if (hasWrappedMention) {
//			clearWrappedMentionSlotValues();
//			try {
//				addSlotValueToWrappedMention(slotValue);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	protected abstract void clearWrappedMentionSlotValues();
//
//	public void overwriteSlotValues(Collection<E> slotValues) {
//		this.slotValues = slotValues;
//		if (hasWrappedMention) {
//			clearWrappedMentionSlotValues();
//			try {
//				setSlotValuesForWrappedMention(slotValues);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

}
