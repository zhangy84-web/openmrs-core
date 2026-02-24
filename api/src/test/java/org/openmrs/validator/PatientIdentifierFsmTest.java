/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.validator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;

import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;

import org.openmrs.test.jupiter.BaseContextSensitiveTest;

public class PatientIdentifierFsmTest extends BaseContextSensitiveTest {

	private PatientService patientService;
	private LocationService locationService;

	@BeforeEach
	public void setUp() {
		patientService = Context.getPatientService();
		locationService = Context.getLocationService();
	}

	private Patient buildPatient() {
		Patient p = new Patient();

		PersonName name = new PersonName();
		name.setGivenName("Test");
		name.setFamilyName("Patient");
		p.addName(name);

		p.setGender("F");
		p.setBirthdate(new Date(100, 0, 1));

		return p;
	}

	private PatientIdentifierType getIdentifierType() {
		List<PatientIdentifierType> types = patientService.getAllPatientIdentifierTypes(false);
		assertFalse(types.isEmpty());
		return types.get(0);
	}

	private Location getLocation() {
		List<Location> locs = locationService.getAllLocations();
		assertFalse(locs.isEmpty());
		return locs.get(0);
	}

	private PatientIdentifier buildIdentifier(String value) {
		PatientIdentifier pid = new PatientIdentifier();
		pid.setIdentifier(value);
		pid.setIdentifierType(getIdentifierType());
		pid.setLocation(getLocation());
		pid.setPreferred(true);
		return pid;
	}

	@Test
	public void shouldFailWhenIdentifierIsMissing() {
		Patient p = buildPatient();

		assertThrows(Exception.class, () -> {
			patientService.savePatient(p);
		});
	}

	@Test
	public void shouldFailWhenIdentifierIsDuplicate() {
		String value = "DUP-1001";

		Patient p1 = buildPatient();
		p1.addIdentifier(buildIdentifier(value));
		patientService.savePatient(p1);

		Patient p2 = buildPatient();
		p2.addIdentifier(buildIdentifier(value));

		assertThrows(Exception.class, () -> {
			patientService.savePatient(p2);
		});
	}

	@Test
	public void shouldSaveWhenIdentifierIsValid() {
		Patient p = buildPatient();
		p.addIdentifier(buildIdentifier("VALID-2002"));

		Patient saved = patientService.savePatient(p);

		assertNotNull(saved.getPatientId());
		assertFalse(saved.getIdentifiers().isEmpty());
	}

	@Test
	public void shouldAllowRetryAfterFixingIdentifier() {
		Patient p = buildPatient();

		assertThrows(Exception.class, () -> {
			patientService.savePatient(p);
		});

		p.addIdentifier(buildIdentifier("FIXED-3003"));

		Patient saved = patientService.savePatient(p);
		assertNotNull(saved.getPatientId());
	}
}

