package com.codef.codesnippets;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class JFreeChartSamples {

	public static void main(String[] args) throws IOException {
		doPieChart();
	}

	public static void doPieChart() throws IOException {

		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("IPhone 5s", new Double(20));
		dataset.setValue("SamSung Grand", new Double(20));
		dataset.setValue("MotoG", new Double(40));
		dataset.setValue("Nokia Lumia", new Double(10));

		JFreeChart chart = ChartFactory.createPieChart("Mobile Sales", // chart title
				dataset, // data
				true, // include legend
				true, // tooltips
				false); // URLs

		int width = 640; /* Width of the image */
		int height = 480; /* Height of the image */
		File pieChart = new File("E:\\PieChart.jpeg");

		ChartUtils.saveChartAsJPEG(pieChart, chart, width, height);

	}

}
