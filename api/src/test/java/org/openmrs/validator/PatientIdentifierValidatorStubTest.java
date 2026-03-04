package org.openmrs.validator;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.openmrs.api.InvalidCheckDigitException;
import org.openmrs.patient.IdentifierValidator;
import org.openmrs.patient.UnallowedIdentifierException;

/**
 * Demonstrates stubbing by using a hand-written stub instead of a real validator implementation.
 */
public class PatientIdentifierValidatorStubTest {

	/**
	 * A simple stub that always returns "invalid".
	 * This replaces a real IdentifierValidator implementation in the test.
	 */
	static class AlwaysInvalidIdentifierValidatorStub implements IdentifierValidator {

		@Override
		public boolean isValid(String identifier) {
			return false; // force the failure path
		}

		@Override
		public String getValidIdentifier(String undecoratedIdentifier) throws UnallowedIdentifierException {
			// not used in this test
			return null;
		}

		@Override
		public String getAllowedCharacters() {
			// not used in this test
			return null;
		}

		@Override
		public String getName() {
			return "AlwaysInvalidStub";
		}
	}

	@Test
	public void checkIdentifierAgainstValidator_shouldFailWhenStubAlwaysReturnsInvalid() {
		IdentifierValidator stubValidator = new AlwaysInvalidIdentifierValidatorStub();

		assertThrows(InvalidCheckDigitException.class, () ->
			PatientIdentifierValidator.checkIdentifierAgainstValidator("ANY-ID", stubValidator)
		);
	}
}
