package edu.ucdenver.ccp.nlp.core.mention;

import java.util.Collection;

public interface ISlotMention<E> {

	public Collection<E> getSlotValues();

	/**
	 * If this slot mention is storing only a single slot filler, then the single slot filler is returned, otherwise it
	 * throws an exception if there are zero or more than one slot value from which to choose
	 * 
	 * @return
	 */
	public E getSingleSlotValue() throws SingleSlotFillerExpectedException;

	public void setSlotValues(Collection<E> slotValues) throws InvalidInputException;

	public void addSlotValue(E slotValue) throws InvalidInputException;

	public void addSlotValues(Collection<E> slotValues) throws InvalidInputException;

	public void overwriteSlotValues(E slotValue) throws InvalidInputException;

}
