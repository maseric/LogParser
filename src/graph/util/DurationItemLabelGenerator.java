package graph.util;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.data.xy.XYDataset;
import org.joda.time.Duration;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;

public class DurationItemLabelGenerator extends StandardXYItemLabelGenerator implements XYItemLabelGenerator
{

	/** The threshold. */
	private double threshold;

	/**
	 * Creates a new generator that only displays labels that are greater than
	 * or equal to the threshold value.
	 * 
	 * @param threshold
	 *            the threshold value.
	 */
	public DurationItemLabelGenerator(double thresholdDateFormat, String formatString, DateFormat format, NumberFormat format2)
	{

		super(formatString, format, format2);
		// super("", NumberFormat.getInstance(), NumberFormat.getInstance());
		this.threshold = thresholdDateFormat;
	}

	public DurationItemLabelGenerator()
	{
		// TODO Auto-generated constructor stub
	}

	public DurationItemLabelGenerator(String formatString)
	{
		super(formatString);
		// TODO Auto-generated constructor stub
	}

	public DurationItemLabelGenerator(String formatString, NumberFormat format, NumberFormat format2)
	{
		super(formatString, format, format2);
		// TODO Auto-generated constructor stub
	}

	public DurationItemLabelGenerator(String formatString, DateFormat format, NumberFormat format2)
	{
		super(formatString, format, format2);
		// TODO Auto-generated constructor stub
	}

	public DurationItemLabelGenerator(String formatString, NumberFormat format, DateFormat format2)
	{
		super(formatString, format, format2);
		// TODO Auto-generated constructor stub
	}

	public DurationItemLabelGenerator(String formatString, DateFormat format, DateFormat format2)
	{
		super(formatString, format, format2);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Generates a label for the specified item. The label is typically a
	 * formatted version of the data value, but any text can be used.
	 * 
	 * @param dataset
	 *            the dataset (<code>null</code> not permitted).
	 * @param series
	 *            the series index (zero-based).
	 * @param category
	 *            the category index (zero-based).
	 * 
	 * @return the label (possibly <code>null</code>).
	 */
	public String generateLabel(XYDataset dataset, int series, int category)
	{
		String result = null;
		Number value = dataset.getYValue(series, category);
		if (value != null)
		{
			double v = value.doubleValue();
			if (v > this.threshold)
			{
				//PeriodFormatter formatter = PeriodFormat.getDefault();

//				result = formatter.withLocale(Locale.FRENCH).print(new Duration(value.longValue() * 1000).toPeriod());
				
				result=secondsToTime(value.longValue());
				// result = value.toString(); // could apply formatting here
			}
		}
		return result;
	}

	public static String secondsToTime(long seconds)
	{
		String result = "";

		PeriodFormatter formatter = PeriodFormat.getDefault();

		result = formatter.withLocale(Locale.FRENCH).print(new Duration(seconds * 1000).toPeriod());

		return result;
	}

}
