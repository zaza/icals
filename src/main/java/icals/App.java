package icals;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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
import net.fortuna.ical4j.util.UidGenerator;
import net.fortuna.ical4j.validate.ValidationException;

public class App {
	public static void main(String[] args) throws Exception {
		App app = new App();
		System.out.println(app.readYaml());
		app.generateCalendarFile(app.createCalendar());
	}

	private Map<String, Object> readYaml() {
		Yaml yaml = new Yaml();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("lipusz-smieci-2020.yaml");
		return yaml.load(inputStream);
	}

	private Calendar createCalendar() {
		Calendar calendar = new Calendar();
		calendar.getProperties().add(new ProdId("-//zaza/icals//iCal4j 1.0//EN"));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);
		calendar.getComponents().add(christmas());
		return calendar;
	}

	private CalendarComponent christmas() {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
		calendar.set(java.util.Calendar.DAY_OF_MONTH, 25);

		VEvent christmas = new VEvent(new Date(calendar.getTime()), "Christmas Day");

		UidGenerator ug = new RandomUidGenerator();
		christmas.getProperties().add(ug.generateUid());
		return christmas;
	}

	private void generateCalendarFile(Calendar calendar) throws ValidationException, IOException {
		FileOutputStream fout = new FileOutputStream("output/mycalendar.ics");
		CalendarOutputter outputter = new CalendarOutputter();
		outputter.output(calendar, fout);
	}

}
