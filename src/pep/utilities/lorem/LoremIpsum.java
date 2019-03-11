package pep.utilities.lorem;

import pep.utilities.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * This class supports the generation of random field values that go into elements on a TMDS web page.
 */
public class LoremIpsum implements Lorem {
    private static Logger logger = Logger.getLogger(LoremIpsum.class.getName());

    /*
     * this command was useful.  I think it takes written paragraph text and
     * converts it into a list of individual words, Latin or English or whatever:
     *
     * cat lorem.txt | sed -e 's/[,;.]//g' | sed -e 's/ /\n/g' | sed -e \
     * 'y/ABCDEFGHIJKLMNOPQRSTUVWXYZ/abcdefghijklmnopqrstuvwxyz/' | sort | \
     * uniq > lorem.txt.2
     */
    private static LoremIpsum instance;

    //private List<String> words = new ArrayList<String>();
    private List<String> words;
//    private Random random = null;
    private List<String> maleNames; // should be namesGivenMale
    private List<String> femaleNames; // should be namesGivenFemale
    private List<String> surnames;
    private List<String> relationships;
    private List<String> usAddresses;
    private List<String> usAddressesNoState;
    private List<String> allergyNames;
    private List<String> allergyReactions;
    private List<String> unitIdentificationCodes;
    private List<String> unitNames;
    private List<String> unitsEmployers;
    private List<String> dischargeNotes;
    private List<String> cptCodes;
    private List<String> ecSpineLevels;
    private List<String> icd9Codes;
    private List<String> icd10Codes;
    private List<String> injuryIllnessAssessments;
    private List<String> injuryIllnessAdmissionNotes;
    private List<String> commentsNotesComplications;
    private List<String> painManagementCommentsDissatisfied;
    private List<String> painManagementPlans;
    private List<String> bhNotes;
    private List<String> blockLocation;
    private List<String> tbiAssessmentNoteComments;
    private List<String> locationAdminNotes;

    public static LoremIpsum getInstance() {
        if (instance == null) {
            synchronized (LoremIpsum.class) {
                if (instance == null) {
//                    Random random = new Random(); // I'm confused.  Diff between Utilities.random and this?
//                    instance = new LoremIpsum(random);
                    instance = new LoremIpsum();
                }
            }
        }
        return instance;
    }

//    public LoremIpsum() {
//        this(new Random());
//    }

//    public LoremIpsum(Random random) {
    private LoremIpsum() {
        //this.random = random; // Is this used?
        // sort this stuff later
        words = readLines("lorem.txt");
        maleNames = readLines("male_names.txt"); // should be maleGivenNames
        femaleNames = readLines("female_names.txt"); // should be femaleGivenNames
        surnames = readLines("surnames.txt");
        relationships = readLines("relationships.txt");
        usAddresses = readLines("us_address.txt");
        usAddressesNoState = readLines("us_address_no_state.txt");
        allergyNames = readLines("allergy_names.txt");
        allergyReactions = readLines("allergy_reactions.txt");
        unitIdentificationCodes = readLines("unit_identification_codes.txt");
        unitNames = readLines("unit_names.txt");
        unitsEmployers = readLines("units_employers.txt");
        dischargeNotes = readLines("discharge_notes.txt");
        cptCodes = readLines("cpt_codes.txt");
        ecSpineLevels = readLines("ec_spine_levels.txt");
        icd9Codes = readLines("icd9_codes.txt");
        icd10Codes = readLines("icd10_codes.txt");
        injuryIllnessAssessments = readLines("injury_illness_assessments.txt");
        injuryIllnessAdmissionNotes = readLines("injury_illness_admission_notes.txt");
        commentsNotesComplications = readLines("comments_notes_complications.txt");
        painManagementCommentsDissatisfied = readLines("pain_management_comments_dissatisfied.txt");
        painManagementPlans = readLines("pain_management_plans.txt");
        bhNotes = readLines("bh_notes.txt");
        blockLocation = readLines("block_location.txt");
        tbiAssessmentNoteComments = readLines("tbi_assessment_note_comments.txt");
        locationAdminNotes = readLines("location_admin_notes.txt");
    }

    public String getAllergyName() {
        return getRandom(allergyNames);
    }
    public String getAllergyReaction() {
        return getRandom(allergyReactions);
    }
    public String getUnitIdentificationCode() {
        return getRandom(unitIdentificationCodes);
    }
    public String getUnitName() {
        return getRandom(unitNames);
    }
    public String getUnitEmployer() {
        return getRandom(unitsEmployers);
    }
    public String getDischargeNote() {
        return getRandom(dischargeNotes);
    }
    public String getCptCode() {
        return getRandom(cptCodes);
    }
    public String getEcSpineLevel() {
        return getRandom(ecSpineLevels);
    }
    public String getIcd9Code() {
        return getRandom(icd9Codes);
    }
    public String getIcd10Code() {
        return getRandom(icd10Codes);
    }
    public String getInjuryIllnessAssessment() {
        return getRandom(injuryIllnessAssessments);
    }
    public String getInjuryIllnessAdmissionNote() {
        return getRandom(injuryIllnessAdmissionNotes);
    }
    public String getCommentNoteComplication() { return getRandom(commentsNotesComplications); }
    public String getPainManagementDissatisfiedComment() { return getRandom(painManagementCommentsDissatisfied); }
    public String getPainManagementPlan() { return getRandom(painManagementPlans); }
    public String getBhNote() { return getRandom(bhNotes); }
    public String getBlockLocation() { return getRandom(blockLocation); }
    public String getTbiAssessmentNoteComment() { return getRandom(tbiAssessmentNoteComments); }
    public String getLocationAdminNote() { return getRandom(locationAdminNotes); }

    public String getFirstNameMale() {
        return getRandom(maleNames);
    }
    public String getFirstNameFemale() {
        return getRandom(femaleNames);
    }
    public String getLastName() { return getRandom(surnames); }

    public String getNameMale() {
        return getFirstNameMale() + " " + getLastName();
    }
    public String getNameFemale() {
        return getFirstNameFemale() + " " + getLastName();
    }


    public String getUsAddressNoState() {
        return getRandom(usAddressesNoState);
    }
    public String getUsAddress() {
        return getRandom(usAddresses);
    }
    public String getRelationship() {
        return getRandom(relationships);
    }


    // why are these with override?


    @Override
    public String getTitle(int count) {
        return getWords(count, count, true);
    }

    /*
     * (non-Javadoc)
     *
     * @see pep.utilities.lorem.Lorem#getTitle(int, int)
     */
    @Override
    public String getTitle(int min, int max) {
        return getWords(min, max, true);
    }

    private int getCount(int min, int max) {
        if (min < 0)
            min = 0;
        if (max < min)
            max = min;
        int count = max != min ? Utilities.random.nextInt(max - min) + min : min;
        return count;
    }

    /*
     * (non-Javadoc)
     *
     * @see pep.utilities.lorem.Lorem#getHtmlParagraphs(int, int)
     */
    @Override
    public String getHtmlParagraphs(int min, int max) {
        int count = getCount(min, max);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("<p>");
            sb.append(getParagraphs(1, 1));
            sb.append("</p>");
        }
        return sb.toString().trim();
    }

    /*
     * (non-Javadoc)
     *
     * @see pep.utilities.lorem.Lorem#getParagraphs(int, int)
     */
    @Override
    public String getParagraphs(int min, int max) {
        int count = getCount(min, max);
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < count; j++) {
            int sentences = Utilities.random.nextInt(3) + 2; // 2 to 6  // 3 was 5
            for (int i = 0; i < sentences; i++) {
                String first = getWords(1, 1, false);
                first = first.substring(0, 1).toUpperCase()
                        + first.substring(1);
                sb.append(first);

                sb.append(getWords(2, 10, false)); // 10 was 20
                sb.append(".  ");
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }

//    /*
//     * (non-Javadoc)
//     *
//     * @see pep.utilities.lorem.Lorem#getUrl()
//     */
//    @Override
//    public String getUrl() {
//        StringBuilder sb = new StringBuilder();
//        int hostId =Utilities.random.nextInt(URL_HOSTS.length);
//        String host = String.format(URL_HOSTS[hostId], getWords(1));
//        sb.append(host);
//        return sb.toString();
//    }

    private String getWords(int min, int max, boolean title) {
        int count = getCount(min, max);
        return getWords(count, title);
    }

    /*
     * (non-Javadoc)
     *
     * @see pep.utilities.lorem.Lorem#getWords(int)
     */
    @Override
    public String getWords(int count) {
        return getWords(count, count, false);
    }

    /*
     * (non-Javadoc)
     *
     * @see pep.utilities.lorem.Lorem#getWords(int, int)
     */
    @Override
    public String getWords(int min, int max) {
        return getWords(min, max, false);
    }

    private String getWords(int count, boolean title) {
        StringBuilder sb = new StringBuilder();
        int size = words.size();
        int wordCount = 0;
        while (wordCount < count) {
//            String word = words.get(random.nextInt(size)); // not Utilities.random.nextInt(size))?
            String word = words.get(Utilities.random.nextInt(size)); // not Utilities.random.nextInt(size))?
            if (title) {
                if (wordCount == 0 || word.length() > 3) {
                    word = word.substring(0, 1).toUpperCase()
                            + word.substring(1);
                }
            }
            sb.append(word);
            sb.append(" ");
            wordCount++;
        }
        return sb.toString().trim();
    }

    private String getRandom(List<String> list) {
        int size = list.size();
//        return list.get(random.nextInt(size));
        return list.get(Utilities.random.nextInt(size));
    }

    private List<String> readLines(String file) {
        List<String> ret = new ArrayList<String>();
        BufferedReader br = null;
        try {
//            br = new BufferedReader(new InputStreamReader(getClass()
//                    .getResourceAsStream(file), "UTF-8"));
            java.lang.Class someClass = getClass();
            java.io.InputStream something =someClass.getResourceAsStream(file); // fails here.  return null
            //java.io.InputStream something = getClass().getResourceAsStream(file);
            java.io.InputStreamReader whatever = new InputStreamReader(something, "UTF-8");
            br = new BufferedReader(whatever);

//            br = new BufferedReader(new InputStreamReader(getClass()
//                    .getResourceAsStream(file), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                ret.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

}
