import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import ihm.VueScaley;
import metier.DetecteurTonalite;
import metier.Gamme;
import metier.Langue;
import metier.Note;
import metier.Resultat;

public class Controleur implements ActionListener
{
	private VueScaley vue;

	public Controleur(VueScaley vue)
	{
		this.vue = vue;
	}

	public static void main(String[] args)
	{
		VueScaley vue = new VueScaley();
		Controleur controleur = new Controleur(vue);
		vue.ajouterEcouteur(controleur);
		vue.ajouterEcouteurGamme(controleur);
		vue.setVisible(true);
	}

	public void actionPerformed(ActionEvent e)
	{
		// les JComboBox émettent l'action "comboBoxChanged" -> on projette la gamme choisie
		if ("comboBoxChanged".equals(e.getActionCommand()))
		{
			construireGamme();
			return;
		}

		ArrayList<String> notes = this.vue.getNotesCochees();

		if (notes.isEmpty())
		{
			this.vue.afficherResultat(Langue.anglais
				? "Please select at least one note."
				: "Veuillez cocher au moins une note.");
			return;
		}

		DetecteurTonalite detecteur = new DetecteurTonalite();
		ArrayList<Resultat> resultats = detecteur.trouverTonalites(notes);

		String texte = (Langue.anglais ? "Notes: " : "Notes : ") + joindre(notes) + "\n\n";
		texte = texte + (Langue.anglais ? "Best matching scales:\n\n" : "Gammes qui collent le mieux :\n\n");

		int max = Math.min(12, resultats.size());
		for (int i = 0; i < max; i++)
		{
			Resultat r = resultats.get(i);
			texte = texte + String.format("%3d%%  %s%n", r.getPourcentage(), r.getNom());
		}

		this.vue.afficherResultat(texte);
	}

	// Construit la gamme choisie dans les deux menus, l'affiche sur le piano et écrit ses notes.
	private void construireGamme()
	{
		String tonique = this.vue.getNoteChoisie();
		int indice = this.vue.getIndiceGammeChoisie();

		Gamme gamme = new Gamme(tonique, indice);
		ArrayList<String> notes = gamme.getNotes();

		this.vue.selectionnerNotes(notes);
		String entete = Langue.anglais ? "Scale: " : "Gamme : ";
		String lblNotes = Langue.anglais ? "\nNotes: " : "\nNotes : ";
		this.vue.afficherResultat(entete + gamme.getNom() + lblNotes + joindre(notes));
	}

	private String joindre(ArrayList<String> notes)
	{
		String s = "";
		for (int i = 0; i < notes.size(); i++)
		{
			s = s + Note.affiche(notes.get(i));
			if (i < notes.size() - 1)
			{
				s = s + ", ";
			}
		}
		return s;
	}
}
