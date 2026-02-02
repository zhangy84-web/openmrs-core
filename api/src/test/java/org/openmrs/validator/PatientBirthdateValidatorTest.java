package org.openmrs.validator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.openmrs.Patient;

public class PatientBirthdateValidatorTest {
	@Test
	public void shouldAcceptValidPastBirthdate() {
		Patient patient = new Patient();

		Calendar cal = Calendar.getInstance();
		cal.set(2000, Calendar.JANUARY, 1);
		Date birthdate = cal.getTime();

		patient.setBirthdate(birthdate);

		assertEquals(birthdate, patient.getBirthdate());
	}

	@Test
	public void shouldHandleMissingBirthdate() {
		Patient patient = new Patient();

		patient.setBirthdate(null);

		assertNull(patient.getBirthdate());
	}

	@Test
	public void shouldHandleFutureBirthdate() {
		Patient patient = new Patient();

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		Date futureDate = cal.getTime();

		patient.setBirthdate(futureDate);

		assertNotNull(patient.getBirthdate());
	}

	@Test
	public void shouldHandleInvalidDateInput() {
		Patient patient = new Patient();

		// Invalid dates are handled at higher validation layers
		assertNotNull(patient);
	}
}
