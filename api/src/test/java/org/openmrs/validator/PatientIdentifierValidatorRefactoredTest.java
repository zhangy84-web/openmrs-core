package org.openmrs.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openmrs.Location;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientIdentifierType.UniquenessBehavior;
import org.openmrs.api.IdentifierNotUniqueException;
import org.openmrs.api.PatientService;
import org.openmrs.messagesource.MessageSourceService;

public class PatientIdentifierValidatorRefactoredTest {

	@Test
	public void validateUniquenessAndLocation_shouldThrowNotUnique_andVerifyServiceCall() {
		// Arrange
		PatientService patientService = Mockito.mock(PatientService.class);
		MessageSourceService messageSourceService = Mockito.mock(MessageSourceService.class);

		PatientIdentifierType type = new PatientIdentifierType();
		type.setUniquenessBehavior(UniquenessBehavior.UNIQUE);

		PatientIdentifier pi = new PatientIdentifier("ABC-123", type, new Location(1));

		when(patientService.isIdentifierInUseByAnotherPatient(pi)).thenReturn(true);
		when(messageSourceService.getMessage(
			eq("PatientIdentifier.error.notUniqueWithParameter"),
			any(Object[].class),
			eq(Locale.US)
		)).thenReturn("not unique");

		PatientIdentifierValidatorRefactored refactored =
			new PatientIdentifierValidatorRefactored(patientService, messageSourceService);

		// Act + Assert
		assertThrows(IdentifierNotUniqueException.class, () ->
			refactored.validateUniquenessAndLocation(pi, Locale.US)
		);

		// Behavior verification (this is now easy without Context)
		verify(patientService).isIdentifierInUseByAnotherPatient(pi);
		verify(messageSourceService).getMessage(
			eq("PatientIdentifier.error.notUniqueWithParameter"),
			any(Object[].class),
			eq(Locale.US)
		);
	}
}
