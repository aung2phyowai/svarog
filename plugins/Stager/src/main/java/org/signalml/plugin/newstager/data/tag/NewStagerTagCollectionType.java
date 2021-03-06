package org.signalml.plugin.newstager.data.tag;

import java.util.EnumSet;

public enum NewStagerTagCollectionType {

	HYPNO_DELTA,
	HYPNO_ALPHA,
	HYPNO_SPINDLE,

	SLEEP_STAGE_1,
	SLEEP_STAGE_2,
	SLEEP_STAGE_3,
	SLEEP_STAGE_4,
	SLEEP_STAGE_W,
	SLEEP_STAGE_R,

	CONSOLIDATED_SLEEP_STAGE_1,
	CONSOLIDATED_SLEEP_STAGE_2,
	CONSOLIDATED_SLEEP_STAGE_3,
	CONSOLIDATED_SLEEP_STAGE_4,
	CONSOLIDATED_SLEEP_STAGE_W,
	CONSOLIDATED_SLEEP_STAGE_REM,
	CONSOLIDATED_SLEEP_STAGE_M,

	SLEEP_PAGES,

	CONSOLIDATED_SLEEP_PAGES;
	
	public static EnumSet<NewStagerTagCollectionType> GetSleepStages() {
		return EnumSet.of(NewStagerTagCollectionType.SLEEP_STAGE_1,
				NewStagerTagCollectionType.SLEEP_STAGE_2,
				NewStagerTagCollectionType.SLEEP_STAGE_3,
				NewStagerTagCollectionType.SLEEP_STAGE_4,
				NewStagerTagCollectionType.SLEEP_STAGE_R,
				NewStagerTagCollectionType.SLEEP_STAGE_W);
	}

	public static EnumSet<NewStagerTagCollectionType> GetConsolidatedSleepStages() {
		return EnumSet.of(
				NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_1,
				NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_2,
				NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_3,
				NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_4,
				NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_REM,
				NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_W,
				NewStagerTagCollectionType.CONSOLIDATED_SLEEP_STAGE_M);
	}

}
