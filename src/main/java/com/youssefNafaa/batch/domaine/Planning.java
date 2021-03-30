package com.youssefNafaa.batch.domaine;

import java.util.List;

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
public class Planning {

	private Formateur formateur;
	private List<PlanningItem> seances;
	public Formateur getFormateur() {
		return formateur;
	}
	public void setFormateur(Formateur formateur) {
		this.formateur = formateur;
	}
	public List<PlanningItem> getSeances() {
		return seances;
	}
	public void setSeances(List<PlanningItem> seances) {
		this.seances = seances;
	}
	@Override
	public String toString() {
		return "Planning [formateur=" + formateur + ", seances=" + seances + "]";
	}
	
	
	
}
