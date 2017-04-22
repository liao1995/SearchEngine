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
	private int docID; 	// document id
	private int start; 	// start position of this word in this document
	private int tf; 	// number of this word in this document

	public DocItem(int id, int start) {
		this.docID = id;
		this.start = start;
		this.tf = 0;
	}

	public int getDocID() {
		return docID;
	}

	public void setDocID(int id) {
		this.docID = id;
	}

	public int getStartPos() {
		return start;
	}

	public void setStartPos(int start) {
		this.start = start;
	}

	public int getTF() {
		return tf;
	}

	public void increase() {
		++this.tf;
	}
	
	public void reset() {
		this.tf = 0;
	}
}
