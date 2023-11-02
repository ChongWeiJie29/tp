package seedu.address;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.address.commons.core.Config;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.Version;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.ConfigUtil;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.PrescriptionList;
import seedu.address.model.ReadOnlyPrescriptionList;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.CompletedPrescriptionListStorage;
import seedu.address.storage.JsonCompletedPrescriptionListStorage;
import seedu.address.storage.JsonPrescriptionListStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.PrescriptionListStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.storage.UserPrefsStorage;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(1, 3, 0, true);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing BayMeds v.2103 ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());
        initLogging(config);

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        PrescriptionListStorage prescriptionListStorage = new JsonPrescriptionListStorage(
                userPrefs.getPrescriptionListFilePath());
        CompletedPrescriptionListStorage completedPrescriptionListStorage = new JsonCompletedPrescriptionListStorage(
                userPrefs.getCompletedPrescriptionListFilePath());

        storage = new StorageManager(prescriptionListStorage, completedPrescriptionListStorage, userPrefsStorage);

        model = initModelManager(storage, userPrefs);

        logic = initLogicManager(model, storage);

        ui = new UiManager(logic);

        LocalDate oldDate = userPrefs.getStoredDate();
        LocalDate currentDate = LocalDate.now();

        if (oldDate == null || oldDate.isBefore(currentDate)) {
            //reset consumption count
            logic.checkAndResetConsumptionCount();
            logic.setStoredDate(currentDate);
            System.out.println("date set to " + currentDate);
        }
    }

    /**
     * Returns a {@code LogicManager} with the already intialised {@code model} and {@code storage}, and
     * sort prescriptions into whether they have ended or not.
     */
    private Logic initLogicManager(Model model, Storage storage) {
        Logic logic = new LogicManager(model, storage);
        logic.checkAndMoveEndedPrescriptions();
        return logic;
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s prescription list and {@code userPrefs}. <br>
     * The data from the sample prescription list will be used instead if {@code storage}'s prescription list is not
     * found, or an empty prescription list will be used instead if errors occur when reading {@code storage}'s
     * prescription list.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getPrescriptionListFilePath()
                + " and " + storage.getCompletedPrescriptionListFilePath());

        Optional<ReadOnlyPrescriptionList> prescriptionListOptional;
        Optional<ReadOnlyPrescriptionList> completedPrescriptionListOptional;
        ReadOnlyPrescriptionList initialData;
        ReadOnlyPrescriptionList initialCompletedData;
        try {
            prescriptionListOptional = storage.readPrescriptionList();
            completedPrescriptionListOptional = storage.readCompletedPrescriptionList();
            if (!prescriptionListOptional.isPresent()) {
                logger.info("Creating a new data file " + storage.getPrescriptionListFilePath()
                        + " populated with a sample PrescriptionList.");
            }
            initialData = prescriptionListOptional.orElseGet(SampleDataUtil::getSamplePrescriptionList);
            if (!completedPrescriptionListOptional.isPresent()) {
                logger.info("Creating a new data file " + storage.getCompletedPrescriptionListFilePath()
                        + " populated with a sample CompletedPrescriptionList.");
            }
            initialCompletedData = completedPrescriptionListOptional.orElseGet(
                    SampleDataUtil::getSampleCompletedPrescriptionList);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getPrescriptionListFilePath()
                    + " or " + storage.getCompletedPrescriptionListFilePath()
                    + " could not be loaded."
                    + " Will be starting with an empty PrescriptionList.");
            initialData = new PrescriptionList();
            initialCompletedData = new PrescriptionList();
        }

        return new ModelManager(initialData, initialCompletedData, userPrefs);
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            if (!configOptional.isPresent()) {
                logger.info("Creating new config file " + configFilePathUsed);
            }
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataLoadingException e) {
            logger.warning("Config file at " + configFilePathUsed + " could not be loaded."
                    + " Using default config properties.");
            initializedConfig = new Config();
        }

        //Update config file in case it was missing to begin with or there are new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (!prefsOptional.isPresent()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting BayMeds v.2103 " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping BayMeds v.2103 ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
