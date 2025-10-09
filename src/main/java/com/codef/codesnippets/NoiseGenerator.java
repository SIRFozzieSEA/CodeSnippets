package com.codef.codesnippets;

import javax.sound.sampled.*;
import java.io.*;

public class NoiseGenerator {
	/**
	 * Generates brown noise and writes it to a WAV file.
	 * 
	 * @param filename        Output WAV file path
	 * @param durationSeconds Duration in seconds
	 * @param sampleRate      Sample rate (e.g., 44100)
	 * @throws IOException
	 * @throws LineUnavailableException
	 */

	public static void main(String[] args) {
		try {
			// generateBrownNoiseWav("D:\\brown_noise.wav", 2700, 44100);
			generateRunningWaterWav("D:\\running_water.wav", 60, 44100);
			generatePinkNoiseWav("D:\\pink_noise.wav", 2700, 44100);
		} catch (IOException | LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void generateBrownNoiseWav(String filename, int durationSeconds, int sampleRate)
			throws IOException, LineUnavailableException {
		int numSamples = durationSeconds * sampleRate;
		byte[] audioData = new byte[numSamples * 2]; // 16-bit PCM
		double lastOut = 0.0;
		for (int i = 0; i < numSamples; i++) {
			double white = Math.random() * 2 - 1;
			lastOut = (lastOut + (0.02 * white)) / 1.02;
			double brown = lastOut * 3.5; // scale for volume
			// Clip to [-1, 1]
			if (brown > 1.0)
				brown = 1.0;
			if (brown < -1.0)
				brown = -1.0;
			short val = (short) (brown * Short.MAX_VALUE);
			audioData[i * 2] = (byte) (val & 0xff);
			audioData[i * 2 + 1] = (byte) ((val >> 8) & 0xff);
		}
		AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
		ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
		AudioInputStream ais = new AudioInputStream(bais, format, numSamples);
		AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filename));
	}

	/**
	 * Generates a WAV file that sounds like running water.
	 * 
	 * @param filename        Output WAV file path
	 * @param durationSeconds Duration in seconds
	 * @param sampleRate      Sample rate (e.g., 44100)
	 * @throws IOException
	 * @throws LineUnavailableException
	 */
	public static void generateRunningWaterWav(String filename, int durationSeconds, int sampleRate)
			throws IOException, LineUnavailableException {
		int numSamples = durationSeconds * sampleRate;
		byte[] audioData = new byte[numSamples * 2]; // 16-bit PCM
		double[] filter = new double[3];
		double lastOut = 0.0;
		for (int i = 0; i < numSamples; i++) {
			// White noise base
			double white = Math.random() * 2 - 1;
			// Simple lowpass filter (IIR)
			filter[0] = 0.99 * filter[0] + 0.01 * white;
			// Amplitude modulation: slow random walk
			if (i % (sampleRate / 10) == 0) {
				lastOut = 0.7 + Math.random() * 0.3; // Vary amplitude every 0.1s
			}
			double modulated = filter[0] * lastOut;
			// Add ripples: short bursts of higher frequency noise
			if (Math.random() < 0.0005) {
				modulated += (Math.random() * 2 - 1) * 0.5;
			}
			// Clip to [-1, 1]
			if (modulated > 1.0)
				modulated = 1.0;
			if (modulated < -1.0)
				modulated = -1.0;
			short val = (short) (modulated * Short.MAX_VALUE);
			audioData[i * 2] = (byte) (val & 0xff);
			audioData[i * 2 + 1] = (byte) ((val >> 8) & 0xff);
		}
		AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
		ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
		AudioInputStream ais = new AudioInputStream(bais, format, numSamples);
		AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filename));
	}

	/**
	 * Generates a WAV file that contains pink noise.
	 * 
	 * @param filename        Output WAV file path
	 * @param durationSeconds Duration in seconds
	 * @param sampleRate      Sample rate (e.g., 44100)
	 * @throws IOException
	 * @throws LineUnavailableException
	 */
	public static void generatePinkNoiseWav(String filename, int durationSeconds, int sampleRate)
			throws IOException, LineUnavailableException {
		int numSamples = durationSeconds * sampleRate;
		byte[] audioData = new byte[numSamples * 2]; // 16-bit PCM
		double b0 = 0.0, b1 = 0.0, b2 = 0.0;
		for (int i = 0; i < numSamples; i++) {
			double white = Math.random() * 2 - 1;
			b0 = 0.99765 * b0 + white * 0.0990460;
			b1 = 0.96300 * b1 + white * 0.2965164;
			b2 = 0.57000 * b2 + white * 1.0526913;
			double pink = b0 + b1 + b2 + white * 0.1848;
			// Normalize and clip
			pink *= 0.05; // scale for volume
			if (pink > 1.0)
				pink = 1.0;
			if (pink < -1.0)
				pink = -1.0;
			short val = (short) (pink * Short.MAX_VALUE);
			audioData[i * 2] = (byte) (val & 0xff);
			audioData[i * 2 + 1] = (byte) ((val >> 8) & 0xff);
		}
		AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
		ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
		AudioInputStream ais = new AudioInputStream(bais, format, numSamples);
		AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filename));
	}

}