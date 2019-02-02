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
@Table(name = "data")
public class Data implements Comparable<Data> {
	
	@Id
	@Column(name = "id_data")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idData;
	
	@Column(name = "time")
	private long time;
	
	@Column(name = "b_por_x")
	private double bPorX;
	
	@Column(name = "b_por_y")
	private double bPorY;
	
	@Column(name = "frame")
	private String frame;
	//fetch = FetchType.EAGER,
	@ManyToOne(cascade = {CascadeType.DETACH,
			CascadeType.MERGE,
			CascadeType.REFRESH})
	@JoinColumn(name = "id_driver")
	private Drivers driver;
	
	public Data() {
		
	}

	public Data(long time, double bPorX, double bPorY, String frame, Drivers driver) {
		this.time = time;
		this.bPorX = bPorX;
		this.bPorY = bPorY;
		this.frame = frame;
		this.driver = driver;
	}

	public int getIdData() {
		return idData;
	}

	public void setIdData(int idData) {
		this.idData = idData;
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

	public String getFrame() {
		return frame;
	}

	public void setFrame(String frame) {
		this.frame = frame;
	}

	public Drivers getDriver() {
		return driver;
	}

	public void setDriver(Drivers driver) {
		this.driver = driver;
	}

	@Override
	public String toString() {
		return "Data [idData=" + idData + ", time=" + time + ", bPorX=" + bPorX + ", bPorY=" + bPorY + ", frame="
				+ frame + "]";
	}

	@Override
	public int compareTo(Data o) {
		 return new Long(this.time).compareTo(o.time);
	}

}
