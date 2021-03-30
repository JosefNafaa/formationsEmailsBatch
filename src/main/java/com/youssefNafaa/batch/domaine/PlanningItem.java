package com.youssefNafaa.batch.domaine;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class PlanningItem {
	private String libelleFormation;
	private String descriptifFormation;
	private LocalDate dateDebutSeance;
	private LocalDate dateFinSeance;
	public String getLibelleFormation() {
		return libelleFormation;
	}
	public void setLibelleFormation(String libelleFormation) {
		this.libelleFormation = libelleFormation;
	}
	public String getDescriptifFormation() {
		return descriptifFormation;
	}
	public void setDescriptifFormation(String descriptifFormation) {
		this.descriptifFormation = descriptifFormation;
	}
	public LocalDate getDateDebutSeance() {
		return dateDebutSeance;
	}
	public void setDateDebutSeance(LocalDate dateDebutSeance) {
		this.dateDebutSeance = dateDebutSeance;
	}
	public LocalDate getDateFinSeance() {
		return dateFinSeance;
	}
	public void setDateFinSeance(LocalDate dateFinSeance) {
		this.dateFinSeance = dateFinSeance;
	}
	@Override
	public String toString() {
		return "PlanningItem [libelleFormation=" + libelleFormation + ", descriptifFormation=" + descriptifFormation
				+ ", dateDebutSeance=" + dateDebutSeance + ", dateFinSeance=" + dateFinSeance + "]";
	}

	
	
}
