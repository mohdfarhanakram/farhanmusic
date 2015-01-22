package com.pointburst.jsmusic.events;

/**
 * Created by kshitij on 18/7/14.
 */
public class VerticalPagerEvent {
	public static final int DRAGGING_UP = 0;
	public static final int DRAGGING_DOWN = 1;
	private int dragDirection;

	public int getDragDirection() {
		return dragDirection;
	}

	public void setDragDirection(int dragDirection) {
		this.dragDirection = dragDirection;
	}
}
