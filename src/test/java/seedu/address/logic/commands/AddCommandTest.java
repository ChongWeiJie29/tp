// package seedu.address.logic.commands;

// import static java.util.Objects.requireNonNull;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static seedu.address.testutil.Assert.assertThrows;
// import static seedu.address.testutil.TypicalPrescriptions.ALICE;

// import java.nio.file.Path;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.function.Predicate;

// import org.junit.jupiter.api.Test;

// import javafx.collections.ObservableList;
// import seedu.address.commons.core.GuiSettings;
// import seedu.address.logic.Messages;
// import seedu.address.logic.commands.exceptions.CommandException;
// import seedu.address.model.PrescriptionList;
// import seedu.address.model.Model;
// import seedu.address.model.ReadOnlyPrescriptionList;
// import seedu.address.model.ReadOnlyUserPrefs;
// import seedu.address.model.prescription.Prescription;
// import seedu.address.testutil.PrescriptionBuilder;

// public class AddCommandTest {

//     @Test
//     public void constructor_nullPrescription_throwsNullPointerException() {
//         assertThrows(NullPointerException.class, () -> new AddCommand(null));
//     }

//     @Test
//     public void execute_prescriptionAcceptedByModel_addSuccessful() throws Exception {
//         ModelStubAcceptingPrescriptionAdded modelStub = new ModelStubAcceptingPrescriptionAdded();
//         Prescription validPrescription = new PrescriptionBuilder().build();

//         CommandResult commandResult = new AddCommand(validPrescription).execute(modelStub);

//         assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPrescription)),
//                 commandResult.getFeedbackToUser());
//         assertEquals(Arrays.asList(validPrescription), modelStub.prescriptionsAdded);
//     }

//     @Test
//     public void execute_duplicatePrescription_throwsCommandException() {
//         Prescription validPrescription = new PrescriptionBuilder().build();
//         AddCommand addCommand = new AddCommand(validPrescription);
//         ModelStub modelStub = new ModelStubWithPrescription(validPrescription);

//         assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_PRESCRIPTION, () -> addCommand.execute(modelStub));
//     }

//     @Test
//     public void equals() {
//         Prescription alice = new PrescriptionBuilder().withName("Alice").build();
//         Prescription bob = new PrescriptionBuilder().withName("Bob").build();
//         AddCommand addAliceCommand = new AddCommand(alice);
//         AddCommand addBobCommand = new AddCommand(bob);

//         // same object -> returns true
//         assertTrue(addAliceCommand.equals(addAliceCommand));

//         // same values -> returns true
//         AddCommand addAliceCommandCopy = new AddCommand(alice);
//         assertTrue(addAliceCommand.equals(addAliceCommandCopy));

//         // different types -> returns false
//         assertFalse(addAliceCommand.equals(1));

//         // null -> returns false
//         assertFalse(addAliceCommand.equals(null));

//         // different prescription -> returns false
//         assertFalse(addAliceCommand.equals(addBobCommand));
//     }

//     @Test
//     public void toStringMethod() {
//         AddCommand addCommand = new AddCommand(ALICE);
//         String expected = AddCommand.class.getCanonicalName() + "{toAdd=" + ALICE + "}";
//         assertEquals(expected, addCommand.toString());
//     }

//     /**
//      * A default model stub that have all of the methods failing.
//      */
//     private class ModelStub implements Model {
//         @Override
//         public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
//             throw new AssertionError("This method should not be called.");
//         }

//         @Override
//         public ReadOnlyUserPrefs getUserPrefs() {
//             throw new AssertionError("This method should not be called.");
//         }

//         @Override
//         public GuiSettings getGuiSettings() {
//             throw new AssertionError("This method should not be called.");
//         }

//         @Override
//         public void setGuiSettings(GuiSettings guiSettings) {
//             throw new AssertionError("This method should not be called.");
//         }

//         @Override
//         public Path getPrescriptionListFilePath() {
//             throw new AssertionError("This method should not be called.");
//         }

//         @Override
//         public void setPrescriptionListFilePath(Path addressBookFilePath) {
//             throw new AssertionError("This method should not be called.");
//         }

//         @Override
//         public void addPrescription(Prescription prescription) {
//             throw new AssertionError("This method should not be called.");
//         }

//         @Override
//         public void setPrescriptionList(ReadOnlyPrescriptionList newData) {
//             throw new AssertionError("This method should not be called.");
//         }

//         @Override
//         public ReadOnlyPrescriptionList getPrescriptionList() {
//             throw new AssertionError("This method should not be called.");
//         }

//         @Override
//         public boolean hasPrescription(Prescription prescription) {
//             throw new AssertionError("This method should not be called.");
//         }

//         @Override
//         public void deletePrescription(Prescription target) {
//             throw new AssertionError("This method should not be called.");
//         }

//         @Override
//         public void setPrescription(Prescription target, Prescription editedPrescription) {
//             throw new AssertionError("This method should not be called.");
//         }

//         @Override
//         public ObservableList<Prescription> getFilteredPrescriptionList() {
//             throw new AssertionError("This method should not be called.");
//         }

//         @Override
//         public void updateFilteredPrescriptionList(Predicate<Prescription> predicate) {
//             throw new AssertionError("This method should not be called.");
//         }
//     }

//     /**
//      * A Model stub that contains a single prescription.
//      */
//     private class ModelStubWithPrescription extends ModelStub {
//         private final Prescription prescription;

//         ModelStubWithPrescription(Prescription prescription) {
//             requireNonNull(prescription);
//             this.prescription = prescription;
//         }

//         @Override
//         public boolean hasPrescription(Prescription prescription) {
//             requireNonNull(prescription);
//             return this.prescription.isSamePrescription(prescription);
//         }
//     }

//     /**
//      * A Model stub that always accept the prescription being added.
//      */
//     private class ModelStubAcceptingPrescriptionAdded extends ModelStub {
//         final ArrayList<Prescription> prescriptionsAdded = new ArrayList<>();

//         @Override
//         public boolean hasPrescription(Prescription prescription) {
//             requireNonNull(prescription);
//             return prescriptionsAdded.stream().anyMatch(prescription::isSamePrescription);
//         }

//         @Override
//         public void addPrescription(Prescription prescription) {
//             requireNonNull(prescription);
//             prescriptionsAdded.add(prescription);
//         }

//         @Override
//         public ReadOnlyPrescriptionList getPrescriptionList() {
//             return new PrescriptionList();
//         }
//     }

// }
