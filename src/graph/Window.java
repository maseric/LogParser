package graph;

import graph.util.DurationItemLabelGenerator;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;

import parser.Event;
import parser.LogParser;
import parser.SimpleLogger;

public class Window extends JPanel
{

	private static SimpleLogger log;
	/**
     *
     */
	private static final long serialVersionUID = -2672797227144920309L;

	private static HashMap<String, HashMap> getData(String file)
	{
		HashMap<String, HashMap> composantMap = null;

		File logFile = new File(file);
		if (logFile.isFile())
		{
			try
			{
				composantMap = LogParser.logToData2(logFile);

				// parse(logFile);
			} catch (IOException e)
			{
				log.ERR("unable to parse file " + file);
				e.printStackTrace();
			} catch (ParseException e)
			{
				log.ERR("error during line parsing (date)");
				e.printStackTrace();
			}
		}
		return composantMap;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		log.INFO("DEBUT");

		try
		{ /*
		 * graphiques
		 */
			Window chart;

			HashMap<String, HashMap> composantMap = getData(args[0]);

			// TODO Auto-generated method stub
			frame = new JFrame("Graphs");

			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			chart = new Window(composantMap);
			bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

			JPanel jPanel1 = new javax.swing.JPanel();
			org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
			jPanel1.setLayout(jPanel1Layout);
			jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(0, 0, Short.MAX_VALUE));
			jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(0, 0, Short.MAX_VALUE));

			jSlider1 = new javax.swing.JSlider();
			jSlider1.setMinimum(0);
			jSlider1.setMaximum(maxValue);
			jSlider1.setValue(maxValue);
			jSlider1.addChangeListener(new javax.swing.event.ChangeListener()
			{
				public void stateChanged(javax.swing.event.ChangeEvent evt)
				{
					jSlider1StateChanged(evt);
				}
			});

			JLabel jLabel1 = new JLabel();
			jLabel1.setText("Seuil affichage durée (s)");

			jlabel2 = new JLabel();
			jTextField1 = new javax.swing.JTextField();
			jTextField1.setText(Integer.toString(maxValue));

			org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider1, org.jdesktop.beansbinding.ELProperty.create("${value}"), jTextField1, org.jdesktop.beansbinding.BeanProperty.create("text"), "");
			bindingGroup.addBinding(binding);

			org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(frame.getContentPane());
			frame.getContentPane().setLayout(layout);

			layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(layout.createSequentialGroup().addContainerGap().add(chart).addContainerGap().add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()).add(
					layout.createSequentialGroup().add(jSlider1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(jLabel1).addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED).add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(
							0, 37, Short.MAX_VALUE)));

			layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
					layout.createSequentialGroup().addContainerGap().add(chart).addContainerGap().add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(18, 18, 18).add(
							layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jSlider1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(jLabel1).add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
							.addContainerGap(47, Short.MAX_VALUE)));

			bindingGroup.bind();

			frame.pack();
			// frame.getContentPane().add(chart, BorderLayout.CENTER);
			// frame.getContentPane().add(jSlider1,BorderLayout.SOUTH);
			// frame.getContentPane().add(jLabel1,
			// BorderLayout.AFTER_LAST_LINE);
			// frame.getContentPane().add(jTextField1,BorderLayout.AFTER_LAST_LINE);

			frame.setSize(640, 480);
			frame.setVisible(true);

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.INFO("FIN");
	}

	private static JFreeChart chart;
	// pour tostring
	private DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRENCH);
	private DateFormat dfHeures = new SimpleDateFormat("H:m:s");
	private static int maxValue = 0;
	// Create a set of panels that can show charts
	private ChartPanel panel;

	public Window(boolean isDoubleBuffered)
	{
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	public Window(HashMap<String, HashMap> composantMap) throws Exception
	{
		this.setLayout(new GridLayout(1, 1));

		this.chart = createGroups(composantMap);
		this.panel = new ChartPanel(chart, true, true, true, true, true);
		// this.panel.setDisplayToolTips(true);

		this.add(panel);

	}

	public Window(LayoutManager layout)
	{
		super(layout);
		// TODO Auto-generated constructor stub
	}

	public Window(LayoutManager layout, boolean isDoubleBuffered)
	{
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	private static void jSlider1StateChanged(javax.swing.event.ChangeEvent evt)
	{
		// TODO add your handling code here:
		System.out.println("INFO : new treeshold value = " + jSlider1.getValue());
		XYItemLabelGenerator labelGenerator = new DurationItemLabelGenerator(jSlider1.getValue(), StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT, new SimpleDateFormat(), new DecimalFormat());
		// chart.fireChartChanged();

		((XYPlot) chart.getPlot()).getRenderer().setBaseItemLabelGenerator(labelGenerator);

		// ((XYPlot)
		// chart.getPlot()).getRenderer().setBaseItemLabelsVisible(Boolean.FALSE,
		// true);
		// frame.pack();
	}

	public JFreeChart createGroups(HashMap<String, HashMap> composantMap) throws Exception
	{

		TimeSeriesCollection dataset = getGroups(composantMap);

		JFreeChart chart = ChartFactory.createTimeSeriesChart("Temps de traitement", // title
				"Date", // x-axis label
				"Durée (s)", // y-axis label
				dataset, // data
				true, // create legend?
				true, // generate tooltips?
				true // generate URLs?
				);

		chart.setBackgroundPaint(Color.white);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.gray);
		plot.setRangeGridlinePaint(Color.gray);
		// plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		XYItemRenderer r = plot.getRenderer();

		if (r instanceof XYLineAndShapeRenderer)
		{
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(true);
			renderer.setBaseShapesFilled(true);
			renderer.setDrawSeriesLineAsPath(true);

			XYToolTipGenerator tt1 = new XYToolTipGenerator()
			{
				DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.FRENCH);
				PeriodFormatter formatter = PeriodFormat.getDefault();

				public String generateToolTip(XYDataset dataset, int series, int item)
				{
					StringBuffer sb = new StringBuffer();
					Number x = dataset.getX(series, item);
					Number y = dataset.getY(series, item);
					String name = (String) dataset.getSeriesKey(series);
					Date dx = new Date(x.longValue());
					Period p = new Period(y.longValue() * 1000);

					sb.append(name + " - date : " + df.format(dx));
					sb.append(" - temps : " + formatter.withLocale(Locale.FRENCH).print(p));

					return sb.toString();
				}
			};

			// XYToolTipGenerator toolTipGenerator = new
			// StandardXYToolTipGenerator();
			// r.setToolTipGenerator(toolTipGenerator);

			// r.setSeriesToolTipGenerator(1, tt1);
			r.setBaseToolTipGenerator(tt1);

		}

		/*
		 * XYItemLabelGenerator generator = new
		 * StandardXYItemLabelGenerator(StandardXYItemLabelGenerator
		 * .DEFAULT_ITEM_LABEL_FORMAT, new SimpleDateFormat(), new
		 * DecimalFormat() );
		 */
		XYItemLabelGenerator labelGenerator = new DurationItemLabelGenerator(maxValue, StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT, new SimpleDateFormat(), new DecimalFormat());

		r.setBaseItemLabelGenerator(labelGenerator);

		r.setBaseItemLabelsVisible(true);

		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("dd MMM yy"));

		return chart;
	}

	public TimeSeriesCollection getGroups(HashMap<String, HashMap> composantMap) throws Exception
	{
		TimeSeriesCollection tsc = new TimeSeriesCollection();

		Iterator<String> iterComposant = composantMap.keySet().iterator();

		// boucle sur les composants
		while (iterComposant.hasNext())
		{
			String composant = iterComposant.next();

			HashMap<String, HashMap> slaveMap = composantMap.get(composant);

			Iterator<String> iterSlave = slaveMap.keySet().iterator();
			// boucle sur les slaves
			while (iterSlave.hasNext())
			{
				String slave = iterSlave.next();

				TimeSeries serie = new TimeSeries(composant + " - " + slave);

				HashMap<Integer, HashMap> pidMap = slaveMap.get(slave);

				Iterator<Integer> iterPid = pidMap.keySet().iterator();
				while (iterPid.hasNext())
				{
					Integer pid = iterPid.next();
					HashMap<Date, Event> dateMap = pidMap.get(pid);

					Iterator<Date> iterDate = dateMap.keySet().iterator();
					while (iterDate.hasNext())
					{
						Event event = dateMap.get(iterDate.next());
						Duration duree = event.duree;
						if (duree != null)
						{
							serie.add(new Second(event.dateDebut), duree.getStandardSeconds());
							if (maxValue < event.duree.getStandardSeconds())
							{
								maxValue = (int) event.duree.getStandardSeconds();
							}
							System.out.println(df.format(event.dateDebut) + ";" + dfHeures.format(event.dateDebut) + ";" + slave + ";" + composant + ";" + pid + ";" + event.duree.getStandardSeconds() + ";" + DurationItemLabelGenerator.secondsToTime(event.duree.getStandardSeconds()));
						}
					}
				}
				tsc.addSeries(serie);
			}
		}

		return tsc;

	}

	private static JSlider jSlider1;
	private static JTextField jTextField1;
	private static JLabel jlabel2;
	private static org.jdesktop.beansbinding.BindingGroup bindingGroup;
	private static JFrame frame;
}
