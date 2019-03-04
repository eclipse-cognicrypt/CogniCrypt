package de.cognicrypt.core.telemetry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import org.eclipse.jface.preference.IPreferenceStore;
import org.json.simple.JSONObject;
import de.cognicrypt.core.Activator;

public class Telemetry {
	// Change to HTTPS for the study, won't get a valid cert for localhost
	private String telemetryUrl = "http://localhost:6200/submit";

	private String generateUUID() {
		return UUID.randomUUID().toString();
	}

	private String getUUID() {
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		if (preferenceStore.isDefault(Activator.PLUGIN_ID + ".telemetry.participantUID")) {
			preferenceStore.setValue(Activator.PLUGIN_ID + ".telemetry.participantUID", generateUUID());
		}
		return preferenceStore.getString(Activator.PLUGIN_ID + ".telemetry.participantUID");
	}

	public void sendEvent(TelemetryEvents event) {
		sendEvent(event, "");
	}
	
	public void sendEvent(TelemetryEvents event, Exception e) {
		StringJoiner sj = new StringJoiner("\n");
		sj.add(e.getMessage());
		for(StackTraceElement elem : e.getStackTrace()){
			if(!elem.toString().startsWith("org.eclipse.core.")) {
				//Let's shorten the stacktrace a bit
				sj.add(elem.toString());
			}
		}
		sendEvent(event, sj.toString());
	}

	/**
	 * Sends an event to our telemetry server. UUID and timestamp will be added
	 * automatically.
	 * 
	 * @param event
	 *            The event that was triggered.
	 * @param payload
	 *            (Optional) Possible payload.
	 */
	public void sendEvent(TelemetryEvents event, String payload) {
		// Execute the telemetry in a background task to avoid interrupting the GUI.
		new Thread(new Runnable() {
			public void run() {
				try {
					URL url = new URL(telemetryUrl);
					URLConnection con = url.openConnection();
					HttpURLConnection http = (HttpURLConnection) con;
					http.setRequestMethod("POST");
					http.setDoOutput(true);

					// Let's use a HashMap here and convert to JSONObject later on, otherwise we
					// would need to suppress unchecked warnings here
					Map<String, String> telemetryData = new HashMap<String, String>();
					telemetryData.put("client-info", "cogniCryptTelemetry");
					telemetryData.put("uid", getUUID());
					telemetryData.put("event", event.toString());
					//telemetryData.put("timestamp", String.valueOf(Instant.now().toEpochMilli()));
					if (payload != null) {
						telemetryData.put("payload", payload);
					}

					String jsonString = new JSONObject(telemetryData).toJSONString();
					
					Activator.getDefault().logInfo("[Telemetry] Submitting: "+jsonString);
					
					byte[] out = jsonString.getBytes(StandardCharsets.UTF_8);
					long length = out.length;

					http.setFixedLengthStreamingMode(length);
					http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
					http.connect();
					try (OutputStream os = http.getOutputStream()) {
						os.write(out);
					}

					http.getInputStream().read();

					http.disconnect();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).run();
	}

}