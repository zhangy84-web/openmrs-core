package org.openmrs.validator;

import java.util.Locale;

import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientIdentifierType.LocationBehavior;
import org.openmrs.PatientIdentifierType.UniquenessBehavior;
import org.openmrs.api.IdentifierNotUniqueException;
import org.openmrs.api.PatientIdentifierException;
import org.openmrs.api.PatientService;
import org.openmrs.messagesource.MessageSourceService;

/**
 * A more testable version of PatientIdentifier validation that avoids static Context calls.
 * This is added for testability demonstration and does not replace the existing validator.
 */
public class PatientIdentifierValidatorRefactored {

	private final PatientService patientService;
	private final MessageSourceService messageSourceService;

	public PatientIdentifierValidatorRefactored(PatientService patientService, MessageSourceService messageSourceService) {
		this.patientService = patientService;
		this.messageSourceService = messageSourceService;
	}

	/**
	 * Demonstrates the part that was hard to unit test in the original design:
	 * uniqueness check + message creation, without using Context.
	 */
	public void validateUniquenessAndLocation(PatientIdentifier pi, Locale locale) throws PatientIdentifierException {
		if (pi == null) {
			throw new PatientIdentifierException("PatientIdentifier.error.null");
		}
		if (pi.getVoided()) {
			return; // match original behavior: skip validation when voided
		}

		PatientIdentifierType idType = pi.getIdentifierType();
		LocationBehavior lb = idType.getLocationBehavior();

		if (pi.getLocation() == null && (lb == null || lb == LocationBehavior.REQUIRED)) {
			String identifierString = (pi.getIdentifier() != null) ? pi.getIdentifier() : "";
			String msg = messageSourceService.getMessage(
				"PatientIdentifier.location.null",
				new Object[] { identifierString },
				locale
			);
			throw new PatientIdentifierException(msg);
		}

		if (idType.getUniquenessBehavior() != UniquenessBehavior.NON_UNIQUE
			&& patientService.isIdentifierInUseByAnotherPatient(pi)) {

			String msg = messageSourceService.getMessage(
				"PatientIdentifier.error.notUniqueWithParameter",
				new Object[] { pi.getIdentifier() },
				locale
			);
			throw new IdentifierNotUniqueException(msg, pi);
		}
	}
}
