package dev.mateusz.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "results")
public class Results {

	@Id
	@Column(name = "id_result")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idResult;
	
	@Column(name = "full_time")
	private double fullTime;

	@Column(name = "time_look_for_advertising")
	private double timeLookForAdvertising;
	
	@Column(name = "time_from_total_in_percent")
	private double timeFromTotalInPercent;
	
	@Column(name = "avg_time_for_one_advertising")
	private double avgTimeForOneAdvertising;

	@OneToOne
	@JoinColumn(name = "id_driver")
	private Drivers driver;

	public Results() {
		
	}

	public Results(double fullTime, double timeLookForAdvertising, double timeFromTotalInPercent,
			double avgTimeForOneAdvertising, Drivers driver) {
		this.fullTime = fullTime;
		this.timeLookForAdvertising = timeLookForAdvertising;
		this.timeFromTotalInPercent = timeFromTotalInPercent;
		this.avgTimeForOneAdvertising = avgTimeForOneAdvertising;
		this.driver = driver;
	}

	public int getIdResult() {
		return idResult;
	}

	public void setIdResult(int idResult) {
		this.idResult = idResult;
	}

	public double getFullTime() {
		return fullTime;
	}

	public void setFullTime(double fullTime) {
		this.fullTime = fullTime;
	}

	public double getTimeLookForAdvertising() {
		return timeLookForAdvertising;
	}

	public void setTimeLookForAdvertising(double timeLookForAdvertising) {
		this.timeLookForAdvertising = timeLookForAdvertising;
	}

	public double getTimeFromTotalInPercent() {
		return timeFromTotalInPercent;
	}

	public void setTimeFromTotalInPercent(double timeFromTotalInPercent) {
		this.timeFromTotalInPercent = timeFromTotalInPercent;
	}

	public double getAvgTimeForOneAdvertising() {
		return avgTimeForOneAdvertising;
	}

	public void setAvgTimeForOneAdvertising(double avgTimeForOneAdvertising) {
		this.avgTimeForOneAdvertising = avgTimeForOneAdvertising;
	}

	public Drivers getDriver() {
		return driver;
	}

	public void setDriver(Drivers driver) {
		this.driver = driver;
	}

	@Override
	public String toString() {
		return "Results [idResult=" + idResult + ", fullTime=" + fullTime + ", timeLookForAdvertising="
				+ timeLookForAdvertising + ", timeFromTotalInPercent=" + timeFromTotalInPercent
				+ ", avgTimeForOneAdvertising=" + avgTimeForOneAdvertising + "]";
	}

}
