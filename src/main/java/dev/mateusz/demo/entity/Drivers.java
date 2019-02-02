package dev.mateusz.demo.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "drivers")
public class Drivers {

	@Id
	@Column(name = "id_driver")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idDriver;

	@Column(name = "name")
	private String name;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "driver", cascade = { CascadeType.ALL })
	private Set<Data> data;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "driver", cascade = { CascadeType.ALL })
	private Set<Fixations> fixation;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "driver", cascade = { CascadeType.ALL })
	private Results result;

	public Drivers() {

	}

	public Drivers(String name) {
		this.name = name;
	}

	public int getIdDriver() {
		return idDriver;
	}

	public void setIdDriver(int idDriver) {
		this.idDriver = idDriver;
	}

	public Results getResult() {
		return result;
	}

	public void setResult(Results result) {
		this.result = result;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Data> getData() {
		return data;
	}

	public void setData(Set<Data> data) {
		this.data = data;
	}

	public Set<Fixations> getFixation() {
		return fixation;
	}

	public void setFixation(Set<Fixations> fixation) {
		this.fixation = fixation;
	}

	@Override
	public String toString() {
		return "Drivers [idDriver=" + idDriver + ", name=" + name + ", data=" + data + ", fixation=" + fixation
				+ ", result=" + result + "]";
	}

	public Drivers(String name, Set<Data> data, Set<Fixations> fixation, Results result) {
		this.name = name;
		this.data = data;
		this.fixation = fixation;
		this.result = result;
	}

}
