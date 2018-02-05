package parser;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;

public class Event
{

	static final String DEBUT = "DEBUT";
	static final String FIN = "FIN";

	public Event(Date date, String slave, String composant, String type)
	{
		super();
		this.dateDebut = date;
		this.slave = slave;
		this.composant = composant;
		this.type = type;

	}

	public Event(Date dateDebut, String slave, String composant, String type, String pid)
	{
		super();
		this.dateDebut = dateDebut;
		this.slave = slave;
		this.composant = composant;
		this.type = type;
		this.pid = Integer.parseInt(pid);

	}

	public Date dateDebut;
	public Date dateFin;
	public String slave;
	/**
	 * composant effectuant un log
	 */
	public String composant;
	/**
	 * type d'évènement log<br>
	 * au choix parmi :
	 * <ul>
	 * <li>DEBUT
	 * <li>FIN
	 * </ul>
	 */
	public String type;
	public Duration duree;
	public int pid;

	// ----------------------------------------------------------------

	enum types
	{
		DEBUT, FIN
	}

	public void setDateDebut(Date date)
	{
		this.dateDebut = date;
	}

	public String setDateFin(Date dateFin)
	{
		this.dateFin = dateFin;
		return this.calcDuree();
	}

	/**
	 * calcule la durée de l'évènement<br>
	 * entre dateDebut et dateFin<br>
	 * ne fonctionne que si dateDebut et dateFin sont renseignés
	 */
	public String calcDuree()
	{
		if (dateFin != null && dateDebut != null)
		{
			duree = new org.joda.time.Duration(dateFin.getTime() - dateDebut.getTime());
		}
		return this.toString();
	}

	public String toString()
	{

		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.FRENCH);
		String ret = "";

		ret += df.format(dateDebut) + " - slave : " + slave + " - composant : " + composant;
		if (this.type.equals(DEBUT))
		{
			PeriodFormatter formatter = PeriodFormat.getDefault();

			// print using the French locale
			String periodStr = formatter.withLocale(Locale.FRENCH).print(duree.toPeriod());
			ret += " - durée : " + periodStr;
		}

		return ret;
	}
}
