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

		DefaultPieDataset<String> dataset = new DefaultPieDataset<String>();
		dataset.setValue("IPhone 5s", Double.valueOf(20));
		dataset.setValue("SamSung Grand", Double.valueOf(20));
		dataset.setValue("MotoG", Double.valueOf(20));
		dataset.setValue("Nokia Lumia", Double.valueOf(20));

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
