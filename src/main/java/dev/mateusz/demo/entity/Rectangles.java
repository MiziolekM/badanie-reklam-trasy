package dev.mateusz.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rectangles")
public class Rectangles {

	@Id
	@Column(name = "id_rectangle")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idRectangle;
	
	@Column(name = "start_x")
	private int startX;
	
	@Column(name = "start_y")
	private int startY;
	
	@Column(name = "end_x")
	private int endX;
	
	@Column(name = "end_y")
	private int endY;
	
	public Rectangles() {
		
	}

	public Rectangles(int startX, int startY, int endX, int endY) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}

	public int getIdRectangle() {
		return idRectangle;
	}

	public void setIdRectangle(int idRectangle) {
		this.idRectangle = idRectangle;
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public int getEndX() {
		return endX;
	}

	public void setEndX(int endX) {
		this.endX = endX;
	}

	public int getEndY() {
		return endY;
	}

	public void setEndY(int endY) {
		this.endY = endY;
	}

	@Override
	public String toString() {
		return "Rectangles [idRectangle=" + idRectangle + ", startX=" + startX + ", startY=" + startY + ", endX=" + endX
				+ ", endY=" + endY + "]";
	}
	
}
