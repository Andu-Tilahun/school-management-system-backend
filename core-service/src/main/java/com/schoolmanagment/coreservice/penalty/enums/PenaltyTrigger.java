package com.schoolmanagment.coreservice.penalty.enums;

import java.util.Arrays;
import java.util.List;

public enum PenaltyTrigger {

    // ---- OFFENCE ----
    /** Physical altercation between students */
    FIGHTING(SourceModule.OFFENCE),
    /** Repeated targeting/intimidation of another student */
    BULLYING(SourceModule.OFFENCE),
    /** Verbal abuse, threats, intimidation (non-bullying) */
    HARASSMENT(SourceModule.OFFENCE),
    /** Unwanted sexual conduct or remarks */
    SEXUAL_HARASSMENT(SourceModule.OFFENCE),
    /** Discriminatory language or conduct */
    HATE_SPEECH(SourceModule.OFFENCE),
    /** Deliberate damage to school or others' property */
    VANDALISM(SourceModule.OFFENCE),
    /** Stealing school or personal property */
    THEFT(SourceModule.OFFENCE),
    /** Exam/assignment dishonesty */
    CHEATING(SourceModule.OFFENCE),
    /** Submitting others' work as one's own */
    PLAGIARISM(SourceModule.OFFENCE),
    /** Forged signatures, notes, or documents */
    FORGERY(SourceModule.OFFENCE),
    /** Defying a staff member's direct instruction */
    INSUBORDINATION(SourceModule.OFFENCE),
    /** Persistent classroom disruption */
    DISRUPTIVE_BEHAVIOR(SourceModule.OFFENCE),
    /** Uniform/dress code breach */
    DRESS_CODE_VIOLATION(SourceModule.OFFENCE),
    /** Leaving/missing a specific class without permission */
    SKIPPING_CLASS(SourceModule.OFFENCE),
    /** Prohibited phone use during school hours */
    MOBILE_PHONE_MISUSE(SourceModule.OFFENCE),
    /** On-campus smoking or vaping */
    SMOKING(SourceModule.OFFENCE),
    /** Possession or use of alcohol */
    ALCOHOL_POSSESSION(SourceModule.OFFENCE),
    /** Possession or use of controlled substances */
    DRUG_POSSESSION(SourceModule.OFFENCE),
    /** Possession of a weapon or dangerous item */
    WEAPON_POSSESSION(SourceModule.OFFENCE),
    /** Any other banned item (catch-all) */
    PROHIBITED_ITEM(SourceModule.OFFENCE),
    /** Gambling on campus */
    GAMBLING(SourceModule.OFFENCE),
    /** Entering an unauthorized area */
    TRESPASSING(SourceModule.OFFENCE),
    /** Impersonation, unauthorized materials, etc. (beyond plain cheating) */
    EXAM_MISCONDUCT(SourceModule.OFFENCE),

    /** Marked absent */
    ABSENCE(SourceModule.ATTENDANCE),
    /** Habitual late arrival */
    TARDINESS(SourceModule.ATTENDANCE),
    /** Leaving before dismissal without permission */
    EARLY_DEPARTURE(SourceModule.ATTENDANCE),
    /** Absence specifically without a valid excuse */
    UNEXCUSED_ABSENCE(SourceModule.ATTENDANCE);

    private final SourceModule sourceModule;

    PenaltyTrigger(SourceModule sourceModule) {
        this.sourceModule = sourceModule;
    }

    public SourceModule getSourceModule() {
        return sourceModule;
    }

    public static List<PenaltyTrigger> valuesFor(SourceModule sourceModule) {
        if (sourceModule == null) {
            return List.of(values());
        }
        return Arrays.stream(values())
                .filter(trigger -> trigger.sourceModule == sourceModule)
                .toList();
    }
}
