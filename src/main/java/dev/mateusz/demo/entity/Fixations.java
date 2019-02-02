package dev.mateusz.demo.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "fixations")
public class Fixations {
	
	@Id
	@Column(name="id_fixation")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idFixation;
	
	@Column(name = "time")
	private long time;
	
	@Column(name = "b_por_x")
	private double bPorX;
	
	@Column(name = "b_por_y")
	private double bPorY;
	
	@Column(name = "timelooking")
	private int timelooking;

	@ManyToOne(cascade = {CascadeType.DETACH,
			CascadeType.MERGE, 
			CascadeType.PERSIST,
			CascadeType.REFRESH})
	@JoinColumn(name = "id_driver")
	private Drivers driver;

	public Fixations() {
		
	}
	
	public Fixations(long time, double bPorX, double bPorY, int timelooking, Drivers driver) {
		this.time = time;
		this.bPorX = bPorX;
		this.bPorY = bPorY;
		this.timelooking = timelooking;
		this.driver = driver;
	}

	public int getIdFixation() {
		return idFixation;
	}

	public void setIdFixation(int idFixation) {
		this.idFixation = idFixation;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public double getbPorX() {
		return bPorX;
	}

	public void setbPorX(double bPorX) {
		this.bPorX = bPorX;
	}

	public double getbPorY() {
		return bPorY;
	}

	public void setbPorY(double bPorY) {
		this.bPorY = bPorY;
	}

	public int getTimelooking() {
		return timelooking;
	}

	public void setTimelooking(int timelooking) {
		this.timelooking = timelooking;
	}

	public Drivers getDriver() {
		return driver;
	}

	public void setDriver(Drivers driver) {
		this.driver = driver;
	}

	@Override
	public String toString() {
		return "Fixations [idFixation=" + idFixation + ", time=" + time + ", bPorX=" + bPorX + ", bPorY=" + bPorY
				+ ", timelooking=" + timelooking + ", driver=" + driver + "]";
	}

}
