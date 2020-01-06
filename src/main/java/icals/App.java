package icals;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.yaml.snakeyaml.Yaml;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.validate.ValidationException;

public class App {

	public static void main(String[] args) throws Exception {
		App app = new App("lipusz-smieci-2020.yaml");
		app.generateCalendarFile(app.createCalendar(app.readYaml()));
	}

	private final String inputYamlFileName;

	private App(String inputYamlFileName) {
		this.inputYamlFileName = inputYamlFileName;
	}

	private Map<String, Object> readYaml() {
		Yaml yaml = new Yaml();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(inputYamlFileName);
		return yaml.load(inputStream);
	}

	private Calendar createCalendar(Map<String, Object> yaml) {
		Calendar calendar = new Calendar();
		calendar.getProperties().add(new ProdId("-//zaza/icals//iCal4j 1.0//EN"));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);
		for (Entry<String, Object> entry : yaml.entrySet()) {
			if (entry.getValue() == null || ((Collection<?>) entry.getValue()).isEmpty()) {
				continue;
			}
			@SuppressWarnings("unchecked")
			Collection<Date> dates = (Collection<Date>) entry.getValue();
			for (java.util.Date date : dates) {
				calendar.getComponents().add(newCalendarComponent(entry.getKey(), date));
			}
		}
		return calendar;
	}

	private CalendarComponent newCalendarComponent(String eventName, java.util.Date date) {
		VEvent event = new VEvent(new Date(date.getTime()), eventName);
		event.getProperties().add(new RandomUidGenerator().generateUid());
		return event;
	}

	private void generateCalendarFile(Calendar calendar) throws ValidationException, IOException {
		String outputIcsFileName = inputYamlFileName.substring(0, inputYamlFileName.lastIndexOf('.'));
		FileOutputStream fout = new FileOutputStream("output/" + outputIcsFileName + ".ics");
		CalendarOutputter outputter = new CalendarOutputter();
		outputter.output(calendar, fout);
	}

}
