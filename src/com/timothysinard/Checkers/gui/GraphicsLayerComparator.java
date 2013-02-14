package com.timothysinard.Checkers.gui;

import java.util.Comparator;

/**
 * Customer comparator used to determine priority of drawable Z-layers
 * 
 * @param <E>
 */
public class GraphicsLayerComparator<E> implements Comparator<E> {

	@Override
	public int compare(E d1, E d2) {
		if (d1 instanceof Drawable && d1 instanceof Drawable) {
			return ((Drawable) d1).getZ() - ((Drawable) d2).getZ();
		}
		return 0;
	}
}
