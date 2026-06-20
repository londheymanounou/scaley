package metier;

public class Resultat
{
	private String nom;
	private int pourcentage;
	private int nbNotes;     // taille de la gamme, sert à départager à pourcentage égal

	public Resultat(String nom, int pourcentage, int nbNotes)
	{
		this.nom = nom;
		this.pourcentage = pourcentage;
		this.nbNotes = nbNotes;
	}

	public String getNom()
	{
		return this.nom;
	}

	public int getPourcentage()
	{
		return this.pourcentage;
	}

	public int getNbNotes()
	{
		return this.nbNotes;
	}
}
