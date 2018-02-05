package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.joda.time.Duration;

public class LogParser
{

	private final static String FS_DATA = ";";
	private final static String FS_RAW = " ";
	private static final String LINE_SEP = "-----------------------------------------";
	private static SimpleLogger log;

	public static void main(String[] args)
	{

		log.INFO("DEBUT");
		if (args.length == 1)
		{
			File logFile = new File(args[0]);
			if (logFile.isFile())
			{
				try
				{
					logToData2(logFile);
					// parse(logFile);
				} catch (IOException e)
				{
					log.ERR("unable to parse file " + args[0]);
					e.printStackTrace();
				} catch (ParseException e)
				{
					log.ERR("error during line parsing (date)");
					e.printStackTrace();
				}
			}
		} else
		{
			System.err.println("Usage : LogParser logFile");
		}
		log.INFO("FIN");
	}

	private static void parse(File logFile) throws IOException, ParseException
	{

		HashMap<String, HashMap> slavesMap = new HashMap<String, HashMap>();
		log.INFO("parse fichier " + logFile.getName());
		FileInputStream fis = new FileInputStream(logFile);

		BufferedReader inputReader = new BufferedReader(new InputStreamReader(fis));
		String line;
		String[] lt;
		GregorianCalendar gc = new GregorianCalendar();
		int len = 0;
		// DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,
		// Locale.FRENCH);
		// SimpleDateFormat sdf = sdf.getDateInstance(DateFormat.SHORT,
		// Locale.FRENCH);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.FRENCH);
		Date dateDebut = new Date();
		Date dateFin = new Date();

		Event prevEvent = null;
		Event thisEvent = null;

		/*
		 * lecture du fichier ligne par ligne
		 */
		while ((line = inputReader.readLine()) != null)
		{
			/*
			 * d�coupage des lignes du fichier par s�parateur (Tab.)
			 */
			lt = line.split(FS_DATA);

			len = lt.length;

			if (len != 4)
			{
				log.ERR("ligne non conforme trouv�e (skipping) : " + line);
			} else
			{

				// Date date = sdf.parse(lt[0]);
				thisEvent = new Event(sdf.parse(lt[0]), lt[1], lt[2], lt[3]);
				if (!slavesMap.containsKey(lt[0]))
				{
					slavesMap.put(lt[0], new HashMap<String, HashMap>());
				}
				if (prevEvent != null && thisEvent.type == Event.FIN && thisEvent.slave == prevEvent.slave)
				{
				}

				if (Event.DEBUT.equals(thisEvent.type))
				{
					dateDebut = thisEvent.dateDebut;
				} else if (Event.FIN.equals(thisEvent.type))
				{
					dateFin = thisEvent.dateDebut;
					Duration duration = new org.joda.time.Duration(dateFin.getTime() - dateDebut.getTime());
					prevEvent.duree = duration;

					// System.out.println("duration = " + periodStr);
					System.out.println(prevEvent.toString());

				}

				// System.out.println(df.format(thisEvent.date));

			}

			prevEvent = thisEvent;

		}
	}

	// --------------------------------------------------------------------------
	/**
	 * Parse un fichier contenant des lignes de <i>FE_LOG</i>.<br>
	 * Extrait les donn�es pour calculer la dur�e des traitements
	 * 
	 * @param logFile
	 *            : fichier <i>FE_LOG</i> � parser
	 * @return
	 * @throws IOException
	 */
	public static HashMap<String, HashMap> logToData(File logFile) throws IOException, ParseException
	{
		log.INFO("parse fichier " + logFile.getName());
		FileInputStream fis = new FileInputStream(logFile);

		BufferedReader inputReader = new BufferedReader(new InputStreamReader(fis));
		String line;
		String[] lt;
		GregorianCalendar gc = new GregorianCalendar();
		int len = 0;
		Event thisEvent = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.FRENCH);

		// map conteant les infos de m�m pid
		// HashMap<String, HashMap<String, HashMap<Integer, HashMap<Date,
		// Event>>>> slaveMap = new HashMap<String, HashMap<String,
		// HashMap<Integer, HashMap<Date, Event>>>>();
		HashMap<String, HashMap> slaveMap = new HashMap<String, HashMap>();

		/*
		 * lecture du fichier ligne par ligne
		 */
		while ((line = inputReader.readLine()) != null)
		{
			/*
			 * d�coupage des lignes du fichier par s�parateur (espace.)
			 */
			lt = line.split(FS_RAW);
			len = lt.length;

			if (len != 9 && len != 10)
			{
				log.ERR("ligne non conforme trouv�e (skipping) : " + line);
			} else
			{
				// parsing des lignes => r�cup�ration des infos n�cessaires
				thisEvent = new Event(sdf.parse(lt[0] + FS_RAW + lt[1]), lt[4], lt[6], lt[8], lt[5]);

				if (!slaveMap.containsKey(thisEvent.slave))
				{

					// ce slave n'a pas encore �t� trouv� => toute la ligne est
					// �
					// ajouter
					// hashmap contenant les infos par composant
					HashMap<String, HashMap> composantMap = new HashMap<String, HashMap>();

					// hashmap contenant les infos par pid
					HashMap<Integer, HashMap> pidMap = new HashMap<Integer, HashMap>();

					// hashmap contenatles infos par date
					HashMap<Date, Event> dateMap = new HashMap<Date, Event>();

					// on ajoute les infos par date
					dateMap.put(thisEvent.dateDebut, thisEvent);

					// on ajoute les infos par pid
					pidMap.put((Integer) thisEvent.pid, dateMap);

					// on ajoute les infos par composant
					composantMap.put(thisEvent.composant, pidMap);

					// on ajoute les infos par slave
					slaveMap.put(thisEvent.slave, composantMap);

				} else
				{
					/**
					 * ce slave a d�j� �t� trouv� <br>
					 * il faut v�rifier que la ligne n'est pas un doublon<br>
					 * cl� de v�rif : <br>
					 * <ul>
					 * <li>- slave
					 * <li>- composant
					 * <li>- pid
					 * <li>- date
					 * <li>- type
					 * </ul>
					 */

					if (slaveMap.get(thisEvent.slave).containsKey(thisEvent.composant))
					{
						// composant d�j� trouv�e pour ce slave => cl� de tri
						// suivante � v�rifier :
						// pid
						if (((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) slaveMap.get(thisEvent.slave)).get(thisEvent.composant)).containsKey(thisEvent.pid))
						{
							/**
							 * pid d�j� trouv� pour ce slave et pour ce
							 * composant<br>
							 * il faut v�rifier que la ligne n'est pas un
							 * doublon<br>
							 * cl� suivante : date
							 */
							if (((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) slaveMap.get(thisEvent.slave)).get(thisEvent.composant)).get(thisEvent.pid)).containsKey(thisEvent.dateDebut))
							{

								/*
								 * date d�j� trouv�e v�rif suivante = type
								 */

								if (!((Event) ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) slaveMap.get(thisEvent.slave)).get(thisEvent.composant)).get(thisEvent.pid)).get(thisEvent.dateDebut)).type.equals(thisEvent.type))
								{
									// pas le m�me type => date debut = datefin
									// ((Event) ((HashMap<Integer, Event>)
									// ((HashMap<String, HashMap>)
									// slaveMap.get(thisEvent.slave)).get(thisEvent.composant)).get(thisEvent.pid)).setDateFin(thisEvent.dateDebut);
									log.INFO(((Event) ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) slaveMap.get(thisEvent.slave)).get(thisEvent.composant)).get(thisEvent.pid)).get(thisEvent.dateDebut)).setDateFin(thisEvent.dateDebut));

								} else
								{
									// si m�me type, on ne fait rien => c'est un
									// doublon }
									log.INFO("doubon d�tect� : " + line);
								}
							} else
							{
								/*
								 * pas la m�me date
								 */
								/**
								 * FIXME : aggr�ger les donn�es pour calculer la
								 * dur�e
								 */

								if (((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) slaveMap.get(thisEvent.slave)).get(thisEvent.composant)).get(thisEvent.pid)).size() == 1)
								{
									/**
									 * cas normal : 1 seule autre date pour ce
									 * slave, ce composant et ce pid<br>
									 * il faut aggr�ger les deux lignes
									 */

									// Date dateStockee = ((Event)
									// ((HashMap<Date, Event>)
									// ((HashMap<Integer, HashMap>)
									// ((HashMap<String, HashMap>)
									// slaveMap.get(thisEvent.slave)).get(thisEvent.composant)).get(thisEvent.pid)).entrySet().iterator().next()).dateDebut;
									Date dateStockee = ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) slaveMap.get(thisEvent.slave)).get(thisEvent.composant)).get(thisEvent.pid)).keySet().iterator().next();

									/**
									 * Si deux fois dateFin, c'est un doublon et
									 * on arrive ici => il faut tester cela
									 */
									if (thisEvent.dateDebut.equals(((Event) ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) slaveMap.get(thisEvent.slave)).get(thisEvent.composant)).get(thisEvent.pid)).get(dateStockee)).dateFin))
									{
										log.INFO("doubon d�tect� :  - " + line);
									} else
									{
										if (thisEvent.dateDebut.after(dateStockee))
										{
											// la ligne pars�e est post�rieure �
											// la
											// ligne stock�e => m�j ligne
											log.INFO(((Event) ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) slaveMap.get(thisEvent.slave)).get(thisEvent.composant)).get(thisEvent.pid)).get(dateStockee)).setDateFin(thisEvent.dateDebut));

										} else
										{
											// la ligne pars�e est ant�rieure �
											// la
											// ligne stock�e => m�j ligne en
											// inversant les dates

											// remplacement de la date de d�but
											((Event) ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) slaveMap.get(thisEvent.slave)).get(thisEvent.composant)).get(thisEvent.pid)).get(dateStockee)).setDateDebut(thisEvent.dateDebut);
											// m�j de la date de fin
											log.INFO(((Event) ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) slaveMap.get(thisEvent.slave)).get(thisEvent.composant)).get(thisEvent.pid)).get(dateStockee)).setDateFin(dateStockee));

										}
									}

								} else
								{
									// cas anormal, je sais pas quoi faire
									log.INFO("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHhhhhhhhhhhhhhhhh !!!");
									// on ajoute les infos par date
									((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) slaveMap.get(thisEvent.slave)).get(thisEvent.composant)).get(thisEvent.pid)).put(thisEvent.dateDebut, thisEvent);
								}

							}

						} else
						{
							// pid non trouv�e pour ce composant et pour ce
							// slave : on ajoute la ligne
							// cr�ation map par date
							HashMap<Date, Event> dateMap = new HashMap<Date, Event>();
							dateMap.put(thisEvent.dateDebut, thisEvent);
							// ajout de la map par date dans la map par pid
							((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) slaveMap.get(thisEvent.slave)).get(thisEvent.composant)).put(thisEvent.pid, dateMap);

						}

					} else
					{
						// composant non trouv� pour ce slave : on ajoute la
						// ligne

						// cr�ation map par pid
						HashMap<Integer, HashMap> pidMap = new HashMap<Integer, HashMap>();

						// cr�ation map par date
						HashMap<Date, Event> dateMap = new HashMap<Date, Event>();

						// ajout des infos par date
						dateMap.put(thisEvent.dateDebut, thisEvent);

						// ajout des infos par pid
						pidMap.put(thisEvent.pid, dateMap);

						// ajout de la map par pid dans la map par composant
						((HashMap<String, HashMap>) slaveMap.get(thisEvent.slave)).put(thisEvent.composant, pidMap);

					}

				}

			}
		}

		/*
		 * retraitement des infos : boucle par pid il faut ensuite diff�rentier
		 * par slave (on peut avoir un m�me pid � la m�me date sur deux slaves
		 * diff�rents) puis aggr�ger les lignes d�but et fin
		 */
		/*
		 * for (Integer pid : pidMap.keySet()) {
		 * 
		 * HashMap<Date, Event> dateMap = pidMap.get(pid); int s =
		 * dateMap.size(); System.out.println("pid=" + pid + " - size=" + s);
		 * 
		 * if (s > 1) { // cas normal : au moins deux lignes pour un pid //
		 * boucle sur les �l�ments ayant ce pid for (Date date :
		 * dateMap.keySet()) {
		 * 
		 * } } else { // une seule ligne avec ce pid => l'info est incompl�te,
		 * on ne // peut pas conna�tre la dur�e. } }
		 */

		return slaveMap;

	}

	public static HashMap<String, HashMap> logToData2(File logFile) throws IOException, ParseException
	{
		log.INFO("parse fichier " + logFile.getName());
		FileInputStream fis = new FileInputStream(logFile);

		BufferedReader inputReader = new BufferedReader(new InputStreamReader(fis));
		String line;
		String[] lt;
		GregorianCalendar gc = new GregorianCalendar();
		int len = 0;
		Event thisEvent = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.FRENCH);

		// map conteant les infos de m�m pid
		// HashMap<String, HashMap<String, HashMap<Integer, HashMap<Date,
		// Event>>>> slaveMap = new HashMap<String, HashMap<String,
		// HashMap<Integer, HashMap<Date, Event>>>>();

		HashMap<String, HashMap> composantMap = new HashMap<String, HashMap>();
		ArrayList<Event> events = new ArrayList<Event>();

		/*
		 * lecture du fichier ligne par ligne
		 */
		while ((line = inputReader.readLine()) != null)
		{
			/*
			 * d�coupage des lignes du fichier par s�parateur (espace.)
			 */
			lt = line.replaceAll("  ", " ").split(FS_RAW);
			len = lt.length;

			if (len != 9 && len != 10)
			{
				log.ERR("ligne non conforme trouv�e (skipping) : " + line);
			} else
			{
				// parsing des lignes => r�cup�ration des infos n�cessaires
				thisEvent = new Event(sdf.parse(lt[0] + FS_RAW + lt[1]), lt[4], lt[6], lt[8], lt[5]);

				if (!composantMap.containsKey(thisEvent.composant))
				{

					// ce composant n'a pas encore �t� trouv� => toute la ligne
					// est
					// �
					// ajouter
					// hashmap contenant les infos par slave
					HashMap<String, HashMap> slaveMap = new HashMap<String, HashMap>();

					// hashmap contenant les infos par pid
					HashMap<Integer, HashMap> pidMap = new HashMap<Integer, HashMap>();

					// hashmap contenatles infos par date
					HashMap<Date, Event> dateMap = new HashMap<Date, Event>();

					// on ajoute les infos par date
					dateMap.put(thisEvent.dateDebut, thisEvent);

					// on ajoute les infos par pid
					pidMap.put((Integer) thisEvent.pid, dateMap);

					// on ajoute les infos par slave
					slaveMap.put(thisEvent.slave, pidMap);

					// on ajoute les infos par composant
					composantMap.put(thisEvent.composant, slaveMap);

				} else
				{
					/**
					 * ce composant a d�j� �t� trouv� <br>
					 * il faut v�rifier que la ligne n'est pas un doublon<br>
					 * cl� de v�rif : <br>
					 * <ul>
					 * <li>- composant
					 * <li>- slave
					 * <li>- pid
					 * <li>- date
					 * <li>- type
					 * </ul>
					 */

					if (composantMap.get(thisEvent.composant).containsKey(thisEvent.slave))
					{
						// slave d�j� trouv�e pour ce composant => cl� de tri
						// suivante � v�rifier :
						// pid
						if (((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).containsKey(thisEvent.pid))
						{
							/**
							 * pid d�j� trouv� pour ce composant et pour ce
							 * slave<br>
							 * il faut v�rifier que la ligne n'est pas un
							 * doublon<br>
							 * cl� suivante : date
							 */
							if (((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).get(thisEvent.pid)).containsKey(thisEvent.dateDebut))
							{

								/*
								 * date d�j� trouv�e v�rif suivante = type
								 */

								if (!((Event) ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).get(thisEvent.pid)).get(thisEvent.dateDebut)).type.equals(thisEvent.type))
								{
									// pas le m�me type => date debut = datefin
									// ((Event) ((HashMap<Integer, Event>)
									// ((HashMap<String, HashMap>)
									// slaveMap.get(thisEvent.slave)).get(thisEvent.composant)).get(thisEvent.pid)).setDateFin(thisEvent.dateDebut);
									log.INFO(((Event) ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).get(thisEvent.pid)).get(thisEvent.dateDebut)).setDateFin(thisEvent.dateDebut));
									events.add(((Event) ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).get(thisEvent.pid)).get(thisEvent.dateDebut)));

								} else
								{
									// si m�me type, on ne fait rien => c'est un
									// doublon }
									log.INFO("doubon d�tect� : " + line);
								}
							} else
							{
								/*
								 * pas la m�me date
								 */
								/**
								 * FIXME : aggr�ger les donn�es pour calculer la
								 * dur�e <br>
								 * => attention si deux PID se trouvent dans la
								 * logs pour deux ex�cutions diff�rentes<br>
								 * il faut les diff�rencier
								 */

								if (((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).get(thisEvent.pid)).size() == 1)
								{

									if (thisEvent.type.equals(Event.DEBUT))
									{
										/**
										 * cas sp�cial : deux traitements sur le
										 * m�me slave <br>
										 * avec le m�me pid<br>
										 * il ne faut pas les m�langer lors de
										 * l'agr�gation
										 */
										((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).get(thisEvent.pid)).put(thisEvent.dateDebut, thisEvent);
										//log.WARN("PID d�j� trouv� sur ce slave :" + thisEvent.pid);
									} else
									{

										/**
										 * cas normal : 1 seule autre date pour
										 * ce slave, ce composant et ce pid<br>
										 * il faut aggr�ger les deux lignes
										 */

										Date dateStockee = ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).get(thisEvent.pid)).keySet().iterator().next();

										/**
										 * Si deux fois dateFin, c'est un
										 * doublon et on arrive ici => il faut
										 * tester cela
										 */
										if (thisEvent.dateDebut.equals(((Event) ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).get(thisEvent.pid)).get(dateStockee)).dateFin))
										{
											log.INFO("doubon d�tect� :  - " + line);
										} else
										{
											if (thisEvent.dateDebut.after(dateStockee))
											{
												// la ligne pars�e est
												// post�rieure �
												// la
												// ligne stock�e => m�j ligne
												log.INFO(((Event) ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).get(thisEvent.pid)).get(dateStockee)).setDateFin(thisEvent.dateDebut));
												events.add(((Event) ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).get(thisEvent.pid)).get(dateStockee)));
											} else
											{
												// la ligne pars�e est
												// ant�rieure �
												// la
												// ligne stock�e => m�j ligne en
												// inversant les dates

												// remplacement de la date de
												// d�but
												((Event) ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).get(thisEvent.pid)).get(dateStockee)).setDateDebut(thisEvent.dateDebut);
												// m�j de la date de fin
												log.INFO(((Event) ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).get(thisEvent.pid)).get(dateStockee)).setDateFin(dateStockee));

											}
										}
									}
								} else
								{
									// cas anormal, je sais pas quoi faire !!!
									log.WARN("PID multiples - r�sultat non granti : " + thisEvent.pid);

									/**
									 * cas complexe :<br>
									 * plusieurs traitements avec le m�me pid<br>
									 * mais pas � la m�me date<br>
									 * on suppose que la FE_LOG est croissante
									 * dans le temps<br>
									 * => on traite les infos les unes apr�s les
									 * autres
									 */

									if (thisEvent.type.equals(Event.DEBUT))
									{
										/**
										 * Event de type DEBUT => on l'ajoute
										 * dans la liste
										 */
										((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).get(thisEvent.pid)).put(thisEvent.dateDebut, thisEvent);
									} else
									{
										/**
										 * Event de type FIN => on doit enrichir
										 * un Event de type DEBUT<BR>
										 * ==> le dernier sans dur�e
										 */
										Iterator dateIterator = ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).get(thisEvent.pid)).keySet().iterator();
										while (dateIterator.hasNext())
										{
											Event event = (Event) ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).get(thisEvent.pid)).get((Date) dateIterator.next());
											if (event.type.equals(Event.DEBUT) && event.duree == null)
											{
												/**
												 * calcul duree pour cet Event
												 */
												log.INFO(((Event) ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).get(thisEvent.pid)).get(event.dateDebut)).setDateFin(thisEvent.dateDebut));
												events.add(((Event) ((HashMap<Date, Event>) ((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).get(thisEvent.pid)).get(thisEvent.dateDebut)));
											}

										}
									}
								}

							}

						} else
						{
							// pid non trouv�e pour ce composant et pour ce
							// slave : on ajoute la ligne
							// cr�ation map par date
							HashMap<Date, Event> dateMap = new HashMap<Date, Event>();
							dateMap.put(thisEvent.dateDebut, thisEvent);
							// ajout de la map par date dans la map par pid
							((HashMap<Integer, HashMap>) ((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).get(thisEvent.slave)).put(thisEvent.pid, dateMap);

						}

					} else
					{
						// slave non trouv� pour ce composant : on ajoute la
						// ligne

						// cr�ation map par pid
						HashMap<Integer, HashMap> pidMap = new HashMap<Integer, HashMap>();

						// cr�ation map par date
						HashMap<Date, Event> dateMap = new HashMap<Date, Event>();

						// ajout des infos par date
						dateMap.put(thisEvent.dateDebut, thisEvent);

						// ajout des infos par pid
						pidMap.put(thisEvent.pid, dateMap);

						// ajout de la map par pid dans la map par composant
						((HashMap<String, HashMap>) composantMap.get(thisEvent.composant)).put(thisEvent.slave, pidMap);

					}

				}

			}
		}
		/*
		 * System.out.println(LINE_SEP); System.out.println(LINE_SEP);
		 * System.out.println(LINE_SEP); System.out.println(LINE_SEP);
		 * System.out.println(LINE_SEP); System.out.println(LINE_SEP);
		 * System.out.println(LINE_SEP);
		 * 
		 * for (Event event : events) { log.INFO(event.toString() + " - type : "
		 * + event.type); }
		 */
		return composantMap;

	}

}
