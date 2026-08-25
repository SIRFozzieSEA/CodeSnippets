package com.codef.codesnippets;

import java.io.*;

public class NoiseGenerator {

	/*
	 * 
	 * This is a complete WAV audio file generator that creates various types of
	 * noise and sounds with adjustable volume. It generates files in stereo format
	 * (2 channels) at 44100 Hz sample rate
	 * 
	 * Note: This generates 5 different types of noise and sounds, each
	 * approximately 3 minutes long, using only standard Java libraries. The volume
	 * can be adjusted from -20 dB (very quiet) to +10 dB (very loud), with negative
	 * values reducing volume and positive values increasing it.
	 * 
	 */

	private static final int SAMPLE_RATE = 44100;
	private static final int DURATION_SECONDS = 2700; // 45 minutes
	private static final int TOTAL_SAMPLES = SAMPLE_RATE * DURATION_SECONDS;

	private static double VOLUME_DB = 0.0;

	public static void main(String[] args) {
		if (args.length > 0) {
			try {
				VOLUME_DB = Double.parseDouble(args[0]);
			} catch (NumberFormatException e) {
				System.out.println("Invalid volume argument, using default " + VOLUME_DB + " db");
			}
		}

		System.out.println("Generating noise files with volume: " + VOLUME_DB + " dB");
		generateNoiseFiles(VOLUME_DB);
	}

	private static void generateNoiseFiles(double volumeDb) {
		try {
			// Generate all noise types as WAV files
			generateWhiteNoiseWav("/home/sirfozzie/Shared/white_noise.wav", volumeDb);
			generatePinkNoiseWav("/home/sirfozzie/Shared/pink_noise.wav", volumeDb);
			generateBrownNoiseWav("/home/sirfozzie/Shared/brown_noise.wav", volumeDb);
			generateRainNoiseWav("/home/sirfozzie/Shared/rain_noise.wav", volumeDb);
			generateWaterfallNoiseWav("/home/sirfozzie/Shared/waterfall_noise.wav", volumeDb);

			System.out.println("All noise files generated successfully!");
		} catch (Exception e) {
			System.err.println("Error generating noise files: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void generateWhiteNoiseWav(String filename, double volumeDb) throws Exception {
		System.out.println("Generating white noise: " + filename);

		byte[] audioData = generateWhiteNoise(TOTAL_SAMPLES, volumeDb);
		writeWavFile(filename, audioData, SAMPLE_RATE, 2); // Stereo
		System.out.println("White noise file created: " + filename);
	}

	private static void generatePinkNoiseWav(String filename, double volumeDb) throws Exception {
		System.out.println("Generating pink noise: " + filename);

		byte[] audioData = generatePinkNoise(TOTAL_SAMPLES, volumeDb);
		writeWavFile(filename, audioData, SAMPLE_RATE, 2); // Stereo
		System.out.println("Pink noise file created: " + filename);
	}

	private static void generateBrownNoiseWav(String filename, double volumeDb) throws Exception {
		System.out.println("Generating brown noise: " + filename);

		byte[] audioData = generateBrownNoise(TOTAL_SAMPLES, volumeDb);
		writeWavFile(filename, audioData, SAMPLE_RATE, 2); // Stereo
		System.out.println("Brown noise file created: " + filename);
	}

	private static void generateRainNoiseWav(String filename, double volumeDb) throws Exception {
		System.out.println("Generating rain noise: " + filename);

		byte[] audioData = generateRainNoise(TOTAL_SAMPLES, volumeDb);
		writeWavFile(filename, audioData, SAMPLE_RATE, 2); // Stereo
		System.out.println("Rain noise file created: " + filename);
	}

	private static void generateWaterfallNoiseWav(String filename, double volumeDb) throws Exception {
		System.out.println("Generating waterfall noise: " + filename);

		byte[] audioData = generateWaterfallNoise(TOTAL_SAMPLES, volumeDb);
		writeWavFile(filename, audioData, SAMPLE_RATE, 2); // Stereo
		System.out.println("Waterfall noise file created: " + filename);
	}

	private static byte[] generateWhiteNoise(int numSamples, double volumeDb) {
		byte[] audioData = new byte[numSamples * 2 * 2]; // 2 bytes per sample * 2 channels

		for (int i = 0; i < numSamples; i++) {
			// Generate random value between -32768 and 32767
			int value = (int) (Math.random() * 65536) - 32768;

			// Apply volume adjustment
			double adjustedValue = value * Math.pow(10, volumeDb / 20.0);
			if (adjustedValue > 32767)
				adjustedValue = 32767;
			if (adjustedValue < -32768)
				adjustedValue = -32768;

			// Convert to bytes and store in little-endian format
			int sample = (int) adjustedValue;
			audioData[i * 4] = (byte) (sample & 0xFF); // Left channel, low byte
			audioData[i * 4 + 1] = (byte) ((sample >> 8) & 0xFF); // Left channel, high byte
			audioData[i * 4 + 2] = (byte) (sample & 0xFF); // Right channel, low byte
			audioData[i * 4 + 3] = (byte) ((sample >> 8) & 0xFF); // Right channel, high byte
		}

		return audioData;
	}

	private static byte[] generatePinkNoise(int numSamples, double volumeDb) {
		byte[] audioData = new byte[numSamples * 2 * 2]; // 2 bytes per sample * 2 channels

		double pinkValue = 0.0;
		double lastValue = 0.0;

		for (int i = 0; i < numSamples; i++) {
			// Generate pink noise using Voss-McCartney algorithm
			double white = Math.random() * 2.0 - 1.0;
			pinkValue = 0.99 * lastValue + 0.01 * white;
			lastValue = pinkValue;

			// Apply volume adjustment
			double adjustedValue = pinkValue * 32767 * Math.pow(10, volumeDb / 20.0);
			if (adjustedValue > 32767)
				adjustedValue = 32767;
			if (adjustedValue < -32768)
				adjustedValue = -32768;

			// Convert to bytes and store in little-endian format
			int sample = (int) adjustedValue;
			audioData[i * 4] = (byte) (sample & 0xFF); // Left channel, low byte
			audioData[i * 4 + 1] = (byte) ((sample >> 8) & 0xFF); // Left channel, high byte
			audioData[i * 4 + 2] = (byte) (sample & 0xFF); // Right channel, low byte
			audioData[i * 4 + 3] = (byte) ((sample >> 8) & 0xFF); // Right channel, high byte
		}

		return audioData;
	}

	private static byte[] generateBrownNoise(int numSamples, double volumeDb) {
		byte[] audioData = new byte[numSamples * 2 * 2]; // 2 bytes per sample * 2 channels

		double brownValue = 0.0;

		for (int i = 0; i < numSamples; i++) {
			// Generate brown noise using integration
			double white = Math.random() * 2.0 - 1.0;
			brownValue += white / 50.0; // Scale down to prevent overflow

			if (brownValue > 1.0)
				brownValue = 1.0;
			if (brownValue < -1.0)
				brownValue = -1.0;

			// Apply volume adjustment
			double adjustedValue = brownValue * 32767 * Math.pow(10, volumeDb / 20.0);
			if (adjustedValue > 32767)
				adjustedValue = 32767;
			if (adjustedValue < -32768)
				adjustedValue = -32768;

			// Convert to bytes and store in little-endian format
			int sample = (int) adjustedValue;
			audioData[i * 4] = (byte) (sample & 0xFF); // Left channel, low byte
			audioData[i * 4 + 1] = (byte) ((sample >> 8) & 0xFF); // Left channel, high byte
			audioData[i * 4 + 2] = (byte) (sample & 0xFF); // Right channel, low byte
			audioData[i * 4 + 3] = (byte) ((sample >> 8) & 0xFF); // Right channel, high byte
		}

		return audioData;
	}

	private static byte[] generateRainNoise(int numSamples, double volumeDb) {
		byte[] audioData = new byte[numSamples * 2 * 2]; // 2 bytes per sample * 2 channels

		for (int i = 0; i < numSamples; i++) {
			// Create rain-like noise with random bursts and filtering
			double value = Math.random() * 0.5;

			// Add some frequency modulation to simulate rain drops
			if (Math.random() > 0.95) { // Occasional loud burst
				value = Math.random() * 0.8 + 0.2;
			}

			// Apply volume adjustment
			double adjustedValue = value * 32767 * Math.pow(10, volumeDb / 20.0);
			if (adjustedValue > 32767)
				adjustedValue = 32767;
			if (adjustedValue < -32768)
				adjustedValue = -32768;

			// Convert to bytes and store in little-endian format
			int sample = (int) adjustedValue;
			audioData[i * 4] = (byte) (sample & 0xFF); // Left channel, low byte
			audioData[i * 4 + 1] = (byte) ((sample >> 8) & 0xFF); // Left channel, high byte
			audioData[i * 4 + 2] = (byte) (sample & 0xFF); // Right channel, low byte
			audioData[i * 4 + 3] = (byte) ((sample >> 8) & 0xFF); // Right channel, high byte
		}

		return audioData;
	}

	private static byte[] generateWaterfallNoise(int numSamples, double volumeDb) {
		byte[] audioData = new byte[numSamples * 2 * 2]; // 2 bytes per sample * 2 channels

		double frequency = 0.1;
		double amplitude = 0.3;

		for (int i = 0; i < numSamples; i++) {
			// Create waterfall-like sound with changing frequencies
			double t = i / (double) SAMPLE_RATE;

			// Create a sweeping frequency effect
			double sweep = Math.sin(2 * Math.PI * frequency * t) * amplitude;
			double noise = Math.random() * 0.5 - 0.25;

			double value = sweep + noise;

			// Apply volume adjustment
			double adjustedValue = value * 32767 * Math.pow(10, volumeDb / 20.0);
			if (adjustedValue > 32767)
				adjustedValue = 32767;
			if (adjustedValue < -32768)
				adjustedValue = -32768;

			// Convert to bytes and store in little-endian format
			int sample = (int) adjustedValue;
			audioData[i * 4] = (byte) (sample & 0xFF); // Left channel, low byte
			audioData[i * 4 + 1] = (byte) ((sample >> 8) & 0xFF); // Left channel, high byte
			audioData[i * 4 + 2] = (byte) (sample & 0xFF); // Right channel, low byte
			audioData[i * 4 + 3] = (byte) ((sample >> 8) & 0xFF); // Right channel, high byte
		}

		return audioData;
	}

	private static void writeWavFile(String filename, byte[] audioData, int sampleRate, int channels) throws Exception {
		int byteRate = sampleRate * channels * 2; // 2 bytes per sample
		int headerSize = 44; // Standard WAV header size
		int dataSize = audioData.length;
		int fileSize = headerSize + dataSize - 8; // File size minus riff and size fields

		try (FileOutputStream fos = new FileOutputStream(filename);
				BufferedOutputStream bos = new BufferedOutputStream(fos)) {

			// Write RIFF header
			writeString(bos, "RIFF");
			writeInt(bos, fileSize);
			writeString(bos, "WAVE");

			// Write fmt sub-chunk
			writeString(bos, "fmt ");
			writeInt(bos, 16); // Sub-chunk size
			writeShort(bos, (short) 1); // Audio format (1 = PCM)
			writeShort(bos, (short) channels); // Number of channels
			writeInt(bos, sampleRate); // Sample rate
			writeInt(bos, byteRate); // Byte rate
			writeShort(bos, (short) (channels * 2)); // Block align
			writeShort(bos, (short) 16); // Bits per sample

			// Write data sub-chunk
			writeString(bos, "data");
			writeInt(bos, dataSize);

			// Write audio data
			bos.write(audioData);
		}
	}

	private static void writeString(OutputStream os, String s) throws IOException {
		for (int i = 0; i < s.length(); i++) {
			os.write(s.charAt(i));
		}
	}

	private static void writeInt(OutputStream os, int value) throws IOException {
		os.write(value & 0xFF);
		os.write((value >> 8) & 0xFF);
		os.write((value >> 16) & 0xFF);
		os.write((value >> 24) & 0xFF);
	}

	private static void writeShort(OutputStream os, short value) throws IOException {
		os.write(value & 0xFF);
		os.write((value >> 8) & 0xFF);
	}
}
