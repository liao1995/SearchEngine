package edu.hit.aoli.spider;

import java.io.Serializable;

/**
 * An Item of Every LinkedList Invert Index Table hold
 * 
 * @author liao
 *
 */
class DocItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id; // document id
	private int start; // start position of this word in this document
	private int num; // number of this word in this document

	public DocItem(int id, int start) {
		this.id = id;
		this.start = start;
		this.num = 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getNum() {
		return num;
	}

	public void increase() {
		++this.num;
	}
	
	public void reset() {
		this.num = 0;
	}
}
