package logic;
import java.io.IOException;
import java.nio.file.*;
import java.time.*;
import java.util.concurrent.*;

public class Syncer {

	private static final String TEMP = "C:\\Iggacorp Bot\\TempLogs\\";

	private static final String TEMP_VOICE = TEMP + "Voice\\";

	private static final String TEMP_TEXT = TEMP + "Text\\";

	private static final String Z_VOICE = "Z:\\Voice\\";

	private static final String Z_TEXT = "Z:\\Text\\";

	private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	public static void start() {
		long delay = secondsUntilMidnight();
		scheduler.scheduleAtFixedRate(Syncer::sync, delay, 86400, TimeUnit.SECONDS);
	}

	/* ================= SYNC ================= */

	private static void sync() {
		String date = LocalDate.now().toString();
		try {
			if (!Files.exists(Path.of("Z:\\"))) {
				System.out.println("Z: unavailable, skipping sync.");
				return;
			}

			// VOICE
			Path zVoiceDay = Path.of(Z_VOICE, date);
			Files.createDirectories(zVoiceDay);

			try (DirectoryStream<Path> voices = Files.newDirectoryStream(Path.of(TEMP_VOICE))) {

				for (Path voiceDir : voices) {
					if (!Files.isDirectory(voiceDir))
						continue;

					Path zVoiceDir = zVoiceDay.resolve(voiceDir.getFileName());
					Files.createDirectories(zVoiceDir);

					try (DirectoryStream<Path> wavs = Files.newDirectoryStream(voiceDir, "*.wav")) {

						for (Path wav : wavs) {
							Files.move(wav, zVoiceDir.resolve(wav.getFileName()), StandardCopyOption.REPLACE_EXISTING);
						}
					}
				}
			}

			// TEXT
			Path zTextDay = Path.of(Z_TEXT, date + ".txt");
			Files.createDirectories(zTextDay.getParent());

			try (DirectoryStream<Path> logs = Files.newDirectoryStream(Path.of(TEMP_TEXT), "*.txt")) {

				for (Path log : logs) {
					Files.write(zTextDay, Files.readAllBytes(log), StandardOpenOption.CREATE,
							StandardOpenOption.APPEND);
					Files.delete(log);
				}
			}

			System.out.println("Sync complete for " + date);

		} catch (Exception e) {
			System.err.println("Sync failed, retry tomorrow.");
			e.printStackTrace();
		}
	}

	/* ================= UTIL ================= */

	private static long secondsUntilMidnight() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
		return Duration.between(now, midnight).getSeconds();
	}
}
